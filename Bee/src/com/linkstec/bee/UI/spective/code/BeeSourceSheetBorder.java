package com.linkstec.bee.UI.spective.code;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants.FontConstants;
import javax.swing.text.StyleContext.NamedStyle;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeeSourceSheetBorder implements Border {
	private int width = BeeUIUtils.getDefaultFontSize() * 3;
	private int gap = BeeUIUtils.getDefaultFontSize() / 4;
	private Font font;

	public BeeSourceSheetBorder() {
		font = new Font(BeeSourceSheet.font, Font.PLAIN, (int) BeeSourceSheet.FONT_SIZE);
	}

	public Insets getBorderInsets(Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component c, Insets insets) {
		if (c instanceof JTextPane) {
			insets.left = width;
		}

		return insets;

	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		JTextPane sheet = (JTextPane) c;
		Element root = sheet.getDocument().getDefaultRootElement();
		g.setFont(font);

		int count = root.getElementCount();

		g.setColor(Color.GRAY);

		String me = count + "11";

		this.width = g.getFontMetrics().stringWidth(me);

		// line
		g.drawLine(x + this.width - gap, y, x + this.width - gap, y + height);
		g.setColor(Color.GRAY.brighter().brighter());
		g.fillRect(x, y, this.width - gap, height);

		for (int i = 0; i < count; i++) {
			Element ele = root.getElement(i);
			BranchElement b = (BranchElement) ele;

			AttributeSet attr = b.getAttributes();
			Enumeration<?> enu = attr.getAttributeNames();
			while (enu.hasMoreElements()) {
				Object name = enu.nextElement();
				Object value = attr.getAttribute(name);
				if (value instanceof NamedStyle) {
					NamedStyle style = (NamedStyle) value;

					String fname = (String) style.getAttribute(FontConstants.Family);

					int size = (int) style.getAttribute(FontConstants.Size);
					boolean bold = (boolean) style.getAttribute(FontConstants.Bold);
					boolean italic = (boolean) style.getAttribute(FontConstants.Italic);

					if (bold) {
						font = new Font(fname, Font.BOLD, size);
					} else if (italic) {
						font = new Font(fname, Font.ITALIC, size);
					} else {
						font = new Font(fname, Font.PLAIN, size);
					}

				}

			}

			g.setColor(Color.GRAY);
			FontMetrics fm = g.getFontMetrics();
			int h = (int) (fm.getHeight());
			String label = padLabel(i + 1 + "", 0, true);
			g.drawString(label, 0, y + h * i + fm.getAscent());
		}

		for (int i = count; i < count * 2; i++) {
			FontMetrics fm = g.getFontMetrics();
			int h = (int) (fm.getHeight());
			String label = padLabel(i + 1 + "", 0, true);
			g.drawString(label, 0, y + h * i + fm.getAscent());
		}
	}

	private String padLabel(String lineNumber, int length, boolean addSpace) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(lineNumber);
		for (int count = (length - buffer.length()); count > 0; count--) {
			buffer.insert(0, " ");
		}
		if (addSpace) {
			buffer.append(" ");
		}
		return " " + buffer.toString();
	}
}
