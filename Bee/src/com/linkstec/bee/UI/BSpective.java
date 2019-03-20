package com.linkstec.bee.UI;

import java.io.File;

import com.linkstec.bee.core.fw.editor.BProject;

public interface BSpective {

	public BEditorFileExplorer getFileExplore();

	public BEditorOutlookExplorer getOutline();

	public BWorkspace getWorkspace();

	public void openFile(File file, BProject project);

	public BEditorTask getTask();

	public final static String CONTAINER = "container";
	public final static String DETAIL_DESIGN = "detail";
	public final static String JAVA_SOURCE = "java_source";
	public final static String BASIC_DESIGN = "basic";

}
