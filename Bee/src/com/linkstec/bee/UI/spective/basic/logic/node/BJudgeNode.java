package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IJudgeCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILogicConnector;

public class BJudgeNode extends BLogicNode implements IJudgeCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4951014687832751290L;

	public BJudgeNode(BLogic logic) {
		super(logic);
	}

	@Override
	public List<ILogicCell> getYes() {
		List<ILogicCell> cells = new ArrayList<ILogicCell>();
		List<ILogicConnector> list = this.getConnectors();
		for (ILogicConnector cell : list) {
			if (cell.getType() == ILogicConnector.YES) {
				ILogicCell logic = cell.getNext();
				while (true) {
					cells.add(logic);
					if (logic instanceof ILogicCell) {
						ILogicCell unit = (ILogicCell) logic;
						logic = BasicGenUtils.getNext((BNode) unit, logic.getLogic().getPath());
						if (logic == null) {
							break;
						}
					} else {
						break;
					}
				}
			}
		}
		return cells;
	}

	@Override
	public List<ILogicCell> getNo() {
		List<ILogicCell> cells = new ArrayList<ILogicCell>();
		List<ILogicConnector> list = this.getConnectors();
		for (ILogicConnector cell : list) {
			if (cell.getType() == ILogicConnector.NO) {
				ILogicCell logic = cell.getNext();
				while (true) {
					cells.add(logic);
					if (logic instanceof ILogicCell) {
						ILogicCell unit = (ILogicCell) logic;
						logic = BasicGenUtils.getNext((BNode) unit, logic.getLogic().getPath());
						if (logic == null) {
							break;
						}
					} else {
						break;
					}
				}
			}
		}
		return cells;
	}
}
