package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableRecordNode;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.swing.util.mxGraphTransferable;

public class ValueTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5520076116990708115L;

	public ValueTransferHandler() {

	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		if (comp instanceof ValueEditor) {
			ValueEditor panel = (ValueEditor) comp;
			try {
				Object obj = t.getTransferData(mxGraphTransferable.dataFlavor);
				if (obj instanceof BeeTransferable) {

					BeeTransferable bt = (BeeTransferable) obj;
					Object[] cells = bt.getCells();

					if (cells != null && cells.length > 0) {
						Object cell = cells[0];

						setValue(cell, panel);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void setValue(Object cell, ValueEditor panel) {
		if (cell instanceof BInvoker) {
			BInvoker invoker = (BInvoker) cell;
			BValuable v = invoker.getInvokeChild();
			panel.setFireEvent(false);
			if (v instanceof BVariable) {
				BVariable var = (BVariable) v;
				panel.setText(var.getName());
			} else if (v instanceof BMethod) {
				BMethod method = (BMethod) v;
				panel.setText(method.getName());
			}
			panel.setFireEvent(true);
			panel.setValue(invoker);
			panel.updateUI();
		} else if (cell instanceof BVariable) {
			BVariable var = (BVariable) cell;

			panel.setFireEvent(false);
			panel.setText(var.getName());
			panel.setFireEvent(true);

			panel.setValue(var);
			panel.updateUI();
		} else if (cell instanceof BAssignment) {
			BAssignment assign = (BAssignment) cell;
			BParameter var = assign.getLeft();
			var.setCaller(true);
			var.setClass(false);
			// BValuable value = null;// assign.getRight();
			// if (value != null) {
			// panel.setFireEvent(false);
			// panel.setText(BValueUtils.createValuable(value, false));
			// panel.setFireEvent(true);
			// panel.setValue(value);
			// } else {
			panel.setFireEvent(false);
			panel.setText(var.getName());
			panel.setFireEvent(true);
			panel.setValue(var);
			// }

			panel.updateUI();
		} else if (cell instanceof BTansferHolderNode) {
			BTansferHolderNode node = (BTansferHolderNode) cell;
			List<BNode> nodes = node.getNodes();
			if (nodes.size() > 0) {
				BNode n = nodes.get(0);
				if (n instanceof BTableRecordNode) {
					BTableRecordNode recorde = (BTableRecordNode) n;

					panel.setFireEvent(false);
					panel.setText(recorde.getSQL(null));
					panel.setFireEvent(true);

					panel.setValue(recorde.getRecord());
					panel.updateUI();
				} else if (n instanceof BDetailNodeWrapper) {
					BDetailNodeWrapper wrapper = (BDetailNodeWrapper) n;
					BasicNode bn = wrapper.getNode();
					this.setValue(bn, panel);
				}
			}
		}
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		super.exportAsDrag(comp, e, action);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		super.exportDone(source, data, action);

	}

	@Override
	public boolean canImport(TransferSupport support) {
		return true;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return true;
	}
}