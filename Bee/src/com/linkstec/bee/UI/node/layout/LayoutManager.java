package com.linkstec.bee.UI.node.layout;

import com.mxgraph.util.mxRectangle;

public interface LayoutManager {

	public void layout();

	public void setLayout(ILayout layout);

	public void layout(mxRectangle rect);

	public ILayout getLayout();
}
