package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BJudgeNode;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class VarCheckLogic extends JudgeLogic {
	protected BValuable var;
	protected BParameter target;

	public VarCheckLogic(BPath parent, BValuable var, BValuable target) {
		super(parent);
		this.var = (BValuable) var.cloneAll();
		this.target = (BParameter) target.cloneAll();
		BJudgeNode node = new BJudgeNode(this);
		this.getPath().setCell(node);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1398830748348004738L;

	@Override
	public String getName() {
		return target.getName() + "チェック";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_TRUEFALSE_ICON;
	}

	@Override
	public String getDesc() {
		return target.getName() + "チェック";
	}

	public static class YesLogic extends BasicLogic {

		/**
		 * 
		 */
		private static final long serialVersionUID = -367785662089777661L;
		private VarCheckLogic logic;

		public YesLogic(VarCheckLogic check) {
			super(check.getPath(), check.getPath().getCell());
			this.logic = check;
		}

		@Override
		public String getName() {
			return "チェックがOKの場合";
		}

		@Override
		public List<BLogicUnit> createUnit() {
			return super.createUnit();
		}

		@Override
		public String getDesc() {
			return logic.getDesc() + "がOKの場合";
		}

	}

	public static class NoLogic extends BasicLogic {

		/**
		 * 
		 */
		private static final long serialVersionUID = -367785662089777661L;
		private VarCheckLogic logic;

		public NoLogic(VarCheckLogic check) {
			super(check.getPath(), check.getPath().getCell());
			this.logic = check;
		}

		@Override
		public String getName() {
			return "チェックがNGの場合に";
		}

		@Override
		public String getDesc() {
			return logic.getDesc() + "がNGの場合に";
		}

		@Override
		public List<BLogicUnit> createUnit() {
			return super.createUnit();
		}
	}

}
