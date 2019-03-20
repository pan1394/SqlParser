package com.linkstec.excel.testcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class demo {
	public static void main(String[] args) {

		List<SqlObject> sqlObjects = new ArrayList<SqlObject>();
		SqlObject sqlObject = new SqlObject();
		List<SqlUnit> units = new ArrayList<SqlUnit>();
		SqlUnit unit = new SqlUnit();
		// select
		List<SqlRow> rows = new ArrayList<SqlRow>();
		SqlRow row = new SqlRow();
		List<SqlNode> nodes = new ArrayList<SqlNode>();
		SqlNode node = new SqlNode();
		HashMap<String, Object> attr = new HashMap<String, Object>();
		attr.put("logicName", "全項目");
		attr.put("name", "");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setFields(rows);
		// table
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "");
		attr.put("name", "現物契約_ポジション基準数量付き");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setTables(rows);
		// where
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "gembutsu_keiyaku_no");
		attr.put("name", " 契約数量範囲.現物契約No");
		attr.put("type", "Field");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", "=");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "#{gembutsu_keiyaku_no}");
		attr.put("name", "取得された一件の現物契約_ポジション基準数量付きの現物契約No");
		attr.put("type", "Field");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setWhere(rows);
		// group by
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "negime_suryou_hani_no");
		attr.put("name", "契約数量範囲.値決数量範囲No");
		attr.put("type", "Field");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "ASC");
		attr.put("name", "昇順");
		attr.put("type", "Field");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setGroup(rows);
		// order by
		rows = new ArrayList<SqlRow>();
		unit.setOrder(rows);

		units.add(unit);
		sqlObject.setName("「現物契約_ポジション基準数量付き」からデータを抽出する。");
		sqlObject.setUnits(units);
		sqlObjects.add(sqlObject);

		// 第二段sql
		sqlObject = new SqlObject();
		units = new ArrayList<SqlUnit>();

		// unit1
		unit = new SqlUnit();
		// select
		rows = new ArrayList<SqlRow>();
		// field1
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "#{risk_haaku_tani_cd} ");
		attr.put("name", "引数のリスク把握単位Cd");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "riskHaakuTaniCd");
		attr.put("name", "リスク把握単位Cd");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field2
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "gembutsu_keiyaku_no");
		attr.put("name", "その他単価_全体．現物契約No");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "gembutsuKeiyakuNo");
		attr.put("name", "");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field3
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "'0'");
		attr.put("name", "0");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "negimeSuryouHaniNo");
		attr.put("name", "値決数量範囲No");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field4
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "'1'");
		attr.put("name", "'1'");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "");
		attr.put("name", "その他単価区分");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		unit.setFields(rows);
		// table
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "t_prc0060");
		attr.put("name", "その他単価_全体");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setTables(rows);
		// where
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", "EXISTS");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", "（");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();

		SqlObject subSqlObject = new SqlObject();
		List<SqlUnit> subUnits = new ArrayList<SqlUnit>();
		SqlUnit subUnit = new SqlUnit();

		// select
		List<SqlRow> subrows = new ArrayList<SqlRow>();
		SqlRow subrow = new SqlRow();
		List<SqlNode> subnodes = new ArrayList<SqlNode>();
		SqlNode subnode = new SqlNode();
		HashMap<String, Object> subattr = new HashMap<String, Object>();
		subattr.put("logicName", "risk_haaku_tani_cd");
		subattr.put("name", "リスク把握単位Cd");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);

		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "gembutsu_keiyaku_no");
		subattr.put("name", "現物契約番号");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setFields(subrows);
		// table
		subrows = new ArrayList<SqlRow>();
		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "");
		subattr.put("name", "現物契約_ポジション基準数量付き");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setTables(subrows);
		// where
		subrows = new ArrayList<SqlRow>();
		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "risk_haaku_tani_cd");
		subattr.put("name", " 現物契約_ポジション基準数量付き.リスク把握単位Cd");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "=");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "#{risk_haaku_tani_cd}");
		subattr.put("name", "引数のリスク把握単位Cd");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);

		subrow = new SqlRow();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "AND");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "gembutsu_keiyaku_no");
		subattr.put("name", " 現物契約_ポジション基準数量付き.現物契約番号");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "=");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "#{gembutsu_keiyaku_no}");
		subattr.put("name", "その他単価_全体.現物契約番号");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setWhere(subrows);
		// group by
		subrows = new ArrayList<SqlRow>();
		subUnit.setGroup(subrows);
		// order by
		subrows = new ArrayList<SqlRow>();
		subUnit.setOrder(subrows);

		subUnits.add(subUnit);
		subSqlObject.setUnits(subUnits);
		attr.put("subsql", subSqlObject);
		attr.put("type", "subsql");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", ")");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		unit.setWhere(rows);
		// group by
		rows = new ArrayList<SqlRow>();
		unit.setGroup(rows);
		// order by
		rows = new ArrayList<SqlRow>();
		unit.setOrder(rows);

		units.add(unit);

		// unit2
		unit = new SqlUnit();
		// select
		rows = new ArrayList<SqlRow>();
		// field1
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "#{risk_haaku_tani_cd} ");
		attr.put("name", "引数のリスク把握単位Cd");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "riskHaakuTaniCd");
		attr.put("name", "リスク把握単位Cd");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field2
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "gembutsu_keiyaku_no");
		attr.put("name", "その他単価_全体．現物契約No");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "gembutsuKeiyakuNo");
		attr.put("name", "");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field3
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "'0'");
		attr.put("name", "0");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "negimeSuryouHaniNo");
		attr.put("name", "値決数量範囲No");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		// field4
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "'1'");
		attr.put("name", "'1'");
		node.setAttr(attr);
		nodes.add(node);
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "");
		attr.put("name", "その他単価区分");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		unit.setFields(rows);
		// table
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("logicName", "t_prc0060");
		attr.put("name", "その他単価_全体");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);
		unit.setTables(rows);
		// where
		rows = new ArrayList<SqlRow>();
		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", "EXISTS");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", "（");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();

		subSqlObject = new SqlObject();
		subUnits = new ArrayList<SqlUnit>();
		subUnit = new SqlUnit();

		// select
		subrows = new ArrayList<SqlRow>();
		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "risk_haaku_tani_cd");
		subattr.put("name", "リスク把握単位Cd");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);

		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "gembutsu_keiyaku_no");
		subattr.put("name", "現物契約番号");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setFields(subrows);
		// table
		subrows = new ArrayList<SqlRow>();
		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "");
		subattr.put("name", "現物契約_ポジション基準数量付き");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setTables(subrows);
		// where
		subrows = new ArrayList<SqlRow>();
		subrow = new SqlRow();
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "risk_haaku_tani_cd");
		subattr.put("name", " 現物契約_ポジション基準数量付き.リスク把握単位Cd");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "=");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "#{risk_haaku_tani_cd}");
		subattr.put("name", "引数のリスク把握単位Cd");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);

		subrow = new SqlRow();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "AND");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subnodes = new ArrayList<SqlNode>();
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "gembutsu_keiyaku_no");
		subattr.put("name", " 現物契約_ポジション基準数量付き.現物契約番号");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("value", "=");
		subattr.put("type", "keyword");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subnode = new SqlNode();
		subattr = new HashMap<String, Object>();
		subattr.put("logicName", "#{gembutsu_keiyaku_no}");
		subattr.put("name", "その他単価_全体.現物契約番号");
		subattr.put("type", "Field");
		subnode.setAttr(subattr);
		subnodes.add(subnode);
		subrow.setNodes(subnodes);
		subrows.add(subrow);
		subUnit.setWhere(subrows);
		// group by
		subrows = new ArrayList<SqlRow>();
		subUnit.setGroup(subrows);
		// order by
		subrows = new ArrayList<SqlRow>();
		subUnit.setOrder(subrows);

		subUnits.add(subUnit);
		subSqlObject.setUnits(subUnits);
		attr.put("subsql", subSqlObject);
		attr.put("type", "subsql");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		row = new SqlRow();
		nodes = new ArrayList<SqlNode>();
		node = new SqlNode();
		attr = new HashMap<String, Object>();
		attr.put("value", ")");
		attr.put("type", "keyword");
		node.setAttr(attr);
		nodes.add(node);
		row.setNodes(nodes);
		rows.add(row);

		unit.setWhere(rows);
		// group by
		rows = new ArrayList<SqlRow>();
		unit.setGroup(rows);
		// order by
		rows = new ArrayList<SqlRow>();
		unit.setOrder(rows);
		units.add(unit);
		sqlObject.setName("その他単価_全体、その他単価_値決数量範囲、その他単価_比率の抽出");
		sqlObject.setUnits(units);
		sqlObjects.add(sqlObject);

		Workbook wb = null;
		FileInputStream in = null;
		File file = new File("E:\\beeAPI\\Ver1.0.4\\exel_template\\new.xlsx");
		try {
			in = new FileInputStream(file);
			if (file.getName().endsWith("xls")) {
				wb = new HSSFWorkbook(in);
			} else if (file.getName().endsWith("xlsx")) {
				wb = new XSSFWorkbook(in);
			}
			int tempIndex = wb.getSheetIndex("SQL仕様書(BBPOS1530SqlClient)");
			Sheet sheet = wb.cloneSheet(tempIndex);
			ExcelSql excelSql = new ExcelSql(sheet, sqlObjects);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
