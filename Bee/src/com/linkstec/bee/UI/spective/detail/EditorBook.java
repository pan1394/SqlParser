package com.linkstec.bee.UI.spective.detail;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BFileExplorer;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BOutLook;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class EditorBook extends BeeTabbedPane implements IBeeTitleUI, BEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4277194857635073042L;
	public static final String ADD_GRAPH = "ADD_GRAPH";
	public static final String ADD_DATA = "ADD_DATA";
	private File file;
	private BookModel model;
	private BProject project;
	private BWorkspace space;
	private BEditor currentEditor;

	public EditorBook(BookModel model, BProject project, BWorkSpace space) {
		this.model = model;
		this.project = project;
		this.space = (BWorkspace) space;
		this.setTabPlacement(JTabbedPane.BOTTOM);

		this.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				currentEditor = (BEditor) getSelectedComponent();
				Application.getInstance().getEditor().getToolbar().refreshItems();
				if (currentEditor != null) {
					currentEditor.onSelected();
				}
			}

		});

	}

	public String getName() {
		return this.model.getName();
	}

	public BProject getProject() {
		return project;
	}

	public String getLogicName() {
		return this.model.getLogicName();
	}

	public BookModel getBookModel() {
		return this.model;
	}

	@Override
	protected void beforeMenuShow() {

		BeePopupMenuItem addGraph = new BeePopupMenuItem();
		addGraph.setText("ロジック新規");
		addGraph.setValue(ADD_GRAPH);
		addGraph.setIcon(BeeConstants.SHEET_LOGIC_ICON);
		this.actionMenu.addItem(addGraph);

		BeePopupMenuItem addData = new BeePopupMenuItem();
		addData.setText("データ新規");
		addData.setValue(ADD_DATA);
		addData.setIcon(BeeConstants.SHEET_DATA_ICON);
		this.actionMenu.addItem(addData);

		super.beforeMenuShow();
	}

	public boolean isModified(int index) {
		BeeTabCloseButton tab = (BeeTabCloseButton) this.getTabComponentAt(index);
		return tab.isModified();
	}

	public boolean isModified(Component comp) {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			if (this.getComponentAt(i).equals(comp)) {
				return ((BeeTabCloseButton) this.getTabComponentAt(i)).isModified();
			}
		}
		return false;
	}

	public boolean contains(Component comp) {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			if (this.getComponentAt(i).equals(comp)) {
				return true;
			}
		}
		return false;
	}

	public boolean setModified(boolean modified, Component comp) {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			if (comp == null) {
				((BeeTabCloseButton) this.getTabComponentAt(i)).setModified(modified);
			} else {
				if (this.getComponentAt(i).equals(comp)) {
					((BeeTabCloseButton) this.getTabComponentAt(i)).setModified(modified);
					return true;
				}
			}
		}
		return comp == null;

	}

	@Override
	public void menuSelected(Object menu) {
		super.menuSelected(menu);
		new Thread(new Runnable() {
			public void run() {
				BeePopupMenuItem item = (BeePopupMenuItem) menu;
				if (item.getValue().equals(ADD_GRAPH)) {
					BeeActions.addNewSheet(project);
				} else if (item.getValue().equals(ADD_DATA)) {
					BeeActions.addNewDataSheet(project);
				}
			}
		}).start();
	}

	public void hidePopup() {
		actionMenu.setVisible(false);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isModified() {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			if (((BeeTabCloseButton) this.getTabComponentAt(i)).isModified()) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return this.model.toString();
	}

	private List<TitleChangeListener> titleChangeListeners = new ArrayList<TitleChangeListener>();

	@Override
	public String getTitleLabel() {
		return this.model.getName();
	}

	public void setTitleLabel(String title) {
		this.model.setName(title);
		for (TitleChangeListener listener : titleChangeListeners) {
			listener.change(this);
		}
	}

	public void addTitleChangeListener(TitleChangeListener listener) {
		this.titleChangeListeners.add(listener);
	}

	public void setTitleWithOutListenerAction(String title) {
		this.model.setName(title);
	}

	public ImageIcon getImageIcon() {
		return BeeConstants.BOOK_ICON;
	}

	@Override
	public void fireErrorStatus() {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			BeeTabCloseButton b = (BeeTabCloseButton) this.getTabComponentAt(i);

			if (b == null) {
				continue;
			}
			if (b.isError()) {
				for (TitleChangeListener listener : titleChangeListeners) {
					listener.setError(true);
				}
				return;
			} else if (b.isAlert()) {
				for (TitleChangeListener listener : titleChangeListeners) {
					listener.setAlert(true);
				}
				return;
			}
		}
		for (TitleChangeListener listener : titleChangeListeners) {
			listener.setError(false);
			listener.setAlert(false);
		}
	}

	public List<BEditor> getList() {
		List<BEditor> list = new ArrayList<BEditor>();
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			list.add((BEditor) this.getComponentAt(i));
		}
		return list;
	}

	@Override
	public String getDisplayPath() {
		if (this.file != null) {
			String root = this.project.getDesignPath();
			String path = file.getAbsolutePath().substring(root.length() + 1);
			return project.getName() + "/" + path.replace(File.separatorChar, '/');
		} else {
			return project.getName() + "/" + this.getLogicName();
		}
	}

	@Override
	public BFileExplorer getFileExplore() {
		return this.getInnerEditor().getFileExplore();
	}

	@Override
	public BOutLook getOutlookExplore() {
		return this.getInnerEditor().getOutlookExplore();
	}

	@Override
	public void refresh() {

	}

	@Override
	public File save() {
		if (this.file != null) {
			this.file.delete();
		}
		model = BeeActions.getBookModel(this);
		file = BeeActions.saveModel(model.getLogicName() + ".bee", model, project);

		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			((BeeTabCloseButton) this.getTabComponentAt(i)).setModified(false);
		}
		((EditorWorkSpace) this.getParent()).setModified(false, this);

		return file;
	}

	@Override
	public BManager getManager() {
		return this.getInnerEditor().getManager();
	}

	@Override
	public JComponent getContents() {
		return (JComponent) this.getInnerEditor();
	}

	public BEditor getInnerEditor() {
		return (BEditor) this.getSelectedComponent();
	}

	@Override
	public void makeTabPopupItems(BManager manager) {
		this.getInnerEditor().makeTabPopupItems(manager);
	}

	@Override
	public List<TitleChangeListener> getTitleChangeListeners() {
		return this.titleChangeListeners;
	}

	@Override
	public void saveAs(ActionEvent e) {
		BeeActions.SaveAs(this);
	}

	@Override
	public void deleteSelect(ActionEvent e) {

	}

	@Override
	public void selectAll(ActionEvent e) {

	}

	public void addEditor(BEditor sheet) {
		int count = getTabCount();
		int index = count;
		for (int i = count - 1; i >= 0; i--) {
			Component comp = this.getComponentAt(i);
			if (comp instanceof BEditor) {
				BEditor b = (BEditor) comp;

				if (b.getFile() != null) {
					if (sheet.getFile() != null) {
						if (b.getFile().getAbsolutePath().equals(sheet.getFile().getAbsolutePath())) {
							this.remove(i);
							index = i;
						}
					}
				}
			}
		}
		this.insertTab(sheet.getLogicName(), sheet.getImageIcon(), (Component) sheet, sheet.getLogicName(), index);
		this.setSelectedIndex(index);
	}

	@Override
	public void setModified(boolean modified) {
		int count = this.space.getTabCount();
		for (int i = count - 1; i >= 0; i--) {
			Component comp = space.getComponentAt(i);
			if (comp.equals(this)) {
				((BeeTabCloseButton) this.space.getTabComponentAt(i)).setModified(modified);
				break;
			}
		}

	}

	@Override
	public void onSelected() {
		currentEditor = (BEditor) getSelectedComponent();
		Application.getInstance().getEditor().getToolbar().refreshItems();
		if (currentEditor != null) {
			currentEditor.onSelected();
		}
	}

	public BWorkspace getSpace() {
		return space;
	}

	public BEditor getCurrentEditor() {
		return currentEditor;
	}

	@Override
	public void updateView() {
		this.updateUI();

	}

	protected PageFormat pageFormat = new PageFormat();

	@Override
	public PageFormat getPageFormat() {
		return pageFormat;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			BEditor c = (BEditor) this.getComponentAt(i);
			c.print(graphics, pageFormat, pageIndex);
		}
		return 0;
	}

	@Override
	public void setPageFormat(PageFormat format) {
		this.pageFormat = format;

	}

	@Override
	public BEditorModel getEditorModel() {
		return this.model;
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

	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProject(BProject project) {

	}

	@Override
	public void removeErrorLine(Object cell) {

	}
}