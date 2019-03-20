package com.linkstec.bee.core.codec.basic;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.logic.model.BasicNodeLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BAssignment;

public class BasicUtils {
	public static void findDefinedList(BClass bclass, BValuable target, BPath path) {
		if (target instanceof BVariable) {
			BVariable var = (BVariable) target;
			String logicName = var.getLogicName();
			BAssignment assign = findList(path, logicName);
			if (assign != null) {
				BParameter param = assign.getLeft();
				param.setName(bclass.getName() + "リスト");
				// param.setLogicName("list" + bclass.getLogicName());
				addClassParameter(param.getBClass(), bclass);

				BValuable value = assign.getRight();
				if (value instanceof BVariable) {
					BVariable varValue = (BVariable) value;
					BClass b = varValue.getBClass();
					addClassParameter(b, bclass);
				}
			}
		}
	}

	public static void findDefinedMap(BClass key, BClass v, BValuable target, BPath path) {
		if (target instanceof BVariable) {
			BVariable var = (BVariable) target;
			String logicName = var.getLogicName();
			BAssignment assign = findList(path, logicName);
			if (assign != null) {
				BParameter param = assign.getLeft();
				param.setName("マップ<" + key.getName() + "," + v.getName() + ">");
				// param.setLogicName("list" + bclass.getLogicName());
				addClassParameter(param.getBClass(), key, v);

				BValuable value = assign.getRight();
				if (value instanceof BVariable) {
					BVariable varValue = (BVariable) value;
					BClass b = varValue.getBClass();
					addClassParameter(b, key, v);
				}
			}
		}
	}

	public static void addClassParameter(BClass target, BClass key, BClass value) {
		List<BType> deleted = new ArrayList<BType>();
		List<BType> types = target.getParameterizedTypes();
		for (BType type : types) {
			if (type instanceof BClass) {
				deleted.add(type);
			}
		}
		if (deleted.size() > 0) {
			for (BType type : deleted) {
				types.remove(type);
			}
			target.setParameterTypes(types);
		}
		key.setParameterValue(true);
		value.setParameterValue(true);
		target.addParameterizedType(key);
		target.addParameterizedType(value);

	}

	public static void addClassParameter(BClass target, BClass value) {
		List<BType> deleted = new ArrayList<BType>();
		List<BType> types = target.getParameterizedTypes();
		for (BType type : types) {
			if (type instanceof BClass) {
				deleted.add(type);
			}
		}
		if (deleted.size() > 0) {
			for (BType type : deleted) {
				types.remove(type);
			}
			target.setParameterTypes(types);
		}
		value.setParameterValue(true);
		target.addParameterizedType(value);

	}

	private static BAssignment findList(BPath path, String logicName) {
		if (path == null) {
			return null;
		}
		BLogic logic = path.getLogic();
		if (logic instanceof BasicNodeLogic) {
			// BasicNodeLogic basic = (BasicNodeLogic) logic;
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) path.getCell();
			BasicNode node = wrapper.getNode();

			if (node instanceof BAssignment) {
				BAssignment assign = (BAssignment) node;
				BParameter left = assign.getLeft();
				if (left.getLogicName().equals(logicName)) {
					return assign;
				}
			}
		} else {
			return findList(path.getParent(), logicName);
		}
		return null;
	}
}
