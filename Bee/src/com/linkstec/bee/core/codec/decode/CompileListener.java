package com.linkstec.bee.core.codec.decode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import com.linkstec.bee.UI.editor.task.problem.BeeSourceError;
import com.linkstec.bee.core.fw.editor.BProject;

public class CompileListener implements DiagnosticListener<Object> {

	private boolean success = true;
	// private String error;
	private BProject project;
	private int sourceNumber = 0;

	private List<BeeSourceError> errors = new ArrayList<BeeSourceError>();

	public int getSourceNumber() {
		return sourceNumber;
	}

	public void setSourceNumber(int sourceNumber) {
		this.sourceNumber = sourceNumber;
	}

	public CompileListener(BProject project) {
		this.project = project;
	}

	@Override
	public void report(Diagnostic<?> diagnostic) {
		// error = diagnostic.getMessage(Locale.JAPAN);

		Kind kind = diagnostic.getKind();
		if (Kind.ERROR.equals(kind)) {
			success = false;

			makeInfo(diagnostic);
		}
	}

	public boolean successed() {
		return this.success;
	}

	private void makeInfo(Diagnostic<?> diagnostic) {

		String code = diagnostic.getCode();

		BeeSourceError error = new BeeSourceError();

		error.setColumn(diagnostic.getColumnNumber());
		error.setLine(diagnostic.getLineNumber());

		error.setContents(diagnostic.getMessage(Locale.JAPAN));
		error.setStart(diagnostic.getStartPosition());
		error.setEnd(diagnostic.getEndPosition() + 1);
		error.setProject(project);

		Object obj = diagnostic.getSource();
		JavaFileObject fo = (JavaFileObject) obj;

		String name = fo.getName();
		if (!name.startsWith(project.getSourcePath())) {
			name = name.replace('/', File.separatorChar);
			name = project.getSourcePath() + name;
		}
		error.setFilePath(name);

		this.errors.add(error);
	}

	public List<BeeSourceError> getErrors() {
		return this.errors;
	}

	public void showError() {
		JOptionPane.showMessageDialog(null, "ソースのコンパイルエラーが発生しました。\n");
	}
}
