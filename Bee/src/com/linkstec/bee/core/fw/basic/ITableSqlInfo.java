package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;

public interface ITableSqlInfo extends Serializable {
	public void setGroupBy();

	public void setSortBy();

	public void setFixedValue();

	public void setUnion();

	public void setNesSelect();

	public void setEqualsExceptedExpression();

	public boolean hasGroupBy();

	public boolean hasSortBy();

	public boolean hasFixedValue();

	public boolean hasNesSelect();

	public boolean hasUnion();

	public boolean hasEqualsExceptedExpression();
}
