package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class ListAddLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6515825013331072238L;

	private BValuable target;
	private BValuable value;

	public ListAddLogic(BPath parent, ILogicCell cell, BValuable var) {
		super(parent, cell);
		this.target = var;
	}

	@Override
	public String getName() {
		if (target != null) {
			return BValueUtils.createValuable(target, false) + "に値を追加する";
		} else {
			return "";
		}
	}

	public BValuable getTarget() {
		return this.target;
	}

	@Override
	public String getDesc() {
		if (target != null) {
			if (this.value != null) {
				return BValueUtils.createValuable(target, false) + "に「" + BValueUtils.createValuable(value, false)
						+ "」を追加する";
			}
			return BValueUtils.createValuable(target, false) + "に値を追加する";
		} else {
			return "";
		}
	}

	public void setAddValue(BValuable var) {
		this.value = var;
	}

	public BValuable getAddValue() {
		return this.value;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();
		invoker.setInvokeParent(this.target);
		if (this.target instanceof BVariable) {
			BVariable var = (BVariable) this.target;
			var.setClass(false);
			var.setCaller(true);
		}
		BMethod method = view.createMethod();
		method.setLogicName("add");
		method.setName("値追加");

		BParameter param = view.createParameter();

		param.setBClass(CodecUtils.getClassFromJavaClass(Object.class, this.getPath().getProject()));
		param.setLogicName("value");
		param.setName("値");

		BVariable var = view.createVariable();
		var.setBClass(CodecUtils.BVoid());
		method.setReturn(var);
		method.addParameter(param);
		invoker.setInvokeChild(method);

		invoker.addParameter(value);

		units.add(invoker);
		return units;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.LIST_ADD_ICON;
	}

}
