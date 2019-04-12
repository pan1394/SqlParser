package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.UI.node.view.Helper;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.Space;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class LoopNode extends BasicNode implements Serializable, IUnit, BLoopUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8427599729828913300L;

	private String conditionBID, EditorBID, numberBID, enhanceListBID, forLoopAssignBID, forLoopUpdatesBID;
	private int type = TYPE_WHILE;

	private String label;
	private String bodyLabelBID;
	private Connector connector;

	public LoopNode() {
		VerticalLayout layout = new VerticalLayout();

		layout.setSpacing(0);
		this.setLayout(layout);
		this.setOpaque(false);

		mxGeometry g = new mxGeometry(0, 0, BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH, 0);
		this.setGeometry(g);

		// title
		this.makeTitle();

		// condition
		this.makeCondition();

		// body
		this.makeBody();

		// connector
		this.makeConnector();
	}

	private void makeTitle() {
		BasicNode row = new BasicNode();
		HorizonalLayout rowLayout = new HorizonalLayout();
		row.setLayout(rowLayout);
		this.makeNumberedLabel(rowLayout);
		this.getLayout().addNode(row);

		BasicNode mxTitle = new BasicNode();
		mxTitle.setRelative();
		mxTitle.setValue("以下のようにループ処理を実施する。");
		rowLayout.addNode(mxTitle);
	}

	private void makeCondition() {
		BasicNode row = new BasicNode();
		row.setRound();
		row.setYellowTitled();

		this.conditionBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);
		this.getLayout().addNode(row);

		int index = this.getIndex(row);
		this.makeSpace(index + 1);
	}

	private void makeSpace(int index) {
		Space space = new Space();
		space.getGeometry().setHeight(50);
		space.getGeometry().setWidth(100);
		this.getLayout().addNode(space, index);
	}

	private void makeBody() {
		BLockNode editor = new BLockNode();
		this.EditorBID = editor.getId();
		this.getLayout().addNode(editor);

		BasicNode label = Helper.makeBodyLabel("Each Action");
		label.getGeometry().setWidth(150);
		this.bodyLabelBID = label.getId();

		editor.getLayout().addNode(label);

		ComplexNode node = new ComplexNode();
		this.addCondition(node);
	}

	public void makeConnector() {
		// if (connector == null) {
		connector = new Connector();
		connector.setValue("Do Loop");
		connector.setSource(this.getCellByBID(this.conditionBID));
		connector.setTarget(this.getCellByBID(this.bodyLabelBID));
		connector.addStyle("labelBackgroundColor=white");
		this.insert(connector);
		// }
	}

	public void removeConnector() {
		connector.removeFromParent();
	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

	}

	private BasicNode getConditionNode() {
		return (BasicNode) this.getCellByBID(conditionBID);
	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	public void clearCondition() {
		BasicNode node = this.getConditionNode();
		node.removeAll();

	}

	public BLockNode getEditor() {
		return (BLockNode) this.getCellByBID(EditorBID);
	}

	@Override
	public BValuable getCondition() {
		BasicNode c = this.getConditionNode();
		if (c.getChildCount() > 0) {
			BasicNode node = (BasicNode) c.getChildAt(0);
			return LayoutUtils.getValueNode(node);
		} else {
			return null;
		}

	}

	@Override
	public void addCondition(BValuable object) {
		BasicNode node = (BasicNode) object;
		node.setOpaque(false);
		this.getConditionNode().getLayout().addNode(node);
	}

	@Override
	public String getNodeDesc() {
		String s = "ループ処理、";
		if (this.type == BLoopUnit.TYPE_ENHANCED) {
			s = s + "リストに対して中身データ別の処理を行う";
		} else if (type == BLoopUnit.TYPE_FORLOOP) {
			s = s + "カウンター持ち式の処理を行う";
		} else {
			s = s + "単純条件による処理を行う";
		}
		return s;
	}

	@Override
	public void addEnhancedCondition(BParameter variable, BValuable expression) {
		this.clearCondition();
		this.type = TYPE_ENHANCED;
		// BasicNode ex = (BasicNode) expression;
		this.addCondition(expression);

		BasicNode row = new BasicNode();
		// row.setYellowTitled();

		this.enhanceListBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		layout.addNode((BasicNode) variable);
		this.getLayout().addNode(row, 1);

		this.getCellByBID(bodyLabelBID).setValue("Each Data");

		this.makeSpace(2);

		Connector c = new Connector();
		c.addStyle("labelBackgroundColor=white");
		this.insert(c);
		c.setSource(this.getConditionNode());
		c.setTarget(row);

	}

	@Override
	public void setEditor(BLogicBody body) {
		this.getEditor().replace((mxCell) body);
	}

	@Override
	public BParameter getEnhanceVariable() {
		return (BParameter) this.getCellByBID(enhanceListBID).getChildAt(0);
	}

	@Override
	public BValuable getEnhanceExpression() {
		return this.getCondition();
	}

	@Override
	public int getLoopType() {
		return this.type;
	}

	@Override
	public List<BAssign> getForLoopInitializers() {
		List<BAssign> list = new ArrayList<BAssign>();
		mxCell node = this.getCellByBID(this.forLoopAssignBID);
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = node.getChildAt(i);
			if (obj instanceof BAssign) {
				list.add((BAssign) obj);
			}
		}

		return list;
	}

	@Override
	public void setForLoopInitializers(List<BAssign> assigns) {
		this.clearCondition();
		this.type = TYPE_FORLOOP;

		BasicNode row = new BasicNode();
		// row.setYellowTitled();

		this.forLoopAssignBID = row.getId();
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);
		this.getLayout().addNode(row, 1);

		this.makeSpace(2);

		for (BAssign a : assigns) {
			layout.addNode((BasicNode) a);
		}

		Connector c = new Connector();
		this.insert(c);
		c.setSource(row);
		c.setTarget(this.getCellByBID(conditionBID));
	}

	@Override
	public void setUpdats(List<BValuable> updates) {
		this.type = TYPE_FORLOOP;

		BasicNode row = new BasicNode();

		this.forLoopUpdatesBID = row.getId();
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);
		BasicNode label = (BasicNode) this.getCellByBID(bodyLabelBID);
		label.insert(row);
		label.addStyle("textOpacity=0");

		for (BObject a : updates) {
			BasicNode node = (BasicNode) a;
			layout.addNode(node);
		}

	}

	@Override
	public List<BValuable> getUpdates() {
		List<BValuable> list = new ArrayList<BValuable>();
		mxCell node = this.getCellByBID(forLoopUpdatesBID);
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = node.getChildAt(i);
			if (obj instanceof BValuable) {
				list.add((BValuable) obj);
			}
		}

		return list;
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
	public void setLoopType(int type) {
		this.type = type;
	}

	@Override
	public void makeDefualtValue(Object target) {

		this.clearCondition();

		this.makeConnector();

		if (type == TYPE_WHILE) {
			ExpressionNode ex = new ExpressionNode();
			ex.makeDefaultValue();
			this.addCondition(ex);
		} else if (type == TYPE_ENHANCED) {

			ParameterNode node = new ParameterNode();
			BClass value = CodecUtils.BString().cloneAll();
			node.setBClass(value);
			node.setLogicName("data");

			BClass list = CodecUtils.getClassFromJavaClass(Application.getInstance().getCurrentProject(),
					List.class.getName());

			ComplexNode listNode = new ComplexNode();
			listNode.setBClass(list);
			listNode.setParameterizedTypeValue(value);
			listNode.setLogicName("list");

			this.addEnhancedCondition(node, listNode);
		} else if (type == TYPE_FORLOOP) {
			AssignmentNode assign = new AssignmentNode();

			ComplexNode value = new ComplexNode();
			value.setBClass(CodecUtils.BInt().cloneAll());
			value.setName("0");
			value.setLogicName("0");
			assign.setRight(value, null);

			ParameterNode define = new ParameterNode();
			define.setBClass(CodecUtils.BInt().cloneAll());
			define.setName("カウンタ");
			define.setLogicName("i");
			assign.setLeft(define);

			List<BAssign> inits = new ArrayList<BAssign>();
			inits.add(assign);
			this.setForLoopInitializers(inits);

			List<BValuable> updates = new ArrayList<BValuable>();
			BSingleExpressionUnit exp = new SingleExpressionNode();
			exp.setOperator(BSingleExpressionUnit.INCREMENT);
			exp.setVariable((BVariable) define.cloneAll());
			updates.add(exp);

			this.setUpdats(updates);
			ExpressionNode ex = new ExpressionNode();
			ex.setExLeft((BValuable) define.cloneAll());
			ex.setExMiddle(BLogiker.LESSTHAN);

			ComplexNode right = new ComplexNode();
			right.setBClass(CodecUtils.BInt().cloneAll());
			right.setName("5");
			right.setLogicName("5");
			ex.setExRight(right);

			this.addCondition(ex);
		}
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_LOOP_ICON;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		int type = this.getLoopType();
		if (type == BLoopUnit.TYPE_DOWHILE) {

		} else if (type == BLoopUnit.TYPE_ENHANCED) {
			BObject con = this.getEnhanceExpression();
			if (con == null) {
				this.setAlert("ループ処理の条件が設定されていません").setType(BAlert.TYPE_ERROR);
			} else {
				BParameter asign = this.getEnhanceVariable();
				if (asign == null) {
					this.setAlert("ループ処理の条件が正しく設定されていません").setType(BAlert.TYPE_ERROR);
				}
			}
		} else if (type == BLoopUnit.TYPE_FORLOOP) {
			List<BAssign> inits = this.getForLoopInitializers();
			if (inits == null || inits.size() == 0) {
				this.setAlert("ループ処理の条件が設定されていません").setType(BAlert.TYPE_ERROR);
			}
			BObject con = this.getCondition();
			if (con == null) {
				this.setAlert("ループ処理の条件が設定されていません").setType(BAlert.TYPE_ERROR);
			} else {
				// TODO steps
			}

		} else if (type == BLoopUnit.TYPE_WHILE) {
			BObject con = this.getCondition();
			if (con == null) {
				this.setAlert("ループ処理の条件が設定されていません").setType(BAlert.TYPE_ERROR);
			}
		} else {
			this.setAlert("ループ処理のタイプが正しくありません").setType(BAlert.TYPE_ERROR);
		}
	}

}
