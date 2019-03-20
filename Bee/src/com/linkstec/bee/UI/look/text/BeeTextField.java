package com.linkstec.bee.UI.look.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.core.ClassInfos;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeTextField extends JTextField implements Serializable, KeyListener, DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283109524849019194L;

	private Object userObject;
	// private List<BeePopupMenuItem> items;
	// private BeePopupMenu menu;
	// private ImageIcon icon;
	private String backgroundText = null;
	private int inclTab = BeeUIUtils.getRoundCornerSize();
	// private int margin_left = BeeUIUtils.getDefaultFontSize();
	private boolean roundBorder = false;
	private boolean validate = true;

	public BeeTextField() {
		// this.addKeyListener(this);
		// this.addFocusListener(this);
		// menu = new BeePopupMenu(this);
	}

	private BProject project;
	private transient BeePopupTreeMenu hint;

	public void setProject(BProject project) {
		this.project = project;

		if (project == null) {
			this.hint = null;
			this.userObject = null;
			this.removeKeyListener(this);
			this.getDocument().removeDocumentListener(this);
			return;
		}
		if (hint != null) {
			return;
		}
		hint = new BeePopupTreeMenu(this);
		hint.setName("HINT");

		this.addKeyListener(this);
		this.getDocument().addDocumentListener(this);

		hint.addAction(new IBeePopupMenuAction() {

			@Override
			public void menuSelected(Object menu) {
				BeeTreeNode m = (BeeTreeNode) menu;
				String s = (String) m.getValue();
				BClass bclass = CodecUtils.getClassFromJavaClass(project, s);

				userObject = bclass;
				// setText(bclass.getQualifiedName());
				hint.setVisible(false);
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
						if (list.size() > 0) {
							for (String s : list) {
								BeeTreeNode node = new BeeTreeNode(s);
								node.setImageIcon(BeeConstants.CLASSES_ICON);
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

	public void cancelled() {
		if (this.hint != null) {
			this.hint.setVisible(false);
		}
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public boolean isRoundBorder() {
		return roundBorder;
	}

	public void setRoundBorder(boolean roundBorder) {
		this.roundBorder = roundBorder;
	}

	public String getBackgroundText() {
		return backgroundText;
	}

	public void setBackgroundText(String backgroundText) {
		this.backgroundText = backgroundText;
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		// if (!this.getText().equals("")) {
		// if (!this.isValidTextNow()) {
		// g.setColor(Color.RED);
		// g.setFont(g.getFont().deriveFont(10F));
		// String s = this.getText();
		// int width = SwingUtilities.computeStringWidth(g.getFontMetrics(), s);
		// int count = (int) (width / 2.8);
		// for (int i = 0; i < count; i++) {
		// g.drawString("^", this.getMargin().left + 2 + i * 5, this.getHeight() + 3);
		// }
		//
		// }
		//
		// } else {
		// if (this.backgroundText != null) {
		// g.setColor(Color.GRAY);
		// g.drawString(this.backgroundText, this.getMargin().left + 2, this.getHeight()
		// - g.getFont().getSize());
		// }
		// }

	}

	@Override
	protected void paintBorder(Graphics g) {
		if (this.roundBorder) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawPolygon(this.getShape(this.getWidth() - 1, this.getHeight() - 1));
		} else {
			super.paintBorder(g);
		}

	}

	private Polygon getShape(int w, int h) {
		int[] xp = new int[] { 0, 0, inclTab, w - inclTab, w, w, w - inclTab, inclTab };
		int[] yp = new int[] { h - inclTab, inclTab, 0, 0, inclTab, h - inclTab, h, h };
		Polygon shape = new Polygon(xp, yp, xp.length);
		return shape;
	}

	// public boolean isValidTextNow() {
	// if (!this.validate) {
	// return true;
	// }
	// if (items != null) {
	// for (BeePopupMenuItem item : items) {
	// if (this.getText().equals(item.getName())) {
	// return true;
	// }
	// }
	// return false;
	// }
	// return true;
	// }

	// public List<BeePopupMenuItem> getItems() {
	// return items;
	// }
	//
	// public void setItems(List<BeePopupMenuItem> items) {
	// this.items = items;
	// menu.clear();
	// menu.setItems(items);
	// }

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_DOWN) {
			hint.selectNext();
			hint.repaint();
			e.consume();
		} else if (code == KeyEvent.VK_UP) {
			hint.selectBefore();
			hint.repaint();
			e.consume();
		} else if (code == KeyEvent.VK_ENTER) {

			if (hint != null) {
				Object obj = hint.getSelectedItem();
				if (obj == null) {
					obj = hint.getItemAt(0);
				}
				if (obj != null) {
					BeeTreeNode m = (BeeTreeNode) obj;
					String s = (String) m.getValue();
					BClass bclass = CodecUtils.getClassFromJavaClass(project, s);
					userObject = bclass;
					hint.setVisible(false);
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		if (Character.isJavaIdentifierPart(c)) {
			makeTypeValues(this.getText(), this);
		}
		e.consume();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

		makeTypeValues(this.getText(), this);

	}

	@Override
	public void removeUpdate(DocumentEvent e) {

		makeTypeValues(this.getText(), this);

	}

	@Override
	public void changedUpdate(DocumentEvent e) {

		makeTypeValues(this.getText(), this);

	}

	// public ImageIcon getIcon() {
	// return icon;
	// }
	//
	// public void setIcon(ImageIcon icon) {
	// this.icon = icon;
	// }

	// @Override
	// public void focusGained(FocusEvent e) {
	// if (items != null) {
	// menu.showPop(this.getHeight());
	// }
	// }

	// @Override
	// public void focusLost(FocusEvent e) {
	// // menu.setVisible(false);
	// }

	// @Override
	// public void keyTyped(KeyEvent e) {
	//
	// }
	//
	// @Override
	// public void keyPressed(KeyEvent e) {
	// if (items != null) {
	// menu.showPop(this.getHeight());
	// }
	// }
	//
	// @Override
	// public void keyReleased(KeyEvent e) {
	//
	// }
	//
	// public BeePopupMenu getPopMenu() {
	// return this.menu;
	// }
}
