package com.linkstec.bee.UI.editor.action;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BValuable;

public interface ConditionAction extends Serializable {
	public boolean will(BValuable var);
}
