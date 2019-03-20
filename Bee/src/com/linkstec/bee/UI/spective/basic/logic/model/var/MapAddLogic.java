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

public class MapAddLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1660517556780130796L;
	private BValuable target;
	private BValuable key;
	private BValuable value;

	public MapAddLogic(BPath parent, ILogicCell cell, BValuable var) {
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
	public ImageIcon getIcon() {
		return BeeConstants.MAP_ICON;
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

	public void setKey(BValuable key) {
		this.key = key;
	}

	public void setValue(BValuable value) {
		this.value = value;
	}

	public BValuable getKey() {
		return this.key;
	}

	public BValuable getValue() {
		return this.value;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		if (this.key != null && this.value != null) {

			IPatternCreator view = PatternCreatorFactory.createView();
			BInvoker invoker = view.createMethodInvoker();
			invoker.setInvokeParent(this.target);
			BMethod method = view.createMethod();
			method.setLogicName("put");
			method.setName("値追加");

			BParameter param = view.createParameter();
			param.setBClass(CodecUtils.getClassFromJavaClass(Object.class, this.getPath().getProject()));
			param.setLogicName("key");
			param.setName("キー");

			BVariable var = view.createVariable();
			var.setBClass(CodecUtils.BVoid());
			method.setReturn(var);
			method.addParameter(param);
			BParameter v = (BParameter) param.cloneAll();
			v.setLogicName("value");
			v.setName("値");
			method.addParameter(v);
			invoker.setInvokeChild(method);

			invoker.addParameter(key);
			invoker.addParameter(value);

			units.add(invoker);
		}
		return units;
	}

}
