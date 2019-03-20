package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.BAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.BaseAction;
import com.linkstec.bee.UI.editor.action.DeleteAction;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.UI.spective.detail.logic.VerifyHelper;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class ComplexNode extends BasicNode implements Serializable, ILogic, BVariable {

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		VerifyHelper.Verify(this, sheet, project);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3041455569424753149L;

	private boolean isClass = false;
	private String name, logicName;
	private boolean caller = false;
	private BValuable arrayIndex;
	private BValuable arrayObject;
	private boolean annotation = false;
	private boolean args = false;
	private BClass bclass = null;

	// private List<BObject> annotationParameters = new ArrayList<BObject>();
	private List<BVariable> unionTyps = new ArrayList<BVariable>();

	public ComplexNode() {
		mxGeometry g = new mxGeometry(0, 0, 0, BeeConstants.LINE_HEIGHT);
		g.setRelative(true);
		this.addStyle("verticalAlign=middle");
		this.setOpaque(false);
		this.setGeometry(g);
		this.setConnectable(true);
		this.setEditable(false);
		this.setTextOnly(true);
		this.setDeleteable(false);
	}

	@Override
	public Object getValue() {
		return this.toString();
		// if (this.getBClass() != null) {
		// // when it is new array
		// if (this.arrayIndex != null) {
		// String name = this.arrayObject.toString() + "[" + this.arrayIndex.toString()
		// + "]";
		// if (this.getBClass().getArrayPressentClass() != null) {
		// name = name + "(" + this.getBClass().getArrayPressentClass().getLogicName() +
		// ")";
		// }
		// return name;
		// } else if (this.dimensions.size() != 0) {
		// return "[" + dimensions + "]";
		// }
		// }
		// return this.getLogicName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLogicName() {
		return this.logicName;
	}

	@Override
	public void setLogicName(String name) {
		this.logicName = name;
	}

	@Override
	public String getNodeDesc() {
		return "変数";
	}

	@Override
	public void makeCaller(BClass bclass, boolean self) {
		if (self) {
			this.logicName = "this";
			this.name = "this";
		} else {
			this.logicName = bclass.getLogicName();
			this.name = bclass.getName();
		}
		this.setBClass(bclass);
	}

	@Override
	public String toString() {
		if (super.getValue() != null && !super.getValue().equals("")) {
			return super.getValue().toString();
		}

		// when it is array access
		if (this.arrayIndex != null && this.arrayObject != null) {
			name = arrayObject.toString() + "[" + this.arrayIndex.toString() + "]";
			return name;
		}
		BClass bclass = this.getBClass();
		if (bclass != null) {
			if (bclass.isArray()) {
				List<BValuable> values = this.getInitValues();
				if (values.size() == 0) {

					List<BValuable> list = this.getArrayDimensions();

					String de = "";
					for (BValuable v : list) {
						if (de.equals("")) {
							de = v.toString();
						} else {
							de = "," + v.toString();
						}

					}
					if (isClass) {
						return bclass.getLogicName() + "[]";
					} else {
						return "new " + this.logicName + "[" + de + "]";
					}

				} else {
					String s = "";
					for (int i = 0; i < values.size(); i++) {

						if (i == 0) {
							s = s + values.get(i).toString();
						} else {
							s = s + "," + values.get(i).toString();
						}
					}
					return s;
				}
			}
		}
		return this.logicName;
	}

	@Override
	public boolean isClass() {
		return this.isClass;
	}

	@Override
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}

	@Override
	public void setNewClass(boolean newClass) {
		this.newClass = newClass;
		if (newClass) {
			this.setEditable(false);
		}
	}

	@Override
	public void setCaller(boolean caller) {
		this.caller = caller;
		if (caller) {
			this.setEditable(false);
			this.addStyle("fontColor=blue");
		}
	}

	@Override
	public boolean isCaller() {
		return this.caller;
	}

	private List<BValuable> dimensions = new ArrayList<BValuable>();

	private boolean newClass = false;

	@Override
	public void addArrayDimension(BValuable d) {
		dimensions.add(d);
	}

	@Override
	public List<BValuable> getArrayDimensions() {
		return dimensions;
	}

	@Override
	public void addInitValue(BValuable value) {
		if (value == null) {
			return;
		}
		HorizonalLayout layout = (HorizonalLayout) this.getLayout();
		if (layout == null) {
			layout = new HorizonalLayout();
			layout.setBetweenSpacing(2);
			this.setLayout(layout);
			this.addStyle("textOpacity=0");
		}
		BasicNode b = (BasicNode) value;
		if (value instanceof BInvoker) {
			ObjectNode node = new ObjectNode();
			node.setValue(b);
			b = node;
		}
		b.setRound();
		b.makeBorder();
		this.setNewClass(true);

		BaseAction action = new DeleteAction();

		final BasicNode node = b;
		action.addAction("削除", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4080825068545265371L;

			@Override
			public void call() {
				node.removeFromParent();
			}

		});

		layout.addNode(b);

	}

	@Override
	public List<BValuable> getInitValues() {
		List<BValuable> values = new ArrayList<BValuable>();
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj instanceof BasicNode) {
				BValuable value = LayoutUtils.getValueNode((BasicNode) obj);
				if (value != null) {
					values.add(value);
				}
			}
		}
		return values;
	}

	@Override
	public EditAction getAction() {
		EditAction old = super.getAction();

		MixAction action = new MixAction();
		if (old != null) {
			List<BAction> actions = old.getActions();
			for (BAction a : actions) {
				action.addAction(a.getName(), new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -7347147206640315646L;

					@Override
					public void call() {
						a.act();
					}

				});
			}
		}

		action.addAction("Nullへ変更する", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4080825068545265371L;

			@Override
			public void call() {
				setLogicName("null");
				setName("null");
				setBClass(CodecUtils.BNull.cloneAll());
			}

		});
		BClass bclass = this.getBClass();
		if (bclass != null) {
			if (bclass.isArray()) {
				if (!this.isClass) {
					BType type = bclass.getArrayPressentClass();
					if (type != null && type instanceof BClass) {
						BClass array = (BClass) type;
						action.addAction("初期値追加", new BCall() {

							/**
							 * 
							 */
							private static final long serialVersionUID = 6284054311103552843L;

							@Override
							public void call() {
								addInitValue(ComplexNode.makeDefaultValue(array));
							}

						});
					}
				}
			}
		}
		return action;
	}

	@Override
	public boolean isEditable() {
		if (bclass != null) {
			if (bclass.isPrimitive()) {
				return true;
			}
			if (bclass.getQualifiedName().equals(String.class.getName())) {
				return true;
			}
		}
		return super.isEditable();
	}

	@Override
	public ValueAction getValueAction() {
		if (this.bclass != null) {
			LabelAction action = new LabelAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -8006215568768289346L;

				@Override
				public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
					if (value instanceof String) {

						String s = (String) value;
						if (bclass.getQualifiedName().equals(String.class.getName())) {
							if (ComplexNode.this instanceof ParameterNode) {
							} else {
								if (!s.startsWith("\"")) {
									s = "\"" + s;
								}
								if (!s.endsWith("\"")) {
									s = s + "\"";
								}
							}
						}
						setLogicName(s);
						LayoutUtils.layoutNode(sheet.getGraph().getDefaultParent());
						return true;
					}
					return false;
				}

			};
			return action;
		}
		return null;
	}

	@Override
	public BValuable getArrayIndex() {
		return this.arrayIndex;
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.arrayIndex = index;
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.arrayObject = object;
	}

	@Override
	public BValuable getArrayObject() {
		return this.arrayObject;
	}

	@Override
	public boolean isNewClass() {
		return this.newClass;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.VAR_ICON;
	}

	boolean wildCard = false;

	@Override
	public void setWildCard() {
		wildCard = true;

	}

	@Override
	public boolean isWildCard() {
		return wildCard;
	}

	@Override
	public void setAnnotation(boolean anno) {
		this.annotation = anno;
	}

	@Override
	public boolean isAnnotation() {
		return this.annotation;
	}

	// @Override
	// public void addAnnotationParameter(BObject parameter) {
	// this.annotationParameters.add(parameter);
	// }
	//
	// @Override
	// public List<BObject> getAnnotationParameters() {
	// return this.annotationParameters;
	// }

	private BType type;

	@Override
	public BClass getBClass() {
		if (this.getCast() != null) {
			return this.getCast().getBClass();
		}
		if (bclass != null) {
			// if it is null(Object)
			if (bclass.getQualifiedName() != null) {

				if (bclass.getQualifiedName().equals(Object.class.getName())) {
					if (this.type != null) {
						if (this.type.getParameterizedTypes().size() == 1) {
							BType type = this.type.getParameterizedTypes().get(0);
							if (type instanceof BClass) {
								return (BClass) type;
							} else {
								List<String> list = type.getBounds();
								if (list.size() == 1) {
									String s = list.get(0);
									// Debug.d(s);
								}
							}
						}
					}
				}
			}
		}
		return bclass;
	}

	@Override
	public void addUnionType(BVariable var) {
		this.unionTyps.add(var);
	}

	@Override
	public List<BVariable> getUnionTypes() {
		return this.unionTyps;
	}

	@Override
	public void setBClass(BClass bclass) {
		this.bclass = bclass;
	}

	private BValuable cast;

	private boolean arrayTitled = true;

	@Override
	public void setCast(BValuable cast) {
		this.cast = cast;

	}

	@Override
	public BValuable getCast() {
		return this.cast;
	}

	@Override
	public void setArrayTitle(boolean titled) {
		this.arrayTitled = titled;
	}

	@Override
	public boolean isArrayTitled() {
		return this.arrayTitled;
	}

	@Override
	public BType getParameterizedTypeValue() {
		return this.type;
	}

	@Override
	public void setVarArgs(boolean args) {
		this.args = args;
	}

	@Override
	public boolean isVarArgs() {
		return this.args;
	}

	@Override
	public void setParameterizedTypeValue(BType type) {
		this.type = type;
	}

	public static BValuable makeDefaultValue(BClass bclass) {
		BProject project = Application.getInstance().getCurrentProject();
		if (bclass.isArray()) {
			BClass b = (BClass) bclass.getArrayPressentClass();
			b = b.cloneAll();
			b.setArrayPressentClass(null);
			BValuable value = makeDefaultValue(b);
			ComplexNode node = new ComplexNode();
			node.addInitValue(value);

			ComplexNode d = new ComplexNode();
			d.setBClass(CodecUtils.BInt().cloneAll());

			node.setBClass(bclass);

			node.addArrayDimension(d);
			return node;
		}

		if (bclass.isPrimitive()) {
			ComplexNode node = new ComplexNode();
			node.setBClass(bclass);
			if (bclass.getQualifiedName().equals("boolean")) {
				node.setLogicName("false");
				node.setName("false");
			} else {
				node.setLogicName("0");
				node.setName("0");
			}
			return node;
		} else {
			if (bclass.getQualifiedName().equals(String.class.getName())) {
				ComplexNode node = new ComplexNode();
				node.setBClass(bclass);
				node.setLogicName("\"\"");
				node.setName("\"\"");
				return node;
			} else if (bclass.getQualifiedName().equals(Object.class.getName())) {
				ComplexNode node = new ComplexNode();
				node.setBClass(bclass);
				node.setLogicName("object");
				node.setName("object");
				return node;
			} else {

				List<BConstructor> cons = CodecUtils.getClassConstructors(bclass.getQualifiedName(), project);
				if (cons.size() > 0) {
					BConstructor b = cons.get(0);
					ReferNode node = new ReferNode();
					ComplexNode parent = new ComplexNode();
					parent.makeCaller(bclass, false);
					node.setInvokeParent(parent);
					node.setInvokeChild(b);
					node.makeDefualtValue(null);
					return node;
				}

				Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
				if (cls == null) {
					return CodecUtils.getNullValue();
				}
				if (cls.isAnnotation()) {
					AnnotationNode node = new AnnotationNode();
					node.setBClass(bclass);
					node.setLogicName(bclass.getLogicName());
					return node;
				} else if (cls.isEnum()) {
					Field[] fs = cls.getDeclaredFields();

					if (fs.length > 0) {
						ReferNode node = new ReferNode();
						ComplexNode parent = new ComplexNode();
						parent.setLogicName(bclass.getLogicName());
						parent.setBClass(bclass);
						parent.setClass(true);

						node.setInvokeParent(parent);
						Field f = fs[0];
						String name = f.getName();
						ComplexNode child = new ComplexNode();
						child.setLogicName(name);
						child.setBClass(CodecUtils.getClassFromJavaClass(f.getType(), project));

						node.setInvokeChild(child);
						return node;

					} else {
						return CodecUtils.getNullValue();
					}
				}
				return CodecUtils.getNullValue();
			}
		}
	}

	@Override
	public void clearInitValues() {
		this.removeAll();
	}

	@Override
	public void replaceInitValue(int index, BValuable value) {
		if (value instanceof mxCell) {
			BasicNode node = (BasicNode) this.getChildAt(index);
			node.replace((mxCell) value);
		} else {
			Debug.d();
		}
	}
}
