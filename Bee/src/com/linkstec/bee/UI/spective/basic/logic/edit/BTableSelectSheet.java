package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableConnectWhereNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableGroupByNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableObjectNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableSelectItemsNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableSortNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableTargetTablesNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableUnionAllNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableUnionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWhereNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWithSelectNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWithesNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.model.mxICell;

public class BTableSelectSheet extends BTableSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4245251287627846967L;

	public BTableSelectSheet(BProject project, BTableSelectModel model) {
		super(project, model);
	}

	@Override
	public void insertStart(BPath path) {
		mxICell root = this.getRoot();

		BTableTargetTablesNode tables = new BTableTargetTablesNode(path);
		root.insert(tables);

		BTableSelectItemsNode select = new BTableSelectItemsNode(path);
		root.insert(select);

		BTableWhereNode where = new BTableWhereNode(path);
		root.insert(where);

		BTableSortNode sort = new BTableSortNode(path);
		root.insert(sort);

		BTableGroupByNode groupby = new BTableGroupByNode(path);
		root.insert(groupby);

		this.layoutNode();

	}

	public void addTableObjects(List<BTableObjectNode> list) {
		mxICell cell = this.getRoot();
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);
			if (child instanceof BTableTargetTablesNode) {
				BTableTargetTablesNode node = (BTableTargetTablesNode) child;
				node.addTableObjects((List<BTableObjectNode>) ObjectFileUtils.deepCopy(list));
				break;
			}
		}
	}

	@Override
	protected boolean cellsAdded(Object[] cells) {
		if (cells != null && cells.length == 1) {
			Object obj = cells[0];
			if (obj instanceof BTableWithSelectNode) {
				BTableWithSelectNode node = (BTableWithSelectNode) obj;
				mxICell cell = this.getRoot();
				int count = cell.getChildCount();

				BTableWithesNode withes = null;
				for (int i = 0; i < count; i++) {
					mxICell child = cell.getChildAt(i);
					if (child instanceof BTableWithesNode) {
						withes = (BTableWithesNode) child;
						break;
					}
				}
				if (withes == null) {
					withes = new BTableWithesNode(this.getPath());
					cell.insert(withes, 0);
				}
				node.getGeometry().setX(20);
				node.getGeometry().setX(120);
				withes.insert(node);
				withes.childAdded(node, this);
			}
		}
		return false;
	}

	@Override
	public void makeDataSelectionWhenSelected(BasicDataSelectionNode root, BNode logic) {
		super.makeDataSelectionWhenSelected(root, logic);
		BTableModel model = (BTableModel) this.getEditorModel();
		if (logic instanceof ILogicCell) {
			// AS items (as result set value to this dto)

			BPath path = model.getActionPath();

			int maxDepth = 20;
			int depth = 0;
			// union or nes select

			while (path != null && path.getSelfAction() == null && depth < maxDepth) {
				path = path.getParent();
				depth++;
			}

			List<BParameter> outputs = path.getLogic().getOutputs();

			BParameter asParam = null;
			for (BParameter p : outputs) {
				BClass bclass = p.getBClass();
				if (bclass.getQualifiedName().equals(List.class.getName())) {
					List<BType> types = bclass.getParameterizedTypes();
					for (BType type : types) {
						if (type instanceof BClass) {
							BClass b = (BClass) type;
							if (b.isData()) {

								asParam = PatternCreatorFactory.createView().createParameter();
								asParam.setName(p.getName());
								asParam.setLogicName(p.getLogicName());
								asParam.setBClass(b);
							}
						}
					}

				} else if (bclass.isData()) {
					asParam = p;
				}
			}
			if (asParam != null) {
				BasicDataSelectionNode c = new BasicDataSelectionNode(this.getProject());
				c.setUserObject(asParam);
				c.setDisplay("AS " + " " + asParam.getName());
				c.setImageIcon(BeeConstants.GREEN_STAR_ICON);
				c.setLeaf(false);
				c.setAs(true);
				root.add(c);
			}
		}

		BasicDataSelectionNode union = new BasicDataSelectionNode(this.getProject());
		BTableUnionNode node = new BTableUnionNode(null);
		union.setUserObject(node);
		union.setDisplay("UNION追加");
		union.setImageIcon(BeeConstants.GREEN_STAR_ICON);
		root.add(union);

		BasicDataSelectionNode unionall = new BasicDataSelectionNode(this.getProject());
		BTableUnionAllNode un = new BTableUnionAllNode(null);
		unionall.setUserObject(un);
		unionall.setDisplay("UNION ALL追加");
		unionall.setImageIcon(BeeConstants.GREEN_STAR_ICON);
		root.add(unionall);

		if (logic instanceof ITableObject) {
			ITableObject obj = (ITableObject) logic;

			BasicDataSelectionNode inner = new BasicDataSelectionNode(this.getProject());
			BTableConnectWhereNode cnode = new BTableConnectWhereNode(null, BTableConnectWhereNode.TYPE_INNER, obj);
			inner.setUserObject(cnode);
			// inner.setUserObject("INNER_JOIN");
			inner.setDisplay(cnode.getTitle());
			inner.setImageIcon(BeeConstants.GREEN_STAR_ICON);
			root.add(inner);

			BasicDataSelectionNode left = new BasicDataSelectionNode(this.getProject());
			BTableConnectWhereNode lnode = new BTableConnectWhereNode(null, BTableConnectWhereNode.TYPE_LEFT, obj);
			left.setUserObject(lnode);
			// left.setUserObject("LEFT_JOIN");
			left.setDisplay(lnode.getTitle());
			left.setImageIcon(BeeConstants.GREEN_STAR_ICON);
			root.add(left);
		}
	}

}
