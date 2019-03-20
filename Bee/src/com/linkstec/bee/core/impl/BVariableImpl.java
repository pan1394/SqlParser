package com.linkstec.bee.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;

public class BVariableImpl extends BValuableImpl implements BVariable, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5115263439380199771L;

	private String name;
	private String logicName;
	private boolean caller = false;
	private boolean annotation = false;
	private List<BObject> annotationParameters = new ArrayList<BObject>();
	private boolean arrayTitled = true;
	private boolean newClass = false;
	private BType type;
	private BClass bclass;
	private boolean varArgs = false;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getLogicName() {
		return this.logicName;
	}

	@Override
	public void setLogicName(String name) {
		this.logicName = name;
	}

	@Override
	public void makeCaller(BClass bclass, boolean self) {
	}

	@Override
	public Object cloneAll() {
		try {
			return this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isClass = false;

	@Override
	public boolean isClass() {
		return this.isClass;
	}

	@Override
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}

	@Override
	public void setNewClass(boolean newClass) {
		this.newClass = newClass;
	}

	public String toString() {
		return this.logicName;
	}

	@Override
	public void setCaller(boolean caller) {
		this.caller = caller;
	}

	@Override
	public boolean isCaller() {
		return this.caller;
	}

	private List<BValuable> dimensions = new ArrayList<BValuable>();
	private List<BValuable> values = new ArrayList<BValuable>();

	@Override
	public void addArrayDimension(BValuable d) {
		dimensions.add(d);
	}

	@Override
	public List<BValuable> getArrayDimensions() {
		return dimensions;
	}

	@Override
	public void addInitValue(BValuable value) {
		values.add(value);

	}

	@Override
	public List<BValuable> getInitValues() {
		return values;
	}

	@Override
	public boolean isNewClass() {
		return this.newClass;
	}

	boolean wildCard = false;

	@Override
	public void setWildCard() {
		wildCard = true;

	}

	@Override
	public boolean isWildCard() {
		return wildCard;
	}

	@Override
	public void setAnnotation(boolean anno) {
		this.annotation = anno;
	}

	@Override
	public boolean isAnnotation() {
		return this.annotation;
	}

	// @Override
	// public void addAnnotationParameter(BObject parameter) {
	// this.annotationParameters.add(parameter);
	// }
	//
	// @Override
	// public List<BObject> getAnnotationParameters() {
	// return this.annotationParameters;
	// }

	private List<BVariable> unionTyps = new ArrayList<BVariable>();

	@Override
	public void addUnionType(BVariable var) {
		this.unionTyps.add(var);
	}

	@Override
	public List<BVariable> getUnionTypes() {
		return this.unionTyps;
	}

	@Override
	public void setArrayTitle(boolean titled) {
		this.arrayTitled = titled;
	}

	@Override
	public boolean isArrayTitled() {
		return this.arrayTitled;
	}

	@Override
	public void setVarArgs(boolean args) {
		this.varArgs = args;

	}

	@Override
	public boolean isVarArgs() {
		return this.varArgs;
	}

	@Override
	public void setParameterizedTypeValue(BType type) {
		this.type = type;
	}

	@Override
	public void setBClass(BClass type) {
		this.bclass = type;
	}

	@Override
	public BType getParameterizedTypeValue() {

		return this.type;
	}

	@Override
	public BClass getBClass() {
		return this.bclass;
	}

	@Override
	public void clearInitValues() {
		this.values.clear();
	}

	@Override
	public void replaceInitValue(int index, BValuable value) {
		this.values.remove(index);
		this.values.add(index, value);
	}

}
