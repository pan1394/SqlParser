package com.linkstec.bee.core.fw.basic;

import com.linkstec.bee.core.fw.BModule;

public interface BSQLModel extends BTableElement {

	public ITableSqlInfo getSqlInfo(BModule module, BLogicProvider provider);
}
