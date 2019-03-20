package com.linkstec.bee.core.fw.logic;

import java.util.List;

public interface BTryUnit extends BLogicUnit {

	public BLogicBody getTryEditor();

	public BLogicBody getFinalEditor();

	public List<BCatchUnit> getCatches();

	public void delteFinalEditor();

	public void addCatch(BCatchUnit catchnode);

	public void addFinalEditor();

	public void clearCatches();
}
