package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.data.LoopLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BListAddNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BMapAddNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BMapGetNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BInvoker;

public class VarLogicList extends LogicList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8604540720774615786L;
	private BValuable var;
	private BVariable target;

	public VarLogicList(BVariable data, BParameter parameter) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();
		invoker.setInvokeParent(data);

		BParameter left = view.createParameter();

		left.setName(parameter.getName());
		left.setLogicName(parameter.getLogicName());
		left.setBClass(parameter.getBClass());
		left.setModifier(parameter.getModifier());

		invoker.setInvokeChild(parameter);

		var = invoker;
		this.target = parameter;
	}

	public VarLogicList(BClass data, BParameter parameter) {

		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();

		BVariable inparent = view.createVariable();
		inparent.setBClass(data);
		inparent.setLogicName("m" + data.getLogicName().toLowerCase());
		invoker.setInvokeParent(inparent);

		BParameter left = view.createParameter();

		left.setName(parameter.getName());
		left.setLogicName(parameter.getLogicName());
		left.setBClass(parameter.getBClass());
		left.setModifier(parameter.getModifier());

		invoker.setInvokeChild(parameter);

		var = invoker;
		this.target = parameter;

	}

	public VarLogicList(BParameter parent, BParameter parameter) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();
		invoker.setInvokeParent(parent);
		BParameter left = view.createParameter();

		left.setName(parameter.getName());
		left.setLogicName(parameter.getLogicName());
		left.setBClass(parameter.getBClass());
		left.setModifier(parameter.getModifier());

		invoker.setInvokeChild(parameter);

		var = invoker;
		this.target = parameter;

	}

	public VarLogicList(BVariable parent, BVariable target) {
		var = target;
		this.target = parent;

	}

	@Override
	public List<BLogic> getList(BPath parent) {
		List<BLogic> list = new ArrayList<BLogic>();

		VarNullCheckLogic nullcheck = new VarNullCheckLogic(parent, var, target);
		list.add(nullcheck);

		VarNotNullCheckLogic notnullcheck = new VarNotNullCheckLogic(parent, var, target);
		list.add(notnullcheck);

		if (var != null && var.getBClass().getQualifiedName().equals(String.class.getName())) {
			VarCheckLogic sizecheck = new VarSizeCheckLogic(parent, var, target);
			list.add(sizecheck);
		}
		if (var != null && var.getBClass().getQualifiedName().equals(List.class.getName())) {

			BListAddNode node = new BListAddNode();
			ListAddLogic logic = new ListAddLogic(parent, node, var);
			node.setLogic(logic);
			list.add(logic);

			if (var instanceof BVariable) {
				if (var.getBClass().getQualifiedName().equals(List.class.getName())) {
					LoopLogic loop = new LoopLogic(parent, (BVariable) var);
					list.add(loop);
				}
			}
		}
		if (var != null && (var.getBClass().getQualifiedName().equals(Map.class.getName())
				|| var.getBClass().getQualifiedName().equals(Hashtable.class.getName()))) {

			BMapAddNode node = new BMapAddNode();
			MapAddLogic logic = new MapAddLogic(parent, node, var);
			node.setLogic(logic);
			list.add(logic);

			BMapGetNode get = new BMapGetNode();
			MapGetLogic getLogic = new MapGetLogic(parent, get, var);
			get.setLogic(getLogic);
			list.add(getLogic);

		}
		VarValueCheckLogic valuecheck = new VarValueCheckLogic(parent, var, target);

		list.add(valuecheck);
		return list;
	}

}
