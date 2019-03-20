package com.linkstec.bee.UI.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;

public class BModule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2913979290272844739L;
	Configuration config = new Configuration();
	List<BClass> commonUseClassList = new ArrayList<BClass>();
	List<BType> appTypes = new ArrayList<BType>();
	List<BClass> outterInterfaces = new ArrayList<BClass>();

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public Configuration getConfig() {
		return config;
	}

	public List<BClass> getCommonUseClassList() {
		return this.commonUseClassList;
	}

	public void setCommonUseClassList(List<BClass> classes) {
		this.commonUseClassList = classes;
	}

	public List<BType> getAppTypes() {
		return this.appTypes;
	}

	public void setAppTypes(List<BType> classes) {
		this.appTypes = classes;
	}

	public List<BClass> getOuterInterfaces() {
		return this.outterInterfaces;
	}

	public void setgetOuterInterfaces(List<BClass> classes) {
		this.outterInterfaces = classes;
	}

}
