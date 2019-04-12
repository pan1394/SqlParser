package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.task.console.ConsoleDisplay;
import com.linkstec.bee.UI.spective.basic.BasicEditDataSelection;
import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.data.PluralDataLogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.var.VarLogicList;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataCopyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BFiexedReturnValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNesSelectNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableRecordListNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableTargetTablesNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWithSelectNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLayerLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BTableElement;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BTableSheet extends BPatternSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7829343450006851659L;

	public BTableSheet(BProject project, BTableModel model) {
		super(project, model);
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.IO_MODEL_ICON;
	}

	@Override
	public void makeDataSelectionWhenSelected(BasicDataSelectionNode root, BNode logic) {
		BTableModel model = (BTableModel) this.getEditorModel();
		BPath path = model.getActionPath();
		BActionModel action = (BActionModel) path.getAction();
		List<BEditorModel> models = this.findBook().getALLModels();

		BLogicProvider provider = model.getActionPath().getProvider();

		if (provider != null) {
			provider.getProperties().setCurrentDeclearedClass(BasicGenUtils.createClass(action, path.getProject()));
		}

		BSqlModel bmodel = new BSqlModel(models, true, provider);

		String sql = model.getSQL(bmodel);
		ConsoleDisplay console = Application.getInstance().getBasicSpective().getTask().getConsole();
		Application.getInstance().getBasicSpective().getTask().setTaskConsoleSelected();
		console.clear();
		String[] ss = sql.split("\r\n");
		for (String s : ss) {
			if (!s.trim().equals("")) {
				s = s.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				s = "<span>" + s + "</span>";
				if (!s.equals("")) {
					console.addText(s, this.getProject());
				}
			}
		}
		console.addText("-----------論理SQL------------", this.getProject());
		String exp = model.getSQLExp(bmodel);
		ss = exp.split("\r\n");
		for (String s : ss) {
			if (!s.trim().equals("")) {
				s = s.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				s = "<span>" + s + "</span>";
				if (!s.equals("")) {
					console.addText(s, this.getProject());
				}
			}
		}

		if (logic instanceof BTableTargetTablesNode) {
			super.makeDataSelectionIO(root, action);

		} else if (logic instanceof ILogicCell || logic instanceof BTableValueNode) {
			List<ITableObject> list = this.getDefinedTables();
			if (list != null) {
				for (ITableObject object : list) {
					BasicDataSelectionNode c = new BasicDataSelectionNode(this.getProject());
					c.setUserObject(object.getModel(models));
					c.setDisplay(
							object.getModel(models).getBClass().getName() + " " + object.getModel(models).getName());
					c.setImageIcon(BeeConstants.VAR_COLUMN_ICON);
					c.setLeaf(false);
					root.add(c);
				}
			}

		}
		super.makeDataSelectionWhenSelected(root, logic);

		BasicDataSelectionNode nesSql = new BasicDataSelectionNode(this.getProject());
		BTableNesSelectNode select = new BTableNesSelectNode(null);
		nesSql.setUserObject(select);
		nesSql.setDisplay("子SELECT追加");
		nesSql.setImageIcon(BeeConstants.GREEN_STAR_ICON);
		root.add(nesSql);

		BasicDataSelectionNode with = new BasicDataSelectionNode(this.getProject());
		BTableWithSelectNode widthNode = new BTableWithSelectNode(null);
		with.setUserObject(widthNode);
		with.setDisplay("WITH追加");
		with.setImageIcon(BeeConstants.GREEN_STAR_ICON);
		root.add(with);

		BasicDataSelectionNode returnSql = new BasicDataSelectionNode(this.getProject());
		BFiexedReturnValueNode r = new BFiexedReturnValueNode();
		returnSql.setUserObject(r);
		returnSql.setDisplay("改行");
		returnSql.setImageIcon(BeeConstants.GREEN_STAR_ICON);
		root.add(returnSql);

		BPath p = path;

		int maxDepth = 20;
		int depth = 0;
		// union or nes select

		while (p != null && p.getSelfAction() == null && depth < maxDepth) {
			p = p.getParent();
			depth++;
		}

		BLogic l = p.getLogic();

		if (l instanceof BLayerLogic) {
			BLayerLogic layer = (BLayerLogic) l;

			List<BParameter> inputs = layer.getParameters();
			for (BParameter param : inputs) {
				BClass bclass = param.getBClass();
				if (bclass.isData()) {
					BasicDataSelectionNode fixedDtoNode = new BasicDataSelectionNode(this.getProject());
					fixedDtoNode.setUserObject(param);
					fixedDtoNode.setDisplay("意味あり固定値[" + param.getName() + "]");
					fixedDtoNode.setImageIcon(BeeConstants.GREEN_STAR_ICON);
					fixedDtoNode.setLeaf(false);
					fixedDtoNode.setInput(true);
					root.add(fixedDtoNode);
				}

			}
		}

	}

	public List<ITableObject> getDefinedTables() {
		mxICell cell = this.getRoot();
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);
			if (child instanceof BTableTargetTablesNode) {
				BTableTargetTablesNode node = (BTableTargetTablesNode) child;
				return node.getObjects();
			}
		}
		return null;
	}

	@Override
	public void makeLogic(BasicEditNode root, BasicEditDataSelection selection, DefaultTreeModel model) {
		List<BasicDataSelectionNode> list = new ArrayList<BasicDataSelectionNode>();
		scanNode(root, (BasicDataSelectionNode) selection.getRoot(), selection, model, list);
		List<BInvoker> vars = new ArrayList<BInvoker>();
		for (BasicDataSelectionNode node : list) {
			Object obj = node.getUserObject();
			TreeNode p = node.getParent();
			if (p instanceof BasicDataSelectionNode) {
				BasicDataSelectionNode prent = (BasicDataSelectionNode) p;
				Object prentObj = prent.getUserObject();
				if (obj instanceof BAssignment) {
					if (prentObj instanceof BVariable) {

						BVariable data = (BVariable) prentObj;
						BAssignment assign = (BAssignment) obj;

						IPatternCreator view = PatternCreatorFactory.createView();
						BInvoker invoker = view.createMethodInvoker();

						invoker.setInvokeParent(data);
						BParameter left = assign.getLeft();
						invoker.setInvokeChild((BValuable) left.cloneAll());

						vars.add(invoker);//

					}
				}
			}
		}

		if (vars.size() > 1) {
			BasicEditNode node = new BasicEditNode(this.getProject());
			node.setUserObject(vars);
			node.setImageIcon(BeeConstants.P_EXPRESSION_ICON);
			String ex = null;
			for (BInvoker param : vars) {

				String name = ((BParameter) param.getInvokeChild()).getName();
				if (ex == null) {
					ex = name;
				} else {
					ex = ex + "+" + name;
				}

			}
			node.setDisplay(ex);
			model.insertNodeInto(node, root, 0);
		}
	}

	@Override
	public void makeLogics(BasicDataSelectionNode select, BasicEditNode parent, BPath path, DefaultTreeModel model) {
		LogicList logicList = null;
		mxCell cell = select.getTransferNode();

		if (cell instanceof BInvoker) {
			BInvoker invoker = (BInvoker) cell;

			logicList = new VarLogicList(invoker);

		} else if (cell instanceof BVariable) {
			BVariable var = (BVariable) cell;
			BClass bclass = var.getBClass();
			if (bclass.getQualifiedName().equals(List.class.getName())) {
				logicList = new PluralDataLogicList(path, var);
			} else {
				logicList = new VarLogicList(var, var);
			}
		} else if (cell instanceof BTansferHolderNode) {
			BTansferHolderNode holder = (BTansferHolderNode) cell;
			List<BNode> nodes = holder.getNodes();
			for (BNode n : nodes) {
				this.makeCell(n, parent, path, model);
			}
		}
		if (logicList != null) {
			List<BLogic> list = logicList.getList(path);
			if (list != null) {
				for (BLogic logic : list) {
					this.makeLogic(logic, parent, model);
				}
			}
		}
	}

	@Override
	public double layoutNode() {
		BTableModel model = (BTableModel) this.getEditorModel();
		mxICell cell = this.getRoot();
		int count = cell.getChildCount();
		double y = 100;
		double x = 10;
		double lastX = x;
		double lastY = y;
		double max = 0;
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);

			if (child instanceof BTableElement) {
				if (child instanceof BTableGroupNode) {
					BTableGroupNode node = (BTableGroupNode) child;
					if (node.getClass().getName().equals(BTableTargetTablesNode.class.getName())) {
						BTableTargetTablesNode from = (BTableTargetTablesNode) node;
						if (model instanceof BTableSelectModel) {
							BTableSelectModel select = (BTableSelectModel) model;
							from.setParentName(select.getParentName());
						}

						int nescount = from.getChildCount();
						for (int j = 0; j < nescount; j++) {
							mxICell nesChild = from.getChildAt(j);
							if (nesChild instanceof BTableGroupNode) {
								BTableGroupNode g = (BTableGroupNode) nesChild;
								g.layout(this);
							}
						}
					}

					node.layout(this);
					layoutGroups(node, x, y, lastX, lastY);
				} else if (child instanceof BTableNode) {
					BTableNode table = (BTableNode) child;
					table.doLayout(this);
					table.getGeometry().setX(10);

					double py = y + 20;
					Object[] cells = this.getGraph().getCellsBeyond(0, py, this.getRoot(), true, true);
					for (Object obj : cells) {
						if (!(obj instanceof BTableGroupNode) && !obj.equals(table)) {
							BNode bnode = (BNode) obj;
							double ny = bnode.getGeometry().getY() + bnode.getGeometry().getHeight() + 20;
							py = Math.max(py, ny);
						}
					}
					table.getGeometry().setY(py);

				} else if (child instanceof BDataCopyNode) {

				}

				lastX = x;
				lastY = y;

				y = child.getGeometry().getY() + child.getGeometry().getHeight();
				x = child.getGeometry().getX() + child.getGeometry().getWidth();

				max = Math.max(max, child.getGeometry().getY() + child.getGeometry().getHeight());
			}

		}
		return max;
	}

	public void layoutGroups(BTableGroupNode node, double x, double y, double lastX, double lastY) {

		this.initStart(node, 10, y + 20);

	}

	private void initStart(BTableGroupNode node, double x, double y) {
		mxGeometry geo = node.getGeometry();
		geo.setX(x);
		geo.setY(y);

	}

	@Override
	public void onSelected() {
		this.layoutNode();
		super.onSelected();
	}

	@Override
	public void receiveClipboard(String value) {
		this.getGraph().getModel().beginUpdate();
		IPatternCreator view = PatternCreatorFactory.createView();
		Object obj = this.getGraph().getSelectionCell();
		double start = 20;

		if (value != null) {
			BTableRecordListNode node = null;
			List<mxICell> half = new ArrayList<mxICell>();
			if (obj instanceof BTableRecordListNode) {
				node = (BTableRecordListNode) obj;
			} else if (obj instanceof BTableValueNode) {
				BTableValueNode tv = (BTableValueNode) obj;
				if (tv.getParent() instanceof BTableRecordListNode) {
					node = (BTableRecordListNode) tv.getParent();
					start = tv.getGeometry().getY() + tv.getGeometry().getHeight();
					int selectedIndex = node.getIndex(tv);
					int count = node.getChildCount();
					for (int i = selectedIndex + 1; i < count; i++) {
						half.add(node.getChildAt(i));
					}

				}
			}
			if (node != null) {
				List<ITableObject> tables = this.getDefinedTables();
				if (tables != null) {

					String[] values = value.split("\n");
					for (String s : values) {
						int index = s.indexOf(".");
						if (index < 0) {
							index = s.indexOf("．");
						}
						if (index > 0) {
							String valueTarget = s.substring(0, index);
							valueTarget = valueTarget.trim();
							String valueName = s.substring(index + 1);
							valueName = valueName.trim();
							this.findAndSet(valueTarget, valueName, tables, node, view, start);

						} else {
							BFixedValueNode fixed = new BFixedValueNode();
							fixed.setValue(s);
							fixed.getGeometry().setY(start);
							node.insert(fixed);
						}
						start = start + 40;
					}

					for (mxICell cell : half) {
						cell.getGeometry().setY(start);
						start = start + 40;
					}

					node.layout(this);
					this.getGraph().refresh();
				}
			}
		}
		this.getGraph().getModel().endUpdate();
	}

	private void findAndSet(String valueTarget, String valueName, List<ITableObject> tables, BTableRecordListNode node,
			IPatternCreator view, double start) {
		for (ITableObject table : tables) {
			BParameter param = table.getParameter();
			BClass bclass = param.getBClass();
			String asName = param.getLogicName();
			if (bclass != null && asName != null) {

				if (bclass.getName().equals(valueTarget)) {
					List<BAssignment> vars = bclass.getVariables();
					for (BAssignment assign : vars) {
						BParameter var = assign.getLeft();
						if (var.getName().equals(valueName)) {
							BInvoker bin = view.createMethodInvoker();
							bin.setInvokeParent(param);
							bin.setInvokeChild(var);
							node.inserInvoker(bin, new mxPoint(0, start));
						}
					}
				}
			}
		}
	}
}
