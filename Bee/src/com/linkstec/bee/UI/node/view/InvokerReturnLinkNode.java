package com.linkstec.bee.UI.node.view;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;

public class InvokerReturnLinkNode extends BasicNode implements ILink {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493640566521235869L;
	private BasicNode value;

	public InvokerReturnLinkNode() {
		this.applyStyle();
	}

	public void applyStyle() {
		this.addStyle("fontColor=blue");
		this.setOpaque(false);
	}

	public void setLinkNode(BasicNode node) {
		this.value = node;
	}

	public BasicNode getLinkNode() {
		return this.value;
	}

	// for debug display
	public Object getValue() {
		BasicNode node = (BasicNode) value;
		if (node != null && node.getNumber() != null) {
			String s = node.getNumber().toString();
			return " " + s.substring(0, s.length() - 2) + " ";
		} else {
			return super.getValue();
		}
	}

	@Override
	public void onMouseOver() {
		this.addStyle("fontColor=blue");
	}

	@Override
	public void onMouseOut() {
		this.addStyle("fontColor=" + BeeConstants.ELEGANT_BRIGHTER_GREEN_COLOR);

	}
}