package com.linkstec.bee.UI.node.layout;

import com.linkstec.bee.UI.node.BasicNode;
import com.mxgraph.util.mxRectangle;

public interface ILayout extends ILayoutBasic {

	public void addNode(BasicNode node);

	public void addNode(BasicNode node, double height, int index);

	public void addNode(BasicNode node, int index);

	public void setSpacing(double spacing);

	public void removeNode(BasicNode node);

	public void layout();

	public void layout(mxRectangle rect);

	public void addLayoutListener(LayoutListener listener);

	public void removeLayoutListener(LayoutListener listener);

	public void clearLayoutListener();

	public double getBetweenSpacing();

	public void setBetweenSpacing(double betweenSpacing);

	public void setMaxWidth(double width);

	public double getMaxWidth();

}
