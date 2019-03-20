package com.linkstec.bee.UI;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BWorkspace extends BeeTabbedPane implements ChangeListener, BWorkSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3584072110114558316L;
	private BSpective spective;
	private BEditor currentEdtitor;

	public BWorkspace(BSpective spective) {
		this.spective = spective;
		this.addChangeListener(this);
	}

	public BSpective getSpective() {
		return spective;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Component comp = this.getSelectedComponent();

		if (comp != null && comp instanceof BEditor) {
			BEditor editor = (BEditor) comp;
			this.spective.getOutline().setEditor(editor);

			this.editorChanged(editor);
			currentEdtitor = editor;

		} else if (comp == null) {
			this.editorChanged(null);
			spective.getOutline().setEditor(null);
			Application.getInstance().setCurrentEditor(null);
		}

	}

	public BEditor getCurrentEditor() {
		return this.currentEdtitor;
	}

	protected void editorChanged(BEditor editor) {

	}

	public BEditor getSelectedEditor() {
		BEditor pane = (BEditor) this.getSelectedComponent();
		return pane;

	}

	public List<BEditor> getAllOpenningEditors() {
		List<BEditor> sheets = new ArrayList<BEditor>();
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			BEditor sheet = (BEditor) this.getComponentAt(i);
			sheets.add(sheet);
		}
		return sheets;
	}

	public void setModified(boolean modified, BEditor editor) {
		editor.setModified(modified);
		int index = this.indexOfComponent((Component) editor);
		BeeTabCloseButton tab = (BeeTabCloseButton) this.getTabComponentAt(index);
		tab.setModified(modified);

	}

	public boolean isModefied(int index) {
		BeeTabCloseButton tab = (BeeTabCloseButton) this.getTabComponentAt(index);
		return tab.isModified();
	}

	public int getIndexOfEditor(File f) {
		int size = this.getTabCount();
		int index = -1;

		for (int i = 0; i < size; i++) {
			Component comp = this.getComponentAt(i);

			if (comp instanceof BEditor) {
				BEditor sheet = (BEditor) comp;
				File file = sheet.getFile();
				if (file == null) {
					continue;
				}

				if (file.getAbsolutePath().equals(f.getAbsolutePath())) {
					return i;
				}
			}
		}
		return index;
	}

	public void setCurrentComponent(int index) {
		this.setSelectedIndex(index);
	}

	public boolean contains(File f) {
		int size = this.getTabCount();

		for (int i = 0; i < size; i++) {
			Component comp = this.getComponentAt(i);

			if (comp instanceof BEditor) {
				BEditor sheet = (BEditor) comp;

				File file = sheet.getFile();
				if (file == null) {
					return false;
				}

				if (file.getAbsolutePath().equals(f.getAbsolutePath())) {
					return true;
				}
			}
		}
		return false;
	}

	public int indexOfEditor(File id) {
		int size = this.getTabCount();
		int index = -1;

		for (int i = 0; i < size; i++) {
			Component comp = this.getComponentAt(i);

			if (comp instanceof BEditor) {
				BEditor sheet = (BEditor) comp;
				if (sheet.getFile() != null) {
					if (sheet.getFile().getAbsolutePath().equals(id.getAbsolutePath())) {
						return i;
					}
				}
			}
		}
		return index;
	}

	public BEditor addEditor(BEditor editor) {
		if (editor.getFile() != null) {
			int index = this.getIndexOfEditor(editor.getFile());
			if (index == -1) {
				this.addTab(editor.getLogicName(), editor.getImageIcon(), (Component) editor);
				this.setSelectedComponent((Component) editor);

			} else {
				this.setSelectedIndex(index);
				editor = (BEditor) this.getSelectedComponent();
			}
		} else {
			this.addTab(editor.getLogicName(), editor.getImageIcon(), (Component) editor);
			this.setSelectedComponent((Component) editor);
		}
		this.editorAdded(editor);
		return editor;
	}

	protected void editorAdded(BEditor editor) {

	}
}
