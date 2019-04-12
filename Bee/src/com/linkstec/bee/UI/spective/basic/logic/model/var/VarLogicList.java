package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.spective.basic.logic.model.InvokerLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.data.LoopLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BAssignNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BInvokerNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BListAddNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BListGetNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BMapAddNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BMapGetNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;

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

	public VarLogicList(BInvoker invoker) {

		var = invoker;
		this.target = (BVariable) invoker.getInvokeChild();

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
		this.target = target;

	}

	@Override
	public List<BLogic> getList(BPath parent) {
		List<BLogic> list = new ArrayList<BLogic>();

		if (var != null) {
			BAssignNode assign = new BAssignNode(parent, var);
			list.add(assign.getLogic());
		}

		VarNullCheckLogic nullcheck = new VarNullCheckLogic(parent, var, target);
		list.add(nullcheck);

		VarNotNullCheckLogic notnullcheck = new VarNotNullCheckLogic(parent, var, target);
		list.add(notnullcheck);

		if (var != null && var.getBClass().getQualifiedName().equals(String.class.getName())) {
			VarCheckLogic sizecheck = new VarSizeCheckLogic(parent, var, target);
			list.add(sizecheck);
		} else if (var != null && var.getBClass().getQualifiedName().equals(List.class.getName())) {

			BListAddNode node = new BListAddNode();
			ListAddLogic logic = new ListAddLogic(parent, node, var);
			node.setLogic(logic);
			list.add(logic);

			BListGetNode getnode = new BListGetNode();
			ListGetLogic get = new ListGetLogic(parent, getnode, var);
			getnode.setLogic(get);
			list.add(get);

			if (var instanceof BVariable && (!(target instanceof BInvoker))) {
				if (var.getBClass().getQualifiedName().equals(List.class.getName())) {
					LoopLogic loop = new LoopLogic(parent, (BVariable) var);
					list.add(loop);
				}
			}
		} else if (var != null && (var.getBClass().getQualifiedName().equals(Map.class.getName())
				|| var.getBClass().getQualifiedName().equals(Hashtable.class.getName()))) {

			BMapAddNode node = new BMapAddNode();
			MapAddLogic logic = new MapAddLogic(parent, node, var);
			node.setLogic(logic);
			list.add(logic);

			BMapGetNode get = new BMapGetNode();
			MapGetLogic getLogic = new MapGetLogic(parent, get, var);
			get.setLogic(getLogic);
			list.add(getLogic);

		} else if (var != null && (var.getBClass().getQualifiedName().equals(Map.class.getName())
				|| var.getBClass().getQualifiedName().equals(BigDecimal.class.getName()))) {
			IPatternCreator view = PatternCreatorFactory.createView();
			BMethod method = view.createMethod();
			method.setLogicName("setScale");
			method.setName("小数点扱い");

			BVariable returnValue = view.createVariable();

			BClass bclass = CodecUtils.getClassFromJavaClass(BigDecimal.class, parent.getProject());
			returnValue.setBClass(bclass);
			method.setReturn(returnValue);

			BParameter param1 = view.createParameter();
			param1.setBClass(CodecUtils.BInt());
			param1.setLogicName("scale");
			param1.setName("小数点桁数");

			BParameter param2 = view.createParameter();
			param2.setBClass(CodecUtils.BInt());
			param2.setLogicName("style");
			param2.setName("扱い方式");

			BParameter staticParent = view.createParameter();
			staticParent.setBClass(bclass.cloneAll());
			staticParent.setClass(true);

			List<BValuable> values = new ArrayList<BValuable>();
			BInvoker p1 = new ReferNode() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -7483962483466542411L;

				@Override
				public String toString() {
					return ((BVariable) this.getInvokeChild()).getName();
				}
			};

			BVariable p1var = view.createVariable();

			p1var.setBClass(CodecUtils.BInt());
			p1var.setLogicName("ROUND_DOWN");// BigDecimal.ROUND_DOWN;
			p1var.setName("切り捨て");

			p1.setInvokeParent((BValuable) staticParent.cloneAll());
			p1.setInvokeChild(p1var);
			values.add(p1);

			BInvoker p2 = new ReferNode() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -7483962483466542411L;

				@Override
				public String toString() {
					return ((BVariable) this.getInvokeChild()).getName();
				}
			};

			BVariable p2var = view.createVariable();
			p2var.setBClass(CodecUtils.BInt());
			p2var.setLogicName("ROUND_UP"); // BigDecimal.ROUND_UP;
			p2var.setName("切り上げ");
			p2.setInvokeParent((BValuable) staticParent.cloneAll());
			p2.setInvokeChild(p2var);
			values.add(p2);

			BInvoker p3 = new ReferNode() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -7483962483466542411L;

				@Override
				public String toString() {
					return ((BVariable) this.getInvokeChild()).getName();
				}
			};

			BVariable p3var = view.createVariable();
			p3var.setBClass(CodecUtils.BInt());
			p3var.setLogicName("ROUND_HALF_UP"); // BigDecimal.ROUND_HALF_UP
			p3var.setName("四捨五入");
			p3.setInvokeParent((BValuable) staticParent.cloneAll());
			p3.setInvokeChild(p3var);
			values.add(p3);

			// param2.addUserAttribute("PARAMETER_OPTIONS", values);

			BInvokerNode node = new BInvokerNode();
			method.addParameter(param1);
			method.addParameter(param2);

			InvokerLogic logic = new InvokerLogic(parent, node, method, var);
			node.setLogic(logic);
			list.add(logic);

		}

		VarValueCheckLogic valuecheck = new VarValueCheckLogic(parent, var, target);

		list.add(valuecheck);
		return list;
	}

}
