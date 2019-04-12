package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ComputerLogicAnylizer.ParseExcetion;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxRectangle;

public class ComputerEditor {
	private JDialog dialog;
	private ComputerLogic logic;

	public ComputerEditor(ComputerLogic logic) {
		this.logic = logic;
		this.init();
		dialog.add(this.getEditor(logic.getExpression()), BorderLayout.CENTER);
		dialog.setVisible(true);
	}

	public ComputerEditor(List<Object> list) {

		this.init();
		dialog.add(this.getEditor(list), BorderLayout.CENTER);
		dialog.setVisible(true);
	}

	private void init() {
		dialog = new JDialog(Application.FRAME, "複雑計算");
		dialog.setAlwaysOnTop(true);
		dialog.setLayout(new BorderLayout());
		BEditorExplorer explore = Application.getInstance().getBasicSpective().getExplore();
		Point p = explore.getLocationOnScreen();
		int height = explore.getHeight() * 2 / 3;
		int width = (int) (p.getX() * 2 / 3);
		Point ep = Application.getInstance().getEditor().getLocationOnScreen();
		int x = (int) (ep.getX() + p.getX() / 6);
		int y = (int) (ep.getY() + explore.getHeight() / 6);
		dialog.setBounds(new Rectangle(x, y, width, height));

	}

	private Display display;

	public JComponent getEditor(BExpression ex) {
		JPanel panel = this.makeEditor();
		List<Object> list = ComputerLogicAnylizer.parseExpression(ex);
		if (list != null) {
			for (Object obj : list) {
				if (obj instanceof JComponent) {
					JComponent c = (JComponent) obj;
					display.add(c);
				}
			}
		}
		display.updateUI();
		return panel;
	}

	public JComponent getEditor(List<Object> list) {
		JPanel panel = this.makeEditor();
		if (list != null) {

			for (Object obj : list) {

				if (obj instanceof BValuable) {
					BValuable bv = (BValuable) obj;
					display.setValue(bv);
				} else if (obj instanceof BLogiker) {
					BLogiker logiker = (BLogiker) obj;
					LogickerLabel l = new LogickerLabel();
					l.setLogiker(logiker);
					display.addCell(l);
				}
			}
		}

		return panel;
	}

	private JPanel makeEditor() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);

		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);
		panel.add(this.getButtons(), BorderLayout.SOUTH);

		display = new Display(this.message);
		JScrollPane pane = new JScrollPane(display);
		pane.setPreferredSize(new Dimension(s * 40, s * 10));
		panel.add(pane, BorderLayout.NORTH);

		ValuesPanel vp = new ValuesPanel(new ArrayList<BValuable>(), display);
		panel.add(vp, BorderLayout.CENTER);

		return panel;
	}

	int s = BeeUIUtils.getDefaultFontSize();
	private JLabel message;

	private JPanel getButtons() {

		JPanel panel = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4209529585243802422L;

			@Override
			public Insets getInsets() {
				return new Insets(s, s, s, s);
			}

		};

		message = new JLabel();
		panel.setOpaque(false);

		FlowLayout layout = new FlowLayout();
		layout.setHgap(s * 2);
		layout.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(layout);

		JButton button = new JButton("キャンセル");
		JButton ok = new JButton("OK");

		panel.add(message);
		message.setPreferredSize(new Dimension(s * 35, s * 4));
		panel.add(ok);
		panel.add(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});

		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BExpression ex = display.getExpression();
				if (ex != null) {
					if (logic == null) {
						ComputerLogic clogic = new ComputerLogic(null);
						clogic.setExpression(ex);
						Application.getInstance().getBasicSpective().getPropeties().setTarget(clogic, clogic.getPath());
					} else {
						logic.setExpression(ex);
						Application.getInstance().getBasicSpective().getPropeties().updateUI();
					}
				}
				dialog.dispose();

			}

		});

		return panel;

	}

	public static class Display extends JPanel implements KeyListener, ITranferRecevier, MouseMotionListener {

		private static final long serialVersionUID = -8098230113993983039L;
		private int s = BeeUIUtils.getDefaultFontSize();
		private JLabel message;

		public Display(JLabel message) {
			this.message = message;
			this.setBackground(Color.LIGHT_GRAY);
			this.setBorder(new EtchedBorder());
			this.setPreferredSize(new Dimension(s * 40, -1));
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			this.setLayout(flow);
			this.addKeyListener(this);
			this.setRequestFocusEnabled(true);
			this.setFocusable(true);
			this.setTransferHandler(new ValueTransferHandler());
			this.addMouseMotionListener(this);
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					Component[] comps = getComponents();

					for (Component c : comps) {
						if (c instanceof DisplayCell) {
							DisplayCell d = (DisplayCell) c;
							if (d.isSelected()) {
								d.setSelected(false);
							}
						}

					}
					repaint();
				}

			});
		}

		public void localMove(int hashCode) {
			Component[] comps = this.getComponents();

			for (Component c : comps) {
				if (c instanceof DisplayCell) {
					DisplayCell d = (DisplayCell) c;
					if (c.hashCode() == hashCode) {
						int location = this.getMouseLocationIndex();
						if (location >= 0) {
							this.add(d, location);
							this.updateUI();
						}
						break;
					}
				}
			}
		}

		public void addCell(CellLabel cell) {
			if (cell instanceof ParentizedLabel) {
				int left = -1;
				int right = -1;
				Component[] comps = this.getComponents();
				int index = 0;
				for (Component c : comps) {
					if (c instanceof DisplayCell) {
						DisplayCell d = (DisplayCell) c;
						if (d.isSelected()) {
							if (left == -1) {
								left = index;
							}
							right = index;

						}
					}
					index++;
				}

				if (left >= 0 && right >= 0) {
					ParentizedDisplayCell pl = new ParentizedDisplayCell(ParentizedDisplayCell.TYPE_LEFT);
					ParentizedDisplayCell pr = new ParentizedDisplayCell(ParentizedDisplayCell.TYPE_RIGHT);
					this.add(pl, left);
					this.add(pr, right + 2);
				}
			} else {
				DisplayCell d = new DisplayCell();
				d.setValue(cell.getValue());
				String s = "<html>";
				String value = "";
				if (cell instanceof LogickerLabel) {
					BLogiker l = (BLogiker) cell.getValue();
					value = l.getLogicName();
					value = value.replace(">", "&gt;");
					value = value.replace("<", "&lt;");
					s = s + value + "</html>";
				} else {
					value = cell.getText();
					s = value;
				}

				d.setText(s);

				Component[] comps = this.getComponents();
				int location = comps.length;
				int index = 0;
				for (Component c : comps) {
					if (c instanceof DisplayCell) {
						DisplayCell dc = (DisplayCell) c;
						if (dc.isSelected()) {
							location = index;
							break;
						}
					}
					index++;
				}
				this.add(d, location);
			}
			this.updateUI();
		}

		@Override
		public void updateUI() {

			Component[] comps = this.getComponents();
			int max = comps.length;
			int maxHeight = 0;

			for (Component c : comps) {
				if (c instanceof DisplayCell) {
					max = Math.max(max, c.getY() + c.getHeight());
					maxHeight = Math.max(maxHeight, c.getHeight());
				}

			}
			if (this.getSize().getHeight() < max + maxHeight) {
				this.setSize(this.getWidth(), max + maxHeight);

			}

			this.setPreferredSize(this.getSize());
			super.updateUI();
			JScrollPane pane = null;
			Container c = this;
			while (c != null) {
				if (c instanceof JScrollPane) {
					pane = (JScrollPane) c;
					break;
				} else {
					c = c.getParent();
				}
			}
			if (pane != null) {

				pane.revalidate();
			}
			this.makeExpression();
		}

		public void deleteCell(DisplayCell cell) {
			this.remove(cell);
			this.updateUI();
		}

		public BExpression getExpression() {
			List<DisplayCell> cells = new ArrayList<DisplayCell>();
			Component[] comps = this.getComponents();

			for (Component c : comps) {
				if (c instanceof DisplayCell) {
					DisplayCell cell = (DisplayCell) c;
					cells.add(cell);
				}

			}
			try {
				return ComputerLogicAnylizer.AnylizeCells(cells);
			} catch (ParseExcetion e) {
				return null;
			}
		}

		private void makeExpression() {

			List<DisplayCell> cells = new ArrayList<DisplayCell>();
			Component[] comps = this.getComponents();

			for (Component c : comps) {
				if (c instanceof DisplayCell) {
					DisplayCell cell = (DisplayCell) c;
					cells.add(cell);
				}

			}
			try {
				String s = ComputerLogicAnylizer.getString(cells);
				if (this.message != null) {
					this.message.setForeground(Color.BLACK);
					this.message.setText("<html>" + s + "</html>");
					message.updateUI();
				}
			} catch (ParseExcetion e) {
				String s = e.getMessage();
				if (this.message != null) {
					this.message.setForeground(Color.RED);
					this.message.setText("<html>" + s + "</html>");
					message.updateUI();
				}
				// e.printStackTrace();
			}
		}

		public void onSelected(DisplayCell cell, boolean shift) {
			if (shift) {
				return;
			}
			if (cell.isSelected()) {
				Component[] comps = this.getComponents();
				for (Component c : comps) {
					if (c instanceof DisplayCell) {
						DisplayCell d = (DisplayCell) c;
						if (!d.equals(cell)) {
							d.setSelected(false);
							d.repaint();
						}
					}
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DELETE) {
				Component[] comps = this.getComponents();
				for (Component c : comps) {
					if (c instanceof DisplayCell) {
						DisplayCell d = (DisplayCell) c;
						if (d.isSelected()) {
							this.deleteCell(d);
						}
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void setFireEvent(boolean b) {

		}

		@Override
		public void setText(String name) {

		}

		public int getMouseLocationIndex() {
			Point p = this.getMousePosition();
			Component[] comps = this.getComponents();

			if (p != null) {

				Component c = this.getComponentAt(p);
				if (c instanceof Display) {
					c = null;
				}
				int count = 0;
				while (count < 100 && c == null) {
					p = new Point(p.x - count * s, p.y);
					c = this.getComponentAt(p);
					if (c instanceof Display) {
						c = null;
					}
					count++;
				}
				if (c != null) {
					int index = 0;
					for (Component comp : comps) {
						if (comp.equals(c)) {

							return index + 1;

						}
						index++;
					}

				}
			}
			return -1;
		}

		@Override
		public void setValue(BValuable value) {
			DisplayCell d = new DisplayCell();
			d.setValue(value);
			d.setText(BValueUtils.createValuable(value, false));

			Point p = this.getMousePosition();
			Component[] comps = this.getComponents();
			int location = comps.length;
			if (p != null) {
				int mi = this.getMouseLocationIndex();
				if (mi >= 0) {
					location = mi;
				}
			} else {

				int index = 0;
				for (Component c : comps) {
					if (c instanceof DisplayCell) {
						DisplayCell dc = (DisplayCell) c;
						if (dc.isSelected()) {
							location = index;
							break;
						}
					}
					index++;
				}
			}
			this.add(d, location);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// System.out.println(e.getPoint());
			// System.out.println(e.getSource());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// System.out.println(e.getPoint());
			// System.out.println(e.getSource());
		}
	}

	public static class DisplayCell extends JLabel implements MouseListener, KeyListener, MouseMotionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7528829005170925637L;
		private Object value;
		int s = BeeUIUtils.getDefaultFontSize();
		private boolean selected = false;

		public DisplayCell() {
			this.setFont(this.getFont().deriveFont((float) s * 2));
			this.addKeyListener(this);
			this.addMouseListener(this);
			this.setRequestFocusEnabled(true);
			this.setFocusable(true);
			this.addMouseMotionListener(this);
			DragGestureListener dragGestureListener = new DragGestureListener() {
				/**
				 * 
				 */
				public void dragGestureRecognized(DragGestureEvent e) {
					BNode node = new BNode();
					mxRectangle bounds = (mxGeometry) node.getGeometry().clone();
					node.addUserAttribute("LOCAL_ID", getHashCode());
					BeeTransferable t = new BeeTransferable(new Object[] { node }, bounds);
					e.startDrag(null, getImage(), new Point(), t, null);

				}

			};
			DragSource dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGestureListener);
		}

		public int getHashCode() {
			return this.hashCode();
		}

		private Image getImage() {

			Image img = this.createImage(this.getWidth(), this.getHeight());
			Graphics g = img.getGraphics();
			this.paint(g);
			return img;
		}

		@Override
		public Insets getInsets() {
			return new Insets(s, s, s, s);
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(BeeConstants.BACKGROUND_COLOR);
			g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
			if (selected) {
				Graphics2D g2d = (Graphics2D) g;
				BasicStroke bs = new BasicStroke(s / 5);
				g2d.setStroke(bs);
				g.setColor(Color.GREEN);
				g.drawRect(0, 0, this.getWidth(), this.getHeight());
			}
			super.paint(g);
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.selected = !selected;
			Display display = (Display) this.getParent();
			display.onSelected(this, e.isShiftDown() || e.isControlDown());
			this.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (this.selected) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DELETE) {
					Display display = (Display) this.getParent();
					display.deleteCell(this);

				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

	}

	public static class ParentizedDisplayCell extends DisplayCell {

		/**
		 * 
		 */
		private static final long serialVersionUID = -887451938942733275L;
		public static final int TYPE_LEFT = 1;
		public static final int TYPE_RIGHT = 2;
		private int type;

		public ParentizedDisplayCell(int type) {
			this.type = type;
			if (type == TYPE_LEFT) {
				this.setText("(");
			} else {
				this.setText(")");
			}
		}

		public int getType() {
			return this.type;
		}

	}

	public static class ValuesPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3615671313675373722L;
		private Display display;

		public ValuesPanel(List<BValuable> values, Display display) {
			this.setOpaque(false);
			this.setBorder(new EtchedBorder());
			this.display = display;
			GridLayout layout = new GridLayout(0, 5);
			// GridBagConstraints gbc=new GridBagConstraints();

			int s = BeeUIUtils.getDefaultFontSize() / 2;

			layout.setHgap(s);
			layout.setVgap(s);
			this.setLayout(layout);
			this.addLogikers();

			ParentizedLabel p = new ParentizedLabel();
			p.setDisplay(display);
			this.add(p);

			this.addValues(values);

		}

		private int s = BeeUIUtils.getDefaultFontSize();

		@Override
		public Insets getInsets() {
			return new Insets(s, s, s, s);
		}

		public Display getDisplay() {
			return display;
		}

		public void setDisplay(Display display) {
			this.display = display;
		}

		public void addLogikers() {
			BLogiker[] logikers = BLogiker.values();
			for (BLogiker logiker : logikers) {
				if (logiker.getType().equals(BLogiker.TYPE_OBJECT) || logiker.getType().equals(BLogiker.TYPE_BOOLEAN)) {
					LogickerLabel label = new LogickerLabel();
					label.setLogiker(logiker);
					label.setText(
							"<html>&nbsp;&nbsp;" + logiker.toString() + "[" + logiker.getLogicName() + "]</html>");
					label.setDisplay(display);
					this.add(label);
				}
			}

		}

		public void addValues(List<BValuable> values) {
			for (BValuable value : values) {
				ValueLabel label = new ValueLabel();
				label.setValue(value);
				label.setText("<html>" + BValueUtils.createValuable(value, false) + "</html>");
				label.setDisplay(display);
				this.add(label);
			}
		}
	}

	public static abstract class CellLabel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2765752245651325656L;
		protected Display display;

		public CellLabel() {
			this.setOpaque(false);
			this.setBorder(null);

			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					display.addCell(CellLabel.this);
				}

			});

		}

		private int s = BeeUIUtils.getDefaultFontSize();

		@Override
		public Insets getInsets() {
			return new Insets(s, s, s, s);
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(BeeConstants.BACKGROUND_COLOR);
			g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
			super.paint(g);
		}

		public void setDisplay(Display display) {
			this.display = display;
		}

		public abstract Object getValue();

		public abstract void setText(String text);

		public abstract String getText();

	}

	public static class LogickerLabel extends CellLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7593311601532068358L;
		private BLogiker logiker;
		private JLabel label = new JLabel();

		public LogickerLabel() {
			this.add(label);
		}

		@Override
		public BLogiker getValue() {
			return logiker;
		}

		public void setLogiker(BLogiker logiker) {
			this.logiker = logiker;
		}

		@Override
		public void setText(String text) {
			this.label.setText(text);
		}

		@Override
		public String getText() {
			return this.label.getText();
		}

	}

	public static class ValueLabel extends CellLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7593311601532068358L;
		private BValuable value;
		private JLabel label = new JLabel();

		public ValueLabel() {
			this.add(label);
		}

		@Override
		public BValuable getValue() {
			return value;
		}

		public void setValue(BValuable value) {
			this.value = value;
		}

		@Override
		public void setText(String text) {
			this.label.setText(text);
		}

		@Override
		public String getText() {
			return this.label.getText();
		}

	}

	public static class ParentizedLabel extends CellLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7593311601532068358L;
		private BValuable value;
		private JLabel label = new JLabel();

		public ParentizedLabel() {
			label.setText("()");
			this.add(label);
		}

		@Override
		public BValuable getValue() {
			return value;
		}

		public void setValue(BValuable value) {
			this.value = value;
		}

		@Override
		public void setText(String text) {
			this.label.setText(text);
		}

		@Override
		public String getText() {
			return this.label.getText();
		}

	}

}
