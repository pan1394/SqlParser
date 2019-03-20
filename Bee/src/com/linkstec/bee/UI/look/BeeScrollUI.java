package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.task.problem.BeeSourceError;
import com.linkstec.bee.UI.look.scroll.BeeScrollPane;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorActionListener;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorListener;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorPoint;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BAlertor;
import com.linkstec.bee.core.fw.BObject;
import com.mxgraph.util.mxRectangle;
import com.sun.java.swing.plaf.windows.WindowsScrollPaneUI;

public class BeeScrollUI extends WindowsScrollPaneUI implements MouseListener, MouseMotionListener, ComponentListener, BeeScrollPaneErrorListener {

	private int borderWidth = BeeUIUtils.getDefaultFontSize();
	private Rectangle rect;
	private Hashtable<String, BeeScrollPaneErrorPoint> errors = new Hashtable<String, BeeScrollPaneErrorPoint>();

	public static ComponentUI createUI(JComponent c) {
		return new BeeScrollUI();
	}

	public BeeScrollUI() {

	}

	public void installUI(JComponent x) {
		super.installUI(x);
		scrollpane.setBorder(new EmptyBorder(0, 0, 0, borderWidth));
		scrollpane.addComponentListener(this);
		scrollpane.addMouseListener(this);
		scrollpane.setViewportBorder(null);
		scrollpane.addMouseMotionListener(this);
		rect = new Rectangle(scrollpane.getWidth() - borderWidth, 0, borderWidth, (int) scrollpane.getSize().getHeight());
	}

	public void addErrorLine(String name, int line, String message, Object object) {
		if (scrollpane == null) {
			return;
		}
		JViewport view = scrollpane.getViewport();
		double height = view.getHeight();
		double contentsHeight = view.getView().getHeight();
		double h = (line / contentsHeight) * height;

		BeeScrollPaneErrorPoint point = new BeeScrollPaneErrorPoint();
		point.setHeight((int) h);
		point.setName(name);
		point.setUserObject(object);
		point.setMessage(message);
		errors.put(name, point);
		scrollpane.repaint(rect);

	}

	public void removeErrorLine(String name) {
		if (errors.containsKey(name)) {
			this.errors.remove(name);
			scrollpane.repaint(rect);
		}
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		g.setColor(BeeConstants.BORDER_BACKCOLOR);

		g.fillRect(scrollpane.getWidth() - borderWidth, 0, borderWidth, (int) scrollpane.getSize().getHeight());

		Enumeration<BeeScrollPaneErrorPoint> enu = this.errors.elements();
		while (enu.hasMoreElements()) {
			BeeScrollPaneErrorPoint in = enu.nextElement();
			int line = in.getHeight();
			Color back = BeeConstants.BORDER_ALERT_BACKCOLOR;
			Color border = BeeConstants.BORDER_ALERT_BORDERCOLOR;
			Object obj = in.getUserObject();
			if (obj instanceof BObject) {
				BObject bobj = (BObject) obj;
				BAlertor alert = bobj.getAlertObject();
				if (alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR)) {
					back = BeeConstants.BORDER_ERROR_BACKCOLOR;
					border = BeeConstants.BORDER_ERROR_BORDERCOLOR;
				}
			} else if (obj instanceof BeeSourceError) {
				back = BeeConstants.BORDER_ERROR_BACKCOLOR;
				border = BeeConstants.BORDER_ERROR_BORDERCOLOR;
			}
			g.setColor(back);
			g.fillRect(rect.x + 2, line, this.borderWidth - 3, BeeUIUtils.getDefaultFontSize() / 2);
			g.setColor(border);
			g.drawRect(rect.x + 2, line, this.borderWidth - 3, BeeUIUtils.getDefaultFontSize() / 2);
		}
		this.paintSelected(g, c);
	}

	private void paintSelected(Graphics g, JComponent c) {
		if (c instanceof BeeGraphSheet) {
			BeeGraphSheet sheet = (BeeGraphSheet) c;
			Object[] list = sheet.getGraph().getSelectionCells();
			for (Object obj : list) {
				if (obj != null && obj instanceof BasicNode) {

					BasicNode node = (BasicNode) obj;
					mxRectangle rect = sheet.getGraph().getBoundingBox(node);
					Graphics2D gg = (Graphics2D) g;
					gg.setColor(BeeConstants.BACKGROUND_COLOR);

					JViewport view = scrollpane.getViewport();
					double height = view.getHeight();
					double contentsHeight = view.getView().getHeight();
					if (rect == null) {
						return;
					}
					double h = (rect.getY() / contentsHeight) * height;
					int rectHeight = (int) (rect.getHeight() / contentsHeight * height);
					if (rectHeight < BeeUIUtils.getDefaultFontSize() / 2) {
						rectHeight = BeeUIUtils.getDefaultFontSize() / 2;
					}
					g.fillRect(scrollpane.getWidth() - borderWidth + 1, (int) h, this.borderWidth, rectHeight);
				}
			}
		} else if (c instanceof BeeScrollPane) {
			BeeScrollPane scroll = (BeeScrollPane) c;
			Component comp = scroll.getViewport().getView();
			if (comp instanceof BeeSourceSheet) {

				BeeSourceSheet sheet = (BeeSourceSheet) comp;
				List<Integer> list = sheet.getSearchResult();
				for (int h : list) {
					Graphics2D gg = (Graphics2D) g;
					gg.setColor(BeeConstants.BACKGROUND_COLOR);

					JViewport view = scrollpane.getViewport();
					double height = view.getHeight();
					double contentsHeight = view.getView().getHeight();

					h = (int) ((h / contentsHeight) * height);
					g.fillRect(scrollpane.getWidth() - borderWidth + 1, (int) h, this.borderWidth, BeeUIUtils.getDefaultFontSize() / 2);
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Enumeration<BeeScrollPaneErrorPoint> enu = this.errors.elements();
		this.scrollpane.setToolTipText(null);
		while (enu.hasMoreElements()) {
			BeeScrollPaneErrorPoint in = enu.nextElement();
			int line = in.getHeight();
			Rectangle point = new Rectangle(rect.x + 2, line, this.borderWidth - 3, BeeUIUtils.getDefaultFontSize() / 2);
			if (point.contains(e.getPoint())) {
				if (this.scrollpane instanceof BeeScrollPaneErrorActionListener) {
					BeeScrollPaneErrorActionListener listener = (BeeScrollPaneErrorActionListener) this.scrollpane;
					listener.errorClicked(in);

				}

			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (this.scrollpane == null) {
			return;
		}
		int x = e.getX();
		int y = e.getY();
		if (this.rect.contains(x, y)) {

			JViewport view = scrollpane.getViewport();
			int height = view.getHeight();
			int sh = view.getView().getHeight();
			int h = y * sh / height;
			int ry = view.getViewRect().y;

			view.scrollRectToVisible(new Rectangle(0, h - ry - height / 2, 10, height));

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (scrollpane != null) {
			rect = new Rectangle(scrollpane.getWidth() - borderWidth, 0, borderWidth, (int) scrollpane.getSize().getHeight());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (scrollpane != null) {
			rect = new Rectangle(scrollpane.getWidth() - borderWidth, 0, borderWidth, (int) scrollpane.getSize().getHeight());
		}

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void error(String name, int line, String message, Object object) {
		addErrorLine(name, line, message, object);
	}

	public boolean hasError() {
		Collection<BeeScrollPaneErrorPoint> values = errors.values();
		for (BeeScrollPaneErrorPoint point : values) {
			Object obj = point.getUserObject();
			if (obj instanceof BObject) {
				BObject bobj = (BObject) obj;
				BAlertor alert = bobj.getAlertObject();
				if (alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAlert() {
		Collection<BeeScrollPaneErrorPoint> values = errors.values();
		for (BeeScrollPaneErrorPoint point : values) {
			Object obj = point.getUserObject();
			if (obj instanceof BObject) {
				BObject bobj = (BObject) obj;
				BAlertor alert = bobj.getAlertObject();
				if (alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_WARNING)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void clearError(String name) {
		removeErrorLine(name);
	}

	public void clearError() {
		this.errors.clear();
		this.scrollpane.repaint(this.rect);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Enumeration<BeeScrollPaneErrorPoint> enu = this.errors.elements();
		if (this.scrollpane == null) {
			return;
		}
		this.scrollpane.setToolTipText(null);
		while (enu.hasMoreElements()) {
			BeeScrollPaneErrorPoint in = enu.nextElement();
			int line = in.getHeight();
			Rectangle point = new Rectangle(rect.x + 2, line, this.borderWidth - 3, BeeUIUtils.getDefaultFontSize() / 2);
			if (point.contains(e.getPoint())) {
				String message = "<html>" + in.getMessage() + "</html>";
				if (in.getUserObject() != null) {
					message = "<html>" + in.getMessage() + "<br/>" + in.getUserObject().toString() + "</html>";
				}
				this.scrollpane.setToolTipText(message);
			}
		}
	}

}
