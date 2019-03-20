package com.linkstec.bee.UI.look.tip;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.popup.BeePopUI;

public class BeeToolTip extends BeePopUI implements MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3554922286505551164L;

	private int x;

	private int y;

	private JTextPane text;
	private static BeeToolTip instance;
	private List<JComponent> comps = new ArrayList<JComponent>();
	private int minWidth = BeeUIUtils.getDefaultFontSize() * 30;
	private TooltipTimer delayTimer;
	private String current;

	public static Color BACK_COLOR = Color.decode("#FFFFCC");

	public static BeeToolTip getInstance() {
		if (instance == null) {
			instance = new BeeToolTip(null);
		}
		return instance;
	}

	public void register(JComponent comp) {
		comps.add(comp);
		comp.addMouseMotionListener(this);
	}

	public void remove(JComponent comp) {
		comp.removeMouseMotionListener(this);
		comps.remove(comp);
	}

	private BeeToolTip(JComponent comp) {
		super(comp);
		this.setName("Visible");
		text = new JTextPane();
		text.setBackground(BACK_COLOR);
		text.setContentType("text/html; charset=EUC-JP");
		text.setEditable(false);
		this.scroll.getViewport().setView(text);
		text.addMouseListener(adapter);
		text.setFont(BeeUIUtils.getDefaultFont());
	}

	private void startShow(int ex, int ey, JComponent comp) {

		if (!comp.isVisible()) {
			return;
		}

		if (!comp.isShowing()) {
			return;
		}

		String s = comp.getToolTipText();
		if (s == null) {

			if (this.delayTimer != null) {
				delayTimer.stop();
			}
			return;
		}
		if (s.equals("")) {
			this.setVisible(false);
		}
		if (s.equals(this.current)) {
			// delayTimer.stop();
			return;
		}
		this.current = s;
		s = s.replaceAll("。", "。<br/>");
		s = "<html><body style='font-size:" + BeeUIUtils.getDefaultFontSize() + "'>" + s + "</body><html>";
		text.setText(s);
		// text.insertIcon(BeeConstants.METHOD_ICON);
		text.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

		Point point = comp.getLocationOnScreen();
		if (this.isVisible()) {
			Rectangle rect = this.getBounds();
			rect.y = rect.y - point.y;
			rect.x = rect.x - point.x;
			if (rect.contains(ex, ey)) {
				return;
			}
		}
		if (delayTimer != null) {
			delayTimer.stop();
		}
		delayTimer = new TooltipTimer(this, comp);
		this.delayTimer.start(ex, ey);
		// this.setSize(new Dimension(0, 0));

		JComponent c = this.findScroll(comp);
		if (c == null) {
			c = comp;
		}
		this.x = c.getLocationOnScreen().x + c.getWidth();// point.x + ex;
		this.y = point.y + ey;
		// this.delayTimer.stop();

		// this.toShow(true);

	}

	private JComponent findScroll(Container comp) {
		if (comp instanceof JScrollPane) {
			return (JComponent) comp;
		}
		Container c = comp.getParent();
		if (c != null) {
			return this.findScroll(c);
		}
		return null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Object obj = e.getSource();
		if (this.comps.contains(obj)) {
			this.startShow(e.getX(), e.getY(), (JComponent) obj);
		}
	}

	public void showImmediatelyWithLocation(int x, int y, JComponent comp) {
		this.startShow(x, y, comp);
	}

	private void toShow(boolean force) {

		this.unstick();
		this.delayTimer.stop();

		int bottom = y + this.getHeight() + this.handlerHeight;
		int screenBottom = BeeUIUtils.getScreenSize().height;
		if (bottom > screenBottom) {
			y = y - this.getHeight();
		}

		if (y < 100) {
			y = 100;
		}

		this.setSize(new Dimension(minWidth, BeeUIUtils.getDefaultFontSize() * 15));

		int b = y + this.getHeight() + this.handlerHeight;
		if (b > screenBottom) {
			this.setSize(this.getWidth(), (screenBottom - y - this.handlerHeight) / 2);
		}

		this.setLocation(x, y);
		this.setVisible(true);
		text.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
	}

	public static class TooltipTimer implements ActionListener {

		private BeeToolTip tip;
		private int startX, startY;
		private JComponent source;
		private Timer timer = new Timer(1500, this);
		private boolean running = false;

		public TooltipTimer(BeeToolTip tip, JComponent comp) {
			this.tip = tip;
			this.source = comp;
		}

		public void stop() {
			timer.stop();
			running = false;
		}

		public void start(int x, int y) {
			this.startX = x;
			this.startY = y;
			timer.start();
			running = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Point point = this.source.getMousePosition();
			if (point != null && running) {
				tip.toShow(false);
			}
		}

	}

}
