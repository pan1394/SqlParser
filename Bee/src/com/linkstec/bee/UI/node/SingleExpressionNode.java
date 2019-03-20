package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.mxgraph.model.mxCell;

public class SingleExpressionNode extends BasicNode implements Serializable, IUnit, BSingleExpressionUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 506324429950731465L;

	private String valueBID, operatorBID;

	private String label;

	public SingleExpressionNode() {
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		this.setLayout(layout);
		ComplexNode value = new ComplexNode();
		layout.addNode(value);
		this.valueBID = value.getId();
		ObjectNode operator = new ObjectNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8032764203186290099L;

			@Override
			public EditAction getAction() {
				MixAction action = new MixAction();
				String op = getOperator();
				String name = "--";

				if (op.equals(BSingleExpressionUnit.DECREMENT)) {
					name = "++";
				}
				name = name + "へ変換する";
				action.addAction(name, new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 2354355026282163804L;

					@Override
					public void call() {
						if (op.equals(BSingleExpressionUnit.DECREMENT)) {
							setOperator(BSingleExpressionUnit.INCREMENT);
						} else if (op.equals(BSingleExpressionUnit.INCREMENT)) {
							setOperator(BSingleExpressionUnit.DECREMENT);
						}
					}

				});
				return action;
			}

		};
		operator.setFixedWidth(20);
		layout.addNode(operator);
		this.operatorBID = operator.getId();
		this.setOpaque(false);

		ComplexNode node = new ComplexNode();
		this.setVariable(node);
	}

	@Override
	public BValuable getVariable() {
		return LayoutUtils.getValueNode(this, valueBID);
	}

	@Override
	public void setVariable(BValuable variable) {
		((BasicNode) this.getCellByBID(this.valueBID)).replace((mxCell) variable);
	}

	@Override
	public void setOperator(String operator) {
		mxCell cell = this.getCellByBID(this.operatorBID);
		if ((operator.equals(BSingleExpressionUnit.DECREMENT_BEFORE)
				|| operator.equals(BSingleExpressionUnit.DECREMENT_BEFORE)
				|| operator.equals(BSingleExpressionUnit.UNARY_MINUS)
				|| operator.equals(BSingleExpressionUnit.UNARY_PLUS))) {
			this.insert(cell, 0);
		}
		this.getCellByBID(this.operatorBID).setValue(operator);
	}

	@Override
	public String getOperator() {
		return (String) this.getCellByBID(this.operatorBID).getValue();
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	private String nodeDesc = "値をインクリメントする。若しくはデクリメントする";

	@Override
	public String getNodeDesc() {
		return nodeDesc;
	}

	@Override
	public BClass getBClass() {
		if (this.cast != null) {
			return this.cast.getBClass();
		}
		BValuable var = this.getVariable();
		if (var != null) {
			return var.getBClass();
		}
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_LINE_ICON;
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

	@Override
	public BValuable getArrayIndex() {
		return this.getVariable().getArrayIndex();
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.getVariable().setArrayIndex(index);
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.getVariable().setArrayObject(object);
	}

	@Override
	public BValuable getArrayObject() {
		return this.getVariable().getArrayObject();
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.cast != null) {
			return this.cast.getParameterizedTypeValue();
		}
		BValuable var = this.getVariable();
		if (var != null) {
			return var.getParameterizedTypeValue();
		}
		return null;
	}

	@Override
	public void makeDefualtValue(Object target) {
		this.setVariable((BVariable) ComplexNode.makeDefaultValue(CodecUtils.BInt().cloneAll()));
	}
}
