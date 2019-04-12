package com.linkstec.bee.core.fw.basic;

import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;

public interface BSQLModel extends BTableElement {

	public ITableSqlInfo getSqlInfo(BModule module, BLogicProvider provider);

	public List<BClass> getTables();

	public int getReturnType();
}
