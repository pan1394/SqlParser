package com.linkstec.bee.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import com.linkstec.bee.UI.list.BeeListCellRenderer;
import com.linkstec.bee.UI.look.BeeComboxUI;
import com.linkstec.bee.UI.look.button.BeeIconButton;
import com.linkstec.bee.UI.look.button.BeeIconButtonAction;
import com.linkstec.bee.UI.look.combox.BeeComBox;
import com.linkstec.bee.UI.look.icon.BeeIcon;
import com.linkstec.bee.UI.look.menu.BeeMemuDisableListener;
import com.linkstec.bee.UI.look.menu.BeeMenu;
import com.linkstec.bee.UI.look.menu.BeeMenuItem;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.Application;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraphView;

public class EditorToolBar extends JToolBar implements MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8015443128436394471L;

	/**
	 * 
	 * @param frame
	 * @param orientation
	 */
	private boolean ignoreZoomChange = false;

	/**
	 * 
	 */

	mxIEventListener scaleTracker;

	public mxIEventListener getScaleTracker() {
		return scaleTracker;
	}

	int gap = BeeUIUtils.getDefaultFontSize();
	public double scale = 1.0;

	private void creatItems(Component[] items) {
		for (Component comp : items) {
			if (comp instanceof BeeMenuItem) {
				BeeMenuItem item = (BeeMenuItem) comp;
				if (item.isOnMenuBar()) {
					this.add(item.getAction());
				}
			} else if (comp instanceof BeeMenu) {
				BeeMenu bm = (BeeMenu) comp;
				creatItems(bm.getMenuComponents());
			}
		}
		addSeparator();
	}

	public EditorToolBar(final BeeEditor editor, int orientation) {

		super(orientation);

		ToolTipManager.sharedInstance().registerComponent(this);

		this.setPreferredSize(new Dimension(20, (int) (BeeUIUtils.getDefaultFontSize() * 2.5)));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		EditorMenuBar bar = editor.getMenuBar();
		int count = bar.getMenuCount();
		for (int i = 0; i < count; i++) {
			JMenu menu = bar.getMenu(i);
			Component[] items = menu.getMenuComponents();
			this.creatItems(items);
		}
		final BeeComBox<Object> zoomCombo = new BeeComBox<Object>(new Object[] { "400%", "200%", "150%", "100%", "75%",
				"50%", mxResources.get("page"), mxResources.get("width"), mxResources.get("actualSize") });
		zoomCombo.setEditable(true);

		zoomCombo.setSelectedItem("100%");
		zoomCombo.setMinimumSize(new Dimension(gap * 7, 0));
		zoomCombo.setPreferredSize(new Dimension(gap * 7, (int) (gap * 1.7)));
		zoomCombo.setMaximumSize(new Dimension(gap * 7, (int) (gap * 1.7)));
		zoomCombo.setMaximumRowCount(9);
		zoomCombo.putClientProperty(BeeMenuItem.DISABLE_LISTENER, EditorMenuBar.graphActiveListner);
		zoomCombo.setUI(new BeeComboxUI());
		zoomCombo.setRenderer(new BeeListCellRenderer());
		add(zoomCombo);

		// Sets the zoom in the zoom combo the current value
		scaleTracker = new mxIEventListener() {
			/**
			 * 
			 */
			public void invoke(Object sender, mxEventObject evt) {
				ignoreZoomChange = true;

				try {
					if (sender instanceof mxGraphView) {

						mxGraphView view = (mxGraphView) sender;
						scale = view.getScale();
						zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale()) + "%");
					}
				} finally {
					ignoreZoomChange = false;
				}
			}
		};

		zoomCombo.addActionListener(new ActionListener() {
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				BeeGraphSheet sheet = Application.getInstance().getDesignSpective().getGraphSheet();
				if (sheet != null) {
					BeeGraphSheet graphComponent = (BeeGraphSheet) sheet;

					// Zoomcombo is changed when the scale is changed in the diagram
					// but the change is ignored here
					if (!ignoreZoomChange) {
						String zoom = zoomCombo.getSelectedItem().toString();

						if (zoom.equals(mxResources.get("page"))) {
							graphComponent.setPageVisible(true);
							graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
						} else if (zoom.equals(mxResources.get("width"))) {
							graphComponent.setPageVisible(true);
							graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
						} else if (zoom.equals(mxResources.get("actualSize"))) {
							graphComponent.zoomActual();
						} else {
							try {
								zoom = zoom.replace("%", "");
								double scale = Math.min(16, Math.max(0.01, Double.parseDouble(zoom) / 100));
								graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(editor, ex.getMessage());
							}
						}
					}
				}
			}
		});

		add(BeeActions.bind("Fill", new BEditorActions.ColorAction("Fill", mxConstants.STYLE_FILLCOLOR),
				"/com/mxgraph/examples/swing/images/fillcolor.gif"));

		this.initButtons();
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}

	public void addSeparator() {
		this.addSeparator(new Dimension((int) (gap * 1.5), (int) (gap * 1.5)));
	}

	@Override
	public JButton add(Action a) {

		JButton button = super.add(a);
		button.setOpaque(false);
		a.putValue("MenuAction", a.getValue(Action.NAME));
		button.setToolTipText((String) a.getValue(Action.NAME));

		Icon icon = button.getIcon();
		if (icon != null) {
			if (icon instanceof ImageIcon) {
				if (icon instanceof BeeIcon) {
					BeeIcon bee = (BeeIcon) icon;
					bee = (BeeIcon) bee.clone();
					bee.setTopMargin(0);
					button.setIcon(bee);
				}

				button.setSelectedIcon(icon);
				button.setDisabledIcon(BeeUIUtils.getDisabledIcon((ImageIcon) icon, this));
			}
		}

		return button;
	}

	public void refreshItems() {
		int count = this.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponent(i);
			Action action = null;
			Object obj = null;
			if (comp instanceof JButton) {
				JButton b = (JButton) comp;
				action = b.getAction();
				if (action != null) {
					obj = action.getValue(BeeMenuItem.DISABLE_LISTENER);
				}
			} else if (comp instanceof JComboBox) {
				JComboBox box = (JComboBox) comp;
				obj = box.getClientProperty(BeeMenuItem.DISABLE_LISTENER);
			}

			if (obj != null) {
				if (obj instanceof BeeMemuDisableListener) {
					BeeMemuDisableListener l = (BeeMemuDisableListener) obj;
					if (l.enable(comp)) {
						comp.setEnabled(true);
					} else {
						comp.setEnabled(false);
					}
				}

			}

		}
	}

	private void initButtons() {

		this.addSpectiveButton(BeeConstants.PERSPECTIVE_DESIGN_ICON, "詳細設計編集モードへシフトする", BeeSpective.DETAIL_DESIGN);
		this.addSpectiveButton(BeeConstants.PERSPECTIVE_JAVA_SOURCE_ICON, "ソース編集モードへシフトする", BeeSpective.JAVA_SOURCE);
		this.addSpectiveButton(BeeConstants.BASIC_DESIGN_ICON, "基本設計編集モードへシフトする", BeeSpective.BASIC_DESIGN);
		// this.addSpectiveButton(BeeConstants.CONFIG_ICON, "設定モードへシフトする",
		// BeeSpective.CONFIG);

	}

	private void addSpectiveButton(ImageIcon icon, String tip, String spectiveName) {
		BeeIconButton d = new BeeIconButton(icon);
		d.setTipText(tip);
		d.getActions().add(new BeeIconButtonAction() {

			@Override
			public void execute(JComponent source) {
				Application.getInstance().setSpactive(spectiveName);
			}

		});
		d.setUserObject(spectiveName);

		this.buttons.add(d);
	}

	private List<BeeIconButton> buttons = new ArrayList<BeeIconButton>();

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		this.paintButtons(g);

	}

	private void paintButtons(Graphics g) {

		int h = this.getHeight();
		int gap = (h - BeeIconButton.SIZE) / 2;
		int width = this.getWidth() - gap;
		double scale = 0.66;
		for (BeeIconButton b : buttons) {
			Dimension size = b.getSize();
			Rectangle rect = new Rectangle((int) (width - size.getWidth() - gap), gap, (int) size.getWidth(),
					(int) size.getHeight());
			b.setBounds(rect);
			width = width - rect.width - gap;
		}
		Graphics2D g2d = (Graphics2D) g;
		this.paintRect(g2d);
		for (BeeIconButton b : buttons) {
			Rectangle bounds = b.getBounds();
			Color color = Color.decode("#007700");// 007700 0099FF
			if (b.getUserObject().equals(Application.getInstance().getCurrentSpactive())) {
				BeeUIUtils.drawCircle((Graphics2D) g, bounds.x, bounds.y, bounds.width, Color.LIGHT_GRAY);
			} else if (b.isMouseOver()) {
				BeeUIUtils.drawCircle((Graphics2D) g, bounds.x, bounds.y, bounds.width, color);

			}
			Dimension size = b.getSize();
			Dimension imageSize = new Dimension((int) (size.getWidth() * scale), (int) (size.getHeight() * scale));
			int imageXGap = (int) (size.getWidth() * (1 - scale) / 2);
			int imageYGap = (int) (size.getHeight() * (1 - scale) / 2);

			g.drawImage(b.getIcon().getImage(), (int) (bounds.x + imageXGap), bounds.y + imageYGap,
					(int) imageSize.getWidth(), (int) imageSize.getHeight(), this);
		}

	}

	private void paintRect(Graphics2D g2) {
		if (!buttons.isEmpty()) {
			int x = -1;
			for (BeeIconButton b : buttons) {
				Rectangle bounds = b.getBounds();
				if (x == -1) {
					x = bounds.x;
				} else {
					x = Math.min(x, bounds.x);
				}
			}
			if (x < 10) {
				return;
			}
			int gap = BeeUIUtils.getDefaultFontSize();
			int margin = gap / 4;
			x = (int) (x - margin * 1.3);

			int y = margin;
			int w = (int) (this.getWidth() - x - margin * 2.8);
			int h = this.getHeight() - margin * 2;
			BeeUIUtils.fillTextureRoundRec(g2, Color.WHITE, x, y, w, h, 50, -30);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		for (BeeIconButton b : buttons) {
			if (b.getBounds().contains(x, y)) {
				b.setMouseOver(true);
			} else {
				b.setMouseOver(false);
			}

		}
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		for (BeeIconButton b : buttons) {
			if (b.getBounds().contains(x, y)) {
				b.execute(this);
			}
		}

	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		for (BeeIconButton b : buttons) {
			if (b.getBounds().contains(x, y)) {
				return b.getTipText();
			}
		}
		return null;
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.repaint();
	}

}
