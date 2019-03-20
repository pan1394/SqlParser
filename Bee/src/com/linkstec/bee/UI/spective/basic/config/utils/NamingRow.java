package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeUIUtils;

public class NamingRow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 990838920748957453L;

	private NamingPanel panel;

	public NamingRow(String title, String name, boolean dotted) {
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		this.setLayout(flow);

		JLabel pack = new JLabel(title);

		int s = BeeUIUtils.getDefaultFontSize();
		pack.setPreferredSize(new Dimension(s * 10, s * 2));
		this.add(pack);

		panel = new NamingPanel(dotted);
		this.add(panel);

		this.setName(name);

	}

	public NamingPanel getNamingPanel() {
		return this.panel;
	}
}
