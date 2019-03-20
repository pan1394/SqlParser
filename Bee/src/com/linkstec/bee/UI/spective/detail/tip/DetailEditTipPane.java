package com.linkstec.bee.UI.spective.detail.tip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.tip.TipAction;

public class DetailEditTipPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1032449332624145758L;

	int VGap = BeeUIUtils.getDefaultFontSize() / 2;
	int HGap = BeeUIUtils.getDefaultFontSize() / 2;
	BeeGraphSheet bee;
	BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
	// private DetailEditToolTip tip;

	public DetailEditTipPane(DetailEditToolTip tip) {

		// this.tip = tip;
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(HGap, VGap, HGap, VGap));
	}

	public void addComp(JComponent comp, BeeGraphSheet bee) {
		this.bee = bee;
		comp.setFont(BeeUIUtils.getDefaultFont());
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout f = new FlowLayout();
		f.setAlignment(FlowLayout.LEFT);
		panel.setLayout(f);

		panel.add(comp);

		this.add(panel);
	}

	public void addLine() {
		JPanel line = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3081225186129416682L;

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.GRAY);
				g.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, this.getHeight() / 2 + 1, this.getWidth(), this.getHeight() / 2 + 1);
			}

		};
		line.setPreferredSize(new Dimension(this.getWidth() - 10, BeeUIUtils.getDefaultFontSize() * 2));
		this.add(line);
	}

	public void addLink(TipAction action, BeeGraphSheet bee) {
		Box pane = Box.createVerticalBox();
		pane.setOpaque(false);
		int gap = BeeUIUtils.getDefaultFontSize();

		JPanel titlePanel = new JPanel();
		FlowLayout f = new FlowLayout();
		f.setAlignment(FlowLayout.LEFT);
		titlePanel.setLayout(f);
		titlePanel.setOpaque(false);

		JLabel title = new JLabel(action.getTitle());
		title.setIcon(BeeConstants.SELECT_ICON);
		// title.setBorder(new EmptyBorder(0, gap, 0, 0));
		title.setForeground(Color.BLUE);
		titlePanel.add(title);
		pane.add(titlePanel);

		JPanel actionPanel = new JPanel();
		actionPanel.setOpaque(false);
		actionPanel.setBorder(new EmptyBorder(0, gap, 0, 0));
		actionPanel.setLayout(new BorderLayout());
		pane.add(actionPanel);

		this.addComp(pane, bee);

		title.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				action.clicked();
				dispatchEvent(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				title.setForeground(Color.PINK);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				title.setForeground(Color.BLUE);
			}

		});
	}
}