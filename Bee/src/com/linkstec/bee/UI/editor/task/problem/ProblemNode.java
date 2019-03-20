package com.linkstec.bee.UI.editor.task.problem;

import java.io.File;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class ProblemNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3480880984577737257L;
	// protected List<ProblemNode> children;
	private String message;
	private String location;
	private String name;
	private BeeEditorError userObject;
	private boolean leaf = true;
	private ProblemNode parent;
	private File file;

	public ProblemNode(BeeEditorError obj) {
		// children = new ArrayList<ProblemNode>();
		if (obj == null) {
			return;
		}
		this.userObject = obj;

		BeeEditorError error = (BeeEditorError) obj;
		message = error.getContents();
		this.location = error.getTargetPath();
		this.name = error.getDisplayFilePath();

	}

	public ImageIcon getIcon() {
		if (userObject instanceof BeeEditorError) {
			BeeEditorError editor = (BeeEditorError) userObject;
			return editor.getIcon();

		}
		return null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public ProblemNode getParent() {
		return parent;
	}

	public void setParent(ProblemNode parent) {
		this.parent = parent;
	}

	public boolean isLeaf() {
		return this.leaf;
	}

	public void setLeft(boolean leaf) {
		this.leaf = leaf;
	}

	public BeeEditorError getUserObject() {
		return userObject;
	}

	public void setUserObject(BeeEditorError userObject) {
		this.userObject = userObject;
	}

	public String getMessage() {
		return message;
	}

	public String getLocation() {
		return this.location;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.message;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ProblemNode) {
			ProblemNode node = (ProblemNode) obj;
			if (this.userObject instanceof BeeSourceError) {
				BeeSourceError error = (BeeSourceError) this.userObject;
				return error.equals(node.userObject);

			} else if (this.userObject instanceof BeeDetailError) {
				BeeDetailError error = (BeeDetailError) this.userObject;
				return error.equals(node.userObject);
			}
		}
		return super.equals(obj);
	}
}
