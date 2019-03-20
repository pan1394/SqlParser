package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BEndLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BClassEnd extends BasicLogic implements BEndLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1406439581264624207L;

	private BClass bclass;
	private String name;
	private String value;

	public BClassEnd(BPath parent, BClass bclass, String name, String value) {
		super(parent, null);

		BLogicNode node = new BLogicNode(this);
		this.getPath().setCell(node);
		this.bclass = bclass;
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_RETURN_ICON;
	}

	@Override
	public String getDesc() {
		return name;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> list = new ArrayList<BLogicUnit>();

		BVariable v = view.createVariable();
		v.setBClass(bclass);
		v.setLogicName(value);
		v.setName(name);

		// return
		BReturnUnit breturn = view.createMethodReturn();
		breturn.setRuturnValue(v);
		list.add(breturn);

		return list;
	}
}
