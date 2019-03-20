package com.linkstec.bee.UI.editor.task.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.task.TaskPane;
import com.linkstec.bee.UI.look.BeeIconButtonUI;
import com.linkstec.bee.UI.look.icon.BeeIcon;
import com.linkstec.bee.UI.progress.BeeProgress;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class ConsoleDisplay extends TaskPane implements HyperlinkListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -705151054182969301L;
	private JEditorPane pane;
	private String header = "<html><body style='font-size:" + BeeUIUtils.getDefaultFontSize() + "'>";
	private String footer = "</body><html>";
	private BProject project;
	int offset = 0;

	private BeeThread thread;
	private JButton stop, suspend;
	private BeeProgress progress;
	private boolean cleard = true;

	public ConsoleDisplay() {
		pane = new JEditorPane();
		this.getScroll().getViewport().setView(pane);
		pane.setEditable(false);
		pane.addHyperlinkListener(this);
		pane.setContentType("text/html");
		pane.setFont(BeeUIUtils.getDefaultFont());
		pane.setText(header + footer);
		this.addProgress();

	}

	private void addProgress() {
		JPanel panel = new JPanel();

		panel.setBackground(Color.WHITE);
		panel.setLayout(new BorderLayout());

		progress = new BeeProgress();

		panel.add(progress, BorderLayout.CENTER);

		panel.add(this.makeButtons(), BorderLayout.EAST);

		this.add(panel, BorderLayout.NORTH);

	}

	// public void setProgress(double p) {
	// this.progress.setValue(p);
	// }

	private JPanel makeButtons() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();

		layout.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(layout);

		suspend = this.makeButton(BeeConstants.SUSPEND_ICON);
		suspend.setName("suspend");
		panel.add(suspend);
		BeeIcon icon = (BeeIcon) BeeConstants.STOP_ICON;
		icon.setTopMargin(-1 * BeeUIUtils.getDefaultFontSize() / 5);
		stop = this.makeButton(icon);
		panel.add(stop);

		return panel;
	}

	private JButton makeButton(ImageIcon icon) {
		JButton button = new JButton() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -258384433294991339L;

			@Override
			protected void paintBorder(Graphics g) {

			}

		};
		button.setUI(new BeeIconButtonUI());
		button.setBackground(null);
		button.setIcon(icon);
		button.setEnabled(false);
		button.setDisabledIcon(BeeUIUtils.getDisabledIcon(icon, this));
		button.setOpaque(false);
		button.setBorder(null);
		button.addActionListener(this);

		return button;
	}

	public void addText(String text, BProject project) {
		synchronized (this) {
			if (project != null) {
				this.project = project;
			}

			SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");

			String s = "<div>" + f.format(Calendar.getInstance(TimeZone.getDefault()).getTime()) + "  " + text
					+ "</div>";
			if (this.cleard) {
				pane.setText(this.header + s + this.footer);
			} else {
				try {
					HTMLDocument doc = (HTMLDocument) pane.getDocument();
					Element[] roots = doc.getRootElements();
					Element body = null;
					for (int i = 0; i < roots[0].getElementCount(); i++) {
						Element element = roots[0].getElement(i);
						if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
							body = element;

							break;
						}
					}
					doc.insertBeforeEnd(body, s);

					int count = body.getElementCount();
					if (count > 250) {
						Element first = body.getElement(0);
						try {
							doc.removeElement(first);
						} catch (Exception e1) {
							// at javax.swing.text.View.setParent(Unknown Source)
						}
					}

					pane.scrollRectToVisible(new Rectangle(0, pane.getHeight() + 10, 0, 0));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			cleard = false;
			Application.getInstance().getDesignSpective().getTask().setTaskConsoleSelected();
		}

	}

	public void clear() {
		pane.setText(header + footer);
		offset = header.length();
		cleard = true;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
			return;
		String des = e.getDescription();

		String f = des.substring(des.indexOf("://") + 3, des.length());
		String p = des.substring(0, des.indexOf("://"));
		if (p.equals("d")) {
			if (project != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						BeeActions.addDetailPane(new File(f), project);
						Application.getInstance().setSpactive(BeeSpective.DETAIL_DESIGN);

					}

				}).start();

			}
		} else if (p.equals("j")) {
			if (project != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Application.getInstance().setSpactive(BeeSpective.JAVA_SOURCE);
						Application.getInstance().getJavaSourceSpective().addEditor(new File(f), project);

					}

				}).start();

			}
		} else if (p.equals("f")) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String[] cmd = new String[5];
					cmd[0] = "cmd";
					cmd[1] = "/c";
					cmd[2] = "start";
					cmd[3] = " ";
					cmd[4] = f;
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}).start();

		}
	}

	public void start(BeeThread thread) {
		thread.setProgress(progress);
		this.thread = thread;
		this.suspend.setEnabled(true);
		this.suspend.setIcon(BeeConstants.SUSPEND_ICON);
		this.suspend.setName("suspend");
		this.stop.setEnabled(true);
	}

	public void end() {
		this.thread = null;
		this.suspend.setIcon(BeeConstants.SUSPEND_ICON);
		this.suspend.setName("suspend");
		this.suspend.setEnabled(false);
		this.stop.setEnabled(false);
		this.progress.setValue(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj.equals(stop)) {
			this.thread.setStatus(BeeThread.STATUS_TOP);
			this.end();
			this.thread = null;
		} else if (obj.equals(suspend)) {
			if (this.thread != null) {
				try {
					if (this.suspend.getName().equals("suspend")) {
						this.thread.setStatus(BeeThread.STATUS_SUSPEND);
						this.suspend.setIcon(BeeConstants.START_ICON);
						this.suspend.setName("start");

					} else if (this.suspend.getName().equals("start")) {
						this.thread.setStatus(BeeThread.STATUS_RESUME);
						this.suspend.setIcon(BeeConstants.SUSPEND_ICON);
						this.suspend.setName("suspend");

					}
				} catch (Exception e1) {

				}
				this.stop.setEnabled(true);
			}
		}
	}

}
