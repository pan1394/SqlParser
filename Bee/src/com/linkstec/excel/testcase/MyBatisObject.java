package com.linkstec.excel.testcase;

import com.linkstec.bee.core.fw.BClass;

public class MyBatisObject {
	private String sqlId;
	private String type;
	private String sql;
	private String namespace;
	private BClass parameterType;
	private BClass resultMap;

	public MyBatisObject() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public BClass getParameterType() {
		return parameterType;
	}

	public void setParameterType(BClass parameterType) {
		this.parameterType = parameterType;
	}

	public BClass getResultMap() {
		return resultMap;
	}

	public void setResultMap(BClass resultMap) {
		this.resultMap = resultMap;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}