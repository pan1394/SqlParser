package com.linkstec.bee.UI.node.layout;

import java.io.Serializable;

import com.linkstec.bee.UI.node.BasicNode;

public interface LayoutListener extends Serializable {
	public void layoutCompleted(BasicNode node);
}
