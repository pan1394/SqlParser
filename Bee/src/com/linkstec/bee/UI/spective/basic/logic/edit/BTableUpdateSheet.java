package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.model.var.DataCopyLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataCopyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableObjectNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableTargetTablesNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWhereNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLayerLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class BTableUpdateSheet extends BTableSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7523163210202161522L;

	public BTableUpdateSheet(BProject project, BTableUpdateModel model) {
		super(project, model);
	}

	public void insertStart(BPath path) {
		BActionModel action = (BActionModel) path.getAction();
		mxICell root = this.getRoot();

		this.insertTableObjects(path, root);

		List<BasicComponentModel> models = action.getInputModels();
		for (BasicComponentModel model : models) {
			this.addTarget(path, model);
		}
		models = action.getOutputModels();
		for (BasicComponentModel model : models) {
			this.addTarget(path, model);
		}

	}

	public void insertTableObjects(BPath path, mxICell root) {
		BTableTargetTablesNode tables = new BTableTargetTablesNode(path);
		root.insert(tables);
	}

	public void addTarget(BPath path, BasicComponentModel model) {
		BTableNode node = new BTableNode(path, model);
		this.getGraph().addCell(node);
		BTableWhereNode where = new BTableWhereNode(path);
		where.setTitle("更新条件");
		this.getGraph().addCell(where);
	}

	public void makeLogics(BasicDataSelectionNode select, BasicEditNode parent, BPath path, DefaultTreeModel model) {
		super.makeLogics(select, parent, path, model);
		// BEditorModel m = this.getEditorModel();
		List<BTableNode> tables = new ArrayList<BTableNode>();
		mxICell root = this.getRoot();
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = root.getChildAt(i);
			if (child instanceof BTableNode) {
				BTableNode table = (BTableNode) child;
				tables.add(table);
			}
		}
		if (!tables.isEmpty()) {
			mxCell cell = select.getTransferNode();
			if (cell instanceof BTansferHolderNode) {
				BTansferHolderNode t = (BTansferHolderNode) cell;
				List<BNode> nodes = t.getNodes();
				if (nodes.size() == 1) {
					cell = nodes.get(0);
				}
			}

			if (cell instanceof BDetailNodeWrapper) {
				BDetailNodeWrapper wrapper = (BDetailNodeWrapper) cell;
				cell = wrapper.getNode();
			}

			if (cell instanceof BAssignment) {
				BAssignment a = (BAssignment) cell;
				cell = (mxCell) a.getLeft();
				BValuable value = a.getRight();
				Debug.a();
			}

			if (cell instanceof BTableObjectNode) {
				BTableObjectNode to = (BTableObjectNode) cell;
				cell = (mxCell) to.getParameter();
			}

			if (cell instanceof BParameter) {

				BParameter source = (BParameter) cell;
				if (source.getBClass().isData()) {

					BLogic pl = path.getLogic();
					if (pl instanceof BLayerLogic) {
						BLayerLogic layer = (BLayerLogic) pl;
						List<BParameter> parameters = layer.getParameters();
						for (BParameter target : parameters) {
							if (target.getBClass().isData()) {
								target = (BParameter) target.cloneAll();
								target.setClass(false);
								target.setCaller(true);
								target.setName(path.getAction().getName() + "編集対象");
								BasicEditNode copy = new BasicEditNode(this.getProject());
								copy.setImageIcon(BeeConstants.REFERENCE_ICON);
								BDataCopyNode bcl = new BDataCopyNode();
								DataCopyLogic logic = new DataCopyLogic(null, bcl);
								bcl.setLogic(logic);
								logic.setSource(source);
								logic.setTarget(target);
								copy.setUserObject(logic);
								copy.setDisplay(logic.getName());

								model.insertNodeInto(copy, parent, 0);
							}
						}
					}
				}
			}
		}
	}
}
