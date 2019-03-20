package com.linkstec.bee.UI.look;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;

public class BeeTreeUI extends WindowsTreeUI {
	JTree t;
	AbstractLayoutCache cache;

	BeeTreeUI(JTree t) {
		this.t = t;
	}

	@Override
	public Icon getExpandedIcon() {
		return BeeConstants.TREE_EXPANDED_ICON;
	}

	@Override
	protected int getRowX(int row, int depth) {

		if (t instanceof BeeTree) {
			BeeTree b = (BeeTree) t;
			if (b.isDialog()) {
				int x = super.getRowX(row, depth);
				if (depth == 1) {
					return 0;
				}
				return x;
			}
		}
		int x = super.getRowX(row, depth);
		if (depth > 1) {
			x = (int) (x + BeeUIUtils.getDefaultFontSize() * 1.5);
		} else {
			x = x + BeeUIUtils.getDefaultFontSize();
		}
		return x;
	}

	@Override
	public int getBaseline(JComponent c, int width, int height) {
		if (t instanceof BeeTree) {
			BeeTree b = (BeeTree) t;
			if (b.isDialog()) {
				int base = super.getBaseline(c, width, height);
				return base;
			}
		}
		int base = super.getBaseline(c, width, height);
		base = base + BeeUIUtils.getDefaultFontSize();
		return base;
	}

	@Override
	public Icon getCollapsedIcon() {
		return BeeConstants.TREE_COLLAPSED_ICON;
	}

	public static ComponentUI createUI(JComponent c) {

		return new BeeTreeUI((JTree) c);
	}

	@Override
	protected void updateSize() {
		if (this.treeState != null) {
			this.cache = treeState;
		}
		try {

			super.updateSize();

		} catch (Exception e) {

		}
		if (this.tree == null) {
			this.tree = t;
		}
		if (this.treeState == null && this.cache != null) {
			this.treeState = cache;
		}
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		if (this.treeModel != null) {
			try {
				super.paint(g, c);
			} catch (Exception e) {

			}
		}
	}

	@Override
	protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
		if (this.rendererPane != null) {
			super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
		}
	}

	@Override
	protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {

	}

	@Override
	protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {

	}

}
