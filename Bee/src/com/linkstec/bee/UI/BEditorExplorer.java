package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.button.BeeIconButton;
import com.linkstec.bee.UI.look.button.BeeIconButtonAction;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.look.text.BeeTextField;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.popup.BeePopupMenu;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.mxgraph.util.mxResources;

public abstract class BEditorExplorer extends JPanel implements KeyListener, IBeePopupMenuAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5503880573146154211L;
	private BEditorFileExplorer fileExplorer;
	private BEditorOutlookExplorer outLineExplore;
	protected BeeTabbedPane pane;
	private JScrollPane explorerScrollPane, outlineScrollPane;
	protected BeePopupMenu actionMenu;
	protected Configuration config;
	private BeeTextField search = new BeeTextField() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1983001837475403681L;

		@Override
		public void setUserObject(Object userObject) {
			if (userObject != null && userObject instanceof BeeTreeFileNode) {
				BeeTreeFileNode node = (BeeTreeFileNode) userObject;
				BeeActions.addDetailPane(node, node.getProject());
			}
			super.setUserObject(userObject);
		}

	};

	public BEditorExplorer(Configuration config) {
		this.config = config;
		setLayout(new BorderLayout());
		// search
		// search.setIcon(BeeConstants.SEARCH_ICON);
		search.setFont(BeeUIUtils.getDefaultFont());
		actionMenu = new BeePopupMenu(this);
		actionMenu.addAction(this);
		actionMenu.getTopContainer().add(search, BorderLayout.NORTH);

		search.addKeyListener(this);

		// tree

		fileExplorer = this.createFileTree();
		fileExplorer.setRootVisible(false);

		outLineExplore = this.createOutline();
		outLineExplore.setScrollsOnExpand(true);

		explorerScrollPane = new JScrollPane(fileExplorer);
		outlineScrollPane = new JScrollPane(outLineExplore);
		outlineScrollPane.setBorder(null);
		explorerScrollPane.setBorder(null);

		pane = new BeeTabbedPane();
		pane.setEditable(false);
		pane.insertTab(mxResources.get("explorer"), BeeConstants.EXPLORE_FILE_TILE_ICON, explorerScrollPane, null, 0);
		pane.insertTab(mxResources.get("outline"), BeeConstants.EXPLORE_OUTLINE_TILE_ICON, outlineScrollPane, null, 1);
		this.add(pane, BorderLayout.CENTER);

		BeeIconButton menuButton = new BeeIconButton(BeeConstants.SEARCH_ICON);
		menuButton.setName("検索");
		menuButton.getActions().add(new BeeIconButtonAction() {

			@Override
			public void execute(JComponent source) {
				if (menuButton.getActions().size() != 0) {
					actionMenu.clear();
					beforeMenuShow(fileExplorer);
					if (!actionMenu.isEmpty()) {
						actionMenu.setUserObject(fileExplorer);
						actionMenu.showPop(menuButton.getBounds().x,
								menuButton.getBounds().y + menuButton.getBounds().height);
						search.requestFocusInWindow();
						actionMenu.stick();
					}
				}
			}

		});
		pane.addComponentIconButton(menuButton);

	}

	public abstract BEditorOutlookExplorer createOutline();

	public abstract BEditorFileExplorer createFileTree();

	protected void beforeMenuShow(BeeTree tree) {
		actionMenu.setItems(getSearchResutlItem(tree, null));
	}

	public BEditorFileExplorer getFileExplorer() {
		return fileExplorer;
	}

	public BEditorOutlookExplorer getOutline() {
		return this.outLineExplore;
	}

	public void setOutLineSelected() {
		this.pane.setSelectedComponent(outlineScrollPane);
	}

	public void setExplorerSelected() {
		this.pane.setSelectedComponent(explorerScrollPane);
	}

	public void setNodeSelectedOnExplorer(String filePath) {
		fileExplorer.setNodeSelected(filePath);
	}

	@Override
	public void menuSelected(Object menu) {
		BeePopupMenuItem item = (BeePopupMenuItem) menu;
		if (item.getValue() instanceof BeeTreeNode) {
			BeeTreeFileNode node = (BeeTreeFileNode) item.getValue();
			BeeActions.addDetailPane(node, node.getProject());
			this.actionMenu.setVisible(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		String s = this.search.getText();
		s = s.trim();
		this.actionMenu.setItems(getSearchResutlItem((BeeTree) actionMenu.getUserObject(), s));
	}

	public List<BeePopupMenuItem> getSearchResutlItem(BeeTree tree, String s) {
		List<TreeNode> list = this.scanTree(tree, s);
		List<BeePopupMenuItem> items = new ArrayList<BeePopupMenuItem>();
		for (TreeNode node : list) {
			BeePopupMenuItem item = new BeePopupMenuItem();
			item.setValue(node);
			BeeTreeNode b = (BeeTreeNode) node;
			item.setIcon(b.getImgeIcon());
			items.add(item);
			item.setName(node.toString());
		}
		return items;
	}

	private List<TreeNode> scanTree(BeeTree tree, String s) {
		int count = tree.getRoot().getChildCount();
		List<TreeNode> list = new ArrayList<TreeNode>();
		for (int i = 0; i < count; i++) {
			TreeNode node = tree.getRoot().getChildAt(i);
			this.findNode(s, node, list);
		}
		return list;
	}

	private void findNode(String s, TreeNode node, List<TreeNode> list) {
		if (s == null || s.equals("")) {
			list.add(node);
		} else {
			if (node.toString().toLowerCase().indexOf(s.toLowerCase()) > 0) {
				list.add(node);
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeNode child = node.getChildAt(i);
			this.findNode(s, child, list);
		}
	}
}
