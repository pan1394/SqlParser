package com.linkstec.bee.UI.look.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import com.linkstec.bee.UI.look.menu.BeeObjectItem;

public class BeeTableStringRenderer implements TableCellRenderer {
	private boolean valid = true;
	private boolean drawTopBorder = false;
	private boolean drawBottomBorder = false;
	private boolean drawLeftBorder = false;
	private boolean drawRightBorder = false;
	private int borderWidth = 3;
	private Color selectedColor = Color.decode("#EAECF5");
	private JLabel label = new JLabel() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 788931668972173649L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (!valid) {
				g.setColor(Color.RED);
				g.setFont(g.getFont().deriveFont(10F));
				String s = this.getText();
				int width = SwingUtilities.computeStringWidth(g.getFontMetrics(), s);
				int count = (int) (width / 2.8);
				for (int i = 0; i < count; i++) {
					g.drawString("^", 2 + i * 5, this.getHeight() + 3);
				}
			}
			g.setColor(Color.GRAY);
			if (drawTopBorder) {
				g.fillRect(0, 0, this.getWidth(), borderWidth);
			}
			if (drawBottomBorder) {
				g.fillRect(0, this.getHeight() - borderWidth, this.getWidth(), borderWidth);
			}
			if (drawLeftBorder) {
				g.fillRect(0, 0, borderWidth, this.getHeight());
			}
			if (drawRightBorder) {
				g.fillRect(this.getWidth() - borderWidth, 0, borderWidth, this.getHeight());
			}
		}

	};

	public BeeTableStringRenderer() {
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		label.setBorder(BeeTable.normalBorder);
		label.setFocusable(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			drawTopBorder = true;
			drawBottomBorder = true;
			drawLeftBorder = true;
			drawRightBorder = true;

			int[] rows = table.getSelectedRows();
			int[] cols = table.getSelectedColumns();
			for (int i = 0; i < rows.length; i++) {
				if (rows[i] == row - 1) {
					drawTopBorder = false;
				} else if (rows[i] == row + 1) {
					drawBottomBorder = false;
				}
			}
			for (int i = 0; i < cols.length; i++) {
				if (cols[i] == column - 1) {
					drawLeftBorder = false;
				} else if (cols[i] == column + 1) {
					drawRightBorder = false;
				}
			}
			label.setBackground(selectedColor);
		} else {
			drawTopBorder = false;
			drawBottomBorder = false;
			drawLeftBorder = false;
			drawRightBorder = false;
			label.setBackground(Color.WHITE);
		}

		BeeTableModelAdapter adapter = (BeeTableModelAdapter) table.getModel();
		BeeTableModel model = adapter.getTreeTableModel();
		label.setIcon(adapter.getIconAt(row, column));
		if (value != null) {
			label.setText(value.toString());
			if (value instanceof List) {

				List list = (List) value;
				if (list.size() == 0) {
					label.setText("");
				}
			}

			List<BeeObjectItem> list = model.getPulldownList(column);
			if (list != null && list.size() > 0) {
				for (BeeObjectItem obj : list) {
					if (obj.getUserObject().equals(value)) {
						label.setText(obj.getText());
						label.setIcon(obj.getIcon());
						break;
					}
				}
			}

		} else {
			label.setText("");
		}

		if (model instanceof BeeTableModel) {
			BeeTableModel tableModel = (BeeTableModel) model;
			this.valid = tableModel.isValid(column, row, value);
			String name = "R" + row + "C" + column;

			int height = table.getRowHeight();
			int line = height * row;
			tableModel.fireError(name, line, valid, "有効なタイプではありません", value);
		}

		return label;
	}

}
