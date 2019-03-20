package com.linkstec.bee.UI.spective.detail.tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.linkstec.bee.UI.BSpective;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.BeeName;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class ValueNode extends BeeTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2934965627739234268L;

	private boolean dataVar = false;
	private boolean inited = false;
	private int logicType = -1;
	private Object valueScope;
	private boolean isSuper = false;

	public static int LOGIC_LOOP_ENHANCED = 1;
	public static int LOGIC_ASSIGNEXPRESSION = 2;
	public static int LOGIC_INCREMENT = 3;
	public static int LOGIC_DECREMENT = 4;
	public static int LOGIC_EXPRESSION = 5;
	public static int LOGIC_EXPRESSION_LINE = 6;
	public static int LOGIC_RETURN = 7;
	public static int LOGIC_ARRAY = 8;

	public ValueNode() {
		super("");
		this.setLeaf(false);

	}

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}

	public Object getValueScope() {
		return valueScope;
	}

	public void setValueScope(Object valueScope) {
		this.valueScope = valueScope;
	}

	public int getLogicType() {
		return logicType;
	}

	public void setLogicType(int logicType) {
		this.logicType = logicType;
	}

	@Override
	public boolean isLeaf() {
		if (super.isLeaf()) {
			return this.getChildCount() == 0;
		} else {
			return false;
		}
	}

	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	public boolean isDataVar() {
		return dataVar;
	}

	public void setDataVar(boolean dataVar) {
		this.dataVar = dataVar;
	}

	public mxCell getTransferNode() {
		return ValueLogicHelper.getTransferNode(this);
	}

	public String getLineString() {
		ValueNode parent = (ValueNode) this.getParent();
		if (parent != null) {
			String pvalue = parent.getNameValue();
			if (pvalue != null) {
				return pvalue + "." + this.getNameValue();
			} else {
				return this.getNameValue();
			}
		} else {
			return this.getNameValue();
		}
	}

	@Override
	public String getUniqueKey() {
		return getLineString();
	}

	public String getTypeNameValue() {
		Object obj = this.getUserObject();
		if (obj instanceof String) {
			String name = (String) obj;

			return name;

		} else if (obj instanceof Class<?>) {
			Class<?> cls = (Class<?>) obj;
			return cls.getName();
		} else if (obj instanceof Constructor) {
			Constructor<?> m = (Constructor<?>) obj;

			Class<?> type = m.getDeclaringClass();

			return type.getSimpleName();

		} else if (obj instanceof Method) {
			Method m = (Method) obj;

			Class<?> type = m.getReturnType();
			if (!type.equals(void.class)) {
				return type.getSimpleName();
			}
			return null;

		} else if (obj instanceof Field) {
			Field f = (Field) obj;

			Class<?> type = f.getType();
			return type.getSimpleName();
		}
		return null;
	}

	public String getNameValue() {
		Object obj = this.getUserObject();
		if (obj instanceof String) {
			String name = (String) obj;

			if (name.indexOf('.') > 0) {
				name = name.substring(name.lastIndexOf('.') + 1);
			}
			if (this.logicType == ValueNode.LOGIC_ARRAY) {
				name = name + "[]";
			}

			return name;
		} else if (obj instanceof Class<?>) {
			Class<?> cls = (Class<?>) obj;

			return cls.getSimpleName();
		} else if (obj instanceof Constructor) {
			Constructor<?> m = (Constructor<?>) obj;

			String ss = "(";
			Class<?>[] types = m.getParameterTypes();
			for (Class<?> cls : types) {
				if (ss.equals("(")) {

				} else {
					ss = ss + ",";
				}
				ss = ss + cls.getSimpleName();
			}

			ss = "new " + m.getDeclaringClass().getSimpleName() + " " + ss + ")";

			return ss;

		} else if (obj instanceof Method) {
			Method m = (Method) obj;

			String ss = "(";
			Class<?>[] types = m.getParameterTypes();
			for (Class<?> cls : types) {
				if (ss.equals("(")) {

				} else {
					ss = ss + ",";
				}
				ss = ss + cls.getSimpleName();
			}

			ss = m.getName() + ss + ")";

			return ss;

		} else if (obj instanceof Field) {
			Field f = (Field) obj;
			return f.getName();
		} else if (obj instanceof BObject) {
			return super.toString();
		}
		return null;

	}

	protected void beforeTransfer(mxICell node) {
		if (Application.getInstance().getCurrentSpactive().equals(BSpective.BASIC_DESIGN)) {
			if (node.getGeometry() != null) {
				node.getGeometry().setRelative(false);
			}
		}
		if (node instanceof ComplexNode) {
			ComplexNode cell = (ComplexNode) node;
			BeeGraphSheet sheet = Application.getInstance().getDesignSpective().getGraphSheet();
			if (sheet != null) {
				if (!cell.isCaller()) {

					if (cell.getName() == null) {
						BeeName name = BeeNamingUtil.makeName((BEditorModel) sheet.getModel(), BeeName.TYPE_VAR);
						cell.setName(name.getName().toLowerCase());
						cell.setLogicName(name.getLogicName().toLowerCase());
					}
					cell.addUserAttribute("MENU_ITEM_TYPE", "MENU_ITEM_TYPE");

				}
			}
		}
	}

}
