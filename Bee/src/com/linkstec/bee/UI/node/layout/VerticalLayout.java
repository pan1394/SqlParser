package com.linkstec.bee.UI.node.layout;

import java.io.Serializable;
import java.util.ArrayList;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class VerticalLayout extends LayoutBasic implements Serializable, ILayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7757478268357268805L;

	private double Spacing = BeeConstants.NODE_SPACING;
	private double Height = BeeConstants.NODE_SPACING;
	private double heightAfterlayout = 0;

	private double betweenSpacing = 0;
	private boolean childFitWidth = false;
	private double maxWidth = -1;

	public VerticalLayout() {

	}

	public boolean isChildFitWidth() {
		return childFitWidth;
	}

	public void setChildFitWidth(boolean childFitWidth) {
		this.childFitWidth = childFitWidth;
	}

	public double getBetweenSpacing() {
		return betweenSpacing;
	}

	public void setBetweenSpacing(double betweenSpacing) {
		this.betweenSpacing = betweenSpacing;
	}

	private ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();

	public void addNode(BasicNode node) {
		this.addNode(node, -1, -1);
	}

	public void setSpacing(double spacing) {
		this.Spacing = spacing;
	}

	public void addNode(BasicNode node, int index) {
		BasicNode Container = this.getContainer();
		if (this.isSingleChild()) {
			int count = Container.getChildCount();
			if (this.isSingleChild()) {
				for (int i = count - 1; i >= 0; i--) {
					if (Container.getChildAt(i) instanceof BasicNode) {
						Container.remove(i);
					}
				}
			}
		}

		if (index >= 0) {
			Container.insert(node, index);
		} else {
			Container.insert(node);
		}

		node.getGeometry().setRelative(true);
		node.getGeometry().setX(0);
		node.getGeometry().setY(0);
	}

	public void layout() {
		BasicNode Container = this.getContainer();
		if (!Application.INSTANCE_COMPLETE)
			return;
		if (Container.isFolded()) {
			return;
		}

		if (Container.getUserAttribute("fixedWidth") != null) {
			this.maxWidth = -1;
		}

		this.Height = this.Spacing;
		int count = Container.getChildCount();

		// the max width to be calculated
		double layouMxWidth = Container.getGeometry().getWidth() - Spacing - Spacing;
		for (int i = 0; i < count; i++) {
			Object obj = Container.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				if (node.getLayout() != null) {
					node.getLayout().beforeContainerLayout();
				}

				if (node instanceof NoteNode) {
					node.reshape();
				}
				Double fixed = (Double) node.getUserAttribute("fixedWidth");
				if (fixed == null) {
					layouMxWidth = Math.max(layouMxWidth, node.getGeometry().getWidth());
				} else {
					layouMxWidth = Math.max(layouMxWidth, fixed.doubleValue());
				}

			}
		}
		int nodeSize = 0;
		for (int i = 0; i < count; i++) {
			Object obj = Container.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				if (!node.isVisible()) {
					continue;
				}
				mxGeometry pv = node.getGeometry();
				pv.setRelative(true);
				if (this.childFitWidth) {
					if (!(Container instanceof MethodNode && Container.getParent() instanceof BLockNode)) {
						pv.setWidth(layouMxWidth);
					}
				}
				int absoluteY = node.getAbsoluteY();
				int absoluteX = node.getAbsoluteX();
				int offsetY = node.getOffsetY();
				int offsetX = node.getOffsetX();

				if (node.isNextToLast()) {
					Height = Height - this.betweenSpacing;
				}

				if (absoluteY != 0) {
					pv.setOffset(new mxPoint(this.Spacing + offsetX + absoluteX, absoluteY + offsetY));
				} else {
					pv.setOffset(new mxPoint(this.Spacing + offsetX + absoluteX, Height + offsetY));
				}

				Height = Height + this.betweenSpacing + pv.getHeight() + offsetY;
				nodeSize++;
			}
		}
		layouMxWidth = layouMxWidth + this.Spacing + Spacing;
		Container.getGeometry().setWidth(layouMxWidth);
		// if the maxWidth works
		if (maxWidth > 0) {

			// the width calculated above is bigger than the mxWidth
			if (maxWidth < layouMxWidth) {

				// the ratio that should be used after to get children fitted
				// keep the spacing
				double ratio = (this.maxWidth - this.Spacing - Spacing) / (layouMxWidth - this.Spacing - Spacing);

				// forced to fit the container width to maxWidth;
				Container.getGeometry().setWidth(maxWidth);

				// calculate again
				this.Height = this.Spacing;

				// confirm all children
				for (int i = 0; i < count; i++) {

					Object obj = Container.getChildAt(i);

					if (obj instanceof BasicNode) {

						BasicNode node = (BasicNode) obj;
						mxGeometry pv = node.getGeometry();
						if (node instanceof MethodNode) {

						} else {
							// if the child width is bigger than maxWidth
							if (node.getGeometry().getWidth() + this.Spacing + Spacing > maxWidth) {

								// the correct width for the child
								node.getGeometry().setWidth(node.getGeometry().getWidth() * ratio);

								// if child has children further more
								ILayout layout = node.getLayout();
								if (layout != null) {
									layout.setMaxWidth(node.getGeometry().getWidth());
									layout.layout();
									layout.setMaxWidth(-1);
								}

							}
							pv.setOffset(new mxPoint(this.Spacing, Height));
							Height = Height + this.betweenSpacing + pv.getHeight();
						}
					}
				}

			}
			// the width calculated above is not bigger than the mxWidth
			// else {

			// if the container itself is bigger than maxWidth
			// if (Container.getGeometry().getWidth() + this.Spacing + Spacing > maxWidth) {
			// Container.getGeometry().setWidth(maxWidth);
			// } else {
			if (Container.getUserAttribute("fixWidth") == null && !(Container instanceof BLogicBody)) {
				Container.getGeometry().setWidth(maxWidth - this.Spacing * 2);
			}
			// }
			// }
		}

		if (nodeSize != 0) {

			Container.getGeometry().setHeight(Height + this.Spacing);
		}

		this.heightAfterlayout = Container.getGeometry().getHeight();

		for (LayoutListener listener : this.listeners) {
			listener.layoutCompleted(Container);
		}
	}

	public void layout(mxRectangle rect) {
		BasicNode Container = this.getContainer();
		if (rect == null) {
			this.layout();
			return;
		}

		double targetWidth = rect.getWidth();
		double targetHeight = rect.getHeight();

		targetWidth = targetWidth - this.Spacing;
		targetHeight = targetHeight - this.Spacing;

		double currentHeight = heightAfterlayout - this.Spacing;

		int count = Container.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = Container.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				mxGeometry pv = node.getGeometry();

				double h = pv.getHeight();
				if (!node.isResizeable()) {
					targetHeight = targetHeight - h;
					currentHeight = currentHeight - h;
				}
			}
		}

		double heightRatio = targetHeight / currentHeight;

		for (int i = 0; i < count; i++) {
			Object obj = Container.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				mxGeometry pv = node.getGeometry();
				if (node.isResizeable()) {
					pv.setHeight(pv.getHeight() * heightRatio);
				}
			}
		}
		this.layout();

	}

	@Override
	public void removeNode(BasicNode node) {
		BasicNode Container = this.getContainer();
		Container.remove(node);
		// this.layout();
	}

	@Override
	public void addLayoutListener(LayoutListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeLayoutListener(LayoutListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void clearLayoutListener() {
		this.listeners.clear();
	}

	@Override
	public void setMaxWidth(double width) {
		this.maxWidth = width;
	}

	@Override
	public double getMaxWidth() {
		return this.maxWidth;
	}

	@Override
	public void addNode(BasicNode node, double height, int index) {
		this.addNode(node, index);
	}

}
