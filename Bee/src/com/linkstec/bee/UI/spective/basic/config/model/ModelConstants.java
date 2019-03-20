package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelConstants {
	public static final int TYPE_PARAMETER = 1;
	public static final int TYPE_STATIC = 2;
	public static final int TYPE_RETURN = 3;
	public static final int TYPE_INPUTEDIT = 4;
	public static final int TYPE_PAREMETE_INVOKE = 5;

	public static final int TYPE_PROCESS_FLOW = 1;
	public static final int TYPE_PROCESS_LOGIC = 2;
	public static final int TYPE_PROCESS_IO = 3;

	public static final String DB_ICON = "/com/linkstec/bee/UI/images/icons/node_db.gif";
	public static final String FILE_ICON = "/com/linkstec/bee/UI/images/icons/node_file.gif";
	public static final String SESSION_ICON = "/com/linkstec/bee/UI/images/icons/node_session.gif";
	public static final String TEXT_ICON = "/com/linkstec/bee/UI/images/icons/node_text.gif";
	public static final String APPLICATION_ICON = "/com/linkstec/bee/UI/images/icons/node_application.png";
	public static final String MESSAGE_ICON = "/com/linkstec/bee/UI/images/icons/node_message.gif";

	public static class BasicType implements Serializable, Cloneable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3377582347395175531L;

		private int type;

		private String name;

		private BasicType(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public static class ProcessType extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3070925112634901055L;
		public static final int TYPE_PROCESS_FLOW = 1;
		public static final int TYPE_PROCESS_LOGIC = 2;
		public static final int TYPE_PROCESS_IO = 3;
		public static final int TYPE_PROCESS_TABLE = 4;

		public static ProcessType TYPE_FLOW = new ProcessType(TYPE_PROCESS_FLOW, "遷移設計", "遷移処理");
		public static ProcessType TYPE_LOGIC = new ProcessType(TYPE_PROCESS_LOGIC, "ロジック設計", "業務処理");
		public static ProcessType TYPE_IO = new ProcessType(TYPE_PROCESS_IO, "IO関連図", "IO対象編集");
		public static ProcessType TYPE_TABLE = new ProcessType(TYPE_PROCESS_TABLE, "IO処理詳細", "IO処理詳細");

		public static ProcessType[] values() {
			return new ProcessType[] { TYPE_FLOW,
					//
					TYPE_LOGIC,
					//
					TYPE_IO,
					//
					TYPE_TABLE };
		}

		private String title;

		public ProcessType(int type, String name, String title) {
			super(type, name);
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

	}

	public static class OutputType extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3070925112634901055L;

		public static OutputType[] values() {
			return new OutputType[] {
					//
					new OutputType(TYPE_PARAMETER, "INDTO"),
					//
					new OutputType(TYPE_STATIC, "静的関数"),
					//
					new OutputType(TYPE_PAREMETE_INVOKE, "渡したパラメータのメソッドを実施する")
					//
			};
		}

		public OutputType(int type, String name) {
			super(type, name);
		}

	}

	public static class ReturnType extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3070925112634901055L;

		public static final int TYPE_SINGLE_DATA = 10;
		public static final int TYPE_LIST_DATA = 11;
		public static final int TYPE_INT = 12;
		public static final int TYPE_STRING = 13;
		public static final int TYPE_UPDATE = 20;
		public static final int TYPE_INSERT = 30;
		public static final int TYPE_DELETE = 40;

		public static final ReturnType SINGLE_RETURN = new ReturnType(TYPE_SINGLE_DATA, "1レコード取得");
		public static final ReturnType COUNT_RETURN = new ReturnType(TYPE_INT, "レコード数取得");
		public static final ReturnType LIST_RETURN = new ReturnType(TYPE_LIST_DATA, "複数レコード取得");
		public static final ReturnType STRING_RETURN = new ReturnType(TYPE_STRING, "文字内容取得");
		public static final ReturnType UPDATE = new ReturnType(TYPE_UPDATE, "レコード更新");
		public static final ReturnType INSERT = new ReturnType(TYPE_INSERT, "レコード追加");
		public static final ReturnType DELETE = new ReturnType(TYPE_DELETE, "レコード削除");

		public static ReturnType[] values() {
			return new ReturnType[] {
					//
					SINGLE_RETURN,
					//
					LIST_RETURN,
					//
					COUNT_RETURN,
					//
					STRING_RETURN,
					//
					UPDATE,
					//
					INSERT,
					//
					DELETE };
		}

		public ReturnType(int type, String name) {
			super(type, name);
		}

	}

	public static class InputType extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5488462479847754612L;
		public static InputType RETURN = new InputType(TYPE_RETURN, "OUTDTO");
		//
		public static InputType DTO_EDIT = new InputType(TYPE_INPUTEDIT, "INDTOの編集後値");
		//
		public static InputType PAREMTER_INVOKE = new InputType(TYPE_PAREMETE_INVOKE, "渡したパラメータのメソッドを実施する");

		public static InputType[] values() {
			return new InputType[] {
					//
					RETURN,
					//
					DTO_EDIT,
					//
					PAREMTER_INVOKE
					//
			};
		}

		private InputType(int type, String name) {
			super(type, name);
		}

	}

	public static class NamingType extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3475221120922232347L;

		public static int TYPE_COMPONENT_ID = 11;
		public static int TYPE_COMPONENT_NAME = 12;
		public static int TYPE_SUB_NAME = 13;
		public static int TYPE_SUB_ID = 14;
		public static int TYPE_FIXED = 15;
		public static int TYPE_CLASS_NAME = 16;
		public static int TYPE_METHOD_NAME = 17;

		public static NamingType COM_ID = new NamingType(TYPE_COMPONENT_ID, "コンポーネントID", "C001");
		public static NamingType COM_NAME = new NamingType(TYPE_COMPONENT_NAME, "コンポーネント英名", "Login");
		public static NamingType SUB_ID = new NamingType(TYPE_SUB_ID, "機能ID", "S001");
		public static NamingType SUB_NAME = new NamingType(TYPE_SUB_NAME, "機能英名", "Mng");
		public static NamingType FIXED = new NamingType(TYPE_FIXED, "固定値", "something");
		public static NamingType CLASS_NAME = new NamingType(NamingType.TYPE_CLASS_NAME, "クラス名", "C001Logic");
		public static NamingType METHOD_NAME = new NamingType(NamingType.TYPE_METHOD_NAME, "メソッド名", "edit");

		private List<NamingSub> subs = new ArrayList<NamingSub>();

		private String example = "";

		public static NamingType[] values() {
			return new NamingType[] {
					//
					COM_ID,
					//
					COM_NAME,
					//
					SUB_ID, SUB_NAME, CLASS_NAME, METHOD_NAME, FIXED
					//
			};
		}

		public List<NamingSub> getSubs() {
			return subs;
		}

		public void setSubs(List<NamingSub> subs) {
			this.subs = subs;
		}

		public NamingType(int type, String name, String example) {
			super(type, name);
			this.example = example;
		}

		public void setExample(String example) {
			this.example = example;
		}

		public String getNaming(String name) {
			if (this.subs != null && this.subs.size() > 0) {
				String s = name;
				for (NamingSub sub : subs) {
					s = sub.change(s);
				}
				return s;
			}
			return name;
		}

		public String getExample() {

			return this.getNaming(example);
		}

		@Override
		public String toString() {
			if (this.subs != null && this.subs.size() > 0) {
				String s = "(";
				for (int i = 0; i < subs.size(); i++) {
					if (i != 0) {
						s = s + ",";
					}
					s = s + subs.get(i).getShortName();
				}
				s = s + ")";

				return this.getName() + s;
			}
			return super.toString();
		}
	}

	public static class NamingSub extends BasicType {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7093081596903345819L;
		public static int TYPE_ALL_LOWER = 1;
		public static int TYPE_ALL_UPPER = 2;
		public static int TYPE_FIRST_UPPER = 3;
		public static int TYPE_FIRST_LOWER = 4;

		public static NamingSub ALL_LOWER = new NamingSub(TYPE_ALL_LOWER, "(AS)すべて小文字", "AS");
		public static NamingSub ALL_UPPER = new NamingSub(TYPE_ALL_UPPER, "(AB)すべて大文字", "AB");
		public static NamingSub FIRST_LOWER = new NamingSub(TYPE_FIRST_LOWER, "(FS)1桁目小文字", "FS");
		public static NamingSub FIRST_UPPER = new NamingSub(TYPE_FIRST_UPPER, "(FB)1桁目大文字", "FB");

		public static NamingSub[] All() {
			return new NamingSub[] { ALL_LOWER, ALL_UPPER };
		}

		public static NamingSub[] First() {
			return new NamingSub[] { FIRST_LOWER, FIRST_UPPER };
		}

		private String shortName;

		public NamingSub(int type, String name, String shortName) {
			super(type, name);
			this.shortName = shortName;
		}

		public String getShortName() {
			return this.shortName;
		}

		public String change(String s) {

			if (this.getType() == TYPE_ALL_LOWER) {
				return s.toLowerCase();
			} else if (this.getType() == TYPE_ALL_UPPER) {
				return s.toUpperCase();
			} else if (this.getType() == TYPE_FIRST_UPPER) {
				return s.substring(0, 1).toUpperCase() + s.substring(1);
			} else if (this.getType() == TYPE_FIRST_LOWER) {
				return s.substring(0, 1).toLowerCase() + s.substring(1);
			}
			return s;
		}
	}
}
