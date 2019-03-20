package com.linkstec.bee.core.codec;

import java.lang.reflect.Method;

import com.linkstec.bee.UI.node.AnnotationNode;
import com.linkstec.bee.UI.node.AnnotationParameterNode;
import com.linkstec.bee.UI.node.AssignExpressionNode;
import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BAssertNode;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BlockUnitNode;
import com.linkstec.bee.UI.node.CatchNode;
import com.linkstec.bee.UI.node.ClassHeaderNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ConditionNode;
import com.linkstec.bee.UI.node.ConstructorNode;
import com.linkstec.bee.UI.node.EmptyNode;
import com.linkstec.bee.UI.node.ExpressionNode;
import com.linkstec.bee.UI.node.IfNode;
import com.linkstec.bee.UI.node.LambdaNode;
import com.linkstec.bee.UI.node.LoopNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.node.OnewordNode;
import com.linkstec.bee.UI.node.ParameterNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.ReturnNode;
import com.linkstec.bee.UI.node.SingleExpressionNode;
import com.linkstec.bee.UI.node.SwitchNode;
import com.linkstec.bee.UI.node.ThrowNode;
import com.linkstec.bee.UI.node.TrueFalseLineNode;
import com.linkstec.bee.UI.node.TryNode;
import com.linkstec.bee.UI.node.view.CallerNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
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
import com.linkstec.bee.core.impl.BClassImpl;
import com.linkstec.bee.core.impl.BImportImpl;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

public class ViewPatternCreator implements IPatternCreator {
	public Object createByClass(Class<?> cls) {
		Class<? extends ViewPatternCreator> clzz = this.getClass();
		Method[] ms = clzz.getDeclaredMethods();
		for (Method m : ms) {

			if (m.getReturnType().equals(cls)) {
				try {
					return m.invoke(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Object createCaller(BInvoker invoker, BMethod method, mxGraph graph) {
		MethodNode node = (MethodNode) method;
		return new CallerNode((mxCell) invoker, node.getInConnector(), graph);
	}

	public BVariable createVariable() {
		return new ComplexNode();
	}

	public BMultiCondition createMultiCondition() {
		return new IfNode();
	}

	public BExpression createExpression() {
		return new ExpressionNode();
	}

	public BMethod createMethod() {
		MethodNode node = new MethodNode();
		node.setGeometry(new mxGeometry());
		return node;
	}

	public BLogicBody createMethodBody() {
		return new BLockNode();
	}

	public BReturnUnit createMethodReturn() {
		return new ReturnNode();
	}

	public BInvoker createMethodInvoker() {
		return new ReferNode();
	}

	public BObject createObject() {
		return new ObjectNode();
	}

	public BLoopUnit createLoop() {
		return new LoopNode();
	}

	public BTryUnit createTry() {
		return new TryNode();
	}

	public BCatchUnit createCatch() {
		return new CatchNode();
	}

	public BConditionUnit createCondition() {
		return new ConditionNode();
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
		return new SingleExpressionNode();
	}

	@Override
	public BOnewordLine createOnewordLine() {
		return new OnewordNode();
	}

	@Override
	public BEmptyUnit createEmpty() {
		return new EmptyNode();
	}

	@Override
	public BSwitchUnit createSwitch() {
		return new SwitchNode();
	}

	@Override
	public BExpressionLine createExpressionLine() {
		return new TrueFalseLineNode();
	}

	@Override
	public BModifiedBlock createModifiedBlock() {
		return new BlockUnitNode();
	}

	@Override
	public BConstructor createConstructor() {
		return new ConstructorNode();
	}

	@Override
	public BNote createComment() {
		return new NoteNode();
	}

	@Override
	public BThrow createThrow() {
		return new ThrowNode();
	}
	//
	// @Override
	// public BUnionType createUnionType() {
	// return new UnionTypeNode();
	// }

	@Override
	public BClassHeader craeteClassHeader() {
		return new ClassHeaderNode();
	}

	@Override
	public BParameter createParameter() {
		return new ParameterNode();
	}

	@Override
	public BAssignment createAssignment() {
		return new AssignmentNode();
	}

	@Override
	public BAssignExpression createAssignExpression() {
		return new AssignExpressionNode();
	}

	@Override
	public BAnnotation createAnnotaion() {
		return new AnnotationNode();
	}

	@Override
	public BAnnotationParameter createAnnotationParameter() {
		return new AnnotationParameterNode();
	}

	@Override
	public BAssert createAssert() {
		return new BAssertNode();
	}

	@Override
	public BLambda createLambda() {
		return new LambdaNode();
	}
}
