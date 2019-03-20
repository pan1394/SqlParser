package com.linkstec.bee.UI.editor.task.problem;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeSourceError implements BeeEditorError {
	private long start;
	private long end;
	private long column;
	private String contents;
	private long line;
	private Rectangle bound;
	private String filePath;
	private BProject project;

	public long getLine() {
		return line;
	}

	public void setLine(long line) {
		this.line = line;
	}

	public Rectangle getBound() {
		return bound;
	}

	public void setBound(Rectangle bound) {
		this.bound = bound;
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getColumn() {
		return column;
	}

	public void setColumn(long column) {
		this.column = column;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public boolean equals(Object obj) {
		if (obj instanceof BeeSourceError) {
			BeeSourceError error = (BeeSourceError) obj;
			if (!error.contents.equals(this.contents)) {
				return false;
			}
			if (!error.getFilePath().equals(this.getFilePath())) {
				return false;
			}
			if (error.start != this.start) {
				return false;
			}
			if (error.end != this.end) {
				return false;
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public String getTargetPath() {
		return this.getLine() + "行目";
	}

	@Override
	public String getDisplayFilePath() {
		String path = this.getProject().getSourcePath();
		return this.getProject().getName() + this.getFilePath().substring(path.length()).replace(File.separator, "/");
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ERROR_ICON;
	}

	@Override
	public String getType() {
		return TYPE_CODE;
	}

	@Override
	public BeeTree getLinkedFileTree() {
		return Application.getInstance().getJavaSourceSpective().getFileExplore();
	}

	@Override
	public void showErrorInEditor() {
		List<BEditor> editors = Application.getInstance().getJavaSourceSpective().getWorkspace().getAllOpenningEditors();
		BeeSourceSheet sheet = null;
		for (BEditor editor : editors) {
			File f = editor.getFile();
			if (f != null) {
				if (f.getAbsolutePath().equals(this.getFilePath())) {
					Application.getInstance().getJavaSourceSpective().getWorkspace().setSelectedComponent((Component) editor);
					sheet = (BeeSourceSheet) editor;
				}
			}
		}
		if (sheet == null) {
			Application.getInstance().getJavaSourceSpective().addEditor(new File(this.getFilePath()), this.getProject());
			sheet = (BeeSourceSheet) Application.getInstance().getJavaSourceSpective().getWorkspace().getSelectedComponent();
		}
		sheet.scrollToWord((int) this.getStart(), (int) this.getEnd(), (int) this.getLine());

	}
}
