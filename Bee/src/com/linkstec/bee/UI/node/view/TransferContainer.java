package com.linkstec.bee.UI.node.view;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.LoopNode;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.core.fw.IUnit;
import com.mxgraph.model.mxICell;

public class TransferContainer extends BasicNode implements IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4423472520325172133L;

	public TransferContainer() {
		VerticalLayout layout = new VerticalLayout();
		this.setLayout(layout);
	}

	public void addNode(BasicNode node) {
		this.getLayout().addNode(node);
	}

	public void toBlock(BLockNode block, int start) {
		int count = this.getChildCount();
		List<mxICell> cells = new ArrayList<mxICell>();
		for (int i = 0; i < count; i++) {
			cells.add(this.getChildAt(i));
		}
		for (int i = 0; i < cells.size(); i++) {
			mxICell cell = cells.get(i);
			if (cell instanceof LoopNode) {
				LoopNode loop = (LoopNode) cell;
				loop.makeConnector();
			}
			block.insert(cell, start + i);
		}
	}

	@Override
	public void makeDefualtValue(Object target) {
		// TODO Auto-generated method stub

	}
}
