package com.linkstec.bee.UI.look.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeTabbedPaneUI;
import com.linkstec.bee.UI.look.button.BeeIconButton;
import com.linkstec.bee.UI.look.button.BeeIconButtonAction;
import com.linkstec.bee.UI.popup.BeePopupMenu;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.UI.popup.IBeePopupMenuAction;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BPopItem;

public class BeeTabbedPane extends JTabbedPane implements MouseListener, FocusListener, IBeePopupMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean editable = true;
	private boolean barEdiable = true;
	private List<BeeIconButton> buttons = new ArrayList<BeeIconButton>();
	private List<BeeIconButton> componentButtons = new ArrayList<BeeIconButton>();
	protected BeePopupMenu actionMenu;

	public static List<BeeTabbedPane> panes = new ArrayList<BeeTabbedPane>();
	private MaxMinControl mmControle = new MaxMinControl(this);
	private boolean withdDefualtActions = true;

	private int buttonAreaWidth = 0;
	private int componentButtonAreaHeight = 0;

	public BeeTabbedPane(boolean withdDefualtActions) {
		this.withdDefualtActions = withdDefualtActions;
		initialize();
	}

	public BeeTabbedPane() {
		super();
		initialize();
	}

	public void initialize() {
		panes.add(this);
		actionMenu = new BeePopupMenu(this);
		actionMenu.scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		actionMenu.addAction(this);
		actionMenu.setName("Popup");
		this.addMouseMotionListener(mouseListener);
		this.addMouseListener(this);

		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		this.setBorder(null);
		this.setFocusable(true);

		this.setFocusCycleRoot(true);
		this.addFocusListener(this);

		if (!this.withdDefualtActions) {
			return;
		}

		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedIndex();
				selectedIndexChanged(index);
			}
		});

		BeeIconButton max = new BeeIconButton(BeeConstants.WIN_MAX_ICON);
		max.setName("MAX");
		max.getActions().add(new BeeIconButtonAction() {

			@Override
			public void execute(JComponent source) {
				maxmize();
			}

		});

		this.buttons.add(max);

		BeeIconButton menuButton = new BeeIconButton(BeeConstants.WIN_MENU_ICON);
		menuButton.setName("MENU");
		menuButton.setSpaceIconTop(BeeUIUtils.getDefaultFontSize() / 7);
		menuButton.getActions().add(new BeeIconButtonAction() {

			@Override
			public void execute(JComponent source) {
				if (menuButton.getActions().size() != 0) {
					actionMenu.clear();
					beforeMenuShow();
					if (!actionMenu.isEmpty()) {
						if (getTabPlacement() == JTabbedPane.TOP) {
							actionMenu.showPop(menuButton.getBounds().x,
									menuButton.getBounds().y + menuButton.getBounds().height);
						} else if (getTabPlacement() == JTabbedPane.BOTTOM) {
							actionMenu.showPop(menuButton.getBounds().x,
									menuButton.getBounds().y - actionMenu.getHeight());
						}
					}
				}
			}

		});
		this.buttons.add(menuButton);

	}

	public boolean isBarEdiable() {
		return barEdiable;
	}

	public void setBarEdiable(boolean barEdiable) {
		this.barEdiable = barEdiable;
	}

	public int getButtonAreaWidth() {
		return buttonAreaWidth;
	}

	public int getComponentButtonAreaHeight() {
		return componentButtonAreaHeight;
	}

	public void maxmize() {
		mmControle.changeStatus();
	}

	protected void beforeMenuShow() {
		BeeTabbedPaneUI ui = (BeeTabbedPaneUI) this.getUI();
		int hiddenIndex = ui.getHiddenStartIndex();
		int selected = this.getSelectedIndex();
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			BeePopupMenuItem item = new BeePopupMenuItem();
			item.setText(this.getTitleAt(i));
			item.setIcon((ImageIcon) this.getIconAt(i));

			this.actionMenu.addItem(item);
			item.setValue(Integer.valueOf(i));
			if (hiddenIndex > 1 && i >= hiddenIndex) {
				item.setFont(item.getFont().deriveFont(Font.BOLD));
			}
			if (i == selected) {
				item.setForeground(Color.GREEN.darker().darker());
				item.setFont(item.getFont().deriveFont(Font.BOLD));
			}
		}
	}

	@Override
	public void menuSelected(Object menu) {
		BeePopupMenuItem item = (BeePopupMenuItem) menu;
		if (item.getValue() instanceof Integer) {
			int index = ((Integer) item.getValue());
			setSelectedIndex(index);
		}
		this.actionMenu.setVisible(false);
	}

	public BeePopupMenu getActionMenu() {
		return this.actionMenu;
	}

	public void addMenuItem(BeePopupMenuItem item) {
		actionMenu.addItem(item);
	}

	public void addMenuAction(IBeePopupMenuAction action) {
		this.actionMenu.addAction(action);
	}

	@Override
	public void focusGained(FocusEvent e) {

		if (getMousePosition(true) != null) {
			setFocusGained(true);
		}

	}

	@Override
	public void focusLost(FocusEvent e) {

		if (getMousePosition(true) == null) {
			setFocusGained(false);
		}
		for (BeeTabbedPane b : panes) {
			if (!b.equals(this)) {
				b.focusGained(e);
			}
		}
	}

	public void fireErrorStatus() {

	}

	public void addIconButton(BeeIconButton b) {
		this.buttons.add(b);
	}

	public void addComponentIconButton(BeeIconButton b) {
		this.componentButtons.add(b);
	}

	public void paint(Graphics g) {
		super.paint(g);

		if (this.barEdiable) {
			this.paintButtons(g);
			this.paintComponentButtons(g);
		}

		if (!this.isEditable()) {
			return;
		}
		int count = this.getTabCount();
		int x = 0;
		// int height = 0;
		JComponent container = null;
		for (int i = 0; i < count; i++) {
			if (this.getTabComponentAt(i) != null) {
				container = (JComponent) this.getTabComponentAt(i).getParent();
				if (container != null) {
					x = x + container.getInsets().left;
				}
				// height = container.getHeight();
				break;
			}
		}
		if (container != null) {
			for (int i = 0; i < count; i++) {
				JComponent comp = (JComponent) this.getTabComponentAt(i);
				if (comp != null) {
					x = Math.max(x, comp.getWidth() + comp.getX() + comp.getInsets().right);
				}
			}
		}
	}

	private void paintButtons(Graphics g) {

		int h = BeeUIUtils.getDefaultFontSize() * 2;
		int gap = (h - BeeIconButton.SIZE) / 2 + 2;
		int width = this.getWidth() - gap - 2;
		int height = this.getHeight() - gap + 6;

		for (BeeIconButton b : buttons) {
			if (b.getBounds() == null) {
				continue;
			}
			if (b.getName() != null && b.getName().equals("MAX")) {
				if (!this.mmControle.isMax()) {
					b.setIcon(BeeConstants.WIN_MAX_ICON);
				} else {
					b.setIcon(BeeConstants.WIN_MIN_ICON);
				}
			}
			if (b.isMouseOver()) {
				g.setColor(b.getBackColor());
				Rectangle bounds = b.getBounds();
				g.fillRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
				g.setColor(b.getBorderColor());
				g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
			}
			double scale = 0.66;
			Dimension size = b.getSize();
			Rectangle rect = null;
			if (this.getTabPlacement() == JTabbedPane.TOP) {
				rect = new Rectangle((int) (width - size.getWidth() - gap), gap, (int) size.getWidth(),
						(int) size.getHeight());
			} else if (this.getTabPlacement() == JTabbedPane.BOTTOM) {
				rect = new Rectangle((int) (width - size.getWidth() - gap), (int) (height - size.getHeight() - gap),
						(int) size.getWidth(), (int) size.getHeight());
			}

			double m = BeeUIUtils.getDefaultFontSize() / 11;
			int imageXGap = (int) (size.getWidth() * (1 - scale) / 2);
			int imageYGap = (int) (size.getHeight() * (1 - scale) / 2 + b.getSpaceIconTop() * m);

			Image img = b.getIcon().getImage();
			int iw = img.getWidth(this);
			int ih = img.getHeight(this);
			g.drawImage(b.getIcon().getImage(), (int) (rect.x + imageXGap), rect.y + imageYGap, (int) (iw * m),
					(int) (ih * m), this);

			b.setBounds(rect);

			this.buttonAreaWidth = this.getWidth() - rect.x + gap;
			width = width - rect.width - gap;

			if (b.getName() != null && b.getName().equals("MENU")) {
				BeeTabbedPaneUI ui = (BeeTabbedPaneUI) this.getUI();
				int count = ui.getHiddenCount();
				if (count != 0) {
					g.setColor(Color.GRAY);
					g.setFont(g.getFont().deriveFont((float) BeeUIUtils.getDefaultFontSize()));
					Rectangle bounds = b.getBounds();
					g.drawString(Integer.toString(count), bounds.x,
							(int) (bounds.y + BeeUIUtils.getDefaultFontSize() * 1.5));
				}
			}
		}

	}

	private void paintComponentButtons(Graphics g) {

		int index = this.getSelectedIndex();
		if (index < 0) {
			return;
		}
		if (componentButtons.size() == 0) {
			return;
		}
		List<BeeIconButton> buttons = new ArrayList<BeeIconButton>();
		Component target = this.getSelectedComponent();
		if (target == null) {
			return;
		}
		for (BeeIconButton b : componentButtons) {
			if (b.getTarget() == null) {
				buttons.add(b);
			} else {
				if (b.getTarget().equals(target)) {
					buttons.add(b);
				}
			}
		}
		if (buttons.size() == 0) {
			return;
		}

		BeeTabbedPaneUI ui = (BeeTabbedPaneUI) this.getUI();
		Component comp = this.getTabComponentAt(index);
		Insets insets = ui.getTabAreaInsets(this.getTabPlacement());
		int tabAtreaHeight = comp.getHeight() + insets.bottom + insets.top;

		int h = BeeUIUtils.getDefaultFontSize() * 2;
		int gap = (h - BeeIconButton.SIZE) / 2 + 2;
		int width = this.getWidth() - gap - 2;
		int height = this.getHeight() - gap + 6;

		// g.setColor(Color.WHITE);
		this.componentButtonAreaHeight = (int) buttons.get(0).getSize().getHeight();
		// g.fillRect(1, tabAtreaHeight + this.getInsets().top + 8, this.getWidth() - 2,
		// this.componentButtonAreaHeight);

		for (BeeIconButton b : buttons) {
			if (b.getBounds() == null) {
				continue;
			}

			if (b.isMouseOver()) {
				g.setColor(b.getBackColor());
				Rectangle bounds = b.getBounds();
				g.fillRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
				g.setColor(b.getBorderColor());
				g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
			}
			double scale = 0.66;
			Dimension size = b.getSize();
			Rectangle rect = null;
			if (this.getTabPlacement() == JTabbedPane.TOP) {
				rect = new Rectangle((int) (width - size.getWidth() - gap), gap + tabAtreaHeight, (int) size.getWidth(),
						(int) size.getHeight());
			} else if (this.getTabPlacement() == JTabbedPane.BOTTOM) {
				rect = new Rectangle((int) (width - size.getWidth() - gap),
						(int) (height - size.getHeight() - gap - tabAtreaHeight), (int) size.getWidth(),
						(int) size.getHeight());
			}

			double m = BeeUIUtils.getDefaultFontSize() / 11;
			int imageXGap = (int) (size.getWidth() * (1 - scale) / 2);
			int imageYGap = (int) (size.getHeight() * (1 - scale) / 2 + b.getSpaceIconTop() * m);

			Image img = b.getIcon().getImage();
			int iw = img.getWidth(this);
			int ih = img.getHeight(this);
			g.drawImage(b.getIcon().getImage(), (int) (rect.x + imageXGap), rect.y + imageYGap, (int) (iw * m),
					(int) (ih * m), this);

			b.setBounds(rect);
			width = width - rect.width - gap;

			if (b.getName() != null && b.getName().equals("MENU")) {
				int count = ui.getHiddenCount();
				if (count != 0) {
					g.setColor(Color.GRAY);
					g.setFont(g.getFont().deriveFont((float) BeeUIUtils.getDefaultFontSize()));
					Rectangle bounds = b.getBounds();
					g.drawString(Integer.toString(count), bounds.x,
							(int) (bounds.y + BeeUIUtils.getDefaultFontSize() * 1.5));
				}
			}
		}

	}

	@Override
	public void removeTabAt(int index) {
		Component comp = this.getComponentAt(index);
		if (this.removeable(comp)) {
			super.removeTabAt(index);
		}
	}

	@Override
	public void remove(Component component) {
		if (this.removeable(component)) {
			super.remove(component);
		}
	}

	@Override
	public void remove(int index) {
		Component comp = this.getComponentAt(index);
		if (this.removeable(comp)) {
			super.remove(index);
		}
	}

	@Override
	public void removeAll() {
		int count = this.getTabCount();
		List<Component> comps = new ArrayList<Component>();
		for (int i = 0; i < count; i++) {
			comps.add(this.getComponentAt(i));
		}
		for (Component comp : comps) {
			this.remove(comp);
		}
	}

	public boolean removeable(Component component) {
		if (component instanceof BEditor) {
			BEditor editor = (BEditor) component;
			return editor.getManager().selectTabBeforeClosing(this.indexOfComponent(component), this);
		} else if (component instanceof BeeTabClosingListener) {
			BeeTabClosingListener l = (BeeTabClosingListener) component;
			return l.selectTabBeforeClosing(this.indexOfComponent(component), this);
		}
		return true;
	}

	private MouseMotionListener mouseListener = new MouseMotionListener() {

		@Override
		public void mouseDragged(MouseEvent arg0) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (barEdiable) {
				int x = e.getX();
				int y = e.getY();

				for (BeeIconButton b : buttons) {
					if (b.getBounds().contains(x, y)) {
						b.setMouseOver(true);
					} else {
						b.setMouseOver(false);
					}
					BeeTabbedPane.this.repaint(b.getBounds());
				}

				for (BeeIconButton b : componentButtons) {
					if (b.getBounds().contains(x, y)) {
						b.setMouseOver(true);
					} else {
						b.setMouseOver(false);
					}
					BeeTabbedPane.this.repaint(b.getBounds());
				}

				if (!isEditable()) {
					return;
				}
			}
		}

	};

	@Override
	public void mouseClicked(MouseEvent e) {
		if (this.barEdiable) {
			int x = e.getX();
			int y = e.getY();

			for (BeeIconButton b : buttons) {
				if (b.getBounds().contains(x, y)) {
					b.execute(this);
				}
			}
			for (BeeIconButton b : componentButtons) {
				if (b.getBounds().contains(x, y)) {
					b.execute(this);
				}
			}

			if (!isEditable()) {
				return;
			}
			this.requestFocusInWindow();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.setFocusGained(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		setTabComponentAt(index, new BeeTabCloseButton(component, title, icon, this));
	}

	@Override
	public void setTitleAt(int index, String title) {
		super.setTitleAt(index, title);
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		cbt.label.setText(title);
	}

	@Override
	public String getTitleAt(int index) {
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		if (cbt != null) {
			return cbt.label.getText();
		} else {
			return super.getTitleAt(index);
		}
	}

	@Override
	public Icon getIconAt(int index) {
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		if (cbt != null) {
			return cbt.label.getIcon();
		} else {
			return super.getIconAt(index);
		}
	}

	@Override
	public void setIconAt(int index, Icon icon) {
		super.setIconAt(index, icon);
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		cbt.label.setIcon(icon);
	}

	@Override
	public void setComponentAt(int index, Component component) {
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		super.setComponentAt(index, component);
		cbt.setComponent(component);
	}

	public void setErrorAt(int index, boolean error) {
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		cbt.setError(error);
	}

	public void setAlertAt(int index, boolean alert) {
		BeeTabCloseButton cbt = (BeeTabCloseButton) getTabComponentAt(index);
		cbt.setAlert(alert);
	}

	public void setFocusGained(boolean gained) {

		TabbedPaneUI ui = this.getUI();
		if (ui instanceof BeeTabbedPaneUI) {
			BeeTabbedPaneUI bee = (BeeTabbedPaneUI) ui;
			bee.setGainedFocus(gained);
			this.repaint();
		}

		for (BeeTabbedPane pane : panes) {
			if (!pane.equals(this)) {
				pane.setFocusLost();
			}
		}
	}

	public void setFocusLost() {
		TabbedPaneUI ui = this.getUI();
		if (ui instanceof BeeTabbedPaneUI) {
			BeeTabbedPaneUI bee = (BeeTabbedPaneUI) ui;
			bee.setGainedFocus(false);
			this.repaint();
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void selectedIndexChanged(int index) {
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			BeeTabCloseButton b = (BeeTabCloseButton) this.getTabComponentAt(i);
			if (b != null) {
				b.onSelected(i == index);
			}

		}
	}

	public static class MaxMinControl {
		private Hashtable<JSplitPane, Integer> positions = new Hashtable<JSplitPane, Integer>();
		private JComponent target;
		private boolean statusMax = false;

		public MaxMinControl(JComponent target) {
			this.target = target;
		}

		public void changeStatus() {
			if (this.statusMax) {
				this.setMin();
			} else {
				this.setMax();
			}
		}

		public void setMax() {
			positions.clear();

			Container parent = target.getParent();
			Container child = target;
			while (parent != null) {
				if (parent instanceof JSplitPane) {
					JSplitPane pane = (JSplitPane) parent;
					positions.put(pane, pane.getDividerLocation());
					if (pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
						if (child.equals(pane.getLeftComponent())) {
							pane.setDividerLocation(1.0);
						} else {
							pane.setDividerLocation(0);
						}
					} else if (pane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
						if (child.equals(pane.getTopComponent())) {
							pane.setDividerLocation(1.0);
						} else {
							pane.setDividerLocation(0);
						}
					}
				}
				child = parent;
				parent = parent.getParent();
			}

			statusMax = true;
		}

		public void setMin() {
			Enumeration<JSplitPane> enu = positions.keys();
			while (enu.hasMoreElements()) {
				JSplitPane pane = enu.nextElement();
				pane.setDividerLocation(positions.get(pane));
				pane.updateUI();
				pane.setBorder(null);
			}
			statusMax = false;
		}

		public boolean isMax() {
			return this.statusMax;
		}
	}

	public JPopupMenu getComponentPopupMenu(BeeTabCloseButton button) {
		return new ClosePopup(button, this);
	}

	public static class ClosePopup extends JPopupMenu implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2639081796501958470L;
		private BeeTabCloseButton button;
		private BeeTabbedPane pane;
		private int index = -1;

		public ClosePopup(BeeTabCloseButton button, BeeTabbedPane pane) {
			this.button = button;
			this.pane = pane;
			int count = pane.getTabCount();
			int index = -1;
			for (int i = 0; i < count; i++) {
				if (pane.getTabComponentAt(i).equals(button)) {
					index = i;
					break;
				}
			}
			this.index = index;
			if (index != -1) {
				this.add(makeItem("Close", BeeConstants.CLOSE_ICON, "SELF"));
				if (count > 1) {
					this.add(makeItem("Close Others", BeeConstants.CLOSEALL_ICON, "OTHERS"));
				}
				if (index > 0) {
					this.add(makeItem("Close tabs to the left", BeeConstants.CLOSEALL_ICON, "LEFT"));
				}
				if (index < count - 1) {
					this.add(makeItem("Close tabs to the right", BeeConstants.CLOSEALL_ICON, "RIGHT"));
				}
				if (count > 1) {
					this.addSeparator();
					this.add(makeItem("Close All", BeeConstants.CLOSEALL_ICON, "ALL"));
				}
			}
			Component comp = button.getComponent();
			if (comp instanceof BEditor) {
				BEditor editor = (BEditor) comp;
				List<BPopItem> menus = editor.getManager().getMenus();
				if (menus != null && menus.size() != 0) {
					this.addSeparator();
					for (BPopItem bmenu : menus) {
						JMenuItem item = this.add(this.makeItem(bmenu.getName(), bmenu.getIcon(), null));
						Object obj = bmenu.getValue();
						if (obj != null && obj instanceof AbstractAction) {
							AbstractAction action = (AbstractAction) obj;
							item.addActionListener(action);
						}
					}
				}
			}
		}

		private JMenuItem makeItem(String title, ImageIcon icon, String name) {
			JMenuItem item = new JMenuItem(title, icon);
			item.setName(name);
			item.addActionListener(this);
			return item;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			String name = item.getName();
			List<Component> removes = new ArrayList<Component>();
			if (name != null) {
				int count = pane.getTabCount();

				for (int i = 0; i < count; i++) {

					if (name.equals("SELF")) {
						if (pane.getTabComponentAt(i).equals(button)) {
							pane.remove(i);
							break;
						}
					} else if (name.equals("ALL")) {
						removes.add(pane.getComponentAt(i));
					} else if (name.equals("OTHERS")) {
						if (!pane.getTabComponentAt(i).equals(button)) {
							removes.add(pane.getComponentAt(i));
						}
					} else if (name.equals("LEFT")) {
						if (i < index) {
							removes.add(pane.getComponentAt(i));
						}
					} else if (name.equals("RIGHT")) {
						if (i > index) {
							removes.add(pane.getComponentAt(i));
						}
					}
				}
			}
			for (Component in : removes) {
				pane.remove(in);
			}
		}
	}

}
