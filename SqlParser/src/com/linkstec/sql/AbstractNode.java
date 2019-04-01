package com.linkstec.sql;

import com.linkstec.utils.Utlities;

public abstract class AbstractNode {

	protected String rawString;
	
	protected abstract void convert();

	public String getRawString() {
		return rawString;
	}

	public void setRawString(String rawString) {
		this.rawString = Utlities.tab2Space(rawString);
		this.convert();
	}
}
