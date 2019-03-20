package com.linkstec.bee.UI.popup;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;

public class BeePopupTreeMenu extends BeePopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3520602436407026427L;
	private BeeTree tree;
	private BeeTreeNode root;

	public BeePopupTreeMenu(JComponent component) {
		super(component);
		root = new BeeTreeNode(null);
		this.setFocusable(false);
		tree = new BeeTree(root) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1544937116208269807L;

			@Override
			public void valueChanged(TreeSelectionEvent e) {

			}

		};
		tree.setFocusable(false);
		tree.setDialog(true);
		tree.setRowHeight((int) (BeeUIUtils.getDefaultFontSize() * 1.5));
		this.contents.setLayout(new BorderLayout());
		this.contents.add(tree, BorderLayout.CENTER);

		tree.setBackground(BACK_COLOR);

		tree.setRowHeight((int) (BeeUIUtils.getDefaultFontSize() * 1.5));

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				BeeTreeNode node = (BeeTreeNode) tree.getLastSelectedPathComponent();
				if (node != null) {
					for (IBeePopupMenuAction action : actions) {
						action.menuSelected(node);
					}
				}
				adapter.mouseClicked(e);
			}

		});
	}

	public BeeTree getTree() {
		return this.tree;
	}

	public BeeTreeNode getTreeRoot() {
		return this.root;
	}

	@Override
	public void clear() {
		this.root.removeAllChildren();
		this.tree.clearSelection();
	}

	@Override
	public void selectNext() {

		if (selected + 1 < tree.getRowCount()) {
			selected++;
			TreePath path = this.tree.getPathForRow(selected);
			BeeTreeNode node = (BeeTreeNode) path.getLastPathComponent();
			if (!node.isLeaf()) {
				selectNext();
			} else {
				this.setSelected(selected);
			}
		}
	}

	@Override
	public void selectBefore() {
		if (selected > 0) {
			selected--;
			TreePath path = this.tree.getPathForRow(selected);
			BeeTreeNode node = (BeeTreeNode) path.getLastPathComponent();
			if (!node.isLeaf()) {
				selectBefore();
			} else {
				this.setSelected(selected);
			}
		}
	}

	@Override
	public void setSelected(int index) {
		selected = index;
		tree.setSelectionRow(index);
		contents.scrollRectToVisible(tree.getPathBounds(tree.getSelectionPath()));
	}

	public Object getItemAt(int index) {
		if (tree.getRowCount() > index) {
			return tree.getPathForRow(index).getLastPathComponent();
		} else {
			return null;
		}
	}

	@Override
	public Object getSelectedItem() {
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			return path.getLastPathComponent();
		}
		return null;// tree.getLastSelectedPathComponent();
	}

}
