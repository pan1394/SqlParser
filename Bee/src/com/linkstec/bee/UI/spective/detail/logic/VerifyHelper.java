package com.linkstec.bee.UI.spective.detail.logic;

import java.util.List;

import com.linkstec.bee.UI.node.AssignExpressionNode;
import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.TryNode;
import com.linkstec.bee.UI.node.view.ObjectMark;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxICell;

public class VerifyHelper {

	public static void Verify(AssignmentNode target, BeeGraphSheet sheet, BProject project) {

		BValuable left = target.getLeft();
		BValuable right = target.getRight();
		BClass leftb = null, rightb = null;
		if (left == null) {
			target.setAlert("変数を定義してください").setType(BAlert.TYPE_ERROR);

		} else {
			leftb = left.getBClass();
			if (leftb == null) {
				left.setAlert("変数のタイプが定義されていません").setType(BAlert.TYPE_ERROR);
			}
		}
		BasicNode node = (BasicNode) right;
		if (node != null && node.isVisible()) {
			if (right != null) {
				rightb = right.getBClass();
				if (leftb != null && rightb != null && !rightb.getQualifiedName().equals(leftb.getQualifiedName())) {
					rightb.setAlert("このタイプ（" + rightb.getName() + "）の値を付与できません");
				}
			}
		}
	}

	public static void Verify(AssignExpressionNode target, BeeGraphSheet sheet, BProject project) {

		BValuable left = target.getLeft();
		BValuable right = target.getRight();
		BClass leftb = null, rightb = null;
		if (left == null) {
			target.setAlert("変数を定義してください").setType(BAlert.TYPE_ERROR);

		} else {
			leftb = left.getBClass();
			if (leftb == null) {
				left.setAlert("変数のタイプが定義されていません").setType(BAlert.TYPE_ERROR);
			}
		}
		BasicNode node = (BasicNode) right;
		if (node.isVisible()) {
			if (right != null) {
				rightb = right.getBClass();
				if (rightb != null && rightb.getQualifiedName() != null) {
					if (leftb != null && !rightb.getQualifiedName().equals(leftb.getQualifiedName())) {
						rightb.setAlert("このタイプ（" + rightb.getName() + "）の値を付与できません");
					}
				}
			}
		}
	}

	public static void Verify(ComplexNode target, BeeGraphSheet sheet, BProject project) {
		BeeModel model = sheet.getModel();
		List<BAssignment> vars = model.getVariables();
		// boolean caller = false;
		for (BAssignment assin : vars) {
			if (assin.getLeft() != null) {
				if (assin.getLeft().getUserObject() instanceof ObjectMark) {
					BObject bobject = assin.getLeft();
					if (bobject instanceof ComplexNode) {
						ComplexNode comlex = (ComplexNode) bobject;
						ObjectMark mark = (ObjectMark) comlex.getUserObject();
						ObjectMark m = (ObjectMark) target.getUserObject();
						if (!comlex.equals(target)) {
							if (mark.getId() == m.getId()) {
								if (comlex.getName() != null) {
									target.setCaller(true);
									if (!comlex.getName().equals(target.getName())) {
										target.setName(comlex.getName());
									}
									if (!comlex.getLogicName().equals(target.getLogicName())) {
										target.setLogicName(comlex.getLogicName());
									}
									if (comlex.getBClass() != null) {
										if (comlex.getBClass().getQualifiedName().equals(target.getBClass().getQualifiedName())) {
											target.setBClass(comlex.getBClass());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// if (!caller) {
		// if (this.getName() == null) {
		// this.setAlert("論理名を設定してください");
		// } else if (this.getLogicName() == null) {
		// this.setAlert("物理名を設定してください").setType(BAlert.TYPE_ERROR);
		// } else if (this.getBClass() == null) {
		// this.setAlert("タイプを設定してください");
		// }
		// }
	}

	public static void Verify(ReferNode refer, BeeGraphSheet sheet, BProject project) {
		refer.setAlert(null);
		BeeModel model = sheet.getModel();
		BObject obj = refer.getInvokeParent();
		BClass bclass = null;
		if (obj instanceof BClass) {
			bclass = (BClass) obj;
		} else if (obj instanceof BVariable) {
			BVariable var = (BVariable) obj;
			bclass = var.getBClass();
		}
		if (bclass != null) {
			if (bclass.getQualifiedName().equals(model.getQualifiedName())) {
				BObject child = refer.getInvokeChild();
				if (child instanceof BMethod) {
					BMethod method = (BMethod) child;
					ObjectMark target = (ObjectMark) method.getUserObject();
					if (target == null) {
						return;
					}
					List<BMethod> methods = model.getMethods();
					boolean found = false;
					for (BMethod m : methods) {
						ObjectMark mark = (ObjectMark) m.getUserObject();
						if (mark.getId() == target.getId()) {
							method.setName(m.getName());
							method.setLogicName(m.getLogicName());
							found = true;

							List<BParameter> paras = method.getParameter();
							List<BValuable> invkerPara = refer.getParameters();
							if (paras.size() != invkerPara.size()) {
								refer.setAlert("パラメータの設定が間違っています").setType(BAlert.TYPE_ERROR);
							} else {
								int size = paras.size();
								for (int i = 0; i < size; i++) {
									BVariable var = paras.get(i);
									BValuable ivar = invkerPara.get(i);
									boolean go = true;
									if (var.getBClass() == null) {
										var.setAlert("タイプ定義されていません").setType(BAlert.TYPE_ERROR);
										go = false;
									}
									if (ivar.getBClass() == null) {
										go = false;
										ivar.setAlert("タイプ定義されていません").setType(BAlert.TYPE_ERROR);
									}
									if (go) {
										if (!var.getBClass().getQualifiedName().equals(ivar.getBClass().getQualifiedName())) {
											refer.setAlert("設定したパラメータのタイプが間違っています").setType(BAlert.TYPE_ERROR);
										}
									}
								}
							}
						}
					}
					if (!found) {
						// refer.setAlert("呼び出している処理が存在しません").setType(BAlert.TYPE_ERROR);
					}
				}
			}
		} else if (obj instanceof ComplexNode) {
			((ComplexNode) obj).Verify(sheet, project);

		}

		// if the refer is on the field,it does not need catch
		if (findMethod(refer) != null) {

			// make sure the exception is catched
			BObject child = refer.getInvokeChild();
			if (child instanceof BMethod) {
				BMethod method = (BMethod) child;
				List<BVariable> thros = method.getThrows();

				if (!thros.isEmpty()) {
					for (BVariable var : thros) {
						if (!errorCatched(var, refer, project)) {

							// refer.setAlert("エラーが発生する可能性がありますが処理されていません").setType(BAlert.TYPE_ERROR);
							break;
						}
					}
				}
			}
		}
	}

	public static boolean errorCatched(BVariable error, BasicNode node, BProject project) {
		BClass bclass = error.getBClass();
		Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), Application.getInstance().getCurrentProject());
		return errorCatched(cls, node, project);
	}

	public static boolean errorCatched(Class<?> error, BasicNode node, BProject project) {
		if (error == null) {
			return true;
		}

		if (node instanceof TryNode) {
			TryNode tryNode = (TryNode) node;
			List<BCatchUnit> catches = tryNode.getCatches();
			for (BCatchUnit unit : catches) {
				BParameter var = unit.getVariable();
				BClass b = var.getBClass();
				Class<?> cls = CodecUtils.getClassByName(b.getQualifiedName(), project);
				if (cls.isAssignableFrom(error)) {
					return true;
				}
			}
		}

		if (node instanceof BMethod) {

			BMethod method = (BMethod) node;
			List<BVariable> ths = method.getThrows();
			for (BVariable var : ths) {
				BClass b = var.getBClass();
				Class<?> cls = CodecUtils.getClassByName(b.getQualifiedName(), project);
				if (cls.isAssignableFrom(error)) {
					return true;
				}
			}
		}
		mxICell parent = node.getParent();
		if (parent != null && parent instanceof BasicNode) {
			return errorCatched(error, (BasicNode) parent, project);
		}
		return false;
	}

	public static BLogicUnit findUnit(BasicNode node, BasicNode original) {
		mxICell parent = node.getParent();
		if (parent == null) {
			String id = ((ObjectMark) node.getUserObject()).getId();
			BasicNode method = (BasicNode) findMethod(original);
			node = (BasicNode) method.getCellByObjectID(id);
		}
		if (node instanceof ReferNode) {
			ReferNode refer = (ReferNode) node;
			BasicNode linker = refer.getLinker();
			if (linker != null) {
				return findUnit(linker, original);
			}
		}

		if (node instanceof BLogicUnit && parent != null && parent instanceof BLockNode) {

			return (BLogicUnit) node;
		}
		if (parent != null && parent instanceof BasicNode) {
			return findUnit((BasicNode) parent, original);
		}
		return null;
	}

	public static BMethod findMethod(BasicNode node) {

		if (node instanceof BMethod) {
			return (BMethod) node;
		}
		mxICell parent = node.getParent();
		if (parent != null && parent instanceof BasicNode) {
			return findMethod((BasicNode) parent);
		}
		return null;
	}

	public static mxICell findRoot(mxICell cell) {
		if (cell instanceof BasicNode) {
			mxICell parent = cell.getParent();
			if (parent != null) {
				return findRoot(parent);
			}
		}
		return cell;
	}
}
