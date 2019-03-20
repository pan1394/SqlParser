package com.linkstec.bee.UI.look.tab;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.detail.IBeeTitleUI;

public class BeeTabCloseButton extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6512192102245022514L;
	private Component component;
	private boolean modified = false;
	private ImageIcon CLOSING_ICON = BeeConstants.SPACE_TAB_CLOSE_BTN_OFF;
	private ImageIcon CLOSING_ICON_SELECTED = BeeConstants.SPACE_TAB_CLOSE_BTN_ON;
	private ImageIcon EMPTY_ICON = BeeConstants.EMPTY_16X16_ICON;
	private BeeTabbedPane pane;
	public JLabel label;
	private JLabel closingLabel;
	private IBeeTitleUI basicUI;

	public BeeTabCloseButton(Component component, String aTitle, Icon aIcon, BeeTabbedPane pane) {

		this.setBorder(new EmptyBorder(5, 5, 5, 3));
		this.component = component;
		this.pane = pane;
		label = new JLabel(aTitle);
		label.setBorder(new EmptyBorder(0, 0, 0, 5));
		label.setIcon(aIcon);
		label.setFont(BeeUIUtils.getDefaultFont());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 0, 0, 5);
		add(label, gbc);
		if (component instanceof IBeeTitleUI) {
			basicUI = (IBeeTitleUI) component;
			basicUI.addTitleChangeListener(new BeeTabLabelListener(label, pane));
		}

		setOpaque(false);
		setLayout(new GridBagLayout());
		setVisible(true);
		this.setFocusable(false);
		boolean close = true;
		if (component instanceof BeeCloseable) {
			BeeCloseable closeable = (BeeCloseable) component;
			close = closeable.tabCloseable();
		}

		if (pane.isEditable() && close) {

			closingLabel = new JLabel(EMPTY_ICON);
			closingLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
			closingLabel.setIconTextGap(5);
			closingLabel.setText(" ");
			closingLabel.setHorizontalTextPosition(JLabel.LEFT);
			closingLabel.addMouseMotionListener(this);
			closingLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					int tabIndex = pane.indexOfComponent(component);
					pane.removeTabAt(tabIndex);
					pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (CLOSING_ICON_SELECTED != null) {
						closingLabel.setIcon(CLOSING_ICON_SELECTED);
						closingLabel.updateUI();
					}
					pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (CLOSING_ICON_SELECTED != null) {
						closingLabel.setIcon(CLOSING_ICON);
						closingLabel.updateUI();

					}
					pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
				}
			});
			gbc.insets = new Insets(3, 0, 0, 5);
			add(closingLabel, gbc);
			// this.label.addMouseListener(this);
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

	}

	private boolean selected = false;

	public void onSelected(boolean seleced) {
		this.selected = seleced;

		if (pane.isEditable() && closingLabel != null) {

			if (seleced) {
				closingLabel.setIcon(this.CLOSING_ICON);
			} else {
				closingLabel.setIcon(this.EMPTY_ICON);
			}
			closingLabel.updateUI();
		}
	}

	public void setError(boolean error) {
		label.setName(error ? "ERROR" : null);
	}

	public void setAlert(boolean alert) {
		label.setName(alert ? "ALERT" : null);
	}

	public boolean isError() {
		String name = label.getName();
		if (name == null) {
			return false;
		}
		if (name.equals("ERROR")) {
			return true;
		}
		return false;
	}

	public boolean isAlert() {
		String name = label.getName();
		if (name == null) {
			return false;
		}
		if (name.equals("ALERT")) {
			return true;
		}
		return false;
	}

	public boolean isModified() {
		return modified;
	}

	public void setTitle(String title) {
		this.label.setText(title);
		this.label.updateUI();
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		String text = label.getText();
		if (text == null) {
			return;
		}
		if (modified) {
			if (!text.startsWith("* ")) {
				text = "* " + text;
			}
		} else {
			if (text.startsWith("* ")) {
				text = text.substring(2, text.length());
			}
		}
		this.label.setText(text);
		this.label.updateUI();
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.closingLabel.setIcon(this.CLOSING_ICON);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.closingLabel.setIcon(this.CLOSING_ICON);
		pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
		pane.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!this.selected) {
			this.closingLabel.setIcon(this.EMPTY_ICON);
		}
		pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
		pane.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {

			pane.getComponentPopupMenu(this).show(this, e.getX(), e.getY());
		} else {
			if (e.getClickCount() == 2) {
				pane.maxmize();
				// EditorInnerEditor.startEdit(label, basicUI);
			} else {
				pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
			}
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		pane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, pane));
	}

}