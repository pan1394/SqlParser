package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ReturnType;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPropertyDialog;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPropertyDialog.PropertyAction;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BActionPropertyNode extends BNode implements PropertyAction<ReturnType>, ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6978750051978992074L;

	protected NewLayerClassLogic logic;

	private String editedName;

	public BActionPropertyNode(NewLayerClassLogic logic) {
		this.logic = logic;

		mxGeometry geo = this.getGeometry();
		geo.setWidth(50);
		geo.setHeight(20);
		geo.setRelative(true);
		geo.setX(0);
		geo.setY(0);
		geo.setOffset(new mxPoint(-25, -10));

		this.setStyle("rounded=1;strokeWidth=0.5;strokeColor=gray;align=center");
		this.setVertex(true);
		this.setValue(logic.getDesc());
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			this.editedName = (String) value;
			BasicBook.changeEditorName(editedName, this.getLogic().getPath());
		}
		super.setValue(value);
	}

	@Override
	public NewLayerClassLogic getLogic() {
		return logic;
	}

	public void setLogic(NewLayerClassLogic logic) {
		this.logic = logic;
	}

	@Override
	public void clicked(BasicLogicSheet sheet) {

		BActionModel model = (BActionModel) this.logic.getPath().getAction();

		BPropertyDialog d = new BPropertyDialog(this, model.getReturnType(), sheet);
		Application.getInstance().getBasicSpective().getPropeties().setTarget(d, null);

	}

	@Override
	public String getLogicName() {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		return model.getLogicName();
	}

	@Override
	public void setLogicName(String name) {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		model.setLogicName(name);
	}

	@Override
	public void setName(String name) {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		model.setName(name);
		this.setValue(name);
	}

	@Override
	public String getName() {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		return model.getName();
	}

	@Override
	public void applied(BasicLogicSheet sheet) {
		sheet.getGraph().refresh();
	}

	@Override
	public void cancelled(BasicLogicSheet sheet) {

	}

	@Override
	public BEditor editDetail(BasicLogicSheet sheet, ProcessType type) {
		BasicBook book = sheet.findBook();
		if (book != null) {
			BPath path = this.logic.getPath();
			BEditor editor = BLogicEditActions.addNewTypeEditor(sheet.getProject(), path, book, type);
			return editor;
		}
		return null;
	}

	@Override
	public ProcessType getDetailEditName(BasicLogicSheet sheet) {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		int depth = model.getActionDepth();
		ConfigModel config = ConfigModel.load(sheet.getProject());
		ActionModel action = config.getAction(model.getInput().getType(), model.getOutput().getType());
		if (action != null) {
			List<LayerModel> layers = action.getLayers();
			if (depth < layers.size()) {
				LayerModel layer = layers.get(depth);
				ProcessType type = layer.getTargetProcessType();
				return type;
			}
		}
		return null;
	}

	@Override
	public void setSelected(ReturnType value) {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		model.setReturnType(value);
	}

	@Override
	public List<ReturnType> getSelection(ProcessType p) {

		if (p.getType() == ProcessType.TYPE_PROCESS_TABLE) {

			ArrayList<ReturnType> list = new ArrayList<ReturnType>();
			ReturnType[] types = ReturnType.values();
			for (ReturnType type : types) {
				list.add(type);
			}
			return list;
		}
		return null;

	}

	@Override
	public ReturnType getSelelect() {
		BActionModel model = (BActionModel) this.logic.getPath().getAction();
		return model.getReturnType();
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

}
