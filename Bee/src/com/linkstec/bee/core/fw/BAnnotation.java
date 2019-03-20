package com.linkstec.bee.core.fw;

import java.io.Serializable;
import java.util.List;

public interface BAnnotation extends Serializable, BVariable {

	public void addParameter(BAnnotationParameter parameter);

	public List<BAnnotationParameter> getParameters();

	public void deleteParameter(BAnnotationParameter parameter);

}
