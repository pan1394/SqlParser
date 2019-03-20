package com.linkstec.bee.UI.look.table;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

public class BeeTableModelAdapter extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8253233301927198414L;
	private JTree tree;
	private BeeTableModel treeTableModel;

	public BeeTableModelAdapter(BeeTableModel treeTableModel, JTree tree) {
		this.tree = tree;
		this.treeTableModel = treeTableModel;

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			// Don't use fireTableRowsInserted() here;
			// the selection model would get updated twice.
			public void treeExpanded(TreeExpansionEvent event) {
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				fireTableDataChanged();
			}
		});
	}

	public void deleteColumn(int index) {
		treeTableModel.deleteColumn(index);
	}

	public void addColumn(int index, String name, Class<?> type) {
		treeTableModel.addColumn(index, name, type);

	}

	public BeeTableModel getTreeTableModel() {
		return treeTableModel;
	}

	// Wrappers, implementing TableModel interface.

	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	public String getColumnName(int column) {
		return treeTableModel.getColumnName(column);
	}

	public Class<?> getColumnClass(int column) {
		return treeTableModel.getColumnClass(column);
	}

	public int getRowCount() {
		return tree.getRowCount();
	}

	public Object nodeForRow(int row) {
		TreePath treePath = tree.getPathForRow(row);
		if (treePath == null) {
			return null;
		}
		return treePath.getLastPathComponent();
	}

	public Object getValueAt(int row, int column) {

		return treeTableModel.getValueAt(nodeForRow(row), column);
	}

	public boolean isCellEditable(int row, int column) {
		return treeTableModel.isCellEditable(nodeForRow(row), column);
	}

	public void setValueAt(Object value, int row, int column) {
		treeTableModel.setValueAt(value, nodeForRow(row), column);
		BeeTableUndoEvent event = new BeeTableUndoEvent(value, row, column);
		treeTableModel.getUndo().undoableEditHappened(event);

	}

	public void setIconAt(ImageIcon icon, int row, int column) {
		treeTableModel.setIconAt(icon, nodeForRow(row), column);
	}

	public ImageIcon getIconAt(int row, int column) {
		return treeTableModel.getIconAt(nodeForRow(row), column);
	}
}
