package com.linkstec.bee.UI.list;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class BeeListCellRenderer implements ListCellRenderer {
	private JPanel panel;
	private JLabel label;
	private int gap = BeeUIUtils.getDefaultFontSize() / 4;

	public BeeListCellRenderer() {
		panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEADING);
		panel.setLayout(layout);
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(gap, gap, gap, gap));
		label = new JLabel();
		panel.add(label);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (value != null) {
			label.setText(value.toString());
		}
		if (isSelected) {
			panel.setBackground(BeeConstants.SELECTED_BACKGROUND_COLOR);
		} else {
			panel.setBackground(Color.WHITE);
		}
		return panel;
	}

}
