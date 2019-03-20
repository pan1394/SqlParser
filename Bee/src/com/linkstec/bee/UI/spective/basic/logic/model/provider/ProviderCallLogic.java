package com.linkstec.bee.UI.spective.basic.logic.model.provider;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BUtilMethod;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class ProviderCallLogic extends BasicLogic {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4127694792276438497L;

	private BInvoker invoker;
	private BMethod method;

	public ProviderCallLogic(BPath parent, BUtilMethod util) {
		super(parent, null);

		BLogicNode node = new BLogicNode(this);
		this.getPath().setCell(node);

		BClass bclass = util.getBClass();
		if (bclass == null) {
			return;
		}

		BMethod method = util.getBMethod();
		if (method == null) {
			return;
		}

		IPatternCreator view = PatternCreatorFactory.createView();
		invoker = view.createMethodInvoker();
		BVariable var = view.createVariable();
		var.setBClass(bclass);
		var.setClass(true);
		invoker.setInvokeParent(var);
		invoker.setInvokeChild(method);
	}

	@Override
	public String getName() {
		return this.method.getName();
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.METHOD_ICON;
	}

	@Override
	public String getDesc() {
		return this.method.getName();
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		if (this.invoker != null) {
			units.add(invoker);
		}

		return units;
	}

	@Override
	public boolean hasException() {
		if (this.method != null) {
			if (this.method.getThrows().size() > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isReturnBoolean() {
		if (this.method != null) {
			if (this.method.getReturn().getBClass().getQualifiedName().equals("boolean")) {
				return true;
			}
		}
		return false;
	}

}
