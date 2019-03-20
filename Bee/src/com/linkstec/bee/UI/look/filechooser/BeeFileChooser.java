package com.linkstec.bee.UI.look.filechooser;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.icon.BeeIcon;

import sun.swing.WindowsPlacesBar;

public class BeeFileChooser extends JFileChooser implements PropertyChangeListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5295181658622104145L;
	private WindowsPlacesBar bar;
	private static File history = null;

	public BeeFileChooser(String lastDir) {
		super(lastDir);
		this.init();
	}

	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog d = super.createDialog(parent);
		if (d != null) {
			d.setName(BeeFileChooser.class.getName());
		}
		return d;
	}

	public BeeFileChooser() {
		this.init();
	}

	private void init() {
		this.addPropertyChangeListener(this);
		bar = this.lookupPlaceBar(this);
		if (bar != null) {
			int s = BeeUIUtils.getDefaultFontSize() / 4;
			bar.setPreferredSize(new Dimension(s * 35, s * 100));
			bar.setOpaque(false);
			bar.setBorder(new EtchedBorder());
		}
		this.make(this);

		this.addActionListener(this);
		if (history != null) {
			this.setCurrentDirectory(history.getParentFile());
		}
	}

	private WindowsPlacesBar lookupPlaceBar(JComponent comp) {
		int count = comp.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component c = comp.getComponent(i);
			if (c instanceof WindowsPlacesBar) {
				return (WindowsPlacesBar) c;
			} else {
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setOpaque(false);
					WindowsPlacesBar bar = this.lookupPlaceBar(jc);
					if (bar != null) {
						return bar;
					}
				}
			}
		}
		return null;
	}

	@Override
	public JComponent getAccessory() {

		JComponent comp = super.getAccessory();
		if (comp != null) {
			comp.setOpaque(false);
		}
		return comp;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(BeeConstants.BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		super.paint(g);
	}

	private void make(JComponent comp) {
		int a = BeeUIUtils.getDefaultFontSize();
		if (comp instanceof JToggleButton) {
			int s = a / 4;
			JToggleButton tog = (JToggleButton) comp;
			tog.setPreferredSize(new Dimension(s * 30, s * 30));
			tog.setSize(tog.getPreferredSize());
			tog.setMaximumSize(tog.getPreferredSize());
			Icon icon = tog.getIcon();

			if (icon instanceof BeeIcon) {

			} else if (icon instanceof ImageIcon) {
				tog.setIconTextGap(a / 3);

				ImageIcon imi = (ImageIcon) icon;
				Image img = imi.getImage();
				int w = img.getWidth(this);
				int h = img.getHeight(this);
				double nw = a * w / h;

				BeeIcon bicon = new BeeIcon(img, (int) nw, a);
				bicon.setTopMargin(a / 10);
				bicon.setLeftMargin(-a / 2);
				tog.setIcon(bicon);
				tog.setOpaque(false);
			}

		} else if (comp instanceof JButton) {
			JButton b = (JButton) comp;
			Icon icon = b.getIcon();
			if (icon == null) {
				b.setUI(new BeeButtonUI());
			} else {
				if (icon instanceof BeeIcon) {

				} else if (icon instanceof ImageIcon) {
					ImageIcon imi = (ImageIcon) icon;
					Image img = imi.getImage();
					BeeIcon bicon = new BeeIcon(img, a / 2, a / 2);
					bicon.setTopMargin(a / 10);
					b.setIcon(bicon);

					b.setOpaque(false);
				}
			}
		}
		int count = comp.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component c = comp.getComponent(i);

			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				if (jc instanceof JPanel) {
					jc.setOpaque(false);
				}
				this.make(jc);

			} else {
				if (c instanceof CellRendererPane) {
					CellRendererPane pane = (CellRendererPane) c;
					Component[] cs = pane.getComponents();
					for (Component cc : cs) {
						if (cc instanceof JComponent) {
							JComponent jc = (JComponent) cc;
							if (jc instanceof JPanel) {
								jc.setOpaque(false);
							}
							this.make(jc);
						} else {
							System.out.println(cc);
						}
					}
				}
				// System.out.println(c);
			}

		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		history = this.getSelectedFile();
	}

}
