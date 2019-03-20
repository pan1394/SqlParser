package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BJudgeNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BJudgeLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IJudgeCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.impl.basic.BasicLogic;

/**
 * used by many condition together
 * 
 * @author linkage
 *
 */
public class JudgeLogic extends BasicLogic implements BJudgeLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4838316156747293749L;
	protected List<Object> list = new ArrayList<Object>();
	private List<BInvoker> invokers = new ArrayList<BInvoker>();

	public JudgeLogic(BPath parent) {
		super(parent, null);
		BJudgeNode node = new BJudgeNode(this);
		this.getPath().setCell(node);
	}

	@Override
	public String getName() {
		return "";
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_CHOICE_ICON;
	}

	@Override
	public String getDesc() {
		String s = "";
		for (Object obj : list) {
			if (obj instanceof BLogic) {
				BLogic logic = (BLogic) obj;
				s = s + logic.getDesc();
			} else if (obj instanceof BLogiker) {
				BLogiker logiker = (BLogiker) obj;
				s = s + "\r\n" + logiker.toString() + " ";
			}
		}
		return s;
	}

	@Override
	public List<BLogic> getYes() {
		List<BLogic> logics = new ArrayList<BLogic>();
		ILogicCell c = this.getPath().getCell();
		if (c instanceof IJudgeCell) {
			IJudgeCell judge = (IJudgeCell) c;
			List<ILogicCell> cells = judge.getYes();
			for (ILogicCell cell : cells) {
				logics.add(cell.getLogic());
			}
		}

		return logics;
	}

	@Override
	public List<BLogic> getNo() {

		List<BLogic> logics = new ArrayList<BLogic>();

		ILogicCell c = this.getPath().getCell();
		if (c instanceof IJudgeCell) {
			IJudgeCell judge = (IJudgeCell) c;
			List<ILogicCell> cells = judge.getNo();
			for (ILogicCell cell : cells) {
				if (cell != null) {
					logics.add(cell.getLogic());
				}
			}
		}
		return logics;
	}

	@Override
	public List<BLogicUnit> createUnit() {

		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		BValuable value = this.getExpression(null);

		if (value != null) {
			BValuable result = value;

			if (value instanceof BExpression) {
				BExpression ex = (BExpression) value;
				if (ex.getExRight() == null) {
					result = ex.getExLeft();
				}
			}

			// BExpression result = (BExpression) ex.getExLeft();
			BConditionUnit condition = view.createCondition();
			condition.setCondition(result);

			BMultiCondition ifs = view.createMultiCondition();
			ifs.addCondition(condition);

			this.makeBranch(condition, ifs);

			units.add(ifs);
		}

		return units;

	}

	@Override
	public boolean isReturnBoolean() {
		return true;
	}

	protected void makeBranch(BConditionUnit condition, BMultiCondition ifs) {
		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogic> yesLogic = this.getYes();
		if (yesLogic != null && yesLogic.size() > 0) {
			for (BLogic logic : yesLogic) {
				List<BLogicUnit> us = logic.createUnit();
				for (BLogicUnit u : us) {
					condition.getLogicBody().addUnit(u);
				}
			}
		}

		List<BLogic> noLogic = this.getNo();
		if (noLogic != null && noLogic.size() > 0) {
			BConditionUnit last = view.createCondition();
			last.setLast(true);
			for (BLogic logic : noLogic) {
				List<BLogicUnit> us = logic.createUnit();
				for (BLogicUnit u : us) {
					last.getLogicBody().addUnit(u);
				}
			}
			ifs.addCondition(last);
		}
	}

	protected BValuable getExpression(ITableSql tsql) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BExpression ex = view.createExpression();
		boolean isLogiker = false;
		for (Object obj : list) {
			if (obj instanceof BLogic) {
				BLogic logic = (BLogic) obj;
				BMultiCondition value = (BMultiCondition) logic.createUnit().get(0);
				BValuable condition = value.getConditionUnits().get(0).getCondition();
				if (isLogiker) {
					ex.setExRight(condition);
					BExpression old = ex;

					ex = view.createExpression();
					ex.setExLeft(old);
				} else {
					ex.setExLeft(condition);
				}
				isLogiker = false;
			} else if (obj instanceof BLogiker) {
				BLogiker logiker = (BLogiker) obj;
				ex.setExMiddle(logiker);
				isLogiker = true;
			}
			if (tsql != null) {
				tsql.getInfo().setEqualsExceptedExpression();
			}
		}
		return ex;
	}

	@Override
	public BLogic getLogic() {
		return this;
	}

	@Override
	public String getSql() {
		BValuable value = this.getExpression(null);
		if (value instanceof BExpression) {
			BExpression ex = (BExpression) value;
			return SQLMakeUtils.getSQL(ex);

		} else {
			return "No SQL";
		}
	}

	@Override
	public String getLogicSql(ITableSql tsql) {
		BValuable value = this.getExpression(null);
		if (value instanceof BExpression) {
			BExpression ex = (BExpression) value;
			return SQLMakeUtils.getLogicSQL(ex, tsql.getInvokers());

		} else {
			return "No SQL";
		}
	}

	@Override
	public List<BInvoker> getInvokers() {
		return this.invokers;
	}

}
