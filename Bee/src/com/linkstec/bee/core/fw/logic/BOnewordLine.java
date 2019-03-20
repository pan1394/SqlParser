package com.linkstec.bee.core.fw.logic;

public interface BOnewordLine extends BLogicUnit {
	public static final String WORD_BREAK = "break";
	public static final String WORD_CONTINUE = "continue";

	public void setWord(String type);

	public String getWord();
}
