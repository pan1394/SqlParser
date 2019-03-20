package com.linkstec.bee.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BMethodImpl extends BValuableImpl implements BMethod, Serializable, ILogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3366838705924283356L;
	private String name;
	private String logicName;
	private String declearedParentName;
	private BValuable returnValue;
	private BLogicBody body;
	private List<BParameter> parameters;
	private List<BVariable> exptions;
	private List<BType> definedTypes = new ArrayList<BType>();
	private String label;
	private int modifier = 0;

	public BMethodImpl() {
		parameters = new ArrayList<BParameter>();
		exptions = new ArrayList<BVariable>();
	}

	public void setDeclearedParentName(String name) {
		this.declearedParentName = name;
	}

	public String getDeclearedParentName() {
		return this.declearedParentName;
	}

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
	public void setReturn(BValuable value) {
		this.returnValue = value;

	}

	@Override
	public BValuable getReturn() {
		return this.returnValue;
	}

	@Override
	public List<BParameter> getParameter() {
		return this.parameters;
	}

	@Override
	public List<BVariable> getThrows() {
		return this.exptions;
	}

	@Override
	public void addThrows(BVariable exception) {
		this.exptions.add(exception);
	}

	@Override
	public void setLogicBody(BLogicBody body) {
		this.body = body;
	}

	@Override
	public BLogicBody getLogicBody() {
		return this.body;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public BClass getBClass() {
		if (this.getCast() != null) {
			return this.getCast().getBClass();
		}
		return returnValue.getBClass();
	}

	public BObject cloneAll() {
		try {
			return (BObject) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addParameter(BParameter parameter) {
		this.parameters.add(parameter);
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.getCast() != null) {
			return this.getCast().getParameterizedTypeValue();
		}
		return returnValue.getParameterizedTypeValue();
	}

	@Override
	public void addDefinedType(BType type) {
		this.definedTypes.add(type);
	}

	@Override
	public List<BType> getDefinedTypes() {
		return this.definedTypes;
	}

	@Override
	public void setModifier(int mods) {
		this.modifier = mods;
	}

	@Override
	public int getModifier() {
		return this.modifier;
	}

	@Override
	public NodeNumber getNumber() {
		return null;
	}

}
