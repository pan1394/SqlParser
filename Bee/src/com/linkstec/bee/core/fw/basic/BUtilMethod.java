package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BUtilMethod implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3889595971420536926L;
	public static final int STYLE_CALL = 1;
	public static final int STYLE_LOGIC = 2;
	public static final int STYLE_PARENT_MADE = 3;
	private BClass className;
	private BMethod method;
	private int useStyle = 0;

	public BUtilMethod(BClass className, BMethod method, int useStyle) {
		this.className = className;
		this.method = method;
		this.useStyle = useStyle;

	}

	public int getUseStyle() {
		return useStyle;
	}

	public void setUseStyle(int useStyle) {
		this.useStyle = useStyle;
	}

	public BClass getBClass() {
		return className;
	}

	public void setBClass(BClass className) {
		this.className = className;
	}

	public BMethod getBMethod() {
		return this.method;
	}

	public void setBMethod(BMethod method) {
		this.method = method;
	}

}
