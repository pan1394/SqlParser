package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.linkstec.bee.UI.look.BeeLook;
import com.linkstec.bee.UI.spective.BeeDetailSpective;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BeeLogger;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;

public class BeeEditor extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	private BEditor currentEditor;
	protected static BeeSplash splash;
	protected EditorMenuBar menuBar;
	protected String appTitle;
	protected EditorStatusBar statusBar;
	protected BeeDetailSpective workSpace;
	protected EditorToolBar toolbar;

	public BeeEditor(String[] args) {
		this("Bee", args);
	}

	public BeeEditor(String appTitle, String[] args) {
		this.setOpaque(false);
		this.appTitle = appTitle;
		Application.getInstance().setEditor(this);
		BeeLogger.closeTemp();
		if (args != null) {
			for (String a : args) {
				if (a.equals("ver")) {
					BeeLogger.initialize(Application.getInstance().getConfigSpective().getConfig());
					break;
				}
			}
		}
		setLayout(new BorderLayout());

		// Creates the status bar
		statusBar = new EditorStatusBar();
		menuBar = new EditorMenuBar(this);
		add(statusBar, BorderLayout.SOUTH);
		installToolBar();

		add(Application.getInstance().getSpectiveContainer(), BorderLayout.CENTER);
		// Application.getInstance().setSpactive(BeeSpective.BASIC_DESIGN);
		Application.afterEditorOpened();

		Application.getInstance().setSpactive(BeeSpective.BASIC_DESIGN);

		splash.setVisible(false);
		Application.EDITOR_INSTANCED = true;
	}

	public EditorStatusBar getStatusBar() {
		return statusBar;
	}

	public static JFrame createFrame() {

		JFrame frame = new JFrame();
		Application.FRAME = frame;

		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int width = screenSize.width - insets.left - insets.right;
		int height = screenSize.height - insets.top - insets.bottom;

		frame.setMinimumSize(new Dimension(width / 2, height / 2));
		frame.setMaximizedBounds(new Rectangle(0, 0, width, height));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(BeeConstants.APP_ICON.getImage());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		frame.setTitle("Bee");

		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {

				Application.beforeApplicationClose();

			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {
				Window[] windows = JWindow.getWindows();
				for (Window win : windows) {
					if (win.isVisible() && !(win instanceof JFrame)) {
						win.setVisible(false);
					}
				}
			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

		});

		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				Application.getInstance().getDesignSpective().doLayout();
			}

		});

		return frame;
	}

	/**
	 * 
	 */
	public void about() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			EditorAboutFrame about = new EditorAboutFrame(frame);
			about.setModal(true);

			// Centers inside the application frame
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	/**
	 * 
	 */
	public void updateFrameTitle(String path) {
		JFrame frame = Application.FRAME;

		if (frame != null) {
			if (path == null || path.equals("")) {
				frame.setTitle(appTitle);
			} else {
				frame.setTitle(appTitle + " - " + path);
			}
		}
	}

	public BEditor getCurrentEditor() {
		return this.currentEditor;
	}

	public void setCurrentEditor(BEditor currentEditor) {
		this.currentEditor = currentEditor;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			splash = new BeeSplash();
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			splash.setLocation(new Point((int) ((d.getWidth() - splash.getWidth()) / 2),
					(int) ((d.getHeight() - splash.getHeight()) / 2)));
			splash.setVisible(true);
			java.awt.Toolkit.getDefaultToolkit().setDynamicLayout(true);

			new BeeLook();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		JFrame frame = BeeEditor.createFrame();
		BeeEditor editor = new BeeEditor(args);
		frame.setJMenuBar(editor.menuBar);
		frame.getContentPane().add(editor);
		frame.setVisible(true);

	}

	public EditorToolBar getToolbar() {
		return toolbar;
	}

	public EditorMenuBar getMenuBar() {
		return this.menuBar;
	}

	protected void installToolBar() {
		toolbar = new EditorToolBar(this, JToolBar.HORIZONTAL);
		add(toolbar, BorderLayout.NORTH);
	}

}
