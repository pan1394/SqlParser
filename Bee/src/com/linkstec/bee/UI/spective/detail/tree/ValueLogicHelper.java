package com.linkstec.bee.UI.spective.detail.tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.AssignExpressionNode;
import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ConstructorNode;
import com.linkstec.bee.UI.node.ExpressionNode;
import com.linkstec.bee.UI.node.LoopNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.node.ParameterNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.ReturnNode;
import com.linkstec.bee.UI.node.SingleExpressionNode;
import com.linkstec.bee.UI.node.TrueFalseLineNode;
import com.linkstec.bee.UI.node.view.TransferContainer;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.linkstec.bee.core.fw.logic.BSwitchUnit;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.mxgraph.model.mxICell;

public class ValueLogicHelper {
	public static void createFieldLogic(Object var, ValueNode parent, DefaultTreeModel model, Class<?> cls) {
		if (cls != null) {
			ValueLogicHelper.createFieldAssignExpressionNode(var, parent, model, cls);
			if (cls.isPrimitive() || Number.class.isAssignableFrom(cls)) {
				ValueLogicHelper.createFieldDecreaseNode(var, parent, model, cls);
				ValueLogicHelper.createFieldIncreaseNode(var, parent, model, cls);
			}
			ValueLogicHelper.createExpressionNode(var, parent, model, cls);
			ValueLogicHelper.createFieldReturnNode(var, parent, model, cls);
		}
	}

	public static ValueNode createFieldAssignExpressionNode(Object var, ValueNode parent, DefaultTreeModel model,
			Class<?> cls) {

		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_ASSIGNEXPRESSION);
		panel.setImageIcon(BeeConstants.P_ASSIGN_ICON);
		panel.setUserObject(var);
		panel.setDisplay("値を変更する");
		model.insertNodeInto(panel, parent, 0);
		return panel;

	}

	public static ValueNode createFieldReturnNode(Object var, ValueNode parent, DefaultTreeModel model, Class<?> cls) {

		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_RETURN);
		panel.setImageIcon(BeeConstants.P_RETURN_ICON);
		panel.setUserObject(var);
		panel.setDisplay("この値をメソッドへ返す");
		model.insertNodeInto(panel, parent, 0);
		return panel;

	}

	public static ValueNode createFieldAssignExpressionLineNode(Object var, ValueNode parent, DefaultTreeModel model,
			Class<?> cls) {

		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_EXPRESSION_LINE);
		panel.setImageIcon(BeeConstants.P_TRUEFALSE_ICON);
		panel.setUserObject(var);
		panel.setDisplay("値を判断した上に変更する");
		model.insertNodeInto(panel, parent, 0);
		return panel;

	}

	public static ValueNode createFieldIncreaseNode(Object var, ValueNode parent, DefaultTreeModel model,
			Class<?> cls) {

		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_INCREMENT);
		panel.setImageIcon(BeeConstants.P_INCREASE_ICON);
		panel.setUserObject(var);
		panel.setDisplay("インクリメント(++)する");
		model.insertNodeInto(panel, parent, 0);
		return panel;

	}

	public static ValueNode createFieldDecreaseNode(Object var, ValueNode parent, DefaultTreeModel model,
			Class<?> cls) {

		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_DECREMENT);
		panel.setImageIcon(BeeConstants.P_DECREASE_ICON);
		panel.setUserObject(var);
		panel.setDisplay("デクリメント(--)する");
		model.insertNodeInto(panel, parent, 0);
		return panel;

	}

	public static ValueNode createExpressionNode(Object var, ValueNode parent, DefaultTreeModel model, Class<?> cls) {
		ValueNode panel = new ValueNode();
		panel.setLogicType(ValueNode.LOGIC_EXPRESSION);
		panel.setImageIcon(BeeConstants.P_EXPRESSION_ICON);
		panel.setUserObject(var);
		panel.setDisplay("当該値で計算する");
		model.insertNodeInto(panel, parent, 0);
		return panel;
	}

	public static ValueNode createLoopNode(Class<?> cls, ValueNode parent, DefaultTreeModel model) {

		if (cls.isArray()) {
			// Class<?> type = cls.getComponentType();
			ValueNode panel = new ValueNode();
			panel.setLogicType(ValueNode.LOGIC_LOOP_ENHANCED);
			panel.setImageIcon(BeeConstants.P_LOOP_ICON);
			panel.setUserObject(cls);
			panel.setLeaf(false);
			panel.setDisplay("ループ処理実施");

			model.insertNodeInto(panel, parent, 0);

			return panel;
		} else {
			if (Map.class.isAssignableFrom(cls)) {
				ValueNode panel = new ValueNode();
				panel.setLogicType(ValueNode.LOGIC_LOOP_ENHANCED);
				panel.setImageIcon(BeeConstants.P_LOOP_ICON);
				panel.setUserObject(cls);
				panel.setLeaf(false);
				panel.setDisplay("ループ処理実施");

				model.insertNodeInto(panel, parent, 0);

				return panel;
			} else if (List.class.isAssignableFrom(cls)) {
				ValueNode panel = new ValueNode();
				panel.setLogicType(ValueNode.LOGIC_LOOP_ENHANCED);
				panel.setImageIcon(BeeConstants.P_LOOP_ICON);
				panel.setUserObject(cls);
				panel.setLeaf(false);
				panel.setDisplay("ループ処理実施");
				model.insertNodeInto(panel, parent, 0);

				return panel;
			}
		}
		return null;
	}

	public static ValueNode createDataFieldNode(Field f, ValueNode parent, DefaultTreeModel model) {
		ValueNode panel = new ValueNode();
		panel.setDataVar(true);
		panel.setImageIcon(BeeConstants.VAR_COLUMN_CELL_ICON);
		panel.setUserObject(f);
		panel.setLeaf(false);
		model.insertNodeInto(panel, parent, parent.getChildCount());
		return panel;
	}

	public static ValueNode createFieldNode(Field f, ValueNode parent, DefaultTreeModel model) {
		ValueNode panel = new ValueNode();

		if (Modifier.isStatic(f.getModifiers())) {
			panel.setImageIcon(BeeConstants.VAR_STATIC_ICON);
		} else if (Modifier.isProtected(f.getModifiers())) {
			panel.setImageIcon(BeeConstants.VAR_PROTECED_ICON);
		} else if (Modifier.isPrivate(f.getModifiers())) {
			panel.setImageIcon(BeeConstants.VAR_PRIVATE_ICON);
		} else {
			panel.setImageIcon(BeeConstants.VAR_ICON);
		}
		panel.setUserObject(f);
		panel.setLeaf(false);
		model.insertNodeInto(panel, parent, parent.getChildCount());
		return panel;
	}

	public static void createFieldMembers(Field f, ValueNode parent, DefaultTreeModel model) {
		Class<?> cls = f.getType();

		Method[] methods = cls.getMethods();

		ValueLogicHelper.createFieldLogic(f, parent, model, f.getType());
		ValueLogicHelper.createClassStaticMembers(cls, parent, model, false);
		for (Method m : methods) {
			ValueLogicHelper.createMethodNode(m, parent, model);
		}

	}

	public static ValueNode createClassNode(String className, ValueNode parent, DefaultTreeModel model) {
		ValueNode item = new ValueNode();
		item.setUserObject(className);
		item.setImageIcon(BeeConstants.CLASSES_ICON);
		model.insertNodeInto(item, parent, 0);

		return item;
	}

	private static ValueNode createArrayNode(String className, ValueNode parent, DefaultTreeModel model) {
		ValueNode item = new ValueNode();
		item.setUserObject(className);
		item.setLogicType(ValueNode.LOGIC_ARRAY);
		item.setImageIcon(BeeConstants.ARRAY_ICON);
		model.insertNodeInto(item, parent, 0);

		return item;
	}

	public static ValueNode createMethodNode(Method m, ValueNode parent, DefaultTreeModel model) {
		ValueNode panel = new ValueNode();

		if (Modifier.isStatic(m.getModifiers())) {
			panel.setImageIcon(BeeConstants.METHOD_STATIC_ICON);
		} else if (Modifier.isProtected(m.getModifiers())) {
			panel.setImageIcon(BeeConstants.METHOD_PROTECED_ICON);
		} else {
			panel.setImageIcon(BeeConstants.METHOD_ICON);
		}
		panel.setUserObject(m);
		panel.setLeaf(false);
		model.insertNodeInto(panel, parent, parent.getChildCount());
		return panel;
	}

	public static ValueNode createConstructorNode(Constructor<?> m, ValueNode parent, DefaultTreeModel model) {
		ValueNode panel = new ValueNode();
		panel.setImageIcon(BeeConstants.INITIALIZE_ICON);
		panel.setUserObject(m);
		model.insertNodeInto(panel, parent, 0);
		return panel;
	}

	public static void createClassAllPublicMembers(Class<?> cls, ValueNode item, DefaultTreeModel model) {

		Method[] methods = cls.getMethods();
		boolean data = CodecUtils.isData(cls);
		if (!data) {
			for (Method m : methods) {
				ValueLogicHelper.createMethodNode(m, item, model);
			}
			Field[] fs = cls.getFields();
			for (Field f : fs) {
				String name = f.getName();
				if (!name.equals("serialVersionUID")) {
					ValueLogicHelper.createFieldNode(f, item, model);
				}
			}
		} else {
			Field[] fs = cls.getDeclaredFields();
			for (Field f : fs) {
				String name = f.getName();
				if (!name.equals("serialVersionUID")) {
					ValueLogicHelper.createDataFieldNode(f, item, model);
				}
			}
			Class<?> sclass = cls.getSuperclass();
			if (sclass != null && !sclass.equals(Object.class)) {
				createClassAllPublicMembers(sclass, item, model);
			}
		}
		ValueLogicHelper.createLoopNode(cls, item, model);
	}

	public void createNoneStaticPublicMembers(Class<?> cls, ValueNode item, DefaultTreeModel model) {
		Method[] methods = cls.getMethods();
		boolean data = CodecUtils.isData(cls);
		if (!data) {
			for (Method m : methods) {
				if (!Modifier.isStatic(m.getModifiers())) {
					ValueLogicHelper.createMethodNode(m, item, model);
				}
			}
			Field[] fs = cls.getFields();
			for (Field f : fs) {
				String name = f.getName();
				if (!Modifier.isStatic(f.getModifiers())) {
					if (!name.equals("serialVersionUID")) {
						ValueLogicHelper.createFieldNode(f, item, model);
					}
				}
			}
		} else {
			Field[] fs = cls.getDeclaredFields();
			for (Field f : fs) {
				String name = f.getName();
				if (!name.equals("serialVersionUID")) {
					if (!Modifier.isStatic(f.getModifiers())) {
						ValueLogicHelper.createDataFieldNode(f, item, model);
					}
				}
			}
			Class<?> sclass = cls.getSuperclass();
			if (sclass != null && !sclass.equals(Object.class)) {
				createNoneStaticPublicMembers(sclass, item, model);
			}
		}
		ValueLogicHelper.createLoopNode(cls, item, model);
	}

	public static void createClassStaticMembers(Class<?> cls, ValueNode item, DefaultTreeModel model,
			boolean makeConstructor) {
		if (cls != null) {

			Method[] methods = cls.getMethods();
			for (Method m : methods) {
				if (Modifier.isStatic(m.getModifiers())) {
					ValueLogicHelper.createMethodNode(m, item, model);
				}
			}
			Field[] fs = cls.getFields();
			for (Field f : fs) {
				if (Modifier.isStatic(f.getModifiers())) {
					String name = f.getName();
					if (!name.equals("serialVersionUID")) {
						ValueLogicHelper.createFieldNode(f, item, model);
					}
				}
			}
			if (makeConstructor) {
				Constructor<?>[] cons = cls.getConstructors();
				for (Constructor<?> m : cons) {
					ValueLogicHelper.createConstructorNode(m, item, model);
				}
			}
		}
	}

	public static void expand(ValueNode node, DefaultTreeModel model, BProject project) {
		if (node.isInited()) {
			return;
		}
		if (node.getLogicType() != -1) {
			ValueLogicHelper.expandLogicNode(node, model, project);
			return;
		}
		node.setInited(true);
		Object obj = node.getUserObject();
		if (obj instanceof String) {
			String className = (String) obj;
			if (className.equals(BClass.NULL)) {
				ValueLogicHelper.createFieldReturnNode(null, node, model, null);
			} else {
				Class<?> cls = CodecUtils.getClassByName(className, project);
				node.setUserObject(cls);
				ValueLogicHelper.createClassStaticMembers(cls, node, model, true);
				ValueLogicHelper.createArrayNode(className, node, model);
			}

		} else if (obj instanceof BMethod) {
			BMethod s = (BMethod) obj;
			List<BParameter> paras = s.getParameter();
			if (paras != null && !paras.isEmpty()) {
				for (BParameter para : paras) {
					ValueNode p = ValueLogicHelper.createCaller(para, node, obj);
					p.setImageIcon(BeeConstants.VAR_PARAMETER_ICON);
				}
			}
			makeBlockNodes(s.getLogicBody(), node);
		}

		if (obj instanceof BValuable) {
			BValuable var = (BValuable) obj;
			expandValue(var, node, project, model);
		} else if (obj instanceof BAssignment) {
			BAssignment f = (BAssignment) obj;
			BValuable var = f.getLeft();
			expandValue(var, node, project, model);
		} else {
			ValueLogicHelper.expandNode(node, model);
		}
	}

	private static void expandLogicNode(ValueNode node, DefaultTreeModel model, BProject project) {
		// if (node.getLogicType() == ValueNode.LOGIC_LOOP_ENHANCED) {
		// TransferContainer container = (TransferContainer) node.getTransferNode();
		// int count = container.getChildCount();
		// for (int i = 0; i < count; i++) {
		// mxICell child = container.getChildAt(i);
		// if (child instanceof LoopNode) {
		// LoopNode loop = (LoopNode) child;
		// if (loop.getLoopType() == BLoopUnit.TYPE_WHILE) {
		// BLockNode para = loop.getEditor();
		// ValueLogicHelper.makeBlockNodes(para, node);
		// } else if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {
		// BParameter para = loop.getEnhanceVariable();
		// createVarNode(para, node);
		// }
		// }
		// }
		// }
	}

	public static ValueNode createVarNode(BVariable cell, ValueNode parent) {
		if (cell == null)
			return null;

		ValueNode c = new ValueNode();
		c.setUserObject(cell);
		BClass view = cell.getBClass();
		c.setLeaf(view.isPrimitive());
		parent.add(c);
		c.setLeaf(false);
		return c;
	}

	public static void makeBlockNodes(BLogicBody cell, ValueNode parent) {
		if (cell == null)
			return;

		if (cell instanceof BLogicBody) {

			BLogicBody s = (BLogicBody) cell;
			List<BLogicUnit> units = s.getUnits();
			for (BLogicUnit unit : units) {
				makeUnitNodes(unit, parent);
			}
		}

	}

	private static void makeUnitNodes(BLogicUnit cell, ValueNode parent) {
		if (cell == null)
			return;

		if (cell instanceof BLogicUnit) {

			BLogicUnit s = (BLogicUnit) cell;
			if (s instanceof BMultiCondition) {
				BMultiCondition bmt = (BMultiCondition) s;
				List<BConditionUnit> cons = bmt.getConditionUnits();
				for (BConditionUnit unit : cons) {
					makeBlockNodes(unit.getLogicBody(), parent);
				}
			} else if (s instanceof BTryUnit) {
				BTryUnit be = (BTryUnit) s;
				makeBlockNodes(be.getTryEditor(), parent);
				List<BCatchUnit> bcts = be.getCatches();
				for (BCatchUnit bct : bcts) {
					BParameter parameter = bct.getVariable();
					ValueLogicHelper.createCaller(parameter, parent, s);
					makeBlockNodes(bct.getEditor(), parent);
				}
			} else if (s instanceof BAssignment) {
				BAssignment be = (BAssignment) s;
				BParameter parameter = be.getLeft();
				ValueNode node = ValueLogicHelper.createCaller(parameter, parent, s);
				if (be instanceof BasicNode) {
					BasicNode b = (BasicNode) be;
					mxICell p = b.getParent();
					if (p != null) {
						if (p instanceof BasicNode) {
							node.setValueScope(p);
						} else {
							b.setValue(null);
						}
					} else {
						b.setValue(null);
					}
				}
			} else if (s instanceof BLogicBody) {
				BLogicBody body = (BLogicBody) s;
				makeBlockNodes(body, parent);
			} else if (s instanceof BSwitchUnit) {
				BSwitchUnit sw = (BSwitchUnit) s;
				List<BConditionUnit> list = sw.getConditionUnits();
				for (BConditionUnit unit : list) {
					makeBlockNodes(unit.getLogicBody(), parent);
				}
			} else if (s instanceof BLoopUnit) {
				BLoopUnit loop = (BLoopUnit) s;
				if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {
					BParameter parameter = loop.getEnhanceVariable();
					ValueLogicHelper.createCaller(parameter, parent, s);
				} else if (loop.getLoopType() == BLoopUnit.TYPE_FORLOOP) {
					List<BAssign> inits = loop.getForLoopInitializers();
					for (BAssign a : inits) {
						if (a instanceof BAssignment) {
							BAssignment assin = (BAssignment) a;
							BParameter parameter = assin.getLeft();
							ValueLogicHelper.createCaller(parameter, parent, s);
						}
					}
				}
				makeBlockNodes(loop.getEditor(), parent);
			}

		}
	}

	private static ValueNode createCaller(BParameter parameter, ValueNode parent, Object scope) {
		if (parameter != null) {
			parameter = (BParameter) parameter.cloneAll();
			parameter.setCaller(true);
			ValueNode node = createVarNode(parameter, parent);
			node.setImageIcon(BeeConstants.VAR_LOCAL_ICON);
			node.setValueScope(scope);
			return node;
		}
		return null;
	}

	private static void expandValue(BValuable var, ValueNode parent, BProject project, DefaultTreeModel model) {
		if (var.getBClass() != null) {
			String className = var.getBClass().getQualifiedName();
			Class<?> cls = CodecUtils.getClassByName(className, project);
			ValueLogicHelper.createClassAllPublicMembers(cls, parent, model);
			if (var instanceof BVariable) {
				ValueLogicHelper.createFieldLogic((BVariable) var, parent, model, cls);
			}
		}
	}

	public static void expandNode(ValueNode node, DefaultTreeModel model) {
		Object obj = node.getUserObject();
		if (obj instanceof Field) {
			Field f = (Field) obj;
			ValueLogicHelper.createFieldMembers(f, node, model);
		} else if (obj instanceof Constructor) {
			Constructor<?> c = (Constructor<?>) obj;
			Class<?> cls = c.getDeclaringClass();
			ValueLogicHelper.createClassAllPublicMembers(cls, node, model);
		} else if (obj instanceof Method) {
			Method m = (Method) obj;
			ValueLogicHelper.createClassAllPublicMembers(m.getReturnType(), node, model);
		}
	}

	public static BasicNode getTransferNode(ValueNode vnode) {
		Object obj = vnode.getUserObject();
		if (obj != null) {
			BProject project = Application.getInstance().getCurrentProject();
			BasicNode t = ValueLogicHelper.getValueLogicTransferNode(vnode);
			if (t != null) {
				vnode.beforeTransfer(t);
				return t;
			}

			t = ValueLogicHelper.getClassLogicTransferNode(vnode, project);
			if (t != null) {
				vnode.beforeTransfer(t);
				return t;
			}

			BasicNode target = null;

			if (obj instanceof String) {
				String name = (String) obj;
				BClass bclass = CodecUtils.getClassFromJavaClass(project, name);
				ComplexNode node = new ComplexNode();
				node.setBClass(bclass);
				if (bclass == null) {
					node.setLogicName("null");
					node.setName("null");
				} else {
					node.setLogicName(bclass.getLogicName());
					node.setName(bclass.getLogicName());
				}
				target = node;

			} else if (obj instanceof Class<?>) {

				Class<?> cls = (Class<?>) obj;
				BClass bclass = CodecUtils.getClassFromJavaClass(cls, project);

				ComplexNode node = new ComplexNode();
				node.setBClass(bclass);
				if (vnode.isSuper()) {
					node.setLogicName("super");
					node.setName("super");
				} else {
					node.setLogicName(cls.getSimpleName());
					node.setName(cls.getSimpleName());
					node.setClass(true);
				}
				target = node;

			} else if (obj instanceof Constructor) {
				Constructor<?> m = (Constructor<?>) obj;
				ConstructorNode child = new ConstructorNode();
				CodecUtils.copyConstructorToBConstructor(m, child, project);

				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				ReferNode node = new ReferNode();
				node.setInvokeParent((BValuable) parent);
				node.setInvokeChild(child);
				node.makeDefualtValue(null);

				target = node;

			} else if (obj instanceof Method) {
				Method m = (Method) obj;
				MethodNode child = new MethodNode();
				CodecUtils.copyMethodToBMethod(m.getDeclaringClass().getName(), m, child, project);

				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				ReferNode node = new ReferNode();
				node.setInvokeParent((BValuable) parent);
				node.setInvokeChild(child);
				node.makeDefualtValue(null);

				target = node;

			} else if (obj instanceof Field) {

				Field f = (Field) obj;
				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				BClass bclass = CodecUtils.getClassFromJavaClass(f.getType(), project);
				ComplexNode child = new ComplexNode();
				child.setBClass(bclass);
				child.setLogicName(f.getName());
				child.setName(f.getName());

				ReferNode node = new ReferNode();

				node.setInvokeParent((BValuable) parent);
				node.setInvokeChild(child);
				node.makeDefualtValue(null);
				target = node;

			} else if (obj instanceof BMethod) {
				BMethod method = (BMethod) obj;
				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				ReferNode node = new ReferNode();
				node.setInvokeParent((BValuable) parent);
				node.setInvokeChild(method);
				node.makeDefualtValue(null);
				node.setInnerClassCall(true);

				target = node;

			} else if (obj instanceof BClass) {
				BClass bclass = (BClass) obj;
				ComplexNode node = new ComplexNode();
				node.setBClass(bclass);
				node.setLogicName("this");
				node.setName("this");
				node.addUserAttribute("ITEM_CLASS", "ITEM_CLASS");

				target = node;
			} else if (obj instanceof BVariable) {
				BVariable child = (BVariable) obj;
				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				if (parent.getUserAttribute("ITEM_CLASS") != null) {
					ReferNode node = new ReferNode();
					node.setInvokeParent((BValuable) parent);
					node.setInvokeChild(child);
					node.makeDefualtValue(null);
					target = node;

				} else {
					child = (BVariable) child.cloneAll();
					child.setCaller(true);
					target = (BasicNode) child;
				}
				if (child.isCaller()) {
					Object scopeObj = vnode.getValueScope();
					if (scopeObj instanceof BasicNode) {
						BasicNode scope = (BasicNode) scopeObj;
						target.addUserAttribute("SCOPE_ID", scope.getId());
					}
				}

			} else if (obj instanceof BAssignment) {
				BAssignment child = (BAssignment) obj;
				ValueNode item = (ValueNode) vnode.getParent();
				BasicNode parent = (BasicNode) item.getTransferNode();

				if (parent.getUserAttribute("ITEM_CLASS") != null) {
					ReferNode node = new ReferNode();
					node.setInvokeParent((BValuable) parent);
					node.setInvokeChild(child.getLeft());
					node.makeDefualtValue(null);
					target = node;
				}

			}
			vnode.beforeTransfer(target);
			return target;
		}
		return null;
	}

	private static BasicNode getClassLogicTransferNode(ValueNode vnode, BProject project) {

		if (vnode.getLogicType() == ValueNode.LOGIC_LOOP_ENHANCED) {
			Object obj = vnode.getUserObject();
			Class<?> cls = (Class<?>) obj;

			ParameterNode node = new ParameterNode();

			LoopNode loop = new LoopNode();
			loop.setLoopType(BLoopUnit.TYPE_ENHANCED);
			loop.removeConnector();

			ValueNode item = (ValueNode) vnode.getParent();
			BasicNode parent = (BasicNode) item.getTransferNode();
			BValuable value = (BValuable) parent;

			BType type = value.getParameterizedTypeValue();
			List<BType> types = type.getParameterizedTypes();
			if (List.class.isAssignableFrom(cls)) {
				BClass bclass = CodecUtils.getClassFromJavaClass(cls, project);
				BClass v = (BClass) types.get(0);
				node.setBClass(bclass);
				node.setLogicName("m" + v.getLogicName());
				node.setName("m" + v.getLogicName());
				loop.addEnhancedCondition(node, value);
				return loop;
			} else if (Map.class.isAssignableFrom(cls)) {

				TransferContainer container = new TransferContainer();
				// HashMap<Object, Object> map = new HashMap<Object, Object>();
				// Set<Object> set = map.keySet();
				// Iterator<Object> it = set.iterator();
				//
				// while (it.hasNext()) {
				// Object o = it.next();
				// }

				try {
					// 1-p1 map.keySet()
					ReferNode refer = new ReferNode();

					MethodNode m = new MethodNode();
					Method keySet = Map.class.getMethod("keySet");
					CodecUtils.copyMethodToBMethod(Map.class.getName(), keySet, m, project);
					refer.setInvokeParent(value);

					BValuable returnValue = m.getReturn();
					returnValue = CodecUtils.getValueable(value, returnValue, project);
					m.setReturn((BValuable) returnValue.cloneAll());
					refer.setInvokeChild(m);

					// 1 Set<Object> set = map.keySet()

					AssignmentNode assign = new AssignmentNode();
					ParameterNode parameter = new ParameterNode();
					parameter.setLogicName("mkeySet");
					parameter.setName("mkeySet");
					parameter.setBClass(returnValue.getBClass().cloneAll());
					parameter.setParameterizedTypeValue((BType) returnValue.getParameterizedTypeValue().cloneAll());
					assign.setLeft(parameter);
					assign.setRight(refer, null);
					container.addNode(assign);

					// 2-p1 set.iterator();
					ReferNode referiterator = new ReferNode();
					MethodNode miterator = new MethodNode();
					Method iterator = Set.class.getMethod("iterator");
					CodecUtils.copyMethodToBMethod(Set.class.getName(), iterator, miterator, project);
					referiterator.setInvokeParent((BValuable) parameter.cloneAll());

					returnValue = miterator.getReturn();
					returnValue = CodecUtils.getValueable(parameter, returnValue, project);
					miterator.setReturn((BValuable) returnValue.cloneAll());
					referiterator.setInvokeChild(miterator);

					// 2 Iterator<Object> it = set.iterator();
					AssignmentNode assign2 = new AssignmentNode();
					ParameterNode parameter2 = new ParameterNode();
					parameter2.setLogicName("mIterator");
					parameter2.setName("mIterator");
					parameter2.setBClass(returnValue.getBClass().cloneAll());
					parameter2.setParameterizedTypeValue((BType) returnValue.getParameterizedTypeValue().cloneAll());
					assign2.setLeft(parameter2);
					assign2.setRight(referiterator, null);

					container.addNode(assign2);

					// 3-p1 it.hasNext()
					ReferNode refer3 = new ReferNode();

					MethodNode m3 = new MethodNode();
					Method jm3 = Iterator.class.getMethod("hasNext");
					CodecUtils.copyMethodToBMethod(Iterator.class.getName(), jm3, m3, project);
					refer3.setInvokeParent((BValuable) parameter2.cloneAll());

					returnValue = m3.getReturn();
					refer3.setInvokeChild(m3);

					// 3-1-p1 it.next()
					ReferNode refer4 = new ReferNode();
					refer4.setInvokeParent(value);
					MethodNode m4 = new MethodNode();
					Method jm4 = Iterator.class.getMethod("next");
					CodecUtils.copyMethodToBMethod(Iterator.class.getName(), jm4, m4, project);
					refer4.setInvokeParent((BValuable) parameter2.cloneAll());

					returnValue = m4.getReturn();
					returnValue = CodecUtils.getValueable(parameter2, returnValue, project);
					m4.setReturn(returnValue);
					refer4.setInvokeChild(m4);

					// 3-1 Object o = it.next();

					AssignmentNode assign3 = new AssignmentNode();
					ParameterNode parameter3 = new ParameterNode();
					parameter3.setLogicName("m" + returnValue.getBClass().getLogicName());
					parameter3.setName("m" + returnValue.getBClass().getLogicName());
					parameter3.setBClass(returnValue.getBClass().cloneAll());
					parameter3.setParameterizedTypeValue((BType) returnValue.getParameterizedTypeValue().cloneAll());
					assign3.setLeft(parameter3);
					assign3.setRight(refer4, null);

					// 3 while (it.hasNext())
					loop = new LoopNode();
					loop.removeConnector();
					loop.clearCondition();
					loop.setLoopType(BLoopUnit.TYPE_WHILE);
					loop.getEditor().addUnit((BLogicUnit) assign3.cloneAll());
					loop.addCondition((BValuable) refer3.cloneAll());

					container.addNode(loop);

					return container;

				} catch (Exception e) {
					e.printStackTrace();
					Debug.d();
				}

			} else if (cls.isArray()) {

				Class<?> component = cls.getComponentType();
				BClass bclass = CodecUtils.getClassFromJavaClass(component, project);
				node.setBClass(bclass);
				node.setLogicName("m" + component.getSimpleName());
				node.setName("m" + component.getSimpleName());
				loop.addEnhancedCondition(node, value);
				return loop;

			}
		}
		return null;
	}

	private static BasicNode getValueLogicTransferNode(ValueNode node) {
		if (node.getLogicType() == ValueNode.LOGIC_ASSIGNEXPRESSION) {
			BValuable value = getValuable(node);
			AssignExpressionNode ex = new AssignExpressionNode();
			ex.setLeft(value);
			BClass b = value.getBClass();
			ex.setRight(ComplexNode.makeDefaultValue(b), null);
			return ex;
		} else if (node.getLogicType() == ValueNode.LOGIC_DECREMENT) {
			BValuable value = getValuable(node);
			SingleExpressionNode single = new SingleExpressionNode();
			single.setOperator(BSingleExpressionUnit.DECREMENT);
			single.setVariable(value);
			return single;
		} else if (node.getLogicType() == ValueNode.LOGIC_INCREMENT) {
			BValuable value = getValuable(node);
			SingleExpressionNode single = new SingleExpressionNode();
			single.setOperator(BSingleExpressionUnit.INCREMENT);
			single.setVariable(value);
			return single;
		} else if (node.getLogicType() == ValueNode.LOGIC_EXPRESSION) {
			BValuable value = getValuable(node);
			ExpressionNode ex = new ExpressionNode();
			ex.setExMiddle(BLogiker.EQUAL);
			ex.setExLeft(value);
			ex.setExRight(ComplexNode.makeDefaultValue(value.getBClass()));
			return ex;
		} else if (node.getLogicType() == ValueNode.LOGIC_RETURN) {
			BValuable value = getValuable(node);
			ReturnNode ex = new ReturnNode();
			if (value == null) {
				ex.setRuturnValue(CodecUtils.getNullValue());
			} else {
				ex.setRuturnValue(value);
			}
			return ex;
		} else if (node.getLogicType() == ValueNode.LOGIC_EXPRESSION_LINE) {
			BValuable value = getValuable(node);
			TrueFalseLineNode line = new TrueFalseLineNode();
			ExpressionNode ex = new ExpressionNode();
			ex.setExLeft(value);
			ex.setExMiddle(BLogiker.NOTQUEAL);
			ex.setExRight(CodecUtils.getNullValue());
			line.setCondition(ex);
			ComplexNode tnode = (ComplexNode) ComplexNode.makeDefaultValue(CodecUtils.BString().cloneAll());
			tnode.setLogicName("\"a\"");
			line.setTrue(tnode);

			ComplexNode fnode = (ComplexNode) ComplexNode.makeDefaultValue(CodecUtils.BString().cloneAll());
			fnode.setLogicName("\"b\"");
			line.setFalse(fnode);

			return line;
		} else if (node.getLogicType() == ValueNode.LOGIC_ARRAY) {

			String name = (String) node.getUserObject();
			if (name.equals("void")) {
				return null;
			}
			BClass bclass = CodecUtils.getClassFromJavaClass(Application.getInstance().getCurrentProject(), name);
			ComplexNode array = new ComplexNode();
			array.setBClass(bclass);
			bclass.setArrayPressentClass(bclass.cloneAll());
			array.setLogicName(bclass.getLogicName());
			array.setName(bclass.getLogicName());

			ComplexNode d = new ComplexNode();
			d.setBClass(CodecUtils.BInt().cloneAll());

			array.addArrayDimension(d);

			return array;
		}

		return null;
	}

	private static BValuable getValuable(ValueNode node) {
		Object obj = node.getUserObject();
		if (obj instanceof Field) {
			ValueNode item = (ValueNode) node.getParent();
			return (BValuable) item.getTransferNode();
		} else {
			return (BVariable) obj;
		}
	}
}
