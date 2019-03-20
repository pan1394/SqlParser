package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.fw.editor.BProject;

public class Tab extends JPanel {
	private static final long serialVersionUID = -4472285969370526722L;
	int s = BeeUIUtils.getDefaultFontSize();
	private String title;
	private JPanel view;
	private Object userObject;

	@Override
	public Insets getInsets() {

		return new Insets(s, s, s, s);
	}

	public Tab(String title) {
		this.title = title;
		this.setBackground(SystemColor.windowBorder);
		BorderLayout b = new BorderLayout();
		b.setVgap(0);
		b.setHgap(0);
		this.setLayout(b);

		JPanel topName = new JPanel();
		topName.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setHgap(0);
		flow.setVgap(0);
		flow.setAlignment(FlowLayout.LEFT);
		topName.setLayout(flow);
		JLabel label = new JLabel(title) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5317736416064486729L;

			@Override
			public void paint(Graphics g) {
				RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight() * 2, this.getHeight() / 2, this.getHeight() / 2);
				Rectangle2D remove = new Rectangle2D.Double(0, this.getHeight(), this.getWidth(), this.getHeight());
				Area area = new Area(rect);
				area.subtract(new Area(remove));
				g.setColor(Color.WHITE);
				Graphics2D gg = (Graphics2D) g;
				gg.fill(area);
				super.paint(g);
			}

		};
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(s * title.length() + 2 * s, s * 2));

		topName.add(label);
		this.add(topName, BorderLayout.NORTH);

		view = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4472285969370526722L;

			@Override
			public Insets getInsets() {
				int s = BeeUIUtils.getDefaultFontSize();
				return new Insets(s * 4 / 3, s, s, s);
			}

		};
		view.setBackground(Color.WHITE);
		BoxLayout box = new BoxLayout(view, BoxLayout.Y_AXIS);
		view.setLayout(box);

		this.add(view, BorderLayout.CENTER);
	}

	private JPanel center;

	public JPanel getCeneter() {
		return this.center;
	}

	public JPanel setEditable(ActionButton[] buttons) {

		JPanel view = this.getView();
		view.setLayout(new BorderLayout());
		center = new JPanel();
		center.setName("Model");
		UIUtils.setVerticalLayout(center);

		view.add(center, BorderLayout.CENTER);
		view.add(this.makeActionButtons(center, buttons), BorderLayout.NORTH);
		view.add(this.makeDeleteButton(this), BorderLayout.SOUTH);

		return center;

	}

	private JPanel makeActionButtons(JPanel center, ActionButton[] buttons) {
		JPanel area = new JPanel();
		area.setOpaque(false);
		area.setBorder(new EmptyBorder(s, s, s, s));

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.CENTER);

		ActionListener listner = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ActionButton b = (ActionButton) e.getSource();
				addRow(b.getFixedValue(), b.getProject(), b.getName(), b.getType());
			}

		};

		for (JButton b : buttons) {

			area.add(b);
			b.addActionListener(listner);
		}

		return area;
	}

	public ValueRow addRow(String fiexedValue, BProject project, String name, String type) {

		ValueRow row = new ValueRow(project, name, fiexedValue);
		row.addProperty("TYPE", type);
		center.add(row);
		center.updateUI();

		return row;
	}

	private JPanel makeDeleteButton(Tab tab) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(flow);

		JButton button = new JButton("削除");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel p = (JPanel) tab.getParent();
				tab.getParent().remove(tab);
				p.updateUI();
			}

		});

		panel.add(button);
		return panel;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public JPanel getView() {
		return this.view;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
