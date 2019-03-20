package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BOnewordLine;

public class OnewordNode extends BasicNode implements BOnewordLine, Serializable, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4413937044100187980L;
	private String type = WORD_BREAK;
	private String label;
	private String numberBID;
	private String valueLabel;

	public OnewordNode() {
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		this.getGeometry().setWidth(300);
		this.setOpaque(false);

		HorizonalLayout layout = new HorizonalLayout();
		this.setLayout(layout);
		this.makeNumberedLabel(layout);

		this.setWord(WORD_BREAK);
	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		LabelNode value = new LabelNode();
		this.valueLabel = value.getId();
		value.setOpaque(false);
		layout.addNode(value);

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
	public void setWord(String type) {
		this.type = type;
		if (type.equals(WORD_BREAK)) {
			this.getCellByBID(valueLabel).setValue("ループ処理を終了させる");
		} else if (type.equals(WORD_CONTINUE)) {
			this.getCellByBID(valueLabel).setValue("ループの以下をスキップし、ループの次回処理を実施する");
		}

	}

	@Override
	public String getWord() {
		return this.type;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;

	}

	@Override
	public String getLabel() {
		return this.label;
	}

	private String nodeDesc = "ループを完全中断したり、ループを中断して次ぎのループ処理へ回したりする";

	@Override
	public String getNodeDesc() {
		return nodeDesc;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_BREAK_ICON;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
