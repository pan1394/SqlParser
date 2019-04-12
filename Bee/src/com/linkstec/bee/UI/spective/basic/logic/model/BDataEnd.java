package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BEndLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BDataEnd extends BasicLogic implements BEndLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6984522740515825817L;

	private BasicDataModel model;

	public BDataEnd(BPath parent, BasicDataModel model, BLogicNode cell) {
		super(parent, cell);
		this.model = (BasicDataModel) model.cloneAll();

	}

	@Override
	public String getName() {
		return model.getName();
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_RETURN_ICON;
	}

	@Override
	public String getDesc() {
		return model.getName();
	}

	@Override
	public List<BLogicUnit> createUnit() {
		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> list = new ArrayList<BLogicUnit>();

		// new instance
		BAssignment var = BasicGenUtils.createInstance(model,
				Application.getInstance().getBasicSpective().getSelection().getProvider());

		list.add(var);

		// return
		BReturnUnit breturn = view.createMethodReturn();
		breturn.setRuturnValue((BValuable) var.getLeft().cloneAll());
		list.add(breturn);
		return list;
	}
}
