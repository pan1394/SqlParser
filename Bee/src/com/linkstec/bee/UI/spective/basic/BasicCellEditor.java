package com.linkstec.bee.UI.spective.basic;

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
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.UI.spective.basic.logic.BasicCellListeItem;
import com.linkstec.bee.UI.spective.basic.logic.IBasicCellList;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.view.mxCellEditor;

public class BasicCellEditor extends mxCellEditor implements KeyListener, Serializable, IBeePopupMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4338440740672366681L;
	private transient BeePopupTreeMenu hint;
	private BasicLogicSheet sheet;
	// private JPanel BasicListPanel=new JPanel();
	// private JScrollPane BasicListScroll=new JScrollPane();

	public BasicCellEditor(BasicLogicSheet sheet) {
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

		if (mx instanceof BNode) {
			BNode node = (BNode) mx;
			textArea.enableInputMethods(node.getUserAttribute("ENGLISH") == null);
		} else {
			textArea.enableInputMethods(true);
		}

		minimumHeight = (int) (mx.getGeometry().getHeight() * this.graphComponent.getGraph().getView().getScale());
		scrollPane.setSize(new Dimension(this.scrollPane.getWidth(), this.minimumHeight));
		textArea.setSize(new Dimension(this.textArea.getWidth(), this.minimumHeight));

		if (cell instanceof IBasicCellList) {
			makeTypeValues();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (this.getEditingCell() != null && this.getEditingCell() instanceof BNode) {
			// BNode node = (BNode) this.getEditingCell();

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
			makeTypeValues();
		}
	}

	protected void valueChanged() {

		Object node = this.getEditingCell();

		if (node instanceof IBasicCellList) {
			IBasicCellList list = (IBasicCellList) node;
			String text = this.textArea.getText().trim();
			List<BasicCellListeItem> values = list.getListItems(text, sheet);
			if (values != null) {

				// if (text.equals("")) {
				// this.hint.setVisible(false);
				// }

				boolean has = false;
				for (BasicCellListeItem item : values) {
					String s = item.getDisplayName();
					if (s != null) {
						boolean show = false;
						if (!text.trim().equals("")) {
							show = true;
						} else if (text.trim().toLowerCase().indexOf(text.toLowerCase()) >= 0) {
							show = true;
						}
						if (show) {
							BeeTreeNode b = new BeeTreeNode(s);
							b.setUserObject(item);
							hint.setAlwaysOnTop(true);
							hint.getTreeRoot().add(b);
							has = true;
						}
					}
				}
				if (has) {
					hint.getTree().updateUI();
					hint.getTree().expandPath(new TreePath(hint.getTreeRoot()));

					hint.setUserObject("DOING");
					hint.showPop(textArea.getHeight());
					textArea.requestFocus();
				}
			}
		}

	}

	private boolean template_doing = false;

	private Thread searchThread = null;

	private void makeTypeValues() {
		if (searchThread != null) {
			searchThread.interrupt();
		}
		hint.clear();
		searchThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);

					valueChanged();
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
		boolean close = false;
		if (cancel) {
			close = true;
		} else {
			if (!"DOING".equals(hint.getUserObject())) {
				close = true;
			} else {
				Object editing = this.getEditingCell();
				if (editing == null) {
					close = true;
				} else {
					Object cell = sheet.getGraph().getSelectionCell();
					if (cell != null) {
						if (!cell.equals(editing)) {
							close = true;
						}
					}
				}
			}
		}

		if (close) {
			super.stopEditing(cancel);
			this.hint.setVisible(false);
			this.hint.setAlwaysOnTop(false);
			this.hint.setUserObject("");
		}

	}

	@Override
	public void menuSelected(Object menu) {
		Object cell = this.getEditingCell();
		if (cell instanceof IBasicCellList) {
			IBasicCellList list = (IBasicCellList) cell;
			BeeTreeNode m = (BeeTreeNode) menu;
			BasicCellListeItem item = (BasicCellListeItem) m.getUserObject();
			list.onMenuSelected(item);
		}
		hint.setUserObject("");
		this.stopEditing(false);

	}

}
