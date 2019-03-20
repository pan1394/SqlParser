package com.linkstec.bee.UI.look.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeeOptionDialog extends JPanel implements BeeDialogCloseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2673909323122932537L;
	private boolean yes = false;
	private boolean later = false;
	private int mode;
	public static final int YES_NO_MODE = 1;
	public static final int OK_MODE = 2;
	public static final int YES_NO_ANDLATER = 3;

	public BeeOptionDialog(String message, int mode) {
		this.mode = mode;
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);

		this.add(this.makeMessage(message), BorderLayout.CENTER);
		this.add(this.makeButtonArea(), BorderLayout.SOUTH);
	}

	private JComponent makeMessage(String message) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		int margin = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(margin, margin, margin, margin));
		BorderLayout layout = new BorderLayout();
		layout.setHgap(BeeUIUtils.getDefaultFontSize());
		panel.setLayout(layout);

		JLabel label = new JLabel("<html>" + message + "</html>");
		margin = margin * 2;
		label.setBorder(new EmptyBorder(margin, margin, margin, margin));
		label.setPreferredSize(
				new Dimension(BeeUIUtils.getDefaultFontSize() * 40, BeeUIUtils.getDefaultFontSize() * 5));
		panel.add(label, BorderLayout.CENTER);
		if (this.mode == YES_NO_ANDLATER) {
			JPanel check = new JPanel();
			check.setOpaque(false);
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEADING);
			check.setBorder(new EmptyBorder(margin, margin, margin, margin));
			check.setLayout(flow);
			JCheckBox laterb = new JCheckBox("以降はすべてこの選択を適用する");
			laterb.setFont(BeeUIUtils.getDefaultFont());
			check.add(laterb);
			laterb.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					later = laterb.isSelected();
				}

			});
			panel.add(check, BorderLayout.SOUTH);
		}

		return panel;
	}

	private JComponent makeButtonArea() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		int margin = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(margin, margin, margin, margin));
		FlowLayout layout = new FlowLayout();
		layout.setHgap(BeeUIUtils.getDefaultFontSize());
		layout.setAlignment(FlowLayout.CENTER);
		panel.setLayout(layout);

		margin = margin / 2;
		JButton yesb = new JButton("YES");
		yesb.setFont(BeeUIUtils.getDefaultFont());
		yesb.setMargin(new Insets(margin, margin, margin, margin));
		JButton nob = new JButton("NO");
		nob.setFont(BeeUIUtils.getDefaultFont());
		nob.setMargin(new Insets(margin, margin, margin, margin));

		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source.equals(yesb)) {
					yes = true;
				} else if (source.equals(nob)) {
					yes = false;
				}
				BeeOptionDialog.this.setVisible(false);
			}

		};
		yesb.addActionListener(action);

		panel.add(yesb);

		if (this.mode == YES_NO_MODE || mode == YES_NO_ANDLATER) {
			JPanel space = new JPanel();
			space.setOpaque(false);
			space.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 5, 1));
			panel.add(space);
			nob.addActionListener(action);
			panel.add(nob);
		}

		return panel;
	}

	public static BeeOptionDialog showDialog(String title, String message, int mode) {
		BeeOptionDialog dialog = new BeeOptionDialog(message, mode);
		BeeDialog.showDialog(title, dialog, dialog);
		return dialog;
	}

	@Override
	public void onclose() {
		yes = false;
		later = false;
	}

	public boolean yes() {
		return this.yes;
	}

	public boolean isLater() {
		return this.later;
	}

}
