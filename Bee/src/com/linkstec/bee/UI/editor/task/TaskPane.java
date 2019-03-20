package com.linkstec.bee.UI.editor.task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class TaskPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -383938515580083195L;
	private JScrollPane pane;

	public TaskPane() {
		pane = new JScrollPane() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7376620053992549885L;

			@Override
			protected void paintBorder(Graphics g) {

			}
		};
		this.init();

	}

	public TaskPane(Component view) {
		pane = new JScrollPane(view) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2683950393906994944L;

			@Override
			protected void paintBorder(Graphics g) {

			}
		};
		this.init();
	}

	public JScrollPane getScroll() {
		return this.pane;
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
		this.setOpaque(false);
	}

	@Override
	protected void paintBorder(Graphics g) {

	}

	protected JViewport createViewport() {
		JViewport view = new JViewport();
		view.setBorder(null);
		view.setBackground(Color.WHITE);

		return view;
	}

}
