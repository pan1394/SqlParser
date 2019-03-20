package com.linkstec.bee.UI.look.table;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class BeeTableTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5362399156794355958L;
	private JTable table;
	private boolean selected = false;

	public BeeTableTreeCellRenderer(JTable table) {
		this.table = table;
		this.setOpaque(true);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		int[] seleced = table.getSelectedRows();

		for (int i = 0; i < seleced.length; i++) {

			if (row == seleced[i]) {
				sel = true;
			}
		}
		selected = sel;
		if (sel) {
			this.setBackground(BeeTable.selectedBackgroundColor);
		} else {
			this.setBackground(BeeTable.backgroundColor);
		}

		return this;
	}
}