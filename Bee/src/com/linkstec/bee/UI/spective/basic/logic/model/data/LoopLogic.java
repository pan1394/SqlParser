package com.linkstec.bee.UI.spective.basic.logic.model.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BLoopNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BLoopLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.mxgraph.model.mxICell;

public class LoopLogic extends BasicDataLogic implements BLoopLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 643695831908454970L;
	private static transient IPatternCreator view = PatternCreatorFactory.createView();

	private static String[] indexNames = { "i", "j", "k", "h", "m", "n", "l", "p", "q", "s", "t", "x", "y", "z" };

	public LoopLogic(BPath parent, BVariable var) {
		super(parent, var);
		BLoopNode node = new BLoopNode(this);
		this.getPath().setCell(node);
	}

	@Override
	public String getName() {
		return var.getName() + "ループ処理";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_LOOP_ICON;
	}

	public String getDesc() {
		return var.getName() + "にループ処理をかける";
	}

	private int getLoopDepth(mxICell cell, int depth) {
		mxICell c = cell.getParent();
		if (c == null) {
			return depth;
		}
		if (c instanceof BLoopNode) {
			depth++;
		}
		depth = this.getLoopDepth(c, depth);
		return depth;
	}

	@Override
	public List<BLogicUnit> createUnit() {

		BLoopNode cell = (BLoopNode) this.getPath().getCell();

		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		BClass bclass = var.getBClass();
		P.check(null);
		boolean did = false;
		if (bclass.getQualifiedName().equals(List.class.getName())) {

			List<BType> types = bclass.getParameterizedTypes();
			BClass model = null;
			for (BType type : types) {
				if (type instanceof BClass) {

					model = (BClass) type;
					break;
				}
			}
			if (model == null) {
				if (var.getParameterizedTypeValue() != null) {
					types = var.getParameterizedTypeValue().getParameterizedTypes();
					for (BType type : types) {
						if (type instanceof BClass) {

							model = (BClass) type;
							break;
						}
					}
				} else {
					Debug.a();
				}
			}

			if (model == null) {
				model = CodecUtils.BObject();
			}

			if (model != null) {

				BLoopUnit unit = view.createLoop();
				unit.setLoopType(BLoopUnit.TYPE_FORLOOP);

				if (var == null) {
					Debug.a();
				}
				did = true;
				// init
				BAssignment index = this.makeIndex();
				List<BAssign> inits = new ArrayList<BAssign>();
				inits.add(index);

				unit.setForLoopInitializers(inits);

				// condition
				BExpression condition = view.createExpression();
				condition.setExMiddle(BLogiker.LESSTHAN);
				condition.setExLeft((BValuable) index.getLeft().cloneAll());
				BInvoker bin = view.createMethodInvoker();
				BVariable parent = (BVariable) var.cloneAll();
				parent.setClass(false);
				parent.setCaller(true);
				bin.setInvokeParent(parent);
				BMethod method = view.createMethod();
				method.setLogicName("size");
				method.setName("サイズ取得");
				BVariable v = view.createVariable();
				v.setBClass(model.cloneAll());
				method.setReturn(v);
				bin.setInvokeChild(method);
				condition.setExRight(bin);

				unit.addCondition(condition);

				// update
				BParameter indexParam = (BParameter) index.getLeft().cloneAll();
				indexParam.setCaller(true);
				indexParam.setClass(false);
				BSingleExpressionUnit line = view.createSingleExpression();
				line.setOperator(BSingleExpressionUnit.INCREMENT);
				line.setVariable((BValuable) index.getLeft().cloneAll());
				List<BValuable> updates = new ArrayList<BValuable>();
				updates.add(line);

				unit.setUpdats(updates);

				// Object obj=list.get(i);
				BAssignment assign = view.createAssignment();

				BParameter variable = view.createParameter();
				variable.setBClass(model);
				variable.setLogicName("lm" + model.getLogicName());
				variable.setName(this.var.getName() + "[ループ処理対象データ]");

				variable.setUserAttributes(var.getUserAttributes());
				this.addMark(variable);
				variable.addUserAttribute("INPUT_PARAMETER", "INPUT_PARAMETER");

				BLogicProvider provider = this.getPath().getProvider();
				if (provider != null) {
					String n = provider.getVariableName(variable, null);
					if (n != null) {
						variable.setLogicName(n);
						variable.setName(n);
					}
				}
				assign.setLeft(variable);
				bin = view.createMethodInvoker();
				parent = (BVariable) this.var.cloneAll();
				parent.setClass(false);
				parent.setCaller(true);
				bin.setInvokeParent(parent);
				method = view.createMethod();
				method.setLogicName("get");
				method.setName(model.getName() + "取得");

				BParameter param = view.createParameter();
				param.setBClass(CodecUtils.BInt());
				param.setLogicName("i");
				param.setName("i");

				BVariable v1 = view.createVariable();
				v1.setBClass(model.cloneAll());
				method.setReturn(v1);
				method.addParameter(param);
				bin.setInvokeChild(method);

				bin.addParameter((BValuable) index.getLeft().cloneAll());
				assign.setRight(bin, null);

				unit.getEditor().addUnit(assign);

				// body

				ILogicCell start = cell.getStart();

				List<BLogic> logics = new ArrayList<BLogic>();

				BasicGenUtils.makeLogics(start, logics, true);

				BLogicBody body = unit.getEditor();
				for (BLogic logic : logics) {
					List<BLogicUnit> uts = logic.createUnit();
					if (uts != null) {
						for (BLogicUnit u : uts) {
							body.addUnit((BLogicUnit) u.cloneAll());
						}
					}
				}

				units.add(unit);

			}

		}
		if (!did) {
			Debug.a();
		}
		return units;
	}

	private BAssignment makeIndex() {
		BLoopNode cell = (BLoopNode) this.getPath().getCell();
		mxICell mx = (mxICell) cell;
		int depth = this.getLoopDepth(mx, 0);

		String name = indexNames[depth];

		BAssignment indexAssign = view.createAssignment();
		BParameter indexParam = view.createParameter();
		indexParam.setBClass(CodecUtils.BInt());
		indexParam.setLogicName(name);
		indexParam.setName(var.getName() + "ループのIndex");
		indexAssign.setLeft(indexParam);

		this.addMark(indexParam);

		BVariable value = view.createVariable();
		value.setBClass(CodecUtils.BInt());
		value.setLogicName("0");
		value.setName("0");
		indexAssign.setRight(value, null);

		return indexAssign;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();

		BClass bclass = var.getBClass();
		if (bclass.getQualifiedName().equals(List.class.getName())) {

			List<BType> types = bclass.getParameterizedTypes();

			BClass typeClass = null;
			for (BType type : types) {
				if (type instanceof BClass) {
					typeClass = (BClass) type;
				}

			}

			if (typeClass == null) {
				BType type = var.getParameterizedTypeValue();
				if (type != null) {
					List<BType> ts = type.getParameterizedTypes();
					for (BType t : ts) {
						if (t instanceof BClass) {
							typeClass = (BClass) t;
						}
					}
				}
			}

			BParameter variable = view.createParameter();
			if (typeClass == null) {
				typeClass = CodecUtils.BObject();
				variable.setLogicName("lmLoopData");
			} else {
				variable.setLogicName("lm" + typeClass.getLogicName());
			}
			variable.setName(var.getName() + "[ループ処理対象データ]");
			variable.setBClass(typeClass);
			variable.setUserAttributes(var.getUserAttributes());
			this.addMark(variable);
			variable.addUserAttribute("INPUT_PARAMETER", "INPUT_PARAMETER");

			outputs.add(variable);
		}

		BParameter index = (BParameter) this.makeIndex().getLeft().cloneAll();
		index.setCaller(true);
		index.setClass(false);
		outputs.add(index);
		return outputs;
	}

}
