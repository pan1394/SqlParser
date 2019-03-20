package com.linkstec.excel;

import java.util.HashMap;
import java.util.List;

public class SqlNode {
	private List<SqlNode> children;
	private boolean isIndent;
	private HashMap<String, Object> attr;

	public List<SqlNode> getChildren() {
		return children;
	}

	public void setChildren(List<SqlNode> children) {
		this.children = children;
	}

	public boolean isIndent() {
		return isIndent;
	}

	public void setIndent(boolean indent) {
		isIndent = indent;
	}

	public HashMap<String, Object> getAttr() {
		return attr;
	}

	public void setAttr(HashMap<String, Object> attr) {
		this.attr = attr;
	}
}
