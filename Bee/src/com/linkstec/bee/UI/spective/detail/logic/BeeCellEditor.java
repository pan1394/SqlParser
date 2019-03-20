package com.linkstec.bee.UI.spective.detail.logic;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.EventObject;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.UI.spective.detail.edit.TypeAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.ClassInfos;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.view.mxCellEditor;

public class BeeCellEditor extends mxCellEditor implements KeyListener, Serializable, IBeePopupMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4338440740672366681L;
	private transient BeePopupTreeMenu hint;
	private BeeGraphSheet sheet;

	public BeeCellEditor(BeeGraphSheet sheet) {
		super(sheet);
		this.sheet = sheet;

		minimumHeight = BeeUIUtils.getDefaultFontSize();
		this.textArea.addKeyListener(this);
		this.textArea.setAutoscrolls(false);

		int s = BeeUIUtils.getDefaultFontSize();
		this.textArea.setBorder(new EmptyBorder(s / 2, 0, 0, 0));

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hint = new BeePopupTreeMenu(this.textArea);
		hint.addAction(this);
	}

	public JTextArea getTextEditor() {
		return this.textArea;
	}

	@Override
	public void startEditing(Object cell, EventObject evt) {
		super.startEditing(cell, evt);
		mxCell mx = (mxCell) cell;
		String text = textArea.getText();
		if (text.startsWith("@")) {
			textArea.setText(text.substring(1));
		}

		if (mx instanceof BasicNode) {
			BasicNode node = (BasicNode) mx;
			textArea.enableInputMethods(node.getUserAttribute("ENGLISH") == null);
		} else {
			textArea.enableInputMethods(true);
		}

		minimumHeight = (int) (mx.getGeometry().getHeight() * this.graphComponent.getGraph().getView().getScale());
		scrollPane.setSize(new Dimension(this.scrollPane.getWidth(), this.minimumHeight));
		textArea.setSize(new Dimension(this.textArea.getWidth(), this.minimumHeight));

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (this.getEditingCell() != null && this.getEditingCell() instanceof BasicNode) {
			BasicNode node = (BasicNode) this.getEditingCell();

			node.keyPressed(e.getKeyCode());
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_DOWN) {
				hint.selectNext();
				hint.repaint();
			} else if (code == KeyEvent.VK_UP) {
				hint.selectBefore();
				hint.repaint();
			} else if (code == KeyEvent.VK_ENTER) {
				if (hint.isVisible()) {
					Object obj = hint.getSelectedItem();
					if (obj == null) {
						obj = hint.getItemAt(0);
					}
					if (obj != null) {
						this.menuSelected(obj);
					}
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		if (Character.isJavaIdentifierPart(c)) {
			valueChanged();
		}
	}

	protected void valueChanged() {
		Object obj = getEditingCell();
		if (obj != null) {
			boolean show = false;
			if (obj != null && obj instanceof BasicNode) {
				BasicNode node = (BasicNode) this.getEditingCell();
				ValueAction action = node.getValueAction();
				if (action != null) {
					if (action instanceof TypeAction) {
						show = true;
					}
				}
			}
			if (show) {
				String text = this.textArea.getText().trim();
				if (text.startsWith("@")) {
					text = text.substring(1);
				}
				if (text.equals("")) {
					this.hint.setVisible(false);
				} else {

					this.makeTypeValues(text);
				}
			}
		}
	}

	private Thread searchThread = null;

	private void makeTypeValues(String text) {
		if (searchThread != null) {
			searchThread.interrupt();
		}
		hint.clear();
		searchThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);

					List<String> list = ClassInfos.lookupClass(text);
					if (list.size() > 0) {
						for (String s : list) {
							BeeTreeNode node = new BeeTreeNode(s);
							node.setUserObject(s);
							hint.getTreeRoot().add(node);
						}

						hint.getTree().updateUI();
						hint.getTree().expandPath(new TreePath(hint.getTreeRoot()));
						hint.showPop(textArea.getHeight());
						textArea.requestFocus();

					}
				} catch (InterruptedException e) {

				}

			}

		});
		searchThread.start();
	}

	public BeePopupTreeMenu getPopup() {
		return this.hint;
	}

	@Override
	public void stopEditing(boolean cancel) {
		ValueAction action = this.getAction();
		if (action == null) {
			super.stopEditing(cancel);
		} else {
			Object cell = this.getEditingCell();
			if (action instanceof TypeAction) {
				super.stopEditing(true);
			} else {
				super.stopEditing(cancel);
			}

			if (!cancel) {
				if (action instanceof LabelAction) {
					if (cell instanceof BasicNode) {
						LabelAction la = (LabelAction) action;
						la.onValueSet(textArea.getText().trim(), (BasicNode) cell, sheet);
					}
				}
			}

			this.graphComponent.getGraph().refresh();
		}
		this.hint.setVisible(false);

	}

	@Override
	public void menuSelected(Object menu) {
		BeeTreeNode m = (BeeTreeNode) menu;
		String s = (String) m.getValue();
		ValueAction action = this.getAction();
		if (action != null) {

			if (action instanceof TypeAction) {

				TypeAction type = (TypeAction) action;

				BClass bclass = CodecUtils.getClassFromJavaClass(Application.getInstance().getCurrentProject(), s);
				ComplexNode var = new ComplexNode();
				var.setBClass(bclass);
				var.setLogicName(bclass.getLogicName());
				var.setName(bclass.getName());

				if (!type.canAct(var)) {
					super.stopEditing(true);
				} else {
					if (!action.onValueSet(var, (BasicNode) getEditingCell(), sheet)) {
						type.afterActFalse(hint, sheet);
						return;
					} else {
						super.stopEditing(false);
					}
				}
			}
		}

		this.hint.setVisible(false);

	}

	private ValueAction getAction() {
		Object obj = getEditingCell();
		if (obj != null) {

			if (obj != null && obj instanceof BasicNode) {
				BasicNode node = (BasicNode) this.getEditingCell();
				ValueAction action = node.getValueAction();
				if (action != null) {
					return action;
				}
			}
		}
		return null;
	}

}
