package com.linkstec.bee.core.fw.editor;

import java.io.File;
import java.io.Serializable;

public interface BEditorModel extends Serializable {

	public String getLogicName();

	public void setLogicName(String logicName);

	public String getName();

	public void setName(String name);

	public BEditor getEditor(BProject project, File file, BWorkSpace space);

	public boolean isAnonymous();

	public Object doSearch(String keyword);

	public BEditor getSheet(BProject project);
}
