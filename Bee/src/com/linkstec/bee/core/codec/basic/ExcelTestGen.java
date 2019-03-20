package com.linkstec.bee.core.codec.basic;

import java.util.Iterator;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BModifiedBlock;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.fw.logic.BThrow;
import com.linkstec.bee.core.fw.logic.BTryUnit;

public class ExcelTestGen {

	public static void generate(BModule paramBModule) {
		// TO DO 生成一个式样书excel；
		List<BClass> localList = paramBModule.getClassList();
		// BClass localObject1, localObject2 = null;
		if (localList.isEmpty()) {
			return;
		}
		for (Object localObject1 = localList.iterator(); ((Iterator) localObject1).hasNext();) {
			BClass localObject2 = (BClass) ((Iterator) localObject1).next();
			if (((BClass) localObject2).getLogicName().endsWith("JclServiceImpl")) {
				localList.remove(localObject2);
				break;
			}
		}
		BClass localObject1 = null;
		for (Object localObject2 = localList.iterator(); ((Iterator) localObject2).hasNext();) {
			BClass localObject3 = (BClass) ((Iterator) localObject2).next();
			if (((BClass) localObject3).getLogicName().endsWith("JobServiceImpl")) {
				localObject1 = localObject3;
				break;
			}
		}

		if (localObject1 != null) {
			String serviceImplName = ((BClass) localObject1).getName();
			System.out.println(serviceImplName);
		}

		BClass logic = localObject1;
		String classA = logic.getLogicName();
		List<BValuable> inters = logic.getInterfaces();
		List<BMethod> methods = logic.getMethods();
		for (int i = 0; i < methods.size(); i++) {
			BMethod method = (BMethod) methods.get(i);
			System.out.println(method.getLogicName());
			createLogicBody(method.getLogicBody());
		}
	}

	private static void createLogicBody(BLogicBody body) {
		List<BLogicUnit> units = body.getUnits();
		int count = units.size();

		int flowIndex = 1;
		for (int i = 0; i < count; i++) {
			BLogicUnit unit = (BLogicUnit) units.get(i);
			Object obj = unit.getUserAttribute("DESC");
			if ((obj instanceof String)) {
				System.out.println(obj);
			}
			createUnit(unit);
		}
	}

	private static boolean createUnit(BLogicUnit unit) {
		if ((unit instanceof BNote)) {
			return true;
		}
		if ((unit instanceof BInvoker)) {
			BInvoker in = (BInvoker) unit;
			if (in.isLinker()) {
				return true;
			}
		}
		if ((unit instanceof IUnit)) {
			// p.setRow(p.getRow() + 1);
			// p.increaseUnit((IUnit)unit);
		}
		if (((unit instanceof IUnit)) && (!(unit instanceof BMethod))) {
			List<BInvoker> linkers = ((IUnit) unit).getLinkers();
			for (BInvoker invoker : linkers) {
				createInvoker(invoker, true, null);
			}
		}
		if ((unit instanceof BMethod)) {
			// createMethod((BMethod) unit);
		} else if ((unit instanceof BAssign)) {
			// createAssign((BAssign) unit);
		} else if ((unit instanceof BInvoker)) {
			// createInvoker((BInvoker) unit, true, null);
		} else if ((unit instanceof BReturnUnit)) {
			// createReturn((BReturnUnit) unit);
		} else if ((unit instanceof BLoopUnit)) {
			// createLoop((BLoopUnit) unit);
		} else if ((unit instanceof BMultiCondition)) {
			// createIf((BMultiCondition) unit);
		} else if ((unit instanceof BTryUnit)) {
			createTry((BTryUnit) unit);
		} else if ((unit instanceof BThrow)) {
			// createThrow((BThrow) unit);
		} else if ((unit instanceof BModifiedBlock)) {
			createLogicBody((BLogicBody) unit);
		}
		if ((unit instanceof IUnit)) {
			// p.setRow(p.getRow() + 1);
		}
		return false;
	}

	private static void createTry(BTryUnit unit) {

		createLogicBody(unit.getTryEditor());
	}

	private static void createInvoker(BInvoker invoker, boolean withTitle, BVariable param) {
		// ("createInvoker:" + invoker.toString());
		if (true/* p.isDoInvoker() */) {
			List<BInvoker> linkers = ((IUnit) invoker).getLinkers();
			for (BInvoker bin : linkers) {
				if (!bin.equals(invoker)) {
					createInvoker(bin, true, null);
				}
			}
		}
//	    if (createStringBuilder(invoker))
//	    {
//	      if (invoker.isLinker()) {
//	        //p.setRow(p.getRow() + 2);
//	      }
//	      return;
//	    }
		BValuable parentValue = invoker.getInvokeParent();
		BValuable childValue = invoker.getInvokeChild();
		Object parameters = invoker.getParameters();
		if ((parentValue != null) && ((parentValue.getBClass().isData()) || ((childValue instanceof BVariable)))
				&& (((List) parameters).size() == 1)) {
			System.out.println(invoker);
			// String parent = getDataSetValue(invoker, p);
			// String value = getDataSetValue((BValuable) ((List) parameters).get(0), p);

//	      ExcelUtils.createDataSet(sheet, p, value, parent, this.style, (IUnit)invoker);
//	      if ((!p.getUnitPath().isContinuous()) && 
//	        (invoker.isLinker())) {
//	        p.setRow(p.getRow() + 2);
//	      }
			return;
		}
		if ((childValue instanceof BMethod)) {
			BMethod mt = (BMethod) childValue;
			System.out.println(invoker);
			String parent = "Logger";// createValuable(parentValue, p, true);
			if (parentValue == null) {
				// createMethodInvoker(sheet, p, withTitle, mt, null, parent, invoker, param);
			} else {
				createMethodInvoker(withTitle, mt, parentValue.getBClass().getLogicName(), parent, invoker, param);
			}
		}
	}

	private static void createMethodInvoker(boolean withTitle, BMethod mt, String parentClassName, String varName,
			BInvoker invoker, BVariable returnValueParam) {
		if (parentClassName.equals("MessageSource")) {
			createLoggerInvoker(withTitle, mt, parentClassName, varName, invoker, returnValueParam);
			return;
		}
	}

	private static void createLoggerInvoker(boolean withTitle, BMethod mt, String parentClassName, String varName,
			BInvoker invoker, BVariable returnValueParam) {
		List<BValuable> paras = invoker.getParameters();
		String title = "logger." + mt.getLogicName() + "でログの出力\n";
		if (paras.size() == 1) {
			BValuable value = (BValuable) paras.get(0);
//	      if ((value instanceof BVariable))
//	      {
//	        List<BLogicUnit> units = p.getCurrentUnits();
//	        if (units != null)
//	        {
//	          int index = p.getCurrentUnitIndex();
//	          if (index - 1 >= 0)
//	          {
//	            BLogicUnit unit = (BLogicUnit)units.get(index - 1);
//	            if ((unit instanceof BAssignment))
//	            {
//	              BAssignment a = (BAssignment)unit;
//	              BValuable child = a.getRight();
//	              if ((child instanceof BInvoker)) {
//	                value = child;
//	              }
//	            }
//	          }
//	        }
//	      }
			if ((value instanceof BInvoker)) {
				BInvoker in = (BInvoker) value;
				List<BValuable> params = in.getParameters();
				BValuable logId = (BValuable) params.get(0);
				BValuable logParas = (BValuable) params.get(1);
				if ((logId instanceof BInvoker)) {
					BInvoker bin = (BInvoker) logId;
					logId = bin.getInvokeChild();
				}
				if ((logId instanceof BVariable)) {
					BVariable idVar = (BVariable) logId;
					String param1 = "ログid:";
					String messageId = idVar.getLogicName();
					if (messageId.startsWith("\"")) {
						messageId = messageId.substring(1);
					}
					if (messageId.endsWith("\"")) {
						messageId = messageId.substring(0, messageId.length() - 1);
					}
					param1 += messageId + "\n";
					title += param1;
				}

				String param2 = "ログパラメーター：";
				if ((logParas.getBClass() == null) || (logParas.getBClass().getLogicName().equals("null"))) {
					param2 += "NULL";
					title += param2;
				}

			}
		}
		System.out.println(title);
	}
}
