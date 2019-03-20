package com.linkstec.bee.UI.node.view;

import com.linkstec.bee.UI.node.BasicNode;

public interface ILink {
	public void setLinkNode(BasicNode node);

	public BasicNode getLinkNode();

	public void onMouseOver();

	public void onMouseOut();
}
