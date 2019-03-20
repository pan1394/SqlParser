package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.BLogicGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BWrpperLogic;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BGroupLogic extends BasicLogic implements BWrpperLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398338305042708578L;

	public BGroupLogic(BPath parent) {
		super(parent, null);
		BLogicGroupNode cell = new BLogicGroupNode(this);
		this.getPath().setCell(cell);
	}

	@Override
	public List<BLogicUnit> createUnit() {
		return null;
	}

	@Override
	public List<BLogicUnit> getStartLogics() {
		BLogicGroupNode cell = (BLogicGroupNode) this.getPath().getCell();
		if (cell.getParent() instanceof BNode) {
			List<BLogicUnit> units = new ArrayList<BLogicUnit>();
			IPatternCreator view = PatternCreatorFactory.createView();
			BNote note = view.createComment();
			note.setNote(cell.getTitle() + "[開始]");
			units.add(note);
			return units;
		}
		BLogicProvider provider = this.getPath().getProvider();
		if (provider != null) {
			return provider.getGroupStartLogics(cell.getTitle());
		}
		return null;
	}

	@Override
	public List<BLogicUnit> getEndLogics() {
		BLogicGroupNode cell = (BLogicGroupNode) this.getPath().getCell();
		if (cell.getParent() instanceof BNode) {
			List<BLogicUnit> units = new ArrayList<BLogicUnit>();
			IPatternCreator view = PatternCreatorFactory.createView();
			BNote note = view.createComment();
			note.setNote(cell.getTitle() + "[終了]");
			units.add(note);
			return units;
		}

		BLogicProvider provider = this.getPath().getProvider();
		if (provider != null) {
			return provider.getGroupEndLogics(cell.getTitle());
		}
		return null;
	}

}
