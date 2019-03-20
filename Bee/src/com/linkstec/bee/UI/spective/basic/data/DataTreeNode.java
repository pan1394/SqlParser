package com.linkstec.bee.UI.spective.basic.data;

import java.io.Serializable;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BComponentNode;
import com.mxgraph.model.mxCell;

public class DataTreeNode extends BeeTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2094902873891928415L;
	private boolean checked = false;
	private String modelClassName;

	public DataTreeNode(Serializable data, String modelClassName) {
		super(data);
		this.modelClassName = modelClassName;
	}

	@Override
	public mxCell getTransferNode() {
		BComponentNode node = new BComponentNode();
		String className = this.getModelClassName();
		if (className == null) {
			return null;
		}
		try {
			BasicComponentModel model = (BasicComponentModel) this.getUserObject();
			node.setModel(model);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getModelClassName() {
		return modelClassName;
	}

	public void setModelClassName(String modelClassName) {
		this.modelClassName = modelClassName;
	}

}
