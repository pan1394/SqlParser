package com.linkstec.bee.UI.node;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BParameter;

public class ParameterNode extends ComplexNode implements BParameter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5464890187384176820L;
	private int mod = 0;

	private List<BAnnotation> annotations = new ArrayList<BAnnotation>();

	public ParameterNode() {
		this.setEditable(true);
	}

	@Override
	public int getModifier() {
		return this.mod;
	}

	@Override
	public void setModifier(int modifier) {
		this.mod = modifier;
	}

	@Override
	public EditAction getAction() {
		return null;
	}

	@Override
	public void addAnnotation(BAnnotation annotion) {
		this.annotations.add(annotion);
	}

	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		this.annotations.remove(annotion);

	}

	@Override
	public List<BAnnotation> getAnnotations() {
		return this.annotations;
	}

}
