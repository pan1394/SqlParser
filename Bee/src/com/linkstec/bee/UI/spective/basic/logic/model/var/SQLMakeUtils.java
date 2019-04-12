package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogiker;

public class SQLMakeUtils {
	public static String getSQL(BExpression ex) {
		BValuable left = ex.getExLeft();
		BValuable right = ex.getExRight();
		BLogiker middle = ex.getExMiddle();

		Object disply = ex.getUserAttribute("FOR_DISPLAY");

		String sl = getValueText(left, false, disply);
		String sr = getValueText(right, false, disply);
		String sm = middle.toString();

		if (sr == null || sr.toLowerCase().equals("null")) {
			if (middle.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
				sm = "IS";
			} else if (middle.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {
				sm = "IS NOT";
			}

		}

		if (disply == null) {
			if (right instanceof BVariable) {
				if (left instanceof BInvoker) {
					BInvoker bin = (BInvoker) left;
					BValuable child = bin.getInvokeChild();
					if (child instanceof BVariable) {
						BVariable var = (BVariable) child;
						sr = "パラメーターの" + var.getName();
					}
				}
			}
		} else {
			ex.removeUserAttribute("FOR_DISPLAY");
		}

		return sl + " " + sm + " " + sr;

	}

	public static String getLogicSQL(BExpression ex, List<BInvoker> invokers) {
		BValuable left = ex.getExLeft();
		BValuable right = ex.getExRight();
		BLogiker middle = ex.getExMiddle();

		String sl = getLogicValueText(left, invokers, middle);
		String sr = getLogicValueText(right, invokers, middle);
		String sm = middle.toString();

		if (right instanceof BVariable) {
			if (left instanceof BInvoker) {
				BInvoker bin = (BInvoker) left;
				BValuable child = bin.getInvokeChild();
				if (child instanceof BVariable) {
					BVariable var = (BVariable) child;
					sr = "#{" + var.getLogicName() + "}";
				}
			}

			BVariable var = (BVariable) right;
			var.addUserAttribute("LOGIKER", middle);
			IPatternCreator view = PatternCreatorFactory.createView();
			BInvoker invoker = view.createMethodInvoker();
			invoker.setInvokeParent(left);
			invoker.setInvokeChild(var);
			invoker.addUserAttribute(BasicGenUtils.STATIC_CALL, BasicGenUtils.STATIC_CALL);
			invokers.add(invoker);

		}

		if (sr == null || sr.toLowerCase().equals("null")) {
			if (middle.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
				sm = "IS";
			} else if (middle.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {
				sm = "IS NOT";
			}

		}

		return sl + " " + sm + " " + sr;
	}

	public static String getValueText(BValuable value, boolean widthType, Object display) {
		if (value == null) {
			return null;
		}
		if (value.getBClass() == null) {
			return null;
		}
		if (value.getBClass().getLogicName().equals(BClass.NULL)) {
			if (value instanceof BVariable) {
				BVariable var = (BVariable) value;
				Object list = var.getUserAttribute("VALUE_LIST");
				if (list instanceof List) {
					List l = (List) list;

					String result = null;
					for (Object obj : l) {
						if (obj instanceof BValuable) {
							BValuable listValue = (BValuable) obj;
							String s = getValueText(listValue, widthType, display);
							if (s != null) {
								if (result == null) {
									result = "(" + s;
								} else {
									result = result + "," + s;
								}
							}
						}
					}
					if (result != null) {
						return result + ")";
					}
				}
			}
			return null;
		}

		if (value instanceof BVariable) {
			BVariable var = (BVariable) value;

			if (widthType) {
				if (var.getBClass() instanceof BasicDataModel) {
					return var.getLogicName();
				} else {
					return var.getLogicName();
				}
			} else {
				return var.getName();
			}

		} else if (value instanceof BInvoker) {
			BInvoker invoker = (BInvoker) value;
			BValuable parent = invoker.getInvokeParent();
			BValuable child = invoker.getInvokeChild();
			boolean withType = parent.getBClass() instanceof BasicDataModel;

			Thread t = Thread.currentThread();
			if (t instanceof BeeThread) {
				BeeThread bee = (BeeThread) t;
				Object obj = bee.getUserAttribute("UP_INSER");
				if ("UP_INSER".equals(obj)) {
					return getValueText(child, false, display);

				}
			}

			if (display == null) {

				if (parent.getBClass().getClass().getName().equals(BeeDataModel.class.getName())) {

					if (child instanceof BVariable) {
						return getInjectValueExp((BVariable) child, null);
					}
				}
			}

			return getValueText(parent, withType, display) + "." + getValueText(child, false, display);

		} else if (value instanceof BExpression) {
			BExpression ex = (BExpression) value;
			return getSQL(ex);

		} else {
			Debug.a();
		}

		return null;
	}

	public static String getLogicValueText(BValuable value, List<BInvoker> invokers, BLogiker logiker) {
		if (value == null) {
			return null;
		}
		if (value.getBClass().getLogicName().equals(BClass.NULL)) {
			if (value instanceof BVariable) {
				BVariable var = (BVariable) value;
				Object list = var.getUserAttribute("VALUE_LIST");
				if (list instanceof List) {
					List l = (List) list;
					String result = null;
					for (Object obj : l) {
						if (obj instanceof BValuable) {
							BValuable listValue = (BValuable) obj;
							String s = getLogicValueText(listValue, new ArrayList<BInvoker>(), logiker);
							if (s != null) {
								if (result == null) {
									result = "(" + s;
								} else {
									result = result + "," + s;
								}
							}
						}
					}
					if (result != null) {
						return result + ")";
					}
				}
			}
			return null;
		}

		if (value instanceof BVariable) {
			BVariable var = (BVariable) value;
			return var.getLogicName();
		} else if (value instanceof BInvoker) {
			BInvoker invoker = (BInvoker) value;
			BValuable parent = invoker.getInvokeParent();
			BValuable child = invoker.getInvokeChild();
			if (parent.getBClass() instanceof BasicDataModel) {
				return getTableReference(invoker);
			} else {
				invokers.add(invoker);
				if (child instanceof BVariable) {
					BVariable var = (BVariable) child;
					return getInjectValue(var, logiker);
				} else {
					return getLogicValueText(parent, invokers, logiker) + "."
							+ getLogicValueText(child, invokers, logiker);
				}
			}
		} else if (value instanceof BExpression) {
			BExpression ex = (BExpression) value;
			return getLogicSQL(ex, invokers);

		} else {
			Debug.a();
		}

		return null;
	}

	public static String changeType(String type) {
		String dbType = "varchar";
		if (type.equals(String.class.getName())) {
			return "VARCHAR";
		}
		if (type.equals(BigDecimal.class.getName())) {
			return "DECIMAL";
		}
		if (type.equals(Integer.class.getName()) || type.equals("int")) {
			return "INTEGER";
		}
		if (type.equals(Date.class.getName())) {
			return "DATE";
		}
		if (type.equals(Timestamp.class.getName())) {
			return "TIMESTAMP";
		}
		return dbType;
	}

	public static String getInjectValue(BVariable child, BLogiker logiker) {
		if (logiker != null) {
			child.addUserAttribute("LOGIKER", logiker);
		}
		return "#{" + child.getLogicName() + "}";
	}

	public static String getInjectValueExp(BVariable child, BLogiker logiker) {
		return "パラメーターの" + child.getName();
	}

	public static String getTableReference(BInvoker invoker) {

		BVariable parent = (BVariable) invoker.getInvokeParent();
		BVariable child = (BVariable) invoker.getInvokeChild();
		String target = child.getLogicName();
		String value = "";
		for (int i = 0; i < target.length(); i++) {
			char c = target.charAt(i);
			if (Character.isUpperCase(c)) {
				value = value + "_" + Character.toLowerCase(c);
			} else {
				value = value + c;
			}
		}
		Thread t = Thread.currentThread();
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			Object obj = bee.getUserAttribute("UP_INSER");
			if ("UP_INSER".equals(obj)) {
				return value;
			}
		}
		return parent.getLogicName() + "." + value;

	}

	public static String getTableReferenceExp(BInvoker invoker) {

		BVariable parent = (BVariable) invoker.getInvokeParent();
		BVariable child = (BVariable) invoker.getInvokeChild();

		Thread t = Thread.currentThread();
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			Object obj = bee.getUserAttribute("UP_INSER");
			if ("UP_INSER".equals(obj)) {
				return child.getName();
			}
		}

		return parent.getName() + "." + child.getName();

	}
}
