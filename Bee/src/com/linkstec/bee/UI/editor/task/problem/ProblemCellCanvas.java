package com.linkstec.bee.UI.editor.task.problem;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.linkstec.bee.UI.BeeConstants;

public class ProblemCellCanvas extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2742287519539659515L;
	private Object value;

	@Override
	public String getToolTipText() {
		if (value == null) {
			return null;
		}
		return "<html>" + value.toString() + "</html>";
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (column == 0) {
			c.setIcon(BeeConstants.ERROR_ICON);
		} else {
			c.setIcon(null);
		}
		this.value = value;
		return c;
	}

}
