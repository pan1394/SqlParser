package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ComboBoxEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;;

public class BeeComboxUI extends BasicComboBoxUI {

	public BeeComboxUI() {
		this.currentValuePane.setBackground(Color.WHITE);
	}

	public static ComponentUI createUI(JComponent c) {
		return new BeeComboxUI();
	}

	protected JButton createArrowButton() {
		JButton button = new JButton() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2187627062931297741L;

			@Override
			public void paint(Graphics g) {
				ImageIcon icon;
				if (this.isEnabled()) {
					icon = (ImageIcon) this.getIcon();
				} else {
					icon = (ImageIcon) this.getDisabledIcon();
				}

				Image img = icon.getImage();
				g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

			}

		};
		button.setIcon(BeeConstants.ZOOM_ICON);
		button.setDisabledIcon(BeeUIUtils.getDisabledIcon(BeeConstants.ZOOM_ICON, button));
		return button;
	}

	protected ComboBoxEditor createEditor() {
		ComboBoxEditor editor = new BasicComboBoxEditor.UIResource() {

			@Override
			protected JTextField createEditorComponent() {
				JTextField text = super.createEditorComponent();

				text.setForeground(Color.black);
				text.setOpaque(false);
				return text;
			}

		};

		return editor;
	}

}
