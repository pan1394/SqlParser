package com.linkstec.bee.UI.look.accordion;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.tabrenderer.AccordionTabRenderer;//.plaf.basic.BasicHorizontalTabRenderer;
import com.javaswingcomponents.accordion.tabrenderer.GetTabComponentParameter;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;

public class BeeTabRenderer extends JPanel implements AccordionTabRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1009655947558028499L;
	private GetTabComponentParameter parameter;
	private JSCAccordion accordion;
	private BasicMenu menu;

	public BeeTabRenderer(JSCAccordion accordion) {

		this.accordion = accordion;
		this.setOpaque(true);
	}

	@Override
	public JComponent getTabComponent(GetTabComponentParameter parameter) {
		this.parameter = parameter;

		if (parameter.panelContents instanceof BasicMenu) {
			menu = (BasicMenu) parameter.panelContents;
		}
		return this;
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		GradientPaint grdp = new GradientPaint(0, 0, BeeConstants.TOOLBAR_GREDIENT_UP, 0, getHeight(), BeeConstants.TOOLBAR_GREDIENT_DOWN);
		g2d.setPaint(grdp);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		Font font = g.getFont();
		font = font.deriveFont((float) BeeUIUtils.getDefaultFontSize());
		g2d.setFont(font);
		int height = this.getHeight();
		FontMetrics fm = g.getFontMetrics();
		int y = (height - BeeUIUtils.getDefaultFontSize()) / 2 + fm.getAscent() - fm.getDescent();

		if (this.menu != null) {
			Image img = this.menu.getIcon().getImage();
			int imgWidth = img.getWidth(this);
			int imgHeigh = img.getHeight(this);

			imgWidth = imgWidth * BeeUIUtils.getDefaultFontSize() / 16;
			imgHeigh = imgHeigh * BeeUIUtils.getDefaultFontSize() / 16;

			g2d.drawImage(img, BeeUIUtils.getDefaultFontSize() / 2, (height - imgHeigh) / 2, imgWidth, imgHeigh, this);
			g2d.drawString(parameter.tabText, BeeUIUtils.getDefaultFontSize() + imgWidth, y);
		} else {
			g2d.drawString(parameter.tabText, BeeUIUtils.getDefaultFontSize() / 2, y);
		}
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		if (parameter.tabIndex == 0) {
			g2d.drawLine(0, 0, getWidth(), 0);
		}
	}

	@Override
	public void setAccordion(JSCAccordion accordion) {
		this.accordion = accordion;
	}

}
