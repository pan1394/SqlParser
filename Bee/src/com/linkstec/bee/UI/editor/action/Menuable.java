package com.linkstec.bee.UI.editor.action;

import com.mxgraph.model.mxICell;

public interface Menuable extends mxICell {
	public EditAction getAction();

	public String getCurrent();
}
