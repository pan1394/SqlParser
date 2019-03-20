package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.BasicGraph;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BGroupNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4937487201796289491L;

	private String titleBID;
	private int space = 12;
	private int titleHeight = 30;
	private boolean folded = false;

	public BGroupNode() {
		this.setGeometry(new mxGeometry());
		this.setVertex(true);
		this.setStyle("dashed=false;strokeColor=black;strokeWidth=0.5;spacingLeft=10;fillColor=F0F8FF");

		BNode node = new GroupTitle();
		node.getGeometry().setHeight(titleHeight);
		node.getGeometry().setOffset(new mxPoint(space, 0));
		titleBID = node.getId();
		this.insert(node);
	}

	@Override
	public void cellFolded(BasicGraph graph, boolean fold) {
		folded = fold;
		mxICell cell = this.getCellById(titleBID);
		if (cell != null) {
			if (fold) {
				this.setValue(cell.getValue());
			} else {
				this.setValue("");
			}
		}
		double height = this.getGeometry().getHeight();
		double foldHeight = this.getGeometry().getAlternateBounds().getHeight();

		if (!fold) {
			height = foldHeight;
			foldHeight = this.getGeometry().getHeight();
		}

		Object[] cells = graph.getCellsBeyond(0, this.getGeometry().getY() + height + 2, this.getParent(), false, true);

		double move = height - foldHeight;
		graph.getModel().beginUpdate();
		for (Object obj : cells) {
			if (obj instanceof BNode) {
				if (!obj.equals(this)) {
					BNode node = (BNode) obj;
					if (node.isVertex()) {
						mxGeometry geo = node.getGeometry();
						if (fold) {
							geo.setY(geo.getY() + move);
						} else {
							geo.setY(geo.getY() - move);
						}
					}
				}
			}
		}
		graph.getModel().endUpdate();
		graph.refresh();
	}

	public String getTitle() {
		mxICell cell = this.getCellById(titleBID);
		if (cell != null) {
			return (String) cell.getValue();
		}
		return null;
	}

	public void setTitle(String title) {
		mxICell cell = this.getCellById(titleBID);
		if (cell != null) {
			cell.setValue(title);
		}
	}

	@Override
	public boolean isResizable() {
		if (folded) {
			return false;
		}
		return super.isResizable();
	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		this.resized();
	}

	public void resized() {
		int count = this.getChildCount();
		this.setValue("");
		mxGeometry geo = this.getGeometry();
		for (int i = 0; i < count; i++) {
			mxICell cell = this.getChildAt(i);
			if (cell instanceof BNode) {
				BNode node = (BNode) cell;
				if (node.getBid().equals(titleBID)) {
					geo.setAlternateBounds(
							new mxRectangle(geo.getX() + space, geo.getY(), geo.getWidth() - space * 2, titleHeight));
					node.getGeometry().setWidth(geo.getWidth() - space * 2);
				} else {
					mxGeometry g = node.getGeometry();
					if (g.getX() < space) {
						g.setX(space);
					}

					if (g.getX() + g.getWidth() > geo.getWidth() - space) {
						geo.setWidth(g.getX() + g.getWidth() + space);
					}
					if (g.getY() < this.titleHeight + this.space) {
						g.setY(this.titleHeight + this.space);
					}
					if (g.getY() + g.getHeight() > geo.getHeight() - space) {
						geo.setHeight(g.getY() + g.getHeight() + space);
					}
				}
			}
		}

	}

	@Override
	public void paint(Graphics g, mxCellState state) {
		Rectangle rect = state.getRectangle();
		g.setColor(Color.BLACK);

		FontMetrics mericts = g.getFontMetrics();

		int height = mericts.getHeight();
		height = (int) (height * 1.8);

		g.drawLine(rect.x, rect.y + height, rect.x + rect.width, rect.y + height);
	}

	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	public static class GroupTitle extends BNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4113952038339111060L;

		public GroupTitle() {
			this.setVertex(true);
			this.setStyle("rectangle;dashed=false;opacity=0");
			this.setEditable(true);
			mxGeometry geo = this.getGeometry();
			geo.setRelative(true);
			geo.setX(0);
			geo.setY(0);
			this.setConnectable(false);
			this.setMoveable(false);
			this.setSelectable(false);
			this.setValue("グループ");
		}

	}
}
