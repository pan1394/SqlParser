package com.linkstec.bee.UI.look.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;

public class BeeTableTreeRenderer extends JTree implements TableCellRenderer, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5001815886871011905L;
	protected int visibleRow;
	private JTable table;
	private boolean selected = false;
	private BeeTableNode currentNode;

	public BeeTableTreeRenderer(TreeModel model, JTable table) {
		super(model);
		this.table = table;
		BeeTableTreeCellRenderer renderer = new BeeTableTreeCellRenderer(table);
		this.setCellRenderer(renderer);
		this.setRootVisible(false);
		this.setForeground(Color.BLACK);
		this.setBorder(null);

		this.addMouseListener(this);
	}

	public void doClick() {
		if (currentNode != null) {
			if (currentNode.hasChild()) {
				this.expandPath(this.getPathForRow(visibleRow));
			}
		}
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, table.getHeight());
	}

	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
		int count = this.getRowCount();

		g.setColor(BeeTable.gradientLineColor);
		for (int i = 0; i < count; i++) {
			g.drawRect(0, i * this.getRowHeight() - 2, this.getWidth() - 2, this.getRowHeight());
		}
		g.setColor(BeeTable.lineColor);
		for (int i = 0; i < count; i++) {
			g.drawRect(0, i * this.getRowHeight() - 1, this.getWidth() - 1, this.getRowHeight());
		}

		if (selected) {
			g.setColor(Color.GREEN.darker());
			g.fillRect(0, 0, 3, this.getHeight());
		}
		TreePath path = this.getPathForRow(this.visibleRow);// tree.getPathForLocation(e.getX(), e.getY());
		if (path != null) {
			Object obj = path.getLastPathComponent();
			if (obj instanceof BeeTableNode) {
				currentNode = (BeeTableNode) obj;
			}
		}
		if (this.currentNode != null) {

			String s = this.currentNode.toString();
			int width = g.getFontMetrics().stringWidth(s);
			int height = g.getFontMetrics().getHeight();
			g.setColor(Color.BLACK);
			g.drawString(s, (this.getWidth() - width) / 2, visibleRow * table.getRowHeight() + (table.getRowHeight() - height) / 2 + g.getFontMetrics().getAscent());
			if (this.currentNode.hasChild()) {
				Image image = BeeConstants.TREE_FOLDER_ICON.getImage();
				int w = image.getWidth(this);
				int h = image.getHeight(this);
				g.drawImage(image, w / 2, visibleRow * table.getRowHeight() + (table.getRowHeight() - h) / 2, this);
				// g.setColor(Color.RED);
				// g.fillRect(0, visibleRow * table.getRowHeight(), this.getWidth(),
				// table.getRowHeight());
			}
		}

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 0) {
			if (isSelected) {
				table.setColumnSelectionInterval(1, table.getColumnCount() - 1);
				table.repaint();
			} else {
				int[] seleced = table.getSelectedRows();
				for (int i = 0; i < seleced.length; i++) {
					if (row == seleced[i]) {
						isSelected = true;
					}
				}
			}
		}
		this.selected = isSelected;
		if (isSelected) {
			setBackground(BeeTable.selectedBackgroundColor);
		} else {
			if (value instanceof BeeTableNode) {

				setBackground(BeeTable.backgroundColor);
			} else {
				setBackground(table.getBackground());
			}
		}
		currentNode = null;
		if (value instanceof BeeTableNode) {
			currentNode = (BeeTableNode) value;
		}
		this.setForeground(Color.BLACK);
		visibleRow = row;
		return this;

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		table.clearSelection();
		table.setRowSelectionInterval(visibleRow, visibleRow);
		table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
		doClick();
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		// table.clearSelection();
		// table.setRowSelectionInterval(visibleRow, visibleRow);
		// table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
		// doClick();

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
