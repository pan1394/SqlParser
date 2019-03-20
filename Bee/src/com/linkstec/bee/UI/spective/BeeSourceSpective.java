package com.linkstec.bee.UI.spective;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.swing.JSplitPane;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.task.problem.BeeSourceError;
import com.linkstec.bee.UI.spective.code.BeeSourceExplorer;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.code.BeeSourceWorkspace;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.thread.BeeListedThread;
import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeSourceSpective extends BeeSpective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3610100557265250179L;

	public BeeSourceSpective(BeeConfig beeConfig) {
		super(beeConfig);

		JSplitPane tasksplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.workSpace, tasks);
		tasksplit.setBorder(null);
		tasksplit.setContinuousLayout(true);
		tasksplit.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tasksplit, explorer);
		split.setBorder(null);
		this.add(split, BorderLayout.CENTER);

		split.setContinuousLayout(true);
		split.setDividerLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - BeeUIUtils.getDefaultFontSize() * 20);
		split.setDividerSize(BeeUIUtils.getDefaultFontSize() / 4);

	}

	public void fileDeleted(File file, BProject project) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ProjectClassLoader.getClassLoader(project).updateClassUrls();
				BeeCompiler.comileAllWithThread(project, null);
			}

		}).start();

	}

	public void fileAdded(List<File> files, BProject project) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ProjectClassLoader.getClassLoader(project).updateClassUrls();
				for (File f : files) {
					BeeCompiler.compile(project, f.getAbsolutePath(), null);
				}
			}

		}).start();

	}

	@Override
	public void openFile(File file, BProject project) {
		BeeActions.addDetailPane(file, project);
	}

	public void showError(Diagnostic<?> diagnostic) {
		new BeeListedThread(new Runnable() {

			@Override
			public void run() {
				doErrorShow(diagnostic);
			}

		});
	}

	private void doErrorShow(Diagnostic<?> diagnostic) {
		BProject project = null;
		List<BProject> projects = config.getProjects();
		for (BProject p : projects) {
			Object obj = diagnostic.getSource();
			JavaFileObject fo = (JavaFileObject) obj;
			String name = fo.getName();
			if (name.startsWith(p.getSourcePath())) {
				project = p;
				break;
			}
		}

		if (project != null) {

			BeeSourceError error = new BeeSourceError();
			error.setColumn(diagnostic.getColumnNumber());
			error.setLine(diagnostic.getLineNumber());

			error.setContents(diagnostic.getMessage(Locale.JAPAN));
			error.setStart(diagnostic.getStartPosition());
			error.setEnd(diagnostic.getEndPosition());
			error.setProject(project);

			Object obj = diagnostic.getSource();
			JavaFileObject fo = (JavaFileObject) obj;
			String name = fo.getName();
			error.setFilePath(name);

			this.getTask().getProblems().addError(error);
		}
	}

	@Override
	public BEditorExplorer createExplorer() {
		return new BeeSourceExplorer(config);
	}

	@Override
	public BWorkspace createWorkspace() {
		return new BeeSourceWorkspace(this);
	}

	@Override
	public BEditor createEditor(BProject project) {
		return new BeeSourceSheet(project);
	}

}
