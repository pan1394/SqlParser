package com.linkstec.bee.UI.spective.code;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import com.linkstec.bee.UI.config.BeeProject;
import com.linkstec.bee.UI.editor.task.problem.BeeSourceError;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.BeeSourceSpective;
import com.linkstec.bee.core.Application;

public class BeeSourceCompileListener implements DiagnosticListener<Object> {
	private List<BeeSourceError> errors = new ArrayList<BeeSourceError>();
	private File temp;
	private File realFile;
	private BeeProject project;

	public BeeSourceCompileListener(BeeProject project) {
		this.project = project;
	}

	@Override
	public void report(Diagnostic<?> diagnostic) {

		Object obj = diagnostic.getSource();

		JavaFileObject fo = (JavaFileObject) obj;
		String name = fo.getName();

		BeeSourceSpective reader = Application.getInstance().getJavaSourceSpective();
		BeeTabbedPane pane = reader.getWorkspace();
		BeeSourceError error = this.makeError(diagnostic);
		if (error.getLine() > -1) {
			int count = pane.getTabCount();
			for (int i = 0; i < count; i++) {
				BeeSourceSheet sheet = (BeeSourceSheet) pane.getComponentAt(i);
				String fileName = sheet.getFile().getAbsolutePath();
				if (fileName.equals(name)) {
					sheet.addError(error);
				} else {

					String path = name.substring((System.getProperty("user.home") + File.separator + "beecache").length());
					String apath = fileName.substring(project.getSourcePath().length());
					if (apath.equals(path)) {
						sheet.addError(error);
					}

				}

			}
		}

	}

	private BeeSourceError makeError(Diagnostic<?> diagnostic) {
		BeeSourceError error = new BeeSourceError();
		error.setColumn(diagnostic.getColumnNumber());
		error.setLine(diagnostic.getLineNumber());

		// diagnostic.getPosition();
		error.setContents(diagnostic.getMessage(Locale.JAPAN));
		error.setStart(diagnostic.getPosition());
		error.setEnd(diagnostic.getEndPosition());
		error.setProject(project);

		Object obj = diagnostic.getSource();
		JavaFileObject fo = (JavaFileObject) obj;
		String name = fo.getName();
		error.setFilePath(name);
		if (temp != null) {
			if (temp.getAbsolutePath().equals(name)) {
				error.setFilePath(this.realFile.getAbsolutePath());
			}
		}
		if (error.getLine() > -1) {
			errors.add(error);
		}
		return error;
	}

	public void clearError() {
		errors.clear();
	}

	public List<BeeSourceError> getErrors() {
		return errors;
	}
}