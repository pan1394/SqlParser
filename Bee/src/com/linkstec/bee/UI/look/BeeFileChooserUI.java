package com.linkstec.bee.UI.look;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.WindowsFileChooserUI;

public class BeeFileChooserUI extends WindowsFileChooserUI {

	public BeeFileChooserUI(JFileChooser filechooser) {
		super(filechooser);
	}

	public static ComponentUI createUI(JComponent c) {
		return new BeeFileChooserUI((JFileChooser) c);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
	}

	@Override
	protected JPanel createList(JFileChooser fc) {

		JPanel panel = super.createList(fc);

		return panel;
	}

	@Override
	protected JPanel createDetailsView(JFileChooser fc) {
		return super.createDetailsView(fc);
	}

	public JPanel getAccessoryPanel() {
		JPanel panel = super.getAccessoryPanel();

		return panel;
	}

}
