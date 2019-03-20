package com.linkstec.bee.UI.editor.action;

import java.io.Serializable;

public interface BAction extends Serializable {

	public String getName();

	public void act();
}
