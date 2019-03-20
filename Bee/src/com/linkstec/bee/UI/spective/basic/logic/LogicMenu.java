package com.linkstec.bee.UI.spective.basic.logic;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;
import com.linkstec.bee.UI.look.tip.BeeToolTip;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;

public class LogicMenu extends BasicMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9046121228027419096L;

	private BeeTree tree;
	private LogicMenuNode root;

	public LogicMenu(LogicMenuNode root) {
		super(null);
		this.root = root;

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

				LogicMenuNode data = (LogicMenuNode) node;
				// addTreePop(this, data);
			}

		};

		BeeToolTip.getInstance().register(tree);
		// tree.setCellRenderer(new DataRender());
		this.contents.add(tree);
	}

	private String getTootip(BeeTree tree) {
		Point p = tree.getMousePosition();
		if (p != null) {
			TreePath path = tree.getPathForLocation(p.x, p.y);
			if (path == null) {
				return null;
			}
			LogicMenuNode node = (LogicMenuNode) path.getLastPathComponent();
			return node.toString();
		}
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.SHEET_LOGIC_ICON;
	}

	public String getTitle() {
		return "共通関数・プロパティ";
	}

	public void clearAll() {

		this.root.removeAllChildren();
	}

	public void addMethod(BClass bclass, BMethod method, BProject project) {
		LogicMenuNode node = new LogicMenuNode(project);

		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();

		BVariable inparent = view.createVariable();
		inparent.setBClass(bclass);
		inparent.setClass(true);
		invoker.setInvokeParent(inparent);
		invoker.setInvokeChild(method);

		node.setImageIcon(BeeConstants.METHOD_STATIC_ICON);

		node.setUserObject(invoker);
		node.setDisplay(method.getName());
		root.add(node);
	}

	public void updateAll() {
		this.tree.expandPath(new TreePath(root));
		DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
		m.reload(root);
		this.updateUI();

	}

	public void addProperties(String name, Properties properties, BProject project) {
		LogicMenuNode node = new LogicMenuNode(project);
		node.setUserObject(properties);
		node.setDisplay(name);
		node.setImageIcon(BeeConstants.PROPERTY_ICON);
		root.add(node);

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = properties.getProperty(key);
			LogicMenuNode child = new LogicMenuNode(project);

			LogicMenuMessage message = new LogicMenuMessage();
			message.setId(key);
			message.setValue(value);
			message.setFileName(name);

			child.setUserObject(message);
			child.setImageIcon(BeeConstants.VAR_COLUMN_CELL_ICON);
			child.setDisplay(key + ":" + value);
			node.add(child);
		}
	}

	@Override
	protected void addAllItems(String text) {

	}

}
