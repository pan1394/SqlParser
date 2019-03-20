package com.linkstec.bee.UI.look.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import com.linkstec.bee.UI.look.BeeButtonUI;

public class BeeButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8749890543256983312L;

	private int inclTab = 5;

	public BeeButton() {
		super();
		this.setUI(new BeeButtonUI());
	}

	public BeeButton(Action a) {
		super(a);
		this.setUI(new BeeButtonUI());
	}

	public BeeButton(Icon icon) {
		super(icon);
		this.setUI(new BeeButtonUI());
	}

	public BeeButton(String text, Icon icon) {
		super(text, icon);
		this.setUI(new BeeButtonUI());
	}

	public BeeButton(String text) {
		super(text);
		this.setUI(new BeeButtonUI());
	}

	@Override
	protected void paintBorder(Graphics g) {

	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
	}

	@Override
	protected void paintChildren(Graphics g) {

		super.paintChildren(g);
	}

	@Override
	public void paintComponents(Graphics g) {

		super.paintComponents(g);
	}

	@Override
	public void paintAll(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		super.paintAll(g);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	@Override
	public Color getBackground() {
		return null;
	}

	private Polygon getShape(int w, int h) {
		int[] xp = new int[] { 0, 0, inclTab, w - inclTab, w, w, w - inclTab, inclTab };
		int[] yp = new int[] { h - inclTab, inclTab, 0, 0, inclTab, h - inclTab, h, h };
		Polygon shape = new Polygon(xp, yp, xp.length);
		return shape;
	}

}
