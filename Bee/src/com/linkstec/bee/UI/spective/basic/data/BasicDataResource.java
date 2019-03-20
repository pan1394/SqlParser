package com.linkstec.bee.UI.spective.basic.data;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenuItem;
import com.linkstec.bee.UI.look.tip.BeeToolTip;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.IBasicSubsystemOwner;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.model.mxICell;

public class BasicDataResource extends BasicMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1964690846273970723L;
	private BeeTree tree;
	private DataTreeNode root;
	private DataTreeNode templateRoot;

	private IBasicSubsystemOwner model;
	private DataTreeNode projectNode;
	private BProject project;
	private SubSystem currentSub;

	public BasicDataResource(BasicSystemModel model, BProject project) {
		super(project);
		templateRoot = new DataTreeNode(null, null);
		this.model = model;
		this.project = project;

		root = new DataTreeNode(null, null);
		tree = new BeeTree(root) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4275686737568389114L;

			@Override
			public String getToolTipText() {
				return getTootip(tree);
			}

			@Override
			protected void addPopupAction(BeeTreeNode node) {
				super.addPopupAction(node);

				DataTreeNode data = (DataTreeNode) node;
				addTreePop(this, data);
			}

		};

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				}
				Object obj = path.getLastPathComponent();
				if (obj instanceof DataTreeNode) {
					DataTreeNode node = (DataTreeNode) obj;
					node.setChecked(!node.isChecked());
					tree.repaint();

					Object value = node.getUserObject();
					if (value instanceof SubSystem) {
						currentSub = (SubSystem) value;
					} else if (obj instanceof BasicComponentModel) {
						DataTreeNode parent = (DataTreeNode) node.getParent();
						value = parent.getUserObject();
						if (value instanceof SubSystem) {
							currentSub = (SubSystem) value;
						}
					} else {
						currentSub = null;
					}
					Application.getInstance().getEditor().getToolbar().refreshItems();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if (path == null) {
						return;
					}
					Object obj = path.getLastPathComponent();
					if (obj instanceof DataTreeNode) {
						DataTreeNode node = (DataTreeNode) obj;
						Object value = node.getUserObject();
						if (value instanceof BasicSystemModel) {
							BasicSystemModel model = (BasicSystemModel) value;
							BasicDataActions.BasicSystemAction action = new BasicDataActions.BasicSystemAction(project,
									model);
							action.actionPerformed(null);
						} else if (value instanceof SubSystem) {
							SubSystem sub = (SubSystem) value;
							BasicDataActions.BasicSystemAction action = new BasicDataActions.BasicSystemAction(project,
									sub);
							action.actionPerformed(null);
						} else if (value instanceof BasicComponentModel) {
							DataTreeNode parent = (DataTreeNode) node.getParent();
							Object o = parent.getUserObject();
							SubSystem sub = (SubSystem) o;
							BasicComponentModel data = (BasicComponentModel) value;
							BasicDataActions.DictionaryAction action = new BasicDataActions.DictionaryAction(project,
									sub, data);
							action.actionPerformed(null);

						}
					}
				}

			}

		});

		BeeToolTip.getInstance().register(tree);
		tree.setCellRenderer(new DataRender());
		this.contents.add(tree);

		projectNode = new DataTreeNode(project.getName(), null);
		projectNode.setProject(project);
		projectNode.setImageIcon(BeeConstants.PROJECT_ICON);
		projectNode.setUserObject(model);
		root.add(projectNode);

		this.makeSubs(projectNode, model, project);

		tree.expandPath(new TreePath(root));
	}

	public SubSystem getCurrentSub() {
		return currentSub;
	}

	public BProject getProject() {
		return project;
	}

	private void addTreePop(BeeTree tree, DataTreeNode node) {
		if (node == null) {
			return;
		}
		Object value = node.getUserObject();

		if (value instanceof BasicSystemModel) {
			BasicSystemModel model = (BasicSystemModel) value;
			tree.addPopupItem("サブシステム一覧編集", BeeConstants.DATA_ICON,
					new BasicDataActions.BasicSystemAction(project, model));
			tree.addPopupItem("最新情報へ更新", BeeConstants.DATA_ICON, new BasicDataActions.UpdateAllDataAction(project));
		} else if (value instanceof SubSystem) {
			SubSystem sub = (SubSystem) value;
			tree.addPopupItem("サブ分類一覧編集", BeeConstants.DATA_ICON, new BasicDataActions.BasicSystemAction(project, sub));
			tree.addPopupItem("コンポーネント一覧編集", BeeConstants.DATA_ICON,
					new BasicDataActions.SubSystemEditAction(project, sub));
			tree.addPopupItem("コンポーネントデータ編集", BeeConstants.DATA_ICON,
					new BasicDataActions.DictionaryAction(project, sub, null));
			tree.addPopupItem("新規デザイン", BeeConstants.BASIC_DESIGN_ICON,
					new BasicDataActions.AddNewAction(project, sub));
		} else if (value instanceof BasicComponentModel) {
			DataTreeNode parent = (DataTreeNode) node.getParent();
			Object o = parent.getUserObject();
			SubSystem sub = (SubSystem) o;
			BasicComponentModel data = (BasicComponentModel) value;
			tree.addPopupItem("コンポーネントデータ編集", BeeConstants.DATA_ICON,
					new BasicDataActions.DictionaryAction(project, sub, data));
		}

	}

	public void updateAll(BasicSystemModel model) {
		this.projectNode.removeAllChildren();

		this.model = model;
		this.makeSubs(projectNode, model, project);

		DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
		m.reload(projectNode);
	}

	public void updateSub(IBasicSubsystemOwner model) {
		DataTreeNode node = this.findModelNode(this.projectNode, model);
		if (node != null) {
			this.updateSub((DataTreeNode) node.getParent(), model);
			DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
			m.reload(projectNode);

			TreePath tp = new TreePath(m.getPathToRoot(node));
			tree.expandPath(tp);
		} else {
			Debug.a();
		}

	}

	public DataTreeNode findModelNode(DataTreeNode parent, IBasicSubsystemOwner model) {
		Object obj = parent.getUserObject();
		if (obj instanceof IBasicSubsystemOwner) {
			IBasicSubsystemOwner sub = (IBasicSubsystemOwner) obj;
			if (sub.getPath().equals(model.getPath())) {
				return parent;
			}
		}

		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			DataTreeNode node = (DataTreeNode) parent.getChildAt(i);
			DataTreeNode s = this.findModelNode(node, model);
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	public void updateSub(DataTreeNode parent, IBasicSubsystemOwner model) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			DataTreeNode node = (DataTreeNode) parent.getChildAt(i);
			Object obj = node.getUserObject();
			if (obj instanceof IBasicSubsystemOwner) {
				IBasicSubsystemOwner sub = (IBasicSubsystemOwner) obj;
				if (sub.getPath().equals(model.getPath())) {
					node.removeAllChildren();
					this.makeDatas(sub, node);

					List<SubSystem> subs = model.getSubs();
					for (SubSystem s : subs) {
						DataTreeNode subNode = new DataTreeNode(s.getName(), null);
						subNode.setImageIcon(BeeConstants.COMMON_METHOD_CLASS_ICON);
						subNode.setProject(project);
						subNode.setUserObject(s);

						node.add(subNode);
						this.makeDatas(s, subNode);
						this.makeSubs(subNode, s, project);
					}

					break;
				}
			}

		}

	}

	private void makeSubs(DataTreeNode p, IBasicSubsystemOwner model, BProject project) {
		List<SubSystem> subs = model.getSubs();
		if (subs == null) {
			return;
		}
		for (SubSystem sub : subs) {
			DataTreeNode s = new DataTreeNode(sub.getName(), null);
			s.setImageIcon(BeeConstants.COMMON_METHOD_CLASS_ICON);
			s.setProject(project);
			s.setUserObject(sub);
			p.add(s);

			List<SubSystem> ss = model.getSubs();
			for (SubSystem sss : ss) {
				this.makeSubs(s, sss, project);
			}

			this.makeDatas(sub, s);
		}
	}

	private void makeDatas(IBasicSubsystemOwner model, DataTreeNode node) {
		String path = model.getPath();

		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.exists() && !f.isDirectory()) {
				String name = f.getName();
				if (name.indexOf(".") > 0) {
					String logicName = name.substring(0, name.lastIndexOf("."));
					name = name.substring(name.lastIndexOf(".") + 1);

					if (name.startsWith("cm") && name.length() == 2) {
						Object obj;
						try {
							obj = ObjectFileUtils.readObject(f);
							if (obj.getClass().getName().equals(BasicComponentModel.class.getName())) {
								BasicComponentModel data = (BasicComponentModel) obj;
								DataTreeNode s = new DataTreeNode(logicName, BasicDataModel.class.getName());

								s.setUserObject(data);
								s.setImageIcon(data.getIcon());
								s.setProject(project);
								node.add(s);
							}
						} catch (Exception e) {

							e.printStackTrace();
						}

					}
				}
			}

		}
	}

	private String getTootip(BeeTree tree) {
		Point p = tree.getMousePosition();
		if (p != null) {
			TreePath path = tree.getPathForLocation(p.x, p.y);
			if (path == null) {
				return null;
			}
			DataTreeNode node = (DataTreeNode) path.getLastPathComponent();
			return node.toString();
		}
		return null;
	}

	public String getTitle() {
		return "データ";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.RESOURCE_ICON;
	}

	@Override
	protected void addAllItems(String text) {
		if (this.templateRoot == null) {
			return;
		}
		if (text == null) {
			return;
		}
		if (templateRoot.getUserObject() == null) {
			templateRoot = (DataTreeNode) root.cloneAll();
			templateRoot.setUserObject("INITED");
		}
		int count = templateRoot.getChildCount();
		this.root.removeAllChildren();
		for (int i = 0; i < count; i++) {
			this.root.add(((DataTreeNode) templateRoot.getChildAt(i)).cloneAll());
		}

		if (text != null && !text.trim().equals("")) {
			deleteItems(root, text);
		}
		this.tree.updateUI();
	}

	public void deleteItems(DataTreeNode node, String text) {
		int count = node.getChildCount();
		if (count == 0) {
			Object value = node.getValue();
			Object obj = node.getUserObject();
			boolean hit = false;
			if (value != null) {
				if (value.toString().toLowerCase().indexOf(text.toLowerCase()) >= 0) {
					hit = true;
				}
				if (!hit) {
					if (obj != null) {
						if (obj.toString().toLowerCase().indexOf(text.toLowerCase()) >= 0) {
							hit = true;
						}
					}
				}
			}
			if (!hit) {
				node.removeFromParent();
			}
		} else {
			for (int i = count - 1; i >= 0; i--) {
				deleteItems((DataTreeNode) node.getChildAt(i), text);
			}
		}

	}

	public static class ItemPanel extends BasicMenuItem {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6219767437547932608L;

		public ItemPanel(BasicMenu menu, mxICell cell, String text, ImageIcon icon) {
			super(menu);
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEADING);
			setLayout(layout);

			this.setMouseAction();
			this.setTransfer(cell);
			JLabel label = new JLabel();
			label.setText(text);
			label.setIcon(icon);
			this.add(label);
		}

	}

}
