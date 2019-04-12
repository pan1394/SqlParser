package com.linkstec.bee.UI.spective.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPropertyDialog;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ComputerEditor;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ComputerLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ExpressionLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.JudgeLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BJudgeNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;

public class BasicLogicProperties extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6780838776457605734L;

	public BasicLogicProperties() {
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());

	}

	public void setTarget(Object obj, BPath path) {
		this.setTransfer(path);
		this.removeAll();
		if (obj == null) {
			this.updateUI();
		}
		if (obj instanceof BasicEdit) {
			BasicEdit edit = (BasicEdit) obj;
			JPanel panel = this.makeLogicList(edit, path);
			this.add(panel, BorderLayout.CENTER);
			this.updateUI();
		} else if (obj instanceof BPropertyDialog) {
			BPropertyDialog dialog = (BPropertyDialog) obj;
			this.add(dialog, BorderLayout.CENTER);
			this.updateUI();
		} else if (obj instanceof BLogic) {
			BLogic logic = (BLogic) obj;
			LogicPanel panel = new LogicPanel(logic, false);
			this.add(panel, BorderLayout.CENTER);
			this.updateUI();
		}
	}

	private JPanel makeLogicList(BasicEdit edit, BPath path) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(box);

		BasicEditNode root = (BasicEditNode) edit.getRoot();

		this.scanNode(root, panel, path);
		String name = panel.getName();
		if (name != null && name.startsWith("A") && name.length() > 1) {
			int s = BeeUIUtils.getDefaultFontSize();
			panel.setBorder(new TitledBorder(new EmptyBorder(s * 3, 0, 0, 0), "ここをドラッグし、ロジックを追加", TitledBorder.CENTER,
					TitledBorder.CENTER));
			panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else {
			panel.setBorder(null);
			panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		return panel;
	}

	private void scanNode(BasicEditNode node, JPanel panel, BPath path) {
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			BasicEditNode child = (BasicEditNode) node.getChildAt(i);
			if (child.isChecked()) {
				Object obj = child.getUserObject();

				if (obj instanceof BLogic) {
					String name = panel.getName();

					BLogic logic = (BLogic) obj;
					LogicPanel cell = new LogicPanel(logic, name != null && name.startsWith("A"));
					panel.add(cell);

					if (name != null && name.startsWith("A")) {
						panel.setName(name + "A");
					} else {
						panel.setName("A");
					}

				} else if (obj instanceof List) {
					List<Object> list = (List<Object>) obj;
					LogicPanel cell = new LogicPanel(list, path);
					panel.add(cell);
				}
			}
			this.scanNode(child, panel, path);
		}

	}

	private BJudgeNode makeTransferAll(BPath path) {
		JudgeLogic judge = new JudgeLogic(path);
		BJudgeNode node = (BJudgeNode) judge.getPath().getCell();
		judge.getPath().setParent(null);

		int count = this.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component c = this.getComponent(i);
			if (c instanceof JPanel) {
				JPanel panel = (JPanel) c;
				int size = panel.getComponentCount();
				for (int j = 0; j < size; j++) {
					Component p = panel.getComponent(j);
					if (p instanceof LogicPanel) {
						LogicPanel logic = (LogicPanel) p;
						BLogic l = logic.getLogic();

						if (logic.getLogiker() != null) {
							judge.getList().add(logic.getLogiker());
						}
						judge.getList().add(l);
					}
				}
			}
		}
		return node;
	}

	protected void setTransfer(BPath path) {
		setTransferHandler(new TransferHandler() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3808525782242227637L;

			public boolean canImport(JComponent comp, DataFlavor[] flavors) {
				return false;
			}
		});
		DragGestureListener dragGestureListener = new DragGestureListener() {
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e) {

				BNode node = makeTransferAll(path);

				mxRectangle bounds = (mxGeometry) node.getGeometry().clone();
				BeeTransferable t = new BeeTransferable(new Object[] { node }, bounds);
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);

			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGestureListener);

	}

	public static class LogicPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8678917242942865421L;
		private BLogic logic;
		private int s = BeeUIUtils.getDefaultFontSize() / 2;
		private BLogiker logiker;

		public LogicPanel(List<Object> list, BPath path) {
			logic = new ExpressionLogic(path, list);
			this.init(false);
		}

		public LogicPanel(BLogic logic, boolean showlogic) {
			this.logic = logic;
			this.init(showlogic);
		}

		public BLogiker getLogiker() {
			return this.logiker;
		}

		private void init(boolean showlogic) {
			JPanel panel = new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {

					Graphics2D g2d = (Graphics2D) g;
					GradientPaint grdp = new GradientPaint(0, 0, BeeConstants.BACKGROUND_COLOR, 0, getHeight() / 2,
							Color.WHITE);
					g2d.setPaint(grdp);
					g2d.fillRect(0, 0, getWidth(), getHeight());

					super.paint(g);
				}
			};
			panel.setOpaque(false);

			this.setLayout(new BorderLayout());
			this.add(panel, BorderLayout.CENTER);

			if (logic instanceof JudgeLogic) {

				JPanel buttonPanel = this.makeComputerLink(logic);
				this.add(buttonPanel, BorderLayout.SOUTH);
			}

			this.setOpaque(false);
			this.setTransfer(panel);
			String disc = logic.getDesc();

			panel.setLayout(new BorderLayout());

			JPanel row = new JPanel();
			row.setOpaque(false);

			FlowLayout f = new FlowLayout();
			f.setAlignment(FlowLayout.LEFT);
			row.setLayout(f);

			if (showlogic) {

				this.logiker = BLogiker.LOGICAND;

				JComboBox<BLogiker> box = new JComboBox<BLogiker>();
				BLogiker[] values = { BLogiker.LOGICAND, BLogiker.LOGICOR, BLogiker.PLUS };
				for (BLogiker logiker : values) {
					box.addItem(logiker);
					if (logiker.equals(BLogiker.LOGICAND)) {
						box.setSelectedItem(logiker);
					}
				}
				box.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						logiker = (BLogiker) e.getItem();
					}

				});
				row.add(box);
			}

			JLabel label = new JLabel("<html>" + disc + "</html>");
			label.setPreferredSize(new Dimension(s * 40, s * 5));
			label.setIcon(BeeConstants.ACTION_ICON);
			row.add(label);

			panel.add(row, BorderLayout.NORTH);

			JComponent editor = logic.getEditor();
			if (editor != null) {
				panel.add(editor, BorderLayout.CENTER);
			}
		}

		private JPanel makeComputerLink(BLogic logic) {
			JButton button = new JButton("複雑計算");
			button.setForeground(Color.BLUE);
			button.setOpaque(false);
			button.setBorder(null);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setOpaque(false);
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			buttonPanel.setLayout(flow);

			if (logic instanceof JudgeLogic) {
				buttonPanel.add(button);
				JudgeLogic ex = (JudgeLogic) logic;
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (ex instanceof ComputerLogic) {
							ComputerLogic c = (ComputerLogic) ex;
							new ComputerEditor(c);
						} else {
							new ComputerEditor(ex.getList());
						}

					}

				});
			}
			return buttonPanel;

		}

		@Override
		public Insets getInsets() {
			return new Insets(s, s, s, s);
		}

		public BLogic getLogic() {
			return this.logic;
		}

		public mxICell getTransferValue() {
			logic.getPath().setParent(null);

			if (Application.getInstance().getBasicSpective().getSelection().getEditor() instanceof BTableSheet) {
				return (BNode) logic.getPath().getCell();
			}

			if (logic instanceof ExpressionLogic) {
				ExpressionLogic ex = (ExpressionLogic) logic;
				Hashtable<Integer, BLogiker> hash = ex.getLogikers();
				Collection<BLogiker> values = hash.values();
				Iterator<BLogiker> ite = values.iterator();

				boolean isObject = false;
				while (ite.hasNext()) {
					BLogiker l = ite.next();
					if (l.getType().equals(BLogiker.TYPE_OBJECT)) {
						isObject = true;
						break;
					}
				}
				if (isObject) {
					BValuable exObj = ex.getExpression(null);
					if (exObj instanceof mxICell) {
						mxICell cell = (mxICell) exObj;
						cell.getGeometry().setRelative(false);
						return cell;
					}

				}
			}

			return (BNode) logic.getPath().getCell();
		}

		protected void setTransfer(JPanel panel) {
			panel.setTransferHandler(new TransferHandler() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3808525782242227637L;

				public boolean canImport(JComponent comp, DataFlavor[] flavors) {
					return true;
				}
			});
			DragGestureListener dragGestureListener = new DragGestureListener() {
				/**
				 * 
				 */
				public void dragGestureRecognized(DragGestureEvent e) {

					mxICell node = getTransferValue();

					mxRectangle bounds = (mxGeometry) node.getGeometry().clone();
					BeeTransferable t = new BeeTransferable(new Object[] { node }, bounds);
					e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);

				}

			};

			DragSource dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGestureListener);

		}

	}

}
