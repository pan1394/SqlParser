package com.linkstec.bee.core.codec.decode;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import javax.swing.JOptionPane;

import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BeeClassLoader;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.codec.CodecAction.ThreadParameter;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotable;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssert;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLambda;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BMod;
import com.linkstec.bee.core.fw.logic.BModifiedBlock;
import com.linkstec.bee.core.fw.logic.BMultiCondition;
import com.linkstec.bee.core.fw.logic.BOnewordLine;
import com.linkstec.bee.core.fw.logic.BReturnUnit;
import com.linkstec.bee.core.fw.logic.BSingleExpressionUnit;
import com.linkstec.bee.core.fw.logic.BSwitchUnit;
import com.linkstec.bee.core.fw.logic.BThrow;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.linkstec.bee.core.impl.BClassImpl;
import com.linkstec.bee.core.impl.BTypeImpl;
import com.mxgraph.model.mxICell;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LambdaExpressionTree.BodyKind;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberReferenceTree.ReferenceMode;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCImport;

public class BasicSourceDecoder implements TreeVisitor<Object, Object>, IDecodeResult {

	protected BClass sheetModel = null;
	// for inner class
	protected BClass parentModel = null;
	protected BProject project;
	protected LineMap map;
	protected DocTrees trees;
	protected TreePath path;
	protected DecodeDoctreeVisitor docTree = new DecodeDoctreeVisitor();
	protected CompilationUnitTree unitTree;
	protected SourceInfo sourceInfo;
	protected DecodeGen gen;
	protected BObject currentVariable = null;
	protected BType currentType = null;
	protected IPatternCreator view, temp;
	protected BookModel model;
	protected List<Object> callers = new ArrayList<Object>();
	protected boolean anonymous = false;

	public BasicSourceDecoder(DecodeGen gen, BProject project, LineMap map, DocTrees trees, SourceInfo sourceInfo,
			BookModel model) {
		this.project = project;
		this.model = model;
		this.gen = gen;
		this.map = map;
		this.trees = trees;
		this.sourceInfo = sourceInfo;
		this.view = PatternCreatorFactory.createView();
		this.temp = PatternCreatorFactory.createTempPattern();
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	@Override
	public Object visitVariable(VariableTree tree, Object parent) {

		log("visitVariable:" + tree.toString());

		makePath(tree);

		return this.makeVariable(tree, parent, true);
	}

	// for the first time class variable made,do not make value because it possibly
	// calls method which has not made that timing
	private Object makeVariable(VariableTree tree, Object parent, boolean makeValue) {
		String typeName = this.readComment(tree);

		Tree tt = tree.getType();

		BParameter type = this.getType(tt);
		if (type == null) {
			// parameterized
			// Debug.d();
			BType btype = new BTypeImpl();
			btype.setLogicName(tt.toString());
			type = view.createParameter();
			type.setParameterizedTypeValue(btype);
			type.setBClass(CodecUtils.getClassFromJavaClass(Object.class, project));
		}
		type.setLogicName(tree.getName().toString());

		if (typeName != null && !typeName.equals("")) {
			type.setName(typeName);
		} else {
			type.setName(tree.getName().toString());
		}

		BAssignment assign = view.createAssignment();

		assign.setLeft(type);
		if (makeValue) {
			this.makeVariableValue(tree, assign, type);
		}

		if (tree.getNameExpression() != null) {
			String name = (String) tree.getNameExpression().accept(this, null);
			error("getNameExpression:" + name);
		}

		ModifiersTree modifir = tree.getModifiers();
		if (modifir != null) {
			// for annotation use ,put type to it
			int mds = (int) modifir.accept(this, assign);

			type.setModifier(mds);

		}

		// for method parameter
		if (parent != null && parent instanceof Boolean) {
			Boolean b = (Boolean) parent;
			if (!b.booleanValue()) {
				return assign;
			}
		}
		type.setOwener(this.currentVariable);
		currentVariable = type;
		return assign;

	}

	private void makeVariableValue(VariableTree tree, BAssign assign, BVariable type) {
		ExpressionTree init = tree.getInitializer();
		if (init != null) {
			// for expamle:String[] ss={};
			Object obj = init.accept(this, type);
			if (obj instanceof BMethod) {
				BInvoker invoker = view.createMethodInvoker();
				invoker.setInvokeChild(invoker);
				BVariable var = view.createParameter();
				var.makeCaller(sheetModel, true);
				invoker.setInvokeParent(var);
				assign.setRight(invoker, null);
			} else {
				assign.setRight((BValuable) obj, null);
			}
		} else {
			assign.setRight(null, null);
		}
	}

	@Override
	public Object visitClass(ClassTree tree, Object parent) {
		log("visitClass:" + tree.toString());
		makePath(tree);

		if (parent instanceof JCCompilationUnit) {
			sheetModel = new BeeModel();
			JCCompilationUnit unit = (JCCompilationUnit) parent;

			if (unit.getPackageName() != null) {
				sheetModel.setPackage(unit.getPackageName().toString());
			}
			this.makeImports(unit);

		} else if (parent instanceof BeeModel) {
			// inner class
			parentModel = (BeeModel) parent;
			sheetModel.setPackage(parentModel.getPackage());
			sheetModel.getImports().addAll(parentModel.getImports());
		}
		model.getList().add((BEditorModel) sheetModel);

		// anonymous,the value will be set when parent class analyze
		sheetModel.setAnonymous(this.anonymous);

		String title = this.getClassTitle(tree);
		this.makeCommon(tree);

		if (!(parent instanceof BeeModel)) {
			// not inner class
			sheetModel.setName(title);
			sheetModel.setLogicName(tree.getSimpleName().toString());
			model.setName(sheetModel.getName());
			model.setLogicName(sheetModel.getLogicName());
		}

		// modifier
		ModifiersTree mod = tree.getModifiers();
		int mo = (int) mod.accept(this, sheetModel);

		sheetModel.setModifier(mo);

		mxICell root = ((mxICell) ((BeeModel) sheetModel).getRoot()).getChildAt(0);

		BClassHeader header = view.craeteClassHeader();
		header.setBClass(sheetModel);
		root.insert((mxICell) header);

		P.start(5);

		// 1.pickup all class members.varible,blocks,inner class,method
		List<? extends Tree> trees = tree.getMembers();
		P.start(trees.size());

		// variables for pickup
		Hashtable<ClassTree, DecodeSource> inners = new Hashtable<ClassTree, DecodeSource>();
		Hashtable<MethodTree, BMethod> methods = new Hashtable<MethodTree, BMethod>();
		Hashtable<VariableTree, BAssignment> variables = new Hashtable<VariableTree, BAssignment>();
		List<BlockTree> blocks = new ArrayList<BlockTree>();
		// start to pick up
		for (Tree t : trees) {

			if (t instanceof ClassTree) {
				ClassTree ctree = (ClassTree) t;
				DecodeSource ds = new DecodeSource(gen, project, map, this.trees, sourceInfo, model);
				BeeModel inner = new BeeModel();
				inner.setPackage(sheetModel.getPackage());
				inner.setLogicName(tree.getSimpleName().toString() + "$" + ctree.getSimpleName().toString());
				inner.setName(ctree.getSimpleName().toString());
				inner.setInnerParentClassName(this.sheetModel.getQualifiedName());
				// make it to be tree with parent?
				ds.setPath(new TreePath(this.unitTree));
				ds.setSheetModel(inner);
				List<BEditorModel> list = this.model.getList();

				for (BEditorModel ibm : list) {
					BClass b = (BClass) ibm;
					if (b.getQualifiedName().equals(inner.getQualifiedName())) {
						return null;
					}
				}
				inners.put(ctree, ds);
				P.go();
				continue;
			} else if (t instanceof BlockTree) {
				blocks.add((BlockTree) t);
				P.go();
				continue;
			} else if (t instanceof VariableTree) {
				VariableTree vt = (VariableTree) t;

				BAssignment node = (BAssignment) this.makeVariable(vt, null, false);
				BVariable v = node.getLeft();
				// if (v.getName() == null) {
				String name = getDoc().getVariableDoc(v.getLogicName());
				if (name != null && !name.equals("")) {
					v.setName(name);
				}
				// }
				// this.sheetModel.getVariables().add(node);
				root.insert((mxICell) node);
				variables.put(vt, node);
				P.go();
				continue;
			}
			Object obj = t.accept(this, null);
			root.insert((mxICell) obj);

			if (obj instanceof BMethod) {
				methods.put((MethodTree) t, (BMethod) obj);
			}

			P.go();
		}
		P.end();
		// pickup end

		// all picks end,so the class variables is fixed
		BObject classVariablePath = currentVariable;
		BType classType = this.currentType;
		P.go();

		// 2. scan variables
		P.start(inners.size());
		Enumeration<VariableTree> keys = variables.keys();
		while (keys.hasMoreElements()) {
			VariableTree m = keys.nextElement();
			BAssignment node = variables.get(m);
			this.makeVariableValue(m, node, (BVariable) node.getLeft());
			P.go();
		}
		P.end();
		// variables end
		P.go();

		// 3.scan class blocks
		P.start(blocks.size());
		for (BlockTree block : blocks) {
			BModifiedBlock b = view.createModifiedBlock();
			block.accept(this, b);
			if (block.isStatic()) {
				b.setMods(java.lang.reflect.Modifier.STATIC);
			}
			root.insert((mxICell) b);
			P.go();
		}
		P.end();
		// block end
		P.go();

		// 4. scan method
		P.start(inners.size());
		Enumeration<MethodTree> en = methods.keys();
		while (en.hasMoreElements()) {
			MethodTree m = en.nextElement();
			BMethod node = methods.get(m);
			this.makeMethodBody(m, node, classVariablePath, classType);
			P.go();
		}
		P.end();
		// method end
		P.go();

		// 5. scan inner class
		Enumeration<ClassTree> enu = inners.keys();
		P.start(inners.size());
		while (enu.hasMoreElements()) {
			ClassTree ctree = enu.nextElement();
			DecodeSource visitor = inners.get(ctree);
			ctree.accept(visitor, sheetModel);
			P.go();
		}
		P.end();
		// inner class end

		// 5 task end
		P.end();
		return this.sheetModel;
	}

	protected void makeMethodBody(MethodTree m, BMethod node, BObject classVariablePath, BType classType) {
		currentVariable = classVariablePath;

		BlockTree body = m.getBody();
		if (body == null) {
			return;
		}

		currentType = classType;
		List<BType> types = node.getDefinedTypes();
		for (BType type : types) {
			type.setOwener(this.currentType);
			currentType = type;
		}

		List<BParameter> parameters = node.getParameter();
		for (BParameter p : parameters) {
			p.setOwener(this.currentVariable);
			currentVariable = p;
		}
		body.accept(this, node.getLogicBody());
	}

	@Override
	public Object visitMethod(MethodTree tree, Object parent) {
		log("visitMethod:" + tree.toString());

		makePath(tree);
		BMethod method = view.createMethod();
		if (tree.getName().toString().equals("<init>")) {
			method = view.createConstructor();
		} else {
			method.setLogicName(tree.getName().toString());
		}

		BType classType = this.currentType;

		List<? extends TypeParameterTree> types = tree.getTypeParameters();
		for (TypeParameterTree type : types) {
			// do not put variable to at path this time,so put the "false"
			BType var = (BType) type.accept(this, false);
			BTypeImpl b = (BTypeImpl) var;
			b.setParameterValue(true);
			method.addDefinedType(var);
			b.setOwener(this.currentType);
			this.currentType = b;

		}

		DocCommentTree docs = trees.getDocCommentTree(path);
		if (docs != null) {
			@SuppressWarnings("unchecked")
			Hashtable<String, Object> hash = (Hashtable<String, Object>) docs.accept(docTree, null);

			if (!tree.getName().toString().equals("<init>")) {
				String title = (String) hash.get("title");
				if (title != null) {
					String comment = title;
					if (comment.indexOf("\n") > 0) {
						comment = comment.substring(0, comment.indexOf("\n"));
					}
					method.setName(comment);
				} else {
					method.setName(tree.getName().toString());
				}
			}

			Hashtable<?, ?> paras = (Hashtable<?, ?>) hash.get("param");
			this.makeMethodParameter(tree, method, paras);
		} else {
			if (!tree.getName().toString().equals("<init>")) {
				method.setName(tree.getName().toString());
			}
			this.makeMethodParameter(tree, method, null);
		}

		ModifiersTree modifier = tree.getModifiers();

		int mds = (int) modifier.accept(this, method);

		method.setModifier(mds);

		// TODO public List<String> getString()??
		if (tree.getReceiverParameter() != null) {
			Application.TODO("visitMethod :getReceiverParameter");
			Debug.d();
			tree.getReceiverParameter().accept(this, tree);
		}
		BVariable var = view.createVariable();
		if (tree.getReturnType() != null) {
			Tree t = tree.getReturnType();

			var = (BVariable) this.getType(t);
			if (var == null) {
				var = (BVariable) t.accept(this, null);
			}
			if (var == null) {
				Debug.d();
			}
			// }
		} else {
			var.setBClass(CodecUtils.getClassFromJavaClass(void.class, project));
		}
		method.setReturn(var);

		List<? extends ExpressionTree> ths = tree.getThrows();
		for (ExpressionTree th : ths) {
			// not use getType
			BVariable obj = (BVariable) th.accept(this, method);
			method.addThrows(obj);
		}

		if (method instanceof BConstructor) {
			this.sheetModel.getConstructors().add((BConstructor) method);
			return method;
		}

		this.sheetModel.getMethods().add(method);

		this.currentType = classType;

		return method;

	}

	private void makeMethodParameter(MethodTree tree, BMethod method, Hashtable<?, ?> paras) {

		if (tree.getParameters().size() > 0) {

			for (int i = 0; i < tree.getParameters().size(); i++) {
				VariableTree v = tree.getParameters().get(i);

				// do not put variable to at path this time,so put the "false"
				BAssignment variable = (BAssignment) v.accept(this, false);
				BParameter left = variable.getLeft();

				String logicName = left.getLogicName();
				if (paras != null) {
					String name = (String) paras.get(logicName);
					if (name != null && !name.equals("")) {
						left.setName(name);
					} else {
						left.setName(logicName);
					}
				} else {
					left.setName(logicName);
				}
				method.addParameter(left);

				List<BType> types = method.getDefinedTypes();
				for (BType type : types) {
					if (type.getLogicName().equals(v.getType().toString())) {
						left.setParameterizedTypeValue((BType) type.cloneAll());
					}
				}

				if (v.toString().indexOf("...") > 0) {
					left.setVarArgs(true);
				}
			}
		}

	}

	@Override
	public Object visitBlock(BlockTree tree, Object parent) {
		log("visitBlock:" + tree.toString());
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;
		BLogicBody editor = null;
		if (parent != null) {
			if (parent.equals("EDITOR")) {
				editor = view.createModifiedBlock();
			} else if (parent instanceof BLogicBody) {
				editor = (BLogicBody) parent;
			}
		}

		if (editor == null) {
			error("fital error : " + this.sheetModel.getQualifiedName());
		}
		List<? extends StatementTree> list = tree.getStatements();

		for (StatementTree t : list) {
			String comment = this.readComment(t);
			if (comment != null) {
				BNote note = view.createComment();
				note.setNote(comment);
				editor.addUnit(note);
			}
			Object obj = t.accept(this, "EDITOR");
			if (obj == null) {
				Debug.d();
			}
			if (obj instanceof BLogicUnit) {
				BLogicUnit unit = (BLogicUnit) obj;
				editor.addUnit(unit);
			} else {
				Debug.d();

			}

		}

		this.currentVariable = blockVariablePath;
		return editor;

	}

	@Override
	public Object visitExpressionStatement(ExpressionStatementTree tree, Object parent) {

		log("visitExpressionStatement:" + tree.toString());
		makePath(tree);
		ExpressionTree statement = tree.getExpression();
		Object obj = statement.accept(this, null);

		return obj;
	}

	@Override
	public Object visitAssignment(AssignmentTree tree, Object parent) {
		log("visitAssignment:" + tree.toString());
		makePath(tree);

		ExpressionTree var = tree.getVariable();
		ExpressionTree value = tree.getExpression();

		BAssignExpression assin = view.createAssignExpression();

		BValuable title = (BValuable) var.accept(this, parent);
		BValuable para = (BValuable) value.accept(this, parent);

		assin.setLeft(title);
		assin.setRight(para, this.getLogiker(tree.getKind()));

		return assin;
	}

	@Override
	public Object visitMemberSelect(MemberSelectTree tree, Object parameters) {
		log("visitMemberSelect:" + tree.toString() + ",parameters:" + parameters);
		makePath(tree);

		// boolean isClassa = false;
		String classInvoke = tree.toString();
		// for the sake of efficiency
		if (classInvoke.startsWith("java.") || classInvoke.startsWith("com.") || classInvoke.startsWith("org.")) {
			Class<?> cls = CodecUtils.getClassByName(classInvoke, project);
			if (cls != null) {
				return this.getClassVariable(cls);
			}

		}

		if (classInvoke.endsWith(".class")) {
			classInvoke = classInvoke.substring(0, classInvoke.lastIndexOf("."));
			Class<?> clss = CodecUtils.getType(classInvoke, sheetModel, project);
			if (clss != null) {
				BVariable var = this.getClassVariable(clss);
				var.setBClass(CodecUtils.getClassFromJavaClass(Class.class, project));
				var.setLogicName(clss.getName() + ".class");
				return var;
			}
		}
		if (parameters != null) {
			classInvoke = null;
		}

		Name name = tree.getIdentifier();
		String member = name.toString();

		Object obj = tree.getExpression().accept(this, "MemberSelect");

		if (obj == null && classInvoke != null) {

			Class<?> clss = CodecUtils.getType(classInvoke, sheetModel, project);

			if (clss != null) {
				BVariable vvar = this.getClassVariable(clss);
				if (vvar != null) {
					// DOTO return a type
					return vvar;
				}
			}

		}

		List<BValuable> list = new ArrayList<BValuable>();
		if (parameters != null && parameters instanceof List) {
			List<?> l = (List<?>) parameters;
			for (Object v : l) {
				if (v instanceof BValuable) {
					list.add((BValuable) v);
				} else if (v == null) {
					list.add(CodecUtils.getNullValue());
				} else {
					Debug.d();
					throw new RuntimeException("DECODE");
				}
			}

		}

		if (obj instanceof BValuable) {

			BValuable node = (BValuable) obj;
			BInvoker refer = view.createMethodInvoker();
			refer.setInvokeParent(node);

			BClass bclass = node.getBClass();
			if (bclass == null) {
				Debug.d("");
			}
			if (bclass.isArray()) {
				if (node.getArrayObject() != null) {
					node = node.getArrayObject();
				}
			}

			bclass = node.getBClass();

			String className = bclass.getQualifiedName();

			// super class,interface
			BValuable svalue = this.sheetModel.getSuperClass();
			if (className.equals(Object.class.getName())) {
				BClass bc = null;
				if (svalue != null) {
					bc = CodecUtils.getBClassInValueType(node, svalue, project);
				}
				if (bc != null) {
					List<BValuable> inters = this.sheetModel.getInterfaces();
					for (BValuable inter : inters) {
						bc = CodecUtils.getBClassInValueType(node, inter, project);
						if (bc != null) {
							break;
						}
					}
				} else {
					if (node instanceof BVariable) {
						BVariable var = (BVariable) node;
						BType type = var.getParameterizedTypeValue();
						if (type != null) {
							if (type.isTypeVariable()) {
								List<String> types = type.getBounds();
								if (types.size() == 1) {
									String s = types.get(0);
									bc = CodecUtils.getClassFromJavaClass(project, s);
								}
							}
						}
					}
				}
				if (bc != null) {
					bclass = bc;
					className = bc.getQualifiedName();
				}
			}

			BMethod foundMethod = null;
			if (node instanceof BVariable) {
				BVariable var = (BVariable) node;

				if (var.getLogicName() == null) {

					if (classInvoke != null) {
						Class<?> clss = CodecUtils.getType(classInvoke, sheetModel, project);

						if (clss != null) {
							BVariable vvar = this.getClassVariable(clss);
							if (vvar != null) {
								return vvar;
							}
						}
					}

				}
				// local method or field
				if (var.getLogicName() != null && var.getLogicName().equals("this")) {

					List<BAssignment> vars = this.sheetModel.getVariables();
					for (BAssignment assign : vars) {
						BParameter bvar = assign.getLeft();
						if (bvar.getLogicName().equals(name.toString())) {
							bvar = (BParameter) bvar.cloneAll();
							bvar.setCaller(true);
							refer.setInvokeChild(bvar);
							return refer;
						}
					}

					foundMethod = CodecUtils.getMethod(this.sheetModel, name.toString(), list, project);
					if (foundMethod != null) {
						foundMethod.setDeclearedParentName("this");

					}

				} else if (var.getLogicName() != null && var.getLogicName().equals("super")) {

					if (this.sheetModel.getSuperClass() == null) {
						foundMethod = CodecUtils.getClassMethod(Object.class.getName(), member, list, project, true);
					} else {
						foundMethod = CodecUtils.getClassMethod(
								this.sheetModel.getSuperClass().getBClass().getQualifiedName(), member, list, project,
								true);
					}
					if (this.sheetModel.getSuperClass() == null) {
						foundMethod = CodecUtils.getClassMethod(Object.class.getName(), member, list, project, true);
					} else {
						foundMethod = CodecUtils.getClassMethod(
								this.sheetModel.getSuperClass().getBClass().getQualifiedName(), member, list, project,
								true);
					}
					if (foundMethod != null) {
						foundMethod.setDeclearedParentName("super");
					}
				}
			}
			if (node.getBClass() == null) {
				Debug.d("fital error: ( at visitMemberSelect) BClass is null ->" + this.sheetModel.getQualifiedName()
						+ ":" + node.toString());
			}
			BClass innerClass = null;
			String type = null;

			if (foundMethod != null) {
				type = BInvoker.TYPE_METHOD;
			} else {
				innerClass = CodecUtils.getInnerClass(member, bclass, project);
				if (innerClass != null) {
					type = BInvoker.TYPE_INNERCLASS;
				} else if (node.getBClass().isData()) {
					type = BInvoker.TYPE_FIELD;
				} else {
					if (parameters != null && parameters instanceof List) {
						type = BInvoker.TYPE_METHOD;
					} else {
						type = BInvoker.TYPE_FIELD;
					}
				}
			}
			if (type.equals(BInvoker.TYPE_FIELD)) {

				String dataName = "";
				if (node.getBClass().isData()) {
					refer.setDataMethodName(member);
					if (member.startsWith("set")) {
						dataName = member.substring(3);
						dataName = dataName.substring(0, 1).toLowerCase() + dataName.substring(1);
					} else if (member.startsWith("get")) {
						dataName = member.substring(3);
						dataName = dataName.substring(0, 1).toLowerCase() + dataName.substring(1);
					} else if (member.startsWith("is")) {
						dataName = member.substring(2);
						dataName = dataName.substring(0, 1).toLowerCase() + dataName.substring(1);
					} else {
						dataName = member;
					}
				} else {
					dataName = member;
				}

				// name
				BVariable var = view.createVariable();
				String mName = DecodeDocProjects.getDoc(project).getDoc(className).getVariableDoc(dataName);
				var.setLogicName(dataName);
				if (mName != null && !mName.equals("")) {
					var.setName(mName);
				} else {
					var.setName(dataName);
				}
				var.setCaller(true);

				// type
				BValuable valuable = null;
				if (node.getBClass().isData()) {

					if (list.size() == 1) {

						Class<?> cls = CodecUtils.getClassByName(className, project);
						valuable = CodecUtils.getDataClassMethodSetterParameterType(cls, member, project);

					} else {
						boolean includeSuperProteced = false;
						if (bclass.getQualifiedName().equals(this.sheetModel.getQualifiedName())) {
							includeSuperProteced = true;
						} else if (this.parentModel != null
								&& bclass.getQualifiedName().equals(this.parentModel.getQualifiedName())) {
							includeSuperProteced = true;
						}
						valuable = CodecUtils.getMethodRuturnType(className, member, list, project,
								includeSuperProteced);

						// dto but ,there are some field defined along
						if (valuable == null) {
							valuable = CodecUtils.getVariableType(className, member, project,
									className.startsWith(this.sheetModel.getQualifiedName()));
						} else {

						}
					}
				} else {
					valuable = CodecUtils.getVariableType(className, member, project,
							className.startsWith(this.sheetModel.getQualifiedName()));
				}

				if (var == null || valuable == null) {
					Debug.d("fital error value is null");

				}

				var.setBClass(valuable.getBClass());
				var.setParameterizedTypeValue(valuable.getParameterizedTypeValue());

				refer.setInvokeChild(var);
			} else if (type.equals(BInvoker.TYPE_METHOD)) {
				BMethod method;

				if (foundMethod != null) {
					method = foundMethod;
				} else {
					boolean needProteced = false;

					if (className.equals(sheetModel.getQualifiedName())) {
						needProteced = true;
					} else {
						if (this.parentModel != null) {
							if (className.equals(this.parentModel.getQualifiedName())) {
								needProteced = true;
							}
						}
					}
					method = CodecUtils.getClassMethod(className, member, list, project, needProteced);
				}

				if (method == null) {
					Debug.d("method == null, member (" + member + ") select error : can not identify child");
					tree.getExpression().accept(this, null);
				}

				// return value
				BValuable returnValue = method.getReturn();

				BValuable var = CodecUtils.getValueable(node, returnValue, project);
				if (var == null || var.getBClass().getQualifiedName().equals(Object.class.getName())) {
					var = CodecUtils.getMethodReturnByDefinedType(refer, method, list, project);
				}
				if (var != null && !var.getBClass().getQualifiedName().equals(Object.class.getName())) {
					method.setReturn(var);
				}

				refer.setInvokeChild(method);
			} else if (type.equals(BInvoker.TYPE_INNERCLASS)) {

				BVariable var = view.createVariable();
				var.setBClass(innerClass);
				var.setLogicName(innerClass.getQualifiedName());

				String mName = DecodeDocProjects.getDoc(project).getDoc(innerClass.getQualifiedName()).getClassDoc();
				if (mName != null) {
					var.setName(mName);
				} else {
					var.setName(innerClass.getName());
				}

				refer.setInvokeChild(var);
			} else {
				Debug.d("fital error occurred:" + member + "はサポートされていません（visitMemberSelect）");

			}

			return refer;

		}
		return null;

	}

	@Override
	public Object visitTypeCast(TypeCastTree tree, Object parent) {
		log("visitTypeCast:" + tree.toString());
		makePath(tree);
		ExpressionTree ex = tree.getExpression();
		Object obj = ex.accept(this, parent);
		Object type = tree.getType().accept(this, parent);
		if (type instanceof BValuable) {
			BValuable cast = (BValuable) type;
			if (obj instanceof BValuable) {
				BValuable boj = (BValuable) obj;
				boj.setCast(cast);
			}
		}

		return obj;
	}

	@Override
	public Object visitCompilationUnit(CompilationUnitTree t, Object parent) {
		log("visitCompilationUnit:" + t.toString());

		this.unitTree = t;

		List<? extends Tree> trees = t.getTypeDecls();
		path = new TreePath(t);

		for (Tree tree : trees) {
			tree.accept(this, parent);
		}
		return this.sheetModel;
	}

	@Override
	public Object visitIf(IfTree tree, Object parent) {
		log("visitIf:" + tree.toString());

		makePath(tree);

		BMultiCondition ifelse = view.createMultiCondition();
		ifelse.clearAllConditionUnit();
		ExpressionTree ex = tree.getCondition();

		BObject blockVariablePath = this.currentVariable;

		// List<BConditionUnit> condtions = ifelse.getConditionUnits();

		BConditionUnit unit = view.createCondition();
		ifelse.addCondition(unit);
		unit.clearConditions();
		BValuable con = (BValuable) ex.accept(this, unit);

		unit.setCondition(con);
		StatementTree then = tree.getThenStatement();
		then.accept(this, unit.getLogicBody());

		StatementTree el = tree.getElseStatement();
		if (el != null) {
			BConditionUnit c = view.createCondition();
			c.setLast(true);
			Object obj = el.accept(this, c.getLogicBody());
			if (obj instanceof BMultiCondition) {
				BMultiCondition mult = (BMultiCondition) obj;
				List<BConditionUnit> list = mult.getConditionUnits();
				for (BConditionUnit u : list) {
					ifelse.addCondition(u);
				}
			} else {
				ifelse.addCondition(c);
			}

		}

		this.currentVariable = blockVariablePath;
		return ifelse;
	}

	@Override
	public Object visitAnnotatedType(AnnotatedTypeTree tree, Object arg1) {
		log("visitAnnotatedType:" + tree.toString());
		makePath(tree);

		ExpressionTree type = tree.getUnderlyingType();
		BParameter paremeter = this.getType(type);

		List<? extends AnnotationTree> anos = tree.getAnnotations();
		for (AnnotationTree t : anos) {
			BAnnotation obj = (BAnnotation) t.accept(this, null);
			paremeter.addAnnotation(obj);
		}

		return paremeter;
	}

	@Override
	public Object visitAnnotation(AnnotationTree tree, Object parent) {
		log("visitAnnotation:" + tree.toString());
		makePath(tree);

		Tree type = tree.getAnnotationType();
		BVariable typeobj = (BVariable) type.accept(this, "Annotation");

		BClass bclass = typeobj.getBClass();

		BAnnotation anno = view.createAnnotaion();

		anno.setLogicName(bclass.getLogicName());
		anno.setName(bclass.getName());
		anno.setBClass(bclass);

		List<? extends ExpressionTree> list = tree.getArguments();
		for (ExpressionTree t : list) {

			BAnnotationParameter para = view.createAnnotationParameter();

			if (t instanceof AssignmentTree) {
				AssignmentTree assign = (AssignmentTree) t;
				ExpressionTree left = assign.getVariable();
				para.setName(left.toString());

				BMethod bmethod = CodecUtils.getClassMethod(typeobj.getBClass().getQualifiedName(), left.toString(),
						null, project, false);

				ExpressionTree right = assign.getExpression();
				Object obj = right.accept(this, bmethod.getReturn());
				para.setLogicName(bmethod.getLogicName());
				para.setName(bmethod.getName());
				if (obj instanceof BValuable) {
					BValuable value = (BValuable) obj;
					para.setBClass(value.getBClass());
					para.setValue(value);
				}

			} else {

				BValuable value = (BValuable) t.accept(this, typeobj);
				if (value == null) {
					Debug.a();
				}
				para.setBClass(value.getBClass());
				para.setLogicName("value");
				para.setName("value");
				para.setValue(value);
			}

			anno.addParameter(para);
		}

		return anno;
	}

	@Override
	public Object visitArrayAccess(ArrayAccessTree tree, Object parent) {
		log("visitArrayAccess:" + tree.toString());
		makePath(tree);

		ExpressionTree ex = tree.getExpression();
		BValuable var = (BValuable) ex.accept(this, parent);

		BVariable variable = view.createVariable();
		variable.setArrayObject(var);
		variable.setBClass((BClass) var.getBClass().getArrayPressentClass());
		ExpressionTree in = tree.getIndex();

		BValuable index = (BValuable) in.accept(this, parent);
		variable.setArrayIndex(index);

		return variable;
	}

	@Override
	public Object visitArrayType(ArrayTypeTree tree, Object parent) {
		log("visitArrayType:" + tree.toString());
		makePath(tree);

		Tree type = tree.getType();
		BVariable obj = this.getType(type);
		obj.getBClass().setArrayPressentClass(obj.getBClass().cloneAll());
		obj.setName(obj.getBClass().getName() + "の配列");
		obj.setClass(true);

		return obj;
	}

	@Override
	public Object visitAssert(AssertTree tree, Object parent) {
		log("visitAssert:" + tree.toString());
		makePath(tree);

		// TODO
		ExpressionTree condition = tree.getCondition();
		ExpressionTree detail = tree.getDetail();
		BValuable c = (BValuable) condition.accept(this, null);
		if (detail != null) {
			Object d = detail.accept(this, null);
		}

		BAssert asser = view.createAssert();
		asser.setExpression(c);

		return asser;
	}

	@Override
	public Object visitBinary(BinaryTree tree, Object box) {
		log("visitBinary(condition):" + tree.toString());
		makePath(tree);

		ExpressionTree left = tree.getLeftOperand();
		ExpressionTree right = tree.getRightOperand();

		BExpression compare = view.createExpression();

		Kind k = tree.getKind();

		if (k.equals(Kind.NOT_EQUAL_TO)) {
			compare.setExMiddle(BLogiker.NOTQUEAL);
		} else if (k.equals(Kind.EQUAL_TO)) {
			compare.setExMiddle(BLogiker.EQUAL);
		} else if (k.equals(Kind.GREATER_THAN)) {
			compare.setExMiddle(BLogiker.GREATTHAN);
		} else if (k.equals(Kind.LESS_THAN)) {
			compare.setExMiddle(BLogiker.LESSTHAN);
		} else if (k.equals(Kind.GREATER_THAN_EQUAL)) {
			compare.setExMiddle(BLogiker.GREATTHANEQUAL);
		} else if (k.equals(Kind.LESS_THAN_EQUAL)) {
			compare.setExMiddle(BLogiker.LESSTHANEQUAL);
		} else if (k.equals(Kind.INSTANCE_OF)) {
			compare.setExMiddle(BLogiker.INSTANCEOF);
		} else if (k.equals(Kind.PLUS)) {
			compare.setExMiddle(BLogiker.PLUS);
		} else if (k.equals(Kind.MINUS)) {
			compare.setExMiddle(BLogiker.MINUS);
		} else if (k.equals(Kind.MULTIPLY)) {
			compare.setExMiddle(BLogiker.MULTIPLY);
		} else if (k.equals(Kind.DIVIDE)) {
			compare.setExMiddle(BLogiker.DIVIDE);
		} else if (k.equals(Kind.XOR)) {
			compare.setExMiddle(BLogiker.XOR);
		} else if (k.equals(Kind.LEFT_SHIFT)) {
			compare.setExMiddle(BLogiker.SHEFTLEFT);
		} else if (k.equals(Kind.RIGHT_SHIFT)) {
			compare.setExMiddle(BLogiker.SHEFTRIGHT);
		} else if (k.equals(Kind.AND)) {
			compare.setExMiddle(BLogiker.BITAND);
		} else if (k.equals(Kind.OR)) {
			compare.setExMiddle(BLogiker.BITOR);
		} else if (k.equals(Kind.CONDITIONAL_AND)) {
			compare.setExMiddle(BLogiker.LOGICAND);
		} else if (k.equals(Kind.CONDITIONAL_OR)) {
			compare.setExMiddle(BLogiker.LOGICOR);
		} else if (k.equals(Kind.REMAINDER)) {
			compare.setExMiddle(BLogiker.MOD);
		} else {
			Debug.d();
		}

		compare.setExLeft((BValuable) left.accept(this, compare));
		compare.setExRight((BValuable) right.accept(this, compare));

		return compare;
	}

	@Override
	public Object visitBreak(BreakTree tree, Object arg1) {
		log("visitBreak:" + tree.toString());
		makePath(tree);

		Name l = tree.getLabel();
		BOnewordLine line = view.createOnewordLine();
		line.setWord(BOnewordLine.WORD_BREAK);
		if (l != null) {
			line.setLabel(l.toString());
		}
		return line;
	}

	@Override
	public Object visitCase(CaseTree tree, Object parent) {
		log("visitCase:" + tree.toString());
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;
		BSwitchUnit swi = (BSwitchUnit) parent;
		ExpressionTree ex = tree.getExpression();
		BConditionUnit condition = view.createCondition();

		if (ex != null) {
			BValuable left = swi.getVariable();
			BClass bclass = left.getBClass();
			if (bclass.isEnum()) {
				BVariable v = view.createVariable();
				v.setBClass(bclass.cloneAll());
				v.setLogicName(ex.toString());
				v.setName(ex.toString());
				condition.setCondition(v);
			} else {
				BValuable v = (BValuable) ex.accept(this, parent);
				BExpression expression = view.createExpression();
				expression.setExLeft((BValuable) left.cloneAll());
				expression.setExRight(v);
				expression.setExMiddle(BLogiker.EQUAL);
				condition.setCondition(expression);

			}
			swi.addCondition(condition);
		} else {
			condition = swi.makeDefault();
		}

		List<? extends StatementTree> ste = tree.getStatements();
		for (StatementTree s : ste) {
			BLogicUnit unit = (BLogicUnit) s.accept(this, parent);
			condition.getLogicBody().addUnit(unit);
		}
		this.currentVariable = blockVariablePath;
		return condition;
	}

	@Override
	public Object visitCatch(CatchTree tree, Object parent) {
		log("visitCatch:" + tree.toString());
		makePath(tree);

		BObject blockVariablePath = this.currentVariable;

		BCatchUnit unit = view.createCatch();
		BAssignment v = (BAssignment) tree.getParameter().accept(this, parent);

		BParameter bv = v.getLeft();
		if (bv.getName() == null) {
			bv.setName("エラー");
		}
		unit.setVariable(bv);

		// unit.setAssign(v);
		BlockTree block = tree.getBlock();

		block.accept(this, unit.getEditor());

		this.currentVariable = blockVariablePath;
		return unit;
	}

	@Override
	public Object visitCompoundAssignment(CompoundAssignmentTree tree, Object parent) {
		log("visitCompoundAssignment:" + tree.toString());
		makePath(tree);

		ExpressionTree value = tree.getExpression();
		ExpressionTree var = tree.getVariable();

		BValuable title = (BValuable) var.accept(this, parent);
		BValuable para = (BValuable) value.accept(this, parent);

		BAssignExpression assin = view.createAssignExpression();
		assin.setLeft(title);
		assin.setRight(para, this.getLogiker(tree.getKind()));
		return assin;

	}

	@Override
	public Object visitConditionalExpression(ConditionalExpressionTree tree, Object parent) {
		log("visitConditionalExpression:" + tree.toString());
		makePath(tree);

		ExpressionTree con = tree.getCondition();
		BValuable conObj = (BValuable) con.accept(this, parent);

		ExpressionTree falseEx = tree.getFalseExpression();
		BValuable falseObj = (BValuable) falseEx.accept(this, parent);

		ExpressionTree trueEx = tree.getTrueExpression();
		BValuable trueObj = (BValuable) trueEx.accept(this, parent);

		BExpressionLine line = view.createExpressionLine();
		line.setCondition(conObj);
		line.setTrue(trueObj);
		line.setFalse(falseObj);

		return line;
	}

	@Override
	public Object visitContinue(ContinueTree tree, Object arg1) {
		log("visitContinue:" + tree.toString());
		makePath(tree);

		Name name = tree.getLabel();
		BOnewordLine line = view.createOnewordLine();
		line.setWord(BOnewordLine.WORD_CONTINUE);
		if (name != null) {
			line.setLabel(name.toString());
		}
		return line;
	}

	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree tree, Object arg1) {
		log("visitDoWhileLoop:" + tree.toString());
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;
		ExpressionTree ex = tree.getCondition();
		StatementTree st = tree.getStatement();

		BLoopUnit loop = view.createLoop();
		loop.clearCondition();

		loop.setLoopType(BLoopUnit.TYPE_DOWHILE);
		Object exobj = ex.accept(this, loop);
		st.accept(this, loop.getEditor());

		loop.addCondition((BValuable) exobj);
		this.currentVariable = blockVariablePath;
		return loop;

	}

	@Override
	public Object visitEmptyStatement(EmptyStatementTree tree, Object arg1) {
		log("visitEmptyStatement:" + tree.toString());
		makePath(tree);

		return view.createEmpty();
	}

	@Override
	public Object visitEnhancedForLoop(EnhancedForLoopTree tree, Object parent) {
		log("visitEnhancedForLoop:" + tree.toString());
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;
		// (???:list)
		ExpressionTree expression = tree.getExpression();
		Object exp = expression.accept(this, parent);

		BLoopUnit loop = this.view.createLoop();
		loop.clearCondition();
		loop.setLoopType(BLoopUnit.TYPE_ENHANCED);
		// (Object obj:???)
		VariableTree variable = tree.getVariable();
		BAssignment assign = (BAssignment) variable.accept(this, parent);

		// loop body
		StatementTree statement = tree.getStatement();
		statement.accept(this, loop.getEditor());

		loop.addEnhancedCondition(assign.getLeft(), (BValuable) exp);

		this.currentVariable = blockVariablePath;
		return loop;
	}

	@Override
	public Object visitErroneous(ErroneousTree tree, Object parent) {
		log("visitErroneous:" + tree.toString());
		makePath(tree);

		// TODO
		List<? extends Tree> list = tree.getErrorTrees();
		for (Tree t : list) {
			t.accept(this, null);
		}

		return null;
	}

	@Override
	public Object visitInstanceOf(InstanceOfTree tree, Object parent) {
		log("visitInstanceOf :" + tree.toString());
		makePath(tree);

		ExpressionTree ex = tree.getExpression();
		Tree type = tree.getType();

		BValuable expression = (BValuable) ex.accept(this, parent);
		BVariable var = (BVariable) type.accept(this, parent);

		BExpression bex = view.createExpression();
		bex.setExLeft(expression);

		if (var == null) {
			Debug.a();
		}

		bex.setExRight(var);
		bex.setExMiddle(BLogiker.INSTANCEOF);

		return bex;
	}

	@Override
	public Object visitForLoop(ForLoopTree tree, Object parent) {
		log("visitForLoop:" + tree.toString());
		// for(int i=0;i<5;i++)
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;

		BLoopUnit loop = this.view.createLoop();
		loop.clearCondition();
		loop.setLoopType(BLoopUnit.TYPE_FORLOOP);

		List<? extends StatementTree> sta = tree.getInitializer();
		List<BAssign> assigns = new ArrayList<BAssign>();
		for (StatementTree stree : sta) {
			Object a = stree.accept(this, parent);
			BAssign assign = (BAssign) a;
			if (a instanceof BAssignment) {
				BAssignment s = (BAssignment) a;
				BObject obj = s.getLeft();
				if (obj instanceof BVariable) {
					BVariable var = (BVariable) obj;
					if (var.getName() != null) {
						var.setName(var.getLogicName());
					}
				}
			}
			assigns.add(assign);
		}
		loop.setForLoopInitializers(assigns);

		List<? extends ExpressionStatementTree> update = tree.getUpdate();
		List<BValuable> updates = new ArrayList<BValuable>();
		for (ExpressionStatementTree expression : update) {
			BValuable obj = (BValuable) expression.accept(this, parent);
			updates.add(obj);
		}
		loop.setUpdats(updates);

		ExpressionTree condition = tree.getCondition();
		// for (; ; )
		if (condition == null) {
			BVariable var = view.createVariable();
			var.setLogicName("true");
			var.setName("無条件");
			// var.setBClass(CodecUtils.);
			loop.addCondition(var);
		} else {
			Object con = condition.accept(this, parent);
			loop.addCondition((BValuable) con);
		}

		// loop body
		StatementTree statement = tree.getStatement();
		statement.accept(this, loop.getEditor());

		this.currentVariable = blockVariablePath;
		return loop;

	}

	@Override
	public Object visitIdentifier(IdentifierTree tree, Object parameters) {
		log("visitIdentifier ->" + tree.getName().toString());
		makePath(tree);

		if (parameters != null && parameters.equals("Annotation")) {
			return this.getType(tree);
		}

		// from exception type search
		if (parameters != null && parameters instanceof BMethod) {
			BParameter para = this.getType(tree);
			if (para != null) {
				return para;
			}
		}

		String name = tree.getName().toString();

		if (name.toString().equals("this")) {
			BVariable variable = view.createVariable();

			variable.makeCaller(this.sheetModel, true);
			return variable;
		} else if (name.toString().equals("super")) {
			BVariable variable = view.createVariable();

			variable.makeCaller(this.sheetModel, true);
			// String superClass = this.sheetModel.getSuperClass();
			// if (superClass == null) {
			// variable.makeCaller(CodecUtils.getClassFromJavaClass(Object.class, project),
			// false);
			// } else {
			// variable.makeCaller(CodecUtils.getClassFromJavaClass(project,
			// this.sheetModel.getSuperClass()), false);
			// }
			variable.setName("親クラス");
			variable.setLogicName("super");
			return variable;
		}

		if (parameters != null && parameters instanceof List) {
			@SuppressWarnings("unchecked")
			List<BValuable> list = (List<BValuable>) parameters;

			BMethod m = CodecUtils.getMethod(this.sheetModel, name, list, project);
			if (m == null) {
				BValuable superClass = sheetModel.getSuperClass();
				if (superClass != null) {
					m = CodecUtils.getClassMethod(superClass.getBClass().getQualifiedName(), name, list, project, true);
					if (m != null) {
						m.setDeclearedParentName("super");
					}
				}
			} else {
				m.setDeclearedParentName("this");
			}
			if (m == null) {
				if (this.parentModel != null) {
					m = CodecUtils.getMethod(parentModel, name, list, project);
					if (m == null) {
						BValuable superClass = parentModel.getSuperClass();
						if (superClass != null) {
							m = CodecUtils.getClassMethod(superClass.getBClass().getQualifiedName(), name, list,
									project, true);
							if (m != null) {
								m.setDeclearedParentName("local_super");
							}
						}
					} else {
						m.setDeclearedParentName("local_parent");
					}
				}
			}

			// the method defined in this class
			if (m != null) {
				return m.cloneAll();
			} else {
				error("fital error occurred : " + this.sheetModel.getQualifiedName() + "->" + name
						+ "はサポートされていません（visitIdentifier）");
				CodecUtils.getMethod(sheetModel, name, list, project);
				return null;
			}
		} else {

			BVariable type = this.getVariable(this.currentVariable, name);
			if (type != null) {

				BVariable obj = (BVariable) type.cloneAll();

				return obj;
			} else {
				return this.getType(tree);
			}
		}

	}

	@Override
	public Object visitImport(ImportTree tree, Object arg1) {
		log("visitImport:" + tree.toString());
		makePath(tree);

		Thread th = Thread.currentThread();
		boolean doChild = true;
		if (th instanceof BeeThread) {
			BeeThread bee = (BeeThread) th;
			Object obj = bee.getUserObject();
			if (obj instanceof ThreadParameter) {
				ThreadParameter p = (ThreadParameter) obj;
				doChild = p.isDoLinkedObject();
			}
		}

		doChild = false;

		if (doChild) {

			Tree t = tree.getQualifiedIdentifier();

			if (gen.getDecodedBClass(t.toString()) != null) {
				return null;
			}

			String path = project.getSourcePath() + File.separator + (t.toString().replace('.', File.separatorChar))
					+ ".java";

			File f = new File(path);
			if (f.exists()) {
				return gen.execute(path, model, project);
			}
		}

		return null;
	}

	@Override
	public Object visitIntersectionType(IntersectionTypeTree tree, Object parent) {
		error("visitIntersectionType:" + tree.toString());
		makePath(tree);

		// TODO lambda
		List<? extends Tree> list = tree.getBounds();
		for (Tree t : list) {
			t.accept(this, null);
		}

		return null;
	}

	@Override
	public Object visitLabeledStatement(LabeledStatementTree tree, Object parent) {
		log("visitLabeledStatement:" + tree.toString());
		makePath(tree);

		Name label = tree.getLabel();
		String slabel = label.toString();
		StatementTree state = tree.getStatement();
		BLogicUnit unit = (BLogicUnit) state.accept(this, parent);
		unit.setLabel(slabel);

		return unit;
	}

	@Override
	public Object visitLambdaExpression(LambdaExpressionTree tree, Object arg1) {
		log("visitLambdaExpression:" + tree.toString());
		makePath(tree);

		BLambda lab = view.createLambda();

		Tree body = tree.getBody();
		body.accept(this, lab.getLogicBody());
		BodyKind kind = tree.getBodyKind();

		List<? extends VariableTree> parameters = tree.getParameters();
		if (parameters != null) {
			for (VariableTree v : parameters) {
				Tree type = v.getType();
				Name name = v.getName();
				BParameter var = null;
				if (type != null) {
					var = (BParameter) type.accept(this, null);
				} else {
					var = view.createParameter();
					var.setBClass(CodecUtils.BString());
				}
				var.setLogicName(name.toString());
				lab.addParameter(var);
			}
		}

		return lab;
	}

	@Override
	public Object visitLiteral(LiteralTree tree, Object arg1) {
		log("visitLiteral:" + tree.toString());
		makePath(tree);

		Object obj = tree.getValue();

		Kind kind = tree.getKind();
		if (obj != null) {
			BVariable variable = view.createVariable();
			String value = obj.toString();

			if (kind.equals(Kind.STRING_LITERAL)) {
				value = "\"" + value + "\"";
			} else if (kind.equals(Kind.CHAR_LITERAL)) {
				value = "'" + value + "'";
			} else if (kind.equals(Kind.LONG_LITERAL)) {
				value = value + "L";
			} else if (kind.equals(Kind.FLOAT_LITERAL)) {
				value = value + "F";
			} else if (kind.equals(Kind.DOUBLE_LITERAL)) {
				value = value + "D";
			} else if (kind.equals(Kind.INT_LITERAL)) {

			}
			variable.setBClass(CodecUtils.getClassFromJavaClass(tree.getValue().getClass(), project));
			variable.setLogicName(value);
			variable.setName(value);
			return variable;
		} else {
			if (kind.equals(Kind.NULL_LITERAL)) {
				return CodecUtils.getNullValue();
			} else {
				return null;
			}
		}

	}

	@Override
	public Object visitMemberReference(MemberReferenceTree tree, Object parent) {
		error("visitMemberReference(enum):" + tree.toString());
		makePath(tree);

		// TODO enum
		ReferenceMode mode = tree.getMode();
		mode.getDeclaringClass();
		Name name = tree.getName();
		log(name.toString());

		ExpressionTree ex = tree.getQualifierExpression();
		ex.accept(this, null);
		List<? extends ExpressionTree> aa = tree.getTypeArguments();
		for (ExpressionTree t : aa) {
			t.accept(this, null);
		}

		return null;
	}

	@Override
	public Object visitMethodInvocation(MethodInvocationTree tree, Object box) {
		log("visitMethodInvocation:" + tree.toString());
		makePath(tree);

		List<? extends ExpressionTree> parameter = tree.getArguments();
		List<BValuable> parameters = new ArrayList<BValuable>();
		if (parameter != null) {
			for (ExpressionTree exp : parameter) {

				Object obj = exp.accept(this, null);
				BValuable sub = (BValuable) obj;
				parameters.add(sub);
			}
		}
		Object target = null;
		String fullName = tree.toString();
		if (fullName.startsWith("this(")) {
			target = view.createMethodInvoker();
			BMethod m = view.createMethod();
			m.setLogicName("this");
			m.setName("インスタンス作成");
			BConstructor c = CodecUtils.getClassConstructor(this.sheetModel.getQualifiedName(), parameters, project,
					true);
			if (c == null) {
				Debug.d("");
			}
			List<BParameter> paras = c.getParameter();
			for (BParameter var : paras) {
				m.addParameter(var);
			}
			((BInvoker) target).setInvokeChild(m);
			((BInvoker) target).setInvokeParent(null);
			for (BValuable para : parameters) {
				((BInvoker) target).addParameter(para);
			}

		} else if (fullName.startsWith("super(")) {
			target = view.createMethodInvoker();
			BMethod m = view.createMethod();
			m.setLogicName("super");
			m.setName("親クラスの初期化");
			if (this.sheetModel.getSuperClass() != null) {
				BConstructor c = CodecUtils.getClassConstructor(
						this.sheetModel.getSuperClass().getBClass().getQualifiedName(), parameters, project, true);
				if (c == null) {
					Debug.d();
				}
				List<BParameter> paras = c.getParameter();
				for (BParameter var : paras) {
					m.addParameter(var);
				}
			}
			((BInvoker) target).setInvokeChild(m);
			((BInvoker) target).setInvokeParent(null);
			for (BValuable para : parameters) {
				((BInvoker) target).addParameter(para);
			}
		} else {

			ExpressionTree ex = tree.getMethodSelect();

			target = ex.accept(this, parameters);

			if (target instanceof BInvoker) {
				BInvoker node = (BInvoker) target;
				Object first = node.getInvokeParent();
				if (first instanceof BVariable) {
					BVariable cn = (BVariable) first;
					if (cn.isClass()) {
						node.setStatic(true);
					}
					BClass bclass = cn.getBClass();
					if (bclass != null) {
						if (bclass.isData()) {
							node.setData(true, parameter != null && !parameter.isEmpty());
						} else {
							BMethod child = (BMethod) node.getInvokeChild();

							if (child == null) {
								Debug.d("fital error:" + node.toString() + "はサポートされていません（visitMethodInvocation）");

							}

							// String logicName = child.getLogicName();
							String text = DecodeDocProjects.getDoc(project).getDoc(bclass.getQualifiedName())
									.getMethodDoc(child);
							if (text != null && !text.equals("")) {
								child.setName(text);
							}
						}

					}

				}
				for (BValuable sub : parameters) {
					node.addParameter(sub);
				}
			} else if (target instanceof BMethod) {
				BMethod sm = (BMethod) target;
				BInvoker refer = view.createMethodInvoker();
				refer.setInvokeChild(sm);
				BVariable parent = view.createVariable();
				parent.makeCaller(this.sheetModel, true);
				// if (sm.getDeclearedParentName() != null &&
				// sm.getDeclearedParentName().startsWith("local")) {
				refer.setInnerClassCall(true);
				// }
				refer.setInvokeParent(parent);
				for (BValuable sub : parameters) {
					refer.addParameter(sub);
				}

				target = refer;
			}

		}

		// make parameter
		if (target instanceof BInvoker) {
			BInvoker refer = (BInvoker) target;
			List<? extends Tree> parameterTypes = tree.getTypeArguments();

			for (Tree pt : parameterTypes) {
				BValuable value = this.getType(pt);
				refer.addParameter(value);
			}
		}

		return target;
	}

	@Override
	public Object visitModifiers(ModifiersTree tree, Object parent) {
		log("visitModifiers:" + tree.toString());

		makePath(tree);

		List<? extends AnnotationTree> anos = tree.getAnnotations();
		for (AnnotationTree t : anos) {
			BAnnotable bobj = (BAnnotable) parent;
			if (!t.toString().equals("@Override()")) {
				BAnnotation anno = (BAnnotation) t.accept(this, null);

				bobj.addAnnotation(anno);
			} else {
				BAnnotation anno = view.createAnnotaion();
				anno.setLogicName("Override");
				anno.setBClass(CodecUtils.getClassFromJavaClass(Override.class, project));
				bobj.addAnnotation(anno);

			}
		}

		int mds = 0;
		Set<Modifier> mods = tree.getFlags();
		Iterator<Modifier> ite = mods.iterator();
		while (ite.hasNext()) {
			Modifier m = ite.next();
			int type = BMod.getType(m.name().toLowerCase());
			mds = mds | type;
		}

		return mds;
	}

	@Override
	public Object visitNewArray(NewArrayTree tree, Object parent) {
		log("visitNewArray:" + tree.toString());
		makePath(tree);

		List<? extends ExpressionTree> dim = tree.getDimensions();
		List<? extends ExpressionTree> inits = tree.getInitializers();
		Tree type = tree.getType();

		BVariable var = view.createVariable();

		BVariable bv = null;
		if (type != null) {
			Object otype = type.accept(this, parent);
			bv = (BVariable) otype;
		}
		if (type == null && parent != null && parent instanceof BVariable) {
			bv = (BVariable) ((BVariable) parent).cloneAll();
			var.setArrayTitle(false);
		}

		if (bv == null) {
			Debug.d("fital error ");
		}

		BClass bclass = temp.createClass();
		bclass.setArrayPressentClass(bv.getBClass());
		bclass.setPackage(bv.getBClass().getPackage());
		bclass.setLogicName(bv.getBClass().getLogicName());
		bclass.setName(bv.getBClass().getName() + "の配列");

		var.setBClass(bclass);
		var.setLogicName(bclass.getLogicName());
		var.setName(bv.getBClass().getName());

		var.setNewClass(true);

		for (ExpressionTree ex : dim) {
			Object obj = ex.accept(this, parent);
			BValuable v = (BValuable) obj;

			var.addArrayDimension(v);
		}
		if (inits != null) {
			for (ExpressionTree ex : inits) {
				Object obj = ex.accept(this, parent);
				var.addInitValue((BValuable) obj);
			}
		}

		return var;
	}

	@Override
	public Object visitNewClass(NewClassTree tree, Object box) {
		log("visitNewClass:" + tree.toString());
		makePath(tree);

		BInvoker invoker = view.createMethodInvoker();

		ExpressionTree iden = tree.getIdentifier();
		BVariable v = (BVariable) this.getType(iden);
		// for efficiency sake
		// BVariable v = (BVariable) iden.accept(this, box);
		v.setNewClass(true);
		invoker.setInvokeParent(v);

		List<? extends ExpressionTree> parameters = tree.getArguments();
		List<BValuable> stringPara = new ArrayList<BValuable>();
		if (parameters != null) {
			for (ExpressionTree et : parameters) {
				BValuable para = (BValuable) et.accept(this, null);
				stringPara.add(para);
			}
		}

		BConstructor con = CodecUtils.getClassConstructor(v.getBClass().getQualifiedName(), stringPara, project, true);
		if (con == null) {
			// default constructor( not decleared)
			con = view.createConstructor();
		}

		BVariable returnValue = (BVariable) v.cloneAll();
		returnValue.setNewClass(false);
		con.setReturn(returnValue);

		if (tree.getClassBody() != null) {
			// inner class

			BasicSourceDecoder ds = new BasicSourceDecoder(gen, project, map, this.trees, sourceInfo, model);
			BeeModel inner = new BeeModel();
			inner.setPackage(sheetModel.getPackage());
			inner.setLogicName(sheetModel.getQualifiedName() + "$anonymous");
			inner.setName("anonymous(" + v.getBClass().getLogicName() + ")");
			inner.setInnerParentClassName(this.sheetModel.getQualifiedName());
			inner.setSuperClass((BValuable) v.cloneAll());
			// make it to be tree with parent?
			ds.setPath(this.path);

			ds.currentVariable = this.currentVariable;
			ds.setSheetModel(inner);
			ds.setAnonymous(true);

			tree.getClassBody().accept(ds, this.sheetModel);
			BClass bclass = ds.getBeeModel();
			// con.getReturn().addParameterizedType(ds.sheetModel.getp);
			con.setBody(bclass);
		}

		invoker.setInvokeChild(con);

		for (BValuable para : stringPara) {
			invoker.addParameter(para);
		}

		// TODO what is this?
		ExpressionTree ex = tree.getEnclosingExpression();
		if (ex != null) {
			JOptionPane.showConfirmDialog(null, "Error occurred!please contact tool technolegical the supporter");
			Object obj = ex.accept(this, parameters);
			Debug.d();
		}

		return invoker;
	}

	@Override
	public Object visitOther(Tree tree, Object parent) {
		error("visitOther:" + tree.toString());
		makePath(tree);

		Application.TODO("visitOther");

		return null;
	}

	@Override
	public Object visitParameterizedType(ParameterizedTypeTree tree, Object parent) {
		log("visitParameterizedType :" + tree.toString());
		makePath(tree);

		List<? extends Tree> args = tree.getTypeArguments();

		Tree type = tree.getType();
		BParameter var = this.getType(type);

		if (var == null) {
			// Map.Entry
			var = (BParameter) type.accept(this, null);

		}
		if (var == null) {
			Debug.d();
		}

		BType btyp = var.getParameterizedTypeValue();
		if (btyp != null) {
			btyp.getParameterizedTypes().clear();
		}

		// List<String,Map<String,String>> t
		// var logicname=t,type=list,para1=string,para2=map

		BTypeImpl paratype = new BTypeImpl();
		paratype.setContainer();
		for (Tree t : args) {
			// Object obj = t.accept(this, parent);
			BParameter typeobj = this.getType(t);
			BType paras = new BTypeImpl();

			if (typeobj == null) {
				// variable type
				paras.setLogicName(t.toString());
				((BTypeImpl) paras).setTypeVariable();
				paras.setName(t.toString());
				paras.setParameterValue(true);
			} else {

				BType bt = typeobj.getParameterizedTypeValue();

				if (bt == null) {
					paras = typeobj.getBClass();
					paras.setParameterValue(true);
					if (typeobj.isWildCard()) {
						((BClassImpl) paras).setWild();
					}
				} else {

					if (!bt.isTypeVariable()) {
						paras = typeobj.getBClass();
					} else {
						paras.setLogicName(t.toString());
						paras.setName(t.toString());
					}
					paras.setParameterValue(true);
					bt.setParameterValue(true);
					paras.addParameterizedType(bt);
				}

			}
			paratype.addParameterizedType(paras);
		}

		var.setParameterizedTypeValue(paratype);

		return var;
	}

	@Override
	public Object visitParenthesized(ParenthesizedTree tree, Object box) {
		log("visitParenthesized:" + tree.toString());
		makePath(tree);

		Object obj = tree.getExpression().accept(this, box);
		if (obj instanceof BExpression) {
			BExpression ex = (BExpression) obj;
			ex.setParenthesized(true);
		}

		return obj;
	}

	@Override
	public Object visitPrimitiveType(PrimitiveTypeTree tree, Object arg1) {
		log("visitPrimitiveType:" + tree.toString());
		makePath(tree);

		BVariable variable = view.createParameter();

		TypeKind type = tree.getPrimitiveTypeKind();
		variable.setLogicName(type.name());
		BClass bclass = CodecUtils.getClassFromJavaClass(project, tree.getPrimitiveTypeKind().name().toLowerCase());
		variable.setName(bclass.getName());
		variable.setBClass(bclass);
		return variable;
	}

	@Override
	public Object visitReturn(ReturnTree tree, Object arg1) {
		log("visitReturn:" + tree.toString());
		makePath(tree);

		BReturnUnit unit = view.createMethodReturn();

		ExpressionTree ex = tree.getExpression();
		if (ex != null) {
			Object obj = ex.accept(this, null);
			unit.setRuturnValue((BValuable) obj);
		} else {
			unit.setRuturnNullValue();
		}
		return unit;
	}

	@Override
	public Object visitSwitch(SwitchTree tree, Object parent) {
		log("visitSwitch:" + tree.toString());
		makePath(tree);

		BSwitchUnit swit = view.createSwitch();

		ExpressionTree ex = tree.getExpression();
		BValuable var = (BValuable) ex.accept(this, parent);
		swit.setVariable(var);

		List<? extends CaseTree> cases = tree.getCases();

		for (CaseTree ca : cases) {
			ca.accept(this, swit);
		}

		return swit;
	}

	@Override
	public Object visitSynchronized(SynchronizedTree tree, Object parent) {
		log("visitSynchronized:" + tree.toString());
		makePath(tree);

		ExpressionTree ex = tree.getExpression();
		BValuable obj = (BValuable) ex.accept(this, parent);

		BlockTree block = tree.getBlock();

		BModifiedBlock bblock = view.createModifiedBlock();
		bblock.setVariable(obj);
		bblock.setMods(java.lang.reflect.Modifier.SYNCHRONIZED);
		return block.accept(this, bblock);

	}

	@Override
	public Object visitThrow(ThrowTree tree, Object box) {
		log("visitThrow:" + tree.toString());
		makePath(tree);

		BThrow thro = view.createThrow();
		BValuable obj = (BValuable) tree.getExpression().accept(this, box);
		thro.setExcetion(obj);

		return thro;
	}

	@Override
	public Object visitTry(TryTree tree, Object parent) {
		log("visitTry:" + tree.toString());
		makePath(tree);

		List<? extends Tree> reources = tree.getResources();

		BTryUnit unit = view.createTry();
		BObject blockVariablePath = this.currentVariable;

		// ExceptionCatchNode node = new ExceptionCatchNode();
		BlockTree tryBlock = tree.getBlock();
		for (Tree t : reources) {
			t.accept(this, unit.getTryEditor());
		}
		tryBlock.accept(this, unit.getTryEditor());

		List<? extends CatchTree> cathes = tree.getCatches();
		unit.clearCatches();
		if (cathes != null && !cathes.isEmpty()) {
			for (CatchTree ct : cathes) {
				BCatchUnit catchnode = (BCatchUnit) ct.accept(this, unit);
				unit.addCatch(catchnode);
			}
		}

		BlockTree finalBlock = tree.getFinallyBlock();
		if (finalBlock != null) {
			unit.addFinalEditor();
			finalBlock.accept(this, unit.getFinalEditor());
		} else {
			unit.delteFinalEditor();
		}

		this.currentVariable = blockVariablePath;
		return unit;
	}

	@Override
	public Object visitTypeParameter(TypeParameterTree tree, Object parent) {
		log("visitTypeParameter:" + tree.toString());
		makePath(tree);

		BTypeImpl type = new BTypeImpl();
		type.setTypeVariable();
		type.setParameterValue(true);

		List<? extends AnnotationTree> annos = tree.getAnnotations();
		for (AnnotationTree t : annos) {

			BAnnotable bobj = (BAnnotable) parent;
			if (!t.toString().equals("@Override()")) {
				BAnnotation anno = (BAnnotation) t.accept(this, null);
				bobj.addAnnotation(anno);
			} else {
				BAnnotation anno = view.createAnnotaion();
				anno.setLogicName("Override");
				anno.setBClass(CodecUtils.getClassFromJavaClass(Override.class, project));
				bobj.addAnnotation(anno);
			}
		}
		List<? extends Tree> bounds = tree.getBounds();
		for (Tree t : bounds) {
			BValuable value = (BValuable) t.accept(this, null);
			type.addBound(value.getBClass().getQualifiedName());
		}
		Name name = tree.getName();
		type.setLogicName(name.toString());

		// for method parameter
		if (parent != null && parent instanceof Boolean) {
			Boolean b = (Boolean) parent;
			if (!b.booleanValue()) {
				return type;
			}
		}
		type.setOwener(currentType);
		this.currentType = type;

		return type;

	}

	@Override
	public Object visitUnary(UnaryTree tree, Object box) {
		log("visitUnary:" + tree.toString());
		makePath(tree);

		Kind kind = tree.getKind();
		ExpressionTree t = tree.getExpression();

		Object obj = t.accept(this, box);

		if (kind.equals(Kind.POSTFIX_INCREMENT)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.INCREMENT);
			exp.setVariable((BVariable) obj);
			return exp;
		} else if (kind.equals(Kind.POSTFIX_DECREMENT)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.DECREMENT);
			exp.setVariable((BVariable) obj);
			return exp;
		} else if (kind.equals(Kind.PREFIX_INCREMENT)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.INCREMENT_BEFORE);
			exp.setVariable((BVariable) obj);
			return exp;
		} else if (kind.equals(Kind.PREFIX_DECREMENT)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.DECREMENT_BEFORE);
			exp.setVariable((BVariable) obj);
			return exp;
		} else if (kind.equals(Kind.LOGICAL_COMPLEMENT)) {
			BExpression exp = view.createExpression();
			exp.setExMiddle(BLogiker.NOT);
			exp.setExLeft((BValuable) obj);
			return exp;
		} else if (kind.equals(Kind.BITWISE_COMPLEMENT)) {
			BExpression exp = view.createExpression();
			exp.setExMiddle(BLogiker.COMPLEMENT);
			exp.setExLeft((BValuable) obj);
			return exp;
		} else if (kind.equals(Kind.UNARY_MINUS)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.UNARY_MINUS);
			exp.setVariable((BValuable) obj);
			return exp;

		} else if (kind.equals(Kind.UNARY_PLUS)) {
			BSingleExpressionUnit exp = view.createSingleExpression();
			exp.setOperator(BSingleExpressionUnit.UNARY_PLUS);
			exp.setVariable((BValuable) obj);
			return exp;
		} else {
			Debug.d();
		}
		return obj;
	}

	@Override
	public Object visitUnionType(UnionTypeTree tree, Object box) {
		log("visitUnionType:" + tree.toString());
		makePath(tree);

		List<? extends Tree> list = tree.getTypeAlternatives();

		BVariable type = view.createParameter();
		List<Class<?>> clss = new ArrayList<Class<?>>();

		for (Tree t : list) {
			BVariable var = this.getType(t);
			type.addUnionType(var);
			clss.add(CodecUtils.getClassByName(var.getBClass().getQualifiedName(), project));
		}

		Class<?> common = CodecUtils.getAllParent(clss);
		type.setBClass(CodecUtils.getClassFromJavaClass(common, project));

		return type;
	}

	@Override
	public Object visitWhileLoop(WhileLoopTree tree, Object arg1) {
		log("visitWhileLoop:" + tree.toString());
		makePath(tree);
		BObject blockVariablePath = this.currentVariable;

		ExpressionTree ex = tree.getCondition();
		StatementTree st = tree.getStatement();

		BLoopUnit loop = view.createLoop();
		loop.clearCondition();
		loop.setLoopType(BLoopUnit.TYPE_WHILE);
		Object exobj = ex.accept(this, loop);
		st.accept(this, loop.getEditor());

		loop.addCondition((BValuable) exobj);

		this.currentVariable = blockVariablePath;

		return loop;
	}

	@Override
	public Object visitWildcard(WildcardTree tree, Object parent) {
		log("visitWildcard:" + tree.toString());
		makePath(tree);

		BClass bclass = CodecUtils.getClassFromJavaClass(Object.class, project);
		Tree bounds = tree.getBound();
		if (bounds != null) {
			BValuable obj = (BValuable) bounds.accept(this, null);
			bclass.addBound(obj.getBClass().getQualifiedName());
		}
		BVariable var = view.createParameter();
		var.setWildCard();
		var.setBClass(bclass);
		var.setClass(true);
		var.setLogicName(tree.toString());

		return var;
	}

	public void makePath(Tree tree) {
		if (path == null) {
			path = new TreePath(unitTree);
		} else {
			path = new TreePath(path, tree);
		}
		// Thread.currentThread().getState();

	}

	public String readComment(Tree tree) {
		SourcePositions sourcePositions = trees.getSourcePositions();
		long pos = sourcePositions.getStartPosition(this.unitTree, tree);
		long lineNumber = map.getLineNumber(pos);
		boolean read = true;
		String result = null;
		int line = 0;
		while (read) {
			String comment = this.sourceInfo.getLine((int) lineNumber - line).trim();
			line++;
			if (comment.startsWith("//")) {
				if (result == null) {
					result = comment.substring(2, comment.length());
				} else {
					result = result + "<br/>" + comment.substring(2, comment.length());
				}
			} else {
				int index = comment.indexOf("//");
				if (index > 0) {
					if (comment.substring(0, index).indexOf(";") >= 0) {
						if (result == null) {
							result = comment.substring(index + 2, comment.length());
						} else {
							result = result + "<br/>" + comment.substring(index + 2, comment.length());
						}
					}
				}
				read = false;

			}
		}
		return result;
	}

	public void setPath(TreePath path) {
		this.path = path;
	}

	public BParameter getInnerClassType(String name) {
		if (name == null) {
			return null;
		}
		if (name.indexOf(".") < 0) {
			if (this.sheetModel.getLogicName() != null) {
				if (this.sheetModel.getLogicName().endsWith("$" + name)) {
					Class<?> cls = CodecUtils.getClassByName(this.sheetModel.getQualifiedName(), project);

					if (cls != null) {
						return this.getClassVariable(cls);
					}
				}
			}
			name = this.sheetModel.getQualifiedName() + "$" + name;
			// return null;
		}
		if (name.startsWith("java.") || name.startsWith("com.") || name.startsWith("org.")) {
			Class<?> cls = CodecUtils.getClassByName(name, project);
			if (cls != null) {
				return this.getClassVariable(cls);
			}
		}

		String sname = name.substring(0, name.indexOf("."));

		Class<?> cls = CodecUtils.getType(sname, this.sheetModel, project);

		sname = name.substring(name.indexOf(".") + 1);
		while (sname.indexOf(".") > 0) {
			name = sname.substring(0, sname.indexOf("."));
			if (cls != null) {
				cls = CodecUtils.getInnerClass(name, cls, project);
			}
			sname = sname.substring(sname.indexOf(".") + 1);
		}
		if (cls != null) {
			cls = CodecUtils.getInnerClass(sname, cls, project);
		}
		if (cls != null) {
			return this.getClassVariable(cls);
		}
		return null;

	}

	public BVariable getVariable(BObject variable, String name) {
		log("getVariable:" + name);

		if (variable != null) {
			if (variable instanceof BVariable) {
				BVariable v = (BVariable) variable;
				if (v.getLogicName().equals(name)) {
					BVariable clone = (BVariable) variable.cloneAll();
					clone.setCaller(true);
					clone.setClass(false);

					return clone;
				}
			}
		}

		BVariable obj = this.getVariableFromPath(null, name);
		if (obj != null) {

			return obj;
		}

		BValuable superClass = sheetModel.getSuperClass();
		if (superClass != null) {
			obj = CodecUtils.getReachableVariable(superClass.getBClass().getQualifiedName(), name, project);
			if (obj != null) {
				return obj;
			}
			Class<?> scls = CodecUtils.getClassByName(superClass.getBClass().getQualifiedName(), project);
			if (scls != null) {
				obj = CodecUtils.getReachableVariable(superClass.getBClass().getQualifiedName(), name, project);
				if (obj != null) {
					return obj;
				}
			}
		}

		if (this.parentModel != null) {
			obj = CodecUtils.getReachableStaticVariable(parentModel.getQualifiedName(), name, project);
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	protected BParameter getClassVariable(Class<?> cls) {
		// BClass bcls = CodecUtils.getClassFromJavaClass(cls, project);

		Thread t = Thread.currentThread();
		boolean doChild = false;
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			Object obj = bee.getUserObject();
			if (obj instanceof ThreadParameter) {
				ThreadParameter p = (ThreadParameter) obj;
				doChild = p.isDoLinkedObject();
			}
		}
		if (doChild) {
			// for the generation of linked class
			if (cls.getClassLoader() instanceof BeeClassLoader) {

				// it is not self
				if (!cls.getName().equals(sheetModel.getQualifiedName())) {
					BClass bclass = gen.getDecodedBClass(cls.getName());
					if (bclass == null) {
						gen.decodeByName(cls.getName(), model, project);
					}
				}
			}
		}

		BClass bcls = CodecUtils.getClassFromJavaClass(cls, project);
		BParameter var = view.createParameter();
		var.setBClass(bcls);
		var.setClass(true);
		var.setLogicName(bcls.getLogicName());
		var.setName(bcls.getName());
		return var;
	}

	protected BVariable getVariableFromPath(BObject v, String logicName) {

		if (v == null) {
			v = this.currentVariable;
		}
		if (v == null) {
			return null;
		}

		BObject obj = v.getOwener();
		if (obj != null) {
			if (obj instanceof BVariable) {

				BVariable bv = (BVariable) obj;
				if (bv.getLogicName() == null) {
					Debug.d();
				}
				if (bv.getLogicName().equals(logicName)) {
					BVariable clone = (BVariable) bv.cloneAll();
					clone.setCaller(true);
					clone.setClass(false);
					return clone;
				} else {
					return getVariableFromPath(bv, logicName);
				}
			} else {
				return getVariableFromPath(obj, logicName);
			}
		}

		return null;
	}

	protected BParameter getTypeFromPath(BType v, String logicName) {

		if (v == null) {
			v = this.currentType;
		}
		if (v == null) {
			return null;
		}

		if (v.getLogicName().equals(logicName)) {
			BParameter var = PatternCreatorFactory.createView().createParameter();
			BClass bclass = CodecUtils.getClassFromJavaClass(Object.class, project);
			var.setBClass(bclass);
			BTypeImpl type = (BTypeImpl) v.cloneAll();
			type.setTypeVariable();
			type.setParameterValue(true);
			var.setParameterizedTypeValue(type);
			var.setLogicName(logicName);
			var.setName(logicName);
			var.setClass(true);
			return var;

		} else {
			BObject obj = v.getOwener();
			if (obj != null) {
				if (obj instanceof BType) {
					BType bv = (BType) obj;
					return getTypeFromPath(bv, logicName);
				}
			}
		}

		return null;
	}

	public BParameter getType(Tree tree) {
		BParameter type = null;
		if (tree instanceof ParameterizedTypeTree || tree instanceof ArrayTypeTree || tree instanceof UnionTypeTree
				|| tree instanceof AnnotatedTypeTree || tree instanceof WildcardTree) {
			type = (BParameter) tree.accept(this, null);
		} else {

			type = this.getInnerClassType(tree.toString());
			if (type == null) {
				Class<?> cls = found.get(tree.toString());
				if (cls == null) {
					cls = CodecUtils.getType(tree.toString(), sheetModel, project);
					if (cls != null) {
						found.put(tree.toString(), cls);
						return this.getClassVariable(cls);
					}
				} else {
					return this.getClassVariable(cls);
				}
				return this.getTypeFromPath(this.currentType, tree.toString());
			} else {

			}
		}
		return type;
	}

	Hashtable<String, Class<?>> found = new Hashtable<String, Class<?>>();

	protected void log(String s) {
		P.check(null);
		System.out.println(s);
	}

	protected void error(String s) {
		Debug.d(s);
	}

	@Override
	public BClass getBeeModel() {
		return this.sheetModel;
	}

	@Override
	public BookModel getBookModel() {
		return this.model;
	}

	public void setSheetModel(BeeModel model) {
		this.sheetModel = model;
	}

	public List<Object> getCallers() {
		return this.callers;
	}

	protected BLogiker getLogiker(Kind kind) {
		if (kind.equals(Kind.AND_ASSIGNMENT)) {
			return BLogiker.BITAND;
		} else if (kind.equals(Kind.DIVIDE_ASSIGNMENT)) {
			return BLogiker.DIVIDE;
		} else if (kind.equals(Kind.MINUS_ASSIGNMENT)) {
			return BLogiker.MINUS;
		} else if (kind.equals(Kind.MULTIPLY_ASSIGNMENT)) {
			return BLogiker.MULTIPLY;
		} else if (kind.equals(Kind.PLUS_ASSIGNMENT)) {
			return BLogiker.PLUS;
		} else if (kind.equals(Kind.OR_ASSIGNMENT)) {
			return BLogiker.BITOR;
		} else if (kind.equals(Kind.XOR_ASSIGNMENT)) {
			return BLogiker.XOR;
		} else if (kind.equals(Kind.XOR_ASSIGNMENT)) {
			return BLogiker.XOR;
		} else if (kind.equals(Kind.BITWISE_COMPLEMENT)) {
			return BLogiker.COMPLEMENT;
		} else if (kind.equals(Kind.RIGHT_SHIFT_ASSIGNMENT)) {
			return BLogiker.SHEFTRIGHT;
		} else if (kind.equals(Kind.REMAINDER_ASSIGNMENT)) {
			return BLogiker.MOD;
		} else if (kind.equals(Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT)) {
			return BLogiker.SHEFTRIGHTPLUS;
		} else if (kind.equals(Kind.ASSIGNMENT)) {
			return null;
		}
		return null;
	}

	protected String getClassTitle(ClassTree tree) {
		String title = null;
		DocCommentTree comments = this.trees.getDocCommentTree(path);

		if (comments != null) {
			Object docs = comments.accept(docTree, null);
			if (docs instanceof Hashtable) {
				@SuppressWarnings("unchecked")
				Hashtable<String, String> hash = (Hashtable<String, String>) docs;
				title = hash.get("title");
			}
		} else {
			title = tree.getSimpleName().toString();
		}

		if (title != null) {
			title = title.trim();
			if (title.endsWith("\n")) {
				title = title.substring(0, title.length() - 1);
			}
		} else {
			title = tree.getSimpleName().toString();
		}
		return title;
	}

	protected void makeCommon(ClassTree tree) {
		// super class
		Tree superClass = tree.getExtendsClause();
		if (superClass != null) {
			BValuable superObj = this.getType(superClass);
			if (superObj == null) {
				Debug.d();
			}
			sheetModel.setSuperClass(superObj);
		}

		// interfaces
		List<? extends Tree> inters = tree.getImplementsClause();
		if (inters != null) {
			for (Tree inter : inters) {
				BValuable interObj = this.getType(inter);
				sheetModel.addInterface(interObj);
			}
		}
		// parameters
		List<? extends TypeParameterTree> parameters = tree.getTypeParameters();
		for (TypeParameterTree type : parameters) {
			BType obj = (BType) type.accept(this, sheetModel);
			sheetModel.addParameterizedType(obj);
		}

		Kind kind = tree.getKind();
		if (kind.equals(Kind.INTERFACE)) {
			sheetModel.setInterface(true);
		}
	}

	protected void makeImports(JCCompilationUnit unit) {
		List<JCImport> imports = unit.getImports();

		for (JCImport impo : imports) {
			impo.accept(this, null);
			String importName = impo.qualid.toString();

			if (importName.endsWith(".*")) {
				BImport imp = temp.createImport();
				imp.setLogicName(importName);
				sheetModel.getImports().add(imp);
			} else {

				BImport imp = temp.createImport();
				imp.setLogicName(importName);
				sheetModel.getImports().add(imp);

			}

		}
	}

	protected DecodeDoc getDoc() {
		return DecodeDocProjects.getDoc(project).getDoc(sheetModel.getQualifiedName());
	}

}