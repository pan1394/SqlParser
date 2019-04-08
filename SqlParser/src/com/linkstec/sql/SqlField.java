package com.linkstec.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.linkstec.sql.constants.SqlConstants;
import com.linkstec.utils.SqlFactory;
import com.linkstec.utils.SqlUtilities;

public class SqlField extends SqlNode {

	private static String[] keys = { "+", "-", "*", "/", "×", "／", "（", "）" };

	private static String[] functions = { "AVG", "SUM", "COUNT", "MAX", "CASE WHEN", "SELECT" };


	private static String[] KEYS_FOR_CASE_WHEN = { "CASE WHEN", "CASE", "WHEN", "THEN", "ELSE", "END" };

	private SqlNode owner;

	private SqlNode columnName;

	private String alias;
 
	private boolean isExp;

	private String expression;

	private List<SqlField> subs = new ArrayList<SqlField>();

	public SqlField() {

	}

	public SqlField(SqlNode owner, SqlNode columnName, String alias) {
		this.owner = owner;
		this.columnName = columnName;
		this.alias = alias;
	}

	public boolean isSimpleField() {
		return !this.isExp;
	}

	public List<SqlField> getSubFields() {
		return this.subs;
	}

	public SqlNode getOwner() {
		return owner;
	}

	public void setOwner(SqlTable owner) {
		this.owner = owner;
	}

	public SqlNode getColumnName() {
		return columnName;
	}

	public void setColumnName(SqlNode columnName) {
		this.columnName = columnName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	protected void convert() {
		super.convert();
		this.splitLevel1();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (this.isSimpleField()) {
			if (this.owner != null && this.owner.toString().length() > 0) {
				str.append(this.owner + ".");
			}
			str.append(this.columnName);
			if (this.alias != null && this.alias.trim().length() > 0) {
				str.append(" AS " + this.alias);
			}
		} else {
			str.append(this.expression);
		}
		return String.format(str.toString());
	}

	

	private void splitLevel1() {
		if (!contains(functions, this.rawString)) { // no functions
			if (this.rawString.contains(SqlConstants.REGEX_SPLIT_CHAR_AS)) { // has alias
				String[] main = SqlUtilities.crop(this.rawString, SqlConstants.REGEX_SPLIT_CHAR_AS);
				if(main[0].contains(SqlConstants.REGEX_SPLIT_CHAR_DOT)) {
					String[] parts = SqlUtilities.crop(main[0], SqlConstants.REGEX_SPLIT_CHAR_DOT);
					this.owner = new SqlNode(parts[0]);
					this.columnName = new SqlNode(parts[1]);
				}else {
					this.owner = new SqlNode("");
					this.columnName = new SqlNode(main[0]);
				}
				this.alias = StringUtils.trimToEmpty(main[1]);
			} else if (this.rawString.contains(SqlConstants.REGEX_SPLIT_CHAR_DOT)) { // no alias
				String[] parts = SqlUtilities.crop(this.rawString, SqlConstants.REGEX_SPLIT_CHAR_DOT);
				this.owner = new SqlNode(parts[0]);
				this.columnName = new SqlNode(parts[1]);
				this.alias = "";
			} else {
				this.owner = new SqlNode("");
				this.columnName = new SqlNode(this.rawString);
				this.alias = "";
			}
			this.isExp = false;
		} else { // has functions
			String expression = this.rawString;
			if (this.rawString.contains(SqlConstants.REGEX_SPLIT_CHAR_AS)) { // has alias
				String[] main = SqlUtilities.crop(this.rawString, SqlConstants.REGEX_SPLIT_CHAR_AS);
				this.alias = StringUtils.trimToEmpty(main[1]);
				expression = main[0];
			}
			this.isExp = true;
			this.expression = expression;
			splitLevel2(expression);

		}
	}

 
	
	
	private void splitLevel2(String expression) {
		//System.out.println(String.format("processed exression: %s", expression));
		// 使用+-*/, SUM,AVG,COUNT, CASE WHEN
		Pattern p3 = Pattern.compile("(\\S+)[\\.．](\\s*\\S+)(?<![\\(\\)])");
		String[] x = null;
		if (contains(keys, expression) || contains(functions, expression)) {
			
			if (expression.contains("CASE WHEN")) {
				expression = SqlUtilities.replace(expression, KEYS_FOR_CASE_WHEN);
				x = expression.split("[ =]");
			} else {
				expression = SqlUtilities.replace(expression, functions);
				x = expression.split("[+\\-\\*\\/×／（）\\(\\)]");
			}
			for (String a : x) {
				Matcher m = p3.matcher(a);
				if (m.find()) { 
					String s1 = m.group(1);
					String s2 = m.group(2);
					subs.add(new SqlField(new SqlNode(s1), new SqlNode(s2), null));
				}
			}
		} else {
			subs.add(this);
		}
	}

	private static boolean contains(String[] keys, String compared) {
		for (String key : keys) {
			if (compared.contains(key))
				return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String st0 = "wk_現物契約比率単位総額.　比率No単位総額+ SUM (wk_その他単価.　単価)*wk_現物契約比率単位総額.　ポジション数量";
		String st00 = "(wk_現物契約比率単位総額.　比率No単位総額+ SUM (wk_その他単価.　単価)*wk_現物契約比率単位総額.　ポジション数量)/wk_現物契約比率単位総額.　ポジション数量";
		String st = "COUNT(CASE WHEN 比率エリア(統計価格)．値決確定フラグ= '0'(未確定） THEN 1 ELSE NULL END) ";
		String st2 = "COUNT(CASE WHEN wk_日別値決明細．値決ステータス（市況価格）= 3'(値決済） THEN 1 ELSE NULL END)/COUNT(1)";
		String st3 = "SUM（数量範囲エリア.　値決数量×比率エリア.　比率×比率エリア.　比率値決進捗率）／契約エリア.　値決数量　×100";
		String st4 = "SUM（数量範囲エリア(統計価格).　値決数量×比率エリア(統計価格).　比率×比率エリア(統計価格).　比率値決進捗率）／契約エリア.　値決数量　　×100";
		Pattern p2 = Pattern.compile("\\w+\\S+\\.\\s+\\S+(?<![+\\-\\*\\/\\(\\)])");
		Pattern p3 = Pattern.compile("(\\S+)[\\．\\.](\\s*\\S+)(?<![\\(\\)=])"); //
//		if (true) {
//			// String[] x = st00.split("[+\\-\\*\\/×／（）\\(\\)]");
//			String[] x = st2.split("[ =]");
//			for (String a : x) {
//				Matcher m = p3.matcher(a);
//				if (m.find()) {
//					// System.out.print (m.group());
//					System.out.print(m.group(1) + "      ");
//					System.out.println(m.group(2) + "    ");
//				}
//			}
//		} 

		SqlField f = SqlFactory.sqlField(st4);
		System.out.println(f);
	}
}
