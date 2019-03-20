package com.linkstec.bee.UI.node;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.BDropAction;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.UI.spective.detail.edit.TypeAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.impl.BTypeImpl;
import com.mxgraph.model.mxICell;

public class TypeNode extends BasicNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5206190859602478578L;

	private String parasBID, labelBID;
	private boolean paramterAdd = false;
	private BVariable value;

	public TypeNode(BVariable value) {
		this.value = value;
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		layout.setBetweenSpacing(1);
		this.setLayout(layout);
		this.setOpaque(false);
		BasicNode label = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -879158292599253983L;

			@Override
			public boolean isEditable() {
				return isTypeEditable();
			}

			@Override
			public ValueAction getValueAction() {
				if (!isTypeEditable()) {
					return null;
				}

				TypeAction action = new TypeAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8410045472330719103L;

					@Override
					public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
						if (value instanceof BValuable) {
							BValuable v = (BValuable) value;
							setTypeBClass(v.getBClass());
							return true;
						}
						return false;
					}

					@Override
					public boolean canAct(BValuable value) {
						return typeCanChange(value);
					}

				};
				return action;
			}

		};

		label.setEditable(true);
		label.setOpaque(false);

		this.labelBID = label.getId();
		layout.addNode(label);

		if (value == null) {
			return;
		}
		label.setValue(value.getBClass().getLogicName());
		this.setParameter(value.getParameterizedTypeValue());

		this.setEditable(true);
	}

	private boolean isTypeEditable() {
		return super.isEditable();
	}

	@Override
	public void setEditable(boolean editable) {

		super.setEditable(editable);
		BasicNode node = (BasicNode) this.getCellByBID(labelBID);
		if (node != null) {
			node.setEditable(editable);
		}
	}

	@Override
	public DropAction getDropAction() {
		if (!this.isEditable()) {
			return null;
		}
		BDropAction action = new BDropAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -321749208212488705L;

			@Override
			public void onDrop(BasicNode source, BeeGraphSheet sheet, int index) {
				if (source != null && source instanceof BValuable) {
					BValuable v = (BValuable) source;
					BClass bclass = v.getBClass();
					if (bclass != null) {
						setTypeBClass(bclass.cloneAll());
					}
				}
			}

			@Override
			public boolean isDropTarget(BasicNode source) {
				if (value == null) {
					return false;
				}
				if (source != null && source instanceof BValuable) {
					BValuable value = (BValuable) source;
					BClass bclass = value.getBClass();
					if (bclass != null) {
						return true;
					}
				}
				return false;
			}

		};
		return action;
	}

	protected boolean typeCanChange(BValuable value) {
		return true;
	}

	public void setObject(BVariable value) {
		if (value == null) {
			return;
		}
		this.value = value;
		this.getCellByBID(labelBID).setValue(value.getBClass().getLogicName());
		this.setParameter(value.getParameterizedTypeValue());
	}

	public BVariable getObject() {
		return this.value;
	}

	public void setParameter(BType type) {
		if (type == null) {
			return;
		}
		this.makeParameter(type);

		if (this.paramterAdd) {
			BasicNode node = this.getParameterArea();
			this.addLeft(node);
			this.addRight(node);
		}

		if (value.getBClass() != null) {
			if (value.getBClass().isArray()) {
				this.setArray();
			}
		}
	}

	// to be used by the action(if not here,it will call self's setBClass
	private void setTypeBClass(BClass bclass) {
		BObject owner = value.getOwener();
		if (owner != null && owner instanceof BVariable) {
			BVariable var = (BVariable) owner;
			BType type = var.getParameterizedTypeValue();

			BClass b = value.getBClass();
			if (type.equals(b)) {
				var.setBClass(bclass);
			} else {
				List<BType> types = type.getParameterizedTypes();
				for (int i = 0; i < types.size(); i++) {
					BType ot = types.get(i);
					if (ot.equals(b)) {
						types.remove(ot);
						types.add(i, bclass);
						break;
					}
				}
				type.setParameterTypes(types);
			}
			layoutTop();

		}
		value.setBClass(bclass);
		this.getCellByBID(labelBID).setValue(value.getBClass().getLogicName());
	}

	@Override
	public EditAction getAction() {
		if (!this.isEditable()) {
			return null;
		}
		if (value == null) {
			return null;
		}
		BClass bclass = value.getBClass();

		boolean addAction = false;

		if (bclass != null) {
			List<BType> types = bclass.getParameterizedTypes();
			List<BType> parameters = new ArrayList<BType>();

			for (BType type : types) {
				if (type.isTypeVariable() && !type.isParameterValue()) {
					parameters.add(type);
				}
			}

			if (parameters.size() > 0) {
				BType v = value.getParameterizedTypeValue();
				if (v == null) {
					if (parameters.size() > 0) {
						addAction = true;
					}
				}
			}
		}

		if (addAction) {
			AddAction action = new AddAction();
			action.addAction("タイプパラメータ追加", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -636723480301404888L;

				@Override
				public void call() {
					BClass bclass = value.getBClass();
					List<BType> types = bclass.getParameterizedTypes();

					List<BType> parameters = new ArrayList<BType>();

					for (BType type : types) {
						if (type.isTypeVariable() && !type.isParameterValue()) {
							parameters.add(type);
						}
					}

					BTypeImpl impl = new BTypeImpl();
					impl.setContainer();
					for (int i = 0; i < parameters.size(); i++) {
						BType t = CodecUtils.BString().cloneAll();
						t.setParameterValue(true);

						impl.addParameterizedType(t);
					}
					setParaterType(impl);
				}

			});
			return action;
		}
		return null;
	}

	private void setParaterType(BType btype) {

		value.setParameterizedTypeValue(btype);
		this.setParameter(btype);
		BObject owner = value.getOwener();
		if (owner != null && owner instanceof BVariable) {
			value.getBClass().addParameterizedType(btype);
			this.layoutTop();
		}
	}

	private void layoutTop() {
		mxICell parent = this.getParent();
		TypeNode target = null;
		while (parent != null && parent instanceof TypeNode) {
			target = (TypeNode) parent;
			parent = parent.getParent();
		}
		if (target != null) {
			LayoutUtils.layoutNode(target);
		}
	}

	private BasicNode getParameterArea() {
		if (this.parasBID == null) {

			BasicNode node = new BasicNode();
			node.setOpaque(false);

			HorizonalLayout layout = new HorizonalLayout();
			node.setLayout(layout);
			layout.setBetweenSpacing(0);
			layout.setSpacing(0);
			this.parasBID = node.getId();

			this.getLayout().addNode(node);

		}
		return (BasicNode) this.getCellByBID(parasBID);
	}

	private void makeParameter(BType type) {
		if (type.getLogicName() == null) {
			List<BType> types = type.getParameterizedTypes();

			for (BType t : types) {
				this.makeParameter(t);
			}

			return;
		}

		BasicNode node = this.getParameterArea();

		boolean add = false;
		if (type.isRawType()) {
			return;
		}

		if (type.isParameterValue()) {
			if (this.paramterAdd) {
				this.addComma(node);
			}

			if (type instanceof BClass && !type.isRawType()) {

				ComplexNode value = new ComplexNode();
				value.setOwener(this.value);
				value.setBClass((BClass) type);
				TypeNode sub = new TypeNode(value);
				sub.setOpaque(false);
				node.getLayout().addNode(sub);
			} else {
				BasicNode sub = new BasicNode();
				sub.setOpaque(false);
				sub.setEditable(true);
				sub.setValue(type);
				node.getLayout().addNode(sub);
			}
			add = true;
		}

		if (type instanceof BClass && !type.isRawType()) {
			ComplexNode value = new ComplexNode();
			value.setOwener(this.value);
			value.setBClass((BClass) type);

			List<BType> stypes = type.getParameterizedTypes();
			BType btype = new BTypeImpl();

			for (BType bt : stypes) {
				btype.addParameterizedType(bt);
			}
			value.setParameterizedTypeValue(btype);

			TypeNode tn = new TypeNode(value);
			if (tn.paramterAdd) {
				if (this.paramterAdd) {
					this.addComma(node);
				}
				node.getLayout().addNode(tn);
				add = true;
			}
		}

		if (add) {
			this.paramterAdd = true;
		}
		node.getLayout().layout();

	}

	private void addComma(BasicNode node) {
		LabelNode comma = new LabelNode();
		comma.setValue(",");
		comma.setOpaque(false);
		comma.getGeometry().setWidth(10);
		node.getLayout().addNode(comma);
	}

	private void addLeft(BasicNode node) {
		LabelNode left = new LabelNode();
		left.setValue("<");
		left.setOpaque(false);
		left.getGeometry().setWidth(10);
		node.getLayout().addNode(left, 0);
	}

	private void addRight(BasicNode node) {
		LabelNode right = new LabelNode();
		right.setValue(">");
		right.setOpaque(false);
		right.getGeometry().setWidth(10);
		right.addUserAttribute("LAST", right);
		node.getLayout().addNode(right);
	}

	public void setArray() {
		BasicNode node = this.getParameterArea();
		LabelNode comma = new LabelNode();
		comma.setValue("[]");
		comma.setOpaque(false);
		comma.getGeometry().setWidth(10);
		node.getLayout().addNode(comma);
	}
}
