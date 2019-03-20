package com.linkstec.bee.UI.look.table;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.core.Application;

public class BeeTablePopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4066232642516231507L;
	private BeeTable table;

	public BeeTablePopupMenu(BeeTable table) {
		this.table = table;
	}

	public void addDefault() {
		this.removeAll();
		add(bind("Clear", new ClearAction(), "/com/linkstec/bee/UI/images/icons/clear_co.gif", table));
		add(bind("Delete rows selected", new DeteleAction(), BeeConstants.LIST_DELETE_ICON, table));
		add(bind("Add before this row", new AddAction(true), BeeConstants.LIST_ADD_ICON, table));
		add(bind("Add after this row", new AddAction(false), BeeConstants.LIST_ADD_ICON, table));
	}

	public void addPopMenu(String title, Action action, ImageIcon icon) {
		add(bind(title, action, icon, table));
	}

	@Override
	public JMenuItem add(Action a) {
		JMenuItem item = super.add(a);
		item.setPreferredSize(new Dimension(350, 40));
		return item;
	}

	public Action bind(String name, final Action action, String iconUrl, BeeTable tree) {
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(BeeEditor.class.getResource(iconUrl)) : null) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 8551536173889872776L;

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(tree, e.getID(), e.getActionCommand()));
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		return newAction;
	}

	public Action bind(String name, final Action action, ImageIcon icon, BeeTable tree) {
		AbstractAction newAction = new AbstractAction(name, icon) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 8551536173889872776L;

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(tree, e.getID(), e.getActionCommand()));
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		return newAction;
	}

	public static abstract class BeeMenuAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5975924775166313708L;

		@Override
		public void actionPerformed(ActionEvent e) {
			this.action(e);

		}

		public abstract void action(ActionEvent e);

	}

	public static class ClearAction extends BeeMenuAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;

		@Override
		public void action(ActionEvent e) {
			BeeTable tree = (BeeTable) e.getSource();
			int[] cls = tree.getSelectedColumns();
			int[] rows = tree.getSelectedRows();
			for (int c = 0; c < cls.length; c++) {
				for (int r = 0; r < rows.length; r++) {
					tree.setValueAt("", rows[r], cls[c]);
				}
			}
			tree.repaint();
		}

	}

	public static class DeteleAction extends BeeMenuAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;

		@Override
		public void action(ActionEvent e) {
			BeeTable tree = (BeeTable) e.getSource();

			int[] rows = tree.getSelectedRows();

			for (int r = 0; r < rows.length; r++) {
				BeeTableNode node = (BeeTableNode) tree.getValueAt(rows[r], 0);
				node.getParent().removeChild(node);
			}
			tree.clearSelection();
			tree.updateUI();

		}

	}

	public static class AddAction extends BeeMenuAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5354663587336882028L;
		private boolean before;

		public AddAction(boolean before) {
			this.before = before;
		}

		@Override
		public void action(ActionEvent e) {
			BeeTable tree = (BeeTable) e.getSource();

			int[] rows = tree.getSelectedRows();
			BeeTableNode[] targets = new BeeTableNode[rows.length];

			int[] added = new int[targets.length];
			int i = 0;
			for (int r = 0; r < rows.length; r++) {
				if (before) {
					if (rows[r] > 1) {
						targets[r] = (BeeTableNode) tree.getValueAt(rows[r], 0);
					} else {
						targets[r] = (BeeTableNode) tree.getValueAt(1, 0);
					}
				} else {
					if (rows[r] + 1 < tree.getRowCount()) {
						targets[r] = (BeeTableNode) tree.getValueAt(rows[r] + 1, 0);
					} else {
						targets[r] = (BeeTableNode) tree.getValueAt(tree.getRowCount(), 0);
					}
				}
				this.addRow(targets[r]);
				added[i] = targets[r].getIndex();
				i++;
			}
			tree.updateUI();
			for (int index : added) {
				tree.setRowSelectionInterval(index - 1, index - 1);
				tree.setColumnSelectionInterval(1, tree.getColumnCount() - 1);
			}

			Application.TODO("bulk selection after added,bulk add action");
		}

		private void addRow(BeeTableNode node) {
			int index = node.getIndex();
			BeeTableNode n = new BeeTableNode(node.getParent(), node.getColumnCount());
			node.getParent().addChildAt(n, index);
		}

	}

}
