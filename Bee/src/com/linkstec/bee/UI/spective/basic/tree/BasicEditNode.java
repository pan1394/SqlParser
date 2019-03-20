package com.linkstec.bee.UI.spective.basic.tree;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxCell;

public class BasicEditNode extends BeeTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387182265146182127L;
	private boolean checked = false;

	public BasicEditNode(BProject data) {
		super(data);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public mxCell getTransferNode() {
		Object obj = this.getUserObject();
		if (obj instanceof BLogic) {
			BLogic logic = (BLogic) obj;

			if (logic.getEditor() == null) {
				logic.getPath().setParent(null);

				return (mxCell) logic.getPath().getCell();
			}
		}

		return null;
	}

}
