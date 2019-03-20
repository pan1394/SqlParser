package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.core.fw.editor.BProject;

public class UIUtils {

	public static JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return panel;
	}

	public static JPanel createFlowRow() {
		JPanel row = createPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		row.setLayout(flow);

		return row;
	}

	public static void setVerticalLayout(JComponent comp) {
		BoxLayout box = new BoxLayout(comp, BoxLayout.Y_AXIS);
		comp.setLayout(box);
	}

	public static ActionButton createButton(String name, String title, String type, BProject project, String fixedValue, boolean dotted) {
		ActionButton b = new ActionButton(title);
		b.setName(name);
		b.setNameDotted(dotted);
		b.setUI(new BeeButtonUI());
		int s = BeeUIUtils.getDefaultFontSize();
		b.setPreferredSize(new Dimension(s * title.length() + s * 2, s * 2));

		b.setType(type);
		b.setProject(project);
		b.setFixedValue(fixedValue);
		return b;
	}

	public static JButton createButton(String title) {
		JButton b = new JButton(title);

		b.setUI(new BeeButtonUI());
		int s = BeeUIUtils.getDefaultFontSize();
		b.setPreferredSize(new Dimension(s * title.length() + s * 2, s * 2));

		return b;
	}
}
