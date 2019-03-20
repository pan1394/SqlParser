package com.linkstec.bee.UI.editor.task.problem;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.core.fw.editor.BProject;

public interface BeeEditorError {

	public static final String TYPE_CODE = "code";
	public static final String TYPE_DETAIL = "detail";

	public BProject getProject();

	public void setProject(BProject project);

	public String getFilePath();

	public void setFilePath(String filePath);

	public String getContents();

	public void setContents(String contents);

	public String getTargetPath();

	public String getDisplayFilePath();

	public ImageIcon getIcon();

	public String getType();

	public BeeTree getLinkedFileTree();

	public void showErrorInEditor();
}
