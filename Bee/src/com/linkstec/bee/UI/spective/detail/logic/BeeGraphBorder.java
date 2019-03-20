package com.linkstec.bee.UI.spective.detail.logic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.mxgraph.util.mxRectangle;

public class BeeGraphBorder implements Border, MouseMotionListener {
	private int width = BeeUIUtils.getDefaultFontSize() * 2 / 3;
	private Insets insets = new Insets(0, width, 0, 0);

	private BeeGraphSheet sheet;
	private static BufferedImage textureImage = null;
	private static Color selectMarkBoderColor = Color.decode("#7F9FFF");
	private Rectangle2D textureRect = new Rectangle(0, 0, 3, 3);
	private List<BasicNode> errors = new ArrayList<BasicNode>();
	private boolean linstenerAdd = false;

	public BeeGraphBorder(BeeGraphSheet sheet) {
		this.sheet = sheet;

		if (textureImage == null) {
			try {
				textureImage = ImageIO.read(BeeGraphBorder.class.getResource("/com/linkstec/bee/UI/images/blue_dot.gif"));
			} catch (IOException e) {

			}
		}
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if (!this.linstenerAdd) {
			c.addMouseMotionListener(this);
			linstenerAdd = true;
		}
		g.setColor(BeeConstants.BORDER_BACKCOLOR);
		g.fillRect(x, y, this.width, height);
		Object[] objs = sheet.getGraph().getSelectionCells();

		for (Object obj : objs) {

			if (obj != null && obj instanceof BasicNode) {

				BasicNode node = (BasicNode) obj;
				mxRectangle rect = sheet.getGraph().getBoundingBox(node);
				Graphics2D gg = (Graphics2D) g;
				TexturePaint tPaint = new TexturePaint(textureImage, textureRect);
				gg.setPaint(tPaint);

				if (rect != null) {

					int ry = (int) rect.getY();
					int rh = (int) rect.getHeight();
					gg.fillRect(x, ry, this.width, rh);
					g.setColor(selectMarkBoderColor);
					g.drawLine(x, ry, this.width, ry);
					g.drawLine(x, ry + rh, this.width, ry + rh);
				}
			}
		}
		for (BasicNode node : errors) {
			mxRectangle rect = sheet.getGraph().getBoundingBox(node);
			if (rect != null) {
				ImageIcon icon = node.getIcon();
				if (icon != null) {
					g.drawImage(icon.getImage(), x, (int) rect.getY() + 10, this.width, this.width, c);
				}
				BAlert alert = node.getAlertObject();
				if (alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR)) {
					g.drawImage(BeeConstants.ERROR_ICON.getImage(), x + this.width / 3, (int) rect.getY() + 17, this.width * 3 / 4, this.width * 3 / 4, c);
				} else {
					g.drawImage(BeeConstants.ALERT_ICON.getImage(), x + this.width / 3, (int) rect.getY() + 17, this.width * 3 / 4, this.width * 3 / 4, c);
				}
			}
		}
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	public int getWidth() {
		return width;
	}

	public void addError(BasicNode node) {
		if (!this.errors.contains(node)) {
			this.errors.add(node);
		}
	}

	public void removeError(BasicNode node) {
		this.errors.remove(node);
	}

	public void clearErrors() {
		this.errors.clear();
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		if (x > this.width) {
			return;
		}
		for (BasicNode node : errors) {
			mxRectangle rect = sheet.getGraph().getBoundingBox(node);
			if (rect != null) {
				Rectangle r = new Rectangle(0, (int) rect.getY() + 14, this.width, this.width);

				if (r.contains(e.getX(), e.getY())) {
					sheet.getTooltip().showImmediatelyWithLocation(node, this.width, (int) r.getY(), sheet);
					break;
				}
			}
		}
	}
}
