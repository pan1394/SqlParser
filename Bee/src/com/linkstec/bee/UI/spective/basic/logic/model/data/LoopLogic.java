package com.linkstec.bee.UI.spective.basic.logic.model.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BLoopNode;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLoopLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILoopCell;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLoopUnit;

public class LoopLogic extends BasicDataLogic implements BLoopLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 643695831908454970L;
	private static transient IPatternCreator view = PatternCreatorFactory.createView();

	public LoopLogic(BPath parent, BVariable var) {
		super(parent, var);
		BLoopNode node = new BLoopNode(this);
		this.getPath().setCell(node);
	}

	@Override
	public String getName() {
		return var.getName() + "ループ処理";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_LOOP_ICON;
	}

	public String getDesc() {
		return var.getName() + "にループ処理をかける";
	}

	@Override
	public List<BLogicUnit> createUnit() {

		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		BClass bclass = var.getBClass();
		if (bclass.getQualifiedName().equals(List.class.getName())) {

			List<BType> types = bclass.getParameterizedTypes();

			for (BType type : types) {
				if (type instanceof BeeDataModel) {
					BeeDataModel model = (BeeDataModel) type;

					BParameter variable = view.createParameter();
					variable.setBClass(model);
					variable.setLogicName("lm" + model.getLogicName());
					variable.setName(var.getName() + "ループ処理対象データ");
					BLoopUnit unit = view.createLoop();
					unit.setLoopType(BLoopUnit.TYPE_ENHANCED);

					unit.addEnhancedCondition(variable, var);

					ILoopCell cell = (ILoopCell) this.getPath().getCell();
					ILogicCell start = cell.getStart();

					List<BLogic> logics = new ArrayList<BLogic>();

					BasicGenUtils.makeLogics(start, logics, true);

					BLogicBody body = unit.getEditor();
					for (BLogic logic : logics) {
						List<BLogicUnit> uts = logic.createUnit();
						if (uts != null) {
							for (BLogicUnit u : uts) {
								body.addUnit((BLogicUnit) u.cloneAll());
							}
						}
					}

					units.add(unit);
				}
			}
		}
		return units;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();

		BClass bclass = var.getBClass();
		if (bclass.getQualifiedName().equals(List.class.getName())) {

			List<BType> types = bclass.getParameterizedTypes();

			for (BType type : types) {
				if (type instanceof BeeDataModel) {
					BeeDataModel model = (BeeDataModel) type;

					BParameter variable = view.createParameter();
					variable.setBClass(model);
					variable.setLogicName("lm" + model.getLogicName());
					variable.setName(var.getName() + "ループ処理対象データ");

					outputs.add(variable);
				}
			}
		}
		return outputs;
	}

}
