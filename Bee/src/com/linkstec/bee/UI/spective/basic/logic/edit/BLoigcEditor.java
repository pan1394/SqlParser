package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.action.BAction;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.Menuable;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.BasicCanvas;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent.mxGraphControl;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BLoigcEditor implements MouseMotionListener, MouseListener {

	private BasicLogicSheet sheet;
	private Target target;
	private MenuTarget menuTarget;

	private int fontSize = BeeUIUtils.getDefaultFontSize();
	private int gap = fontSize / 3;
	private Color color = Color.decode("#" + BeeConstants.ELEGANT_BLUE_COLOR);
	private Handler handler = new Handler();

	public BLoigcEditor(BasicLogicSheet sheet) {
		this.sheet = sheet;
		sheet.getGraphControl().addMouseMotionListener(this);
		sheet.getGraphControl().addMouseListener(this);

	}

	public void paint(mxCellState state, BasicCanvas canvas) {

		Graphics2D g = canvas.getGraphics();
		if (g == null) {
			return;
		}
		if (state == null) {
			return;
		}

		Object obj = state.getCell();
		if (obj instanceof BNode) {
			Graphics2D previousGraphics = g;
			BNode node = (BNode) obj;
			g = canvas.createTemporaryGraphics(state.getStyle(), 100, null);
			this.drawCell(g, node, state);
			g.dispose();
			g = previousGraphics;
		}
	}

	private void drawCell(Graphics2D g, BNode node, mxCellState state) {
		if (this.target != null && target.getNode().equals(node)) {

			this.makeTargetInfo(state, g);
			this.paintBackGround(g, target);

			// paint action
			mxICell c = this.target.getNode();
			EditAction action = null;
			if (c instanceof BNode) {
				BNode n = (BNode) c;
				action = n.getAction();
			}

			ImageIcon icon = null;
			Object cell = state.getCell();
			if (cell instanceof BNode) {
				BNode b = (BNode) cell;
				icon = b.getIcon();
			}
			if (icon == null) {
				icon = action.getIcon();
			}
			if (icon != null) {
				Rectangle rect = target.getIconBound();
				Image img = icon.getImage();
				g.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);

			}
			if (target.isMouseOver()) {

				List<ActionComponent> actions = target.getComponents();
				for (ActionComponent a : actions) {
					String name = a.getAction().getName();
					Rectangle rect = a.getRect();
					if (a.isMouseOver()) {
						g.setColor(Color.GREEN);
					} else {
						g.setColor(Color.WHITE);
					}

					Image img = BeeConstants.METHOD_ICON.getImage();
					// int imgWidth=img.getWidth(this.sheet);
					int imgHeight = img.getHeight(this.sheet);

					g.drawImage(img, rect.x, rect.y + (rect.height - imgHeight) / 2, null);
					g.drawString(name, rect.x + fontSize, rect.y + g.getFontMetrics().getAscent());
				}
			}
		}
	}

	private void makeTargetInfo(mxCellState state, Graphics2D g) {
		mxRectangle box = state.getBoundingBox();
		int max = fontSize * 3;
		int h = Math.min(max, (int) box.getHeight());
		int gap = h / 4;

		int w = fontSize + gap * 2;

		int x = (int) (box.getX()) - w + 1;
		int y = (int) (box.getY()) + 1;

		Rectangle rect = new Rectangle(x, y, w, h);
		this.target.setRect(rect);

		mxICell c = this.target.getNode();
		EditAction action = null;
		if (c instanceof BNode) {
			BNode n = (BNode) c;
			action = n.getAction();
		}

		ImageIcon icon = null;

		Object cell = state.getCell();
		if (cell instanceof BNode) {
			BNode b = (BNode) cell;
			icon = b.getIcon();
		}

		if (icon == null) {
			icon = action.getIcon();
		}

		int stringX = x + gap;

		if (icon != null) {
			if (!target.isInited()) {
				Rectangle iconb = new Rectangle(stringX, y + (h - fontSize) / 2, fontSize, fontSize);
				this.target.setIconBound(iconb);
			}
		}

		if (target.getIconBound() != null) {
			stringX = stringX + target.getIconBound().width + fontSize / 4;
		}

		if (!target.isInited()) {
			int compX = stringX;
			List<BAction> actions = action.getActions();
			int stringHeight = g.getFontMetrics().getHeight();
			int stringY = y + (h - stringHeight) / 2;// + g.getFontMetrics().getAscent();
			for (BAction a : actions) {

				String name = a.getName();
				int sw = SwingUtilities.computeStringWidth(g.getFontMetrics(), name) + fontSize;

				// target.getComponents().clear();
				ActionComponent comp = new ActionComponent();
				comp.setAction(a);

				Rectangle bound = new Rectangle(compX, stringY, sw, stringHeight);
				comp.setRect(bound);
				this.target.addComponent(comp);

				compX = compX + sw;
				compX = compX + fontSize / 4;
			}
		}

		if (this.target.isMouseOver()) {
			List<ActionComponent> comps = target.getComponents();
			if (comps.size() > 0) {
				Rectangle rc = comps.get(comps.size() - 1).rect;
				rect.width = rc.x + rc.width + gap * 2 - rect.x;
			}
		}

		target.setInited(true);
	}

	private void paintBackGround(Graphics2D g, Target target) {
		int x = target.getRect().x;
		int y = target.getRect().y;
		int w = target.getRect().width;
		int h = target.getRect().height;

		boolean over = target.isMouseOver();
		BeeUIUtils.setAntiAliasing(g, true);

		Color color = this.color;
		Area area;
		if (!over) {
			Rectangle b = target.getIconBound();
			w = b.width + (b.x - x) * 2;

			RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, w * 2, h, h, h);
			Rectangle2D remove = new Rectangle2D.Double(x + w, y, w, h);
			area = new Area(rect);
			area.subtract(new Area(remove));

			g.setColor(color);
			g.fill(area);

		} else {
			RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, w, h, h, h);
			// color = color.darker();
			g.setColor(color);
			g.fill(rect);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		Point p = SwingUtilities.convertPoint(this.sheet.getGraphControl(), e.getPoint(), this.sheet);
		this.mouseMovedOnSheet(p);

		if (!this.handler.isOn()) {

			int modifier = e.getModifiers();
			int c = MouseEvent.CTRL_MASK;
			if ((modifier & c) == 0) {
				if (this.target != null || this.menuTarget != null) {
					this.target = null;
					this.menuTarget = null;
					this.sheet.getGraphControl().repaint();
				}
				return;
			}
		}
		if (this.target != null) {
			Rectangle rect = this.target.getRect();
			if (rect != null) {
				if (rect.contains(e.getPoint())) {
					this.target.setMouseOver(true);
					List<ActionComponent> comps = this.target.getComponents();
					for (ActionComponent a : comps) {
						Rectangle b = a.getRect();
						a.setMouseOver(b.contains(e.getPoint()));
					}
					this.sheet.getGraphControl().repaint(rect);

					return;
				} else {
					List<ActionComponent> comps = this.target.getComponents();
					for (ActionComponent a : comps) {
						a.setMouseOver(false);
					}

					this.target.setMouseOver(false);
					this.sheet.getGraphControl().repaint(rect);
				}
			} else {
				this.target.setMouseOver(false);
			}
		}
		Object obj = this.sheet.getCellAt(e.getX(), e.getY());
		this.target = this.findTarget(obj);

		if (obj instanceof Menuable) {
			Menuable node = (Menuable) obj;
			if (node.getAction() != null) {
				this.makeMenuTargetInfo(node);
				this.sheet.getGraphControl().repaint();
			} else {
				if (this.menuTarget != null) {
					Rectangle rect = this.menuTarget.getRect();
					if (!rect.contains(e.getPoint())) {
						this.menuTarget = null;
						this.sheet.getGraphControl().repaint();
					}
				}
			}
		} else {
			if (this.menuTarget != null) {
				Rectangle rect = this.menuTarget.getRect();
				if (!rect.contains(e.getPoint())) {
					this.menuTarget = null;
					this.sheet.getGraphControl().repaint();
				} else {
					List<ActionComponent> comps = this.menuTarget.getComponents();
					for (ActionComponent a : comps) {
						Rectangle b = a.getRect();
						a.setMouseOver(b.contains(e.getPoint()));
					}
					this.sheet.getGraphControl().repaint(rect);
				}
			}
		}
	}

	private void makeMenuTargetInfo(Menuable cell) {

		if (menuTarget != null) {
			if (menuTarget.getNode().equals(cell)) {

				return;
			}
		}

		mxGraphControl c = sheet.getGraphControl();

		menuTarget = new MenuTarget();
		menuTarget.setNode(cell);
		EditAction action = cell.getAction();
		List<BAction> actions = action.getActions();

		mxCellState state = sheet.getGraph().getView().getState(cell);
		mxRectangle box = state.getBoundingBox();
		int y = (int) (box.getY() + box.getHeight());
		Rectangle rect = new Rectangle((int) box.getX(), y, 0, 0);

		int stringY = y + gap;

		int compHeight = (int) (fontSize * 1.5);

		String current = cell.getCurrent();
		int maxWidth = 0;
		if (current != null) {
			stringY = stringY + compHeight;
			maxWidth = SwingUtilities.computeStringWidth(c.getFontMetrics(c.getFont()), current) + gap;
		}

		int compY = stringY;

		for (BAction a : actions) {

			String name = a.getName();
			int sw = SwingUtilities.computeStringWidth(c.getFontMetrics(c.getFont()), name) + fontSize;

			sw = sw + gap;
			maxWidth = Math.max(maxWidth, sw);

			if (!menuTarget.isInited()) {
				ActionComponent comp = new ActionComponent();
				comp.setAction(a);
				Rectangle bound = new Rectangle((int) (box.getX() + gap), compY + (compHeight - fontSize) / 2, sw, fontSize);
				comp.setRect(bound);
				this.menuTarget.addComponent(comp);
			}

			compY = compHeight + compY;
		}

		rect.width = maxWidth + gap * 2;
		rect.height = compY + gap * 2 - rect.y;
		menuTarget.setRect(rect);
		menuTarget.setInited(true);
	}

	public void afterConctrolPainted(Graphics g) {
		if (menuTarget != null) {
			Rectangle box = menuTarget.getRect();
			g.setColor(color);
			g.fillRect(box.x, box.y, box.width, box.height);

			String c = menuTarget.getNode().getCurrent();
			if (c != null) {
				g.setColor(Color.WHITE);
				g.drawString(c, box.x + gap, box.y + g.getFontMetrics().getAscent() + gap);
			}

			List<ActionComponent> actions = menuTarget.getComponents();
			for (ActionComponent a : actions) {
				String name = a.getAction().getName();
				Rectangle rect = a.getRect();
				if (a.isMouseOver()) {
					g.setColor(Color.GREEN);
				} else {
					g.setColor(Color.WHITE);
				}

				Image img = BeeConstants.METHOD_ICON.getImage();
				// int imgWidth=img.getWidth(this.sheet);
				int imgHeight = img.getHeight(this.sheet);

				g.drawImage(img, rect.x, rect.y + (rect.height - imgHeight) / 2, null);
				g.drawString(name, rect.x + fontSize, rect.y + g.getFontMetrics().getAscent());
			}

		}
	}

	private Target findTarget(Object obj) {
		if (obj != null) {
			if (obj instanceof BNode) {
				BNode node = (BNode) obj;
				if (node.getAction() != null) {
					Target target = new Target();
					target.setNode(node);
					this.sheet.getGraphControl().repaint();
					return target;
				} else {
					return this.findTarget(node.getParent());
				}
			}
		}
		return null;
	}

	public static class MenuTarget {
		private Menuable node;
		private Rectangle rect;
		private List<ActionComponent> component = new ArrayList<ActionComponent>();
		private boolean inited = false;

		public Menuable getNode() {
			return node;
		}

		public void setNode(Menuable node) {
			this.node = node;
		}

		public boolean isInited() {
			return inited;
		}

		public void setInited(boolean inited) {
			this.inited = inited;
		}

		public Rectangle getRect() {
			return rect;
		}

		public void setRect(Rectangle rect) {
			this.rect = rect;
		}

		public void addComponent(ActionComponent comp) {
			boolean contains = false;
			for (ActionComponent a : this.component) {
				if (a.getAction().equals(comp.getAction())) {
					contains = true;
				}
			}
			if (!contains) {
				this.component.add(comp);
			}
		}

		public List<ActionComponent> getComponents() {
			return this.component;
		}
	}

	public static class Target {
		private mxICell node;
		private Rectangle rect;
		private Rectangle iconBound;
		private boolean mouseOver = false;
		private boolean inited = false;
		private List<ActionComponent> component = new ArrayList<ActionComponent>();

		public boolean isInited() {
			return inited;
		}

		public void setInited(boolean inited) {
			this.inited = inited;
		}

		public Rectangle getIconBound() {
			return iconBound;
		}

		public void setIconBound(Rectangle iconBound) {
			this.iconBound = iconBound;
		}

		public mxICell getNode() {
			return node;
		}

		public void setNode(mxICell node) {
			this.node = node;
		}

		public Rectangle getRect() {
			return rect;
		}

		public void setRect(Rectangle rect) {
			this.rect = rect;
		}

		public boolean isMouseOver() {
			return mouseOver;
		}

		public void setMouseOver(boolean mouseOver) {
			this.mouseOver = mouseOver;
		}

		public void addComponent(ActionComponent comp) {
			boolean contains = false;
			for (ActionComponent a : this.component) {
				if (a.getAction().equals(comp.getAction())) {
					contains = true;
				}
			}
			if (!contains) {
				this.component.add(comp);
			}
		}

		public List<ActionComponent> getComponents() {
			return this.component;
		}
	}

	public static class ActionComponent {

		private BAction action;
		private Rectangle rect;
		private boolean mouseOver = false;

		public boolean isMouseOver() {
			return mouseOver;
		}

		public void setMouseOver(boolean mouseOver) {
			this.mouseOver = mouseOver;
		}

		public BAction getAction() {
			return action;
		}

		public void setAction(BAction action) {
			this.action = action;
		}

		public Rectangle getRect() {
			return rect;
		}

		public void setRect(Rectangle rect) {
			this.rect = rect;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point p = SwingUtilities.convertPoint(this.sheet.getGraphControl(), e.getPoint(), this.sheet);
		this.mousePressedOnSheet(p);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.target != null) {
			Rectangle rect = this.target.getRect();
			if (rect != null) {
				if (this.target.isMouseOver()) {
					List<ActionComponent> comps = this.target.getComponents();
					BAction firstAction = null;
					for (ActionComponent a : comps) {
						if (a.isMouseOver()) {
							Rectangle b = a.getRect();
							if (b.contains(e.getPoint())) {
								a.getAction().act();
								firstAction = null;
								this.target = null;
								BLogicLayout.layoutNodes(sheet);

								break;
							}
						} else {
							if (firstAction == null) {
								firstAction = a.getAction();
							}
						}
					}

					// act the first,if icon clicked

					if (firstAction != null) {

						Rectangle ibounds = this.target.getIconBound();
						if (ibounds.contains(e.getPoint())) {
							firstAction.act();
							this.target = null;
							BLogicLayout.layoutNodes(sheet);
						}
					}

				}
			}
		} else if (this.menuTarget != null) {
			Rectangle rect = this.menuTarget.getRect();
			if (rect != null) {

				List<ActionComponent> comps = this.menuTarget.getComponents();

				for (ActionComponent a : comps) {
					if (a.isMouseOver()) {
						Rectangle b = a.getRect();
						if (b.contains(e.getPoint())) {
							a.getAction().act();
							this.menuTarget = null;
							BLogicLayout.layoutNodes(sheet);
							break;
						}
					}
				}

			}

		}
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public void paintHandler(Graphics g) {
		int height = sheet.getHeight();
		int width = sheet.getWidth();
		int right = 0;
		if (sheet.getBorder() != null) {
			right = right + sheet.getBorder().getBorderInsets(sheet).right;
		}

		int scrollWidth = sheet.getVerticalScrollBar().getWidth();

		int fontSize = BeeUIUtils.getDefaultFontSize();
		int handlerWidth = fontSize * 2;
		int x = width - right - scrollWidth - handlerWidth;
		int y = (height - handlerWidth) / 2;

		RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, handlerWidth * 2, handlerWidth, handlerWidth, handlerWidth);
		Rectangle2D remove = new Rectangle2D.Double(x + handlerWidth, y, handlerWidth, handlerWidth);
		Area area = new Area(rect);
		area.subtract(new Area(remove));

		Graphics2D gg = (Graphics2D) g;

		if (handler.isActive() || handler.isOn()) {
			g.setColor(color);
		} else {
			g.setColor(Color.LIGHT_GRAY);
		}

		BeeUIUtils.setAntiAliasing(gg, true);
		gg.fill(area);

		this.handler.setRect(area.getBounds());

		Image img = BeeConstants.ACTION_ICON.getImage();
		gg.drawImage(img, x + fontSize / 2, y + fontSize / 2, fontSize, fontSize, sheet);
	}

	public Handler getHandler() {
		return this.handler;
	}

	public void mouseMovedOnSheet(Point p) {
		if (!handler.isActive()) {
			if (handler.getRect() != null) {
				if (handler.getRect().contains(p)) {
					handler.setActive(true);
					sheet.repaint(handler.getRect());
				}
			}
		} else {
			if (!handler.getRect().contains(p)) {
				if (!handler.isOn()) {
					handler.setActive(false);
					sheet.repaint(handler.getRect());
				}
			}
		}
	}

	public void mousePressedOnSheet(Point p) {
		if (handler == null) {
			return;
		}
		if (handler.getRect() == null) {
			return;
		}
		if (handler.getRect().contains(p)) {
			if (handler.isOn()) {
				handler.setOn(false);
			} else {
				handler.setOn(true);
			}
			sheet.repaint(handler.getRect());
		}
	}

	public static class Handler {
		private Rectangle rect;
		private boolean active = false;
		private boolean on = false;

		public boolean isOn() {
			return on;
		}

		public void setOn(boolean on) {
			this.on = on;
		}

		public Rectangle getRect() {
			return rect;
		}

		public void setRect(Rectangle rect) {
			this.rect = rect;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}
}
