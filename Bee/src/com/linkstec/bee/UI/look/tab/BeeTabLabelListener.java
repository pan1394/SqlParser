package com.linkstec.bee.UI.look.tab;

import java.io.Serializable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.linkstec.bee.UI.look.icon.BeeIcon;
import com.linkstec.bee.UI.spective.detail.IBeeTitleUI;
import com.linkstec.bee.UI.spective.detail.IBeeTitleUI.TitleChangeListener;

public class BeeTabLabelListener implements TitleChangeListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4447197981619871682L;

	transient JLabel label = null;
	transient BeeTabbedPane pane;

	public BeeTabLabelListener(JLabel label, BeeTabbedPane pane) {
		this.pane = pane;
		this.label = label;
	}

	@Override
	public void change(IBeeTitleUI comp) {
		if (label != null) {
			String t = comp.getTitleLabel();
			label.setText(t);
			label.updateUI();

			if (pane instanceof IBeeTitleUI) {
				IBeeTitleUI ui = (IBeeTitleUI) pane;
				List<TitleChangeListener> listeners = ui.getTitleChangeListeners();
				for (TitleChangeListener l : listeners) {
					l.change(comp);
				}
			}

		}
	}

	@Override
	public void setError(boolean error) {
		if (label != null) {
			Icon icon = label.getIcon();

			if (icon != null && icon instanceof BeeIcon) {
				label.setName(error ? "ERROR" : null);
				label.repaint();
			}
			pane.fireErrorStatus();
		}

	}

	@Override
	public void setAlert(boolean alert) {
		if (label != null) {
			Icon icon = label.getIcon();

			if (icon != null && icon instanceof BeeIcon) {
				label.setName(alert ? "ALERT" : null);
				label.repaint();
			}
			pane.fireErrorStatus();
		}

	}
}