package com.linkstec.bee.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BType;

public class BTypeImpl extends BObjectImpl implements BType, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3109006022389800420L;

	protected String logicName;
	private String name;
	protected List<String> bounds = new ArrayList<String>();
	protected List<BType> types = new ArrayList<BType>();
	private BType arrayClass = null;
	private boolean wild = false;
	private boolean typeVariable = false;
	private boolean container = false;
	private boolean value = false;
	private boolean rawType = false;
	private BType ownerType = null;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
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
	public void addParameterizedType(BType name) {
		this.types.add(name);
	}

	@Override
	public void setParameterTypes(List<BType> types) {
		this.types = types;
	}

	@Override
	public List<BType> getParameterizedTypes() {
		List<BType> ts = new ArrayList<BType>();
		ts.addAll(types);
		return ts;
	}

	@Override
	public void addBound(String type) {
		this.bounds.add(type);
	}

	@Override
	public List<String> getBounds() {
		return this.bounds;
	}

	@Override
	public BType getArrayPressentClass() {
		return this.arrayClass;
	}

	@Override
	public void setArrayPressentClass(BType bclass) {
		this.arrayClass = bclass;
	}

	@Override
	public boolean isArray() {
		return this.arrayClass != null;
	}

	@SuppressWarnings("unchecked")
	protected void cloneList(BTypeImpl type) {
		type.bounds = (List<String>) ((ArrayList<String>) this.bounds).clone();
		type.types = (List<BType>) ((ArrayList<BType>) this.types).clone();
	}

	@Override
	public boolean isWild() {
		return this.wild;
	}

	public void setWild() {
		this.wild = true;
	}

	@Override
	public boolean isParameterized() {
		return this.types.size() > 0;
	}

	@Override
	public boolean isTypeVariable() {
		return this.typeVariable;
	}

	@Override
	public boolean isClass() {
		return false;
	}

	public void setTypeVariable() {
		this.typeVariable = true;
	}

	@Override
	public void clearParameterTypes() {
		this.types.clear();
	}

	@Override
	public boolean isContainer() {
		return this.container;
	}

	public void setContainer() {
		this.container = true;
	}

	@Override
	public boolean isParameterValue() {
		return this.value;
	}

	@Override
	public void setParameterValue(boolean value) {
		this.value = value;
	}

	public String toString() {
		return this.logicName;
	}

	@Override
	public boolean isRawType() {
		return this.rawType;
	}

	@Override
	public void setRowType(boolean raw) {
		this.rawType = raw;

	}

	@Override
	public void setOwnerType(BType type) {
		this.ownerType = type;
	}

	@Override
	public BType getOwnerType() {
		return this.ownerType;
	}
}
