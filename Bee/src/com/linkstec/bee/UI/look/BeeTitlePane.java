package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.border.RootPaneBorder;
import com.linkstec.bee.core.Application;

import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

public class BeeTitlePane extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6695962433649490296L;
	private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
	private static final int IMAGE_HEIGHT = BeeUIUtils.getDefaultFontSize() / 11 * 16;
	private static final int IMAGE_WIDTH = BeeUIUtils.getDefaultFontSize() / 11 * 16;

	/**
	 * PropertyChangeListener added to the JRootPane.
	 */
	private PropertyChangeListener propertyChangeListener;

	/**
	 * JMenuBar, typically renders the system menu items.
	 */
	private JMenuBar menuBar;
	/**
	 * Action used to close the Window.
	 */
	private Action closeAction;

	/**
	 * Action used to iconify the Frame.
	 */
	private Action iconifyAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action restoreAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action maximizeAction;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton toggleButton;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton iconifyButton;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton closeButton;

	/**
	 * Icon used for toggleButton when window is normal size.
	 */
	private Icon maximizeIcon;

	/**
	 * Icon used for toggleButton when window is maximized.
	 */
	private Icon minimizeIcon;

	/**
	 * Image used for the system menu icon
	 */
	private Image systemIcon;

	/**
	 * Listens for changes in the state of the Window listener to update the state
	 * of the widgets.
	 */
	private WindowListener windowListener;

	/**
	 * Window we're currently in.
	 */
	private Window window;

	/**
	 * JRootPane rendering for.
	 */
	private JRootPane rootPane;

	/**
	 * Room remaining in title for bumps.
	 */
	private int buttonsWidth;

	/**
	 * Buffered Frame.state property. As state isn't bound, this is kept to
	 * determine when to avoid updating widgets.
	 */
	private int state;

	/**
	 * MetalRootPaneUI that created us.
	 */
	private BeeRootPaneUI rootPaneUI;

	// Colors
	private Color inactiveBackground = UIManager.getColor("inactiveCaption");
	private Color inactiveForeground = UIManager.getColor("inactiveCaptionText");
	private Color inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
	private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
	private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
	private Color activeBackground = null;
	private Color activeForeground = null;
	private Color activeShadow = null;

	public BeeTitlePane(JRootPane root, BeeRootPaneUI ui) {
		this.rootPane = root;
		this.setFont(BeeUIUtils.getDefaultFont());
		this.setOpaque(false);
		rootPaneUI = ui;

		state = -1;

		if (getWindowDecorationStyle() != JRootPane.FRAME) {
			root.setOpaque(false);
			root.setBorder(new RootPaneBorder(Color.RED));
		}

		installSubcomponents();
		determineColors();
		installDefaults();

		setLayout(createLayout());
	}

	/**
	 * Uninstalls the necessary state.
	 */
	private void uninstall() {
		uninstallListeners();
		window = null;
		removeAll();
	}

	/**
	 * Installs the necessary listeners.
	 */
	private void installListeners() {
		if (window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * Uninstalls the necessary listeners.
	 */
	private void uninstallListeners() {
		if (window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * Returns the <code>WindowListener</code> to add to the <code>Window</code>.
	 */
	private WindowListener createWindowListener() {
		return new WindowHandler();
	}

	/**
	 * Returns the <code>PropertyChangeListener</code> to install on the
	 * <code>Window</code>.
	 */
	private PropertyChangeListener createWindowPropertyChangeListener() {
		return new PropertyChangeHandler();
	}

	/**
	 * Returns the <code>JRootPane</code> this was created for.
	 */
	public JRootPane getRootPane() {
		return rootPane;
	}

	/**
	 * Returns the decoration style of the <code>JRootPane</code>.
	 */
	private int getWindowDecorationStyle() {
		return getRootPane().getWindowDecorationStyle();
	}

	public void addNotify() {
		super.addNotify();

		uninstallListeners();

		window = SwingUtilities.getWindowAncestor(this);
		if (window != null) {
			if (window instanceof Frame) {
				setState(((Frame) window).getExtendedState());
			} else {
				setState(0);
			}
			setActive(window.isActive());
			installListeners();
			updateSystemIcon();
		}
	}

	public void removeNotify() {
		super.removeNotify();

		uninstallListeners();
		window = null;
	}

	/**
	 * Adds any sub-Components contained in the <code>MetalTitlePane</code>.
	 */
	private void installSubcomponents() {
		int decorationStyle = getWindowDecorationStyle();
		if (decorationStyle == JRootPane.FRAME) {
			createActions();
			menuBar = createMenuBar();
			add(menuBar);
			createButtons();
			add(iconifyButton);
			add(toggleButton);
			add(closeButton);
		} else if (decorationStyle == JRootPane.PLAIN_DIALOG || decorationStyle == JRootPane.INFORMATION_DIALOG
				|| decorationStyle == JRootPane.ERROR_DIALOG || decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG
				|| decorationStyle == JRootPane.FILE_CHOOSER_DIALOG || decorationStyle == JRootPane.QUESTION_DIALOG
				|| decorationStyle == JRootPane.WARNING_DIALOG) {
			createActions();
			createButtons();
			add(closeButton);
		}
	}

	/**
	 * Determines the Colors to draw with.
	 */
	private void determineColors() {
		switch (getWindowDecorationStyle()) {
		case JRootPane.FRAME:
			activeBackground = UIManager.getColor("activeCaption");
			activeForeground = UIManager.getColor("activeCaptionText");
			activeShadow = UIManager.getColor("activeCaptionBorder");
			break;
		case JRootPane.ERROR_DIALOG:
			activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
			activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
			activeShadow = UIManager.getColor("OptionPane.errorDialog.titlePane.shadow");
			break;
		case JRootPane.QUESTION_DIALOG:
		case JRootPane.COLOR_CHOOSER_DIALOG:
		case JRootPane.FILE_CHOOSER_DIALOG:
			activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
			activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
			activeShadow = UIManager.getColor("OptionPane.questionDialog.titlePane.shadow");
			break;
		case JRootPane.WARNING_DIALOG:
			activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
			activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
			activeShadow = UIManager.getColor("OptionPane.warningDialog.titlePane.shadow");
			break;
		case JRootPane.PLAIN_DIALOG:
		case JRootPane.INFORMATION_DIALOG:
		default:
			activeBackground = UIManager.getColor("activeCaption");
			activeForeground = UIManager.getColor("activeCaptionText");
			activeShadow = UIManager.getColor("activeCaptionBorder");
			break;
		}
	}

	/**
	 * Installs the fonts and necessary properties on the MetalTitlePane.
	 */
	private void installDefaults() {
		setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
	}

	/**
	 * Uninstalls any previously installed UI values.
	 */
	private void uninstallDefaults() {
	}

	/**
	 * Returns the <code>JMenuBar</code> displaying the appropriate system menu
	 * items.
	 */
	protected JMenuBar createMenuBar() {
		menuBar = new SystemMenuBar();
		menuBar.setFocusable(false);
		menuBar.setBorderPainted(true);
		menuBar.add(createMenu());
		return menuBar;
	}

	/**
	 * Closes the Window.
	 */
	private void close() {
		Window window = getWindow();

		if (window != null) {
			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * Iconifies the Frame.
	 */
	private void iconify() {
		Frame frame = getFrame();
		if (frame != null) {
			frame.setExtendedState(state | Frame.ICONIFIED);
		}
	}

	/**
	 * Maximizes the Frame.
	 */
	private void maximize() {
		Frame frame = getFrame();
		if (frame != null) {
			frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
		}
	}

	/**
	 * Restores the Frame size.
	 */
	private void restore() {
		Frame frame = getFrame();

		if (frame == null) {
			return;
		}

		if ((state & Frame.ICONIFIED) != 0) {
			frame.setExtendedState(state & ~Frame.ICONIFIED);
		} else {
			frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
		}
	}

	/**
	 * Create the <code>Action</code>s that get associated with the buttons and menu
	 * items.
	 */
	private void createActions() {
		closeAction = new CloseAction();
		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			iconifyAction = new IconifyAction();
			restoreAction = new RestoreAction();
			maximizeAction = new MaximizeAction();
		}
	}

	/**
	 * Returns the <code>JMenu</code> displaying the appropriate menu items for
	 * manipulating the Frame.
	 */
	private JMenu createMenu() {
		JMenu menu = new JMenu("");
		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			addMenuItems(menu);
		}
		return menu;
	}

	/**
	 * Adds the necessary <code>JMenuItem</code>s to the passed in menu.
	 */
	private void addMenuItems(JMenu menu) {
		Locale locale = getRootPane().getLocale();
		JMenuItem mi = menu.add(restoreAction);
		int mnemonic = -1;// MetalUtils.getInt("MetalTitlePane.restoreMnemonic", -1);

		if (mnemonic != -1) {
			mi.setMnemonic(mnemonic);
		}

		mi = menu.add(iconifyAction);
		// mnemonic = MetalUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
		if (mnemonic != -1) {
			mi.setMnemonic(mnemonic);

		}

		if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
			mi = menu.add(maximizeAction);

			// mnemonic = MetalUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}
		}

		menu.add(new JSeparator());

		mi = menu.add(closeAction);
		mi.setBackground(Color.RED);
		// mnemonic = MetalUtils.getInt("MetalTitlePane.closeMnemonic", -1);
		if (mnemonic != -1) {
			mi.setMnemonic(mnemonic);
		}
	}

	/**
	 * Returns a <code>JButton</code> appropriate for placement on the TitlePane.
	 */
	private JButton createTitleButton() {
		JButton button = new JButton() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2633480666934739777L;

			@Override
			public void paint(Graphics g) {

				int w = this.getWidth();
				int h = this.getHeight() * 4 / 5;
				int y = this.getHeight() / 7;
				int x = (w - h) / 2;

				Graphics2D g2d = (Graphics2D) g;

				Point point = this.getMousePosition();

				Color up = BeeConstants.BORDER_BACKCOLOR;
				if (point != null) {
					Action action = this.getAction();
					if (action != null && action.equals(closeAction)) {
						up = Color.RED;
					} else if (action != null && action.equals(iconifyAction)) {
						up = Color.ORANGE;
					} else {
						up = Color.GREEN.darker().darker();
					}
				}
				BeeUIUtils.drawCircleButton(g2d, x, y, h, up);
			}

		};

		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setOpaque(false);
		return button;
	}

	/**
	 * Creates the Buttons that will be placed on the TitlePane.
	 */
	private void createButtons() {
		closeButton = createTitleButton();
		closeButton.setAction(closeAction);
		closeButton.setText(null);
		closeButton.putClientProperty("paintActive", Boolean.TRUE);
		closeButton.setToolTipText("閉じる");
		closeButton.setBorder(handyEmptyBorder);
		closeButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, "Close");
		closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));

		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
			minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");

			iconifyButton = createTitleButton();
			iconifyButton.setAction(iconifyAction);
			iconifyButton.setText(null);
			iconifyButton.setToolTipText("最小化");
			iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
			iconifyButton.setBorder(handyEmptyBorder);
			iconifyButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, "Iconify");
			iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));

			toggleButton = createTitleButton();
			toggleButton.setAction(restoreAction);
			toggleButton.putClientProperty("paintActive", Boolean.TRUE);
			toggleButton.setToolTipText("元に戻す");
			toggleButton.setBorder(handyEmptyBorder);
			toggleButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, "Maximize");
			toggleButton.setIcon(maximizeIcon);
		}
	}

	/**
	 * Returns the <code>LayoutManager</code> that should be installed on the
	 * <code>MetalTitlePane</code>.
	 */
	private LayoutManager createLayout() {
		return new TitlePaneLayout();
	}

	/**
	 * Updates state dependant upon the Window's active state.
	 */
	private void setActive(boolean isActive) {
		Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

		closeButton.putClientProperty("paintActive", activeB);
		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			iconifyButton.putClientProperty("paintActive", activeB);
			toggleButton.putClientProperty("paintActive", activeB);
		}
		// Repaint the whole thing as the Borders that are used have
		// different colors for active vs inactive
		getRootPane().repaint();
	}

	/**
	 * Sets the state of the Window.
	 */
	private void setState(int state) {
		setState(state, false);
	}

	/**
	 * Sets the state of the window. If <code>updateRegardless</code> is true and
	 * the state has not changed, this will update anyway.
	 */
	private void setState(int state, boolean updateRegardless) {
		Window w = getWindow();

		if (w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
			if (this.state == state && !updateRegardless) {
				return;
			}
			Frame frame = getFrame();

			if (frame != null) {
				JRootPane rootPane = getRootPane();

				if (((state & Frame.MAXIMIZED_BOTH) != 0)
						&& (rootPane.getBorder() == null || (rootPane.getBorder() instanceof UIResource))
						&& frame.isShowing()) {
					rootPane.setBorder(null);
				} else if ((state & Frame.MAXIMIZED_BOTH) == 0) {
					// This is a croak, if state becomes bound, this can
					// be nuked.
					rootPaneUI.installBorder(rootPane);
				}
				if (frame.isResizable()) {
					if ((state & Frame.MAXIMIZED_BOTH) != 0) {
						updateToggleButton(restoreAction, minimizeIcon);
						maximizeAction.setEnabled(false);
						restoreAction.setEnabled(true);
					} else {
						updateToggleButton(maximizeAction, maximizeIcon);
						maximizeAction.setEnabled(true);
						restoreAction.setEnabled(false);
					}
					if (toggleButton.getParent() == null || iconifyButton.getParent() == null) {
						add(toggleButton);
						add(iconifyButton);
						revalidate();
						repaint();
					}
					toggleButton.setText(null);
				} else {
					maximizeAction.setEnabled(false);
					restoreAction.setEnabled(false);
					if (toggleButton.getParent() != null) {
						remove(toggleButton);
						revalidate();
						repaint();
					}
				}
			} else {
				// Not contained in a Frame
				maximizeAction.setEnabled(false);
				restoreAction.setEnabled(false);
				iconifyAction.setEnabled(false);
				remove(toggleButton);
				remove(iconifyButton);
				revalidate();
				repaint();
			}
			closeAction.setEnabled(true);
			this.state = state;
		}
	}

	/**
	 * Updates the toggle button to contain the Icon <code>icon</code>, and Action
	 * <code>action</code>.
	 */
	private void updateToggleButton(Action action, Icon icon) {
		toggleButton.setAction(action);
		toggleButton.setIcon(icon);
		toggleButton.setText(null);
	}

	/**
	 * Returns the Frame rendering in. This will return null if the
	 * <code>JRootPane</code> is not contained in a <code>Frame</code>.
	 */
	private Frame getFrame() {
		Window window = getWindow();

		if (window instanceof Frame) {
			return (Frame) window;
		}
		return null;
	}

	/**
	 * Returns the <code>Window</code> the <code>JRootPane</code> is contained in.
	 * This will return null if there is no parent ancestor of the
	 * <code>JRootPane</code>.
	 */
	private Window getWindow() {
		return window;
	}

	/**
	 * Returns the String to display as the title.
	 */
	private String getTitle() {
		Window w = getWindow();

		if (w instanceof Frame) {
			return ((Frame) w).getTitle();
		} else if (w instanceof Dialog) {
			return ((Dialog) w).getTitle();
		}
		return null;
	}

	/**
	 * Renders the TitlePane.
	 */
	public void paintComponent(Graphics g) {
		// As state isn't bound, we need a convenience place to check
		// if it has changed. Changing the state typically changes the
		if (getFrame() != null) {
			setState(getFrame().getExtendedState());
		}
		JRootPane rootPane = getRootPane();
		Window window = getWindow();
		boolean leftToRight = (window == null) ? rootPane.getComponentOrientation().isLeftToRight()
				: window.getComponentOrientation().isLeftToRight();
		boolean isSelected = (window == null) ? true : window.isActive();
		int width = getWidth();
		int height = getHeight();

		Color background;
		Color foreground;

		if (isSelected) {
			background = activeBackground;
			foreground = activeForeground;

		} else {
			background = inactiveBackground;
			foreground = inactiveForeground;

		}

		g.setColor(background);

		if (getWindowDecorationStyle() == JRootPane.FRAME) {

			Color up = Color.decode("#FDFEFF");
			Color down = Color.decode("#F7F9FD");
			Graphics2D g2d = (Graphics2D) g;
			GradientPaint grdp = new GradientPaint(0, 0, up, 0, height, down);
			g2d.setPaint(grdp);
			g2d.fillRect(0, 0, width, height);
		} else {
			// Color up = Color.decode("#ADC1EB").darker();

			Graphics2D g2d = (Graphics2D) g;

			Paint old = g2d.getPaint();

			Color upcolor = BeeConstants.TOOLBAR_GREDIENT_UP;
			Color color = BeeConstants.TOOLBAR_GREDIENT_DOWN;

			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.addRenderingHints(rh);

			GradientPaint gp = new GradientPaint(0, 0, upcolor, 0, height, color);

			g2d.setPaint(gp);

			g2d.fillRoundRect(1, 1, width, height * 2, height / 2, height / 2);

			g2d.setPaint(old);

			if (Application.FRAME != null) {
				Image img = Application.FRAME.getIconImage();
				if (img != null) {
					g.drawImage(img, img.getWidth(this) / 3, img.getHeight(this) / 5, IMAGE_WIDTH, IMAGE_HEIGHT, this);
				}
			}
		}

		int xOffset = leftToRight ? 5 : width - 5;

		// if (getWindowDecorationStyle() == JRootPane.FRAME) {
		xOffset += leftToRight ? IMAGE_WIDTH + 5 : -IMAGE_WIDTH - 5;
		// }

		String theTitle = getTitle();
		if (theTitle != null) {
			FontMetrics fm = SwingUtilities2.getFontMetrics(rootPane, g);

			g.setColor(foreground);

			int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

			Rectangle rect = new Rectangle(0, 0, 0, 0);
			if (iconifyButton != null && iconifyButton.getParent() != null) {
				rect = iconifyButton.getBounds();
			}
			int titleW;

			if (leftToRight) {
				if (rect.x == 0) {
					rect.x = window.getWidth() - window.getInsets().right - 2;
				}
				titleW = rect.x - xOffset - 4;
				theTitle = SwingUtilities2.clipStringIfNecessary(rootPane, fm, theTitle, titleW);
			} else {
				titleW = xOffset - rect.x - rect.width - 4;
				theTitle = SwingUtilities2.clipStringIfNecessary(rootPane, fm, theTitle, titleW);
				xOffset -= SwingUtilities2.stringWidth(rootPane, fm, theTitle);
			}
			if (getWindowDecorationStyle() != JRootPane.FRAME) {
				// g.setColor(Color.WHITE);
			}
			g.drawString(theTitle, xOffset, yOffset);
		}
	}

	/**
	 * Actions used to <code>close</code> the <code>Window</code>.
	 */
	private class CloseAction extends AbstractAction {
		public CloseAction() {
			super(UIManager.getString("MetalTitlePane.closeTitle", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			close();
		}
	}

	/**
	 * Actions used to <code>iconfiy</code> the <code>Frame</code>.
	 */
	private class IconifyAction extends AbstractAction {
		public IconifyAction() {
			super(UIManager.getString("MetalTitlePane.iconifyTitle", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			iconify();
		}
	}

	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class RestoreAction extends AbstractAction {
		public RestoreAction() {
			super(UIManager.getString("MetalTitlePane.restoreTitle", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			restore();
		}
	}

	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class MaximizeAction extends AbstractAction {
		public MaximizeAction() {
			super(UIManager.getString("MetalTitlePane.maximizeTitle", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			maximize();
		}
	}

	/**
	 * Class responsible for drawing the system menu. Looks up the image to draw
	 * from the Frame associated with the <code>JRootPane</code>.
	 */
	private class SystemMenuBar extends JMenuBar {
		public void paint(Graphics g) {
			if (isOpaque()) {
				g.setColor(getBackground());
				// g.fillRect(0, 0, getWidth(), getHeight());
			}

			if (systemIcon != null) {
				g.drawImage(systemIcon, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
			} else {
				Icon icon = UIManager.getIcon("InternalFrame.icon");

				if (icon != null) {
					icon.paintIcon(this, g, 0, 0);
				}
			}
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();

			return new Dimension(Math.max(IMAGE_WIDTH, size.width), Math.max(size.height, IMAGE_HEIGHT));
		}
	}

	private class TitlePaneLayout implements LayoutManager {
		public void addLayoutComponent(String name, Component c) {
		}

		public void removeLayoutComponent(Component c) {
		}

		public Dimension preferredLayoutSize(Container c) {
			int height = computeHeight();
			return new Dimension(height, height);
		}

		public Dimension minimumLayoutSize(Container c) {
			return preferredLayoutSize(c);
		}

		private int computeHeight() {
			// FontMetrics fm = rootPane.getFontMetrics(getFont());
			int fontHeight = BeeUIUtils.getDefaultFontSize();
			fontHeight += BeeUIUtils.getDefaultFontSize();
			int iconHeight = 0;
			if (getWindowDecorationStyle() == JRootPane.FRAME) {
				iconHeight = IMAGE_HEIGHT;
			}

			int finalHeight = Math.max(fontHeight, iconHeight);
			return finalHeight;
		}

		public void layoutContainer(Container c) {
			boolean leftToRight = (window == null) ? getRootPane().getComponentOrientation().isLeftToRight()
					: window.getComponentOrientation().isLeftToRight();

			int w = getWidth();
			int x;
			int y = BeeUIUtils.getDefaultFontSize() / 3;
			int spacing;
			int buttonHeight;
			int buttonWidth;

			// if (closeButton != null && closeButton.getIcon() != null) {
			// buttonHeight = closeButton.getIcon().getIconHeight();
			// buttonWidth = closeButton.getIcon().getIconWidth();
			// } else {
			buttonHeight = IMAGE_HEIGHT;
			buttonWidth = IMAGE_WIDTH;
			// }

			// assumes all buttons have the same dimensions
			// these dimensions include the borders

			x = leftToRight ? w : 0;

			spacing = 5;
			x = leftToRight ? spacing : w - buttonWidth - spacing;
			if (menuBar != null) {
				menuBar.setBounds(x, y, buttonWidth, buttonHeight);
			}

			x = leftToRight ? w : 0;
			spacing = BeeUIUtils.getDefaultFontSize() / 3;
			x += leftToRight ? -spacing - buttonWidth : spacing;
			if (closeButton != null) {
				closeButton.setBounds(x, y, buttonWidth, buttonHeight);
			}

			if (!leftToRight)
				x += buttonWidth;

			if (getWindowDecorationStyle() == JRootPane.FRAME) {
				if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
					if (toggleButton.getParent() != null) {
						spacing = 2;
						x += leftToRight ? -spacing - buttonWidth : spacing;
						toggleButton.setBounds(x, y, buttonWidth, buttonHeight);
						if (!leftToRight) {
							x += buttonWidth;
						}
					}
				}

				if (iconifyButton != null && iconifyButton.getParent() != null) {
					spacing = 2;
					x += leftToRight ? -spacing - buttonWidth : spacing;
					iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
					if (!leftToRight) {
						x += buttonWidth;
					}
				}
			}
			buttonsWidth = leftToRight ? w - x : x;
		}
	}

	/**
	 * PropertyChangeListener installed on the Window. Updates the necessary state
	 * as the state of the Window changes.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent pce) {
			String name = pce.getPropertyName();

			// Frame.state isn't currently bound.
			if ("resizable".equals(name) || "state".equals(name)) {
				Frame frame = getFrame();

				if (frame != null) {
					setState(frame.getExtendedState(), true);
				}
				if ("resizable".equals(name)) {
					getRootPane().repaint();
				}
			} else if ("title".equals(name)) {
				repaint();
			} else if ("componentOrientation" == name) {
				revalidate();
				repaint();
			} else if ("iconImage" == name) {
				updateSystemIcon();
				revalidate();
				repaint();
			}
		}
	}

	/**
	 * Update the image used for the system icon
	 */
	private void updateSystemIcon() {
		Window window = getWindow();
		if (window == null) {
			systemIcon = null;
			return;
		}
		java.util.List<Image> icons = window.getIconImages();
		assert icons != null;

		if (icons.size() == 0) {
			systemIcon = null;
		} else if (icons.size() == 1) {
			systemIcon = icons.get(0);
		} else {
			systemIcon = SunToolkit.getScaledIconImage(icons, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
	}

	/**
	 * WindowListener installed on the Window, updates the state as necessary.
	 */
	private class WindowHandler extends WindowAdapter {
		public void windowActivated(WindowEvent ev) {
			setActive(true);
		}

		public void windowDeactivated(WindowEvent ev) {
			setActive(false);
		}
	}
}
