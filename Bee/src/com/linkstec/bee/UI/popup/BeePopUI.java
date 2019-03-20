package com.linkstec.bee.UI.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.border.DialogBorder;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraphBorder;
import com.linkstec.bee.core.Application;

public class BeePopUI extends JDialog implements FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2786171079379652760L;

	private static boolean sticking = false;

	protected int borderWidth = BeeUIUtils.getDefaultFontSize() / 6;

	private DialogBorder border = new DialogBorder(BeeUIUtils.getDefaultFontSize() / 2);

	private DialogBorder stickBorder = new DialogBorder(BeeUIUtils.getDefaultFontSize() / 3);

	public JScrollPane scroll;

	private static BufferedImage textureImage = null;
	static {

		if (textureImage == null) {
			try {
				textureImage = ImageIO
						.read(BeeGraphBorder.class.getResource("/com/linkstec/bee/UI/images/texture_control.gif"));
			} catch (IOException e) {

			}
		}
	}

	private JLabel handler = new JLabel("") {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6528299546584298004L;

		@Override
		public void paint(Graphics g) {

			Graphics2D gg = (Graphics2D) g;
			Rectangle2D textureRect = new Rectangle(0, 0, BeeUIUtils.getDefaultFontSize() / 8,
					BeeUIUtils.getDefaultFontSize() / 8);
			TexturePaint tPaint = new TexturePaint(textureImage, textureRect);
			gg.setPaint(tPaint);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

	};

	private JPanel container = new JPanel();

	public static Color BACK_COLOR = Color.decode("#FFFFCC");

	protected int handlerHeight = (int) (BeeUIUtils.getDefaultFontSize() * 1.5);

	protected MouseAdapter adapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			stick();
		}

	};

	protected MouseAdapter entered = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			stick();
		}

	};

	protected AncestorListener ancstorListener = new AncestorListener() {

		@Override
		public void ancestorAdded(AncestorEvent event) {
			Object obj = event.getSource();
			if (obj instanceof JComponent) {
				JComponent comp = (JComponent) obj;
				comp.addMouseListener(adapter);
			}

		}

		@Override
		public void ancestorRemoved(AncestorEvent event) {

		}

		@Override
		public void ancestorMoved(AncestorEvent event) {

		}

	};

	protected JPanel inner = new JPanel();

	public BeePopUI(JComponent com) {
		// super(com == null ? Application.FRAME : (com.getTopLevelAncestor() == null ?
		// Application.FRAME : (com.getTopLevelAncestor() instanceof JFrame) ? ((JFrame)
		// com.getTopLevelAncestor()) : (com.getTopLevelAncestor() instanceof JDialog) ?
		// ((JDialog) com.getTopLevelAncestor()) : ((JWindow)
		// com.getTopLevelAncestor())));
		super(FocusManager.getCurrentManager().getActiveWindow());
		this.getContentPane().setLayout(new BorderLayout());
		this.setFocusable(true);
		this.addFocusListener(this);

		handler.setPreferredSize(new Dimension(0, handlerHeight));
		handler.setForeground(Color.GRAY);
		handler.setBorder(new EmptyBorder(0, BeeUIUtils.getDefaultFontSize() / 2, 0, 0));

		this.unstick();

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		inner.setLayout(flow);
		inner.addMouseListener(adapter);
		inner.addMouseListener(entered);
		inner.setBackground(BACK_COLOR);

		scroll = new JScrollPane(inner);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUnitIncrement(50);

		container.setLayout(new BorderLayout());
		container.setOpaque(false);
		this.getContentPane().add(container, BorderLayout.CENTER);
		// this.add(container, BorderLayout.CENTER);
		container.add(scroll, BorderLayout.CENTER);

		new BeeUIUtils.Handler(container, this, borderWidth, handler);

		inner.addAncestorListener(ancstorListener);
	}

	protected JFrame getFrameOwner() {
		return Application.FRAME;
	}

	protected JWindow getWindowOwner() {
		return null;
	}

	public JPanel getTopContainer() {
		return this.container;
	}

	public void stick() {
		if (!sticking) {
			this.container.setBorder(this.stickBorder);
			this.setSize(new Dimension((int) (this.getSize().getWidth()),
					(int) (this.getHeight() + this.handler.getHeight())));
			this.container.add(this.handler, BorderLayout.SOUTH);
			this.container.updateUI();
			sticking = true;

			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
	}

	public Container getContainer() {
		return this.container;
	}

	public void unstick() {
		this.container.setBorder(this.border);
		if (this.handler.getParent() != null) {
			this.container.remove(this.handler);
			this.setSize(new Dimension((int) (this.getSize().getWidth()),
					(int) (this.getSize().getHeight() - this.handler.getSize().getHeight())));
			this.container.updateUI();
		}
		sticking = false;
		if (scroll != null) {
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
	}

	public static boolean isSticking() {
		return sticking;
	}

	private int shadow = BeeUIUtils.getDefaultFontSize() / 10;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// g.setColor(Color.RED);
		// g.fillRect(3, this.getHeight() - shadow, this.getWidth(), shadow);
		// g.drawLine(this.getWidth() - shadow, 3, this.getWidth() - shadow,
		// this.getHeight());
	}

	@Override
	public void focusGained(FocusEvent e) {
		// this.stick();

	}

	@Override
	public void focusLost(FocusEvent e) {
		this.unstick();

	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

}
