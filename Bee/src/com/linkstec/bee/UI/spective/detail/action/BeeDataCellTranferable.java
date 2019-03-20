package com.linkstec.bee.UI.spective.detail.action;

import java.awt.datatransfer.StringSelection;
import java.io.Serializable;

public class BeeDataCellTranferable extends StringSelection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5878532904365265300L;
	private Object userObject;

	public BeeDataCellTranferable(String data) {
		super(data);
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

}
