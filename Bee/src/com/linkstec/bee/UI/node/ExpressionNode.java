package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LogikerNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class ExpressionNode extends BasicNode implements Serializable, BExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3151178175575062492L;

	private String leftBID, rightBID, middleBID;
	private String parenthesizedLeftBID, parenthesizedRightBID;
	private boolean isParenthesized = false;

	public ExpressionNode() {
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		layout.setBetweenSpacing(5);
		layout.setMaxWidth(BeeConstants.SEGMENT_MAX_WIDTH - BeeConstants.NODE_SPACING * 4);
		this.setLayout(layout);
		this.setOpaque(false);
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);

		BasicNode pleft = new BasicNode();
		pleft.setValue("（");
		pleft.setVisible(false);
		pleft.setOpaque(false);
		this.parenthesizedLeftBID = pleft.getId();

		BasicNode pright = new BasicNode();
		pright.setValue("）");
		pright.setVisible(false);
		pright.setOpaque(false);
		this.parenthesizedRightBID = pright.getId();

		ComplexNode mxLeft = new ComplexNode();
		mxLeft.setOpaque(false);
		this.leftBID = mxLeft.getId();

		ComplexNode mxRight = new ComplexNode();
		this.rightBID = mxRight.getId();
		mxRight.setOpaque(false);

		LogikerNode mxActor = new LogikerNode();
		mxActor.setValue(BLogiker.LOGICAND);
		mxActor.setOpaque(false);
		this.middleBID = mxActor.getId();

		layout.addNode(pleft);
		layout.addNode(mxLeft);
		layout.addNode(mxActor);
		layout.addNode(mxRight);
		layout.addNode(pright);
	}

	@Override
	public BValuable getExLeft() {
		return (BValuable) LayoutUtils.getValueNode(this, leftBID);
	}

	@Override
	public BClass getBClass() {
		if (this.getCast() != null) {
			return this.getCast().getBClass();
		}
		BLogiker logic = this.getExMiddle();
		if (logic.getType().equals(BLogiker.TYPE_BOOLEAN)) {
			return CodecUtils.BBoolean();
		}

		boolean plus = false;

		if (logic.getLogicName().equals(BLogiker.PLUS.getLogicName())) {
			plus = true;
		}

		BValuable obj = this.getExLeft();
		BClass leftBClass = null;
		if (obj != null) {
			leftBClass = obj.getBClass();
		}

		obj = this.getExRight();
		BClass rightBClass = null;
		if (obj != null) {
			rightBClass = obj.getBClass();
		}

		if (plus) {
			String[] ps = { "int", "short", "long", "double", "float", "byte", "char", Integer.class.getName(),
					Short.class.getName(), Long.class.getName(), Double.class.getName(), Float.class.getName(),
					Byte.class.getName(), Character.class.getName() };
			if (rightBClass != null && leftBClass != null) {
				String rb = rightBClass.getQualifiedName();
				String lb = leftBClass.getQualifiedName();

				BClass primative = null;

				for (String p : ps) {
					if (lb.equals(p)) {
						primative = leftBClass;
						break;
					}
				}

				if (primative != null) {

					for (String p : ps) {
						if (rb.equals(p)) {
							return rightBClass;
						}
					}
				}
			}

			return CodecUtils.BString();

		} else {
			if (leftBClass != null) {
				return leftBClass;
			}
			if (rightBClass != null) {

				return rightBClass;

			}
		}
		return null;
	}

	@Override
	public BValuable getExRight() {
		return (BValuable) LayoutUtils.getValueNode(this, rightBID);
	}

	@Override
	public void setExMiddle(BLogiker logiker) {
		if (logiker == null) {
			return;
		}
		this.getCellByBID(this.middleBID).setValue(logiker);
		if (logiker.getLogicName().equals(BLogiker.NOT.getLogicName())) {
			this.getCellByBID(this.rightBID).removeFromParent();
		}
	}

	@Override
	public BLogiker getExMiddle() {
		return (BLogiker) this.getCellByBID(this.middleBID).getValue();
	}

	@Override
	public void setExLeft(BValuable left) {
		BasicNode node = (BasicNode) this.getCellByBID(this.leftBID);
		if (node == null) {
			mxICell l = (mxICell) left;
			this.leftBID = l.getId();
			this.insert(l, 1);
		} else {
			node.replace((mxCell) left);
		}

	}

	@Override
	public void setExRight(BValuable right) {
		BasicNode node = (BasicNode) this.getCellByBID(this.rightBID);
		node.replace((mxCell) right);
	}

	@Override
	public String getNodeDesc() {
		return "計算式、数字ベースの" + "数字計算と成り立つかどうの論理真偽計算と分ける";
	}

	@Override
	public void setParenthesized(boolean flg) {
		this.isParenthesized = flg;
		this.getCellByBID(this.parenthesizedLeftBID).setVisible(flg);
		this.getCellByBID(this.parenthesizedRightBID).setVisible(flg);
	}

	@Override
	public boolean isParenthesized() {
		return this.isParenthesized;
	}

	public String toString() {
		if (isParenthesized) {
			String left = "";
			if (this.getExLeft() != null) {
				left = this.getExLeft().toString();
			}
			String right = "";
			if (this.getExRight() != null) {
				right = this.getExRight().toString();
			}

			return this.getCellByBID(this.parenthesizedLeftBID) + left + this.getExMiddle() + right
					+ this.getCellByBID(this.parenthesizedRightBID);
		} else {
			String left = "";
			if (this.getExLeft() != null) {
				left = this.getExLeft().toString();
			}
			String right = "";
			if (this.getExRight() != null) {
				right = this.getExRight().toString();
			}
			return left + this.getExMiddle() + right;

		}
	}

	@Override
	public ImageIcon getIcon() {
		return null;
	}

	// @Override
	public JComponent getPatternEditor() {

		return null;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		BValuable left = this.getExLeft();
		BClass rbclass = null, lbclass = null;
		if (left == null) {
			this.setAlert("左側が設定されていません").setType(BAlert.TYPE_ERROR);
		} else {
			lbclass = left.getBClass();
			if (lbclass == null) {
				this.setAlert("左側が設定されていません").setType(BAlert.TYPE_ERROR);
			}
		}
		if (!this.getExMiddle().getLogicName().equals(BLogiker.NOT.getLogicName())) {

			BValuable right = this.getExRight();
			if (right == null) {
				this.setAlert("右側が設定されていません").setType(BAlert.TYPE_ERROR);
			} else {
				rbclass = right.getBClass();
				if (rbclass == null) {
					// this.setAlert("右側が設定されていません").setType(BAlert.TYPE_ERROR);
				}

			}
			if (lbclass != null && rbclass != null) {
				if (!lbclass.getQualifiedName().equals(rbclass.getQualifiedName())) {
					this.verifyWhenTypeDiffer();
				}
			}
		}

		if (this.getParent() instanceof BLockNode) {
			this.setAlert("適切な場所ではありません").setType(BAlert.TYPE_ERROR);
		}
	}

	private void verifyWhenTypeDiffer() {
		BLogiker m = this.getExMiddle();
		if (m.getLogicName().equals(BLogiker.DIVIDE.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.GREATTHAN.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.GREATTHANEQUAL.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.INSTANCEOF.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.LESSTHAN.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.LESSTHANEQUAL.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.LOGICAND.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.LOGICOR.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.MINUS.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.MOD.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.MULTIPLY.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.NOT.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.PLUS.getLogicName())) {

		} else if (m.getLogicName().equals(BLogiker.XOR.getLogicName())) {

		}
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
		return null;
	}

	@Override
	public void setArrayIndex(BValuable index) {

	}

	@Override
	public void setArrayObject(BValuable object) {

	}

	@Override
	public BValuable getArrayObject() {
		return null;
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.getCast() != null) {
			return this.getCast().getParameterizedTypeValue();
		}

		BValuable obj = this.getExLeft();
		if (obj != null) {
			return obj.getParameterizedTypeValue();
		}

		obj = this.getExRight();
		if (obj != null) {
			return obj.getParameterizedTypeValue();
		}
		return null;
	}

	public void makeDefaultValue() {
		BValuable left = this.getExLeft();
		if (left == null || left.getBClass() == null) {
			ComplexNode node = new ComplexNode();
			node.setBClass(CodecUtils.BString().cloneAll());
			node.setLogicName("\"\"");
			node.setName("\"\"");
			this.setExLeft(node);
			this.setExMiddle(BLogiker.NOTQUEAL);
			this.setExRight(CodecUtils.getNullValue());
		}
	}
}
