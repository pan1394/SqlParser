package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;

public interface BTableElement extends Serializable {

	public String getSQL(ITableSql sql);

	public String getSQLExp(ITableSql sql);

}
