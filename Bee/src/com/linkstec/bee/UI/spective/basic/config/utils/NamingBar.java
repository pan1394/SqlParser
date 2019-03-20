package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.NamingType;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;

public class NamingBar extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3343783539266817366L;
	private boolean dragging = false;
	private NamingModel model = new NamingModel();
	private NamingPanel panel;

	public NamingBar(NamingPanel panel, boolean dotted) {
		this.panel = panel;
		this.model.setDotted(dotted);
		this.setBackground(Color.LIGHT_GRAY);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		this.setLayout(layout);
		int s = BeeUIUtils.getDefaultFontSize();
		this.setPreferredSize(new Dimension(s * 50, s * 2));
		this.setTransferHandler(new NamingTransferHandler());
		this.addMouseListener(this);
	}

	public NamingPanel getPanel() {
		return panel;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (dragging) {
			Graphics2D gg = (Graphics2D) g;

			int s = BeeUIUtils.getDefaultFontSize();
			gg.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			g.setColor(Color.BLUE.darker());
			g.drawRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

	public void startDropDrag() {
		this.dragging = true;
		this.repaint();
	}

	public void endDrop() {
		this.dragging = false;
		this.repaint();

		Container p = this.getParent();

		if (p instanceof NamingPanel) {
			NamingPanel panel = (NamingPanel) p;
			panel.updateExample();
		}
	}

	public NamingModel getModel() {
		this.model.getList().clear();

		Component[] comps = this.getComponents();
		for (Component c : comps) {
			if (c instanceof NameCell) {
				NameCell cell = (NameCell) c;
				Object obj = cell.getUserObject();
				if (obj instanceof NamingType) {
					NamingType type = (NamingType) obj;
					this.model.getList().add(type);
				}
			}
		}
		return this.model;
	}

	public void setModel(NamingModel model) {
		this.model = model;

		List<NamingType> list = model.getList();
		for (NamingType type : list) {
			NameCell label = new NameCell(this);
			label.setUserObject(type);

			this.add(label);
		}

	}

	public static class NamingTransferHandler extends TransferHandler {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5520076116990708115L;

		public NamingTransferHandler() {

		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			NamingBar panel = (NamingBar) comp;
			try {
				NamingType object = (NamingType) t.getTransferData(DataFlavor.stringFlavor);
				NameCell label = new NameCell(panel);
				label.setUserObject(object);

				panel.add(label);
				panel.endDrop();

				Rectangle bounds = label.getBounds();
				if (bounds.x + bounds.height > panel.getHeight()) {
					panel.setSize(new Dimension((int) panel.getPreferredSize().getWidth(), bounds.x + bounds.height + 2));
				}
				panel.updateUI();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			super.exportAsDrag(comp, e, action);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			NamingBar panel = (NamingBar) source;

			super.exportDone(source, data, action);
			panel.endDrop();
		}

		@Override
		public boolean canImport(TransferSupport support) {
			NamingBar panel = (NamingBar) support.getComponent();
			panel.startDropDrag();
			return true;
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return true;
		}
	}

	public static class NameCell extends JPanel implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2622825854128262769L;

		private NamingBar bar;
		private Object userObject;
		private JTextField text = new JTextField();

		private JButton delete = new JButton() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6683978436851589739L;

			@Override
			public void paint(Graphics g) {
				ImageIcon icon = (ImageIcon) this.getIcon();
				Image img = icon.getImage();

				int x = (this.getWidth() - img.getWidth(this)) / 2;
				int y = (this.getHeight() - img.getHeight(this)) / 2;
				g.drawImage(img, x, y, this);
			}

		};

		public NameCell(NamingBar bar) {
			this.bar = bar;
			int s = BeeUIUtils.getDefaultFontSize();
			this.setBorder(new EmptyBorder(s / 3, s, s / 3, s));
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			this.setLayout(flow);
			text.setOpaque(false);
			text.setEditable(false);
			text.setBorder(null);
			text.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {

				}

				@Override
				public void focusLost(FocusEvent e) {
					if (userObject instanceof NamingType) {
						NamingType type = (NamingType) userObject;
						if (type.getType() == NamingType.TYPE_FIXED) {
							type.setName(text.getText());
							type.setExample(text.getText());
							bar.getPanel().updateExample();
						}
					}

				}

			});
			text.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {

				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						// if (userObject instanceof NamingType) {
						// NamingType type = (NamingType) userObject;
						// if (type.getType() == NamingType.TYPE_FIXED) {
						// type.setName(text.getText());
						// }
						// }
						text.transferFocus();
					}

				}

				@Override
				public void keyReleased(KeyEvent e) {

				}

			});
			this.add(text);

			delete.setIcon(BeeConstants.DELETE_ICON);
			delete.setOpaque(false);
			delete.addActionListener(this);
			delete.setBorder(null);

			this.add(delete);
			this.setOpaque(false);
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(Color.white);
			RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), this.getHeight(), this.getHeight());
			Graphics2D gg = (Graphics2D) g;
			gg.fill(rect);
			super.paint(g);
		}

		@Override
		public Insets getInsets() {
			int s = BeeUIUtils.getDefaultFontSize();
			return new Insets(0, s, 0, s);
		}

		public Object getUserObject() {
			return userObject;
		}

		public void setUserObject(Object userObject) {
			this.userObject = userObject;
			this.text.setText(userObject.toString());
			if (userObject instanceof NamingType) {
				NamingType type = (NamingType) userObject;
				if (type.getType() == NamingType.TYPE_FIXED) {
					text.setEditable(true);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			bar.remove(this);
			bar.getPanel().updateExample();
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.dragging = false;
		this.repaint();

	}

}
