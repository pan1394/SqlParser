package com.linkstec.bee.UI.node.layout;

import com.linkstec.bee.UI.node.BasicNode;

public interface ILayoutBasic {

	public void setContainer(BasicNode node);

	public BasicNode getContainer();

	public boolean isSingleChild();

	public void setSingleChild(boolean singleChild);

	public void beforeContainerLayout();
}
