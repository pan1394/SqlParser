package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.core.fw.BAnnotation;

public class LayerModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8874530387591361228L;
	private NamingModel packegeName;
	private NamingModel name;
	private String superClass;
	private List<String> interfaces = new ArrayList<String>();
	private List<BAnnotation> annotations = new ArrayList<BAnnotation>();
	private ActionModel model;
	private ProcessType targetProcessType;
	private int index;

	private List<Object> parameters = new ArrayList<Object>();
	private Object returnType;

	public ProcessType getTargetProcessType() {
		return targetProcessType;
	}

	public void setTargetProcessType(ProcessType targetProcessType) {
		this.targetProcessType = targetProcessType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public LayerModel(ActionModel model) {
		this.model = model;
	}

	public ActionModel getModel() {
		return this.model;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	public Object getReturnType() {
		return returnType;
	}

	public void setReturnType(Object returnType) {
		this.returnType = returnType;
	}

	public List<BAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BAnnotation> annotations) {
		this.annotations = annotations;
	}

	public NamingModel getPackegeName() {
		return packegeName;
	}

	public void setPackegeName(NamingModel packegeName) {
		this.packegeName = packegeName;
	}

	public NamingModel getName() {
		return name;
	}

	public void setName(NamingModel name) {
		this.name = name;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public List<String> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}

}
