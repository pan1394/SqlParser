package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ExpressionNode;
import com.linkstec.bee.UI.node.ParameterNode;
import com.linkstec.bee.UI.spective.basic.logic.model.BasicNodeLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IBodyCell;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILoopCell;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BDetailNodeWrapper extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -696119859622099032L;
	private BasicNode node;
	private BasicNodeLogic logic;

	public BDetailNodeWrapper(BPath path, BasicNode node) {
		this.setVertex(true);
		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;rounded=1");
		mxICell parent = node.getParent();
		this.setGeometry((mxGeometry) node.getGeometry().clone());
		if ((!(parent instanceof BNode)) || parent instanceof IBodyCell || parent instanceof ILoopCell) {

			this.node = this.makeValue(node, path.getProject());
			if (this.node == null) {
				this.node = BLockNode.difineVar(node);
			}
		} else {
			this.node = node;
		}

		this.getGeometry().setWidth(150);
		this.getGeometry().setHeight(40);
		logic = new BasicNodeLogic(path, this);
	}

	public AssignmentNode makeValue(BasicNode source, BProject project) {

		if (source instanceof ComplexNode) {
			ComplexNode complex = (ComplexNode) source;
			BClass bclass = complex.getBClass();
			if (bclass.getQualifiedName().equals(List.class.getName())) {

				AssignmentNode assign = new AssignmentNode();
				assign.addUserAttribute("LIST_VAR", "LIST_VAR");
				ParameterNode node = new ParameterNode();
				node.setBClass(bclass);
				node.setName(complex.getName());
				node.setLogicName("m" + complex.getLogicName().toLowerCase());
				assign.setLeft(node);

				BVariable value = new ComplexNode();
				value.setBClass(CodecUtils.getClassFromJavaClass(ArrayList.class, project));
				value.setNewClass(true);
				value.setName(complex.getName());
				value.setLogicName("List");

				assign.setRight(value, null);

				return assign;
			} else if (bclass.getQualifiedName().equals(Map.class.getName())
					|| bclass.getQualifiedName().equals(Hashtable.class.getName())) {
				AssignmentNode assign = new AssignmentNode();
				assign.addUserAttribute("MAP_VAR", "MAP_VAR");
				ParameterNode node = new ParameterNode();
				node.setBClass(bclass);
				node.setName(complex.getName());
				node.setLogicName("m" + complex.getLogicName().toLowerCase());
				assign.setLeft(node);

				BVariable value = new ComplexNode();
				value.setBClass(CodecUtils.getClassFromJavaClass(Hashtable.class, project));
				value.setNewClass(true);
				value.setName(complex.getName());
				value.setLogicName("Map");

				assign.setRight(value, null);

				return assign;
			}
		} else if (source instanceof ExpressionNode) {
			ExpressionNode ex = (ExpressionNode) source;

			AssignmentNode assign = new AssignmentNode();
			assign.addUserAttribute("EXPRESSION_VAR", "EXPRESSION_VAR");
			ParameterNode node = new ParameterNode();
			node.setBClass(ex.getBClass());
			node.setName("計算");
			node.setLogicName("mexpression");
			assign.setLeft(node);

			assign.setRight((BValuable) ex.cloneAll(), null);
			return assign;

		}
		return null;
	}

	@Override
	public Object getValue() {
		if (node instanceof BAssignment) {
			BAssignment assgin = (BAssignment) node;
			BParameter var = assgin.getLeft();
			String s = "変数[" + var.getName() + "]を作っておく";
			BValuable right = assgin.getRight();
			if (right != null) {
				s = "変数[" + var.getName() + "]=" + BValueUtils.createValuable(right, false);
			}
			BNode node = BLogicLayout.reshape(this, s);
			return node.getValue();
		} else {
			return node;
		}
	}

	public BasicNode getNode() {
		return node;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}
}
