package com.linkstec.bee.UI.look.accordion;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SystemColor;

import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.TabOrientation;
import com.javaswingcomponents.accordion.tabrenderer.AccordionTabRenderer;
import com.javaswingcomponents.framework.painters.configurationbound.ConfigurationBoundPainter;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeAccordion extends JSCAccordion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 405297354846846416L;
	private BProject project;

	public BeeAccordion() {

		this.setTabOrientation(TabOrientation.VERTICAL);
		this.init();

	}

	private void init() {

		this.setUI(new BeeAccordionUI());
		this.setDrawShadow(false);
		this.setTabHeight(BeeUIUtils.getDefaultFontSize() * 2);
		this.setBorder(null);

		this.setSelectedIndex(0);
	}

	@Override
	public AccordionTabRenderer getTabRenderer() {
		return new BeeTabRenderer(this);
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	@Override
	public void updateUI() {
		this.init();
	}

	@Override
	public ConfigurationBoundPainter getBackgroundPainter() {
		return new BackPainter();
	}

	public static class BackPainter extends ConfigurationBoundPainter {

		@Override
		public void paint(Graphics2D g, Rectangle rect) {

			g.setColor(SystemColor.control);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);

		}

	}

}
