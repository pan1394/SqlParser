package com.linkstec.bee.UI.editor.sidemenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.swing.mxGraphComponent;

public abstract class BasicMenu extends JPanel implements KeyListener {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1976147303364723903L;
	protected JPanel contents = new JPanel();
	protected JTextField search;

	protected static int spacing = BeeUIUtils.getDefaultFontSize() / 3;
	protected JPanel title = new JPanel();
	protected BProject project;

	public BasicMenu(BProject project) {
		this.project = project;
		this.setOpaque(true);

		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());

		this.createSearch();
		this.init();

		JPanel container = new JPanel();
		container.setBackground(Color.WHITE);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEADING);
		container.setLayout(layout);
		GridLayout grid = new GridLayout(0, 1);
		contents.setLayout(grid);
		contents.setBackground(Color.WHITE);
		container.add(this.contents);

		JScrollPane scroll = new JScrollPane(container) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintBorder(Graphics g) {

			}

		};
		scroll.getVerticalScrollBar().setUnitIncrement(BeeUIUtils.getDefaultFontSize() * 5);
		this.add(scroll, BorderLayout.CENTER);

		this.addAllItems(null);
		scroll.updateUI();
	}

	protected void createSearch() {
		title.setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
		this.add(title, BorderLayout.NORTH);
		title.setLayout(new BorderLayout());

		Image img = BeeConstants.SEARCH_ICON.getImage();
		search = new JTextField() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5652103360455150616L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.LIGHT_GRAY);
				int w = img.getHeight(this);
				int h = img.getHeight(this);

				int height = this.getHeight();
				g.drawImage(img, 0, (height - h) / 2, this);
				g.drawLine(w, getHeight() - 1, getWidth(), getHeight() - 1);
			}

		};
		search.setBorder(new EmptyBorder(0, spacing * 3, 0, 0));
		search.setOpaque(false);
		search.addKeyListener(this);

		title.add(search, BorderLayout.CENTER);
	}

	protected void init() {

	}

	public abstract ImageIcon getIcon();

	protected abstract void addAllItems(String text);

	protected boolean maches(String target, String word) {

		int l = word.length();
		for (int i = 0; i < l; i++) {
			if (target.toLowerCase().indexOf(word.substring(i, i + 1).toLowerCase()) > -1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

		String text = this.search.getText().trim();

		char c = e.getKeyChar();
		if (Character.isJavaIdentifierPart(c)) {
			this.addAllItems(text);
			this.contents.updateUI();
		}
	}

	private Thread thread = null;

	public void makePopValue(Object obj, BeePopupTreeMenu pop, int height, String text, mxGraphComponent sheet) {
		if (thread != null) {
			thread.interrupt();
		}
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);

				} catch (InterruptedException e) {

					return;
				}
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addAllItems(text);
				contents.updateUI();
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

		});
		thread.start();

	}

	public void setSelected(BasicMenuItem panel) {
		int count = this.contents.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.contents.getComponent(i);
			if (comp instanceof BasicMenuItem) {
				BasicMenuItem item = (BasicMenuItem) comp;
				if (!item.equals(panel)) {
					item.setSelected(false);
				} else {
					item.setSelected(true);
				}
			}
		}
	}

}
