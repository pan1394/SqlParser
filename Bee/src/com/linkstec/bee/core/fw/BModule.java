package com.linkstec.bee.core.fw;

import java.io.Serializable;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BEditorModel;

public interface BModule extends Serializable, Cloneable {

	public List<BClass> getClassList();

	public String getLogicName();

	public List<BEditorModel> getList();

}
