package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BSwitchUnit;

public class SwitchNode extends BasicNode implements Serializable, IUnit, BSwitchUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1376322231011943941L;
	private BValuable variable;
	private String label;
	private List<BConditionUnit> conditions = new ArrayList<BConditionUnit>();
	private VerticalLayout layout = new VerticalLayout();

	public SwitchNode() {
		layout.setSpacing(0);
		this.setLayout(layout);
		this.getGeometry().setWidth(BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH);
	}

	// for tooltip
	public String getNodeDesc() {
		return "分岐処理";
	}

	@Override
	public List<BConditionUnit> getConditionUnits() {
		return this.conditions;
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
	public void setVariable(BValuable variable) {
		this.variable = variable;
	}

	@Override
	public BValuable getVariable() {
		return this.variable;
	}

	@Override
	public void addCondition(BConditionUnit condition) {
		this.conditions.add(condition);
		layout.addNode((BasicNode) condition);
	}

	public ConditionNode makeDefault() {
		ConditionNode last = new ConditionNode();
		last.setLast(true);
		this.conditions.add(last);
		layout.addNode(last);
		return last;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
