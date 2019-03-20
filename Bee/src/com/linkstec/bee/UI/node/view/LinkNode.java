package com.linkstec.bee.UI.node.view;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.BDropAction;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;

public class LinkNode extends BasicNode implements ILink {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493640566521235869L;
	private BasicNode value;

	public LinkNode() {
		this.applyStyle();
	}

	public void applyStyle() {
		this.addStyle("fontColor=3F8020");
		this.addStyle("fontStyle=" + mxConstants.FONT_BOLD);
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
			return s.substring(0, s.length() - 2);
		} else {
			return super.getValue();
		}
	}

	@Override
	public DropAction getDropAction() {
		BDropAction action = new BDropAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onDrop(BasicNode source, BeeGraphSheet sheet, int index) {
				if (source instanceof BValuable) {
					mxICell parent = getParent();
					if (parent != null && parent instanceof BasicNode) {
						// TODO how about its inner link
						value.removeFromParent();
						LinkNode.this.replace(source);
						LayoutUtils.doInvokerLayout((BasicNode) parent);
					}
				}

			}

			@Override
			public boolean isDropTarget(BasicNode source) {
				if (getParent() == null) {
					return false;
				}
				if (source instanceof BValuable) {
					return true;
				}
				return false;
			}

		};
		return action;
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
