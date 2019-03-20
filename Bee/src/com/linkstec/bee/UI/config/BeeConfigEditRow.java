package com.linkstec.bee.UI.config;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.filechooser.BeeFileChooser;
import com.linkstec.bee.UI.look.text.BeeLabel;
import com.linkstec.bee.UI.look.text.BeeTextField;

public class BeeConfigEditRow extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6769245697713536357L;

	private BeeTextField text;
	private BeeLabel label;
	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public boolean isRequired() {
		return this.label.isRequired();
	}

	public void setRequired(boolean required) {
		label.setRequired(required);
	}

	/**
	 * 
	 * @param view
	 * @param title
	 * @param changeExplore
	 * @param allowInput
	 * @param defaultValue
	 * @param targetProperty
	 */
	public BeeConfigEditRow(BeeConfigView view, String title, String defaultValue, String targetProperty, boolean selectFolder) {
		int s = BeeUIUtils.getDefaultFontSize();
		setBorder(BorderFactory.createEmptyBorder(s / 2, s / 2, s / 2, s / 2));

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		setLayout(layout);

		label = new BeeLabel(title);
		label.setFont(label.getFont().deriveFont(BeeUIUtils.getDefaultFontSize()));
		label.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 12, BeeUIUtils.getDefaultFontSize() * 2));
		add(label);

		text = new BeeTextField();
		text.setRoundBorder(true);

		if (defaultValue != null) {
			text.setText(defaultValue);
		}

		text.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				view.rowValueChanged(targetProperty, text.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				view.rowValueChanged(targetProperty, text.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				view.rowValueChanged(targetProperty, text.getText());
			}

		});

		text.setPreferredSize(new Dimension(s * 25, BeeUIUtils.getDefaultFontSize() * 2));
		add(text);
		setOpaque(false);
		if (selectFolder) {

			JButton b = new JButton();
			b.setIcon(BeeConstants.FOLDER_ICON);
			b.setPreferredSize(new Dimension(3 * s, (int) (1.7 * s)));
			b.setUI(new BeeButtonUI());
			add(b);

			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					BeeFileChooser dialog = new BeeFileChooser();
					dialog.setFileSelectionMode(BeeFileChooser.DIRECTORIES_ONLY);
					dialog.showDialog(b, "選択");
					File f = dialog.getSelectedFile();
					if (f != null) {
						if (f.exists()) {
							if (f.isDirectory()) {
								setText(f.getAbsolutePath());
							}
						}
					}
				}

			});
		}

	}

	protected String getText() {
		return this.text.getText();
	}

	protected void setText(String text) {
		this.text.setText(text);
	}

}
