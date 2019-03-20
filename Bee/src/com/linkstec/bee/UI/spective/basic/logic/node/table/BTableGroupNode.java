package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedInputLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedValueLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BSegmentLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BTableElement;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ITableSegmentCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BTableGroupNode extends BGroupNode implements ITableSegmentCell, BTableElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4103377311538351297L;
	protected int space = 5;
	protected BSegmentLogic logic;

	public BTableGroupNode(BPath parent) {
		this.setStyle("dashed=false;strokeColor=black;strokeWidth=0.5;spacingLeft=10;fillColor=F0F8FF");
		this.setConnectable(false);
		this.setDeletable(false);
		this.setMoveable(false);
		logic = new BSegmentLogic(parent, this);
	}

	@Override
	public void cellAdded(mxICell cell) {
		if (cell instanceof BInvoker) {
			BInvoker invoker = (BInvoker) cell;
			if (invoker.getUserAttribute("AS") != null) {
				BFixedAsValueNode as = new BFixedAsValueNode(invoker);
				BFixedValueLogic fixed = new BFixedValueLogic(this.getLogic().getPath(), as);
				as.setLogic(fixed);
				this.insert(as);
			} else if (invoker.getUserAttribute("INPUT_PARAMETER_VALUE") != null) {
				BFixedInputValueNode as = new BFixedInputValueNode(invoker);
				BFixedInputLogic fixed = new BFixedInputLogic(this.getLogic().getPath(), as);
				as.setLogic(fixed);
				this.insert(as);

			} else {
				BClass bclass = invoker.getInvokeParent().getBClass();
				int start = 0;
				if (bclass instanceof BasicDataModel) {
					BTableRecordNode node = new BTableRecordNode(invoker);
					node.getGeometry().getOffset().setY(start);
					start = start + 100;
					this.insert(node);
				} else if (bclass.isData()) {
					BTableVarRecordNode node = new BTableVarRecordNode(invoker);
					node.getGeometry().getOffset().setY(start);
					start = start + 100;
					this.insert(node);
				}
			}
		} else {
			Debug.d();
		}
		cell.removeFromParent();

	}

	public void layout(BasicLogicSheet sheet) {

	}

	public void fitHeight() {
		int count = this.getChildCount();
		if (count > 1) {
			mxICell last = this.getChildAt(count - 1);
			mxGeometry geo = last.getGeometry();
			double height = geo.getY() + geo.getHeight() + space;
			if (height > this.getGeometry().getHeight()) {
				this.getGeometry().setHeight(height);
			}
		}
		this.resized();
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

	@Override
	public List<BInvoker> getParameters() {
		return null;
	}

	@Override
	public String getSQL(ITableSql sql) {
		return "No SQL";
	}

	public int getSQLPriority() {
		return 0;
	}

	@Override
	public String getSQLExp(ITableSql sql) {
		return "SQLなし";
	}

}
