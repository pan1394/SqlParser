package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.core.fw.basic.IJudgeCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILogicConnector;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BLogicConnector extends BNode implements ILogicConnector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7150190540502430947L;

	private int type = -1;

	public BLogicConnector() {
		this.setGeometry(new mxGeometry());
		this.setEdge(true);
		this.setStyle("edgeStyle=sideToSideEdgeStyle;strokeWidth=0.5;strokeColor=gray;dashed=false");
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		if (type == YES) {
			return "Yes";
		} else if (type == NO) {
			return "No";
		} else if (type == LOOP) {
			return "Loop";
		}
		return null;
	}

	public void connected() {
		mxICell source = this.getSource();
		int count = source.getEdgeCount();
		if (source instanceof IJudgeCell) {
			// IJudgeCell cell = (IJudgeCell) source;
			this.setType(YES);
			for (int i = 0; i < count; i++) {
				mxICell edge = source.getEdgeAt(i);
				if (edge instanceof BLogicConnector) {
					BLogicConnector c = (BLogicConnector) edge;
					if (!c.equals(this)) {
						if (c.getType() == YES) {
							this.setType(NO);
						}
					}
				}
			}

		}
	}

	@Override
	public ILogicCell getNext() {
		mxICell target = this.getTarget();
		if (target instanceof ILogicCell) {
			return (ILogicCell) target;
		}
		return null;
	}
}
