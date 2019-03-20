package com.linkstec.bee.core.fw.basic;

public interface ILogicConnector {

	public static final int LOOP = 1;
	public static final int YES = 2;
	public static final int NO = 3;
	public static final int NEXT = 4;

	public void setType(int type);

	public int getType();

	public ILogicCell getNext();
}
