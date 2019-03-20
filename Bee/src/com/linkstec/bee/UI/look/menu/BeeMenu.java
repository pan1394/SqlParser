package com.linkstec.bee.UI.look.menu;

import java.awt.Insets;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeMenuUI;
import com.linkstec.bee.UI.look.icon.BeeIcon;

public class BeeMenu extends JMenu implements AncestorListener, MenuListener, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6176145219390249897L;
	private static int margin = BeeUIUtils.getDefaultFontSize() / 2;

	public BeeMenu(String s) {
		super(s);
		setFont(BeeUIUtils.getDefaultFont());
		this.setMargin(new Insets(margin, margin, 0, margin));
		this.addAncestorListener(this);
		this.addMenuListener(this);
		this.setUI(new BeeMenuUI());
	}

	@Override
	public void ancestorAdded(AncestorEvent event) {

		makeChildStatus();

	}

	private void makeChildStatus() {
		int count = this.getMenuComponentCount();
		for (int i = 0; i < count; i++) {
			Object menu = this.getMenuComponent(i);
			if (menu instanceof BeeMenuItem) {
				BeeMenuItem item = (BeeMenuItem) menu;
				item.beforeShow();
			}
		}
	}

	public void beforeShow() {
		if (this.getAction() != null) {
			Object obj = this.getAction().getValue(BeeMenuItem.DISABLE_LISTENER);
			if (obj != null && obj instanceof BeeMemuDisableListener) {
				BeeMemuDisableListener list = (BeeMemuDisableListener) obj;
				this.setEnabled(list.enable(this));
			}
		}
	}

	@Override
	public void ancestorRemoved(AncestorEvent event) {

	}

	@Override
	public void ancestorMoved(AncestorEvent event) {

	}

	@Override
	public JMenuItem add(Action a) {
		BeeMenuItem mi = createActionComponent(a);
		mi.setAction(a);
		add(mi);
		mi.setFont(BeeUIUtils.getDefaultFont());
		return mi;
	}

	protected BeeMenuItem createActionComponent(Action a) {
		BeeMenuItem mi = new BeeMenuItem() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1416182309290511975L;

			protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
				PropertyChangeListener pcl = createActionChangeListener(this);
				if (pcl == null) {
					pcl = super.createActionPropertyChangeListener(a);
				}
				return pcl;
			}
		};
		mi.setHorizontalTextPosition(JButton.TRAILING);
		mi.setVerticalTextPosition(JButton.CENTER);
		return mi;
	}

	public String toString() {
		return this.getText();
	}

	@Override
	public void menuSelected(MenuEvent e) {
		makeChildStatus();
	}

	@Override
	public void menuDeselected(MenuEvent e) {

	}

	@Override
	public void menuCanceled(MenuEvent e) {

	}

	@Override
	public Icon getIcon() {
		Icon icon = super.getIcon();
		if (icon instanceof BeeIcon) {
			BeeIcon b = (BeeIcon) icon;
			b.setTopMargin(margin);
		}
		return icon;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
