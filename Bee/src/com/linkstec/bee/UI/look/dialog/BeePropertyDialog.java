package com.linkstec.bee.UI.look.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeePropertyDialog extends JPanel implements BeeDialogCloseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3840976381398431111L;

	private BeePropertyDialog(Hashtable<String, String> list) {
		this.setBackground(Color.WHITE);
		int m = BeeUIUtils.getDefaultFontSize();
		this.setBorder(new EmptyBorder(m, m, m, m));
		GridLayout layout = new GridLayout(0, 1);
		this.setLayout(layout);
		Enumeration<String> keys = list.keys();
		while (keys.hasMoreElements()) {
			String title = keys.nextElement();
			String value = list.get(title);
			this.makeRow(title, value);
		}
	}

	private void makeRow(String title, String value) {
		int m = BeeUIUtils.getDefaultFontSize();

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setHgap(0);
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);

		JTextField label = new JTextField(title);
		label.setBorder(new LineBorder(Color.LIGHT_GRAY));
		label.setEditable(false);
		label.setPreferredSize(new Dimension(m * 8, (int) (m * 2)));
		panel.add(label);

		JTextField v = new JTextField(value);
		v.setBackground(Color.WHITE);
		v.setEditable(false);
		v.setBorder(new LineBorder(Color.LIGHT_GRAY));
		v.setPreferredSize(new Dimension(m * 25, (int) (m * 2)));
		panel.add(v);

		this.add(panel);
	}

	public static void showDialog(String title, Hashtable<String, String> list) {
		BeePropertyDialog dialog = new BeePropertyDialog(list);
		BeeDialog.showDialog(title, dialog, dialog);
	}

	@Override
	public void onclose() {

	}
}
