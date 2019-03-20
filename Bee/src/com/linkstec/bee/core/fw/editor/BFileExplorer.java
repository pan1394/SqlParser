package com.linkstec.bee.core.fw.editor;

import java.io.File;

public interface BFileExplorer {

	public void initAll();

	public void doDelete();

	public BTreeNode lookupNode(File file, BProject project);

	public void updateNode(File file, BProject project);

	public BTreeNode lookupNode(File file, BTreeNode parent);

	public void addProject(BProject project);

	public String getRoot(BProject project);

	public BTreeNode makeNode(File f, BProject project);

	public void updateProject(BProject project);

	public BTreeNode getProjectRoot(BTreeNode node);

	public void doRefresh();

	public void updateAll();

	public void deleteProject(BProject project, boolean deleteFiles);
}
