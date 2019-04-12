package com.linkstec.bee.core.codec.basic;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.AssignExpressionNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.mxgraph.model.mxICell;

public class BasicScanner {
	private BasicScannerPath path;

	public BasicScanner(BClass bclass) {
		// TODO blocks
		path = new BasicScannerPath();

		BeeModel model = (BeeModel) bclass;
		mxICell root = ((mxICell) model.getRoot()).getChildAt(0);

		List<BAssignment> vars = new ArrayList<BAssignment>();
		List<BMethod> methods = new ArrayList<BMethod>();
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = root.getChildAt(i);
			if (child instanceof BAssignment) {
				vars.add((BAssignment) child);
			} else if (child instanceof BMethod) {
				methods.add((BMethod) child);
			}
		}
		for (BAssignment a : vars) {
			a.addUserAttribute("DELEABLE", "DELEABLE");
			this.setPath(a, null);
		}

		for (BMethod method : methods) {
			this.scanMethod(method);
		}
	}

	public BasicScannerPath getPath() {
		return path;
	}

	private void setPath(BAssignment var, BLogicBody body) {
		BParameter p = var.getLeft();
		P.check(null);
		if (this.defined(p, body)) {

			if (var.getUserAttribute("DELEABLE") != null) {
				BasicNode node = (BasicNode) var;
				node.removeFromParent();
			}
			// else if (p.getUserAttribute("SAME_BODY") != null) {
			// AssignExpressionNode node = new AssignExpressionNode();
			// node.setLeft(p);
			// node.setRight(var.getRight(), var.getAssignment());
			// BasicNode b = (BasicNode) var;
			// b.replace(node);
			// } else {
			// for (int i = 0; i < 100; i++) {
			// p.setLogicName(p.getLogicName() + i);
			// if (!this.defined(p, body)) {
			// break;
			// }
			// }
			// }
			else {
				AssignExpressionNode node = new AssignExpressionNode();
				node.setLeft(p);
				node.setRight(var.getRight(), var.getAssignment());
				BasicNode b = (BasicNode) var;

				b.replace(node);
				node.addUserAttribute("ASSIGNMENT_REPLACED", "ASSIGNMENT_REPLACED");
			}
		}

		this.setPath(p, body);

	}

	private boolean defined(BParameter p, BLogicBody body) {
		BasicScannerPath pt = this.path;
		while (pt != null && pt.getParameter() != null) {
			String param = pt.getParameter().getLogicName();
			if (param.equals(p.getLogicName())) {
				if (!pt.getParameter().getBClass().getQualifiedName().toLowerCase()
						.equals(p.getBClass().getQualifiedName().toLowerCase())) {
					Debug.a();
				}
				if (pt.getBody() != null) {
					if (pt.getBody().equals(body)) {
						p.addUserAttribute("SAME_BODY", "SAME_BODY");
					}
				}
				return true;
			}
			pt = pt.getParent();
		}

		return false;
	}

	private void setPath(BParameter var, BLogicBody body) {
		BasicScannerPath p = new BasicScannerPath();
		p.setParameter(var);
		p.setParent(path);
		p.setBody(body);
		path = p;
	}

	private void scanMethod(BMethod method) {
		BasicScannerPath scope = this.path;

		List<BParameter> parameters = method.getParameter();
		for (BParameter p : parameters) {
			this.setPath(p, null);
		}
		BLogicBody body = method.getLogicBody();
		this.scanLogicBody(body);
		this.path = scope;

	}

	private void scanLogicBody(BLogicBody body) {
		if (body == null) {
			return;
		}

		BasicScannerPath scope = this.path;
		List<BLogicUnit> units = body.getUnits();
		for (BLogicUnit unit : units) {
			this.scanUnit(unit, body);
		}
		this.path = scope;
	}

	private void scanUnit(BLogicUnit unit, BLogicBody b) {
		if (unit instanceof BAssignment) {
			BAssignment a = (BAssignment) unit;
			this.setPath(a, b);
		} else if (unit instanceof BLoopUnit) {
			BLoopUnit loop = (BLoopUnit) unit;
			if (loop.getLoopType() == BLoopUnit.TYPE_FORLOOP) {
				List<BAssign> inits = loop.getForLoopInitializers();
				for (BAssign as : inits) {
					if (as instanceof BAssignment) {
						BAssignment a = (BAssignment) as;
						// this.setPath(a, loop.getEditor());
					}
				}
			} else if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {
				BParameter var = loop.getEnhanceVariable();
				this.setPath(var, loop.getEditor());
			}
			BLogicBody body = loop.getEditor();
			this.scanLogicBody(body);
		} else if (unit instanceof BTryUnit) {
			BTryUnit tryUnit = (BTryUnit) unit;

			BLogicBody body = tryUnit.getTryEditor();
			this.scanLogicBody(body);
			List<BCatchUnit> catches = tryUnit.getCatches();
			for (BCatchUnit bc : catches) {
				BParameter p = bc.getVariable();
				this.setPath(p, bc.getEditor());
				this.scanLogicBody(bc.getEditor());
			}
			this.scanLogicBody(tryUnit.getFinalEditor());

		} else if (unit instanceof BMultiCondition) {
			BMultiCondition con = (BMultiCondition) unit;
			List<BConditionUnit> us = con.getConditionUnits();
			for (BConditionUnit u : us) {
				this.scanLogicBody(u.getLogicBody());
			}
		}
	}
}
