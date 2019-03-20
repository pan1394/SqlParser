package com.linkstec.bee.UI.look.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.Application;

public class BeeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2909182144418524248L;

	public BeeDialog(BeeDialogCloseAction action) {
		super(Application.FRAME);
		this.setIconImage(BeeConstants.APP_ICON.getImage());
		// this.setAlwaysOnTop(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {

				if (action != null)
					action.onclose();
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

		});
	}

	public static BeeDialog showDialog(String title, JComponent component, BeeDialogCloseAction action) {
		BeeDialog dialog = new BeeDialog(action);
		dialog.setTitle(title);
		int margin = BeeUIUtils.getDefaultFontSize() / 2;
		component.setBorder(new EmptyBorder(margin, margin, margin, margin));

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(component, BorderLayout.CENTER);

		component.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				dialog.setVisible(false);
			}

		});
		dialog.pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((size.width - dialog.getWidth()) / 2, (size.height - dialog.getHeight()) / 2);
		dialog.setVisible(true);
		return dialog;
	}

	public void setVisible(boolean v) {

		super.setVisible(v);
	}
}
