package com.linkstec.bee.UI.spective.basic.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;

public class BasicModel extends mxGraphModel implements BEditorModel, ILogic {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4884765976980763789L;
	private String logicName;
	private String name;
	private SubSystem sub;

	public BasicModel(SubSystem sub) {
		this.sub = sub;
	}

	public SubSystem getSubSystem() {
		return this.sub;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BNode> getBNodes() {
		List<BNode> nodes = new ArrayList<BNode>();
		mxICell root = ((mxCell) this.getRoot()).getChildAt(0);
		this.findNode(root, nodes);
		return nodes;
	}

	private void findNode(mxICell node, List<BNode> nodes) {
		if (node == null) {
			return;
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell cell = node.getChildAt(i);
			if (cell instanceof BNode) {
				BNode n = (BNode) cell;
				nodes.add(n);
			}
			findNode(cell, nodes);
		}
	}

	@Override
	public NodeNumber getNumber() {
		return null;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BasicSystemModel basic = BasicSystemModel.load(project);
		SubSystem sub = new SubSystem(basic);
		sub.setLogicName(file.getParentFile().getParentFile().getName());
		BasicLogicSheet sheet = new BasicLogicSheet(project, sub);
		sheet.setModel(this);
		return sheet;
	}

	public mxICell getPossibleCellById(String scopeID) {
		return null;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public Object doSearch(String keyword) {
		return null;
	}

	@Override
	public BEditor getSheet(BProject project) {
		return new BasicLogicSheet(project, null);
	}

	public BGroupNode getGroupNode() {
		return null;
	}

	public List<BActionModel> getActions() {
		List<BActionModel> actions = new ArrayList<BActionModel>();
		mxCell root = (mxCell) this.getRoot();
		this.addAction(root, actions);
		return actions;
	}

	private void addAction(mxICell cell, List<BActionModel> list) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell sub = cell.getChildAt(i);
			if (sub instanceof BActionPropertyNode) {
				BActionPropertyNode node = (BActionPropertyNode) sub;
				list.add((BActionModel) node.getLogic().getPath().getAction());
			} else {
				this.addAction(sub, list);
			}
		}
	}

	public List<BActionPropertyNode> getActionNodes() {
		List<BActionPropertyNode> actions = new ArrayList<BActionPropertyNode>();
		mxCell root = (mxCell) this.getRoot();
		this.addActionNode(root, actions);
		return actions;
	}

	private void addActionNode(mxICell cell, List<BActionPropertyNode> list) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell sub = cell.getChildAt(i);
			if (sub instanceof BActionPropertyNode) {
				BActionPropertyNode node = (BActionPropertyNode) sub;
				list.add(node);
			} else {
				this.addActionNode(sub, list);
			}
		}
	}

	public List<BNode> getPreviousNodes(BNode node) {
		List<BNode> sources = new ArrayList<BNode>();
		if (node == null) {
			return sources;
		}
		int count = node.getEdgeCount();
		for (int i = 0; i < count; i++) {
			mxCell edge = (mxCell) node.getEdgeAt(i);
			mxCell source = (mxCell) edge.getSource();
			if (edge instanceof BActionNode) {
				BActionNode action = (BActionNode) edge;
				BActionPropertyNode p = action.getProperty();
				sources.add(p);
				sources.addAll(getPreviousNodes(p));
			}
			if (!source.equals(node) && source instanceof BNode) {
				BNode b = (BNode) source;
				sources.add(b);
				sources.addAll(getPreviousNodes(b));
			}
		}
		return sources;
	}

}
