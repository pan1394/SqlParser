package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BGroupLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BSetterNode;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditRenderer;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BAssignment;

public class BasicEdit extends BEditorOutlookExplorer implements TreeWillExpandListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 638097704650875614L;
	private BEditor editor;
	private BasicEditNode root;

	private BasicEditDataSelection selection;
	private BasicLogicProperties properties;

	public BasicEdit(BasicEditNode node, BasicEditDataSelection selection, BasicLogicProperties properties) {
		super(node);
		root = node;
		this.selection = selection;
		this.properties = properties;
		this.selection.setEdit(this);
		this.addTreeWillExpandListener(this);

		this.setCellRenderer(new BasicEditRenderer(this));

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				}
				Object obj = path.getLastPathComponent();
				if (obj instanceof BasicEditNode) {
					BasicEditNode node = (BasicEditNode) obj;
					node.setChecked(!node.isChecked());
					new BeeThread(new Runnable() {

						@Override
						public void run() {
							properties.setTarget(BasicEdit.this, selection.getActionPath());
							repaint();
						}

					}).start();

				}
			}
		});
	}

	@Override
	public void update() {

	}

	@Override
	public void setEditor(BEditor editor) {
		this.editor = editor;
		if (editor instanceof BasicBook) {
			BasicBook book = (BasicBook) editor;
			Component comp = book.getSelectedComponent();
			if (comp instanceof BEditor) {
				this.editor = (BEditor) comp;
			}
		}
		this.selection.setEditor(this.editor);
		((BasicExplorer) Application.getInstance().getBasicSpective().getExplore()).setEditSelected();
	}

	public void setLogic(BNode logic) {
		this.selection.setLogic(logic);
		this.root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		model.reload(root);

	}

	@Override
	public void setSelected(Object node) {

	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		BasicEditNode node = (BasicEditNode) event.getPath().getLastPathComponent();
		Object obj = node.getUserObject();
		// this.makeGroup(node);
		if (obj instanceof BLogic) {
			BLogic logic = (BLogic) obj;
			List<BLogic> logics = logic.getSubLogics();
			if (logics != null) {
				for (BLogic l : logics) {
					this.makeLogic(l, node);
				}
			}
		} else if (obj instanceof BParameter) {
			node.removeAllChildren();
			// the output edit
			BParameter para = (BParameter) obj;
			BClass bclass = para.getBClass();
			if (bclass.isData()) {
				BClass data = bclass;
				BClass saved = this.selection.getBookModel().getData(data);
				if (saved != null) {
					data = saved;
				}
				List<BAssignment> vars = data.getVariables();
				// if not defined ,use all possible data
				if (vars.size() == 0) {
					BasicDataDictionary dict = selection.getBookDictoinery(this.selection.getBookSheet());
					data = dict.getModel();
					vars = data.getVariables();
				}
				for (BAssignment a : vars) {

					if (!a.getLeft().getLogicName().equals("serialVersionUID")) {

						BVariable v = BasicGenUtils.toView(a.getLeft());

						BSetterNode setter = new BSetterNode(this.selection.getActionPath(), para, v);
						BasicEditNode var = new BasicEditNode(this.editor.getProject());
						var.setUserObject(setter.getLogic());
						var.setDisplay(a.getLeft().getName() + "編集");
						var.setImageIcon(BeeConstants.VAR_COLUMN_CELL_ICON);
						node.add(var);
					}
				}
			}
		}
		DefaultTreeModel m = (DefaultTreeModel) this.getModel();
		m.reload(node);
	}

	private void makeGroup(BasicEditNode node) {
		BasicEditNode group = this.findGroupNode(node);
		if (group != null) {
			node.removeAllChildren();
			int count = root.getChildCount();
			for (int i = 0; i < count; i++) {
				BasicEditNode child = (BasicEditNode) root.getChildAt(i);
				Object obj = child.getUserObject();
				if (obj instanceof BGroupLogic) {

				} else {
					BasicEditNode n = (BasicEditNode) child.clone();
					node.add(n);
				}
			}
		}
	}

	private BasicEditNode findGroupNode(BasicEditNode node) {
		if (node == null) {
			return null;
		}

		Object obj = node.getUserObject();
		if (obj instanceof BGroupLogic) {
			return node;
		}
		return findGroupNode((BasicEditNode) node.getParent());
	}

	public void dataSelected() {
		root.setProject(editor.getProject());

		DefaultTreeModel model = (DefaultTreeModel) getModel();

		BasicEditNode loading = new BasicEditNode(editor.getProject());
		loading.setImageIcon(BeeConstants.FLOW_ICON);
		loading.setDisplay("loading...");
		root.add(loading);

		model.reload(root);

		new BeeThread(new Runnable() {

			@Override
			public void run() {
				root.setProject(editor.getProject());
				root.removeAllChildren();

				if (editor instanceof BasicLogicSheet) {
					BasicLogicSheet sheet = (BasicLogicSheet) editor;
					BEditorModel mo = sheet.getEditorModel();
					if (mo instanceof BPatternModel) {
						BPatternModel pattern = (BPatternModel) mo;
						BPath path = pattern.getActionPath();
						BActionModel action = (BActionModel) path.getAction();
						path.getProvider().getProperties()
								.setCurrentDeclearedClass(BasicGenUtils.createClass(action, path.getProject()));
					}
					sheet.makeLogic(root, selection, model);
					// int count = root.getChildCount();
					// if (count > 0) {
					// BasicEditNode node = new BasicEditNode(sheet.getProject());
					//
					// BGroupLogic group = new BGroupLogic(selection.getActionPath());
					// node.setUserObject(group);
					// node.setDisplay("ブロック処理");
					// node.setImageIcon(BeeConstants.GROUP_ICON);
					// root.add(node);
					// }
				}
				model.reload(root);
				properties.setTarget(null, null);

			}

		}).start();

	}

	private void makeLogic(BLogic logic, BasicEditNode parent) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		BasicEditNode node = new BasicEditNode(parent.getProject());
		node.setUserObject(logic);
		node.setDisplay(logic.getName());
		node.setImageIcon(logic.getIcon());
		model.insertNodeInto(node, parent, 0);

	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

}
