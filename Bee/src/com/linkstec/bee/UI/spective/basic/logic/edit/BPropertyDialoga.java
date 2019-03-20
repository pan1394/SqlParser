package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.core.fw.editor.BEditor;

public class BPropertyDialoga extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -886997822539609861L;
	private JPanel contents;
	private int s = BeeUIUtils.getDefaultFontSize();
	private JTextField propertyName, propertyLogicName;
	private PropertyAction action;
	private BasicLogicSheet sheet;

	public BPropertyDialoga(String title, PropertyAction action, BasicLogicSheet sheet) {
		// super(Application.FRAME, true);
		this.setModal(true);
		this.sheet = sheet;
		this.action = action;
		this.setTitle(title);
		this.setLayout(new BorderLayout());
		contents = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 923395245715598093L;

			@Override
			public Insets getInsets() {
				return new Insets(s, s, s, s);
			}

		};
		contents.setBackground(Color.WHITE);
		this.add(contents, BorderLayout.CENTER);

		contents.setLayout(new BorderLayout());
		contents.add(this.makeContents(), BorderLayout.CENTER);
		contents.add(this.makeButton(), BorderLayout.SOUTH);
		this.setSize(s * 50, s * 18);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size.width - this.getWidth()) / 2, (size.height - this.getHeight()) / 2);
	}

	private JPanel makeContents() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(box);

		propertyName = this.makeRow(panel, "論理名");
		propertyName.setText(action.getName());
		propertyLogicName = this.makeRow(panel, "物理名");
		propertyLogicName.setText(action.getLogicName());
		propertyLogicName.enableInputMethods(false);

		return panel;
	}

	private JTextField makeRow(JPanel p, String name) {
		JPanel panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8009784536441518257L;

			@Override
			public Insets getInsets() {
				return new Insets(s / 2, s / 2, s / 2, s / 2);
			}
		};
		panel.setOpaque(false);

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);

		JLabel label = new JLabel(name);
		panel.add(label);
		label.setPreferredSize(new Dimension(s * 10, s * 2));

		JTextField text = new JTextField();
		text.setPreferredSize(new Dimension(s * 30, s * 2));
		panel.add(text);

		p.add(panel);

		return text;
	}

	private JPanel makeButton() {
		JPanel panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8009784536441518257L;

			@Override
			public Insets getInsets() {
				return new Insets(s * 2, s, s, s);
			}
		};
		panel.setOpaque(false);

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);

		JButton cancel = this.makeButton("キャンセル");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BPropertyDialoga.this.setVisible(false);
				action.cancelled(sheet);
			}

		});

		JButton apply = this.makeButton("適用");
		apply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BPropertyDialoga.this.setVisible(false);
				action.setLogicName(getPropertyLogicName());
				action.setName(getPropertyName());
				action.applied(sheet);
			}

		});

		panel.add(cancel);
		panel.add(apply);

		ProcessType ptype = action.getDetailEditName(sheet);
		if (ptype != null) {

			JButton edit = this.makeButton(ptype.getTitle());
			edit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					BPropertyDialoga.this.setVisible(false);
					action.setLogicName(getPropertyLogicName());
					action.setName(getPropertyName());
					action.editDetail(sheet, ptype);

				}

			});
			panel.add(edit);
		}

		return panel;
	}

	public String getPropertyName() {
		return this.propertyName.getText();
	}

	public String getPropertyLogicName() {
		return this.propertyLogicName.getText();
	}

	private JButton makeButton(String name) {
		JButton b = new JButton(name);
		b.setUI(new BeeButtonUI());
		int s = BeeUIUtils.getDefaultFontSize();
		b.setPreferredSize(new Dimension(s * name.length() + s * 2, s * 2));
		return b;
	}

	public static interface PropertyAction {

		public String getLogicName();

		public void setLogicName(String name);

		public void setName(String name);

		public String getName();

		public void applied(BasicLogicSheet sheet);

		public void cancelled(BasicLogicSheet sheet);

		public BEditor editDetail(BasicLogicSheet sheet, ProcessType type);

		public ProcessType getDetailEditName(BasicLogicSheet sheet);
	}
}
