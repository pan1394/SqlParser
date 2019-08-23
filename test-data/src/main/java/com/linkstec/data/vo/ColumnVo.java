package com.linkstec.data.vo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnVo {

	private String columnId;
	private String name;
	private String type;
	private String value;
	private String maxLength;
	private String isNullable;
	private String isIdentity;
	private String precision;
	private String scale;
	private String isPk;
	private String length;
	private boolean isPrimary;
	
	
	public boolean isPrimary() {
		return "1".equals(isPk);
	}
 
	public String getIsPk() {
		return isPk;
	}

	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		Pattern p = Pattern.compile("\\((\\d+)\\)");
		Matcher m = p.matcher(type);
		String tmp = "";
		if (m.find()) {
			tmp = m.group();
			length = m.group(1);
		}
		this.type = type.replace(tmp, "");
	}

	public String getMaxLength() {
		if (maxLength == null) {
			this.maxLength = length;
		}
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}

	public String getIsIdentity() {
		return isIdentity;
	}

	public void setIsIdentity(String isIdentity) {
		this.isIdentity = isIdentity;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

}
