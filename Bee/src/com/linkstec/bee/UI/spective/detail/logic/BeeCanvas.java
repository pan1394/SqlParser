package com.linkstec.bee.UI.spective.detail.logic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ClassHeaderNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.node.ThrowNode;
import com.linkstec.bee.UI.node.view.ModifierNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxLightweightLabel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class BeeCanvas extends mxInteractiveCanvas {
	private BeeGraphSheet sheet;

	public BeeCanvas(BeeGraphSheet sheet) {
		this.sheet = sheet;
	}

	@Override
	public Object drawCell(mxCellState state) {

		Object obj = super.drawCell(state);
		Object cell = state.getCell();
		if (cell instanceof BasicNode) {
			if (cell instanceof NoteNode) {
				this.paintIcon(state, state.getStyle());
			}
			this.paintShadow(state);
		} else if (cell instanceof ModifierNode) {
			this.paintIcon(state, state.getStyle());
		}

		return obj;
	}

	public BeeGraphSheet getSheet() {
		return sheet;
	}

	@Override
	public Object drawLabel(String text, mxCellState state, boolean html) {

		Object cell = state.getCell();
		if (!(cell instanceof NoteNode)) {
			if (cell instanceof BasicNode) {
				BasicNode node = (BasicNode) cell;
				Map<String, Object> style = state.getStyle();
				if (node.getAlert() != null) {

					style.put("fontColor", "orange");
					this.paintAlert(text, state, style, node.getAlertObject());

					sheet.addErrorLine(node);
				} else {
					String color = (String) style.get("fontColor");
					if (color != null && color.equals("white")) {
						// style.put("fontColor", "black");
					}

					sheet.removeErrorLine(node);

				}

			}
		} else {
			Debug.a();
		}

		Object obj = super.drawLabel(text, state, html);
		return obj;
	}

	private void paintIcon(mxCellState state, Map<String, Object> style) {
		Object cell = state.getCell();
		if (cell instanceof BasicNode) {
			BasicNode node = (BasicNode) cell;

			mxICell parent = node.getParent();
			ImageIcon icon = node.getIcon();

			if (icon != null && parent != null) {
				if (node instanceof IUnit || node instanceof ThrowNode) {

					if ((parent instanceof BLogicBody) || !(parent instanceof BasicNode) || node instanceof ThrowNode) {

						if (node instanceof ThrowNode) {
							ThrowNode t = (ThrowNode) node;
							if (t.getChildCount() == 0) {
								return;
							}
						}
						Graphics2D previousGraphics = g;

						g = createTemporaryGraphics(style, 100, null);

						mxRectangle rect = state.getBoundingBox();
						if (rect == null) {
							return;
						}
						double scale = getScale();
						int x = (int) rect.getX() - (int) (9 * scale);
						int y = (int) rect.getY() + (int) (4 * scale);
						int w = (int) (10 * scale);
						int h = (int) (10 * scale);

						g.drawImage(icon.getImage(), x, y, w, h, null);
						BAlert alert = hasErrorChild(node);

						if (alert != null) {
							if (alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR)) {
								g.drawImage(BeeConstants.ERROR_ICON.getImage(), x - w / 20, y + h / 3, w * 2 / 3, h * 2 / 3, null);
							} else {
								g.drawImage(BeeConstants.ALERT_ICON.getImage(), x - w / 20, y + h / 3, w * 2 / 3, h * 2 / 3, null);
							}
						}

						g.dispose();
						g = previousGraphics;

					}
				}
			}
		} else if (cell instanceof ModifierNode) {
			ModifierNode node = (ModifierNode) cell;
			mxICell parent = node.getParent();

			mxRectangle rect = state.getBoundingBox();
			if (rect == null) {
				return;
			}

			ImageIcon icon = null;
			if (parent instanceof BMethod) {
				if (Modifier.isPrivate(node.getModifier())) {
					icon = BeeConstants.METHOD_PRIVATE_ICON;
				} else if (Modifier.isProtected(node.getModifier())) {
					icon = BeeConstants.METHOD_PROTECED_ICON;
				} else if (Modifier.isPublic(node.getModifier())) {
					icon = BeeConstants.METHOD_ICON;
				}
			} else if (parent instanceof ClassHeaderNode) {
				ClassHeaderNode header = (ClassHeaderNode) parent;
				icon = header.getIcon();

			} else if (parent instanceof BAssignment) {
				if (Modifier.isPrivate(node.getModifier())) {
					icon = BeeConstants.VAR_PRIVATE_ICON;
				} else if (Modifier.isPublic(node.getModifier())) {
					icon = BeeConstants.VAR_ICON;
				} else if (Modifier.isProtected(node.getModifier())) {
					icon = BeeConstants.VAR_PROTECED_ICON;
				}
			}
			double scale = getScale();
			int x = (int) rect.getX() + (int) (11 * scale);
			int y = (int) rect.getY() + (int) (3 * scale);
			int w = (int) (10 * scale);
			int h = (int) (10 * scale);

			Graphics2D previousGraphics = g;
			g = createTemporaryGraphics(style, 100, null);
			if (icon != null) {
				g.drawImage(icon.getImage(), x, y, w, h, null);
			}

			int nextLine = (int) (20 * scale);
			x = (int) (x - 8 * scale);
			if (Modifier.isFinal(node.getModifier())) {
				icon = BeeConstants.FINAL_ICON;
				g.drawImage(icon.getImage(), x, y + nextLine, w, h, null);
				x = x + icon.getIconWidth() * 2 / 3;
			}
			if (Modifier.isStatic(node.getModifier())) {
				icon = BeeConstants.STATIC_ICON;
				g.drawImage(icon.getImage(), x, y + nextLine, w, h, null);
				x = x + icon.getIconWidth() * 2 / 3;
			}
			if (Modifier.isAbstract(node.getModifier())) {
				icon = BeeConstants.CLASS_ABSTRACT_ICON;
				g.drawImage(icon.getImage(), x, y + nextLine, w, h, null);
				x = x + icon.getIconWidth() * 2 / 3;
			}

			if (Modifier.isSynchronized(node.getModifier())) {
				icon = BeeConstants.SYNCHRONIZE_ICON;
				g.drawImage(icon.getImage(), x, y + nextLine, w, h, null);
				x = x + icon.getIconWidth() * 2 / 3;
			}

			g.dispose();
			g = previousGraphics;

		}

	}

	private BAlert hasErrorChild(BasicNode node) {
		if (node.getAlert() != null) {
			return node.getAlertObject();
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			Object child = node.getChildAt(i);
			if (child instanceof BasicNode) {
				BasicNode b = (BasicNode) child;
				BAlert alert = hasErrorChild(b);
				if (alert != null) {
					return alert;
				}
			}
		}
		return null;
	}

	private void paintAlert(String text, mxCellState state, Map<String, Object> style, BAlert alert) {

		// mxRectangle rect = state.getBoundingBox();

		// float opacity = mxUtils.getFloat(style, mxConstants.STYLE_TEXT_OPACITY, 100);
		Graphics2D previousGraphics = g;
		g = createTemporaryGraphics(style, 100, null);
		if (alert.getType() != null && alert.getMessage().equals(BAlert.TYPE_ERROR)) {
			g.setColor(Color.RED);
		} else {
			g.setColor(BeeConstants.BORDER_ALERT_BORDERCOLOR);
		}
		// int margin = BeeUIUtils.getDefaultFontSize() / 5;
		// g.fillRoundRect((int) rect.getX(), (int) rect.getY() + margin, (int)
		// rect.getWidth(),
		// (int) rect.getHeight() - margin * 2, (int) rect.getHeight() / 3, (int)
		// rect.getHeight() / 3);
		g.dispose();
		g = previousGraphics;

	}

	private void paintAlerta(String text, mxCellState state, Map<String, Object> style) {
		mxLightweightLabel textRenderer = mxLightweightLabel.getSharedInstance();
		CellRendererPane rendererPane = getRendererPane();
		Rectangle rect = state.getLabelBounds().getRectangle();
		float opacity = mxUtils.getFloat(style, mxConstants.STYLE_TEXT_OPACITY, 100);
		Graphics2D previousGraphics = g;
		g = createTemporaryGraphics(style, opacity, null);

		if (textRenderer != null && rendererPane != null && (g.getClipBounds() == null || g.getClipBounds().intersects(rect))) {
			double scale = getScale();
			int x = rect.x;
			int y = rect.y;
			int w = rect.width;
			int h = rect.height;

			if (!mxUtils.isTrue(style, mxConstants.STYLE_HORIZONTAL, true)) {
				g.rotate(-Math.PI / 2, x + w / 2, y + h / 2);
				g.translate(w / 2 - h / 2, h / 2 - w / 2);

				int tmp = w;
				w = h;
				h = tmp;
			}

			Font textFont = g.getFont();// mxUtils.getFont(style, getScale());
			float markSize = (float) (textFont.getSize() * 10 / BeeUIUtils.getDefaultFontSize());
			Font font = textFont.deriveFont(Font.PLAIN, markSize);
			textRenderer.setFont(font);
			g.scale(scale, scale);

			double width = mxUtils.getSizeForString(text, textFont, scale).getWidth();
			String s = "^";
			double sWidth = mxUtils.getSizeForString(s, font, scale).getWidth();

			if (sWidth == 0) {
				sWidth = 0.1;
			}
			int count = (int) (width / sWidth);
			String alert = "";
			for (int i = 0; i < count; i++) {
				alert = alert + "^";
			}
			textRenderer.setText(alert);
			textRenderer.setForeground(Color.RED);
			rendererPane.paintComponent(g, textRenderer, rendererPane, (int) (x / scale) + mxConstants.LABEL_INSET, (int) ((int) (y / scale) + mxConstants.LABEL_INSET + markSize), (int) (w / scale), (int) (h / scale), true);
			g.dispose();
			g = previousGraphics;
		}
	}

	private void paintShadow(mxCellState state) {
		Object cell = state.getCell();

		mxRectangle rect = state.getBoundingBox();
		if (rect == null) {
			return;
		}
		int x = (int) (rect.getX() + 2);
		int y = (int) (rect.getY() + 2);
		int w = (int) (rect.getWidth());
		int h = (int) (rect.getHeight());
		int gradientWidth = 10;
		if (cell instanceof BMethod || cell instanceof BClassHeader) {
			Graphics2D previousGraphics = g;
			g = createTemporaryGraphics(state.getStyle(), 100, null);
			BeeUIUtils.drawRectShadow(g, x - gradientWidth, y - gradientWidth, w + gradientWidth * 2, h + gradientWidth * 2, gradientWidth);
			g.dispose();
			g = previousGraphics;
		}

	}

}
