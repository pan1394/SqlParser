package com.linkstec.bee.UI.look.table;

import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.linkstec.bee.UI.look.table.BeeTableHeader.BeeTableHeaderRenderer;

public class BeeTableColumnModel extends DefaultTableColumnModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2922747910790516963L;

	public void addColumn(int index, TableColumn column) {
		tableColumns.add(index, column);
		column.addPropertyChangeListener(this);
		totalColumnWidth = -1;

		// Post columnAdded event notification
		fireColumnAdded(new TableColumnModelEvent(this, index, index));
	}

	public void removeColumn(int index, BeeTable table) {
		if (index != -1) {
			// Adjust for the selection
			if (selectionModel != null) {
				selectionModel.removeIndexInterval(index, index);
			}

			TableColumn column = this.getColumn(index);

			column.removePropertyChangeListener(this);
			tableColumns.removeElementAt(index);
			totalColumnWidth = -1;

			for (int i = 0; i < tableColumns.size(); i++) {
				TableColumn c = tableColumns.get(i);
				c.setModelIndex(i);
				c.setHeaderRenderer(new BeeTableHeaderRenderer(table, i, (BeeTableHeader) table.getTableHeader()));
			}

			// Post columnAdded event notification. (JTable and JTableHeader
			// listens so they can adjust size and redraw)
			fireColumnRemoved(new TableColumnModelEvent(this, index, index));
		}

	}

	@Override
	public TableColumn getColumn(int index) {
		if (index < 0) {
			return null;
		}
		TableColumn c = super.getColumn(index);

		return c;
	}

}
