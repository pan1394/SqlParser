package com.linkstec.bee.UI.node.layout;

import java.io.Serializable;
import java.util.ArrayList;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.core.Debug;
import com.mxgraph.util.mxRectangle;

public class LayoutBasic implements ILayoutBasic, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4868920887113503132L;

	protected boolean singleChild = false;
	protected ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();
	protected double maxWidth = -1;
	protected double Spacing = BeeConstants.VALUE_NODE_SPACING;
	protected double betweenSpacing = 0;
	private BasicNode container;

	@Override
	public void setContainer(BasicNode node) {
		this.container = node;
		// int hash = this.hashCode();
		// ObjectHolder.setObject(String.valueOf(hash), node);
	}

	@Override
	public BasicNode getContainer() {
		// int hash = this.hashCode();
		// return (BasicNode) ObjectHolder.getObject(String.valueOf(hash));
		return this.container;
	}

	@Override
	public boolean isSingleChild() {
		return singleChild;
	}

	@Override
	public void setSingleChild(boolean singleChild) {
		this.singleChild = singleChild;
	}

	public void addLayoutListener(LayoutListener listener) {
		this.listeners.add(listener);
	}

	public void removeLayoutListener(LayoutListener listener) {
		this.listeners.remove(listener);
	}

	public void clearLayoutListener() {
		this.listeners.clear();
	}

	public void setMaxWidth(double width) {
		this.maxWidth = width;
	}

	public double getMaxWidth() {

		return this.maxWidth;
	}

	public void setSpacing(double spacing) {
		this.Spacing = spacing;

	}

	public void layout(mxRectangle rect) {

	}

	public double getBetweenSpacing() {
		return betweenSpacing;
	}

	public void setBetweenSpacing(double betweenSpacing) {
		this.betweenSpacing = betweenSpacing;
	}

	public void addNode(BasicNode node) {
		addNode(node, -1);
	}

	public void addNode(BasicNode node, int index) {
		BasicNode Container = this.getContainer();
		int count = Container.getChildCount();
		if (this.isSingleChild()) {

			for (int i = count - 1; i >= 0; i--) {
				if (Container.getChildAt(i) instanceof BasicNode) {
					Container.remove(i);
				}
			}

		}

		if (node == null) {
			Debug.d();
		}

		node.getGeometry().setRelative(true);
		node.getGeometry().setX(0);
		node.getGeometry().setY(0);

		if (index < Container.getChildCount() && index > -1) {
			Container.insert(node, index);
		} else {
			Container.insert(node);
		}

	}

	public void removeNode(BasicNode node) {
		BasicNode Container = this.getContainer();
		Container.remove(node);
		// this.layout();
	}

	public void addNode(BasicNode node, double height, int index) {
		this.addNode(node);

	}

	@Override
	public void beforeContainerLayout() {

	}

}
