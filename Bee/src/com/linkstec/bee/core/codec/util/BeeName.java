package com.linkstec.bee.core.codec.util;

import com.linkstec.bee.core.fw.editor.BEditorModel;

public class BeeName {
	public static final String TYPE_VAR = "var";
	public static final String TYPE_CLASS = "class";
	public static final String TYPE_METHOD = "method";
	public static final String TYPE_BOOK = "book";
	private BEditorModel model;
	private String type;
	private String name;
	private String logicName;

	public BEditorModel getModel() {
		return model;
	}

	public void setModel(BEditorModel model) {
		this.model = model;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

}
