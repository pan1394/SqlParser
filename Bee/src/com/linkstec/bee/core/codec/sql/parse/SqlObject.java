package com.linkstec.bee.core.codec.sql.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.linkstec.bee.core.codec.sql.sql.SqlCondition;
import com.linkstec.bee.core.codec.sql.sql.SqlExpression;
import com.linkstec.bee.core.codec.sql.sql.SqlField;
import com.linkstec.bee.core.codec.sql.sql.SqlNode;
import com.linkstec.bee.core.codec.sql.sql.SqlTable;

public class SqlObject {

	private static final String qItem = "【抽出項目】";
	private static final String qTable = "【対象テーブル】";
	private static final String qJoin = "【結合条件】";
	private static final String qCondition = "【抽出条件】";
	private static final String qOrder = "【ソート条件】";

	private static List<String> keys = Arrays.asList(qItem, qTable, qJoin, qCondition, qOrder);

	private static List<String> keys2 = Arrays.asList("SELECT", "FROM", "WHERE");

	private static Map<String, String> mapping = new HashMap<>();
	static {
		mapping.put("SELECT", qItem);
		mapping.put("FROM", qTable);
		mapping.put("WHERE", qCondition);
		mapping.put("", "");
	}

	public static SqlObject parseSubQueries(List<String> obj) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String currentKey = "header";
		map.put(currentKey, new ArrayList<>());
		for (String modelSql : obj) {
			String tmp = modelSql.trim();
			if (tmp.length() == 0)
				continue;
			String key1 = contains(keys2, tmp);
			String mykey = mapping.get(key1);
			if (mykey.length() > 0) {
				currentKey = mykey;
				if (map.containsKey(currentKey))
					continue;
				else {
					map.put(currentKey, new ArrayList<String>());
					String cut = tmp.substring(key1.length());
					if (cut.contains(",")) {
						map.get(currentKey).addAll(Arrays.asList(cut.split(",")));
					} else {
						map.get(currentKey).add(cut.trim());
					}
				}
			} else {
				map.get(currentKey).add(tmp);
			}
		}

		SqlObject res = new SqlObject();
		res.tables = parseTables(map);
		res.items = parseItems(res.tables, map.get(qItem));
		res.order = parseOrder(map.get(qOrder));
		parseConditions(res, map.get(qCondition));
		return res;
	}

	public static SqlObject parse(List<String> sqlQuery) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String currentKey = "header";
		map.put(currentKey, new ArrayList<>());
		for (String modelSql : sqlQuery) {
			String tmp = modelSql.trim();
			if (tmp.length() == 0)
				continue;

			String mykey = contains(keys, tmp);

			if (mykey.length() > 0) {
				currentKey = mykey;
				if (map.containsKey(currentKey))
					continue;
				else {
					map.put(currentKey, new ArrayList<String>());
				}
			} else {
				map.get(currentKey).add(tmp);
			}
		}
		SqlObject res = new SqlObject();
		res.tables = parseTables(map);
		res.items = parseItems(res.tables, map.get(qItem));
		res.order = parseOrder(map.get(qOrder));
		parseConditions(res, map.get(qCondition));
		return res;
	}

	private static void parseConditions(SqlObject res, List<String> list) {
		List<SqlTable> tables = res.tables;
		SqlCondition previous = new SqlCondition();
		previous.setExpression(new SqlNode(""));
		SqlCondition root = previous;
		Map<String, String> existsFlag = new HashMap<>();
		List<String> subQueries = new ArrayList<>();
		if (list == null) {
			res.condition = previous;
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (s.contains("=")) {
				String[] tmp = s.split("(=)");
				String left = tmp[0];
				String right = tmp[1];
				SqlField leftNode = new SqlField();
				SqlField rightNode = new SqlField();
				if (left.contains(".")) {
					String[] pair = left.split("\\.");
					leftNode.setOwner(get(tables, pair[0].trim(), null));
					leftNode.setColumnName(new SqlNode(pair[1].trim()));
				} else if (left.contains("��")) {
					String[] pair = left.split("��");
					leftNode.setOwner(get(tables, pair[0].trim(), null));
					leftNode.setColumnName(new SqlNode(pair[1].trim()));
				} else {
					leftNode.setColumnName(new SqlNode(left.trim()));
				}

				if (right.contains(".")) {
					String[] pair = right.split("\\.");
					rightNode.setOwner(get(tables, pair[0].trim(), null));
					rightNode.setColumnName(new SqlNode(pair[1].trim()));
				} else if (right.contains("��")) {
					String[] pair = right.split("��");
					rightNode.setOwner(get(tables, pair[0].trim(), null));
					rightNode.setColumnName(new SqlNode(pair[1].trim()));
				} else {
					rightNode.setColumnName(new SqlNode(right.trim()));
				}

				SqlCondition current = new SqlCondition();
				SqlNode exp = new SqlExpression(leftNode, new SqlNode("="), rightNode);
				current.setExpression(exp);
				previous.setNext(current);
				previous = current;

			} else {
				SqlCondition current = new SqlCondition();
				current.setExpression(new SqlNode(s));
				previous.setNext(current);
				previous = current;
			}

			if (s.contains("EXISTS")) {
				existsFlag.put("exists", "true");
			}
			if ((s.contains("(") || s.contains("��")) && existsFlag.get("exists") != null) {
				existsFlag.put("exists", "start");
			} else if ("start".equals(existsFlag.get("exists")) && !s.contains(")") && !s.contains("��")) {
				subQueries.add(s.trim());
			} else if ("start".equals(existsFlag.get("exists")) && (s.contains(")") || s.contains("��"))) {
				existsFlag.put("exists", "end");
			}

		}
		if ("end".equals(existsFlag.get("exists"))) {
			String key = "exists";
			if (res.subqueries.get(key) == null) {
				res.subqueries.put("exists", parseSubQueries(subQueries));
			} else {
				res.subqueries.put(key + res.no++, parseSubQueries(subQueries));
			}
		}
		res.condition = root;
		return;
	}

	private SqlCondition condition;

	private List<SqlTable> tables;

	private List<SqlField> items;

	private List<SqlNode> order;

	private Map<String, SqlObject> subqueries = new HashMap<String, SqlObject>();

	private int no = 0;

	private static String contains(List<String> keys, String aStr) {
		for (String key : keys) {
			if (aStr.contains(key))
				return key;
		}
		return "";
	}

	private static List<SqlTable> parseTables(Map<String, List<String>> map) {
		List<SqlTable> res = new ArrayList<SqlTable>();
		List<String> l1 = map.get(qTable);
		if (l1 != null) {
			for (String s : l1) {
				if (s.contains("AS")) {
					String[] tmp = s.split("AS");
					res.add(new SqlTable(tmp[0].trim(), tmp[1].trim()));
				}
				res.add(new SqlTable(s));
			}
		}

		List<String> l2 = map.get(qJoin);
		if (l2 != null) {
			for (String s : l2) {
				if (s.equals("�ʤ�"))
					break;
				if (s.contains("."))
					continue;
				if (s.contains("AS")) {
					String[] tmp = s.split("AS");
					res.add(new SqlTable(tmp[0].trim(), tmp[1].trim()));
				}
				res.add(new SqlTable(s));
			}
		}
		List<String> l3 = map.get(qCondition);
		if (l3 != null) {
			for (String s : l3) {
				if (s.contains("FROM")) {
					String x = s.substring(s.indexOf("FROM") + 4);
					if (x.contains("AS")) {
						String[] tmp = s.split("AS");
						res.add(new SqlTable(tmp[0].trim(), tmp[1].trim()));
					}
					res.add(new SqlTable(x));
				}
			}
		}
		return res;
	}

	private static SqlTable get(List<SqlTable> lst, String tableName, String alias) {
		if (tableName != null && alias == null) {
			Optional<SqlTable> o = lst.stream().filter(t -> t.getTableName().equals(tableName)).findAny();
			return o.orElseGet(() -> new SqlTable(""));
		} else if (tableName == null && alias != null) {
			Optional<SqlTable> o = lst.stream().filter(t -> t.getAlias().equals(alias)).findAny();
			return o.orElseGet(() -> new SqlTable(""));
		}
		return new SqlTable("");
	}

	private static List<SqlNode> parseOrder(List<String> list) {
		List<SqlNode> nodes = new ArrayList<SqlNode>();
		if (list == null || list.size() == 0) {
			nodes.add(new SqlNode("なし"));
			return nodes;
		}

		int l = list.size();
		if (l == 1) {
			String tmp = (list.get(0));
			int idx = -1;
			if (tmp.contains("ORDER BY")) {
				idx = tmp.indexOf("ORDER BY");
				tmp = tmp.substring(idx + 8);
				nodes.add(new SqlNode(tmp.trim()));
			} else {
				nodes.add(new SqlNode(tmp.trim()));
			}
		} else {
			System.err.println("==================not only one order===============");
		}
		return nodes;
	}

	private static List<SqlField> parseItems(List<SqlTable> tables, List<String> param) {
		if (param == null)
			return new ArrayList<>();
		Set<SqlField> c = new LinkedHashSet<SqlField>();
		for (String str : param) {

			String tmp = str;
			int idx = -1;
			String alias = "";
			String owner = "";
			String field = "";
			if (str.contains("AS")) {
				idx = str.indexOf("AS");
				alias = str.substring(idx + 2);
				tmp = str.substring(0, idx);
			}

			String[] pair = split(str);
			owner = pair[0].trim();
			field = pair[1].trim();

			c.add(new SqlField(get(tables, owner, null), new SqlNode(field), alias));
		}
		List<SqlField> itms = new ArrayList<SqlField>(c);
		return itms;
	}

	private static String[] split(String str) {
		String[] pair = new String[2];
		String tmp = str;
		if (str.contains("なし")) {
			pair = tmp.split("なし");
		}
		if (str.contains(".")) {
			pair = tmp.split("\\.");
		} else {
			pair[0] = "";
			pair[1] = tmp.trim();
			;
		}
		return pair;
	}

	public List<SqlTable> getTables() {
		return this.tables;
	}

	public List<SqlField> getItems() {
		return this.items;
	}

	public SqlCondition getCondition() {
		return this.condition;
	}

	public List<SqlNode> getOrder() {
		return this.order;
	}
}
