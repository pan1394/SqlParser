package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Map;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.basic.BTableValue;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public abstract class BTableValueNode extends BNode implements BTableValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2629743739359678343L;

	private int row;
	private int indent = 0;

	public BTableValueNode() {
		this.setVertex(true);
		this.setConnectable(false);
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public void fitWidth(BasicLogicSheet sheet) {
		mxCellState state = sheet.getGraph().getView().getState(this);
		if (state == null) {
			return;
		}
		Map<String, Object> style = state.getStyle();
		Font font = mxUtils.getFont(style);
		FontMetrics m = mxUtils.getFontMetrics(font);
		String value = this.getValue().toString();
		int width = m.stringWidth(value);
		this.getGeometry().setWidth(width + 5);
	}

	public String getSQL(ITableSql tsql) {
		return "";
	}

}
