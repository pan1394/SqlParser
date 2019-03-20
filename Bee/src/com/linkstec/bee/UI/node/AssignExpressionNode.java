package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.IClassMember;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.logic.VerifyHelper;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.util.BeeName;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class AssignExpressionNode extends BasicNode
		implements Serializable, IClassMember, ILogic, IUnit, BAssignExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2597786306424975700L;

	private String labelRowBID, nameBID, valueBID, titleLabelBID;
	private String nameLabelBID, valueLabelBID;

	private String label;

	private String numberBID, logikerBID, valueCellBID, valueLeftBID;

	public AssignExpressionNode() {
		this.getGeometry().setRelative(true);
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setOpaque(false);
		this.setLayout(layout);

		mxGeometry node = new mxGeometry(0, 0, 600, BeeConstants.LINE_HEIGHT);
		this.setGeometry(node);
		this.setFixedWidth(600);

		this.makeNumber();
		this.makeTitle();
		this.makeValue();

	}

	private void makeNumber() {
		BasicNode row = new BasicNode();
		row.setOpaque(false);
		this.titleLabelBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		LabelNode number = new LabelNode();
		number.setOpaque(false);
		this.numberBID = number.getId();
		layout.addNode(number);

		LabelNode title = new LabelNode();
		title.setOpaque(false);
		this.titleLabelBID = number.getId();
		title.setValue("以下のように変数へ値を設定する");
		layout.addNode(title);

		this.getLayout().addNode(row);
	}

	private void makeTitle() {
		BasicNode row = new BasicNode();
		labelRowBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		LabelNode nameLable = new LabelNode();
		nameLable.setValue("変数");
		this.nameLabelBID = nameLable.getId();
		layout.addNode(nameLable);
		nameLable.setFixedWidth(300);
		nameLable.setTitled();

		LabelNode valueLable = new LabelNode();
		valueLable.setValue("値");
		this.valueLabelBID = valueLable.getId();
		layout.addNode(valueLable);
		valueLable.setFixedWidth(300);
		valueLable.setTitled();

		this.getLayout().addNode(row);
	}

	private void makeValue() {
		BasicNode row = new BasicNode();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		ComplexNode name = new ComplexNode();
		name.setFixedWidth(300);
		this.nameBID = name.getId();
		layout.addNode(name);
		name.makeBorder();
		name.setEditable(true);

		BasicNode value = new BasicNode();
		this.valueCellBID = value.getId();
		value.setFixedWidth(300);
		value.makeBorder();
		layout.addNode(value);

		HorizonalLayout valueLayout = new HorizonalLayout();
		valueLayout.setSpacing(0);
		value.setLayout(valueLayout);

		LabelNode left = new LabelNode();
		this.valueLeftBID = left.getId();
		valueLayout.addNode(left);

		LabelNode logiker = new LabelNode();
		this.logikerBID = logiker.getId();
		valueLayout.addNode(logiker);

		ComplexNode mxParaValue = new ComplexNode();
		mxParaValue.getGeometry().setWidth(50);
		this.valueBID = mxParaValue.getId();
		valueLayout.addNode(mxParaValue);

		this.getLayout().addNode(row);
	}

	@Override
	public NodeNumber getNumber() {
		mxCell cell = this.getCellByBID(this.numberBID);
		if (cell == null) {
			return null;
		}
		Object obj = cell.getValue();
		if (obj instanceof NodeNumber) {
			return (NodeNumber) obj;
		} else {
			return null;
		}
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	// public void setTitle(String title) {
	// this.setName(title);
	// }

	@Override
	public String getName() {

		Object cell = this.getLeft();
		if (cell != null && cell instanceof BVariable) {
			BVariable model = (BVariable) cell;
			return model.getName();
		}
		return null;
	}

	// @Override
	// public void setName(String name) {
	// BValuable cell = this.getLeft();
	//
	// if (cell != null && cell instanceof BVariable) {
	// BVariable model = (BVariable) cell;
	// model.setName(name);
	// }
	// }

	@Override
	public String getLogicName() {
		BValuable cell = this.getLeft();
		if (cell != null && cell instanceof BVariable) {
			BVariable model = (BVariable) cell;
			return model.getLogicName();
		}
		return null;
	}

	// @Override
	// public void setLogicName(String name) {
	// BValuable cell = this.getLeft();
	// if (cell != null && cell instanceof BVariable) {
	// BVariable model = (BVariable) cell;
	// model.setLogicName(name);
	// }
	//
	// }

	@Override
	public String getNodeDesc() {
		return "値を付与する式。新規変数を定義し、値を付与したり、定義済みの変数へ値を付与したりする";
	}

	@Override
	public void setLeft(BValuable left) {
		if (left instanceof ReferNode) {
			ReferNode node = (ReferNode) left;
			ObjectNode obj = new ObjectNode();
			obj.setValue(node);
			((BasicNode) this.getCellByBID(nameBID)).replace(obj);
		} else {
			if (this.getCellByBID(nameBID) == null) {
				Debug.d();
				this.getCellByBID(nameBID);
			}
			((BasicNode) this.getCellByBID(nameBID)).replace((mxCell) left);
		}

	}

	@Override
	public BValuable getLeft() {
		return LayoutUtils.getValueNode(this, nameBID);
	}

	private void removeValueAssignment() {
		mxICell cell = this.getCellByBID(valueLeftBID);
		if (cell != null) {
			cell.removeFromParent();
		}

		cell = this.getCellByBID(logikerBID);
		if (cell != null) {
			cell.removeFromParent();
		}

	}

	private void setValueAssignment(BLogiker logiker) {
		if (logiker == null) {
			this.removeValueAssignment();
			return;
		}
		mxICell cell = this.getCellByBID(valueLeftBID);
		if (cell != null) {
			cell.setValue(this.getLeft());
		}

		cell = this.getCellByBID(logikerBID);
		if (cell != null) {
			cell.setValue(logiker);
		}
	}

	@Override
	public void setRight(BValuable right, BLogiker logiker) {
		if (right == null) {
			this.removeValueAssignment();
			return;
		}
		if (right instanceof BVariable) {
			BVariable var = (BVariable) right;
			if (var.getBClass() != null && var.getBClass().isNullClass()) {
				((BasicNode) this.getCellByBID(valueBID)).replace((mxCell) var);

				this.removeValueAssignment();
				return;
			} else if (var.getBClass() == null) {
				((BasicNode) this.getCellByBID(valueBID)).replace((mxCell) CodecUtils.getNullValue());
				this.removeValueAssignment();
				return;
			}
		}

		if (right instanceof ReferNode) {
			ReferNode refer = (ReferNode) right;
			BasicNode node = LayoutUtils.makeInvokerChild(refer, this, 0, this, false);
			if (node == null) {
				((BasicNode) this.getCellByBID(valueBID)).replace((mxCell) right);
			} else {
				((BasicNode) this.getCellByBID(valueBID)).replace(node);
			}

		} else {
			((BasicNode) this.getCellByBID(valueBID)).replace((mxCell) right);
		}
		this.setValueAssignment(logiker);
	}

	@Override
	public BValuable getRight() {
		return LayoutUtils.getValueNode(this, valueBID);

	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public void onAddSheet(BasicNode node, BeeGraphSheet sheet) {
		if (this.getLeft() instanceof BVariable) {
			BVariable var = (BVariable) this.getLeft();
			if (var.getBClass() == null) {
				BeeName name = BeeNamingUtil.makeName(sheet.getModel(), BeeName.TYPE_VAR);
				var.setName(name.getName());
				var.setLogicName(name.getLogicName());
				var.setBClass(CodecUtils.BString());
				// this.getRight().setBClass(CodecUtils.BString);

				// BeeActions.relayoutClassMember(this, sheet.getGraph());
			}
		}
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_EXPRESSION_ICON;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {

		VerifyHelper.Verify(this, sheet, project);
	}

	public String toString() {
		if (this.getNumber() != null) {
			return this.getNumber().toString();
		} else {
			return "";
		}
	}

	@Override
	public BClass getBClass() {
		return this.getLeft().getBClass();
	}

	@Override
	public void setCast(BValuable cast) {
		this.getLeft().setCast(cast);

	}

	@Override
	public BValuable getCast() {
		return this.getLeft().getCast();
	}

	@Override
	public BValuable getArrayIndex() {
		return this.getLeft().getArrayIndex();
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.getLeft().setArrayIndex(index);
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.getLeft().setArrayObject(object);
	}

	@Override
	public BValuable getArrayObject() {
		return this.getLeft().getArrayObject();
	}

	@Override
	public BLogiker getAssignment() {
		mxICell cell = this.getCellByBID(logikerBID);
		if (cell != null) {
			return (BLogiker) cell.getValue();
		}
		return null;
	}

	@Override
	public BType getParameterizedTypeValue() {
		return this.getLeft().getParameterizedTypeValue();
	}

	@Override
	public void makeDefualtValue(Object target) {
		ComplexNode var = new ComplexNode();
		var.setBClass(CodecUtils.BString().cloneAll());
		var.setLogicName("\"\"");
		var.setName("\"\"");
		this.setRight(var, null);
	}

}
