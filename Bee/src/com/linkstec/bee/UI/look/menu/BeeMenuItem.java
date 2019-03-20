package com.linkstec.bee.UI.look.menu;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.icon.BeeIcon;

public class BeeMenuItem extends JMenuItem implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4728965883577760709L;
	public static final String DISABLE_LISTENER = "DISABLE_LISTENER";
	private static int margin = BeeUIUtils.getDefaultFontSize() / 3;

	private boolean onMenuBar = true;

	public BeeMenuItem() {
		setFont(BeeUIUtils.getDefaultFont());
		this.setBorder(new EmptyBorder(margin, margin, margin, margin));
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

	public String toString() {
		return this.getText();
	}

	public boolean isOnMenuBar() {
		return onMenuBar;
	}

	public void setOnMenuBar(boolean onMenuBar) {
		this.onMenuBar = onMenuBar;
	}

	@Override
	public Icon getDisabledIcon() {

		Icon icon = this.getIcon();
		if (icon != null) {
			if (icon instanceof ImageIcon) {
				return BeeUIUtils.getDisabledIcon((ImageIcon) icon, this);
			}
		}
		return icon;
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
