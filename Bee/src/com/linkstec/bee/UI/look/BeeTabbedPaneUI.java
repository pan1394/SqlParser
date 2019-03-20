package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

public class BeeTabbedPaneUI extends WindowsTabbedPaneUI {

	private int inclTab = BeeUIUtils.getRoundCornerSize();

	private Color focused = BeeConstants.TABBEDPANE_FOCUSED_COLOR;
	private Color notfocused = BeeConstants.TABBEDPANE_UNFOCUSED_COLOR;
	private boolean gainedFocus = false;

	public BeeTabbedPaneUI() {

	}

	public static ComponentUI createUI(JComponent c) {
		return new BeeTabbedPaneUI();
	}

	public boolean isGainedFocus() {
		return gainedFocus;
	}

	public void setGainedFocus(boolean gainedFocus) {
		this.gainedFocus = gainedFocus;
	}

	private Color getBackColor() {
		if (this.isGainedFocus()) {
			return this.focused;
		}
		return this.notfocused;
	}

	protected int calculateMaxTabHeight(int tabPlacement) {
		return BeeUIUtils.getDefaultFontSize() * 2;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		this.paintPane(g);
		if (tabPane.getSelectedIndex() > -1) {
			super.paint(g, c);
		} else {

		}
	}

	public int getHiddenStartIndex() {
		int tabCount = this.getTabRunCount(tabPane);
		if (tabCount > 1) {
			return tabRuns[1];
		}
		return -1;
	}

	public int getHiddenCount() {
		int count = 0;
		int size = tabPane.getTabCount();
		int tabCount = this.getTabRunCount(tabPane);
		if (tabCount > 1) {
			int first = tabRuns[1];

			for (int i = 0; i < size; i++) {

				if (i >= first) {
					count++;
				}

			}

		}
		return count;

	}

	@Override
	public Insets getTabAreaInsets(int tabPlacement) {
		return super.getTabAreaInsets(tabPlacement);
	}

	@Override
	protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {

		if (selectedIndex < 0) {
			int height = this.calculateMaxTabHeight(tabPlacement);
			Insets in = this.tabPane.getInsets();
			int width = this.tabPane.getWidth();
			g.setColor(BeeConstants.BACKGROUND_COLOR);
			g.fillRect(0, 0, width, height + 2);
			width = width - in.left - in.right - 3;

			Polygon shage = this.getTabAreaShape(in.left, in.top, width, height, tabPlacement);

			g.setColor(Color.LIGHT_GRAY);
			g.drawPolygon(shage);
			g.setColor(BeeConstants.BACKGROUND_COLOR);

		} else {
			if (tabPane instanceof BeeTabbedPane) {
				BeeTabbedPane pane = (BeeTabbedPane) tabPane;

				int height = pane.getComponentButtonAreaHeight();
				if (height > 0) {
					g.setColor(Color.WHITE);
					Insets insets = tabPane.getInsets();
					Insets in = this.getTabAreaInsets(tabPlacement);
					if (tabPlacement == TOP) {
						g.fillRect(insets.left + in.left, this.calculateMaxTabHeight(tabPlacement) + insets.top + in.bottom + in.top, tabPane.getWidth() - insets.left - insets.right - in.left - in.right, height);
					} else {

					}
				}
			}
			if (this.tabPane.getTabCount() != 0) {
				try {
					super.paintTabArea(g, tabPlacement, selectedIndex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
		g.setColor(Color.LIGHT_GRAY);
		int x = 0;
		int y = 0;
		int w = tabPane.getWidth();
		int h = tabPane.getHeight();
		if (tabPlacement == LEFT || tabPlacement == RIGHT) {
			int tabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
			if (tabPlacement == LEFT) {
				x += (tabWidth - tabAreaInsets.bottom);
			}
			w -= (tabWidth - tabAreaInsets.bottom);
		} else {
			int tabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
			if (tabPlacement == TOP) {
				y += (tabHeight);
			}
			h -= (tabHeight);
		}
		g.drawRect(x, y, w, h);
		g.setColor(Color.WHITE);
		g.fillRect(1, y + h - 3, w, 3);
		g.fillRect(1, y - 2, w - 4, 4);
		if (this.tabPane != null && this.tabPane.getParent() != null) {
			Container c = this.tabPane.getParent();
			if (c instanceof BeeTabbedPane) {
				g.fillRect(0, y, 3, h);
				g.fillRect(w - 3, y, 3, h);
			}
		}

	}

	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {

	}

	private Polygon getTabAreaShape(int x, int y, int w, int h, int tabPlacement) {
		if (tabPlacement == JTabbedPane.BOTTOM) {
			int[] xp = new int[] { x, x, x + w, x + w, x + w - inclTab, inclTab };
			int[] yp = new int[] { y + h - inclTab, y, y, y + h - inclTab, y + h, y + h };
			Polygon shape = new Polygon(xp, yp, xp.length);
			return shape;
		} else if (tabPlacement == JTabbedPane.TOP) {
			int[] xp = new int[] { x, x, x + inclTab, x + w - inclTab, x + w, x + w };
			int[] yp = new int[] { y + h, y + inclTab, y, y, y + inclTab, y + h, };
			Polygon shape = new Polygon(xp, yp, xp.length);
			return shape;
		}
		return null;
	}

	private void paintPane(Graphics g) {

		Insets insets = tabPane.getInsets();

		int x = insets.left;
		int y = insets.top;
		int w = tabPane.getWidth() - insets.right - insets.left - 3;
		int h = tabPane.getHeight() - insets.top - insets.bottom;

		Polygon shape = this.getPaneShape(x, y, w, h);

		g.setColor(BeeConstants.BACKGROUND_COLOR);
		g.fillRect(0, 0, w, h + 2);

		g.setColor(getBackColor());
		// g.setColor(Color.WHITE);
		g.fillPolygon(shape);
		// g.fillRect(x, y, w, h);
		g.setColor(Color.LIGHT_GRAY);
		g.drawPolygon(shape);
	}

	private Polygon getPaneShape(int x, int y, int w, int h) {

		int[] xp = new int[] { x, x, x + inclTab, x + w - inclTab, x + w, x + w, x + w - inclTab, x + inclTab };
		int[] yp = new int[] { y + h - inclTab, y + inclTab, y, y, y + inclTab, y + h - inclTab, y + h, y + h };
		Polygon shape = new Polygon(xp, yp, xp.length);
		return shape;
	}

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		if (tabPlacement == JTabbedPane.TOP) {
			if (y >= h - 9) {
				return;
			}
		}
		if (tabPlacement == JTabbedPane.BOTTOM) {
			int height = this.tabPane.getHeight();
			if (height - y >= h + 9) {
				return;
			}
		}

		if (isSelected || tabIndex == this.getRolloverTab()) {

			boolean mouseOverAndnotSelected = (!isSelected) && tabIndex == this.getRolloverTab();

			if (mouseOverAndnotSelected) {
				y = y - 4;
				h = h + 6;
				x--;
			} else {
				y--;
				h++;
			}

			if (tabPlacement == JTabbedPane.BOTTOM) {
				if (!mouseOverAndnotSelected) {
					y = y + 1;
					h = h + 1;
				}
			}
			if (tabPlacement == JTabbedPane.TOP) {
				if (mouseOverAndnotSelected) {
					y = y + 1;
					h = h - 4;
				}
			}

			g.setColor(Color.LIGHT_GRAY);
			Polygon shape = this.getTabShape(x, y, w, h, tabPlacement, mouseOverAndnotSelected);
			Graphics2D g2D = (Graphics2D) g;
			g2D.drawPolygon(shape);

			g.setColor(Color.WHITE);
			if (tabPlacement == JTabbedPane.TOP) {
				x++;
				g2D.fillRect(x, y + h, w, 2);
				if (mouseOverAndnotSelected) {
					g.setColor(Color.LIGHT_GRAY);
					y++;
					g.drawLine(x, y + h, x + w, y + h);
				}
			} else if (tabPlacement == JTabbedPane.BOTTOM) {
				g2D.fillRect(x, y - 2, w, 3);
				if (mouseOverAndnotSelected) {
					g.setColor(Color.LIGHT_GRAY);
					y = y + 2;
					g.drawLine(x, y, x + w, y);
				}
			}
		}
	}

	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		if (tabPlacement == JTabbedPane.TOP) {
			if (y >= h - 9) {
				return;
			}
		}
		if (tabPlacement == JTabbedPane.BOTTOM) {
			int height = this.tabPane.getHeight();
			if (height - y >= h + 9) {
				return;
			}
		}
		if (isSelected || tabIndex == this.getRolloverTab()) {

			boolean mouseOverAndnotSelected = (!isSelected) && tabIndex == this.getRolloverTab();

			if (mouseOverAndnotSelected) {
				y = y - 4;
				h = h + 6;
				x--;
			} else {
				y--;
				h++;
			}

			if (tabPlacement == JTabbedPane.BOTTOM) {
				if (!mouseOverAndnotSelected) {
					y = y + 1;
					h = h + 1;
				}
			}

			if (tabPlacement == JTabbedPane.TOP) {
				if (mouseOverAndnotSelected) {
					y = y + 2;
					h = h - 5;
				}
			}

			g.setColor(Color.WHITE);
			Polygon shape = this.getTabShape(x, y, w, h, tabPlacement, mouseOverAndnotSelected);
			Graphics2D g2D = (Graphics2D) g;
			g2D.fillPolygon(shape);
		}
	}

	private Polygon getTabShape(int x, int y, int w, int h, int tabPlacement, boolean mouseOverAndnotSelected) {

		if (tabPlacement == JTabbedPane.TOP) {

			int[] xp = new int[] { x, x, x + inclTab, x + w - inclTab, x + w, x + w };
			int[] yp = new int[] { y + h, y + inclTab, y, y, y + inclTab, y + h };
			Polygon shape = new Polygon(xp, yp, xp.length);
			return shape;
		} else if (tabPlacement == JTabbedPane.BOTTOM) {
			int[] xp = new int[] { x, x, x + inclTab, x + w - inclTab, x + w, x + w };
			int[] yp = new int[] { y, y + h - inclTab, y + h, y + h, y + h - inclTab, y };
			Polygon shape = new Polygon(xp, yp, xp.length);
			return shape;
		}
		return null;
	}

	protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
		Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

		int componentButtonsAreaHeight = 0;
		if (tabPane instanceof BeeTabbedPane) {
			BeeTabbedPane pane = (BeeTabbedPane) tabPane;

			componentButtonsAreaHeight = pane.getComponentButtonAreaHeight();
		}
		return maxTabHeight + tabAreaInsets.top + tabAreaInsets.bottom + componentButtonsAreaHeight;
	}

	protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
		Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
		int buttonsAreaWidth = 0;
		if (tabPane instanceof BeeTabbedPane) {
			BeeTabbedPane pane = (BeeTabbedPane) tabPane;
			buttonsAreaWidth = pane.getButtonAreaWidth();
		}
		return maxTabWidth + tabAreaInsets.left + tabAreaInsets.right - buttonsAreaWidth;
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BeeTabbedPaneLayout();
	}

	public class BeeTabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			int buttonsAreaWidth = 0;
			int componentButtonsAreaHeight = 0;
			if (tabPane instanceof BeeTabbedPane) {
				BeeTabbedPane pane = (BeeTabbedPane) tabPane;
				buttonsAreaWidth = pane.getButtonAreaWidth();
				componentButtonsAreaHeight = pane.getComponentButtonAreaHeight();
			}
			return new Dimension(maxTabWidth + tabAreaInsets.left + tabAreaInsets.right - buttonsAreaWidth, maxTabHeight + tabAreaInsets.top + tabAreaInsets.bottom - componentButtonsAreaHeight);
		}

		@Override
		protected int preferredTabAreaHeight(int tabPlacement, int width) {
			// TODO Auto-generated method stub
			return maxTabHeight + tabAreaInsets.top + tabAreaInsets.bottom;
		}

		@Override
		protected int preferredTabAreaWidth(int tabPlacement, int height) {
			// TODO Auto-generated method stub
			// return super.preferredTabAreaWidth(tabPlacement, height);
			return maxTabWidth + tabAreaInsets.left + tabAreaInsets.right - BeeUIUtils.getDefaultFontSize() * 150;
		}

		protected void normalizeTabRuns(int tabPlacement, int tabCount, int start, int max) {

		}

		protected void rotateTabRuns(int tabPlacement, int selectedRun) {
			for (int i = 0; i < selectedRun; i++) {
				int save = tabRuns[0];
				for (int j = 1; j < runCount; j++) {
					tabRuns[j - 1] = tabRuns[j];
				}
				tabRuns[runCount - 1] = save;
			}
		}

		protected void calculateTabRects(int tabPlacement, int tabCount) {
			FontMetrics metrics = getFontMetrics();
			Dimension size = tabPane.getSize();

			int buttonsAreaWidth = 0;
			int componentButtonsAreaHeight = 0;
			if (tabPane instanceof BeeTabbedPane) {
				BeeTabbedPane pane = (BeeTabbedPane) tabPane;
				buttonsAreaWidth = pane.getButtonAreaWidth();
				componentButtonsAreaHeight = pane.getComponentButtonAreaHeight();
			}

			size.width = size.width - buttonsAreaWidth;
			size.height = size.height - componentButtonsAreaHeight;
			Insets insets = tabPane.getInsets();
			Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
			int fontHeight = metrics.getHeight();
			int selectedIndex = tabPane.getSelectedIndex();
			int tabRunOverlay;
			int i, j;
			int x, y;
			int returnAt;
			boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
			boolean leftToRight = true;// BasicGraphicsUtils.isLeftToRight(tabPane);

			//
			// Calculate bounds within which a tab run must fit
			//
			switch (tabPlacement) {
			case LEFT:
				maxTabWidth = calculateMaxTabWidth(tabPlacement);
				x = insets.left + tabAreaInsets.left;
				y = insets.top + tabAreaInsets.top;
				returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
				break;
			case RIGHT:
				maxTabWidth = calculateMaxTabWidth(tabPlacement);
				x = size.width - insets.right - tabAreaInsets.right - maxTabWidth;
				y = insets.top + tabAreaInsets.top;
				returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
				break;
			case BOTTOM:
				maxTabHeight = calculateMaxTabHeight(tabPlacement);
				x = insets.left + tabAreaInsets.left;
				y = size.height - insets.bottom - tabAreaInsets.bottom - maxTabHeight;
				returnAt = size.width - (insets.right + tabAreaInsets.right);
				break;
			case TOP:
			default:
				maxTabHeight = calculateMaxTabHeight(tabPlacement);
				x = insets.left + tabAreaInsets.left;
				y = insets.top + tabAreaInsets.top;
				returnAt = size.width - (insets.right + tabAreaInsets.right);
				break;
			}

			tabRunOverlay = getTabRunOverlay(tabPlacement);

			runCount = 0;
			selectedRun = -1;

			if (tabCount == 0) {
				return;
			}

			// Run through tabs and partition them into runs
			Rectangle rect;
			boolean gonext = true;
			while (gonext) {
				loop: for (i = 0; i < tabCount; i++) {
					rect = rects[i];

					if (!verticalTabRuns) {
						// Tabs on TOP or BOTTOM....
						if (i > 0) {
							rect.x = rects[i - 1].x + rects[i - 1].width;
						} else {
							tabRuns[0] = 0;
							runCount = 1;
							maxTabWidth = 0;
							rect.x = x;
						}
						rect.width = calculateTabWidth(tabPlacement, i, metrics);
						maxTabWidth = Math.max(maxTabWidth, rect.width);

						// Never move a TAB down a run if it is in the first column.
						// Even if there isn't enough room, moving it to a fresh
						// line won't help.
						if (rect.x != x && rect.x + rect.width > returnAt) {
							if (runCount > tabRuns.length - 1) {
								expandTabRunsArray();
							}
							tabRuns[runCount] = i;
							runCount++;
							rect.x = x;
						}
						// Initialize y position in case there's just one run
						rect.y = y;
						rect.height = maxTabHeight/* - 2 */;

					} else {
						// Tabs on LEFT or RIGHT...
						if (i > 0) {
							rect.y = rects[i - 1].y + rects[i - 1].height;
						} else {
							tabRuns[0] = 0;
							runCount = 1;
							maxTabHeight = 0;
							rect.y = y;
						}
						rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
						maxTabHeight = Math.max(maxTabHeight, rect.height);

						// Never move a TAB over a run if it is in the first run.
						// Even if there isn't enough room, moving it to a fresh
						// column won't help.
						if (rect.y != y && rect.y + rect.height > returnAt) {
							if (runCount > tabRuns.length - 1) {
								expandTabRunsArray();
							}
							tabRuns[runCount] = i;
							runCount++;
							rect.y = y;
						}
						// Initialize x position in case there's just one column
						rect.x = x;
						rect.width = maxTabWidth/* - 2 */;

					}
					gonext = false;
					if (i == selectedIndex) {
						selectedRun = runCount - 1;

						boolean test = true;
						if (selectedRun != 0 && test) {
							if (i - 1 >= 0) {
								Rectangle pre = rects[selectedIndex - 1];
								// Component preComp = tabPane.getComponentAt(selectedIndex - 1);
								rects[selectedIndex - 1] = rects[selectedIndex];
								rects[selectedIndex] = pre;
								// tabPane.insertTab(tabPane.getTitleAt(selectedIndex),
								// tabPane.getIconAt(selectedIndex),
								// tabPane.getComponent(selectedIndex), tabPane.getToolTipTextAt(selectedIndex),
								// selectedIndex - 1);
								try {
									// TODO
									// Component selectedComp = tabPane.getSelectedComponent();
									Field pages = JTabbedPane.class.getDeclaredField("pages");
									pages.setAccessible(true);
									List list = (List) pages.get(tabPane);
									Object obj = list.remove(selectedIndex);
									list.add(selectedIndex - 1, obj);
									selectedIndex = selectedIndex - 1;

									int selectedRunRow = getRunForTab(tabCount, selectedIndex);
									// if (selectedRunRow == 0) {
									// tabPane.setSelectedIndex(selectedIndex);
									// tabPane.getModel().setSelectedIndex(selectedIndex);
									// }

								} catch (Exception e) {
									e.printStackTrace();
								}
								gonext = true;
								break loop;
							}
						}
					}

				}
			}
			tabPane.getModel().setSelectedIndex(selectedIndex);
			// tabPane.setSelectedIndex(selectedIndex);

			if (runCount > 1) {
				for (i = 1; i < runCount; i++) {
					int start = tabRuns[i];
					int next = tabRuns[i == (runCount - 1) ? 0 : i + 1];
					int end = (next != 0 ? next - 1 : tabCount - 1);

					if (!verticalTabRuns) {
						if (tabPlacement == BOTTOM) {
							y += (maxTabHeight + tabRunOverlay * 10);
						} else {
							y -= (maxTabHeight + tabRunOverlay * 10);
						}
						for (j = start; j <= end; j++) {
							rect = rects[j];
							rect.y = y;
							rect.x += getTabRunIndent(tabPlacement, i);
						}

					}
				}
			}
			// return;

			// if (runCount > 1) {
			// // Re-distribute tabs in case last run has leftover space
			// normalizeTabRuns(tabPlacement, tabCount, verticalTabRuns ? y : x, returnAt);
			//
			// selectedRun = getRunForTab(tabCount, selectedIndex);
			//
			// // Rotate run array so that selected run is first
			// if (shouldRotateTabRuns(tabPlacement)) {
			// // rotateTabRuns(tabPlacement, selectedRun);
			// }
			// }
			//
			// // Step through runs from back to front to calculate
			// // tab y locations and to pad runs appropriately
			// for (i = runCount - 1; i >= 0; i--) {
			// int start = tabRuns[i];
			// int next = tabRuns[i == (runCount - 1) ? 0 : i + 1];
			// int end = (next != 0 ? next - 1 : tabCount - 1);
			// if (!verticalTabRuns) {
			// for (j = start; j <= end; j++) {
			// rect = rects[j];
			// rect.y = y;
			// rect.x += getTabRunIndent(tabPlacement, i);
			// }
			// if (shouldPadTabRun(tabPlacement, i)) {
			// padTabRun(tabPlacement, start, end, returnAt);
			// }
			// if (tabPlacement == BOTTOM) {
			// y -= (maxTabHeight - tabRunOverlay);
			// } else {
			// y += (maxTabHeight - tabRunOverlay);
			// }
			// } else {
			// for (j = start; j <= end; j++) {
			// rect = rects[j];
			// rect.x = x;
			// rect.y += getTabRunIndent(tabPlacement, i);
			// }
			// if (shouldPadTabRun(tabPlacement, i)) {
			// padTabRun(tabPlacement, start, end, returnAt);
			// }
			// if (tabPlacement == RIGHT) {
			// x -= (maxTabWidth - tabRunOverlay);
			// } else {
			// x += (maxTabWidth - tabRunOverlay);
			// }
			// }
			// }
			//
			// // Pad the selected tab so that it appears raised in front
			// padSelectedTab(tabPlacement, selectedIndex);
			//
			// // if right to left and tab placement on the top or
			// // the bottom, flip x positions and adjust by widths
			// if (!leftToRight && !verticalTabRuns) {
			// int rightMargin = size.width - (insets.right + tabAreaInsets.right);
			// for (i = 0; i < tabCount; i++) {
			// rects[i].x = rightMargin - rects[i].x - rects[i].width;
			// }
			// }
		}

	}

}
