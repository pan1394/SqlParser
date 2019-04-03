package com.linkstec.sql;

import com.linkstec.utils.Utilities;

public abstract class AbstractNode {

	protected String rawString;
	
	protected abstract void convert();

	public String getRawString() {
		return rawString;
	}

	public void setRawString(String rawString) {
		this.rawString = Utilities.tab2Space(rawString);
		this.convert();
	}
}
