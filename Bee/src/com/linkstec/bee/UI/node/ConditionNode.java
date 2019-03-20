package com.linkstec.bee.UI.node;

import java.io.Serializable;

import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.UI.node.view.Helper;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.Space;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxResources;

public class ConditionNode extends BasicNode implements BConditionUnit, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8712358313600547854L;
	private String condtionLabelBID, conditionBID, editorBID;
	private String elseLable = mxResources.get("lastCondition");
	boolean last = false;

	public ConditionNode() {

		VerticalLayout layout = new VerticalLayout();
		this.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);

		// the condition expression
		this.makeCondition();

		// space
		this.makeSpace();

		// logic body
		this.makeBody();

		// yes connector
		this.makeYesConnector();
	}

	private void makeCondition() {
		BasicNode expression = new BasicNode();
		expression.setRound();
		expression.setYellowTitled();

		expression.addStyle("portConstraint=west");
		this.conditionBID = expression.getId();
		this.getLayout().addNode(expression);
	}

	private void makeBody() {
		BLockNode segment = new BLockNode();
		this.getLayout().addNode(segment);
		this.editorBID = segment.getId();

		BasicNode label = Helper.makeBodyLabel("Action");
		this.condtionLabelBID = label.getId();
		segment.getLayout().addNode(label);
	}

	private void makeSpace() {
		Space space = new Space();
		space.getGeometry().setHeight(50);
		space.getGeometry().setWidth(100);
		this.getLayout().addNode(space);
	}

	private void makeYesConnector() {
		Connector edge = new Connector();
		edge.addStyle("labelBackgroundColor=white");
		edge.setValue("Yes");
		edge.setSource(this.getCellByBID(conditionBID));
		edge.setTarget(this.getCellByBID(this.condtionLabelBID));
		this.insert(edge);
	}

	public LabelNode getLabelTitle() {
		return (LabelNode) this.getCellByBID(condtionLabelBID);
	}

	public BValuable getConditon() {
		return LayoutUtils.getValueNode(this, conditionBID);
	}

	public boolean isLast() {
		return this.last;
	}

	public BLockNode getBody() {
		return (BLockNode) this.getCellByBID(editorBID);
	}

	public void clearConditions() {
		BasicNode con = (BasicNode) this.getCellByBID(conditionBID);

		if (con != null) {
			con.removeAll();
		}
	}

	@Override
	public BValuable getCondition() {
		BasicNode con = (BasicNode) this.getConditionNode();
		if (con == null) {
			return null;
		}
		mxICell cell = con.getChildAt(0);

		return LayoutUtils.getValueNode((BasicNode) cell);
	}

	@Override
	public BLogicBody getLogicBody() {
		return this.getBody();
	}

	@Override
	public void setCondition(BValuable object) {
		BasicNode b = (BasicNode) object;
		b.setOpaque(false);
		BasicNode node = (BasicNode) getConditionNode();
		node.getLayout().addNode(b);
	}

	public mxCell getConditionLabel() {
		return this.getCellByBID(condtionLabelBID);
	}

	public mxCell getConditionNode() {
		return this.getCellByBID(conditionBID);
	}

	public void next(ConditionNode next) {
		mxICell cell = this.getParent();
		Connector edge = new Connector();
		edge.setEntityConnnector();
		edge.setStyle(edge.getStyle() + ";exitX=10");
		edge.setValue("No");
		edge.setSource(this.getCellByBID(conditionBID));

		if (next.isLast()) {
			edge.setTarget(next.getConditionLabel());
		} else {
			edge.setTarget(next.getConditionNode());

		}
		cell.insert(edge);
	}

	@Override
	public void setLast(boolean last) {
		this.last = last;
		if (last) {
			((BasicNode) (this.getCellByBID(this.condtionLabelBID))).setOffsetX(-5);
			mxCell ex = this.getCellByBID(conditionBID);
			if (ex != null) {
				ex.removeFromParent();
			}

		}
	}

	@Override
	public EditAction getAction() {
		mxICell parent = this.getParent();
		if (parent != null && parent instanceof IfNode) {
			IfNode node = (IfNode) parent;
			MixAction action = new MixAction();
			action.addAction("上に条件処理を追加する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void call() {
					ConditionNode c = new ConditionNode();
					c.makeDefaultValue();
					int index = node.getIndex(ConditionNode.this);
					node.getLayout().addNode(c, index);
					node.doConditionAdd(c);

				}

			});
			action.addAction("下に条件処理を追加する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void call() {
					ConditionNode c = new ConditionNode();
					c.makeDefaultValue();
					int index = node.getIndex(ConditionNode.this);
					node.getLayout().addNode(c, index + 1);
					node.doConditionAdd(c);

				}

			});
			action.addAction("この条件処理を削除する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void call() {
					ConditionNode.this.removeFromParent();
				}

			});
			return action;
		}
		return null;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		BObject obj = this.getCondition();
		if (!this.isLast()) {
			if (obj == null) {
				this.setAlert("処理条件が設定されていません").setType(BAlert.TYPE_ERROR);
			}
		}
	}

	public void makeDefaultValue() {
		ExpressionNode ex = new ExpressionNode();
		ex.makeDefaultValue();
		this.setCondition(ex);
	}

}
