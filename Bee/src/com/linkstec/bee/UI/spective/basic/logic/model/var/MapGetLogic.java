package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

public class MapGetLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5458032518528425131L;

	private BValuable target;
	private BValuable key;
	private BParameter left;

	public MapGetLogic(BPath parent, ILogicCell cell, BValuable var) {
		super(parent, cell);
		this.target = var;

	}

	@Override
	public String getName() {

		return this.getDesc();
	}

	public BValuable getTarget() {
		return this.target;
	}

	@Override
	public String getDesc() {
		if (target != null) {
			if (this.key != null) {
				return BValueUtils.createValuable(target, false) + "から「" + BValueUtils.createValuable(key, false)
						+ "」で値を取得する";
			}
			return BValueUtils.createValuable(target, false) + "から[DragIn]で値を取得する";
		} else {
			return "";
		}
	}

	public void setKey(BValuable key) {
		this.key = key;
	}

	public BValuable getKey() {
		return this.key;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		if (this.key != null) {
			IPatternCreator view = PatternCreatorFactory.createView();
			BInvoker invoker = view.createMethodInvoker();
			if (this.target instanceof BVariable) {
				BVariable var = (BVariable) this.target;
				var.setClass(false);
				var.setCaller(true);
			}
			invoker.setInvokeParent(this.target);
			BMethod method = view.createMethod();
			method.setLogicName("get");
			method.setName("値取得");

			BClass keyClass = null;
			BClass valueClass = null;

			List<BType> types = this.target.getBClass().getParameterizedTypes();
			int index = 0;
			for (BType type : types) {
				if (type instanceof BClass) {
					if (index == 0) {
						keyClass = (BClass) type;
					} else {
						valueClass = (BClass) type;
					}
					index++;
				}
			}

			if (keyClass != null && valueClass != null) {

				BParameter param = view.createParameter();
				param.setBClass(keyClass.cloneAll());
				param.setLogicName("key");
				param.setName("キー");

				BVariable var = view.createVariable();
				var.setBClass(valueClass.cloneAll());
				method.setReturn(var);
				method.addParameter(param);
				invoker.setInvokeChild(method);

				invoker.addParameter(key);

				BAssignment assign = view.createAssignment();
				this.makeleft(valueClass);

				this.addMark(left);

				assign.setLeft(left);
				assign.setRight(invoker, null);

				units.add(assign);
			}
		}
		return units;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.MAP_ICON;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();

		if (this.key != null) {
			BClass valueClass = null;

			List<BType> types = this.target.getBClass().getParameterizedTypes();
			int index = 0;
			for (BType type : types) {
				if (type instanceof BClass) {
					if (index == 0) {

					} else {
						valueClass = (BClass) type;
					}
					index++;
				}
			}

			this.makeleft(valueClass);

			this.addMark(left);
			outputs.add(left);
		}

		return outputs;
	}

	private void makeleft(BClass valueClass) {
		IPatternCreator view = PatternCreatorFactory.createView();
		if (left == null) {
			left = view.createParameter();
			left.setBClass(valueClass.cloneAll());
			left.setLogicName("mMapValue" + valueClass.getLogicName());
			left.setName("マップから取得した" + valueClass.getName());
		}
	}

	@Override
	public JComponent getEditor() {

		if (this.key == null) {
			return null;
		}

		BClass valueClass = null;

		List<BType> types = this.target.getBClass().getParameterizedTypes();
		int index = 0;
		for (BType type : types) {
			if (type instanceof BClass) {
				if (index == 0) {

				} else {
					valueClass = (BClass) type;
				}
				index++;
			}
		}

		BeeGraphSheet comp = new BeeGraphSheet(this.getPath().getProject());
		mxIEventListener undo = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				BEditor editor = Application.getInstance().getCurrentEditor();
				editor.updateView();
			}
		};

		comp.getGraph().getView().addListener(mxEvent.UNDO, undo);
		comp.getGraph().getModel().addListener(mxEvent.UNDO, undo);

		IPatternCreator view = PatternCreatorFactory.createView();
		BAssignment assign = view.createAssignment();
		this.makeleft(valueClass);
		assign.setLeft(left);

		BasicNode node = (BasicNode) assign;
		node.getGeometry().setX(100);
		node.getGeometry().setRelative(false);
		LayoutUtils.layoutNode(node);
		comp.getGraph().addCell(node);
		return comp;
	}

}
