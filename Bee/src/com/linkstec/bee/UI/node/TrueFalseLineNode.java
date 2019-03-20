package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.mxgraph.model.mxCell;

public class TrueFalseLineNode extends BasicNode implements Serializable, BExpressionLine {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9020988243998913362L;
	private String conditionBID, trueObjectBID, falseObjetBID;

	private HorizonalLayout layout = new HorizonalLayout();

	public TrueFalseLineNode() {
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		layout.setSpacing(0);
		this.setLayout(layout);

		ExpressionNode ex = new ExpressionNode();
		ex.setAlert("条件を設定してください").setType(BAlert.TYPE_ERROR);
		this.setCondition(ex);

		ComplexNode t = new ComplexNode();
		t.setAlert("値を設定してください").setType(BAlert.TYPE_ERROR);
		this.setTrue(t);

		ComplexNode f = new ComplexNode();
		f.setAlert("値を設定してください").setType(BAlert.TYPE_ERROR);
		this.setFalse(f);

	}

	@Override
	public BValuable getCondition() {
		return LayoutUtils.getValueNode(this, conditionBID);
	}

	@Override
	public BClass getBClass() {
		if (this.cast != null) {
			return this.cast.getBClass();
		}

		BValuable t = this.getTrue();
		if (t != null && t.getBClass() != null && !t.getBClass().getQualifiedName().equals(BClass.NULL)) {
			return t.getBClass();
		}

		BValuable f = this.getFalse();
		if (f != null && t.getBClass() != null && !f.getBClass().getQualifiedName().equals(BClass.NULL)) {
			return f.getBClass();
		}

		return null;
	}

	@Override
	public void setCondition(BValuable object) {
		BasicNode node = (BasicNode) this.getCondition();
		if (node != null) {
			node.replace((mxCell) object);
		} else {
			BasicNode b = (BasicNode) object;
			this.conditionBID = b.getId();
			this.layout.addNode(b);
			LabelNode label = new LabelNode();
			label.setValue("の場合に");
			layout.addNode(label);
		}
	}

	@Override
	public void setTrue(BValuable object) {
		BasicNode node = (BasicNode) this.getTrue();
		if (node != null) {
			node.replace((mxCell) object);
		} else {
			BasicNode b = (BasicNode) object;
			this.trueObjectBID = b.getId();
			this.layout.addNode(b);
			LabelNode label = new LabelNode();
			label.setValue("、");
			layout.addNode(label);
		}
	}

	@Override
	public BValuable getTrue() {
		return LayoutUtils.getValueNode(this, trueObjectBID);
	}

	@Override
	public void setFalse(BValuable object) {
		BasicNode node = (BasicNode) this.getFalse();
		if (node != null) {
			node.replace((mxCell) object);
		} else {
			LabelNode label = new LabelNode();
			label.setValue("否の場合に");
			layout.addNode(label);

			BasicNode b = (BasicNode) object;
			this.falseObjetBID = b.getId();
			this.layout.addNode(b);
		}
	}

	@Override
	public BValuable getFalse() {
		return LayoutUtils.getValueNode(this, falseObjetBID);
	}

	private String nodeDesc = "値に、ある条件が成り立つ場合に指定値を設定し、当該条件が成り立たない場合に違う指定値を設定する";

	@Override
	public String getNodeDesc() {
		return nodeDesc;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_TRUEFALSE_ICON;
	}

	private BValuable cast;

	@Override
	public void setCast(BValuable cast) {
		this.cast = cast;

	}

	@Override
	public BValuable getCast() {
		return this.cast;
	}

	private BValuable arrayIndex;
	private BValuable arrayObject;

	@Override
	public BValuable getArrayIndex() {
		return this.arrayIndex;
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.arrayIndex = index;
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.arrayObject = object;
	}

	@Override
	public BValuable getArrayObject() {
		return this.arrayObject;
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.cast != null) {
			return this.cast.getParameterizedTypeValue();
		}

		BValuable t = this.getTrue();
		if (t != null && t.getBClass() != null && !t.getBClass().getQualifiedName().equals(BClass.NULL)) {
			return t.getParameterizedTypeValue();
		}

		BValuable f = this.getFalse();
		if (f != null && t.getBClass() != null && !f.getBClass().getQualifiedName().equals(BClass.NULL)) {
			return f.getParameterizedTypeValue();
		}

		return null;
	}

	public void makeDefaultValue() {
		ExpressionNode node = new ExpressionNode();
		node.makeDefaultValue();
		this.setCondition(node);
		ComplexNode tnode = (ComplexNode) ComplexNode.makeDefaultValue(CodecUtils.BString().cloneAll());
		tnode.setLogicName("\"a\"");
		this.setTrue(tnode);

		ComplexNode fnode = (ComplexNode) ComplexNode.makeDefaultValue(CodecUtils.BString().cloneAll());
		fnode.setLogicName("\"b\"");
		this.setFalse(fnode);
	}

}
