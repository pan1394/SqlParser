package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicEditDataSelection;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.BasicCellListeItem;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;

public class BFixedAsValueNode extends BFixedValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128850982394242452L;

	private BInvoker invoker;

	// for effectiveness
	private String logicName;

	public BFixedAsValueNode(BInvoker invoker) {

		this.invoker = invoker;
		this.setStyle("strokeColor=gray;strokeWidth=0.5;fillColor=f8c471");
	}

	public BInvoker getInvoker() {
		return invoker;
	}

	@Override
	public List<BasicCellListeItem> getListItems(String text, BasicLogicSheet sheet) {
		BVariable var = (BVariable) invoker.getInvokeParent();
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
					item.setUserObject(left);
					item.setDisplayName(left.getName() + "[" + left.getLogicName() + "]");
					items.add(item);
				}

				return items;
			}
		}
		return null;
	}

	@Override
	public void onMenuSelected(BasicCellListeItem item) {

		BParameter left = (BParameter) item.getUserObject();
		this.invoker.setInvokeChild((BValuable) left.cloneAll());
	}

	@Override
	public Object getValue() {

		BVariable var = (BVariable) invoker.getInvokeChild();
		logicName = "AS " + var.getName();

		return logicName;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BVariable var = (BVariable) invoker.getInvokeChild();
		return "AS " + var.getLogicName();
	}

}
