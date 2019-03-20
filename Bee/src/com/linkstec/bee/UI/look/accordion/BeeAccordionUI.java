package com.linkstec.bee.UI.look.accordion;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.JSCAccordion.TabInformation;
import com.javaswingcomponents.accordion.plaf.basic.BasicAccordionUI;

public class BeeAccordionUI extends BasicAccordionUI {

	public BeeAccordionUI() {
		this.setTabPadding(0);
		this.shadowSize = 0;
		this.setBackgroundPadding(0);

	}

	public static BasicAccordionUI createUI(JComponent c) {
		return new BeeAccordionUI();
	}

	@Override
	protected void paintBackgroundOnCachedImage(TabInformation tab, Rectangle contentsRectangle, BufferedImage contentImage, JSCAccordion accordion) {

		// super.paintBackgroundOnCachedImage(tab, contentsRectangle, contentImage,
		// accordion);
	}

	@Override
	public void paint(Graphics arg0, JComponent arg1) {
		// TODO Auto-generated method stub
		super.paint(arg0, arg1);
	}

}
