package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.TransferContainer;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.BDropAction;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BLockNode extends BasicNode implements Serializable, BLogicBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3874180329095824088L;

	public BLockNode() {
		this.getGeometry().setRelative(true);
		mxGeometry editor = new mxGeometry(0, 0, BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH / 4,
				BeeConstants.SEGMENT_EDITOR_DEFAULT_HEIGHT);
		this.setResizeable(true);
		this.setGeometry(editor);
		this.addStyle("textOpacity=0");

		VerticalLayout layout = new VerticalLayout();
		layout.setChildFitWidth(false);
		layout.setSpacing(10);
		this.makeBorder();
		layout.setBetweenSpacing(BeeConstants.HEIGHT_BETWEEN_BLOCK);
		this.setLayout(layout);
		this.setConnectable(false);
	}

	@Override
	public List<BLogicUnit> getUnits() {

		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell cell = this.getChildAt(i);
			if (cell instanceof BLogicUnit) {
				units.add((BLogicUnit) cell);
			}
		}

		return units;
	}

	@Override
	public void setUnits(List<BLogicUnit> units) {
	}

	@Override
	public void addUnit(BLogicUnit unit) {
		getLayout().addNode((BasicNode) unit);

	}

	@Override
	public void addUnit(BLogicUnit unit, int index) {
		getLayout().addNode((BasicNode) unit, index);

	}

	@Override
	public DropAction getDropAction() {
		BDropAction action = new BDropAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onDrop(BasicNode source, BeeGraphSheet sheet, int index) {
				onNodeDrop(source, sheet, index);
			}

			@Override
			public boolean isDropTarget(BasicNode source) {
				if (source instanceof OnewordNode) {
					BLoopUnit loop = findLoop(BLockNode.this);
					if (loop == null) {
						return false;
					} else {
						return true;
					}
				}
				if (source instanceof ReturnNode) {
					if (getParent() != null) {
						if (getParent() instanceof BMethod) {
							return true;
						} else {
							return false;
						}
					}
				}
				if (source.getUserAttribute("MENU_ITEM_TYPE") != null) {
					return true;
				}
				if (source instanceof BMethod) {
					return false;
				}
				if (source instanceof IUnit) {
					return true;
				}
				if (source instanceof BValuable) {
					return true;
				}
				return false;
			}

			@Override
			public BasicNode beforeDrop(BasicNode source, BeeGraphSheet sheet) {
				return beforeNodeDrop(source, sheet);
			}

		};
		return action;
	}

	private BLoopUnit findLoop(mxICell node) {
		if (node instanceof BLoopUnit) {
			return (BLoopUnit) node;
		}
		mxICell parent = node.getParent();
		if (parent != null) {
			return this.findLoop(parent);
		}
		return null;
	}

	public void onNodeDrop(BasicNode source, BeeGraphSheet sheet, int index) {
		if (source instanceof TransferContainer) {
			TransferContainer c = (TransferContainer) source;
			c.toBlock(this, index);
			LayoutUtils.doInvokerLayout(this);
			return;
		}

		if (index >= 0) {
			this.getLayout().addNode(source, -1, index);
		} else {
			this.getLayout().addNode(source);
		}
		if (source instanceof LoopNode) {
			LoopNode loop = (LoopNode) source;
			loop.makeConnector();
			LayoutUtils.doInvokerLayout(source);
		}
		if (source instanceof BAssign) {
			LayoutUtils.doInvokerLayout(source);
		} else if (source instanceof BExpression) {
			LayoutUtils.doInvokerLayout(source);
		}
	}

	public BasicNode beforeNodeDrop(BasicNode source, BeeGraphSheet sheet) {

		return BLockNode.difineVar(source);
	}

	public static BasicNode difineVar(BasicNode source) {
		BValuable value = null;
		if (source instanceof ExpressionNode) {
			ExpressionNode ex = (ExpressionNode) source;
			ex.makeDefaultValue();
			value = ex;
		} else if (source instanceof TrueFalseLineNode) {
			TrueFalseLineNode tf = (TrueFalseLineNode) source;
			tf.makeDefaultValue();
			value = tf;
		} else if (source instanceof BInvoker) {
			value = (BInvoker) source;
			BInvoker bin = (BInvoker) source;
			BValuable child = bin.getInvokeChild();
			if (child instanceof BParameter) {
				AssignmentNode assign = new AssignmentNode();

				BParameter var = (BParameter) child;
				BParameter invokeChild = (BParameter) var.cloneAll();

				assign.setLeft(invokeChild);
				assign.setRight(value, null);
				return assign;
			}
		}

		if (value != null) {
			BClass bclass = value.getBClass();
			if (bclass != null && !bclass.getQualifiedName().equals(BClass.VOID)) {
				AssignmentNode assign = new AssignmentNode();
				ParameterNode node = new ParameterNode();

				node.setBClass(bclass.cloneAll());
				String logicName = "m" + bclass.getLogicName().toLowerCase();
				String name = bclass.getName();
				if (bclass.getQualifiedName().equals(List.class.getName())) {
					List<BType> types = bclass.getParameterizedTypes();
					for (BType type : types) {
						if (type instanceof BClass) {
							BClass b = (BClass) type;
							logicName = "m" + b.getLogicName() + "List";
							name = b.getName() + "リスト";
						}
					}
					// name = name + "リスト";
				}
				node.setLogicName(logicName);
				node.setName(name);
				assign.setLeft(node);
				assign.setRight(value, null);
				return assign;
			}
		}

		if (source instanceof ComplexNode) {
			ComplexNode complex = (ComplexNode) source;
			AssignmentNode assign = new AssignmentNode();

			complex.removeFromParent();

			BClass bclass = complex.getBClass();

			ParameterNode node = new ParameterNode();
			node.setBClass(bclass);
			node.setName(complex.getName());
			node.setLogicName("m" + complex.getLogicName().toLowerCase());
			assign.setLeft(node);
			assign.getGeometry().setRelative(false);
			if (bclass.isArray()) {
				assign.setRight(makeNewArray(node), null);
				return assign;
			}

			if (complex.isCaller()) {
				assign.setRight(complex, null);
			}

			return assign;
		}

		return source;
	}

	private static BVariable makeNewArray(ComplexNode v) {

		BVariable var = PatternCreatorFactory.createView().createVariable();

		BClass bclass = v.getBClass();
		bclass.setArrayPressentClass(bclass.cloneAll());

		var.setBClass(bclass);
		var.setLogicName(bclass.getLogicName());
		var.setName(var.getBClass().getName());

		var.setNewClass(true);

		BVariable d = PatternCreatorFactory.createView().createVariable();
		d.setBClass(CodecUtils.BInt().cloneAll());
		d.setLogicName("1");
		d.setName("1");
		var.addArrayDimension(d);

		return var;
	}

	@Override
	public void clear() {
		this.removeAll();
	}

}
