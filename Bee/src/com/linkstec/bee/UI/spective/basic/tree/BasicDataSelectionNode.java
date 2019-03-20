package com.linkstec.bee.UI.spective.basic.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableObjectNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxCell;

public class BasicDataSelectionNode extends BeeTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7300342602213365753L;

	private boolean checked = false;
	private boolean isAs = false;
	private boolean isInput = false;

	public BasicDataSelectionNode(BProject data) {
		super(data);
		this.setProject(data);
	}

	public boolean isInput() {
		return isInput;
	}

	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}

	public boolean isAs() {
		return isAs;
	}

	public void setAs(boolean isAs) {
		this.isAs = isAs;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public TreeNode findRoot(TreeNode node) {
		if (node.getParent() == null) {
			return node;
		} else {
			return this.findRoot(node.getParent());
		}
	}

	private List<BasicDataSelectionNode> findCheckedNode(BasicDataSelectionNode node) {

		List<BasicDataSelectionNode> list = new ArrayList<BasicDataSelectionNode>();
		this.findCheckedNode(node, list);
		return list;
	}

	private void findCheckedNode(BasicDataSelectionNode node, List<BasicDataSelectionNode> list) {

		int count = node.getChildCount();
		boolean allChildChecked = true;

		List<BasicDataSelectionNode> sub = new ArrayList<BasicDataSelectionNode>();
		for (int i = 0; i < count; i++) {
			BasicDataSelectionNode child = (BasicDataSelectionNode) node.getChildAt(i);
			findCheckedNode(child, sub);
			if (!child.isChecked()) {
				allChildChecked = false;
			}
		}

		if (allChildChecked) {
			if (node.isChecked()) {
				list.add(node);
			}
		} else {
			list.addAll(sub);
		}

	}

	@Override
	public mxCell getTransferNode() {
		List<BasicDataSelectionNode> nodes = this.findCheckedNode((BasicDataSelectionNode) this.getParent());
		List<BNode> list = new ArrayList<BNode>();

		if (!nodes.isEmpty()) {
			if (!this.isChecked()) {
				mxCell cell = makeTransferNode(this);
				if (cell instanceof BNode) {
					BNode t = (BNode) cell;
					list.add(t);
				}
			}
			for (BasicDataSelectionNode child : nodes) {
				mxCell cell = makeTransferNode(child);
				if (cell instanceof BNode) {
					BNode t = (BNode) cell;
					list.add(t);
				} else if (cell instanceof BasicNode) {
					BasicNode b = (BasicNode) cell;
					BDetailNodeWrapper w = new BDetailNodeWrapper(
							Application.getInstance().getBasicSpective().getSelection().getActionPath(), b);
					list.add(w);

				}
			}
		}
		if (!list.isEmpty()) {
			BTansferHolderNode holder = new BTansferHolderNode(list);
			return holder;
		}

		return makeTransferNode(this);
	}

	public static mxCell makeTransferNode(BasicDataSelectionNode selected) {
		Object obj = selected.getUserObject();
		Object parent = selected.getParent();
		if (obj instanceof BAssignment) {
			BAssignment var = (BAssignment) obj;

			if (parent instanceof BasicDataSelectionNode) {
				BasicDataSelectionNode p = (BasicDataSelectionNode) parent;
				Object po = p.getUserObject();
				if (po instanceof BVariable) {
					BVariable para = (BVariable) po;
					para = BasicGenUtils.toView(para);

					BClass bclass = para.getBClass();
					if (bclass.isData()) {
						IPatternCreator view = PatternCreatorFactory.createView();
						BInvoker invoker = view.createMethodInvoker();
						invoker.setInvokeParent(para);
						BVariable child = BasicGenUtils.toView(var.getLeft());
						invoker.setInvokeChild(child);
						if (selected.isAs()) {
							invoker.addUserAttribute("AS", "AS");
						} else if (selected.isInput()) {
							invoker.addUserAttribute("INPUT_PARAMETER_VALUE", "INPUT_PARAMETER_VALUE");
						}
						return (mxCell) invoker;
					}
				}
			}
		} else if (obj instanceof BasicDataModel) {
			// Debug.d();
			BasicDataModel model = (BasicDataModel) obj;
			BParameter p = PatternCreatorFactory.createView().createParameter();
			p.setBClass(model);
			BTableObjectNode node = new BTableObjectNode(p);
			return node;
		} else if (obj instanceof BParameter) {
			// Debug.d();
			BParameter var = (BParameter) obj;
			BEditor current = Application.getInstance().getBasicSpective().getSelection().getEditor();
			if (current instanceof BTableSheet) {
				BTableObjectNode node = new BTableObjectNode(var);
				return node;
			}
		} else if (obj instanceof BLogic) {
			BLogic logic = (BLogic) obj;
			logic.getPath().setParent(null);
			return (mxCell) logic.getPath().getCell();
		}

		if (obj instanceof mxCell) {
			return (mxCell) obj;
		}

		return null;
	}

}
