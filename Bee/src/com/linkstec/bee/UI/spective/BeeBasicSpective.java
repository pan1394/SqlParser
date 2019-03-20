package com.linkstec.bee.UI.spective;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JSplitPane;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.BasicEditDataSelection;
import com.linkstec.bee.UI.spective.basic.BasicExplorer;
import com.linkstec.bee.UI.spective.basic.BasicLogicProperties;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.BasicMenus;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.BasicWorkSpace;
import com.linkstec.bee.UI.spective.basic.IBasicSubsystemOwner;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenu;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeBasicSpective extends BeeSpective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4185827354152364912L;
	private BasicMenus menus;
	protected JSplitPane datas, work, right, all;

	public BeeBasicSpective(BeeConfig config) {
		super(config);
		menus = new BasicMenus(config.getConfig());
		doLayoutAll();
	}

	private void doLayoutAll() {
		JSplitPane tasksplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.workSpace, tasks);
		tasksplit.setBorder(null);
		tasksplit.setContinuousLayout(true);
		tasksplit.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tasksplit, explorer);
		split.setBorder(null);
		split.setContinuousLayout(true);
		split.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.62));
		split.setDividerSize(BeeUIUtils.getDefaultFontSize() / 4);

		JSplitPane all = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menus, split);
		all.setBorder(null);
		all.setContinuousLayout(true);
		all.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.16));
		all.setDividerSize(BeeUIUtils.getDefaultFontSize() / 4);

		this.add(all, BorderLayout.CENTER);
	}

	public BasicMenus getMenu() {
		return this.menus;
	}

	@Override
	public void openFile(File file, BProject project) {

	}

	public void updateDataResource(BProject project, IBasicSubsystemOwner model) {
		this.menus.updateDataResource(project, model);
	}

	public void updateDataResource(BProject project, SubSystem sub) {
		this.menus.updateDataResource(project, sub);
	}

	@Override
	public BEditorExplorer createExplorer() {
		return new BasicExplorer(this.config);
	}

	@Override
	public BWorkspace createWorkspace() {
		return new BasicWorkSpace(this);
	}

	@Override
	public BEditor createEditor(BProject project) {
		return new BasicLogicSheet(project, null);
	}

	public void setLogic(BNode obj) {
		BasicExplorer explore = (BasicExplorer) this.getExplore();
		explore.getEdit().setLogic(obj);
		explore.setEditSelected();
	}

	public BasicLogicProperties getPropeties() {
		BasicExplorer explore = (BasicExplorer) this.getExplore();
		return explore.getProperties();
	}

	public BasicEditDataSelection getSelection() {
		BasicExplorer explore = (BasicExplorer) this.getExplore();
		return explore.getSelection();
	}

	public LogicMenu getLogicMenu() {
		return this.getMenu().getLogic();
	}

}
