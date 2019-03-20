package com.linkstec.bee.core;

import java.util.Hashtable;

public class Dectionery {
	private static Hashtable<String, String> hash = new Hashtable<String, String>();

	static {
		hash.put("equals", "一致判定する");
		hash.put("next", "次の値がある場合に");
		hash.put("execute", "実行する");
		hash.put("substring", "サブ文字列を取得する");
		hash.put("getInt", "整数を取得する");
		hash.put("prepareStatement", "SQL文を用意する");
		hash.put("executeQuery", "検索SQLを実施する");
		hash.put("printStackTrace", "トレースを出力する");
		hash.put("println", "出力");
		hash.put("size", "長さを取得する");
		hash.put("length", "長さを取得する");
		hash.put("list", "リスト");
		hash.put("get", "取得");
		hash.put("set", "設定");
		hash.put("trim", "トリムする");
		hash.put("length", "長さを取得する");
		hash.put("java.lang.Integer", "整数");
		hash.put("java.lang.Dobule", "小数");
		hash.put("java.lang.System", "システム");
		hash.put("java.lang.Math", "計算");
		hash.put("java.lang.String", "文字列");
		hash.put("java.util.ArrayList", "リスト");
		hash.put("java.util.List", "リスト");
		hash.put("java.sql.Connection", "DB接続ハンドラー");
		hash.put("java.lang.Exception", "エラー");
		hash.put("java.sql.PreparedStatement", "SQL作成ツール");
		hash.put("java.sql.ResultSet", "検索結果");
		hash.put("java.io.PrintStream", "出力ストリーム");
		hash.put("startWith", "開始文字列を判定する");
		hash.put("endWith", "終了文字列を判定する");
		hash.put("indexOf", "文字位置を取得する");
		hash.put("toString", "名前を取得する");
		hash.put("array", "配列");
	}

	public static String get(String key) {
		String value = hash.get(key);
		if (value == null) {
			return key;
		}
		return value;
	}

	public static void put(String key, String value) {
		if (value.indexOf(".") > 0) {
			Debug.d();
		}
		hash.put(key, value);
	}
}
