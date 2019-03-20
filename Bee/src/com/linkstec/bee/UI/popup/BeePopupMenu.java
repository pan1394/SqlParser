package com.linkstec.bee.UI.popup;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.text.BeeTextField;

public class BeePopupMenu extends BeePopUI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1606141364107932148L;
	private JComponent component;
	private Object userObject;
	protected JPanel contents = new JPanel();

	protected List<IBeePopupMenuAction> actions = new ArrayList<IBeePopupMenuAction>();
	protected int selected = -1;
	private int defaultWidth = BeeUIUtils.getDefaultFontSize() * 30;

	public BeePopupMenu(JComponent component) {
		super(component);
		this.component = component;
		this.setSize(new Dimension(defaultWidth, defaultWidth));
		// JScrollPane pane = new JScrollPane(contents);
		// pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inner.add(contents);
		GridLayout g = new GridLayout(0, 1);
		g.setVgap(0);
		contents.setLayout(g);
		contents.setBackground(BACK_COLOR);

		contents.addMouseListener(entered);
		// this.contents.setBorder(new EmptyBorder(3, 3, 3, 3));
	}

	public void showPop(int x, int y) {
		this.selected = -1;
		Point point = component.getLocationOnScreen();

		this.setSize(defaultWidth, this.getHeight());

		int screenWidth = BeeUIUtils.getScreenSize().width;
		if (point.x + x + this.getWidth() + BeeUIUtils.getDefaultFontSize() > screenWidth) {
			this.setLocation(point.x + x - this.getWidth(), point.y + y);
		} else {
			this.setLocation(point.x + x, point.y + y);
		}

		makeBounds(x, y);
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent(JComponent component) {
		this.component = component;
	}

	public void showPop(int hgap) {
		this.selected = -1;
		if (component.getWidth() > defaultWidth) {
			// if (this.getWidth() < component.getWidth()) {
			this.setSize(component.getWidth(), this.getHeight());
			// }
		} else {
			this.setSize(defaultWidth, this.getHeight());
		}
		try {

			Point point = component.getLocationOnScreen();
			int screenWidth = BeeUIUtils.getScreenSize().width;
			if (point.x + this.getWidth() + BeeUIUtils.getDefaultFontSize() > screenWidth) {
				this.setLocation(point.x - this.getWidth(), point.y + hgap);
			} else {
				this.setLocation(point.x, point.y + hgap);
			}

			makeBounds(point.x, point.y + hgap);
		} catch (Exception e) {

		}

	}

	public void makeBounds(int x, int y) {
		int bottom = y + this.getHeight() + this.handlerHeight;
		int screenBottom = BeeUIUtils.getScreenSize().height;

		if (bottom > screenBottom) {
			int adjust = bottom - screenBottom;
			this.setSize(this.getWidth(), this.getHeight() - adjust);
		}
		this.unstick();
		this.setVisible(true);
	}

	public void addAction(IBeePopupMenuAction action) {
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return this.contents.getComponentCount() == 0;
	}

	public void clear() {
		this.contents.removeAll();
	}

	public void setItems(List<BeePopupMenuItem> items) {
		this.clear();
		if (items == null) {
			contents.updateUI();
			return;
		}
		for (BeePopupMenuItem item : items) {
			this.addItem(item);
		}
		contents.updateUI();
	}

	public void addItem(BeePopupMenuItem item) {

		MouseAdapter lis = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if (component instanceof BeeTextField) {
						BeeTextField text = (BeeTextField) component;
						text.setUserObject(item.getValue());
						text.setText(item.getName());
						// text.setIcon(item.getIcon());
						setVisible(false);
					}
					for (IBeePopupMenuAction action : actions) {
						action.menuSelected((BeePopupMenuItem) e.getComponent());
					}
				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				item.setSelected(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				item.setSelected(false);
			}

		};

		MouseMotionAdapter motion = new MouseMotionAdapter() {

		};
		item.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 30, BeeUIUtils.getDefaultFontSize() * 2));
		item.addMouseListener(lis);
		item.addMouseMotionListener(motion);
		item.addMouseListener(adapter);
		contents.add(item);
	}

	public void setSelected(int index) {
		selected = index;
		int count = contents.getComponentCount();
		for (int i = 0; i < count; i++) {
			if (i == index) {
				BeePopupMenuItem item = ((BeePopupMenuItem) contents.getComponent(i));
				item.setSelected(true);
				inner.scrollRectToVisible(item.getBounds());

			} else {
				((BeePopupMenuItem) contents.getComponent(i)).setSelected(false);
			}
		}

	}

	public void selectNext() {
		if (selected < this.contents.getComponentCount() - 1) {
			selected++;
			this.setSelected(selected);
		}
	}

	public void selectBefore() {
		if (selected > 0) {
			selected--;

			this.setSelected(selected);
		}
	}

	public Object getSelectedItem() {
		if (this.selected > -1 && this.selected < contents.getComponentCount() - 1) {
			return (BeePopupMenuItem) contents.getComponent(this.selected);
		}
		return null;
	}

	@Override
	public void setVisible(boolean b) {
		if (!b) {
			selected = -1;
		}
		super.setVisible(b);
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

}
