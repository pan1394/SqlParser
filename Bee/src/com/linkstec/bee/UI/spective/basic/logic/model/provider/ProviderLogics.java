package com.linkstec.bee.UI.spective.basic.logic.model.provider;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenu;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BClassEnd;
import com.linkstec.bee.UI.spective.basic.logic.model.InvokerLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BInvokerNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.basic.BEndLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BPropertiesUtil;
import com.linkstec.bee.core.fw.basic.BUtilMethod;
import com.linkstec.bee.core.fw.editor.BProject;

public class ProviderLogics {
	private static Hashtable<BActionModel, List<BLogic>> restored = new Hashtable<BActionModel, List<BLogic>>();

	public static List<BLogic> getList(BPath parent, boolean reload) {

		BActionModel model = (BActionModel) parent.getAction();
		int layer = model.getActionDepth();
		List<BLogic> list = new ArrayList<BLogic>();

		if (!reload) {
			List<BLogic> restore = restored.get(parent.getAction());
			if (restore != null) {
				return restore;
			}
		}
		if (layer < 0) {
			return list;
		}

		BLogicProvider p = parent.getProvider();
		p.getProperties().setReload(reload);

		BClass bclass = p.getProperties().getCurrentDeclearedClass();
		if (bclass == null) {
			bclass = BasicGenUtils.createClass((BActionModel) parent.getAction(), parent.getProject());
			p.getProperties().setCurrentDeclearedClass(bclass);
		}

		List<BUtilMethod> utils = p.getCommonLogics(bclass);
		if (utils == null) {
			return list;
		}

		BProject bp = p.getProperties().getProject();

		LogicMenu menu = Application.getInstance().getBasicSpective().getLogicMenu();
		menu.clearAll();

		for (BUtilMethod util : utils) {

			BLogic logic = ProviderLogics.getLogic(parent, util, bp, p);
			if (logic != null) {
				list.add(logic);
			}
		}

		BPropertiesUtil prop = p.getPropeties(bclass);
		if (prop != null) {
			Hashtable<String, Properties> pros = prop.getProperties();
			Enumeration<String> keys = pros.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				Properties value = pros.get(key);
				menu.addProperties(key, value, parent.getProject());

			}
		}

		menu.updateAll();

		list.addAll(getEndLoigc(parent, p));

		restored.put((BActionModel) parent.getAction(), list);
		return list;
	}

	public static BLogic getLogic(BPath parent, BUtilMethod util, BProject project, BLogicProvider provider) {
		if (util.getUseStyle() == BUtilMethod.STYLE_CALL) {

			return new ProviderCallLogic(parent, util);
		} else if (util.getUseStyle() == BUtilMethod.STYLE_PARENT_MADE) {
			BClass bclass = util.getBClass();

			BInvokerNode node = new BInvokerNode();
			InvokerLogic logic = new InvokerLogic(parent, node, util.getBMethod(), bclass);
			node.setLogic(logic);
			return logic;

		} else {
			return new ProviderContentsLogic(parent, util);
		}
	}

	public static List<BEndLogic> getEndLoigc(BPath parent, BLogicProvider provider) {
		List<BEndLogic> ends = new ArrayList<BEndLogic>();
		BActionModel model = (BActionModel) parent.getAction();
		List<BasicComponentModel> outputs = model.getOutputModels();
		for (BasicComponentModel comp : outputs) {
			BClassEnd end = new BClassEnd(parent, comp, comp.getName(), provider.getReturnValue(comp,
					model.getActionDepth(), comp.getLogicName(), model.getSubSystem().getLogicName()));
			ends.add(end);
		}
		return ends;
	}
}
