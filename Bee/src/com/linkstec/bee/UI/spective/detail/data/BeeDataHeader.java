package com.linkstec.bee.UI.spective.detail.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.table.BeeTable;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.spective.detail.BeeDataSheet;
import com.linkstec.bee.UI.spective.utils.ClassPopup;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BValuable;

public class BeeDataHeader extends JPanel implements Border {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1571056921803185685L;
	private Insets insets = new Insets(0, 0, 0, 0);
	private BeeDataModel model;

	// private JTextField t;

	// private int fontSize = BeeUIUtils.getDefaultFontSize();
	// private Color color = Color.decode("#" + BeeConstants.ELEGANT_BLUE_COLOR);
	private BeeDataSheet pane;
	private JPanel rowsArea = new JPanel();
	private JPanel buttonArea = new JPanel();

	public BeeDataHeader(BeeDataModel model, BeeDataSheet pane) {

		this.model = model;
		this.pane = pane;
		int s = BeeUIUtils.getDefaultFontSize() / 2;

		this.setBorder(new EmptyBorder(s * 2, s * 4, s * 2, s));
		this.setBackground(BeeTable.backgroundColor);
		this.setLayout(new BorderLayout());

		BoxLayout layout = new BoxLayout(rowsArea, BoxLayout.Y_AXIS);
		rowsArea.setLayout(layout);
		rowsArea.setOpaque(false);

		BoxLayout blayout = new BoxLayout(buttonArea, BoxLayout.Y_AXIS);

		buttonArea.setOpaque(false);
		buttonArea.setLayout(blayout);

		this.add(rowsArea, BorderLayout.CENTER);
		this.add(buttonArea, BorderLayout.EAST);

		this.makePackage();
		this.makeNames();
		this.makeSuperClass();
		this.makeInterfaces();
		this.makeButtons();

	}

	JButton addInterface, addSuper;

	private void makeButtons() {

		int s = BeeUIUtils.getDefaultFontSize() / 2;

		if (addInterface == null) {
			addInterface = new JButton("インターフェース追加");
			addInterface.setBorder(new EmptyBorder(s, s, s, s));

			addInterface.setUI(new BeeButtonUI());
			addInterface.setPreferredSize(new Dimension(s * 20, (int) (s * 2)));
			addInterface.setSize(addInterface.getPreferredSize());
			addInterface.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ComplexNode node = new ComplexNode();
					node.setBClass(
							CodecUtils.getClassFromJavaClass(pane.getProject(), Cloneable.class.getName()).cloneAll());
					model.addInterface(node);
					makeInterfaces();
					elementChanged();

				}

			});
			this.buttonArea.add(addInterface);

			JLabel space = new JLabel("");
			space.setBorder(new EmptyBorder(s, s, s, s));
			space.setPreferredSize(new Dimension(0, s));
			space.setSize(new Dimension(0, s));
			buttonArea.add(space);
		}
		if (model.getSuperClass() == null) {

			if (addSuper == null) {
				addSuper = new JButton("親クラス追加");
				addSuper.setBorder(new EmptyBorder(s, s, s, s));
				addSuper.setUI(new BeeButtonUI());
				addSuper.setPreferredSize(new Dimension(s * 20, (int) (s * 2)));
				addSuper.setSize(addSuper.getPreferredSize());
				addSuper.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ComplexNode node = new ComplexNode();
						node.setBClass(CodecUtils.BObject().cloneAll());
						model.setSuperClass(node);
						makeSuperClass();
						elementChanged();
					}

				});
			}
			this.buttonArea.add(addSuper);

		} else {
			if (this.addSuper != null && this.addSuper.getParent() != null) {
				this.buttonArea.remove(this.addSuper);
			}
		}

		buttonArea.setPreferredSize(new Dimension(s * 30, buttonArea.getPreferredSize().height));
	}

	private void elementChanged() {

		this.makeButtons();
		this.updateUI();
		this.setSize(getPreferredSize());
		this.revalidate();
		pane.revalidate();
		pane.setModified(true);
	}

	private void makeSuperClass() {
		if (model.getSuperClass() != null) {

			JPanel sclass = this.makeRow("親クラス", model.getSuperClass().getBClass().getQualifiedName(),
					BeeConstants.METHOD_ICON, new ClassPopup.DTextAction() {

						@Override
						public void changed(String text, JTextField t) {

						}

						@Override
						public void valueChanged(BValuable value) {
							model.setSuperClass(value);

						}

						@Override
						public boolean isType() {

							return true;
						}
					}, new BCall() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 61528014532021714L;

						@Override
						public void call() {
							removeById("SUPER_CLASS", rowsArea);
							model.setSuperClass(null);
							elementChanged();
						}

					}

			);
			sclass.setName("SUPER_CLASS");
			rowsArea.add(sclass);
		}
	}

	private void makeInterfaces() {

		int count = rowsArea.getComponentCount();
		for (int i = count - 1; i >= 0; i--) {
			Component child = rowsArea.getComponent(i);
			String name = child.getName();
			if (name != null && name.startsWith("INTERFACE")) {
				rowsArea.remove(child);
			}
		}

		List<BValuable> inters = model.getInterfaces();
		for (BValuable inter : inters) {
			String id = "INTERFACE" + System.currentTimeMillis();
			JPanel interf = this.makeRow("インターフェース", inter.getBClass().getQualifiedName(), BeeConstants.METHOD_ICON,
					new ClassPopup.DTextAction() {

						@Override
						public void changed(String text, JTextField t) {

						}

						@Override
						public void valueChanged(BValuable value) {
							model.getInterfaces().remove(inter);
							model.getInterfaces().add(value);
							elementChanged();
						}

						@Override
						public boolean isType() {
							return true;
						}
					}, new BCall() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 61528014532021714L;

						@Override
						public void call() {
							model.getInterfaces().remove(inter);
							removeById(id, rowsArea);
						}

					}

			);
			interf.setName(id);
			rowsArea.add(interf);
		}

	}

	private void removeById(String id, Container parent) {
		int count = parent.getComponentCount();
		for (int i = count - 1; i >= 0; i--) {
			Component child = parent.getComponent(i);
			String name = child.getName();
			if (name != null && name.equals(id)) {
				parent.remove(child);
				elementChanged();
				break;
			}
		}
	}

	private void makePackage() {
		JPanel pack = this.makeRow("パッケージ", model.getPackage(), BeeConstants.EXPLORE_FILE_TILE_ICON,
				new ClassPopup.DTextAction() {

					@Override
					public void changed(String text, JTextField t) {
						model.setPackage(text);
					}

					@Override
					public void valueChanged(BValuable value) {

					}

					@Override
					public boolean isType() {
						return false;
					}
				}, null);
		rowsArea.add(pack);
	}

	private void makeNames() {
		JPanel logicclassName = this.makeRow("クラス名", model.getLogicName(), BeeConstants.METHOD_ICON,
				new ClassPopup.DTextAction() {

					@Override
					public void changed(String text, JTextField t) {
						model.setLogicName(text);
						pane.setTitleLabel(text);
						pane.setModified(true);
					}

					@Override
					public void valueChanged(BValuable value) {

					}

					@Override
					public boolean isType() {
						return false;
					}
				}, null);
		rowsArea.add(logicclassName);

		JPanel className = this.makeRow("説明", model.getName(), BeeConstants.METHOD_ICON, new ClassPopup.DTextAction() {

			@Override
			public void changed(String text, JTextField t) {
				model.setName(text);
				pane.setModified(true);
			}

			@Override
			public void valueChanged(BValuable value) {

			}

			@Override
			public boolean isType() {
				return false;
			}
		}, null);
		rowsArea.add(className);
	}

	private JPanel makeRow(String title, String value, ImageIcon icon, ClassPopup.DTextAction action, BCall call) {
		Row panel = new Row();

		int s = BeeUIUtils.getDefaultFontSize();
		JLabel label = new JLabel(title);
		if (call != null) {
			label.setIcon(BeeConstants.DELETE_ICON);
			label.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					call.call();
				}

			});
		} else {
			label.setIcon(icon);
		}
		label.setPreferredSize(new Dimension(s * 8, (int) (s * 1.5)));
		panel.add(label);

		JTextField text = new JTextField(value);
		text.setOpaque(false);
		text.setPreferredSize(new Dimension(s * 43, (int) (s * 1.5)));
		text.setBorder(null);
		panel.add(text);
		if (action != null) {
			new ClassPopup(text, action, pane.getProject(), null);
		}

		return panel;

	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics gg = g.create(0, 0, width, this.getPreferredSize().height);
		gg.setColor(Color.RED);
		gg.fillRect(0, 0, width, this.getPreferredSize().height);
		gg.clipRect(0, 0, width, this.getPreferredSize().height);
		this.setSize(width, (int) this.getPreferredSize().getHeight());
		try {
			this.paint(gg);
		} catch (Exception e) {

		}
		gg.dispose();

	}

	@Override
	public Insets getBorderInsets(Component c) {
		if (this.isVisible()) {
			insets.top = this.getPreferredSize().height;
		} else {
			insets.top = 0;
		}
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	public static class Row extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 860058133556590082L;

		public Row() {
			this.setOpaque(false);

			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);
			this.setLayout(layout);

		}

		public EditAction getAction() {
			return null;
		}
	}

}
