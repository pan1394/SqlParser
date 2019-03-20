package com.linkstec.bee.core.codec;

import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.logic.BAssert;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BEmptyUnit;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLambda;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BModifiedBlock;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BOnewordLine;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.linkstec.bee.core.fw.logic.BSwitchUnit;
import com.linkstec.bee.core.fw.logic.BThrow;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.linkstec.bee.core.impl.BAnnotationImpl;
import com.linkstec.bee.core.impl.BAnnotationParameterImpl;
import com.linkstec.bee.core.impl.BAssignExpressionImpl;
import com.linkstec.bee.core.impl.BAssignmentImpl;
import com.linkstec.bee.core.impl.BClassImpl;
import com.linkstec.bee.core.impl.BConstructorImpl;
import com.linkstec.bee.core.impl.BImportImpl;
import com.linkstec.bee.core.impl.BMethodImpl;
import com.linkstec.bee.core.impl.BObjectImpl;
import com.linkstec.bee.core.impl.BParameterImpl;
import com.linkstec.bee.core.impl.BVariableImpl;

public class TempPatternCreator implements IPatternCreator {

	// @Override
	// public Object createCaller(BInvoker invoker, BMethod method, mxGraph graph) {
	// return null;
	// }

	@Override
	public BVariable createVariable() {
		return new BVariableImpl();
	}

	@Override
	public BMultiCondition createMultiCondition() {
		return null;
	}

	@Override
	public BExpression createExpression() {
		return null;
	}

	@Override
	public BMethod createMethod() {
		return new BMethodImpl();
	}

	@Override
	public BLogicBody createMethodBody() {
		return null;
	}

	@Override
	public BReturnUnit createMethodReturn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BInvoker createMethodInvoker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BObject createObject() {
		return new BObjectImpl();
	}

	@Override
	public BLoopUnit createLoop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BTryUnit createTry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BCatchUnit createCatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BConditionUnit createCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BAssignment createAssignment() {
		return new BAssignmentImpl();
	}

	@Override
	public BClass createClass() {
		return new BClassImpl();
	}

	@Override
	public BImport createImport() {
		return new BImportImpl();
	}

	@Override
	public BSingleExpressionUnit createSingleExpression() {
		return null;
	}

	@Override
	public BOnewordLine createOnewordLine() {
		return null;
	}

	@Override
	public BEmptyUnit createEmpty() {
		return null;
	}

	@Override
	public BSwitchUnit createSwitch() {
		return null;
	}

	@Override
	public BExpressionLine createExpressionLine() {
		return null;
	}

	@Override
	public BModifiedBlock createModifiedBlock() {
		return null;
	}

	@Override
	public BConstructor createConstructor() {
		return new BConstructorImpl();
	}

	@Override
	public BNote createComment() {
		return null;
	}

	@Override
	public BThrow createThrow() {
		return null;
	}

	@Override
	public BClassHeader craeteClassHeader() {
		return null;
	}

	@Override
	public BParameter createParameter() {
		return new BParameterImpl();
	}

	@Override
	public BAssignExpression createAssignExpression() {
		return new BAssignExpressionImpl();
	}

	@Override
	public BAnnotation createAnnotaion() {
		return new BAnnotationImpl();
	}

	@Override
	public BAnnotationParameter createAnnotationParameter() {

		return new BAnnotationParameterImpl();
	}

	@Override
	public BAssert createAssert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BLambda createLambda() {
		// TODO Auto-generated method stub
		return null;
	}

}
