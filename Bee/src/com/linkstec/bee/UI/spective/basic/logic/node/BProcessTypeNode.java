package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IUnitCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BProcessTypeNode extends BActionPropertyNode implements IUnitCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5381416086952927292L;

	public BProcessTypeNode(NewLayerClassLogic logic) {

		super(logic);

		this.setStyle(
				"shape=rectangle;align=center;rounded=1;fontColor=white;strokeWidth=0.5;strokeColor=black;align=center;fillColor="
						+ BeeConstants.ELEGANT_BLUE_COLOR);

		mxGeometry geo = this.getGeometry();
		geo.setWidth(100);
		geo.setHeight(50);
		geo.setRelative(false);
		geo.setX(0);
		geo.setY(0);
		geo.setOffset(new mxPoint(0, 0));

	}

	@Override
	public BEditor editDetail(BasicLogicSheet sheet, ProcessType type) {
		BEditor editor = super.editDetail(sheet, type);
		sheet.getGraph().refresh();

		return editor;
	}

	@Override
	public ProcessType getDetailEditName(BasicLogicSheet sheet) {
		BActionModel action = (BActionModel) logic.getPath().getAction();
		return action.getProcessType();
	}

	@Override
	public void added(BasicLogicSheet sheet) {
		if (sheet instanceof BPatternSheet) {
			BPatternSheet pattern = (BPatternSheet) sheet;
			BPatternModel model = (BPatternModel) pattern.getEditorModel();
			BPath parent = model.getActionPath();
			if (parent != null) {
				BActionModel action = (BActionModel) parent.getAction();
				ActionModel am = action.getProcessModel();
				List<LayerModel> layers = am.getLayers();
				ProcessType type = layers.get(action.getActionDepth() + 1).getTargetProcessType();
				BActionModel newAction = new BActionModel(am, action, type);
				newAction.setName(type.getName());
				newAction.setLogicName("doExecute");
				this.getLogic().getPath().setAction(newAction);
			}
		}
		FontMetrics metrics = sheet.getFontMetrics(sheet.getFont());
		this.reshape(metrics);
	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		FontMetrics metrics = sheet.getFontMetrics(sheet.getFont());
		this.reshape(metrics);
	}

	@Override
	public void applied(BasicLogicSheet sheet) {
		FontMetrics metrics = sheet.getFontMetrics(sheet.getFont());
		this.reshape(metrics);
		super.applied(sheet);

	}

	@Override
	public void reshape(FontMetrics metrics) {
		String value = (String) this.getValue();
		value = value.replace("\r\n", "");
		String oldValue = value;
		int length = value.length();
		double width = this.getGeometry().getWidth() * 2;

		List<String> list = new ArrayList<String>();

		int index = 0;
		for (int i = 5; i < length; i++) {
			String s = value.substring(0, i);
			int l = metrics.stringWidth(s);
			if (l > width) {
				list.add(value.substring(0, i - 1));
				value = value.substring(i - 1);
				length = value.length();
				index = index + i - 1;
				i = 0;

			}
		}
		if (index > 0 && index < oldValue.length()) {
			list.add(oldValue.substring(index));
		}

		String finalValue = "";
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (i != list.size() - 1) {
				finalValue = finalValue + s + "\r\n";
			} else {
				finalValue = finalValue + s;
			}
		}

		if (list.size() > 1) {
			this.setValue(finalValue);
		} else {
			this.setValue(oldValue);
		}

	}

}
