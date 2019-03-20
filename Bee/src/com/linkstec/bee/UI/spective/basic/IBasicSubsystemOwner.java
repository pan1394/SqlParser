package com.linkstec.bee.UI.spective.basic;

import java.io.File;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public interface IBasicSubsystemOwner {

	public String getName();

	public String getLogicName();

	public List<SubSystem> getSubs();

	public void setSubs(List<SubSystem> subs);

	public BEditor getListSheet(BProject project);

	public String getProjectName();

	public IBasicSubsystemOwner getOwner();

	public File save();

	public String getPath();
}
