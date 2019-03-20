package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILogicConnector;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BLogicNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5175064909737769670L;
	protected BLogic logic;
	private int height = BeeConstants.LINE_HEIGHT;

	public BLogicNode(BLogic logic) {
		this.logic = logic;
		mxGeometry geo = this.getGeometry();

		geo.setHeight(50);
		geo.setWidth(100);
		geo.setOffset(new mxPoint(0, 0));
		this.setValue(logic.getDesc());
		this.setVertex(true);
		if (logic.isReturnBoolean()) {
			this.setStyle("rhombus;align=center;strokeWidth=0.5;strokeColor=gray;fillColor=white;");
		} else {
			this.setStyle("rounded=1;align=center;strokeWidth=0.5;strokeColor=gray;fillColor=white;");
		}
		this.setCollapsed(false);
	}

	public BLogic getLogic() {
		return logic;
	}

	@Override
	public Object getValue() {
		if (logic == null) {
			return null;
		}
		return this.logic.getDesc();
	}

	public void insertNumberNode() {
		BNodeNumber number = new BNodeNumber();
		this.insert(number);
	}

	public BNodeNumber getNumber() {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BNodeNumber) {
				return (BNodeNumber) child;
			}

		}
		return null;
	}

	// @Override
	// public void doubleClicked(BasicLogicSheet sheet) {
	// String actionName = this.logic.getFurtherProcess();
	//
	// if (actionName != null) {
	//
	// BeeTabbedPane pane = sheet.findTopPane();
	// BTableModel bm = new BTableModel(null);
	// BTableSheet s = new BTableSheet(sheet.getProject(), bm);
	// // s.setDataModel(this.logic.getModel());
	//
	// BActionModel model = (BActionModel) sheet.getGraph().getModel();
	// List<BClass> inputs = model.getInputs();
	// // for (BClass bclass : inputs) {
	// s.setInputs(inputs);
	// // }
	// pane.addTab(actionName, BeeConstants.BASIC_DESIGN_ICON, s);
	// }
	// }

	@Override
	public void added(BasicLogicSheet sheet) {
		super.added(sheet);
		FontMetrics metrics = sheet.getFontMetrics(sheet.getFont());
		this.reshape(metrics);
	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		FontMetrics metrics = sheet.getFontMetrics(sheet.getFont());
		this.reshape(metrics);
	}

	@Override
	public void reshape(FontMetrics metrics) {
		String value = (String) this.getValue();
		if (value == null) {
			return;
		}
		value = value.replace("\r\n", "");
		String oldValue = value;
		int length = value.length();
		double width = this.getGeometry().getWidth() * 2;

		List<String> list = new ArrayList<String>();

		int index = 0;
		for (int i = 5; i < length; i++) {
			String s = value.substring(0, i);
			int l = metrics.stringWidth(s);
			if (l > width) {
				list.add(value.substring(0, i - 1));
				value = value.substring(i - 1);
				length = value.length();
				index = index + i - 1;
				i = 0;

			}
		}
		if (index > 0 && index < oldValue.length()) {
			list.add(oldValue.substring(index));
		}

		String finalValue = "";
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (i != list.size() - 1) {
				finalValue = finalValue + s + "\r\n";
			} else {
				finalValue = finalValue + s;
			}
		}

		if (list.size() > 1) {
			this.setValue(finalValue);
		} else {
			this.setValue(oldValue);
		}

	}

	public List<ILogicConnector> getConnectors() {
		List<ILogicConnector> cells = new ArrayList<ILogicConnector>();

		int count = this.getEdgeCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getEdgeAt(i);
			if (child instanceof ILogicConnector) {
				ILogicConnector c = (ILogicConnector) child;
				mxICell source = child.getTerminal(true);
				if (source.equals(this)) {
					cells.add(c);
				}
			}
		}

		return cells;
	}

	public List<ILogicCell> getNexts() {
		List<ILogicCell> cells = new ArrayList<ILogicCell>();

		int count = this.getEdgeCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getEdgeAt(i);
			mxICell target = child.getTerminal(false);
			mxICell source = child.getTerminal(true);
			if (source.equals(this)) {
				if (target != null) {
					if (target instanceof ILogicCell) {
						ILogicCell logic = (ILogicCell) target;
						cells.add(logic);
					}
				}
			}
		}

		return cells;
	}

	@Override
	public IExceptionCell getExcetion() {
		if (logic.hasException()) {
			List<ILogicCell> cells = this.getNexts();
			for (ILogicCell cell : cells) {
				if (cell instanceof IExceptionCell) {
					return (IExceptionCell) cell;
				}
			}
		}
		return null;

	}

}
