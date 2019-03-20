package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BInvoker;

public interface ITableSql extends Serializable {

	public BLogicProvider getProvider();

	public List<BEditorModel> getEditors();

	public void setEditors(List<BEditorModel> editors);

	public List<BInvoker> getBeforeSqlInvokers();

	public List<BInvoker> getInvokers();

	public void setFormat(boolean format);

	public boolean isFormat();

	public ITableSqlInfo getInfo();

}
