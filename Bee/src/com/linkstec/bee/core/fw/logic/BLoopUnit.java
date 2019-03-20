package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;

public interface BLoopUnit extends BLogicUnit {

	public static final int TYPE_WHILE = 0;
	public static final int TYPE_ENHANCED = 1;
	public static final int TYPE_FORLOOP = 2;
	public static final int TYPE_DOWHILE = 3;

	public void addEnhancedCondition(BParameter variable, BValuable expression);

	public BValuable getCondition();

	public void clearCondition();

	public BLogicBody getEditor();

	public void setEditor(BLogicBody body);

	public void addCondition(BValuable object);

	public void setLoopType(int type);

	public int getLoopType();

	public BParameter getEnhanceVariable();

	public BValuable getEnhanceExpression();

	public void setForLoopInitializers(List<BAssign> assigns);

	public List<BAssign> getForLoopInitializers();

	public void setUpdats(List<BValuable> updates);

	public List<BValuable> getUpdates();

}
