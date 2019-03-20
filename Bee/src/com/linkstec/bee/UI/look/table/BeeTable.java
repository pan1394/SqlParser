package com.linkstec.bee.UI.look.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.annotation.Annotation;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.table.BeeTableHeader.BeeTableHeaderRenderer;
import com.linkstec.bee.UI.spective.detail.data.BeeDataAnnotation;

public class BeeTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 310630191014904987L;
	protected BeeTableTreeRenderer tree;

	public static Icon folderIcon = BeeConstants.TREE_FOLDER_ICON;
	public static Icon nodeIcon = BeeConstants.TREE_NODE_ICON;
	public static Color lineColor = Color.decode("#F0F0F0");
	public static Color gradientLineColor = Color.decode("#E8F1FB");
	public static Color backgroundColor = Color.decode("#FCFCFC");
	// public static Color selectedBackgroundColor = Color.decode("#3399FF");
	public static Color selectedBackgroundColor = Color.decode("#FFFFCC");
	public static Border selectedBorder = new LineBorder(Color.GRAY, 1);
	public static Border normalBorder = new LineBorder(lineColor);
	private BeeTableUndo undo;

	public static interface PopListener {
		public void beforeShowup(BeeTablePopupMenu menu, int[] column, int[] row);
	}

	private PopListener listener;

	public PopListener getListener() {
		return listener;
	}

	public void setListener(PopListener listener) {
		this.listener = listener;
	}

	// protected void installKeyboardActions() {
	//
	// KeyStroke undo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK,
	// false);
	// KeyStroke redo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK,
	// false);
	// this.registerKeyboardAction(new UndoAction(this), "undo", undo,
	// JComponent.WHEN_FOCUSED);
	// this.registerKeyboardAction(new RedoAction(this), "redo", redo,
	// JComponent.WHEN_FOCUSED);
	//
	// }

	private TreeTableCellEditor treeEditor = new TreeTableCellEditor();
	private BeeTableStringCellEditor stringEditor = new BeeTableStringCellEditor(this, null);
	private BeeTableStringCellEditor annoEditor = new BeeTableStringCellEditor(this, new BeeDataAnnotation());

	private BeeTableStringRenderer stringRender = new BeeTableStringRenderer();

	public BeeTable(BeeTableModel treeTableModel) {
		undo = new BeeTableUndo(this);// treeTableModel.getUndo();
		undo.setTable(this);
		treeTableModel.setUndo(undo);
		setDefaultEditor(BeeTableModel.class, treeEditor);
		setDefaultEditor(String.class, stringEditor);
		setDefaultEditor(Annotation.class, annoEditor);
		setShowGrid(true);

		this.setColumnModel(new BeeTableColumnModel());
		setIntercellSpacing(new Dimension(0, 0));
		this.setRowHeight(BeeUIUtils.getDefaultFontSize() * 2);
		this.setBorder(null);
		this.setCellSelectionEnabled(true);
		this.setColumnSelectionAllowed(true);
		this.setGridColor(lineColor);
		this.createDefaultColumnsFromModel();

		// Create the tree. It will be used as a renderer and editor.
		tree = new BeeTableTreeRenderer(treeTableModel, this);

		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new BeeTableModelAdapter(treeTableModel, tree));
		// Make the tree and table row heights the same.
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(BeeTableModel.class, tree);
		setDefaultRenderer(String.class, stringRender);
		setDefaultRenderer(Annotation.class, stringRender);
		this.setTableHeader(new BeeTableHeader(this));

		addMouseListener(new MouseAdapter() {

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupMenu(e);
				} else {
					int c = getSelectedColumn();
					int r = getSelectedRow();
					if (c == 0) {
						TreePath path = tree.getPathForRow(r);// tree.getPathForLocation(e.getX(), e.getY());
						if (path != null) {
							Object obj = path.getLastPathComponent();
							if (obj instanceof BeeTableNode) {
								BeeTableNode b = (BeeTableNode) obj;
								if (b.hasChild()) {
									if (b.isExpanded()) {
										tree.collapsePath(path);
										b.setExpanded(false);
									} else {
										tree.expandPath(path);
										b.setExpanded(true);
									}
								}

							}
						}

					}

				}
			}

		});

		getColumnModel().getColumn(0).setMaxWidth(80);
		// BeeActions.setTransfer(this);
		// installKeyboardActions();
	}

	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		if (columnClass == null) {
			return null;
		} else {
			Object renderer = defaultRenderersByColumnClass.get(columnClass);
			if (renderer != null) {
				return (TableCellRenderer) renderer;
			} else {
				Class c = columnClass.getSuperclass();
				if (c == null && columnClass != Object.class) {
					c = Object.class;
				}
				return getDefaultRenderer(c);
			}
		}
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableColumn tableColumn = getColumnModel().getColumn(column);
		TableCellRenderer renderer = tableColumn.getCellRenderer();
		if (renderer == null) {
			renderer = getDefaultRenderer(getColumnClass(column));
		}
		return renderer;
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Object value = getValueAt(row, column);

		boolean isSelected = false;
		boolean hasFocus = false;

		// Only indicate the selection and focused cell if not printing
		if (!isPaintingForPrint()) {
			isSelected = isCellSelected(row, column);

			boolean rowIsLead = (selectionModel.getLeadSelectionIndex() == row);
			boolean colIsLead = (columnModel.getSelectionModel().getLeadSelectionIndex() == column);

			hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
		}

		return renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
	}

	public void deleteColum(int index) {
		BeeTableModelAdapter model = (BeeTableModelAdapter) this.getModel();
		model.deleteColumn(index);
		BeeTableColumnModel cm = (BeeTableColumnModel) this.getColumnModel();
		cm.removeColumn(index, this);
		this.getTableHeader().setDraggedColumn(cm.getColumn(0));
	}

	public TableColumn addColumn(int index, String text) {
		int count = this.getColumnCount();
		for (int i = index; i < count; i++) {
			this.getColumnModel().getColumn(i).setModelIndex(i + 1);
		}

		TableColumn c = new TableColumn();

		c.setHeaderValue(text);
		c.setCellRenderer(this.getStringRender());
		c.setCellEditor(this.getStringEditor());
		c.setModelIndex(index);
		c.setHeaderRenderer(new BeeTableHeaderRenderer(this, index, (BeeTableHeader) this.getTableHeader()));

		BeeTableColumnModel m = (BeeTableColumnModel) this.getColumnModel();
		m.addColumn(index, c);

		BeeTableModelAdapter model = (BeeTableModelAdapter) this.getModel();
		model.addColumn(index, text, String.class);

		return c;
	}

	public BeeTableStringCellEditor getStringEditor() {
		return stringEditor;
	}

	public BeeTableStringCellEditor getAnnoEditor() {
		return annoEditor;
	}

	public BeeTableStringRenderer getStringRender() {
		return stringRender;
	}

	protected void showPopupMenu(MouseEvent e) {
		int[] c = this.getSelectedColumns();
		int[] r = this.getSelectedRows();
		if (c != null && c.length > 0 && r != null && r.length > 0) {
			boolean show = false;
			Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this);
			for (int i = 0; i < c.length; i++) {
				for (int j = 0; j < r.length; j++) {
					Rectangle rect = this.getCellRect(r[j], c[i], true);

					if (rect.contains(pt)) {
						show = true;
					}
				}
			}

			if (show) {
				BeeTablePopupMenu menu = new BeeTablePopupMenu(this);
				menu.addDefault();
				if (this.listener != null) {
					this.listener.beforeShowup(menu, c, r);
				}
				menu.show(this, pt.x, pt.y);
				e.consume();
			}
		}
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to paint
	 * the editor. The UI currently uses different techniques to paint the renderers
	 * and editors and overriding setBounds() below is not the right thing to do for
	 * an editor. Returning -1 for the editing row in this case, ensures the editor
	 * is never painted.
	 */
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == BeeTableModel.class) ? -1 : editingRow;
	}

	public class TreeTableCellEditor extends BeeTableAbstractCellEditor implements TableCellEditor {
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
			return tree;
		}
	}

	public static class UndoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 525680244348997846L;
		private BeeTable table;

		public UndoAction(BeeTable table) {
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BeeTableModelAdapter adapter = (BeeTableModelAdapter) table.getModel();
			IBeeTableModel model = adapter.getTreeTableModel();
			model.getUndo().undo();
		}

	}

	public static class RedoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6974620913402246042L;
		private BeeTable table;

		public RedoAction(BeeTable table) {
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BeeTableModelAdapter adapter = (BeeTableModelAdapter) table.getModel();
			IBeeTableModel model = adapter.getTreeTableModel();
			model.getUndo().redo();
		}

	}

	public void copy(String trstring) {
		BeeTableModelAdapter adapter = (BeeTableModelAdapter) this.getModel();
		BeeTableModel model = adapter.getTreeTableModel();

		BeeTableNode root = model.getRoot();

		try {

			int[] rows = this.getSelectedRows();
			int startRow = rows[0];
			int startCol = (this.getSelectedColumns())[0];

			int copyrows = 0;
			while (copyrows < rows.length) {
				copyrows = copyrows + copy(startRow + copyrows, startCol, trstring, root, this);
			}
			this.repaint();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
	}

	private int copy(int startRow, int startCol, String trstring, BeeTableNode root, BeeTable table) {
		int copyrows = 0;
		String rowstring;
		StringTokenizer st1 = new StringTokenizer(trstring, "\n");
		for (int i = 0; st1.hasMoreTokens(); i++) {
			rowstring = st1.nextToken();
			String[] ss = rowstring.split("\t");
			int j = 0;
			for (String s : ss) {
				if (startRow + i < table.getRowCount() && startCol + j < table.getColumnCount()) {
					table.setValueAt(s, startRow + i, startCol + j);
				} else if (startRow + i >= table.getRowCount()) {
					BeeTableNode n = new BeeTableNode(root, root.getColumnCount());
					root.getChildren().add(n);
					table.updateUI();
					table.setValueAt(s, startRow + i, startCol + j);

				}
				j++;
			}
			copyrows++;

		}
		return copyrows;
	}

}
