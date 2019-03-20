package com.linkstec.bee.UI.node;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BThrow;
import com.mxgraph.model.mxCell;

public class ThrowNode extends BasicNode implements BThrow, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4949625294919105327L;
	private String variableBID, labelBID;
	private String numberBID;

	public ThrowNode() {
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		HorizonalLayout layout = new HorizonalLayout();
		layout.setBetweenSpacing(10);
		layout.setSpacing(0);
		this.setLayout(layout);

		ComplexNode node = new ComplexNode();
		layout.addNode(node);
		this.variableBID = node.getId();

		this.makeNumberedLabel(layout);
	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		LabelNode node = new LabelNode();
		node.setValue("をメソッド外に上へ投げる");
		this.labelBID = node.getId();
		layout.addNode(node);
	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.THROW_ICON;
	}

	@Override
	public void setLabel(String label) {

	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void setExcetion(BValuable exception) {
		((BasicNode) this.getCellByBID(variableBID)).replace((mxCell) exception);
	}

	@Override
	public BValuable getException() {
		return LayoutUtils.getValueNode(this, variableBID);
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
