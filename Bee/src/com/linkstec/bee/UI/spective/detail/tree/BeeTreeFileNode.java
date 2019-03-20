package com.linkstec.bee.UI.spective.detail.tree;

import java.io.Serializable;

import javax.swing.Icon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BTreeNode;

public class BeeTreeFileNode extends BeeTreeNode implements BTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8912755589599003028L;

	private boolean project;

	public BeeTreeFileNode(Serializable data, BProject project) {
		super(data);
		this.setProject(project);
	}

	public boolean isProject() {
		return project;
	}

	public void setProject(boolean project) {
		this.project = project;
	}

	@Override
	public Icon getImgeIcon() {
		Icon icon = super.getImgeIcon();
		if (icon == null) {
			return BeeConstants.BOOK_ICON;
		}
		return icon;
	}

	@Override
	public String getUniqueKey() {
		return this.getProject().getName() + this.getFilePath();
	}

}
