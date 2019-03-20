package com.linkstec.bee.UI.look.tree;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.icon.BeeIcon;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.mxgraph.util.mxResources;

public class BeeTreePopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7663575889861092345L;
	private BeeTree tree;

	public BeeTreePopupMenu(BeeTree tree) {
		this.tree = tree;
		this.init();
	}

	public void init() {
		this.removeAll();
		add(BeeActions.bind("削除", new DeleteAction(tree), BeeConstants.DELETE_ICON, null));
		add(BeeActions.bind(mxResources.get("refresh"), new RefreshAction(tree), BeeConstants.REFRESH_ICON, null));
	}

	@Override
	public JMenuItem add(Action a) {
		Object obj = a.getValue(Action.SMALL_ICON);
		if (obj instanceof BeeIcon) {
			BeeIcon icon = (BeeIcon) obj;
			icon = (BeeIcon) icon.clone();
			// icon.setTopMargin(BeeUIUtils.getDefaultFontSize() / 3);
			a.putValue(Action.SMALL_ICON, icon);
		}

		JMenuItem item = super.add(a);
		// item.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 10,
		// BeeUIUtils.getDefaultFontSize() * 2));
		return item;
	}

	public static class DeleteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;
		private BeeTree tree;

		public DeleteAction(BeeTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (tree instanceof BeeTreeAction) {
				BeeTreeAction action = (BeeTreeAction) tree;
				action.delete();
			}
			if (tree instanceof BeeTree) {
				BeeTree bee = (BeeTree) tree;
				List<BeeTreeAction> actions = bee.getPopActions();
				for (BeeTreeAction action : actions) {
					action.delete();
				}
			}
		}

	}

	public static class RefreshAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;
		private BeeTree tree;

		public RefreshAction(BeeTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (tree instanceof BeeTreeAction) {
				BeeTreeAction action = (BeeTreeAction) tree;
				action.refresh();
			}
			if (tree instanceof BeeTree) {
				BeeTree bee = (BeeTree) tree;
				List<BeeTreeAction> actions = bee.getPopActions();
				for (BeeTreeAction action : actions) {
					action.refresh();

				}
			}
		}

	}

}
