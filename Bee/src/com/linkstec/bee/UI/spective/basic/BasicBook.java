package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BComponentNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.detail.IBeeTitleUI;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicBook extends BeeTabbedPane implements IBeeTitleUI, BEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3808777193117470274L;
	private BasicBookModel model;
	private BProject project;
	private BWorkSpace space;
	private File file;
	private BEditor currentEditor;
	private SubSystem sub;

	private List<TitleChangeListener> titleChangeListeners = new ArrayList<TitleChangeListener>();

	public BasicBook(BasicBookModel model, BProject project, BWorkSpace space, SubSystem sub) {
		this.model = model;
		this.project = project;
		this.space = space;
		this.sub = sub;
		this.setTabPlacement(JTabbedPane.BOTTOM);

		file = new File(project.getRootPath() + File.separator + "basic" + File.separator + sub.getLogicName()
				+ File.separator + "logic" + File.separator + model.getLogicName());

		this.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				BEditor b = (BEditor) getSelectedComponent();
				if (!b.equals(currentEditor)) {
					currentEditor = b;
					if (Application.INSTANCE_COMPLETE) {
						Application.getInstance().getEditor().getToolbar().refreshItems();
						if (currentEditor != null) {
							currentEditor.onSelected();
						}
					}
				}
			}

		});
	}

	public static void changeEditorName(String name, BPath path) {
		BEditor editor = Application.getInstance().getCurrentEditor();
		BasicBook book = null;
		if (editor instanceof BasicBook) {
			book = (BasicBook) editor;
		} else if (editor instanceof BasicLogicSheet) {
			BasicLogicSheet logic = (BasicLogicSheet) editor;
			book = logic.findBook();
		}
		if (book != null) {
			int count = book.getTabCount();

			for (int i = 0; i < count; i++) {
				Component comp = book.getComponentAt(i);
				if (comp instanceof BEditor) {
					BEditor b = (BEditor) comp;

					BEditorModel model = b.getEditorModel();
					if (model instanceof BPatternModel) {
						BPatternModel pattern = (BPatternModel) model;
						BPath p = pattern.getActionPath();
						if (path.equals(p)) {
							BeeTabCloseButton button = (BeeTabCloseButton) book.getTabComponentAt(i);
							button.setTitle(name);
							return;
						}
					}
				}
			}

		}
	}

	public SubSystem getSub() {
		return sub;
	}

	@Override
	public String getDisplayPath() {
		if (this.file != null) {
			String root = this.project.getRootPath() + File.separator + "basic";
			String path = file.getAbsolutePath().substring(root.length() + 1);
			return project.getName() + "/" + path.replace(File.separatorChar, '/');
		} else {
			return project.getName() + "/" + sub.getLogicName() + "/" + this.getLogicName();
		}
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
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public File save() {
		if (this.file != null) {
			this.file.delete();
		}
		model = this.getALLModel();

		File dir = this.file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		this.file = new File(dir.getAbsolutePath() + File.separator + model.getLogicName() + ".bl");

		Application.getInstance().getEditor().getStatusBar().startProgress("saving " + file.getName() + "...");
		try {
			// forTest(dir);
			ObjectFileUtils.writeObject(file, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Application.getInstance().getEditor().getStatusBar().endProgress();

		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			((BeeTabCloseButton) this.getTabComponentAt(i)).setModified(false);
		}
		((BasicWorkSpace) this.getParent()).setModified(false, this);

		return file;

	}

	private void forTest(File dir) throws IOException {
		////////////////////// test
		File targetDir = new File(dir.getAbsolutePath() + File.separator + model.getLogicName());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		} else {
			File[] files = targetDir.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		List<BEditorModel> editors = model.getEditors();
		List<BEditorModel> saved = new ArrayList<BEditorModel>();
		int index = 1;
		for (BEditorModel editor : editors) {
			if (editor instanceof BPatternModel) {
				File sub = new File(targetDir.getAbsolutePath() + File.separator + editor.getName() + ".bl" + index);
				Application.getInstance().getEditor().getStatusBar().startProgress("saving " + sub.getName() + "...");

				index++;
				ObjectFileUtils.writeObject(sub, editor);
				saved.add(editor);
			}
		}

		for (BEditorModel editor : saved) {
			editors.remove(editor);
		}
		//////////////////////// test
	}

	public List<BEditorModel> getALLModels() {
		int count = this.getTabCount();
		model.getEditors().clear();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponentAt(i);
			if (comp instanceof BEditor) {
				BEditor editor = (BEditor) comp;
				model.getEditors().add(editor.getEditorModel());
			}
		}
		return model.getEditors();
	}

	public BasicBookModel getALLModel() {
		int count = this.getTabCount();
		model.getEditors().clear();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponentAt(i);
			if (comp instanceof BEditor) {
				BEditor editor = (BEditor) comp;
				model.getEditors().add(editor.getEditorModel());
			}
		}
		return model;
	}

	public Hashtable<String, BasicComponentModel> getAllComponents() {
		Hashtable<String, BasicComponentModel> comps = new Hashtable<String, BasicComponentModel>();
		BasicBookModel model = this.getALLModel();
		List<BEditorModel> models = model.getEditors();
		for (BEditorModel bm : models) {
			if (bm instanceof BasicModel) {
				BasicModel basic = (BasicModel) bm;
				List<BNode> nodes = basic.getBNodes();
				for (BNode node : nodes) {
					if (node instanceof BComponentNode) {
						BComponentNode comp = (BComponentNode) node;
						BasicComponentModel bbb = comp.getModel();
						comps.put(bbb.getLogicName(), bbb);
					}
				}
			}
		}
		return comps;

	}

	public BasicBookModel getBookModel() {
		return model;
	}

	@Override
	public BManager getManager() {
		return this.getInnerEditor().getManager();
	}

	public BEditor getInnerEditor() {
		return (BEditor) this.getSelectedComponent();
	}

	@Override
	public void makeTabPopupItems(BManager manager) {
	}

	@Override
	public JComponent getContents() {
		return this;
	}

	@Override
	public String getLogicName() {
		return this.model.getLogicName();
	}

	@Override
	public String getTitleLabel() {
		return this.model.getName();
	}

	public void setTitleLabel(String title) {
		this.model.setName(title);
		File dir = this.file.getParentFile();
		this.file = new File(dir.getAbsolutePath() + File.separator + model.getLogicName() + ".bl");
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
		return BeeConstants.BASIC_DESIGN_ICON;
	}

	@Override
	public void saveAs(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSelect(ActionEvent e) {

	}

	@Override
	public void selectAll(ActionEvent e) {

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
	public boolean isModified() {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			if (((BeeTabCloseButton) this.getTabComponentAt(i)).isModified()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onSelected() {
		currentEditor = (BEditor) getSelectedComponent();
		if (currentEditor != null) {
			currentEditor.onSelected();
		}
		Application.getInstance().getEditor().getToolbar().refreshItems();
	}

	@Override
	public void updateView() {

	}

	@Override
	public List<TitleChangeListener> getTitleChangeListeners() {

		return null;
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

	public void validateModels() {
		BasicBookModel model = this.getBookModel();
		model.validateLayers(this);
	}
}
