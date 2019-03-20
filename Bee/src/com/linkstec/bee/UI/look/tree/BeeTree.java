package com.linkstec.bee.UI.look.tree;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.Application;

public class BeeTree extends JTree implements TreeSelectionListener, TreeExpansionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458896748412912210L;

	private BeeTreeNode root;
	private List<BeeTreeAction> actions = new ArrayList<BeeTreeAction>();
	private List<File> error = null;
	protected BeeTreePopupMenu popmenu;
	private boolean dialog;

	public BeeTree(BeeTreeNode node) {
		super(node);
		this.root = node;
		this.setShowsRootHandles(true);
		this.setRootVisible(false);
		this.setCellRenderer(new BeeTreeRenderer());
		BeeActions.setTransfer(this);
		this.addTreeSelectionListener(this);
		this.setRowHeight((int) (BeeUIUtils.getDefaultFontSize() * 1.8));
		this.setBorder(null);
		this.setFocusable(true);
		popmenu = new BeeTreePopupMenu(this);
		this.setComponentPopupMenu(popmenu);
		this.setTransferHandler(new BeeFileTransferHander(this));
		this.addTreeExpansionListener(this);
		this.makeTreePopupProjectItems();
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				}
				Object obj = path.getLastPathComponent();
				if (obj instanceof BeeTreeNode) {
					BeeTreeNode node = (BeeTreeNode) obj;
					if (node.getProject() != null) {
						Application.getInstance().setCurrentProject(node.getProject());
						Application.getInstance().getEditor().getToolbar().refreshItems();
					}
				}
			}

		});

	}

	public boolean isDialog() {
		return dialog;
	}

	public void setDialog(boolean dialog) {
		this.dialog = dialog;
	}

	protected void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (File f : list) {
				this.deleteFile(f);
			}
		}
		file.delete();

	}

	public BeeTreeNode lookupNode(File file, BeeTreeNode parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode c = (BeeTreeNode) parent.getChildAt(i);
			if (!c.isLeaf()) {
				return this.lookupNode(file, c);
			} else {
				if (c.getFilePath() != null && c.getFilePath().equals(file.getAbsolutePath())) {
					return c;
				}
			}
		}
		return null;
	}

	public BeeTreeNode getRoot() {
		return root;
	}

	public void addPopupItem(String name, ImageIcon icon, Action action) {
		popmenu.add(BeeActions.bind(name, action, icon, null));
	}

	public BeeTreePopupMenu getPopupMenu() {
		return this.popmenu;
	}

	public void importFiles(List<File> files) {

	}

	public boolean canImportFiles() {
		return true;
	}

	public void importData(String data) {

	}

	public boolean canImportData() {
		return true;
	}

	public List<BeeTreeAction> getPopActions() {
		return this.actions;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

	public void expandAllNode(boolean expand) {
		expandAllNode(root, expand);
	}

	public void expandAllNode(TreePath parent, boolean expand) {
		TreeNode node = null;
		if (parent == null) {
			node = this.root;
			parent = new TreePath(root);
		} else {
			node = (TreeNode) parent.getLastPathComponent();
		}
		this.expandAllNode(node, expand);
	}

	public void expandAllNode(TreeNode node, boolean expand) {
		TreePath parent = new TreePath(node);
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAllNode(path, expand);
			}
		}

		if (parent.getPathCount() > 0) {
			if (expand) {
				this.expandPath(parent);
			} else {
				this.collapsePath(parent);
			}
		}
	}

	public void setNodeSelected(String filePath) {
		setNodeSelected(null, filePath);
	}

	private void setNodeSelected(TreePath parent, String filePath) {
		TreeNode node = null;
		if (parent == null) {
			node = this.root;
			parent = new TreePath(root);
		} else {
			node = (TreeNode) parent.getLastPathComponent();
		}

		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				BeeTreeNode n = (BeeTreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				if (n.getFilePath() != null) {
					if (n.getFilePath().equals(filePath)) {
						this.setSelectionPath(path);
						return;
					}
				}
				setNodeSelected(path, filePath);
			}
		}
	}

	@Override
	public Insets getInsets() {
		Insets in = super.getInsets();
		if (this.dialog) {
			return new Insets(0, 0, 0, 0);
		}
		in.top = in.top + BeeUIUtils.getDefaultFontSize();
		return in;
	}

	public void setError(List<File> error) {
		this.error = error;
		this.repaint();
	}

	public List<File> getError() {
		return this.error;
	}

	public void setError(BeeTreeNode node, List<File> error) {
		if (node == null) {
			node = root;
		}

		String path = node.getFilePath();
		node.setError(false);
		if (path != null) {
			for (File f : error) {
				if (path.equals(f.getAbsolutePath())) {
					node.setError(true);
					System.out.println(node.hashCode());
				}
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode child = (BeeTreeNode) node.getChildAt(i);
			setError(child, error);
		}

	}

	@Override
	public void updateUI() {
		super.updateUI();
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		BeeTreeNode node = (BeeTreeNode) event.getPath().getLastPathComponent();
		node.setExpanded(true);
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		BeeTreeNode node = (BeeTreeNode) event.getPath().getLastPathComponent();
		node.setExpanded(false);

	}

	public void updateOldNode(BeeTreeNode newNode, BeeTreeNode oldNode) {
		int count = oldNode.getChildCount();
		Hashtable<BeeTreeNode, BeeTreeNode> sames = new Hashtable<BeeTreeNode, BeeTreeNode>();
		List<BeeTreeNode> removes = new ArrayList<BeeTreeNode>();
		List<BeeTreeNode> adds = new ArrayList<BeeTreeNode>();
		for (int i = 0; i < count; i++) {
			BeeTreeNode oldChild = (BeeTreeNode) oldNode.getChildAt(i);
			BeeTreeNode sameChild = this.getSameChildOf(oldChild, newNode);
			if (sameChild != null) {
				sames.put(sameChild, oldChild);
			} else {
				removes.add(oldChild);
			}
		}
		count = newNode.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode newChild = (BeeTreeNode) newNode.getChildAt(i);
			BeeTreeNode sameChild = this.getSameChildOf(newChild, oldNode);
			if (sameChild == null) {
				adds.add(newChild);
			}
		}
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		for (BeeTreeNode n : removes) {
			model.removeNodeFromParent(n);
		}
		for (BeeTreeNode n : adds) {
			// TODO
			model.insertNodeInto(n, oldNode, 0);
		}
		Enumeration<BeeTreeNode> keys = sames.keys();
		while (keys.hasMoreElements()) {
			BeeTreeNode newChild = keys.nextElement();
			BeeTreeNode oldChild = sames.get(newChild);
			this.updateOldNode(newChild, oldChild);
		}

	}

	private BeeTreeNode getSameChildOf(BeeTreeNode child, BeeTreeNode parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode c = (BeeTreeNode) parent.getChildAt(i);
			if (c.getUniqueKey().equals(child.getUniqueKey())) {
				return c;
			}
		}
		return null;
	}

	public void makeExpand(BeeTreeNode cache, BeeTreeNode node) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode child = (BeeTreeNode) node.getChildAt(i);
			if (!child.isLeaf()) {
				BeeTreeNode target = this.findSameNode(cache, child);
				if (target != null) {
					if (target.isExpanded()) {
						TreePath path = new TreePath(child);
						this.expandPath(path);
						DefaultTreeModel model = (DefaultTreeModel) this.getModel();
						model.nodeChanged(node);

						makeExpand(cache, child);
					}
				}
			}
		}
	}

	public BeeTreeNode findSameNode(BeeTreeNode parent, BeeTreeNode node) {

		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			if (i >= parent.getChildCount()) {
				return null;
			}
			BeeTreeNode child = (BeeTreeNode) parent.getChildAt(i);
			if (!child.isLeaf()) {
				if (child.getUniqueKey() != null) {
					if (child.getUniqueKey().equals(node.getUniqueKey())) {
						return child;
					}
				}

				BeeTreeNode next = this.findSameNode(child, node);
				if (next != null) {
					return next;
				}
			}
		}

		return null;
	}

	public void makeTreePopupProjectItems() {
		BeeTreePopupMenu popmenu = this.getPopupMenu();
		popmenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				TreePath path = getSelectionPath();
				popmenu.removeAll();
				if (path != null) {
					BeeTreeNode node = (BeeTreeNode) path.getLastPathComponent();
					addPopupAction(node);
				} else {
					addPopupAction(null);
				}

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

			}

		});
	}

	protected void addPopupAction(BeeTreeNode node) {

	}

	public void doDelete() {

	}

	public void doRefresh() {

	}

	protected void doRefresh(BeeTreeNode node) {

	}

	public static class DeleteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;
		private BeeTree tree;

		public DeleteAction(BeeTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					tree.doDelete();

				}

			}).start();
		}

	}

	public static class RefreshAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;
		private BeeTreeNode node;
		private BeeTree tree;

		public RefreshAction(BeeTree tree, BeeTreeNode node) {
			this.node = node;
			this.tree = tree;
		}

		public RefreshAction(BeeTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					if (node != null) {
						tree.doRefresh(node);
					} else {
						tree.doRefresh();
					}

				}

			}).start();

		}

	}

}
