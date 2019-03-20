package com.linkstec.bee.UI.look.scroll;

import java.awt.Component;

import javax.swing.JScrollPane;

import com.linkstec.bee.UI.look.BeeScrollUI;

public class BeeScrollPane extends JScrollPane implements BeeScrollPaneErrorListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7216707974379753267L;
	private BeeScrollUI ui;

	public BeeScrollPane() {
		ui = new BeeScrollUI();
		this.setUI(ui);
	}

	public BeeScrollPane(Component view) {
		super(view);
		ui = new BeeScrollUI();
		this.setUI(ui);
	}

	@Override
	public void error(String name, int line, String message, Object object) {
		ui.addErrorLine(name, line, message, object);
	}

	@Override
	public void clearError(String name) {
		ui.removeErrorLine(name);
	}

	public void clearError() {
		ui.clearError();
	}

}
