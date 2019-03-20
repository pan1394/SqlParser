package com.linkstec.excel.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.basic.ITableSql;

public class SQLCaseObject {

	private static final String qItem = "検索項目：";
	private static final String qTable = "検索テーブル：";
	private static final String qJoin = "結合条件：";
	private static final String qCondition = "検索条件：";
	private static final String qOrder = "ソート順：";

	public static SQLCaseObject parse(BSQLSet set, ITableSql tsql) {
		String modelSql = set.getModel().getSQLExp(tsql);
		String mName = set.getMethod().getLogicName();
		String tName = set.getMethod().getName();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		System.out.println("name:" + mName + "   " + tName);
		String currentKey = "";
		if (modelSql.indexOf("\r\n") >= 0) {
			String[] sqls = modelSql.split("\r\n");
			for (String sql : sqls) {
				if (!sql.startsWith("\t")) {
					currentKey = sql.trim();
					if (map.containsKey(currentKey))
						continue;
					else {
						map.put(currentKey, new ArrayList<String>());
					}
				} else {
					map.get(currentKey).add(sql.trim());
				}
			}
		}

		SQLCaseObject obj = new SQLCaseObject();
		obj.methodName = mName;
		obj.tableName = cutBefore(tName, keys);
		obj.tables = parseTables(map);
		obj.items = parseItems(map.get(qItem));
		obj.order = parseOrder(map.get(qOrder));
		obj.originalConditions = parseConditions(map.get(qCondition));
		return obj;
	}

	private static SQLTableObjectList parseTables(Map<String, List<String>> map) {
		List<SQLTableObject> res = new ArrayList<SQLTableObject>();
		List<String> l1 = map.get(qTable);
		for (String s : l1) {
			String[] tmp = s.split(" ");
			res.add(new SQLTableObject(tmp[0].trim(), tmp[1].trim()));
		}
		List<String> l2 = map.get(qJoin);
		if (l2 != null) {
			for (String s : l2) {
				if (s.contains("JOIN")) {
					String[] tmp = s.split(" ");
					res.add(new SQLTableObject(tmp[0].trim(), tmp[1].trim()));
				}
			}
		}
		List<String> l3 = map.get(qCondition);
		if (l3 != null) {
			for (String s : l3) {
				if (s.contains("FROM")) {
					String[] tmp = s.split(" ");
					res.add(new SQLTableObject(tmp[1].trim(), tmp[2].trim()));
				}
			}
		}
		return new SQLTableObjectList(res);
	}

	private static List<String> parseConditions(List<String> list) {
		if (list == null || list.size() == 0)
			return new ArrayList<String>();
		Set<String> c = new LinkedHashSet<String>();
		for (String str : list) {
			String tmp = str.trim();
			if (tmp.contains("=")) {
				int x = -1;
				if ((x = tmp.indexOf("AND")) != -1) {
					tmp = tmp.substring(x + 3);
				}
				c.add(tmp.trim());
			}
		}
		return new ArrayList<String>(c);
	}

	private static String parseOrder(List<String> list) {
		if (list == null || list.size() == 0)
			return "なし";
		int l = list.size();
		StringBuilder order = new StringBuilder();
		if (l == 1) {
			order.append(list.get(0));
		} else {
			System.err.println("==================不止一个orders===============");
			// String[] tmp = list.toArray(new String[] {});
		}
		return order.toString();
	}

	private static List<String> parseItems(List<String> param) {
		Set<String> c = new LinkedHashSet<String>();
		for (String str : param) {
			String[] itKeys = { "AS", "." };
			String tmp = cutAfter(str.trim(), itKeys);

			String[] itKeys2 = { ")" };
			tmp = cutBefore(tmp, itKeys2);
			c.add(tmp.trim());
		}
		List<String> itms = new ArrayList<String>(c);
		return itms;
	}

	private static String cutBefore(String string, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			String k = keys[i];
			if (string.contains(k)) {
				int idx = string.lastIndexOf(k);
				return string.substring(0, idx);
			}
		}
		return string;
	}

	private static String cutAfter(String string, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			String k = keys[i];
			if (string.contains(k)) {
				int idx = string.lastIndexOf(k);
				return string.substring(idx + k.length());
			}
		}
		return string;
	}

	private String methodName;

	private String tableName;
	// query items
	private List<String> items;

	private String order;

	private List<String> originalConditions;

	private List<String> processedConditions;

	private List<String[]> counts;

	private SQLTableObjectList tables;

	public List<String[]> fetchCounts() {
		counts = new ArrayList<String[]>();
		String f1 = "%s取得%s件の場合";
		String f2 = "%s取得%s件";
		int i = 1;
		while (i < 4) {
			String v = String.valueOf(i);
			String[] tmp = new String[2];
			if (i == 3)
				v = "複数";
			tmp[0] = String.format(f1, this.tableName, v);
			tmp[1] = String.format(f2, this.tableName, v);
			counts.add(tmp);
			i++;
		}
		return counts;
	}

	private static String[] keys = { "からデータ", "を", "取得" };

	public String getTableName() {
		return this.tableName;
	}

	public List<String> fetchItems() {
		return this.items;
	}

	public String fetchOrders() {
		return this.order;
	}

	public List<String[]> fetchConditions() {
		List<String> itms = this.originalConditions.stream()
				.filter(o -> !SQLCaseObject.isJointCondition(o, this.tables))
				.filter(o -> !SQLCaseObject.isRightConstant(o)).collect(Collectors.toList());
		return analysis(itms);
	}

	public String fetchMethodName() {
		return this.methodName;
	}

	private static String join(String[] array, String c) {
		StringBuilder res = new StringBuilder();
		res.append(array[0]);
		for (int i = 1; i < array.length; i++) {
			res.append(c).append(array[i]);
			// result = result + "-" + array[i];
		}
		return res.toString();
	}

	/**
	 * 判断等式左右是否都有表别名(表连接查询条件，不作为检索条件项)。
	 * 
	 * @param expression
	 */
	private static boolean isJointCondition(String expression, SQLTableObjectList list) {
		String[] parts = expression.split("=");
		boolean flag = true;
		for (int i = 0; i < parts.length; i++) {
			String string = parts[i].trim();
			if (!string.contains("."))
				return false;
			int x = string.indexOf(".");
			String alias = string.substring(0, x);
			if (!list.hasTable(alias))
				return false;
		}
		return flag;
	}

	private static boolean isRightConstant(String expression) {
		String[] parts = expression.split("=");
		String right = parts[1];
		if (right.contains("NULL") || right.contains("\"\"") || right.indexOf("\"") != right.lastIndexOf("\"")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param originals
	 * @return
	 */
	private List<String[]> analysis(List<String> originals) {
		if (originals == null)
			return new ArrayList<>();
		int size = originals.size();
		List<String[]> res = new ArrayList<>();
		String pass = String.format("「%s」からデータを抽出する", this.tableName);
		String failed = String.format("「%s」からデータを抽出しない", this.tableName);
		if (size == 1) {
			String[] item = new String[2];
			String tmp = originals.get(0);
			item[0] = tmp;
			item[1] = pass;
			res.add(item);

			tmp = tmp.replace("=", "≠");
			item = new String[2];
			item[0] = tmp;
			item[1] = failed;
			res.add(item);
		} else {
			for (int i = 0; i <= size; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < size; j++) {
					String t = originals.get(j);
					if (i != 0 && j == (i - 1)) {
						t = t.replace("=", "≠");
					}
					sb.append(t + ExcelUtils.getReturnStr());
				}
				String[] item = new String[2];
				item[0] = sb.toString();
				if (i == 0) {
					item[1] = pass;
				} else {
					item[1] = failed;
				}
				res.add(item);
			}
		}
		return res;
	}
}
