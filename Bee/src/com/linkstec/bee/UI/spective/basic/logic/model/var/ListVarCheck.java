package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BMethod;

public class ListVarCheck extends VarCheckLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557716818815293819L;

	public ListVarCheck(BPath parent, BValuable var, BValuable target) {
		super(parent, var, target);
	}

	@Override
	public String getName() {
		return target.getName() + "の内容Nullチェック";
	}

	@Override
	public String getDesc() {
		if (target == null) {
			return "";
		}
		return target.getName() + " is Null";
	}

	@Override
	protected BExpression getExpression(ITableSql tsql) {
		if (var != null) {
			if (tsql != null) {
				// do nothing
			}
			IPatternCreator view = PatternCreatorFactory.createView();

			BExpression ex = view.createExpression();
			ex.setExLeft(var);
			ex.setExMiddle(BLogiker.EQUAL);
			ex.setExRight(CodecUtils.getNullValue());

			if (var.getBClass().getQualifiedName().equals(List.class.getName())) {
				BExpression child = (BExpression) ex.cloneAll();
				ex = view.createExpression();
				ex.setExLeft(child);
				ex.setExMiddle(BLogiker.LOGICOR);

				BInvoker right = view.createMethodInvoker();
				right.setInvokeParent((BValuable) var.cloneAll());
				BMethod method = view.createMethod();
				method.setLogicName("isEmpty");
				method.setName("isEmpty");
				BVariable var = view.createVariable();
				var.setBClass(CodecUtils.BBoolean());
				method.setReturn(var);
				right.setInvokeChild(method);

				ex.setExRight(right);
			} else if (var.getBClass().getQualifiedName().equals(String.class.getName())) {
				BActionModel action = (BActionModel) this.getPath().getAction();
				if (action.getProcessType().getType() != ProcessType.TYPE_PROCESS_TABLE) {
					BExpression child = (BExpression) ex.cloneAll();
					ex = view.createExpression();
					ex.setExLeft(child);
					ex.setExMiddle(BLogiker.LOGICOR);

					BExpression blankCheck = view.createExpression();
					blankCheck.setExLeft((BValuable) var.cloneAll());
					blankCheck.setExMiddle(BLogiker.EQUAL);
					BParameter para = view.createParameter();
					para.setBClass(CodecUtils.BString());
					para.setLogicName("\"\"");
					blankCheck.setExRight(para);

					ex.setExRight(blankCheck);
				}
			}
			return ex;
		}
		return null;
	}

}
