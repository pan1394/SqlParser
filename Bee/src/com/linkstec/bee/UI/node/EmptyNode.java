package com.linkstec.bee.UI.node;

import java.io.Serializable;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BEmptyUnit;

public class EmptyNode extends BasicNode implements Serializable, BEmptyUnit, IUnit {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2584378814527883753L;

	public EmptyNode() {
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		this.getGeometry().setWidth(200);
		this.setValue("空行;");
		this.setOpaque(false);
	}

	@Override
	public void setLabel(String label) {
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}

}
