package com.linkstec.bee.core.fw;

import java.util.List;

public interface BAnnotable {

	public void addAnnotation(BAnnotation annotion);

	public void deleteAnnotation(BAnnotation annotion);

	public List<BAnnotation> getAnnotations();

}
