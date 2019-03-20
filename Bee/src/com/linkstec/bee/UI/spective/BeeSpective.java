package com.linkstec.bee.UI.spective;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BEditorTask;
import com.linkstec.bee.UI.BSpective;
import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public abstract class BeeSpective extends JPanel implements BSpective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3541678646657587998L;

	protected BEditorTask tasks;
	protected BWorkspace workSpace;
	protected Configuration config;
	protected BeeConfig beeConfig;
	protected BEditorExplorer explorer;

	public BeeSpective(BeeConfig config) {
		this.setBorder(null);
		this.setLayout(new BorderLayout());
		this.beeConfig = config;
		this.config = config.getConfig();
		workSpace = this.createWorkspace();
		tasks = new BEditorTask(config.getConfig());
		explorer = this.createExplorer();
	}

	protected JScrollPane createScrollPane(JComponent comp) {
		return new JScrollPane(comp) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2855161683767655308L;

			@Override
			protected void paintBorder(Graphics g) {

			}

		};
	}

	@Override
	public BWorkspace getWorkspace() {
		return this.workSpace;
	}

	@Override
	public BEditorTask getTask() {
		return this.tasks;
	}

	public BEditorExplorer getExplore() {
		return this.explorer;
	}

	public void refresh() {
		this.explorer.getFileExplorer().doRefresh();
	}

	public abstract BEditorExplorer createExplorer();

	public abstract BWorkspace createWorkspace();

	@Override
	public BEditorFileExplorer getFileExplore() {
		return this.explorer.getFileExplorer();
	}

	@Override
	public BEditorOutlookExplorer getOutline() {
		return this.explorer.getOutline();
	}

	public void setOutlineSelected() {
		explorer.setOutLineSelected();

	}

	public void setSelected(File file, BProject project) {

		int count = workSpace.getTabCount();
		for (int i = count - 1; i >= 0; i--) {
			BeeSourceSheet source = (BeeSourceSheet) this.workSpace.getComponentAt(i);

			if (source.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
				workSpace.setSelectedIndex(i);
				return;
			}
		}

		count = workSpace.getTabCount();
		BEditor editor = this.createEditor(project);

		workSpace.insertTab(file.getName(), editor.getImageIcon(), (Component) editor, file.getAbsolutePath(), count);
		workSpace.setSelectedIndex(workSpace.getTabCount() - 1);
		editor.setFile(file);

	}

	public abstract BEditor createEditor(BProject project);

	public void addEditor(File file, BProject project) {
		BEditor editor = this.createEditor(project);
		editor.setFile(file);
		int count = workSpace.getTabCount();

		for (int i = count - 1; i >= 0; i--) {
			BEditor source = (BEditor) this.workSpace.getComponentAt(i);
			if (source.getFile() != null) {
				if (source.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
					workSpace.remove(i);
				}
			}
		}

		count = workSpace.getTabCount();

		workSpace.insertTab(file.getName(), editor.getImageIcon(), (Component) editor, file.getAbsolutePath(), count);

		workSpace.setSelectedIndex(workSpace.getTabCount() - 1);
		Application.getInstance().getJavaSourceSpective().getOutline().setEditor(editor);
		this.editorAdded(editor);
	}

	protected void editorAdded(BEditor editor) {

	}
}
