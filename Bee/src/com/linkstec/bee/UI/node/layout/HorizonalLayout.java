package com.linkstec.bee.UI.node.layout;

import java.io.Serializable;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.core.Application;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class HorizonalLayout extends LayoutBasic implements Serializable, ILayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2758637509623123184L;

	private boolean containerPack = false;

	private boolean heightSame = false;
	private boolean returnAllowed = true;

	public boolean isReturnAllowed() {
		return returnAllowed;
	}

	public void setReturnAllowed(boolean returnAllowed) {
		this.returnAllowed = returnAllowed;
	}

	public boolean isHeightSame() {
		return heightSame;
	}

	public void setHeightSame(boolean heightSame) {
		this.heightSame = heightSame;
	}

	public HorizonalLayout() {

	}

	@Override
	public void layout() {
		BasicNode Container = this.getContainer();
		if (!Application.INSTANCE_COMPLETE)
			return;

		int spacingLeft = Container.getSpacingLeft();
		int spacingRight = Container.getSpacingRight();
		double x = this.Spacing + spacingLeft;
		double y = this.Spacing;

		if (Container.getUserAttribute("fixedWidth") != null) {
			this.returnAllowed = false;
		}

		int count = Container.getChildCount();
		int nodeSize = 0;
		double childMxHeight = -1;
		double totalWidth = 0;

		BasicNode lastNode = null;
		boolean returned = false;
		for (int i = 0; i < count; i++) {
			mxICell cell = Container.getChildAt(i);

			if (cell instanceof ReferNode && cell.getGeometry().getOffset() != null) {
				y = y + cell.getGeometry().getOffset().getY() + cell.getGeometry().getHeight();
				double width = cell.getGeometry().getOffset().getX() + 600;// cell.getGeometry().getWidth();
				totalWidth = Math.max(width, totalWidth);
				nodeSize++;
				lastNode = (BasicNode) cell;
				continue;
			}
			if (cell instanceof BasicNode) {
				BasicNode node = (BasicNode) cell;
				if (node.getLayout() != null) {
					node.getLayout().beforeContainerLayout();
				}
				mxGeometry pv = node.getGeometry();
				if (!node.isVisible()) {
					continue;
				}
				if (pv.getWidth() == 0 && node.getChildCount() == 0 && (node.getValue() == null || node.getValue().toString().equals(""))) {
					continue;
				}
				node.reshape();

				pv.setRelative(true);
				// if the node before this is returned ,make this node under the node before
				if (returned) {
					x = this.Spacing;
					y = y + childMxHeight + this.betweenSpacing;
				}

				int marginLeft = node.getMarginLeft();
				int marginRight = node.getMarginRight();

				x = marginLeft + x;

				pv.setOffset(new mxPoint(x, y));

				returned = false;
				if (this.maxWidth > 0 && this.returnAllowed) {
					if (x + pv.getWidth() + this.Spacing > this.maxWidth) {
						if (i != 0) {
							returned = true;
						}
					}
				}

				double xbeforereturn = x;
				double ybeforereturn = y;
				if (returned) {
					x = this.Spacing;
					y = y + childMxHeight + this.betweenSpacing;
					pv.setOffset(new mxPoint(x, y));
					childMxHeight = 0;
				}

				if (x + pv.getWidth() + this.Spacing > this.maxWidth && this.maxWidth > 0) {

					if (node.getChildCount() != 0) {
						if (node.getLayout() != null) {
							node.getLayout().setMaxWidth(maxWidth - x - this.Spacing);
							node.getLayout().layout();
							node.getLayout().setMaxWidth(-1);
							pv = node.getGeometry();
							double widthafter = pv.getWidth();

							// get back to before position if it is good after self adjust
							if (widthafter + xbeforereturn + this.Spacing <= this.maxWidth) {
								x = xbeforereturn;
								y = ybeforereturn;
								pv.setOffset(new mxPoint(x, y));
								returned = true;
							}
						}
					}
				}

				childMxHeight = Math.max(childMxHeight, pv.getHeight());

				x = x + pv.getWidth() + this.betweenSpacing + marginRight + spacingRight;
				totalWidth = Math.max(x, totalWidth);

				nodeSize++;
				lastNode = node;
			}

		}

		// totalWidth = totalWidth + spacingRight;

		if (this.betweenSpacing < 0) {
			totalWidth = totalWidth - betweenSpacing;
		}

		// fit the container if any child there
		mxGeometry pv = Container.getGeometry();
		if (nodeSize != 0) {

			if (this.containerPack || pv.getWidth() < totalWidth + this.Spacing) {

				pv.setWidth(totalWidth + this.Spacing);
			}
			if (lastNode != null) {
				pv.setHeight(y + lastNode.getGeometry().getHeight() + this.Spacing);
			}
		}

		for (LayoutListener listener : this.listeners) {
			listener.layoutCompleted(Container);
		}

	}

	public void setContainerPack(boolean containerPack) {
		this.containerPack = containerPack;
	}

	public boolean isContainerPack() {
		return this.containerPack;
	}

}
