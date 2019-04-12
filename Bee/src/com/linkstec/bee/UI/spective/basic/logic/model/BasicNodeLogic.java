package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

public class BasicNodeLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5613798677579455558L;

	public BasicNodeLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	@Override
	public String getName() {
		return this.getDesc();
	}

	@Override
	public String getDesc() {
		BDetailNodeWrapper wrapper = (BDetailNodeWrapper) this.getPath().getCell();
		BasicNode node = wrapper.getNode();
		return node.getNodeDesc();
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		BDetailNodeWrapper wrapper = (BDetailNodeWrapper) this.getPath().getCell();
		BasicNode node = wrapper.getNode();

		if (node instanceof BAssignment) {
			BAssignment assign = (BAssignment) node;

			IPatternCreator view = PatternCreatorFactory.createView();
			BParameter para = assign.getLeft();

			///////////////////////////////////////////////
			BClass bclass = para.getBClass();
			if (bclass.getQualifiedName().equals(List.class.getName())) {
				List<BType> types = bclass.getParameterizedTypes();
				for (BType type : types) {
					if (type instanceof BClass) {
						types.remove(type);
						bclass.setParameterTypes(types);
						break;
					}
				}
			}

			///////////////////////////////////////////////

			BNote note = view.createComment();
			note.setNote("変数「" + para.getName() + "」を作っておく");
			note.addUserAttribute("DESC", note.getNote());
			units.add(note);
			units.add(assign);

			this.addMark(assign.getLeft());

			return units;
		}

		return super.createUnit();
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();
		BDetailNodeWrapper wrapper = (BDetailNodeWrapper) this.getPath().getCell();
		BasicNode node = wrapper.getNode();

		if (node instanceof BAssignment) {
			BAssignment assign = (BAssignment) node;
			outputs.add(assign.getLeft());
			this.addMark(assign.getLeft());
		}
		return outputs;
	}

	@Override
	public JComponent getEditor() {

		BeeGraphSheet comp = new BeeGraphSheet(this.getPath().getProject());
		mxIEventListener undo = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				BEditor editor = Application.getInstance().getCurrentEditor();
				editor.updateView();
			}
		};

		comp.getGraph().getView().addListener(mxEvent.UNDO, undo);
		comp.getGraph().getModel().addListener(mxEvent.UNDO, undo);

		BDetailNodeWrapper wrapper = (BDetailNodeWrapper) this.getPath().getCell();
		BasicNode node = wrapper.getNode();
		node.getGeometry().setX(100);
		node.getGeometry().setRelative(false);
		LayoutUtils.layoutNode(node);
		comp.getGraph().addCell(node);
		return comp;
	}

}
