package com.linkstec.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.SqlField;
import com.linkstec.sql.SqlGroup;
import com.linkstec.sql.SqlJoinNode;
import com.linkstec.sql.SqlNode;
import com.linkstec.sql.SqlOrder;
import com.linkstec.sql.SqlTable;
import com.linkstec.sql.SqlWhereNode;
import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.JoinRawData;
import com.linkstec.utils.SqlUtilities;

public class SqlObject extends SqlNode{

	private static final String qItem = "【抽出項目】";
	private static final String qTable = "【対象テーブル】";
	private static final String jTable = "【結合テーブル】";
	private static final String qJoin = "【結合条件】";
	private static final String qCondition = "【抽出条件】";
	private static final String qOrder = "【ソート条件】";
	private static final String qGroup = "【集計条件】";
	
	private static List<String> keys = Arrays.asList(qItem, qTable, qJoin, qCondition, qOrder,jTable, qGroup); 

	private SqlWhereNode condition;
	
	private List<SqlJoinNode> join;
	
	private SqlTable table;
	
	private List<SqlField> items;
	
	private SqlOrder order;

	private SqlGroup group;
	
	public static SqlObject parse(List<String> sqlQuery) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String currentKey = "header";
		map.put(currentKey, new ArrayList<>());
		for(String modelSql : sqlQuery) {
			String mykey = SqlUtilities.startWith(keys, modelSql);
			String processedLine = SqlUtilities.tab2Space(modelSql);
			if(StringUtils.isEmpty(processedLine)) continue;
			if (mykey.length() > 0) {
				currentKey = mykey;
				if (!map.containsKey(currentKey)) {
					map.put(currentKey, new ArrayList<String>());
				} 
			} else {
				map.get(currentKey).add(modelSql);
			} 
		}
		SqlObject res = new SqlObject(); 
 		res.table = parseTable(map);
		res.items = parseItems(map.get(qItem));
		res.order = parseOrder(map.get(qOrder));
		res.join = parseJCondtions(map.get(qJoin));
		res.condition = parseConditions(map.get(qCondition));
		res.group = parseGroup(map.get(qGroup));
		return res;
	}
	
	private static SqlWhereNode parseConditions(List<String> list) {
		SqlWhereNode whereNode = new SqlWhereNode();
		if (list != null) {
			for (String s : list) {
				if(s.equals(SqlConstants.RAW_STRING_NULL))  break;
				whereNode.setRawString(s);
			}
		}
		return whereNode;
	}

	private static List<SqlJoinNode> parseJCondtions(List<String> list) {
 		List<SqlJoinNode> all = new ArrayList<>();
 		List<JoinRawData> raws = new ArrayList<>();
 		JoinRawData raw = null;
 		boolean flag = false;
 		if(list == null) return all;
 		String first = list.get(0).trim();
 		if(SqlUtilities.contains(SqlConstants.REGEX_START_PARENT, first) && first.length() == 1){
 			for(String tmp : list) {
 	 			String line = tmp.trim();
 	 			if(line.length() == 1 && SqlUtilities.contains(SqlConstants.REGEX_START_PARENT, line)) {  //"(" line
 	 				flag = false;    
 	 				raw = new JoinRawData();
 	 				raws.add(raw);
 	 			} 
 	 			
 	 			if(!flag) {
 	 				raw.addTableString(line);
 	 			}else {
 	 				raw.addConditionString(line);
 	 			}
 	 			
 	 			if(SqlUtilities.contains(SqlConstants.CONDITON_JOIN_SYBOL, line)) {   // includes JOIN statment
 	 				int idx = -1;          // decide if first char is ")"
 	 				if(SqlUtilities.contains(SqlConstants.REGEX_END_PARENT, line)) {
 	 					String startChar = SqlUtilities.fetch(SqlConstants.REGEX_END_PARENT, line);
 	 					idx = line.indexOf(startChar);
 	 					if(idx == 0) {
 	 						flag = true;  // flag switched.
 	 					}
 	 				}
 	 				if(idx !=0 ) {
 	 					flag = true; 
 	 					raw.removeConditionString(line);
 	 	 				raw = new JoinRawData();
 	 	 				raws.add(raw);
 	 	 				raw.addTableString(line);
 	 				}
 	 			}  
 	 		}
 		}else {
 			SqlJoinNode joinNode = new SqlJoinNode();
			for (String s : list) {
				if(s.equals(SqlConstants.RAW_STRING_NULL))  break;
				joinNode.setRawString(s);
			}
			all.add(joinNode);
 		}
 		for(JoinRawData rd : raws) {
 			SqlJoinNode joinNode = new SqlJoinNode();
 			joinNode.convert(rd);
 			all.add(joinNode);
 		} 
		return all;
	} 
	
	private static SqlTable parseTable(Map<String, List<String>> map) {
		List<String> l1 = map.get(qTable);
		if(l1 != null) {
			if(l1.size() == 1) {
				SqlTable tbl = new SqlTable();
				tbl.setRawString(l1.get(0)); 
				return tbl;
			}else {
				String first = l1.get(0);
				String last = l1.get(l1.size() - 1);
				if(SqlUtilities.contains(SqlConstants.REGEX_START_PARENT, first) && SqlUtilities.contains(SqlConstants.REGEX_END_PARENT, last)) {
					List<String> subs = l1.subList(1, l1.size() -1 );
					List<String> p = new ArrayList<>();
					subs.forEach(o -> p.add(SqlUtilities.leftTrimBlank(o)));
					SqlTable tbl = new SqlTable();
					tbl.setTable(SqlObject.parse(p));
					String endChar = SqlUtilities.fetch(SqlConstants.REGEX_END_PARENT, last);
					int start = last.indexOf(endChar);
					if(start > 0) {
						String alias = StringUtils.trimToEmpty(last.substring(start+1));
						if(StringUtils.isNotEmpty(alias)) {
							tbl.setAlias(alias);
						}
					}
					return tbl;
				}
			}
			 
		}
		return null;
	}
	 
	private static SqlOrder parseOrder(List<String> list) {
		SqlOrder order = new SqlOrder();
		if(list != null)
			list.forEach(order::setRawString);
		return order;
	}

	private static SqlGroup parseGroup(List<String> list) {
		SqlGroup gp = new SqlGroup();
		if(list != null)
			list.forEach(gp::setRawString);
		return gp;
	}
	
	private static List<SqlField> parseItems(List<String> param) {
		if(param == null) return new ArrayList<>();
		Set<SqlField> c = new LinkedHashSet<SqlField>();
		for (String raw : param) {
			 SqlField field = new SqlField();
			 field.setRawString(raw);
			 c.add(field);
		}
		List<SqlField> itms = new ArrayList<SqlField>(c);
		return itms;
	}
 
	public SqlTable getTable() {
		return this.table;
	}
	
	public List<SqlField> getItems(){
		return this.items;
	}
	
	public SqlWhereNode	getCondition() {
		return this.condition;
	}
	
	public SqlOrder getOrder() {
		return this.order;
	}

	public List<SqlJoinNode> getJoin() {
		return join;
	}

	public SqlGroup getGroup() {
		return group;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Table: \n" + this.getTable() + "\n");
		sb.append("Items: \n" );
		List<SqlField> fs = this.getItems();
		for(SqlField f : fs) {
			sb.append(f.toString()+"\n");
			if(!f.isSimpleField()) {
				sb.append("******************\n");
				sb.append("此节点存在子项，如下： \n");
				sb.append(f.getSubFields() + "\n");
				sb.append("******************\n");
			}
		}
		if(!this.getJoin().isEmpty()) {
			sb.append("Join conditions: \n" + this.getJoin() + "\n");
		}
		if(!this.getCondition().isEmpty()) {
			sb.append("Query: \n" + this.getCondition()+"\n");
		}
		if(!this.getGroup().isEmpty()) {
			sb.append("Group: \n" + this.getGroup() +"\n");
		}
		if(!this.getOrder().isEmpty()) {
			sb.append("Order:" + this.getOrder()+"\n");
		}
		return sb.toString();
	}
}
