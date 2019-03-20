package com.linkstec.bee.UI.editor.task.problem;

import java.io.File;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeDetailError implements BeeEditorError {
	private String filePath;
	private String contents;
	private String targetPath;
	private BProject project;
	private Object sheet;
	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public Object getSheet() {
		return sheet;
	}

	public void setSheet(Object sheet) {
		this.sheet = sheet;
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public boolean equals(Object obj) {
		if (obj instanceof BeeDetailError) {
			BeeDetailError error = (BeeDetailError) obj;
			if (!error.contents.equals(this.contents)) {
				return false;
			}
			if (!error.getFilePath().equals(this.getFilePath())) {
				return false;
			}
			if (error.targetPath != this.targetPath) {
				return false;
			}

			return true;
		}
		return super.equals(obj);
	}

	@Override
	public String getDisplayFilePath() {
		String path = this.getProject().getDesignPath();
		String filePath = this.getFilePath();
		if (filePath == null) {
			return this.getProject().getName() + this.getSheet().toString();
		} else {
			return this.getProject().getName() + filePath.substring(path.length()).replace(File.separator, "/");
		}

	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ERROR_ICON;
	}

	@Override
	public String getType() {
		return TYPE_DETAIL;
	}

	@Override
	public BeeTree getLinkedFileTree() {
		return Application.getInstance().getDesignSpective().getFileExplore();
	}

	@Override
	public void showErrorInEditor() {
		BWorkspace space = Application.getInstance().getDesignSpective().getWorkspace();
		int count = space.getTabCount();
		boolean opned = false;
		for (int i = 0; i < count; i++) {
			EditorBook book = (EditorBook) space.getComponentAt(i);
			if (book.getFile() != null) {
				if (book.getFile().getAbsolutePath().equals(this.getFilePath())) {
					opned = true;
					BeeGraphSheet bsheet = (BeeGraphSheet) this.getSheet();
					if (bsheet != null) {
						Object objec = this.getUserObject();
						bsheet.getGraph().setSelectionCell(objec);
						bsheet.scrollCellToVisible(objec);
						opned = true;
						break;
					}

				}

			}
		}
		if (!opned) {
			File f = new File(this.getFilePath());

			if (f != null) {
				if (f.exists()) {
					BeeActions.addDetailPane(f, this.getProject());
				}
			}
		}
	}
}
