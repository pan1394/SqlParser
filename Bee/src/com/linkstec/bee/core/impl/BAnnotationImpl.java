package com.linkstec.bee.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;

public class BAnnotationImpl extends BVariableImpl implements BAnnotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4238554622103829351L;

	private List<BAnnotationParameter> parameters = new ArrayList<BAnnotationParameter>();

	@Override
	public void addParameter(BAnnotationParameter parameter) {
		this.parameters.add(parameter);
	}

	@Override
	public List<BAnnotationParameter> getParameters() {
		return this.parameters;
	}

	@Override
	public void deleteParameter(BAnnotationParameter parameter) {
		for (BAnnotationParameter anno : this.parameters) {
			if (anno.getLogicName().equals(parameter.getLogicName())) {
				this.parameters.remove(anno);
				break;
			}
		}

	}

}
