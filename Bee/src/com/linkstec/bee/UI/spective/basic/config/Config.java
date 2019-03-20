package com.linkstec.bee.UI.spective.basic.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.utils.UIUtils;
import com.linkstec.bee.core.fw.editor.BProject;

public abstract class Config extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8660125252989541073L;

	protected BProject project;
	protected ConfigModel model;
	protected int s = BeeUIUtils.getDefaultFontSize();

	public Config(BProject project, ConfigModel model) {
		this.setBackground(Color.WHITE);
		this.model = model;
		this.project = project;
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public ConfigModel getConfigModel() {
		return this.model;
	}

	protected JPanel createPanel() {
		return UIUtils.createPanel();
	}

	protected JPanel createFlowRow() {
		return UIUtils.createFlowRow();
	}

	public boolean Debug() {
		return false;
	}

	public void onSelected() {

	}

	public void addConfig(Config config) {
		BeeTabbedPane pane = this.getTabbePane();
		int count = pane.getTabCount();

		JScrollPane opened = null;
		for (int i = 0; i < count; i++) {
			JScrollPane p = (JScrollPane) pane.getComponentAt(i);

			Component c = p.getViewport().getView();
			if (c instanceof Config) {
				Config cf = (Config) c;
				if (cf.getTitle().equals(config.getTitle())) {
					opened = p;
					break;
				}
			}
		}
		if (opened == null) {
			opened = this.makeTab(config);
			this.getTabbePane().addTab(config.getTitle(), config.getIcon(), opened);
		}
		pane.setSelectedComponent(opened);

	}

	private JScrollPane makeTab(JPanel panel) {
		JScrollPane pane = new JScrollPane(panel);
		pane.getVerticalScrollBar().setUnitIncrement(50);
		pane.setBorder(null);
		return pane;
	}

	public BeeTabbedPane getTabbePane() {
		Container p = this.getParent();
		while (p != null) {
			if (p instanceof BeeTabbedPane) {
				return (BeeTabbedPane) p;
			}
			p = p.getParent();
		}
		return null;
	}

	public abstract void beforeSave();

	public abstract String getTitle();

	public abstract ImageIcon getIcon();
}
