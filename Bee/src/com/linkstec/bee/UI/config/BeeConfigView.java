package com.linkstec.bee.UI.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;

public abstract class BeeConfigView extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5199749688567969372L;

	protected BeeConfig config;
	private JPanel panel = new JPanel();
	protected BeeProject project;

	public BeeConfigView(BeeProject project, BeeConfig config) {
		this.setOpaque(false);
		this.project = project;
		this.config = config;

		this.setLayout(new BorderLayout());
		this.add(this.getView(), BorderLayout.CENTER);
	}

	public JPanel getView() {

		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		JPanel contents = makeContents();
		if (contents != null) {
			if (this.fillContents()) {
				panel.add(contents, BorderLayout.CENTER);
			} else {
				JPanel container = new JPanel();
				container.setOpaque(false);
				FlowLayout layout = new FlowLayout();
				layout.setAlignment(FlowLayout.LEFT);
				container.setLayout(layout);
				container.add(contents);
				panel.add(container, BorderLayout.CENTER);
			}
		}
		JPanel actions = makeActions();
		actions.setOpaque(false);
		if (actions != null) {
			panel.add(actions, BorderLayout.SOUTH);
		}
		return panel;
	}

	public abstract JPanel makeContents();

	public abstract JPanel makeActions();

	protected boolean fillContents() {
		return false;
	}

	protected boolean save() {
		return this.config.save();
	}

	protected void rowValueChanged(String name, String value) {

	}

	public JPanel AddButtons(JButton[] buttons) {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setHgap(10);
		layout.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(layout);

		for (JButton b : buttons) {
			panel.add(b, BorderLayout.SOUTH);
			b.addActionListener(this);
			int s = BeeUIUtils.getDefaultFontSize();
			b.setPreferredSize(new Dimension(10 * s, 2 * s));
			b.setUI(new BeeButtonUI());
		}

		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		b.setEnabled(false);
		this.buttonClicked(b);
	}

	protected void buttonClicked(JButton b) {

	}

}
