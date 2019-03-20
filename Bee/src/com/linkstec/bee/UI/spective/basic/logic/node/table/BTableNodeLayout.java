package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BTableNodeLayout {
	private BTableGroupNode container;
	protected int space = 5;

	public BTableNodeLayout(BTableGroupNode container, BasicLogicSheet sheet) {
		this.container = container;
		// this.sheet = sheet;
		this.layout(sheet);
	}

	public void layout(BasicLogicSheet sheet) {
		this.changeOrder();
		int count = container.getChildCount();
		//
		double y = space + 40;
		double x = 0;
		double rowHeight = 0;
		int currentRow = 0;
		boolean first = true;
		for (int i = 0; i < count; i++) {
			mxICell child = container.getChildAt(i);
			if (child instanceof BTableValueNode) {
				BTableValueNode node = (BTableValueNode) child;
				node.fitWidth(sheet);

				mxGeometry geo = child.getGeometry();

				int row = node.getRow();
				if (row == currentRow) {
					// from the second value
					rowHeight = Math.max(rowHeight, geo.getHeight());
					if (first) {
						x = 12 - space;
						first = false;
					}
					geo.setX(x + space);
					//
				} else {
					// new line
					int indent = (int) geo.getX() / 50;
					x = 12 + indent * 50;
					node.setIndent(indent);
					geo.setX(x);
					y = y + rowHeight + space;
				}
				x = geo.getX() + geo.getWidth();

				currentRow = row;
				geo.setY(y);
			}

		}
		container.fitHeight();
	}

	public void changeOrder() {
		List<mxICell> cells = new ArrayList<mxICell>();
		cells.add(container.getChildAt(0));
		this.changeOrder(cells, 0);
	}

	public void changeOrder(List<mxICell> cells, int rowIndex) {
		List<mxICell> row = new ArrayList<mxICell>();

		mxICell first = selectFirst(cells, rowIndex);
		if (first == null) {
			return;
		}
		row.add(first);
		this.setRowNumber(first, rowIndex);

		selectRow(first, row, rowIndex);

		Collections.sort(row, new Comparator<mxICell>() {

			@Override
			public int compare(mxICell o1, mxICell o2) {

				double d = o1.getGeometry().getX() - o2.getGeometry().getX();
				if (d > 0) {
					return 1;
				} else if (d == 0) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		cells.addAll(row);
		if (cells.size() == container.getChildCount()) {

			int count = container.getChildCount();
			for (int i = count - 1; i > 0; i--) {
				mxICell child = container.getChildAt(i);
				child.removeFromParent();
			}
			for (mxICell cell : cells) {
				container.insert(cell);
			}
		} else {
			changeOrder(cells, (rowIndex + 1));
		}
	}

	private void setRowNumber(mxICell cell, int row) {
		if (cell instanceof BTableValueNode) {
			BTableValueNode node = (BTableValueNode) cell;
			node.setRow(row);
		}
	}

	public mxICell selectFirst(List<mxICell> cells, int rowIndex) {

		double minx = 10000;
		double miny = 10000;
		mxICell first = null;
		int count = container.getChildCount();
		for (int i = 0; i < count; i++) {

			mxICell child = container.getChildAt(i);
			if (!cells.contains(child)) {
				double x = child.getGeometry().getX();
				double y = child.getGeometry().getY();
				if (first == null) {
					first = child;
				} else {
					if (y < miny) {
						first = child;
					} else if (y == miny) {
						if (x < minx) {
							first = child;
						}
					}
				}
				minx = first.getGeometry().getX();
				miny = first.getGeometry().getY();
			}
		}
		return first;
	}

	public void selectRow(mxICell first, List<mxICell> cells, int rowIndex) {
		mxGeometry geo = first.getGeometry();
		int count = container.getChildCount();
		for (int i = 0; i < count; i++) {

			mxICell child = container.getChildAt(i);
			if (!cells.contains(child) && child instanceof BTableValueNode) {
				double y = child.getGeometry().getY();

				if (y >= geo.getY() && y < geo.getHeight() / 2 + geo.getY()) {
					cells.add(child);
					this.setRowNumber(child, rowIndex);
				}
			}
		}
	}

}
