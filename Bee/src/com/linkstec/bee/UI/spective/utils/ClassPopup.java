package com.linkstec.bee.UI.spective.utils;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.core.ClassInfos;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;

public class ClassPopup {

	private transient BeePopupTreeMenu hint;
	private String fixedValue;

	public ClassPopup(JTextField text, ClassPopup.DTextAction action, BProject project, String fixedValue) {
		this.fixedValue = fixedValue;

		hint = new BeePopupTreeMenu(text);

		hint.setName("HINT");
		if (action.isType()) {
			text.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {

				}

				@Override
				public void keyPressed(KeyEvent e) {
					int code = e.getKeyCode();
					if (code == KeyEvent.VK_DOWN) {
						hint.selectNext();
						hint.repaint();
					} else if (code == KeyEvent.VK_UP) {
						hint.selectBefore();
						hint.repaint();
					} else if (code == KeyEvent.VK_ENTER) {

						if (hint != null) {
							Object obj = hint.getSelectedItem();
							if (obj == null) {
								obj = hint.getItemAt(0);
							}
							if (obj != null) {
								BeeTreeNode m = (BeeTreeNode) obj;
								String s = (String) m.getValue();
								if (s.equals(fixedValue)) {
									text.setText(s);
									if (action instanceof DTextActionImpl) {
										DTextActionImpl impl = (DTextActionImpl) action;
										impl.fixedSelected(fixedValue);
									}
								} else {
									BClass bclass = CodecUtils.getClassFromJavaClass(project, s);
									ComplexNode var = new ComplexNode();
									var.setBClass(bclass);
									var.setLogicName(bclass.getLogicName());
									var.setName(bclass.getName());
									action.valueChanged(var);

									text.setText(bclass.getQualifiedName());
								}

								hint.setVisible(false);
							}
						}

					}

				}

				@Override
				public void keyReleased(KeyEvent e) {

					char c = e.getKeyChar();
					if (Character.isJavaIdentifierPart(c)) {
						makeTypeValues(text.getText(), text);
					}
					e.consume();
				}

			});
		}

		text.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				action.changed(text.getText(), text);
				if (action.isType()) {
					makeTypeValues(text.getText(), text);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				action.changed(text.getText(), text);
				if (action.isType()) {
					makeTypeValues(text.getText(), text);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				action.changed(text.getText(), text);
				if (action.isType()) {
					makeTypeValues(text.getText(), text);
				}
			}

		});

		hint.addAction(new IBeePopupMenuAction() {

			@Override
			public void menuSelected(Object menu) {
				BeeTreeNode m = (BeeTreeNode) menu;
				String s = (String) m.getValue();
				if (s.equals(fixedValue)) {
					text.setText(s);
					if (action instanceof DTextActionImpl) {
						DTextActionImpl impl = (DTextActionImpl) action;
						impl.fixedSelected(fixedValue);
					}
				} else {
					BClass bclass = CodecUtils.getClassFromJavaClass(project, s);
					ComplexNode var = new ComplexNode();
					var.setBClass(bclass);
					var.setLogicName(bclass.getLogicName());
					var.setName(bclass.getName());
					action.valueChanged(var);
					text.setText(bclass.getQualifiedName());
				}

				hint.setVisible(false);
			}

		});

		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				Component comp = e.getOppositeComponent();
				if (hint != null) {
					if (comp == null) {
						hint.setVisible(false);
					} else {
						if (!comp.equals(hint)) {
							hint.setVisible(false);
						}
					}
				}

			}

		});

	}

	private Thread searchThread = null;

	private void makeTypeValues(String text, JTextField t) {
		if (searchThread != null) {
			searchThread.interrupt();
		}
		if (hint != null) {
			hint.clear();

			searchThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(500);

						List<String> list = ClassInfos.lookupClass(text);
						if (list.size() > 0 || fixedValue != null) {
							if (fixedValue != null) {
								BeeTreeNode node = new BeeTreeNode(fixedValue);
								node.setUserObject(fixedValue);
								hint.getTreeRoot().add(node);

								if (fixedValue.equals(text)) {
									return;
								}
							}

							for (String s : list) {
								BeeTreeNode node = new BeeTreeNode(s);
								node.setUserObject(s);
								hint.getTreeRoot().add(node);
							}

							hint.getTree().updateUI();
							hint.getTree().expandPath(new TreePath(hint.getTreeRoot()));
							hint.showPop(t.getHeight());
							t.requestFocus();

						}
					} catch (InterruptedException e) {

					}

				}

			});
			searchThread.start();
		}
	}

	public static interface DTextAction {
		public void changed(String text, JTextField f);

		public void valueChanged(BValuable value);

		public boolean isType();
	}

	public static class DTextActionImpl implements DTextAction {

		@Override
		public void changed(String text, JTextField f) {

		}

		@Override
		public void valueChanged(BValuable value) {

		}

		@Override
		public boolean isType() {
			return true;
		}

		public void fixedSelected(String fixedValue) {

		}

	}

	public static interface DTypeAction {
		public void changed(BValuable value);
	}

}
