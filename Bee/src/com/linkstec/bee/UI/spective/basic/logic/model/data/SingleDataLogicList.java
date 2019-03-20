package com.linkstec.bee.UI.spective.basic.logic.model.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;

public class SingleDataLogicList extends LogicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037622172154652992L;
	private List<BLogic> list = new ArrayList<BLogic>();
	private BVariable var;

	public SingleDataLogicList(BVariable var) {
		this.var = var;
	}

	@Override
	public List<BLogic> getList(BPath parent) {
		list.clear();
		Check check = new Check(parent, var);
		list.add(check);

		Copy copy = new Copy(parent, var);
		list.add(copy);

		Clear clear = new Clear(parent, var);
		list.add(clear);

		if (var.getBClass().getQualifiedName().equals(List.class.getName())) {
			LoopLogic loop = new LoopLogic(parent, var);
			list.add(loop);
		}

		return list;
	}

	public static class Check extends BasicDataLogic {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1398830748348004738L;

		public Check(BPath parent, BVariable var) {
			super(parent, var);
		}

		@Override
		public String getName() {

			return var.getName() + "チェック";
		}

		@Override
		public ImageIcon getIcon() {
			return super.getIcon();
		}

		public String getDesc() {
			return var.getName() + "をチェックする";
		}

		public List<BLogic> getSubLogics() {
			List<BLogic> list = new ArrayList<BLogic>();
			Check error = new Check(parent, var) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 6864333849280262640L;

				@Override
				public String getName() {
					return "チェックがNGの場合にエラーメッセージを表示する";
				}

				@Override
				public String getDesc() {
					return var.getName() + "をチェックし、" + this.getName();
				}

			};
			Check mcontinue = new Check(parent, var) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 6864333849280262640L;

				@Override
				public String getName() {
					return "チェックがNGの場合に処理を行った上に、継続する";
				}

				@Override
				public String getDesc() {

					return var.getName() + "をチェックし、" + this.getName();
				}

			};
			Check mbreak = new Check(parent, var) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 6864333849280262640L;

				@Override
				public String getName() {
					return "チェックがNGの場合に処理を行った上に、中断する";
				}

				@Override
				public String getDesc() {
					return var.getName() + "をチェックし、" + this.getName();
				}

			};
			list.add(error);
			list.add(mcontinue);
			list.add(mbreak);

			return list;
		}
	}

	public static class Copy extends BasicDataLogic {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3544936056918736830L;

		public Copy(BPath parent, BVariable var) {
			super(parent, var);
		}

		@Override
		public String getName() {
			return var.getName() + "複製";
		}

		@Override
		public ImageIcon getIcon() {
			return super.getIcon();
		}

		public String getDesc() {
			return var.getName() + "をコピーし、新規データを作成する";
		}
	}

	public static class Clear extends BasicDataLogic {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4727849667709821818L;

		public Clear(BPath parent, BVariable var) {
			super(parent, var);
		}

		@Override
		public String getName() {
			return var.getName() + "クリア";
		}

		@Override
		public ImageIcon getIcon() {
			return super.getIcon();
		}

		public String getDesc() {
			return var.getName() + "をクリアする";
		}
	}
}
