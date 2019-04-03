package com.linkstec.sql;

import java.util.List;

public class SqlOrder extends SqlNode{
  

	private List<SqlField> items;
	
	public List<SqlField> getItems() {
		return items;
	}
	
	
	@Override
	protected void convert() {
		super.convert();
//		if (list == null || list.size() == 0) {
//			nodes.add(new SqlNode("なし"));
//			return nodes;
//		}
//		
//		int l = list.size();
//		if (l == 1) {
//			String tmp = (list.get(0));
//			int idx = -1;
//			if(tmp.contains("ORDER BY")) {
//				idx = tmp.indexOf("ORDER BY");
//				tmp = tmp.substring(idx + 8);
//				nodes.add(new SqlNode(tmp.trim()));
//			}else {
//				nodes.add(new SqlNode(tmp.trim()));
//			}
//		} else {
//			System.err.println("==================not only one order===============");
//		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(items.isEmpty()) {
			return "";
		}
		items.forEach(i -> sb.append(i + ","));
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
