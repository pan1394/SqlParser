package com.linkstec.bee.UI.spective.detail.edit;

import java.io.Serializable;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;

public interface DropAction extends Serializable {

	public void onDrop(BasicNode source, BeeGraphSheet sheet, int index);

	public boolean isDropTarget(BasicNode source);

	public BasicNode beforeDrop(BasicNode source, BeeGraphSheet sheet);
}
