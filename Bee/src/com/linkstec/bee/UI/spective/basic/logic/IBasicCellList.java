package com.linkstec.bee.UI.spective.basic.logic;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;

public interface IBasicCellList {

	public List<BasicCellListeItem> getListItems(String text, BasicLogicSheet sheet);

	public void onMenuSelected(BasicCellListeItem item);

	public Object getValue();
}
