package com.linkstec.sql;

import com.linkstec.utils.SqlUtilities;

public abstract class AbstractNode {

	protected String rawString;
	
	protected abstract void convert();

	public String getRawString() {
		return rawString;
	}

	public void setRawString(String rawString) {
		this.rawString = SqlUtilities.tab2Space(rawString);
		this.convert();
	}
}
