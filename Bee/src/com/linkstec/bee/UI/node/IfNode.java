package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class IfNode extends BasicNode implements Serializable, IUnit, BLogicUnit, BMultiCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5036324237152513452L;

	private String lastConditionBID, numberBID;

	private String label;

	public IfNode() {
		mxGeometry g = new mxGeometry(0, 0, BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH, 0);
		this.setGeometry(g);
		this.setConnectable(false);
		g.setRelative(true);

		VerticalLayout layout = new VerticalLayout();
		this.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(3);

		this.makeTitle();

		// ConditionNode condition = new ConditionNode();

		// layout.addNode(condition);

		// this.addLastCondtion();
		this.addStyle("textOpacity=0");

	}

	private void makeTitle() {
		BasicNode row = new BasicNode();
		row.setOpaque(false);
		HorizonalLayout h = new HorizonalLayout();
		h.setSpacing(0);
		row.setLayout(h);
		this.getLayout().addNode(row);

		LabelNode number = new LabelNode();
		number.setOpaque(false);
		this.numberBID = number.getId();
		h.addNode(number);

		LabelNode title = new LabelNode();
		title.setOpaque(false);
		title.setValue("以下の通りに分岐処理を実施する");
		h.addNode(title);
	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	public void addConditionAt(int index) {
		ConditionNode c = new ConditionNode();
		this.getLayout().addNode(c, index);
		this.doConditionAdd(c);
	}

	public int getCondtionIndex(ConditionNode node) {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell cell = this.getChildAt(i);
			if (cell instanceof BConditionUnit) {
				ConditionNode unit = (ConditionNode) cell;
				if (unit.equals(node)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void addLastCondtion() {

		boolean add = false;
		if (this.lastConditionBID == null) {
			add = true;
		} else {
			mxCell cell = this.getCellByBID(lastConditionBID);
			if (cell == null) {
				add = true;
			}
		}
		if (add) {
			ConditionNode last = new ConditionNode();
			last.setLast(true);
			this.getLayout().addNode(last);
			lastConditionBID = last.getId();
			this.doConditionAdd(last);
		}
	}

	// for tooltip
	public String getNodeDesc() {
		return "分岐処理、条件が成り立つ場合に処理を行ったり、並べた条件がすべて成り立たない場合に処理を行ったする。";
	}

	@Override
	public List<BConditionUnit> getConditionUnits() {
		List<BConditionUnit> conditions = new ArrayList<BConditionUnit>();
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell cell = this.getChildAt(i);
			if (cell instanceof BConditionUnit) {
				ConditionNode unit = (ConditionNode) cell;

				conditions.add(unit);

			}
		}
		return conditions;
	}

	public void deleteLastConditionUnit() {
		mxCell cell = this.getCellByBID(lastConditionBID);
		if (cell != null) {
			cell.removeFromParent();
		}
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
		return BeeConstants.P_CHOICE_ICON;
	}

	@Override
	public EditAction getAction() {
		List<BConditionUnit> list = this.getConditionUnits();

		boolean hasLast = false;
		for (BConditionUnit unit : list) {
			if (unit.isLast()) {

				hasLast = true;
			}
		}

		AddAction action = new AddAction();

		action.addAction("条件処理を追加する", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4162733734606139397L;

			@Override
			public void call() {
				ConditionNode c = new ConditionNode();
				c.makeDefaultValue();
				getLayout().addNode(c);
				doConditionAdd(c);

			}

		});
		if (!hasLast) {
			action.addAction("最後の処理を追加する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -4162733734606139397L;

				@Override
				public void call() {
					ConditionNode c = new ConditionNode();
					c.setLast(true);
					getLayout().addNode(c);
					doConditionAdd(c);

				}

			});
		}
		return action;

	}

	@Override
	public void clearAllConditionUnit() {
		this.removeAll();
		this.makeTitle();
	}

	@Override
	public void addCondition(BConditionUnit unit) {
		this.getLayout().addNode((BasicNode) unit);
		this.doConditionAdd((ConditionNode) unit);
	}

	public void doConditionAdd(ConditionNode unit) {
		int count = this.getChildCount();
		ConditionNode last = null;
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj.equals(unit)) {
				break;
			}
			if (obj instanceof ConditionNode) {
				last = (ConditionNode) obj;
			}
		}
		if (last != null) {
			last.next(unit);
		}
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		List<BConditionUnit> units = this.getConditionUnits();
		int i = 0;
		for (BConditionUnit unit : units) {
			i++;
			if (unit.isLast()) {
				if (i != units.size()) {
					BasicNode b = (BasicNode) unit;
					b.setAlert("本条件処理は最後にいる必要があります").setType(BAlert.TYPE_ERROR);
				}
			}
		}
	}

	@Override
	public void makeDefualtValue(Object target) {
		this.clearAllConditionUnit();

		ConditionNode condition = new ConditionNode();
		condition.makeDefaultValue();
		this.addCondition(condition);
	}

}
