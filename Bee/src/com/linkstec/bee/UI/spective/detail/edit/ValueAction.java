package com.linkstec.bee.UI.spective.detail.edit;

import java.io.Serializable;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;

public interface ValueAction extends Serializable {

	public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet);
}
