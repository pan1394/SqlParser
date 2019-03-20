package com.linkstec.bee.UI.spective.basic;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorManager;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.look.tab.BeeCloseable;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.table.BeeTable;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.look.table.BeeTableUndo.UndoListener;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.BeeDataTransferHandler;
import com.linkstec.bee.UI.thread.BeeListedThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicDataSheet extends JScrollPane implements BEditor, BeeCloseable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4006012124642156627L;
	protected BProject project;
	protected BEditorManager manager;
	protected BeeTable table;
	private BasicDataModel model;

	public BasicDataSheet(BasicDataModel model, BProject project) {
		this.model = model;
		table = new BeeTable(model);
		table.setBorder(null);
		this.project = project;
		this.getViewport().setView(table);
		this.getViewport().setBorder(null);
		this.setBorder(null);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		manager = new BEditorManager(this, model.getUndo());

		model.getUndo().setListener(new UndoListener() {

			@Override
			public void undoOcurred() {
				setModified(true);
			}

		});

		model.addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
				setModified(true);

			}

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				setModified(true);

			}

		});

		new BeeDataTransferHandler(table);
	}

	public BasicDataModel getModel() {
		return model;
	}

	public void deleteData(int index) {
		table.deleteColum(index);
	}

	public void deleteAllModelData() {
		TableColumnModel model = this.table.getColumnModel();
		int count = model.getColumnCount();

		for (int i = count - 1; i >= 0; i--) {

			TableColumn c = model.getColumn(i);
			Object value = c.getHeaderValue();
			// if (value instanceof BasicDataModel) {
			// TODO
			if (i >= 7) {
				table.deleteColum(i);
			}
			// }
		}

	}

	public TableColumn addData(BClass data) {
		TableColumnModel model = this.table.getColumnModel();
		int count = model.getColumnCount();
		boolean added = false;
		for (int i = 0; i < count; i++) {
			TableColumn c = model.getColumn(i);
			Object value = c.getHeaderValue();
			if (value instanceof BasicDataModel) {

				BasicDataModel m = (BasicDataModel) value;

				if (m.getLogicName().equals(data.getLogicName())) {
					added = true;
				}
			}
		}
		if (!added) {
			int index = table.getColumnCount();

			TableColumn c = this.table.addColumn(index, data.getName());
			c.setHeaderValue(data);

			BeeTableNode root = this.model.getRoot();
			int size = root.getChildCount();
			List<BAssignment> vars = data.getVariables();
			for (BAssignment assign : vars) {
				boolean exist = false;
				String logicName = assign.getLeft().getLogicName();
				for (int i = 0; i < size; i++) {
					BeeTableNode node = (BeeTableNode) root.getChild(i);
					BParameter left = node.getLeft();

					if (left != null) {
						if (left.getLogicName().equals(logicName)) {
							table.setValueAt("〇", i, index);
							exist = true;
							break;
						}
					}
				}
				if (!exist) {
					if (!logicName.equals("serialVersionUID")) {
						int row = this.model.getVariables().size();
						this.model.addVar(row, (BAssignment) ObjectFileUtils.deepCopy(assign));
						table.updateUI();
						table.setValueAt("〇", row, index);

					}
				}
			}
			this.setModified(false);
			return c;
		}

		return null;
	}

	@Override
	public String getDisplayPath() {
		BasicBook book = this.findBook();
		if (book != null) {
			return book.getDisplayPath();
		}
		return null;
	}

	public BasicBook findBook() {
		Container parent = this.getParent();
		while (parent != null && !(parent instanceof BasicBook)) {
			parent = parent.getParent();
		}
		return (BasicBook) parent;
	}

	@Override
	public BEditorFileExplorer getFileExplore() {
		return Application.getInstance().getBasicSpective().getFileExplore();
	}

	@Override
	public BEditorOutlookExplorer getOutlookExplore() {
		return Application.getInstance().getBasicSpective().getOutline();
	}

	@Override
	public BProject getProject() {
		return this.project;
	}

	@Override
	public File getFile() {
		BasicBook book = this.findBook();
		if (book != null) {
			return book.getFile();
		}
		return null;
	}

	@Override
	public void refresh() {
		this.updateUI();

	}

	@Override
	public File save() {
		BasicBook book = this.findBook();
		if (book != null) {
			return book.save();
		}
		return null;
	}

	@Override
	public BEditorManager getManager() {
		return this.manager;
	}

	@Override
	public JComponent getContents() {
		return this.table;
	}

	@Override
	public String getLogicName() {
		return this.model.getLogicName();
	}

	@Override
	public ImageIcon getImageIcon() {
		return this.model.getIcon();
	}

	@Override
	public void saveAs(ActionEvent e) {
		BasicBook book = this.findBook();
		if (book != null) {
			book.saveAs(e);
		}

	}

	@Override
	public void deleteSelect(ActionEvent e) {

	}

	@Override
	public void selectAll(ActionEvent e) {
		table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
		table.setRowSelectionInterval(0, table.getRowCount() - 1);

	}

	@Override
	public void setModified(boolean modified) {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			button.setModified(modified);
			if (modified) {
				BeeTabCloseButton b = BeeActions.findPaneButton(this.getParent().getParent(),
						(JComponent) this.getParent());
				if (b != null) {
					b.setModified(true);
				}
			}
		}
		new BeeListedThread(new Runnable() {

			@Override
			public void run() {
				getOutlookExplore().update();
			}
		});

	}

	@Override
	public boolean isModified() {
		BeeTabCloseButton button = BeeActions.findPaneButton((JComponent) this.getParent(), this);
		if (button != null) {
			return button.isModified();
		} else {
			return false;
		}
	}

	@Override
	public void onSelected() {
		Application.getInstance().setCurrentEditor(this);

	}

	@Override
	public void updateView() {
		this.table.updateUI();

	}

	protected PageFormat pageFormat = new PageFormat();

	@Override
	public PageFormat getPageFormat() {

		return pageFormat;
	}

	@Override
	public void setPageFormat(PageFormat format) {
		this.pageFormat = format;

	}

	@Override
	public int print(Graphics g, PageFormat f, int pageIndex) throws PrinterException {
		return this.manager.print(g, f, pageIndex, this.table);
	}

	@Override
	public void setFile(File file) {

	}

	@Override
	public BEditorModel getEditorModel() {
		return this.model;
	}

	@Override
	public void makeTabPopupItems(BManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactived() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoom(double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProject(BProject project) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeErrorLine(Object cell) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tabCloseable() {
		return true;
	}

}
