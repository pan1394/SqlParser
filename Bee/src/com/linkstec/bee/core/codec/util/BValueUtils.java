package com.linkstec.bee.core.codec.util;

import java.util.List;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BValueUtils {
	public static String createValuable(BValuable value, boolean logical) {
		if (value == null) {
			return null;
		}

		if (value.getUserAttribute("DISPLY_NAME") != null) {
			return (String) value.getUserAttribute("DISPLY_NAME");
		}

		if (value instanceof BVariable) {

			BVariable var = (BVariable) value;
			BValuable index = var.getArrayIndex();
			BValuable array = var.getArrayObject();
			if (index != null && array != null) {
				return BValueUtils.createValuable(array, logical) + "[" + BValueUtils.createValuable(array, logical)
						+ "]";
			}
			String name = var.getName();
			if (logical) {
				name = var.getLogicName();
			}
			if (name == null) {
				return "";
			}
			if (name.equals("")) {
				return "\"\"";
			}
			return name;
		} else if (value instanceof BExpression) {
			BExpression expression = (BExpression) value;
			return BValueUtils.createExpression(expression, logical);
		} else if (value instanceof BInvoker) {
			return BValueUtils.createInvoker(value, logical);
		} else if (value instanceof BAssign) {
			BAssign assign = (BAssign) value;

			BValuable obj;
			if (assign instanceof BAssignment) {
				BAssignment bas = (BAssignment) assign;
				obj = bas.getLeft();
			} else {
				BAssignExpression bas = (BAssignExpression) assign;
				obj = bas.getLeft();
			}
			BValuable right = assign.getRight();
			String sl = BValueUtils.createValuable(obj, logical);
			String sr = BValueUtils.createValuable(right, logical);

			return sl + "=" + sr;
		} else if (value instanceof BExpressionLine) {
			BExpressionLine line = (BExpressionLine) value;

			BValuable left = line.getTrue();
			BValuable right = line.getFalse();
			BValuable ex = line.getCondition();
			String se = BValueUtils.createValuable(ex, logical);
			String sl = BValueUtils.createValuable(left, logical);
			String sr = BValueUtils.createValuable(right, logical);
			if (logical) {
				return "if " + se + " then " + sl + " else " + sr;
			} else {
				return se + "?" + sl + ":" + sr;
			}
		} else if (value instanceof BConstructor) {
			return BValueUtils.createConstructor(value, logical);
		} else if (value instanceof BMethod) {
			BMethod method = (BMethod) value;
			return method.getLogicName();

		} else {
			throw new RuntimeException("Not Valuable!");
		}
	}

	public static String createInvoker(BValuable value, boolean logical) {
		BInvoker invoker = (BInvoker) value;
		BValuable parent = invoker.getInvokeParent();
		BValuable child = invoker.getInvokeChild();

		boolean widthParameter = true;
		String sp = BValueUtils.createValuable(parent, logical);
		if (sp.equals("this")) {
			sp = "";
		} else {
			sp = sp + ".";
		}
		String s = BValueUtils.createValuable(child, logical);
		List<BValuable> parameters = invoker.getParameters();
		if ((child instanceof BConstructor)) {
			s = "";
			sp = "new " + parent.getBClass().getLogicName();
		} else {
			if (parent.getBClass().isData() || child instanceof BVariable) {
				widthParameter = false;
			}
		}
		String parameter = "";
		if (widthParameter) {
			parameter = "(";
			for (BValuable para : parameters) {
				String paraValue = BValueUtils.createValuable(para, logical);
				if (parameter.equals("(")) {
					parameter = parameter + paraValue;
				} else {
					parameter = parameter + "," + paraValue;
				}
			}
			parameter = parameter + ")";
		} else {
			// sp = "return value of " + sp;
		}
		return sp + s + parameter;

	}

	public static String createConstructor(BValuable value, boolean logical) {
		if (value instanceof IUnit) {
			IUnit unit = (IUnit) value;
			if (unit.getNumber() != null) {

				return unit.getNumber().getString();
			}
		}

		BConstructor c = (BConstructor) value;
		List<BParameter> list = c.getParameter();
		String parameters = "";

		if (list != null && list.size() > 0) {
			parameters = " width parameter ";
			boolean first = true;
			for (BParameter var : list) {
				if (first) {
					first = false;
				} else {
					parameters = parameters + ",";
				}

				parameters = parameters + var.getLogicName();
			}
		}

		return " instance of " + c.getReturn().getBClass().getLogicName() + parameters;
	}

	private static String createExpression(BExpression expression, boolean logical) {
		BValuable left = expression.getExLeft();
		BValuable right = expression.getExRight();
		String sl = BValueUtils.createValuable(left, logical);
		String sr = BValueUtils.createValuable(right, logical);
		String s = expression.getExMiddle().toString();
		BLogiker middle = expression.getExMiddle();

		if (middle.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
			if (logical) {
				s = " == ";
			} else {
				s = " = ";
			}
		} else if (middle.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {
			s = " is not ";
			if (logical) {
				s = " != ";
			} else {
				s = " <> ";
			}
		} else if (middle.getLogicName().equals(BLogiker.LOGICAND.getLogicName())) {
			if (logical) {
				s = " and ";
			} else {
				s = " 且つ ";
			}
		} else if (middle.getLogicName().equals(BLogiker.LOGICOR.getLogicName())) {
			if (logical) {
				s = " or ";
			} else {
				s = " または ";
			}
		} else if (middle.getLogicName().equals(BLogiker.INSTANCEOF.getLogicName())) {
			s = "　instanceof ";
		}
		if (expression.getUserAttribute("PARENTIZED") != null) {
			return "(" + sl + s + sr + ")";
		}
		return sl + s + sr;
	}

}
