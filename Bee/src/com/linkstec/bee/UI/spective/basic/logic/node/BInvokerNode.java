package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.model.InvokerLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BInvokerNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364387741673626110L;
	private InvokerLogic logic;

	public BInvokerNode() {
		this.setVertex(true);

		this.setStyle(
				"shape=rectangle;align=center;rounded=1;fontColor=white;strokeWidth=0.5;strokeColor=black;align=center;fillColor="
						+ BeeConstants.ELEGANT_YELLOW_COLOR);

		mxGeometry geo = this.getGeometry();
		geo.setWidth(100);
		geo.setHeight(50);
		geo.setRelative(false);
		geo.setX(0);
		geo.setY(0);
		geo.setOffset(new mxPoint(0, 0));
	}

	public void setLogic(InvokerLogic logic) {
		this.logic = logic;
	}

	@Override
	public Object getValue() {
		return this.logic.getDesc();
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

}
