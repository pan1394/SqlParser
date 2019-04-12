package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedInputLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedValueLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BJudgeLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BTableListHelper {

	public static List<BTableValueNode> getRecords(BNode b) {
		List<BTableValueNode> list = new ArrayList<BTableValueNode>();
		int count = b.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = b.getChildAt(i);
			if (child instanceof BTableValueNode) {
				BTableValueNode node = (BTableValueNode) child;
				list.add(node);
			}
		}
		return list;
	}

	public static void childAdded(ILogicCell target, BNode node, BasicLogicSheet sheet) {
		BNode bnode = (BNode) target;
		if (node instanceof BLogicNode) {
			BLogicNode n = (BLogicNode) node;
			BLogic logic = n.getLogic();
			if (logic instanceof BJudgeLogic) {
				BJudgeLogic judge = (BJudgeLogic) logic;
				BTableCondionNode cn = new BTableCondionNode(judge);
				bnode.insert(cn);
			}
			node.removeFromParent();
		} else if (node instanceof BTansferHolderNode) {
			BTansferHolderNode t = (BTansferHolderNode) node;
			List<BNode> list = t.getNodes();
			for (BNode n : list) {
				bnode.childAdded(n, sheet);
			}
			node.removeFromParent();
			return;
		}
		if (node instanceof BTableObjectNode) {
			BTableObjectNode table = (BTableObjectNode) node;
			BParameter parameter = table.getModel(sheet.findBook().getALLModels());
			BasicDataModel model = (BasicDataModel) parameter.getBClass();
			List<BAssignment> list = model.getVariables();
			IPatternCreator view = PatternCreatorFactory.createView();
			double start = node.getGeometry().getX();

			for (BAssignment var : list) {
				BInvoker invoker = view.createMethodInvoker();
				invoker.setInvokeParent(parameter);
				invoker.setInvokeChild(var.getLeft());
				inserInvoker(target, invoker, new mxPoint(0, start));
				start = start + 100;
			}
			table.removeFromParent();
		} else if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			wrapper.removeFromParent();
			BasicNode basic = wrapper.getNode();

			BInvoker invoker = null;
			if (basic instanceof BInvoker) {
				invoker = (BInvoker) basic;

			} else if (basic instanceof BAssignment) {
				BAssignment assign = (BAssignment) basic;
				BValuable value = assign.getRight();
				if (value instanceof BInvoker) {
					invoker = (BInvoker) value;
				} else if (value instanceof BVariable) {
					// value = assign.getLeft();
					BVariable parms = (BVariable) value;
					BClass b = parms.getBClass();
					if (b instanceof BasicDataModel) {
						BasicDataModel model = (BasicDataModel) parms.getBClass();
						List<BAssignment> list = model.getVariables();
						IPatternCreator view = PatternCreatorFactory.createView();
						double start = node.getGeometry().getX();

						for (BAssignment var : list) {
							BInvoker bin = view.createMethodInvoker();
							bin.setInvokeParent(parms);
							bin.setInvokeChild(var.getLeft());
							inserInvoker(target, bin, new mxPoint(0, start));
							start = start + 100;
						}
					}

				}
			}
			if (invoker != null) {
				double y = wrapper.getGeometry().getY();
				double x = wrapper.getGeometry().getX();
				if (wrapper.getGeometry().isRelative()) {
					y = wrapper.getGeometry().getOffset().getY();
				}

				inserInvoker(target, invoker, new mxPoint(x, y));
			}
		}

	}

	public static void inserInvoker(ILogicCell target, BInvoker invoker, mxPoint offset) {
		BNode node = (BNode) target;
		if (invoker.getUserAttribute("AS") != null) {
			BFixedAsValueNode as = new BFixedAsValueNode(invoker);
			BFixedValueLogic fixed = new BFixedValueLogic(target.getLogic().getPath(), as);
			as.setLogic(fixed);
			as.getGeometry().setX(offset.getX());
			as.getGeometry().setY(offset.getY());
			node.insert(as);
		} else if (invoker.getUserAttribute("INPUT_PARAMETER_VALUE") != null) {
			BFixedInputValueNode as = new BFixedInputValueNode(invoker);
			BFixedInputLogic fixed = new BFixedInputLogic(target.getLogic().getPath(), as);
			as.setLogic(fixed);
			as.getGeometry().setX(offset.getX());
			as.getGeometry().setY(offset.getY());
			node.insert(as);

		} else {
			BClass bclass = invoker.getInvokeParent().getBClass();
			if (bclass instanceof BasicDataModel) {
				BTableRecordNode record = new BTableRecordNode(invoker);
				if (offset != null) {
					record.getGeometry().setX(offset.getX());
					record.getGeometry().setY(offset.getY());
				}
				node.insert(record);
			} else if (bclass.isData()) {
				BTableVarRecordNode record = new BTableVarRecordNode(invoker);
				record.getGeometry().setY(offset.getY());
				record.getGeometry().setX(offset.getX());
				node.insert(record);
			}
		}
	}

	public static SqlResult getSqlItemValue(String start, String splitter, List<BTableValueNode> list, ITableSql tsql) {
		// pick up the select name
		List<BInvoker> pickupSelectName = null;
		Thread t = Thread.currentThread();
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			if (bee.getUserAttribute("PICKUP_SELECT") != null) {
				pickupSelectName = new ArrayList<BInvoker>();
			}
		}

		boolean format = tsql.isFormat();
		SqlResult result = new SqlResult();
		if (list.isEmpty()) {
			return result;
		}

		if (!format) {
			start = " " + start;
		} else {
			start = "\r\n" + start;
		}

		int row = -1;
		BTableValueNode last = null;
		for (BTableValueNode target : list) {
			// pick up the select name
			if (pickupSelectName != null) {
				if (target instanceof BTableRecordNode) {
					BTableRecordNode record = (BTableRecordNode) target;
					pickupSelectName.add(record.getRecord());
				} else if (target instanceof BFixedAsValueNode) {
					BFixedAsValueNode as = (BFixedAsValueNode) target;
					pickupSelectName.add(as.getInvoker());
				}
			}

			String sql = target.getSQL(tsql);
			if (target instanceof BTableVarRecordNode) {
				BTableVarRecordNode n = (BTableVarRecordNode) target;
				tsql.getInvokers().add(n.getInvoker());
			}

			// a new line
			if (target.getRow() != row) {
				if (!format) {
					start = start + " ";
				} else {
					start = start + "\r\n\t";
					int indent = target.getIndent();
					for (int i = 0; i < indent; i++) {
						start = start + "\t";
					}
				}
				if (!(last instanceof BFiexedReturnValueNode)) {
					if (row != -1) {
						start = start + splitter;
					}
				}

			} else {
				start = start + " ";
			}

			start = start + sql;
			row = target.getRow();
			last = target;

			result.setRow(row);

		}
		// pick up the select name
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			if (bee.getUserAttribute("PICKUP_SELECT") != null) {
				bee.addUserAttribute("PICKUP_SELECT", pickupSelectName);
			}
		}
		result.setSql(start);
		return result;
	}

	public static String getSqlItemExp(String start, String splitter, List<BTableValueNode> list, ITableSql tsql) {
		boolean format = tsql.isFormat();
		if (list.isEmpty()) {
			return "";
		}
		if (!format) {
			start = " " + start;
		} else {
			start = "\r\n" + start;
		}

		int row = -1;
		BTableValueNode last = null;
		for (BTableValueNode target : list) {
			String sql = target.getSQLExp(tsql);
			if (target.getRow() != row) {
				if (!format) {
					start = start + " ";
				} else {
					start = start + "\r\n\t";
					int indent = target.getIndent();
					for (int i = 0; i < indent; i++) {
						start = start + "\t";
					}
				}
				if (!(last instanceof BFiexedReturnValueNode)) {
					if (row != -1) {
						start = start + splitter;
					}
				}

			} else {
				start = start + " ";
			}

			start = start + sql;
			row = target.getRow();
			last = target;
		}
		return start;
	}

	public static class SqlResult {
		private String sql = "";
		private int row = -1;

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

	}
}
