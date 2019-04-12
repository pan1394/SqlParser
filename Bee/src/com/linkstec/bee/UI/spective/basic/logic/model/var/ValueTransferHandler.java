package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableRecordNode;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.swing.util.mxGraphTransferable;

import sun.awt.datatransfer.ClipboardTransferable;
import sun.awt.datatransfer.TransferableProxy;

public class ValueTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5520076116990708115L;

	public ValueTransferHandler() {

	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		if (comp instanceof ITranferRecevier) {
			ITranferRecevier panel = (ITranferRecevier) comp;
			try {
				if (t instanceof ClipboardTransferable) {
					ClipboardTransferable clip = (ClipboardTransferable) t;
					Object obj = clip.getTransferData(DataFlavor.stringFlavor);
					if (obj instanceof String) {
						String value = (String) obj;
						setValue(value, panel);
						return true;
					}
				}
				Object obj = t.getTransferData(mxGraphTransferable.dataFlavor);
				if (obj instanceof TransferableProxy) {
					TransferableProxy proxy = (TransferableProxy) obj;
					obj = proxy.getTransferData(mxGraphTransferable.dataFlavor);
				}
				if (obj instanceof BeeTransferable) {

					BeeTransferable bt = (BeeTransferable) obj;
					Object[] cells = bt.getCells();

					if (cells != null && cells.length > 0) {
						Object cell = cells[0];
						boolean isLocal = false;
						if (cell instanceof BNode) {
							BNode node = (BNode) cell;
							if (node.getUserAttribute("LOCAL_ID") != null) {
								int hashCode = (int) node.getUserAttribute("LOCAL_ID");
								panel.localMove(hashCode);
								isLocal = true;
							}
						}

						if (!isLocal) {
							setValue(cell, panel);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void setValue(Object cell, ITranferRecevier panel) {
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
		} else if (cell instanceof BLogicNode) {
			BLogicNode bn = (BLogicNode) cell;
			BLogic logic = bn.getLogic();
			if (logic instanceof JudgeLogic) {
				JudgeLogic judge = (JudgeLogic) logic;
				BValuable value = judge.getExpression(null);
				this.setValue(value, panel);
			}
		} else if (cell instanceof String) {
			String s = (String) cell;
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable var = view.createVariable();
			var.setBClass(CodecUtils.BString());
			s = "\"" + s + "\"";
			var.setLogicName(s);
			var.setName(s);

			panel.setFireEvent(false);
			panel.setText(var.getName());
			panel.setFireEvent(true);

			panel.setValue(var);
			panel.updateUI();
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