package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedValueLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableCondionNode;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionRenderer;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BasicEditDataSelection extends BeeTree implements TreeWillExpandListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1408660388026857947L;
	private BasicDataSelectionNode root;
	private BEditor editor;

	public BEditor getEditor() {
		return editor;
	}

	private BasicBookModel book;
	private BasicBook bookSheet;
	private BPath actionPath;
	private BasicEdit edit;
	private BLogicProvider provider;
	private static boolean prividerReload = false;

	@Override
	protected void addPopupAction(BeeTreeNode node) {
		super.addPopupAction(node);
		String name = "プロバイダーリロードする";
		if (prividerReload) {
			name = "プロバイダーリロードしない";
		}

		this.addPopupItem(name, BeeConstants.PROPERTY_ICON, new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				prividerReload = !prividerReload;
			}

		});
	}

	public BasicEditDataSelection(BasicDataSelectionNode node) {
		super(node);
		this.root = node;
		this.setBorder(null);
		this.setCellRenderer(new BasicDataSelectionRenderer(this));

		this.addTreeWillExpandListener(this);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				}
				Object obj = path.getLastPathComponent();
				if (obj instanceof BasicDataSelectionNode) {
					BasicDataSelectionNode node = (BasicDataSelectionNode) obj;
					setChecked(node, !node.isChecked());
					edit.dataSelected();
					repaint();
				}
			}
		});
	}

	public BasicBook getBookSheet() {
		return bookSheet;
	}

	private void setChecked(BasicDataSelectionNode node, boolean check) {
		node.setChecked(check);
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BasicDataSelectionNode child = (BasicDataSelectionNode) node.getChildAt(i);
			this.setChecked(child, check);
		}
	}

	public BasicEdit getEdit() {
		return edit;
	}

	public void setEdit(BasicEdit edit) {
		this.edit = edit;
	}

	public void setEditor(BEditor editor) {
		actionPath = null;
		this.editor = editor;
		if (editor instanceof BasicBook) {
			this.bookSheet = (BasicBook) editor;
			Component comp = bookSheet.getSelectedComponent();
			if (comp instanceof BEditor) {
				this.editor = (BEditor) comp;

			}
		} else if (editor instanceof BasicLogicSheet) {
			BasicLogicSheet sheet = (BasicLogicSheet) editor;
			this.bookSheet = sheet.findBook();
		}

		this.root.removeAllChildren();

		if (this.editor == null) {
			return;
		}

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		BasicDataSelectionNode loading = new BasicDataSelectionNode(editor.getProject());
		loading.setImageIcon(BeeConstants.FLOW_ICON);
		loading.setDisplay("Analizing usable data ...");
		root.add(loading);
		model.reload(root);

		new BeeThread(new Runnable() {

			@Override
			public void run() {
				root.removeAllChildren();
				if (editor instanceof BasicLogicSheet) {

					BasicLogicSheet sheet = (BasicLogicSheet) editor;
					DefaultTreeModel model = (DefaultTreeModel) getModel();
					actionPath = sheet.getPath();
					BasicBook bookSheet = sheet.findBook();
					book = bookSheet.getBookModel();

					BClass current = null;
					if (actionPath != null) {
						current = book.getLogic(
								BasicGenUtils.createClass((BActionModel) actionPath.getAction(), editor.getProject()));
					}
					if (actionPath != null) {
						provider = actionPath.getProvider();
						provider.getProperties().setCurrentDeclearedClass(current);
						Hashtable<BPath, List<BParameter>> outputs = BLogicEditActions.getUsefulData(actionPath);
						makeOutputs(outputs, root);
					}

					expandPath(new TreePath(root));
					model.reload(root);
				}
			}
		}).start();

	}

	public BLogicProvider getProvider() {
		return this.provider;
	}

	public BasicBookModel getBookModel() {
		return this.book;
	}

	public BPath getActionPath() {
		return this.actionPath;
	}

	public void setLogic(BNode node) {
		this.root.removeAllChildren();

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		BasicDataSelectionNode loading = new BasicDataSelectionNode(editor.getProject());
		loading.setImageIcon(BeeConstants.FLOW_ICON);
		loading.setDisplay("Analizing usable data ...");
		root.add(loading);
		model.reload(root);

		new BeeThread(new Runnable() {

			@Override
			public void run() {
				root.removeAllChildren();
				if (editor instanceof BasicLogicSheet) {
					BasicLogicSheet sheet = (BasicLogicSheet) editor;
					BasicBook bookSheet = sheet.findBook();
					book = bookSheet.getBookModel();

					sheet.makeDataSelectionWhenSelected(root, node);
					BLogic logic = null;
					ILogicCell cell = null;
					if (node instanceof ILogicCell) {
						cell = (ILogicCell) node;
						logic = cell.getLogic();
					} else if (node instanceof BTableCondionNode) {
						BTableCondionNode tvalue = (BTableCondionNode) node;
						logic = tvalue.getExpression();
						if (logic.getPath().getParent() == null) {
							logic.getPath().setParent(((BasicLogicSheet) editor).getPath());
						}
					}

					if (logic != null) {

						BPath path = logic.getPath();
						if (cell != null) {
							path.setCell(cell);
						}

						if (path.getParent() == null) {
							path.setParent(((BasicLogicSheet) editor).getPath());
						}

						if (provider != null) {
							path.getAction();
							provider.getProperties().setCurrentDeclearedClass(
									BasicGenUtils.createClass((BActionModel) path.getAction(), path.getProject()));
							provider.getProperties().addUserAttribute("SQL_INFO",
									book.getSqlInfos(sheet.getProject(), provider));
						}
						Hashtable<BPath, List<BParameter>> outputs = BLogicEditActions.getUsefulData(path);
						if (provider != null) {
							provider.getProperties().removeUserAttribute("SQL_INFO");
						}
						makeOutputs(outputs, root);

					}
				}

				BFixedValueNode fixed = new BFixedValueNode();
				BFixedValueLogic fl = new BFixedValueLogic(null, fixed);
				fixed.setLogic(fl);

				BasicDataSelectionNode fixedNode = new BasicDataSelectionNode(editor.getProject());
				fixedNode.setUserObject(fl);
				fixedNode.setDisplay("固定値");
				fixedNode.setImageIcon(BeeConstants.GREEN_STAR_ICON);
				root.add(fixedNode);

				expandPath(new TreePath(root));

				DefaultTreeModel model = (DefaultTreeModel) getModel();
				model.reload(root);

			}

		}).start();

	}

	private void makeOutputs(Hashtable<BPath, List<BParameter>> outputs, BasicDataSelectionNode node) {

		if (outputs != null) {

			Enumeration<BPath> keys = outputs.keys();
			while (keys.hasMoreElements()) {
				BPath key = keys.nextElement();
				List<BParameter> vars = outputs.get(key);
				for (BParameter var : vars) {
					this.makeDataVar(var, node);
				}
			}
		}
	}

	public boolean makeDataVar(BParameter var, BasicDataSelectionNode node) {
		BClass bclass = var.getBClass();

		boolean did = false;
		if (bclass.isData()) {
			BasicDataSelectionNode c = new BasicDataSelectionNode(root.getProject());
			c.setUserObject(var);
			c.setImageIcon(BeeConstants.PROPERTY_ICON);
			c.setLeaf(false);

			node.add(c);
			did = true;
		} else if (bclass.getQualifiedName().equals(List.class.getName())) {
			did = this.makeList(bclass, var, node);

		}
		if (!did) {
			IPatternCreator view = PatternCreatorFactory.createView();
			BAssignment assign = view.createAssignment();
			BParameter p = (BParameter) var.cloneAll();
			p.setName(var.getName() + "[" + var.getBClass().getName() + "]");
			assign.setLeft(p);
			this.makeAssignNodes(assign, node);
		} else {
			this.makeListSize(var, node);
		}
		return did;
	}

	private boolean makeList(BClass bclass, BParameter var, BasicDataSelectionNode node) {
		List<BType> types = bclass.getParameterizedTypes();

		for (BType type : types) {
			if (type instanceof BClass) {
				BClass data = (BClass) type;
				if (data.isData()) {
					BClass saved = this.book.getData(data);
					if (saved != null) {
						data = saved;
					}
					BasicDataSelectionNode c = new BasicDataSelectionNode(root.getProject());
					c.setUserObject(var);
					c.setImageIcon(BeeConstants.TABLES_ICON);
					c.setLeaf(false);

					node.add(c);
					return true;
				}
			}
		}
		return false;
	}

	private void makeAssignNodes(BAssignment node, BasicDataSelectionNode parent) {
		if (node == null)
			return;

		if (!node.getLeft().getLogicName().equals("serialVersionUID")) {
			BasicDataSelectionNode c = new BasicDataSelectionNode(parent.getProject());
			c.setUserObject(node);
			if (parent.isAs()) {
				c.setAs(true);
				c.setDisplay("AS " + node.getLeft().getName());
				c.setImageIcon(BeeConstants.GREEN_STAR_ICON);
			} else if (parent.isInput()) {
				c.setInput(true);
				c.setDisplay("編集可能固定値[" + node.getLeft().getName() + "]");
				c.setImageIcon(BeeConstants.GREEN_STAR_ICON);
			} else if (parent.isInput()) {
				c.setInput(true);
				c.setDisplay("編集可能固定値[" + node.getLeft().getName() + "]");
				c.setImageIcon(BeeConstants.GREEN_STAR_ICON);
			} else {
				BParameter var = node.getLeft();
				BClass bclass = var.getBClass();

				boolean did = false;
				if (bclass.getQualifiedName().equals(List.class.getName())) {
					did = this.makeList(bclass, var, parent);
					this.makeListSize(var, parent);
				}
				if (!did) {
					c.setImageIcon(BeeConstants.VAR_COLUMN_CELL_ICON);
				} else {
					return;
				}
			}
			parent.add(c);
		}

	}

	private void makeListSize(BParameter var, BasicDataSelectionNode parent) {
		if (!this.root.equals(parent)) {
			return;
		}
		try {
			if (var.getBClass().getQualifiedName().equals(List.class.getName())) {
				Method method = List.class.getMethod("size");
				IPatternCreator view = PatternCreatorFactory.createView();
				BMethod b = view.createMethod();
				CodecUtils.copyMethodToBMethod(null, method, b, parent.getProject());

				BInvoker invoker = view.createMethodInvoker();
				String name = var.getName().trim() + "のサイズ";

				var = (BParameter) var.cloneAll();
				var.setClass(false);
				var.setCaller(true);
				invoker.setInvokeParent(var);
				invoker.setInvokeChild(b);
				invoker.addUserAttribute("DISPLY_NAME", name);
				BasicDataSelectionNode sizeInvoker = new BasicDataSelectionNode(parent.getProject());
				sizeInvoker.setUserObject(invoker);
				sizeInvoker.setImageIcon(BeeConstants.VAR_PARAMETER_ICON);
				sizeInvoker.setDisplay(name);
				parent.add(sizeInvoker);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		BasicDataSelectionNode node = (BasicDataSelectionNode) event.getPath().getLastPathComponent();
		Object obj = node.getUserObject();

		if (obj instanceof BClass) {
			BClass bclass = (BClass) obj;
			if (bclass.isData()) {
				node.removeAllChildren();
				this.makeList(bclass, node);
			}

		} else if (obj instanceof BVariable) {
			node.removeAllChildren();

			BVariable para = (BVariable) obj;
			BClass bclas = para.getBClass();
			if (bclas.isData()) {
				this.makeList(bclas, node);
			} else if (bclas.getQualifiedName().equals(List.class.getName())) {
				List<BType> types = bclas.getParameterizedTypes();
				for (BType type : types) {
					if (type instanceof BClass) {
						BClass b = (BClass) type;
						if (b.isData()) {

							this.makeList(b, node);
						}
					}
				}
			}
		}
		DefaultTreeModel m = (DefaultTreeModel) this.getModel();
		m.reload(node);
	}

	public boolean isProviderReload() {
		return prividerReload;
	}

	private void makeList(BClass data, BasicDataSelectionNode node) {

		// BClass saved = this.book.getData(data);
		// if (saved != null) {
		// data = saved;
		// }
		Object loader = data.getUserAttribute("LOADER");
		List<BAssignment> vars = data.getVariables();
		boolean loaded = false;
		if (loader != null) {
			if (loader.equals("PROVIDER")) {
				loaded = true;
				for (BAssignment a : vars) {
					if (a.getLeft().getBClass().getQualifiedName().equals(List.class.getName())) {
						Debug.a();
					}
				}
			}
		}

		// List<BAssignment> noseriaVars = new ArrayList<BAssignment>();
		// for (BAssignment a : vars) {
		// if (!a.getLeft().getLogicName().equals("serialVersionUID")) {
		// noseriaVars.add(a);
		// }
		// }
		//
		// // if not defined ,use all possible data
		// if (noseriaVars.size() == 0) {
		if (!loaded) {
			BasicDataDictionary dict = getBookDictoinery(this.bookSheet);
			data = dict.getModel();
			vars = data.getVariables();
		}
		// }
		for (BAssignment a : vars) {
			this.makeAssignNodes(a, node);
		}
	}

	public static BasicDataDictionary getBookDictoinery(BasicBook book) {
		int count = book.getTabCount();
		for (int i = 0; i < count; i++) {
			Component comp = book.getComponentAt(i);
			if (comp instanceof BasicDataDictionary) {
				return (BasicDataDictionary) comp;
			}
		}
		return null;
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		BasicDataSelectionNode node = (BasicDataSelectionNode) event.getPath().getLastPathComponent();
		TreeNode parent = node.getParent();
		if (parent != null && parent.equals(root)) {
			node.removeAllChildren();
		}
	}

	// public BLogicProvider getProvider(BActionModel model, BProject project,
	// boolean reload, BClass current) {
	// if (model == null) {
	// return null;
	// }
	// BLogicProvider p = ProviderManager.getProvider(model, project, reload);
	// p.getProperties().setCurrentDeclearedClass(current);
	// return p;
	// }

}
