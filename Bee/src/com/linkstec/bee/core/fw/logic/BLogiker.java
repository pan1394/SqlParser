package com.linkstec.bee.core.fw.logic;

public enum BLogiker {

	MINUS("-", "-", BLogiker.TYPE_OBJECT),
	// !
	NOT("!", " is false", BLogiker.TYPE_BOOLEAN),
	// +
	PLUS("+", "+", BLogiker.TYPE_OBJECT),
	// *
	MULTIPLY("*", "*", BLogiker.TYPE_OBJECT),
	// divide
	DIVIDE("/", "/", BLogiker.TYPE_OBJECT),
	// <
	LESSTHAN("<", "<", BLogiker.TYPE_BOOLEAN),
	// <=
	LESSTHANEQUAL("<=", "<=", BLogiker.TYPE_BOOLEAN),
	// >=
	GREATTHANEQUAL(">=", ">=", BLogiker.TYPE_BOOLEAN),
	// >
	GREATTHAN(">", " > ", BLogiker.TYPE_BOOLEAN),
	// =
	EQUAL("=", "=", BLogiker.TYPE_BOOLEAN),
	// !=
	NOTQUEAL("!=", "!=", BLogiker.TYPE_BOOLEAN),
	// %
	MOD("%", "除算の余り", BLogiker.TYPE_OBJECT),
	// <<
	SHEFTLEFT("<<", "左移動", BLogiker.TYPE_OBJECT),
	// <<<
	SHEFTLEFTPLUS("<<<", "左移動3", BLogiker.TYPE_OBJECT),
	// >>
	SHEFTRIGHT(">>", "右移動", BLogiker.TYPE_OBJECT),
	// >>>
	SHEFTRIGHTPLUS(">>>", "左移動3", BLogiker.TYPE_OBJECT),
	// &
	BITAND("&", "ビットAND", BLogiker.TYPE_OBJECT),
	// |
	BITOR("bor", "|", BLogiker.TYPE_OBJECT),
	// &&
	LOGICAND("&&", "且つ", BLogiker.TYPE_BOOLEAN),
	// ||
	LOGICOR("||", "若しくは", BLogiker.TYPE_BOOLEAN),
	// ^
	XOR("^", "XOR", BLogiker.TYPE_OBJECT),

	// complement
	COMPLEMENT("~", " ~", BLogiker.TYPE_OBJECT),

	// instance of
	INSTANCEOF("instanceof", "のタイプ=", BLogiker.TYPE_BOOLEAN),

	// ---------------------------extra ----------------
	// IN
	IN("IN", "IN", BLogiker.TYPE_BOOLEAN),
	// IN
	NOTIN("NOT IN", "NOT IN", BLogiker.TYPE_BOOLEAN),
	// IN
	LIKE("LIKE", "LIKE", BLogiker.TYPE_BOOLEAN),
	// IN
	NOTLIKE("NOT LIKE", "NOT LIKE", BLogiker.TYPE_BOOLEAN),
	// between
	BETWEE("BETWEEN", "BETWEEN", BLogiker.TYPE_BOOLEAN),
	// not between
	NOTBETWEE("NOT BETWEEN", "NOT BETWEEN", BLogiker.TYPE_BOOLEAN);

	public static final String TYPE_BOOLEAN = "BOOLEAN";
	public static final String TYPE_OBJECT = "OBJECT";
	private final String name;
	private final String logicName;
	private final String type;

	private BLogiker(String logicName, String name, String type) {
		this.name = name;
		this.logicName = logicName;
		this.type = type;
	}

	public String toString() {
		return this.name;
	}

	public String getLogicName() {
		return logicName;
	}

	public String getType() {
		return type;
	}
}
