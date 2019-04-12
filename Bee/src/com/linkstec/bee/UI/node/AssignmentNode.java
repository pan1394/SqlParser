package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.IClassMember;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.ModifierNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.UI.spective.detail.logic.VerifyHelper;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class AssignmentNode extends BasicNode implements Serializable, IClassMember, ILogic, IUnit, BAssignment {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2597786306424975700L;

	private String labelRowBID, nameBID, logicNameBID, valueBID, typeBID, titleLabelBID;
	private String nameLabelBID, logicNameLabelBID, valueLabelBID, typeLabelBID;
	private String label;

	private String numberBID, logikerBID, valueCellBID, valueLeftBID;
	private int typeWidth = 200, nameWidth = 100, logicNameWidth = 150, valueWidth = 150;

	private String modifierBID;

	public AssignmentNode() {
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
		title.setValue("以下の通りに変数を定義する");
		layout.addNode(title);

		this.getLayout().addNode(row);
	}

	private void makeTitle() {
		BasicNode row = new BasicNode();
		labelRowBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7831178739947937592L;

			@Override
			public void beforeContainerLayout() {
				makeModifier((BasicNode) this.getContainer().getParent());
			}

		};
		layout.setSpacing(0);
		row.setLayout(layout);

		LabelNode typeLable = new LabelNode();
		typeLable.setValue("タイプ");
		this.typeLabelBID = typeLable.getId();
		typeLable.setFixedWidth(typeWidth);
		typeLable.setTitled();
		layout.addNode(typeLable);

		LabelNode nameLable = new LabelNode();
		nameLable.setValue("説明");
		this.nameLabelBID = nameLable.getId();
		layout.addNode(nameLable);
		nameLable.setFixedWidth(this.nameWidth);
		nameLable.setTitled();

		LabelNode logicnameLable = new LabelNode();
		logicnameLable.setValue("変数名");
		this.logicNameLabelBID = logicnameLable.getId();
		layout.addNode(logicnameLable);
		logicnameLable.setFixedWidth(this.logicNameWidth);
		logicnameLable.setTitled();

		LabelNode valueLable = new LabelNode();
		valueLable.setValue("値");
		this.valueLabelBID = valueLable.getId();
		layout.addNode(valueLable);
		valueLable.setFixedWidth(this.valueWidth);
		valueLable.setTitled();

		this.getLayout().addNode(row);
	}

	public void removeTitle() {
		this.getCellByBID(titleLabelBID).removeFromParent();
		mxCell cell = this.getCellByBID(labelRowBID);
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			BasicNode node = (BasicNode) cell.getChildAt(i);
			node.addStyle("fillColor=" + BeeConstants.ELEGANT_YELLOW_COLOR);
		}
	}

	private void makeValue() {
		BasicNode row = new BasicNode();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		TypeNode type = new TypeNode(null);
		type.setFixedWidth(this.typeWidth);
		this.typeBID = type.getId();
		layout.addNode(type);
		type.makeBorder();

		BasicNode name = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7831178739947937592L;

			@Override
			public ValueAction getValueAction() {
				LabelAction action = new LabelAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8793505733320489627L;

					@Override
					public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
						if (value instanceof String) {
							String s = (String) value;
							if (!s.equals("")) {
								getLeft().setName(s);
								return true;
							}
						}
						return false;
					}

				};
				return action;
			}

		};
		name.setFixedWidth(this.nameWidth);
		this.nameBID = name.getId();
		layout.addNode(name);
		name.makeBorder();
		name.setEditable(true);

		BasicNode logicname = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7831178739947937592L;

			@Override
			public ValueAction getValueAction() {
				LabelAction action = new LabelAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8676940970227502329L;

					@Override
					public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
						if (value instanceof String) {
							String s = (String) value;
							if (!s.equals("")) {
								getLeft().setLogicName(s);
								return true;
							}
						}
						return false;
					}

				};
				return action;
			}

		};
		logicname.setFixedWidth(this.logicNameWidth);
		this.logicNameBID = logicname.getId();
		layout.addNode(logicname);
		logicname.makeBorder();
		logicname.setEditable(true);

		BasicNode value = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4387708885066696649L;

			@Override
			public EditAction getAction() {
				BParameter left = getLeft();
				if (left != null && left.getBClass() != null) {
					BClass bclass = left.getBClass();
					MixAction action = new MixAction();
					if (!Modifier.isAbstract(bclass.getModifier()) || bclass.isPrimitive()) {
						action.addAction(bclass.getLogicName() + "の値作成", new BCall() {

							/**
							 * 
							 */
							private static final long serialVersionUID = -3058979235000637174L;

							@Override
							public void call() {
								BValuable value = ComplexNode.makeDefaultValue(bclass);
								setRight(value, null);
								if (value instanceof BInvoker) {
									LayoutUtils.doInvokerLayout(AssignmentNode.this);
								}
							}

						});

						return action;
					}
				}
				return null;
			}

		};

		this.valueCellBID = value.getId();
		value.setFixedWidth(this.valueWidth);
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

		ComplexNode mxParaValue = new ComplexNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4008890538091541977L;

			@Override
			public EditAction getAction() {
				return null;
			}

		};
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

		BParameter cell = this.getLeft();
		if (cell != null) {
			return cell.getName();
		}
		return null;
	}

	@Override
	public String getLogicName() {
		BParameter cell = this.getLeft();
		if (cell != null) {
			return cell.getLogicName();
		}
		return null;
	}

	@Override
	public String getNodeDesc() {
		return "新規変数を定義し、値を付与したり、定義済みの変数へ値を付与したりする";
	}

	@Override
	public void setLeft(BParameter left) {

		if (left.getBClass() != null) {
			TypeNode node = (TypeNode) this.getCellByBID(this.typeBID);
			node.setObject(left);
		}
		this.getCellByBID(logicNameBID).setValue(left.getLogicName());
		this.getCellByBID(nameBID).setValue(left.getName());

	}

	@Override
	public BParameter getLeft() {
		TypeNode node = (TypeNode) this.getCellByBID(this.typeBID);
		return (BParameter) node.getObject();
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

	private void makeModifier(BasicNode b) {
		if (!(b.getParent() instanceof BasicNode)) {
			if (b instanceof AssignmentNode) {
				AssignmentNode node = (AssignmentNode) b;
				ModifierNode modifier = null;
				if (node.modifierBID == null) {
					modifier = new ModifierNode(node);
					modifier.getGeometry().getOffset().setY(0);
					node.modifierBID = modifier.getId();
				} else {
					modifier = (ModifierNode) node.getCellByBID(modifierBID);
					if (modifier == null) {
						modifier = new ModifierNode(node);
						modifier.getGeometry().getOffset().setY(0);
						node.modifierBID = modifier.getId();
					}
				}
				if (!modifier.isConnected()) {
					modifier.connect(labelRowBID);
				}
				BParameter left = node.getLeft();
				if (left != null) {
					modifier.setModifier(left.getModifier());
				}
			}
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
			if (node != null) {
				((BasicNode) this.getCellByBID(valueBID)).replace(node);
			} else {
				((BasicNode) this.getCellByBID(valueBID)).replace((mxCell) right);
			}

		} else {
			BasicNode b = ((BasicNode) this.getCellByBID(valueBID));
			if (b == null) {

			} else {
				b.replace((mxCell) right);
			}
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

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ASSIGN_ICON;
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
	public BLogiker getAssignment() {
		mxICell cell = this.getCellByBID(logikerBID);
		if (cell != null) {
			return (BLogiker) cell.getValue();
		}
		return null;
	}

	@Override
	public void makeDefualtValue(Object target) {
		if (target != null && !(target instanceof BasicNode)) {
			BParameter left = this.getLeft();
			if (left != null) {
				left.setModifier(Modifier.PRIVATE);
			}
		}

	}

	@Override
	public void addAnnotation(BAnnotation annotation) {
		this.getLayout().addNode((BasicNode) annotation, 0);
	}

	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell obj = this.getChildAt(i);
			if (obj instanceof BAnnotation) {
				BAnnotation anno = (BAnnotation) obj;

				if (anno.getLogicName().equals(annotion.getLogicName())) {
					obj.removeFromParent();
					break;
				}
			}
		}
	}

	@Override
	public List<BAnnotation> getAnnotations() {
		int count = this.getChildCount();
		List<BAnnotation> list = new ArrayList<BAnnotation>();
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj instanceof BAnnotation) {
				list.add((BAnnotation) obj);
			}
		}
		return list;
	}

	private boolean MehodResotred;

	@Override
	public void setMehodResotred(boolean restored) {
		MehodResotred = restored;
	}

	@Override
	public boolean isMethodRestored() {
		return MehodResotred;
	}

	@Override
	public EditAction getAction() {
		if (this.getParent() instanceof BasicNode) {
			return null;
		} else {
			AddAction action = new AddAction();
			action.addAction("Annotation追加", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -4976548980119073155L;

				@Override
				public void call() {
					AnnotationNode anno = new AnnotationNode();
					anno.setBClass(CodecUtils.getClassFromJavaClass(null, Override.class.getName()));
					anno.setLogicName(Override.class.getSimpleName());
					addAnnotation(anno);

					BasicNode title = (BasicNode) AssignmentNode.this.getCellByBID(valueLabelBID);
					BasicNode value = (BasicNode) AssignmentNode.this.getCellByBID(valueCellBID);
					title.setFixedWidth((int) (BeeConstants.SEGMENT_MAX_WIDTH - 450));
					value.setFixedWidth((int) (BeeConstants.SEGMENT_MAX_WIDTH - 450));
				}

			});
			return action;
		}
	}

}
