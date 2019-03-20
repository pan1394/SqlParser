package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IJudgeCell;
import com.linkstec.bee.core.fw.basic.IUnitCell;

public class BLogicNodeManager {

	public static IUnitCell createUnit(BLogic logic) {
		return new BUnitNode(logic);
	}

	public static IJudgeCell createJudge(BLogic logic) {
		return new BJudgeNode(logic);
	}
}
