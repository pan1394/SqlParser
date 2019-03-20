package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicSheet;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BProcessNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7748413407722177459L;
	private String startBID;
	private BLogicNode parentLogic;
	private transient BLogicSheet sheet;

	public BProcessNode(BLogicSheet sheet, String name, String logicName) {

		this.sheet = sheet;
		mxGeometry geo = this.getGeometry();
		geo.setWidth(300);
		geo.setHeight(400);
		geo.setRelative(false);

		this.setVertex(true);
		this.setStyle("dashed=true;strokWidth=0.5;strokeColor=gray;opacity=0");

		BStart start = new BStart(name, logicName) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4238407074483717406L;

			@Override
			public void nameChanged(String name) {
				processNameChanged(name);
			}

			@Override
			public void logicNameChanged(String name) {
				processLogicNameChanged(name);
			}

		};
		this.startBID = start.getId();
		BEnd end = new BEnd();

		this.insert(start);
		this.insert(end);

		BActionConnector cell = new BActionConnector();
		cell.setSource(start);
		cell.setTarget(end);
		this.insert(cell);
	}

	public void processNameChanged(String name) {
		if (this.sheet != null) {
			this.sheet.setModelName(name);
		}
	}

	public void processLogicNameChanged(String name) {
		if (this.sheet != null) {
			this.sheet.setModelLogicName(name);
		}
	}

	public BStart getStart() {
		return (BStart) this.getCellById(startBID);
	}

	public BLogicNode getParentLogic() {
		return parentLogic;
	}

	public void setParentLogic(BLogicNode parentLogic) {
		this.parentLogic = parentLogic;
	}

	public List<BLogic> getLogics() {
		List<BLogic> logics = new ArrayList<BLogic>();
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BLogicNode) {
				BLogicNode node = (BLogicNode) child;
				logics.add(node.getLogic());
			}
		}
		return logics;
	}

}
