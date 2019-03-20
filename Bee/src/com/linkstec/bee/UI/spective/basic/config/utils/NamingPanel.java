package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.NamingType;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;

public class NamingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3343783539266817366L;
	private JPanel examplePanel = new JPanel();
	private JLabel example = new JLabel();
	private NamingBar bar;

	public NamingPanel(boolean dotted) {
		bar = new NamingBar(this, dotted);
		this.setLayout(new BorderLayout());
		this.add(bar, BorderLayout.CENTER);
		this.setOpaque(false);
		examplePanel.setOpaque(false);
		this.add(examplePanel, BorderLayout.SOUTH);

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		examplePanel.setLayout(layout);

	}

	public NamingBar getBar() {
		return this.bar;
	}

	public void updateExample() {
		this.examplePanel.removeAll();
		NamingModel model = this.bar.getModel();
		List<NamingType> list = model.getList();
		int index = 0;
		String s = "ex. ";
		for (NamingType type : list) {
			if (index == 0) {
				s = s + type.getExample();
			} else {
				if (model.isDotted()) {
					s = s + ".";
				}
				s = s + type.getExample();
			}
			index++;
		}
		example.setText(s);
		examplePanel.add(example);
	}

}
