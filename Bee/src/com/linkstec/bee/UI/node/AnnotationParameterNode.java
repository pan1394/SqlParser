package com.linkstec.bee.UI.node;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.DeleteAction;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxCell;

public class AnnotationParameterNode extends ComplexNode implements BAnnotationParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -234099957642203489L;
	private String typeBID, nameBID, valueBID;
	private int titleWidth = 150;
	private int height = BeeConstants.VALUE_NODE_SPACING * 2 + BeeConstants.LINE_HEIGHT;

	public AnnotationParameterNode() {

		this.getGeometry().setHeight(height);
		HorizonalLayout hl = new HorizonalLayout();

		hl.setSpacing(0);
		hl.setBetweenSpacing(0);
		this.setLayout(hl);
		this.setGreenTitled();

		LabelNode type = new LabelNode();
		type.setFixedWidth(titleWidth);
		this.typeBID = type.getId();
		type.getGeometry().setHeight(height);
		type.setTitled();
		hl.addNode(type);

		LabelNode nameLabel = new LabelNode();
		this.nameBID = nameLabel.getId();
		nameLabel.getGeometry().setHeight(height);
		nameLabel.setFixedWidth(titleWidth);
		nameLabel.setTitled();
		hl.addNode(nameLabel);

		ComplexNode valueLabel = new ComplexNode();
		this.valueBID = valueLabel.getId();
		valueLabel.getGeometry().setHeight(height);
		valueLabel.setFixedWidth((int) (BeeConstants.SEGMENT_MAX_WIDTH - titleWidth * 2 - 8));

		valueLabel.setEditable(true);
		valueLabel.makeBorder();
		hl.addNode(valueLabel);

		this.addStyle("textOpacity=0");

	}

	@Override
	public EditAction getAction() {
		DeleteAction action = new DeleteAction();
		action.addAction("削除", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6439208185250910288L;

			@Override
			public void call() {
				AnnotationParameterNode.this.removeFromParent();
			}

		});
		return action;
	}

	@Override
	public String getLogicName() {
		return (String) this.getCellByBID(nameBID).getValue();
	}

	@Override
	public void setLogicName(String name) {
		this.getCellByBID(nameBID).setValue(name);
	}

	@Override
	public BClass getBClass() {
		return (BClass) this.getCellByBID(typeBID).getValue();
	}

	@Override
	public void setBClass(BClass bclass) {
		this.getCellByBID(typeBID).setValue(bclass);
	}

	@Override
	public void setValue(BValuable value) {

		BasicNode b = (BasicNode) value;
		b.getGeometry().setHeight(height);
		((BasicNode) this.getCellByBID(valueBID)).replace(b);

		b.getGeometry().setHeight(height);
		if (b.getChildCount() > 0) {
			b.addStyle("textOpacity=0");
		}
		if (b instanceof ComplexNode) {
			ComplexNode node = (ComplexNode) b;
			node.setArrayTitle(false);
		}
	}

	@Override
	public BValuable getValue() {
		mxCell value = this.getCellByBID(valueBID);
		Object v = value.getValue();
		if (v instanceof BValuable) {
			return (BValuable) v;
		}

		return (BValuable) value;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ANNOTATION_PARAMETER_ICON;
	}
}
