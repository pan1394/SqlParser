package com.linkstec.bee.UI.spective.basic;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.javaswingcomponents.accordion.JSCAccordion.TabInformation;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.accordion.BeeAccordion;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.data.BasicDataResource;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenu;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenuNode;
import com.linkstec.bee.UI.spective.detail.tree.ClassesTree;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicMenus extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3450321150273996595L;
	// protected BeeAccordion datasourcePane;
	protected BeeAccordion dataresourcePane;
	protected BeeAccordion logicPane;

	private LogicMenu logic;

	public BasicMenus(Configuration config) {
		this.setLayout(new BorderLayout());

		this.createDataResource(config);
		// this.createDataSource(project);
		this.createLogic();

		JSplitPane tasksplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataresourcePane, logicPane);
		tasksplit.setBorder(null);
		tasksplit.setContinuousLayout(true);
		tasksplit.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.6));

		this.add(tasksplit, BorderLayout.CENTER);
	}

	private void createDataResource(Configuration config) {
		dataresourcePane = new BeeAccordion();

		List<BProject> projects = config.getProjects();
		for (BProject project : projects) {

			BasicSystemModel model = BasicSystemModel.load(project);
			if (model == null) {
				model = new BasicSystemModel(project);
				SubSystem sub = new SubSystem(model);
				sub.setName("共通");
				sub.setLogicName("common");
				sub.setId("000");
				sub.setDesc("共通機能");
				model.getSubs().add(sub);
				if (Application.INSTANCE_COMPLETE) {
					model.save();
				}
			}
			BasicDataResource data = new BasicDataResource(model, project);
			this.dataresourcePane.addTab(project.getName(), data);
		}
	}

	public void updateDataResource(BProject project, IBasicSubsystemOwner model) {
		List<TabInformation> tabs = dataresourcePane.getTabs();
		for (TabInformation info : tabs) {
			JComponent comp = info.getContents();
			if (comp instanceof BasicDataResource) {
				BasicDataResource data = (BasicDataResource) comp;
				BProject p = data.getProject();
				if (p.getName().equals(project.getName())) {
					data.updateSub(model);
					break;
				}
			}

		}
	}

	public void updateDataResource(BProject project, SubSystem sub) {
		List<TabInformation> tabs = dataresourcePane.getTabs();
		for (TabInformation info : tabs) {
			JComponent comp = info.getContents();
			if (comp instanceof BasicDataResource) {
				BasicDataResource data = (BasicDataResource) comp;
				BProject p = data.getProject();
				if (p.getName().equals(project.getName())) {
					data.updateSub(sub);
					break;
				}
			}

		}
	}

	public BasicDataResource getDataResurce(BProject project) {
		List<TabInformation> tabs = dataresourcePane.getTabs();
		for (TabInformation info : tabs) {
			JComponent comp = info.getContents();
			if (comp instanceof BasicDataResource) {
				BasicDataResource data = (BasicDataResource) comp;
				BProject p = data.getProject();
				if (p.getName().equals(project.getName())) {
					return data;

				}
			}

		}
		return null;
	}

	public LogicMenu getLogic() {
		return logic;
	}

	private void createLogic() {
		logicPane = new BeeAccordion();
		LogicMenuNode root = new LogicMenuNode(null);
		logic = new LogicMenu(root);
		logicPane.addTab(logic.getTitle(), logic);
		logicPane.addTab("リソース", new ClassesTree(null));
	}
}
