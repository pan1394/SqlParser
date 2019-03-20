package com.linkstec.bee.core.fw.basic;

import java.util.List;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BEditorModel;

public interface ITableObject extends BTableElement {

	public BVariable getModel(List<BEditorModel> models);

	public BParameter getParameter();
}
