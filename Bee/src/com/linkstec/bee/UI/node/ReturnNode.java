package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class ReturnNode extends BasicNode implements Serializable, IUnit, BReturnUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6344483438233600172L;

	private String label;

	private String numberBID;

	private String labelBID, valueLabel;

	public ReturnNode() {

		this.addStyle("portConstraint=west");
		this.getGeometry().setWidth(150);
		this.setOpaque(false);
		HorizonalLayout layout = new HorizonalLayout();
		layout.setBetweenSpacing(1);
		this.setLayout(layout);
		this.makeNumberedLabel(layout);

	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		ComplexNode value = new ComplexNode();
		this.valueLabel = value.getId();
		layout.addNode(value);

		LabelNode node = new LabelNode();
		node.setValue("処理終了");
		this.labelBID = node.getId();
		layout.addNode(node);
	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	@Override
	public BValuable getReturnValue() {
		return LayoutUtils.getValueNode(this, valueLabel);

	}

	@Override
	public void setRuturnValue(BValuable obj) {
		((BasicNode) this.getCellByBID(valueLabel)).replace((mxCell) obj);
		this.getCellByBID(labelBID).setValue("を返す");
	}

	@Override
	public void setRuturnNullValue() {
		this.getCellByBID(labelBID).setValue("処理終了");
	}

	@Override
	public String getNodeDesc() {
		return "戻り値、値がない場合に当該処理を終了させる";
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
	public ImageIcon getIcon() {
		return BeeConstants.P_RETURN_ICON;
	}

	@Override
	public DropAction getDropAction() {
		return super.getDropAction();
	}

	@Override
	public void makeDefualtValue(Object target) {
		if (target instanceof BasicNode) {
			BMethod method = this.findMethod((mxICell) target);
			if (method != null) {

				BValuable value = method.getReturn();
				if (value == null) {
					this.setNullable(true);

				} else {
					BClass bclass = value.getBClass();

					if (bclass.getQualifiedName().equals(BClass.VOID)) {
						this.setNullable(true);
					} else {
						this.setRuturnValue(ComplexNode.makeDefaultValue(bclass));
					}
				}
			}
		}
	}

	private BMethod findMethod(mxICell node) {
		if (node instanceof BMethod) {
			return (BMethod) node;
		}
		mxICell parent = node.getParent();
		if (parent != null) {
			return this.findMethod(parent);
		}
		return null;
	}

}
