package com.linkstec.bee.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BObject;

public class BObjectImpl implements BObject, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4128006759693806942L;

	private BObject owener;
	private long line;
	private BAlert alert = new BAlert();
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();
	private Object object;
	private List<BAnnotation> annotations = new ArrayList<BAnnotation>();

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	@Override
	public void setOwener(BObject object) {
		this.owener = object;
	}

	public long getLine() {
		return line;
	}

	public void setLine(long line) {
		this.line = line;
	}

	@Override
	public BObject getOwener() {
		return owener;
	}

	@Override
	public String getAlert() {
		return this.alert.getMessage();
	}

	@Override
	public BAlert setAlert(String alert) {
		this.alert = new BAlert();
		this.alert.setMessage(alert);
		return this.alert;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void setUserObject(Object object) {
		this.object = object;

	}

	@Override
	public Object getUserObject() {
		return this.object;
	}

	@Override
	public BAlert getAlertObject() {
		return this.alert;
	}

	public void addAnnotation(BAnnotation annotation) {
		this.annotations.add(annotation);
	}

	public List<BAnnotation> getAnnotations() {
		return this.annotations;
	}

	@Override
	public Object cloneAll() {
		try {
			return this.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void deleteAnnotation(BAnnotation annotion) {
		for (BAnnotation anno : this.annotations) {
			if (anno.getLogicName().equals(annotion.getLogicName())) {
				this.annotations.remove(anno);
				break;
			}
		}
	}

}
