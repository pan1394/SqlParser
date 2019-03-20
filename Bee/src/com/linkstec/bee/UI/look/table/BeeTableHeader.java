package com.linkstec.bee.UI.look.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class BeeTableHeader extends JTableHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6951001484706660422L;

	private int curentColumn = -1;
	private BeeTable table;

	public BeeTableHeader(BeeTable table) {
		super(table.getColumnModel());
		this.table = table;

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (!e.isShiftDown()) {
					table.clearSelection();
				}
				int pick = columnAtPoint(e.getPoint());

				table.setColumnSelectionInterval(pick, pick);
				table.setRowSelectionInterval(0, table.getRowCount() - 1);
			}
		});
		initialize();
		ToolTipManager.sharedInstance().registerComponent(this);

	}

	public int getCurentColumn() {
		return curentColumn;
	}

	public void setCurentColumn(int curentColumn) {
		this.curentColumn = curentColumn;
	}

	public void initialize() {
		TableColumnModel cm = table.getColumnModel();
		int count = cm.getColumnCount();
		for (int i = 0; i < count; i++) {
			cm.getColumn(i).setHeaderRenderer(new BeeTableHeaderRenderer(table, i, this));

		}
	}

	@Override
	public String getToolTipText() {
		Point p = this.getMousePosition();
		if (p != null) {
			int count = this.table.getColumnCount();
			for (int i = 0; i < count; i++) {
				Rectangle rect = this.getHeaderRect(i);
				if (rect.contains(p)) {
					Object obj = this.getColumnModel().getColumn(i).getHeaderValue();
					if (obj != null) {
						return obj.toString();
					}
				}
			}
		}
		return super.getToolTipText();
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		BeeTableModelAdapter model = (BeeTableModelAdapter) this.getTable().getModel();
		if (curentColumn == -1) {
			return null;
		}
		BeeTableModel tableModel = model.getTreeTableModel();

		JPopupMenu menu = new JPopupMenu();
		String name = tableModel.getAddColumnBeforeButtonName();
		this.makeMenu(menu, tableModel, curentColumn, name, model);

		name = tableModel.getAddColumnAfterButtonName();
		this.makeMenu(menu, tableModel, curentColumn + 1, name, model);

		name = tableModel.getDleteColumnButtonName();
		this.makeDeleteMenu(menu, tableModel, curentColumn, name, model);
		return menu;
	}

	private JMenuItem makeMenu(JPopupMenu menu, BeeTableModel tableModel, int location, String name, BeeTableModelAdapter model) {
		JMenuItem item = menu.add(new AbstractAction() {

			/**
			*
			*/
			private static final long serialVersionUID = -5544254534759673098L;

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = JOptionPane.showInputDialog(name);

				if (!tableModel.beforeColumnAdd(location, text)) {
					return;
				}

				table.addColumn(location, text);
			}

		});

		item.setName(name);
		item.setText(name);
		item.setToolTipText(name);
		item.setIcon(BeeConstants.ADD_ICON);
		return item;
	}

	private JMenuItem makeDeleteMenu(JPopupMenu menu, BeeTableModel tableModel, int location, String name, BeeTableModelAdapter model) {
		JMenuItem item = menu.add(new AbstractAction() {

			/**
			*
			*/
			private static final long serialVersionUID = -5544254534759673098L;

			@Override
			public void actionPerformed(ActionEvent e) {
				table.deleteColum(location);
			}

		});

		item.setName(name);
		item.setText(name);
		item.setToolTipText(name);
		item.setIcon(BeeConstants.DELETE_ICON);
		return item;
	}

	@Override
	public TableColumn getDraggedColumn() {
		TableColumn c = super.getDraggedColumn();
		return c;
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		int first = e.getFirstIndex();
		int last = e.getLastIndex();
		for (int i = first; i <= last; i++) {

		}
		super.columnSelectionChanged(e);
	}

	public static class BeeTableHeaderRenderer extends DefaultTableCellRenderer {

		private int index;
		private JLabel label;
		private BeeTable table;
		private boolean selected = false;
		private BeeTableHeader header;

		public BeeTableHeaderRenderer(BeeTable table, int index, BeeTableHeader header) {
			this.table = table;
			this.index = index;
			this.header = header;
			this.label = new JLabel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 7237730731794569080L;

				@Override
				protected void paintBorder(Graphics g) {
					g.setColor(BeeTable.lineColor);
					g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
					g.setColor(BeeTable.gradientLineColor);
					g.drawRect(0, 0, this.getWidth() - 2, this.getHeight() - 2);
				}

				@Override
				public void paint(Graphics g) {
					g.setColor(this.getBackground());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					super.paint(g);
					if (selected) {
						g.setColor(Color.GREEN.darker());
						g.fillRect(0, 0, this.getWidth(), 3);
					}
				}

			};

			this.label.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 30, BeeUIUtils.getDefaultFontSize() * 2));
			this.label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 277709744112816029L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			int[] cs = table.getSelectedColumns();
			for (int i = 0; i < cs.length; i++) {
				if (cs[i] == column) {
					isSelected = true;
				}
			}
			if (isSelected) {
				header.setCurentColumn(index);
			}
			selected = isSelected;
			if (isSelected) {
				label.setBackground(BeeTable.selectedBackgroundColor);
			} else {
				label.setBackground(BeeTable.backgroundColor);
			}
			label.setText(value.toString());

			header.repaint();
			return label;
		}

	}

}
