package com.linkstec.bee.UI.node.view;

import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.Menuable;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ExpressionNode;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxICell;

public class LogikerNode extends BasicNode implements Menuable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1056921312741252770L;

	public EditAction getAction() {
		ExpressionNode p1 = this.findNode(this);
		if (p1 == null) {
			return null;
		}
		mxICell p = p1.getParent();

		if (p != null && p instanceof BasicNode) {
			BasicNode parent = (BasicNode) p;

			int index = parent.getIndex(p1);
			AddAction action = new AddAction();
			action.addAction("右に条件を追加する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -2260374899165623410L;

				@Override
				public void call() {
					ExpressionNode ex = new ExpressionNode();
					p1.setParenthesized(true);
					ex.setExLeft(p1);

					ExpressionNode right = new ExpressionNode();
					right.makeDefaultValue();
					right.setParenthesized(true);
					ex.setExRight(right);

					ex.setExMiddle(BLogiker.LOGICAND);

					parent.insert(ex, index);
					parent.getLayout().layout();
				}

			});
			action.addAction("左に条件を追加する", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -2260374899165623410L;

				@Override
				public void call() {
					ExpressionNode ex = new ExpressionNode();

					ExpressionNode left = new ExpressionNode();
					left.makeDefaultValue();
					left.setParenthesized(true);
					ex.setExLeft(left);

					p1.setParenthesized(true);
					ex.setExRight(p1);

					ex.setExMiddle(BLogiker.LOGICAND);

					parent.insert(ex, index);
					parent.getLayout().layout();
				}

			});

			BLogiker[] logikers = BLogiker.values();
			for (BLogiker l : logikers) {
				ExpressionNode ex = (ExpressionNode) this.getParent();
				action.addAction(l.getLogicName(), new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -2260374899165623410L;

					@Override
					public void call() {
						ex.setExMiddle(l);
						// parent.getLayout().layout();
					}

				});
			}

			return action;
		} else {
			return null;
		}
	}

	private ExpressionNode findNode(mxICell cell) {
		if (cell instanceof ExpressionNode) {
			return (ExpressionNode) cell;
		}
		mxICell p = this.getParent();
		if (p != null) {
			return this.findNode(p);
		}
		return null;
	}

	@Override
	public String getCurrent() {
		Object obj = this.getValue();
		if (obj instanceof BLogiker) {
			BLogiker logiker = (BLogiker) obj;
			return logiker.getLogicName();
		}
		return null;
	}
}
