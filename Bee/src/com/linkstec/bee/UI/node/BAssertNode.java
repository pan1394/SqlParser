package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BAssert;
import com.mxgraph.model.mxCell;

public class BAssertNode extends BasicNode implements Serializable, IUnit, BAssert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1908005655411096396L;
	private String label;

	private String numberBID;

	private String valueLabel;

	public BAssertNode() {

		this.addStyle("portConstraint=west");
		this.getGeometry().setWidth(150);
		this.setOpaque(false);
		HorizonalLayout layout = new HorizonalLayout();
		layout.setBetweenSpacing(1);
		this.setLayout(layout);
		this.makeNumberedLabel(layout);

	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		ComplexNode value = new ComplexNode();
		this.valueLabel = value.getId();
		layout.addNode(value);

		LabelNode node = new LabelNode();
		node.setValue("検証：");
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
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setExpression(BValuable value) {
		((BasicNode) this.getCellByBID(valueLabel)).replace((mxCell) value);
	}

	@Override
	public BValuable getExpression() {
		return LayoutUtils.getValueNode(this, valueLabel);
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_CHOICE_ICON;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}

}
