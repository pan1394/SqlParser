package com.linkstec.bee.core.fw;

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

public interface IPatternCreator {

	public BAssert createAssert();

	public BAnnotation createAnnotaion();

	public BLambda createLambda();

	public BAnnotationParameter createAnnotationParameter();

	public BClass createClass();

	public BClassHeader craeteClassHeader();

	public BImport createImport();

	// public Object createCaller(BInvoker invoker, BMethod method, mxGraph graph);

	public BVariable createVariable();

	public BParameter createParameter();

	public BMultiCondition createMultiCondition();

	public BExpression createExpression();

	public BMethod createMethod();

	public BLogicBody createMethodBody();

	public BReturnUnit createMethodReturn();

	public BInvoker createMethodInvoker();

	public BObject createObject();

	public BLoopUnit createLoop();

	public BTryUnit createTry();

	public BThrow createThrow();

	public BCatchUnit createCatch();

	public BConditionUnit createCondition();

	public BAssignment createAssignment();

	public BAssignExpression createAssignExpression();

	public BSingleExpressionUnit createSingleExpression();

	public BOnewordLine createOnewordLine();

	public BEmptyUnit createEmpty();

	public BSwitchUnit createSwitch();

	public BExpressionLine createExpressionLine();

	public BModifiedBlock createModifiedBlock();

	public BConstructor createConstructor();

	public BNote createComment();

	// public BUnionType createUnionType();
}
