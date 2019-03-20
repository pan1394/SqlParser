package com.linkstec.bee.UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.Enumeration;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.core.Application;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import sun.swing.ImageIconUIResource;

public class BeeUIUtils {
	// to keep the id not to be same
	private static long id = 0;

	public static BasicNode createInnerNode(int width, int height, int offsetX, int offsetY) {
		mxGeometry geo = new mxGeometry(0, 0, width, height);
		geo.setOffset(new mxPoint(offsetX, offsetY));
		geo.setRelative(true);
		BasicNode cell = new BasicNode();
		cell.setRelative();
		cell.setGeometry(geo);
		cell.setVertex(true);
		cell.setConnectable(false);
		return cell;
	}

	public static String createID() {
		id++;
		if (id == 100000) {
			id = 0;
		}
		long time = System.currentTimeMillis();
		return Long.toString(time + id);

	}

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static int getDefaultFontSize() {
		int r = Toolkit.getDefaultToolkit().getScreenResolution();
		int h = r / 9;
		if (h >= 11) {
			return h;
		} else {
			return 11;
		}
		// return r / 9;
		// int screenHeight=screenSize.height;
		// int realHeight=screenHeight/r;
		// return screenSize.height / 70;
	}

	public static Dimension getScreenSize() {
		return screenSize;
	}

	public static String getDefaultFontFamily() {
		return "Meiryo UI";
	}

	public static int getRoundCornerSize() {
		return screenSize.height / 360;
	}

	private static Font font = new Font(getDefaultFontFamily(), Font.PLAIN, getDefaultFontSize());

	public static Font getDefaultFont() {
		return font;
	}

	public static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

	public static int IMAGE_HEIGHT = (int) (BeeUIUtils.getDefaultFontSize());
	public static double IMAGE_SCALE = IMAGE_HEIGHT / 11;
	private static Color borderDown = Color.decode("#E5E5E5");
	private static Color innerUp = Color.decode("#DFDFDF");

	public static void drawRectShadow(Graphics g, int x, int y, int w, int h, int borderWidth) {
		Color color = BeeConstants.TOOLBAR_GREDIENT_DOWN;

		int componentWidth = w;
		int componentHeight = h;
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);

		x = x + borderWidth - 1;
		y = y + borderWidth - 1;
		w = w - borderWidth * 2 + 2;
		h = h - borderWidth * 2 + 2;

		for (int i = 0; i < borderWidth; i++) {
			g.setColor(color);

			g.drawRect(x, y, w, h);
			int f = (255 * (borderWidth - i)) / borderWidth;

			color = BeeUIUtils.brighter(color, borderWidth / 80, f);
			x--;
			y--;
			w = w + 2;
			h = h + 2;
		}

		g.setColor(Color.LIGHT_GRAY);
		Rectangle rect = new Rectangle(x + borderWidth - 1, y + borderWidth - 1, componentWidth - borderWidth * 2 + 2, componentHeight - borderWidth * 2 + 2);
		Area area = new Area(rect);
		g2.draw(area);
	}

	public static Color brighter(Color c, double p, int op) {
		if (c == null) {
			return null;
		}

		double r = c.getRed();
		double g = c.getGreen();
		double b = c.getBlue();

		double rd = 255.0 - r;
		double gd = 255.0 - g;
		double bd = 255.0 - b;

		r += (rd * p) / 100.0;
		g += (gd * p) / 100.0;
		b += (bd * p) / 100.0;
		return new Color((int) r, (int) g, (int) b, op);
	}

	public static void drawString(Graphics g, String text, Color color, Color borderColor, int borderWidth, Rectangle rect, Dimension parentsize) {
		Font f = g.getFont();
		GlyphVector v = f.createGlyphVector(g.getFontMetrics(f).getFontRenderContext(), text);
		Shape shape = v.getOutline();
		Graphics2D gg = (Graphics2D) g;

		gg.translate((parentsize.getWidth() - rect.width) / 2 - rect.x, (parentsize.getHeight() - rect.height) / 2 - rect.y);

		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gg.setColor(color);
		gg.fill(shape);
		gg.setColor(borderColor);
		gg.setStroke(new BasicStroke(borderWidth));
		gg.draw(shape);
	}

	public static void drawCircleButton(Graphics2D g2d, int x, int y, int w, Color color) {
		Color c = Color.LIGHT_GRAY;
		BeeUIUtils.fillTextureRoundRec(g2d, new Color(c.getRed(), c.getGreen(), c.getBlue(), 150), x, y, w, w, w, -w / 2);
		int border = w / 10;
		BeeUIUtils.drawCircle(g2d, x + border, y + border, w - border * 2, color);
	}

	public static void drawCircle(Graphics2D g2d, int x, int y, int h, Color color) {
		Color up = color;
		Color down = borderDown;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		RadialGradientPaint p = new RadialGradientPaint(new Point2D.Double(x + h / 2, y), h / 2 * 3, new float[] { 0.0f, 1f }, new Color[] { new Color(up.getRed(), up.getGreen(), up.getBlue(), 255), new Color(down.getRed(), down.getGreen(), down.getBlue(), 150) });
		g2d.setPaint(p);
		g2d.fillRoundRect(x, y, h, h, h, h);

		down = up;
		up = innerUp;

		int ex = -BeeUIUtils.getDefaultFontSize() / 8;
		x = x - ex;
		y = y - ex;
		h = h + ex * 2;

		p = new RadialGradientPaint(new Point2D.Double(x + h / 3, y), (float) (h * 2), new float[] { 0.06f, 0.22f, 0.8f }, new Color[] { new Color(up.getRed(), up.getGreen(), up.getBlue(), 255), new Color(down.getRed(), down.getGreen(), down.getBlue(), 155), new Color(up.getRed(), up.getGreen(), up.getBlue(), 255) });
		g2d.setPaint(p);
		g2d.fillRoundRect(x, y, h, h, h, h);
	}

	public static ImageIcon getDisabledIcon(ImageIcon icon, Component comp) {
		Image img = ((ImageIcon) icon).getImage();
		int w = img.getWidth(comp);
		int h = img.getHeight(comp);
		double s = BeeUIUtils.IMAGE_SCALE;
		if (s == 0) {
			s = 1;
		}
		int width = (int) (w * s);
		int height = (int) (h * s);
		img = img.getScaledInstance(width, height, Image.SCALE_FAST);
		return new ImageIconUIResource(GrayFilter.createDisabledImage(img));

		// return new ImageIconUIResource(makeImageColorToBlackWhite((BufferedImage)
		// img));
	}

	public static BufferedImage makeImageColorToBlackWhite(BufferedImage bufImg) {
		int[][] result = getImageGRB(bufImg);
		int[] rgb = new int[3];
		BufferedImage bi = new BufferedImage(result.length, result[0].length, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				rgb[0] = (result[i][j] & 0xff0000) >> 16;
				rgb[1] = (result[i][j] & 0xff00) >> 8;
				rgb[2] = (result[i][j] & 0xff);
				int color = (int) (rgb[0] * 0.3 + rgb[1] * 0.59 + rgb[2] * 0.11);
				color = color > 128 ? 255 : 0;
				bi.setRGB(i, j, (color << 16) | (color << 8) | color);
			}
		}
		return bi;
	}

	public static int[][] getImageGRB(BufferedImage bufImg) {

		int height = bufImg.getHeight(Application.FRAME);
		int width = bufImg.getWidth(Application.FRAME);
		int[][] result = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				result[i][j] = bufImg.getRGB(i, j) & 0xFFFFFF;

			}
		}
		return result;
	}

	public static class Handler implements MouseMotionListener, MouseListener {
		private JComponent handler;
		private Container win;
		private int handlerSize = BeeUIUtils.getDefaultFontSize();
		private int currentx = 0;
		private int currnety = 0;
		private JComponent moveHandler;
		private boolean dragging = false;
		private Cursor current = new Cursor(Cursor.DEFAULT_CURSOR);

		public Handler(JComponent handler, Container win, int borderSize, JComponent moveHandler) {
			handlerSize = borderSize;
			this.moveHandler = moveHandler;
			this.handler = handler;
			this.win = win;
			this.handler.addMouseListener(this);
			this.handler.addMouseMotionListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.currentx = e.getX();
			this.currnety = e.getY();
			this.mouseMoved(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			dragging = false;
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!this.dragging) {
				handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (this.moveHandler.isVisible() && this.moveHandler.getParent() != null) {
				dragging = true;
				int x = e.getX();
				int y = e.getY();
				Dimension p = win.getSize();
				Point location = win.getLocation();
				Cursor c = this.current;
				if (c.getType() == Cursor.NW_RESIZE_CURSOR) {
					win.setBounds(location.x + x - this.currentx, location.y + y - this.currnety, (int) p.getWidth() - x + this.currentx, (int) (p.getHeight() - y + this.currnety));
					this.currentx = 0;
					this.currnety = 0;
				} else if (c.getType() == Cursor.NE_RESIZE_CURSOR) {
					win.setLocation(location.x, location.y + y - this.currnety);
					win.setSize((int) p.getWidth() + x - this.currentx, (int) (p.getHeight() - y + this.currnety));
					this.currentx = x;
					this.currnety = 0;
				} else if (c.getType() == Cursor.N_RESIZE_CURSOR) {
					win.setLocation(location.x, location.y + y - this.currnety);
					win.setSize((int) p.getWidth(), (int) (p.getHeight() - y + this.currnety));
					this.currentx = x;
					this.currnety = 0;
				} else if (c.getType() == Cursor.E_RESIZE_CURSOR) {
					win.setSize((int) p.getWidth() + x - this.currentx, (int) (p.getHeight()));
					this.currentx = x;
					this.currnety = y;
				} else if (c.getType() == Cursor.S_RESIZE_CURSOR) {
					win.setSize((int) p.getWidth(), (int) (p.getHeight() + y - this.currnety));
					this.currentx = x;
					this.currnety = y;
				} else if (c.getType() == Cursor.W_RESIZE_CURSOR) {
					win.setLocation(location.x + x - this.currentx, location.y);
					win.setSize((int) p.getWidth() - x + this.currentx, (int) (p.getHeight()));
					this.currentx = 0;
					this.currnety = y;
				} else if (c.getType() == Cursor.SW_RESIZE_CURSOR) {
					win.setLocation(location.x + x - this.currentx, location.y);
					win.setSize((int) p.getWidth() - x + this.currentx, (int) (p.getHeight() + y - this.currnety));
					this.currentx = 0;
					this.currnety = y;
				} else if (c.getType() == Cursor.MOVE_CURSOR) {
					win.setLocation(location.x + x - this.currentx, location.y + y - this.currnety);
				} else if (c.getType() == Cursor.SE_RESIZE_CURSOR) {
					win.setSize((int) p.getWidth() + x - this.currentx, (int) (p.getHeight() + y - this.currnety));
					this.currentx = x;
					this.currnety = y;
				}

			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (this.moveHandler.isVisible() && this.moveHandler.getParent() != null) {
				int x = e.getX();
				int y = e.getY();

				int width = handler.getWidth();
				int height = handler.getHeight();

				if (x < handlerSize && y < handlerSize) {
					handler.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
				} else if (x < handlerSize && y > height - handlerSize) {
					handler.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
				} else if (x > width - handlerSize && y > height - handlerSize) {
					handler.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				} else if (x > width - handlerSize && y < handlerSize) {
					handler.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
				} else if (x > width - handlerSize) {
					handler.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				} else if (x < handlerSize) {
					handler.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				} else if (y < handlerSize) {
					handler.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				} else if (y > height - handlerSize) {
					handler.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
				} else {
					handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				if (this.moveHandler.getBounds().contains(x, y)) {
					handler.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
			} else {
				handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			this.current = handler.getCursor();
		}

	}

	public static void fillTextureHorizonalRoundRec(Graphics2D g2, Color baseColor, int x, int y, int w, int h, int arc, int colorDelta) {
		setAntiAliasing(g2, true);
		Paint oldpaint = g2.getPaint();
		GradientPaint gp = new GradientPaint(x, y, getColor(baseColor, colorDelta, colorDelta, colorDelta), x + w, y, baseColor);

		g2.setPaint(gp);
		g2.fillRoundRect(x, y, w, h, arc, arc);
		g2.setPaint(oldpaint);
		setAntiAliasing(g2, false);
	}

	public static void fillTextureRoundRec(Graphics2D g2, Color baseColor, int x, int y, int w, int h, int arc, int colorDelta) {
		setAntiAliasing(g2, true);
		Paint oldpaint = g2.getPaint();
		GradientPaint gp = new GradientPaint(x, y, getColor(baseColor, colorDelta, colorDelta, colorDelta), x, y + h, baseColor);
		g2.setPaint(gp);
		g2.fillRoundRect(x, y, w, h, arc, arc);
		g2.setPaint(oldpaint);
		setAntiAliasing(g2, false);
	}

	public static void setAntiAliasing(Graphics2D g2, boolean antiAliasing) {
		if (antiAliasing)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public static Color getColor(Color basic, int r, int g, int b) {
		return new Color(getColorInt(basic.getRed() + r), getColorInt(basic.getGreen() + g), getColorInt(basic.getBlue() + b), getColorInt(basic.getAlpha()));
	}

	public static Color getColor(Color basic, int r, int g, int b, int a) {
		return new Color(getColorInt(basic.getRed() + r), getColorInt(basic.getGreen() + g), getColorInt(basic.getBlue() + b), getColorInt(basic.getAlpha() + a));
	}

	public static int getColorInt(int rgb) {
		return rgb < 0 ? 0 : (rgb > 255 ? 255 : rgb);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(1000, 1000));

		frame.getContentPane().setLayout(new FlowLayout());

		JButton button = new JButton() {

			@Override
			public void paint(Graphics g) {

				int w = this.getWidth();

				BeeUIUtils.drawCircleButton((Graphics2D) g, 0, 0, w, Color.GRAY);

			}

		};

		button.setPreferredSize(new Dimension(200, 200));
		button.setOpaque(false);

		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				String s = "testaaaaaaaaaaaaaaattttttttgggggttttttttttttt";
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Font plainFont = new Font("Times New Roman", Font.PLAIN, 80);

				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, plainFont);

				// as.addAttribute(new TextAttribute("Underline-Color"), Color.red);
				as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED, 1, 8);
				as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 18, 25);

				g2.drawString(as.getIterator(), 24, 70);

			}
		};
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				button.repaint();
				panel.repaint();
			}

		});
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(800, 200));
		frame.getContentPane().add(panel);
		frame.getContentPane().add(button);
		frame.setVisible(true);
	}

}
