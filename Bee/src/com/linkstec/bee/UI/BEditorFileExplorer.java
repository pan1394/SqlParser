package com.linkstec.bee.UI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.detail.action.EditorActions;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BFileExplorer;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BTreeNode;

public abstract class BEditorFileExplorer extends BeeTree implements BFileExplorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -738604937770633346L;
	protected Configuration config;

	public BEditorFileExplorer(BeeTreeNode node, Configuration config) {
		super(node);
		this.config = config;
		this.initAll();
		this.expandPath(new TreePath(this.getRoot()));
	}

	public void initAll() {
		List<BProject> projects = config.getProjects();
		for (BProject project : projects) {
			this.addProject(project);
		}
	}

	protected void addPopupAction(BeeTreeNode n) {
		this.popmenu.removeAll();

		BeeTreeFileNode node = (BeeTreeFileNode) n;
		if (node == null) {
			return;
		}
		if (node.isProject()) {
			BProject project = node.getProject();
			this.addPopupItem("削除", BeeConstants.DELETE_ICON, new DeleteAction(this));
			this.addPopupItem("プロジェクト設定", BeeConstants.CONFIG_ICON, new EditorActions.ConfigAction(project));
		} else {
			this.addPopupItem("削除", BeeConstants.DELETE_ICON, new DeleteAction(this));
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this, node));
			this.addPopupItem("プロパティ", BeeConstants.PROPERTY_ICON,
					new BEditorActions.FilePropertyAction(new File(node.getFilePath())));
		}
	}

	public void doDelete() {
		BeeTreeFileNode node = (BeeTreeFileNode) getLastSelectedPathComponent();
		if (node != null) {
			if (node.getUserObject() != null && node.getUserObject() instanceof BProject) {
				BProject project = (BProject) node.getUserObject();
				int r = JOptionPane.showConfirmDialog(Application.FRAME, project.getName() + "を削除します。よろしいですか？",
						"プロジェクト削除", JOptionPane.OK_CANCEL_OPTION);
				if (r == JOptionPane.OK_OPTION) {
					Application.getInstance().deleteProject(project);
				}
			} else {
				String path = node.getFilePath();
				File file = new File(path);
				this.deleteFile(file);
				DefaultTreeModel model = (DefaultTreeModel) this.getModel();
				model.removeNodeFromParent(node);
			}
		}
	}

	public BTreeNode lookupNode(File file, BProject project) {
		BTreeNode root = (BTreeNode) this.getProjectRoot(project);
		return this.lookupNode(file, root);
	}

	public void updateNode(File file, BProject project) {
		if (file == null) {
			return;
		}
		BTreeNode node = this.lookupNode(file, project);
		if (node == null) {

			node = this.lookupNode(file.getParentFile(), project);
		}
		if (node != null) {
			this.updateFileNodes((BeeTreeNode) node);
		}

	}

	public BTreeNode lookupNode(File file, BTreeNode parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			BTreeNode c = (BTreeNode) parent.getChildAt(i);
			if (file != null) {

				if (c.getFilePath() != null && c.getFilePath().equals(file.getAbsolutePath())) {
					return c;
				}
			}

			if (!c.isLeaf()) {
				BTreeNode f = this.lookupNode(file, c);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}

	public void addProject(BProject project) {
		File file = new File(this.getRoot(project));
		BeeTreeFileNode projectNode = new BeeTreeFileNode(file, project);
		projectNode.setProject(project);
		projectNode.setDisplay(project.getName());
		projectNode.setImageIcon(BeeConstants.PROJECT_ICON);
		projectNode.setLeaf(false);
		projectNode.setFilePath(file.getAbsolutePath());
		projectNode.setProject(true);
		projectNode.setUserObject(project);

		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		model.insertNodeInto(projectNode, this.getRoot(), this.getRoot().getChildCount());
		if (file.exists()) {
			makeFileNodes(file.listFiles(), projectNode, project);
		}
	}

	public abstract String getRoot(BProject project);

	private void makeFileNodes(File[] fs, BeeTreeNode parent, BProject project) {
		for (File f : fs) {
			BeeTreeFileNode node = this.makeNode(f, project);
			if (node != null) {
				DefaultTreeModel model = (DefaultTreeModel) this.getModel();
				model.insertNodeInto(node, parent, parent.getChildCount());
				if (f.isDirectory()) {
					makeFileNodes(f.listFiles(), node, project);
				}
			}
		}
	}

	public BeeTreeFileNode makeNode(File f, BProject project) {
		if (!this.addable(f)) {
			return null;
		}
		BeeTreeFileNode node = new BeeTreeFileNode(f, project);
		node.setFilePath(f.getAbsolutePath());
		node.setProject(project);
		node.setDisplay(f.getName());
		if (f.isDirectory()) {
			node.setImageIcon(BeeConstants.FOLDER_ICON);
			node.setLeaf(false);
		}
		return node;
	}

	public void updateProject(BProject project) {
		synchronized (this) {
			updateFileNodes((BeeTreeNode) this.getProjectRoot(project));
		}
	}

	protected boolean addable(File file) {
		return true;
	}

	private void updateFileNodes(BeeTreeNode parent) {

		BProject project = parent.getProject();
		File file = new File(parent.getFilePath());
		File[] fs = file.listFiles();
		List<BeeTreeNode> sames = new ArrayList<BeeTreeNode>();
		List<BeeTreeNode> removes = new ArrayList<BeeTreeNode>();
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			if (i < parent.getChildCount()) {
				removes.add((BeeTreeNode) parent.getChildAt(i));
			}
		}
		if (fs == null) {
			return;
		}
		for (File f : fs) {

			BeeTreeFileNode node = this.makeNode(f, project);
			if (node != null) {
				BeeTreeNode oldNode = this.findSameNode(parent, node);

				if (oldNode == null) {
					DefaultTreeModel model = (DefaultTreeModel) this.getModel();
					model.insertNodeInto(node, parent, 0);
					if (!node.isLeaf()) {
						updateFileNodes(node);
					}
				} else {
					removes.remove(oldNode);
					if (f.isDirectory()) {
						sames.add(oldNode);
					}
				}
			}
		}
		for (BeeTreeNode node : removes) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			if (node.getParent() != null) {
				model.removeNodeFromParent(node);
			}
		}
		for (BeeTreeNode node : sames) {
			updateFileNodes(node);
		}

	}

	public BeeTreeFileNode getProjectRoot(BTreeNode node) {
		BeeTreeFileNode root = (BeeTreeFileNode) this.getRoot();
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeFileNode c = (BeeTreeFileNode) root.getChildAt(i);
			if (c.isProject() && c.getProject().equals(node.getProject())) {
				return c;
			}
		}
		return null;
	}

	private BTreeNode getProjectRoot(BProject project) {
		BeeTreeFileNode root = (BeeTreeFileNode) this.getRoot();
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeFileNode c = (BeeTreeFileNode) root.getChildAt(i);
			if (c.isProject() && c.getProject().equals(project)) {
				return c;
			}
		}
		return null;
	}

	@Override
	protected void doRefresh(BeeTreeNode node) {
		this.updateFileNodes(node);
	}

	@Override
	public void doRefresh() {
		updateAll();
	}

	public void updateAll() {
		List<BProject> projects = config.getProjects();
		List<BeeTreeFileNode> removes = new ArrayList<BeeTreeFileNode>();
		List<BeeTreeFileNode> sames = new ArrayList<BeeTreeFileNode>();
		List<BProject> adds = new ArrayList<BProject>();
		adds.addAll(projects);
		int count = this.getRoot().getChildCount();

		for (int i = 0; i < count; i++) {
			BeeTreeFileNode node = (BeeTreeFileNode) this.getRoot().getChildAt(i);
			boolean found = false;
			for (BProject project : projects) {
				if (project.getName().equals(node.getProject().getName())) {
					sames.add(node);
					found = true;
					adds.remove(project);
					break;
				}
			}
			if (!found) {
				removes.add(node);
			}
		}
		for (BeeTreeFileNode node : removes) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			model.removeNodeFromParent(node);
		}

		for (BProject p : adds) {
			this.addProject(p);
		}

		for (BeeTreeFileNode node : sames) {
			this.updateProject(node.getProject());
		}
	}

	public void deleteProject(BProject project, boolean deleteFiles) {
		this.updateAll();
	}

}
