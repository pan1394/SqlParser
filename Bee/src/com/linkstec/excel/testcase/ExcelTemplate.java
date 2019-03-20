package com.linkstec.excel.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTemplate {

	public static final String TEMPLATE_NAME = "テストケース";

	public static final String NULL_STRING = "なし";

	public static class SQLTemplate {
		public static final String LITTLE_CATEGORY_CONDITION = "SQL条件確認";
		public static final String LITTLE_CATEGORY_ITEMS = "取得項目確認";
		public static final String LITTLE_CATEGORY_NUMBER = "取得件数確認";
		public static final String LITTLE_CATEGORY_ORDER = "ソート順確認";
	}

	public static class JCLService {

		private static Map<String, String> map = new HashMap<String, String>();

		public static final String BBPOS010 = "BBPOS010";
		public static final String BBPRC020 = "BBPRC020";
		public static final String BBPRC010 = "BBPRC010";
		public static final String BBDLV010 = "BBDLV010";

		private static List<String> keys = new ArrayList<String>();

		static {
			map.put(BBPOS010, "pos");
			map.put(BBPRC020, "prc");
			map.put(BBPRC010, "cnt");
			map.put(BBDLV010, "mtl");

			keys.add(BBPOS010);
			keys.add(BBPRC020);
			keys.add(BBPRC010);
			keys.add(BBDLV010);
		}

		public static String get(String key) {
			return map.get(key);
		}

		public static List<String> list() {
			return keys;
		}
	}

	public static void main(String[] args) {
		System.out.println(ExcelTemplate.JCLService.get(ExcelTemplate.JCLService.BBDLV010));
	}
}
