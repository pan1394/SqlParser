package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicEditDataSelection;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.BasicCellListeItem;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BTableValue;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BAssignment;
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

	@Override
	public List<BasicCellListeItem> getListItems(String text, BasicLogicSheet sheet) {
		BVariable var = this.getListTargetVar();
		if (var == null) {
			return null;
		}
		BClass bclass = var.getBClass();
		if (bclass.isData()) {
			List<BAssignment> vars = bclass.getVariables();
			if (vars.isEmpty()) {
				BasicBook book = sheet.findBook();
				if (book == null) {
					return null;
				}

				BasicDataDictionary dict = BasicEditDataSelection.getBookDictoinery(book);

				bclass = dict.getModel();
				vars = bclass.getVariables();
			}
			if (vars.size() > 0) {
				List<BasicCellListeItem> items = new ArrayList<BasicCellListeItem>();

				for (BAssignment assign : vars) {
					BParameter left = assign.getLeft();
					BasicCellListeItem item = new BasicCellListeItem();

					String name = left.getName() + left.getLogicName();
					boolean add = false;

					if (text == null || text.trim().equals("")) {
						add = true;
					} else {
						if (name.toLowerCase().indexOf(text.trim().toLowerCase()) >= 0) {
							add = true;
						}
					}

					if (add) {
						item.setUserObject(left);
						item.setDisplayName(left.getName() + "[" + left.getLogicName() + "]");
						items.add(item);
					}
				}

				return items;
			}
		}
		return null;
	}

	public BVariable getListTargetVar() {
		return null;
	}

	public void onListTargetChange(BParameter var) {

	}

	@Override
	public void onMenuSelected(BasicCellListeItem item) {

		BParameter left = (BParameter) item.getUserObject();
		this.onListTargetChange(left);
	}

}
