package com.linkstec.excel.testcase;

import java.util.List;

public class SQLTableObjectList {

	private List<SQLTableObject> lst;

	public SQLTableObjectList(List<SQLTableObject> list) {
		this.lst = list;
	}

	/**
	 * 检索表中是否存在别名为alias的表
	 * 
	 * @param alias
	 * @return
	 */
	public boolean hasTable(String alias) {
		return lst.stream().anyMatch(o -> o.hasAlias(alias));
	}

}
