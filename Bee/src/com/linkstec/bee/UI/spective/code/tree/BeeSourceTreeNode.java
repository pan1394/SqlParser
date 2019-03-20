package com.linkstec.bee.UI.spective.code.tree;

import java.io.File;
import java.io.Serializable;

import javax.swing.Icon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeSourceTreeNode extends BeeTreeFileNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4816704492774592665L;

	private boolean subConfiged = false;
	private boolean lib = false;
	private boolean libRoot = false;
	private boolean sourceRoot = false;
	private File file;

	public boolean isSubConfiged() {
		return subConfiged;
	}

	public void setSubConfiged(boolean subConfiged) {
		this.subConfiged = subConfiged;
	}

	public boolean isLibRoot() {
		return libRoot;
	}

	public boolean isSourceRoot() {
		return sourceRoot;
	}

	public void setSourceRoot(boolean source) {
		this.sourceRoot = source;
	}

	public void setLibRoot(boolean libRoot) {
		this.libRoot = libRoot;
	}

	public boolean isLib() {
		return lib;
	}

	public void setLib(boolean lib) {
		this.lib = lib;
	}

	public BeeSourceTreeNode(File file, BProject project) {
		super(file, project);
		this.setProject(project);
		this.file = file;
		if (file != null)
			this.setFilePath(file.getAbsolutePath());
		this.setUserObject(file);
	}

	@Override
	public String getUniqueKey() {

		if (this.getProject() == null) {
			Debug.d();
		}

		if (this.file != null) {
			return this.getProject().getName() + this.file.getAbsolutePath();
		}
		if (this.getDisplay() != null) {
			return this.getProject().getName() + this.getDisplay();
		}
		return null;
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File f) {
		this.file = f;
		if (file != null)
			this.setFilePath(f.getAbsolutePath());
	}

	public String toString() {
		if (file != null) {
			if (file.isDirectory()) {
				String s = file.getAbsolutePath();
				String r = this.getProject().getSourcePath();
				if (s.startsWith(r) && !s.equals(r)) {
					s = s.substring(r.length() + 1, s.length());
					return s.replace(File.separatorChar, '.');
				} else {
					return file.getName();
				}
			} else {
				return file.getName();
			}
		} else {
			return null;
		}
	}

	@Override
	public Icon getImgeIcon() {
		File f = this.getFile();
		if (f == null) {
			return super.getImgeIcon();
		} else {
			if (this.isLeaf()) {

				return BeeConstants.JAVA_SOURCE_ICON;
			} else {
				return BeeConstants.TREE_FOLDER_ICON;
			}
		}

	}

	@Override
	public String getFilePath() {
		if (this.file != null) {
			return this.file.getAbsolutePath();
		}

		return null;
	}

}
