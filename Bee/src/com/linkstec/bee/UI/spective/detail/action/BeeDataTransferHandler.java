package com.linkstec.bee.UI.spective.detail.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import com.linkstec.bee.UI.look.table.BeeTable;
import com.linkstec.bee.UI.look.table.BeeTableModel;
import com.linkstec.bee.UI.look.table.BeeTableModelAdapter;

import sun.awt.datatransfer.ClipboardTransferable;

public class BeeDataTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5454731186109817517L;
	private BeeTable table;

	public BeeDataTransferHandler(BeeTable table) {
		this.table = table;
		table.setTransferHandler(this);
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		super.exportAsDrag(comp, e, action);
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return true;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return true;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {

		if (t instanceof ClipboardTransferable) {
			try {
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				this.table.copy(s);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {

			BeeTableModelAdapter adapter = (BeeTableModelAdapter) table.getModel();
			BeeTableModel model = adapter.getTreeTableModel();

			try {
				CellData[][] data = (CellData[][]) t.getTransferData(BeeDataTransferable.valueFlavor);
				int[] rows = table.getSelectedRows();
				int[] cols = table.getSelectedColumns();
				int startRow = rows[0];

				int scope = rows.length;

				int rowRun = 0;
				while (rowRun < scope) {
					startRow = rows[rowRun];
					for (int i = 0; i < data.length; i++) {
						CellData[] rs = data[i];
						int startColumn = cols[0];
						for (int j = 0; j < rs.length; j++) {

							CellData cell = rs[j];
							Object value = cell.getValue();
							int column = startColumn + j;// cell.getColumn();
							table.getModel().setValueAt(value, startRow + i, column);
						}
						rowRun++;

					}
				}

				table.repaint();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return super.importData(comp, t);
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		return this.createTransferable();
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

	public BeeDataTransferable createTransferable() {
		StringBuffer sbf = new StringBuffer();
		int numcols = table.getSelectedColumnCount();
		int numrows = table.getSelectedRowCount();
		int[] rowsselected = table.getSelectedRows();
		int[] colsselected = table.getSelectedColumns();

		if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length) &&

				(numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] && numcols == colsselected.length))) {
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);

			return null;
		}
		CellData[][] values = new CellData[numrows][numcols];
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {

				Object value = table.getValueAt(rowsselected[i], colsselected[j]);
				CellData cell = new CellData();
				cell.setColumn(colsselected[j]);
				cell.setRow(rowsselected[i]);
				cell.setValue(value);
				values[i][j] = cell;

				value = ((value == null) ? "" : value.toString());
				value = ((value == null) ? "" : value.toString());
				value = (value.equals("null") ? "" : value);
				sbf.append(value);
				if (j < numcols - 1) {
					sbf.append("\t");
				}
			}
			sbf.append("\n");
		}

		BeeDataTransferable t = new BeeDataTransferable(sbf.toString());
		t.setValues(values);
		return t;
	}

	public static class CellData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9111550109898343909L;
		private Object value;
		private int column;
		private int row;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		super.exportDone(source, data, action);
	}

}
