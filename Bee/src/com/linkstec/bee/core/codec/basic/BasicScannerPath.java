package com.linkstec.bee.core.codec.basic;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.logic.BLogicBody;

public class BasicScannerPath {
	private BasicScannerPath parent;

	private BParameter parameter;
	private BLogicBody body;

	public BasicScannerPath() {
	}

	public BLogicBody getBody() {
		return body;
	}

	public void setBody(BLogicBody body) {
		this.body = body;
	}

	public BParameter getParameter() {
		return parameter;
	}

	public void setParameter(BParameter parameter) {
		this.parameter = parameter;
	}

	public BasicScannerPath getParent() {
		return parent;
	}

	public void setParent(BasicScannerPath parent) {
		this.parent = parent;
	}

}
