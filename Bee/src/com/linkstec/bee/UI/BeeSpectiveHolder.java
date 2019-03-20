package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class BeeSpectiveHolder extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -818687511635494843L;

	public BeeSpectiveHolder() {
		this.setLayout(new BorderLayout());
	}

	public void setContents(JComponent comp) {
		this.removeAll();
		this.add(comp, BorderLayout.CENTER);
		if (comp instanceof BSpective) {
			BSpective s = (BSpective) comp;
			s.getWorkspace().stateChanged(null);
		}
	}

	@Override
	public void paint(Graphics g) {
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		super.paint(g);
	}
}
