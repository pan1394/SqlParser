package com.linkstec.data;

import java.util.List;
import java.util.Map;

public interface Database {

	public void insert(String schema, String table, List<Map<String, Object>> data);
}
