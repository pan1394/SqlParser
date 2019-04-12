package com.linkstec.bee.core.codec.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotable;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssert;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BEmptyUnit;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
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
import com.sun.codemodel.ClassType;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JArrayCompRef;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCase;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDoLoop;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JLabel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JWhileLoop;

public class JavaGen {
	private List<ClassSet> classes;
	private List<JDefinedClass> jclasses;
	private BProject project;

	public JavaGen(BProject project) {
		this.project = project;
	}

	public void generate(List<BClass> bclasses)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generate:");
		if (bclasses == null) {
			return;
		}
		this.classes = new ArrayList<ClassSet>();
		jclasses = new ArrayList<JDefinedClass>();
		for (BClass bcls : bclasses) {
			if (bcls.getInnerParentClassName() == null) {
				ClassSet set = new ClassSet();
				set.setBclass(bcls);
				classes.add(set);
			}
		}
		for (BClass bcls : bclasses) {
			if (bcls.getInnerParentClassName() != null) {
				for (ClassSet set : classes) {
					if (set.getBclass().getQualifiedName().equals(bcls.getInnerParentClassName())) {
						if (!bcls.isAnonymous()) {
							set.getInners().add(bcls);
						}
					}
				}

			}
		}
		for (ClassSet set : classes) {
			generate(set);
		}
		// error("complete:" + bclasses.toString());
	}

	public void generate(ClassSet set)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		JDefinedClass jclsss = this.generateClass(set);
		if (jclsss != null) {
			jclasses.add(jclsss);
		}
	}

	public JDefinedClass generateClass(ClassSet set)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateClass:");
		BClass bcls = set.getBclass();
		for (JDefinedClass jcls : jclasses) {
			if (jcls.fullName().equals(bcls.getQualifiedName())) {
				return null;
			}
		}

		JDefinedClass defined = null;
		if (bcls.isData()) {
			defined = generateDataClass(set, null, null);
		} else if (bcls.isLogic()) {
			defined = generateLogicClass(set, null, null);
		}

		return defined;

	}

	public JDefinedClass generateDataClass(Object obj, JCodeModel cm, JDefinedClass cls)
			throws JClassAlreadyExistsException, ClassNotFoundException, IOException, BeeClassExistsException {
		log("generateDataClass:");
		BClass bclass = null;
		if (obj instanceof ClassSet) {
			ClassSet set = (ClassSet) obj;
			bclass = set.getBclass();
		} else if (obj instanceof BClass) {
			bclass = (BClass) obj;
		}

		if (cm == null) {
			cm = new JCodeModel();
		}

		if (cls == null) {
			cls = this.generateClassCommonInfo(cm, bclass);
		}
		cls = generateDataClassLogic(bclass, cm, cls);

		if (obj instanceof ClassSet) {
			ClassSet set = (ClassSet) obj;
			this.generateInnerClass(cls, set.getInners(), cm);
			CodeWriter writer = new BeeCodeWriter(cls.name(), cls.getPackage().name(), project);

			cm.build(writer);
		}
		return cls;
	}

	private JDefinedClass generateDataClassLogic(BClass bclass, JCodeModel cm, JDefinedClass cls)
			throws JClassAlreadyExistsException, ClassNotFoundException, BeeClassExistsException, IOException {
		log("generateDataClassLogic");
		List<BAssignment> list = bclass.getVariables();
		for (BAssignment cell : list) {

			BParameter v = cell.getLeft();
			String varName = v.getLogicName();

			JType typeClass = this.getType(cm, v, cls);

			int mods = v.getModifier();
			JFieldVar f;
			BValuable value = cell.getRight();

			f = cls.field(mods, typeClass, varName, this.generateObject(cm, value, cls));

			f.javadoc().add(v.getName().trim());

			this.generateAnnotation(cell, cm, f, cls);

			boolean iffinal = f.mods().isFinal();
			boolean istatic = f.mods().isStatic();

			boolean skip = iffinal || istatic;

			if (!skip) {

				if (!cell.isMethodRestored()) {
					// getter;
					JMethod getter;
					String bname = typeClass.binaryName();
					if (bname.equals("boolean")) {
						getter = cls.method(JMod.PUBLIC, boolean.class, BeeNamingUtil.getGetter(varName));
					} else {
						getter = cls.method(JMod.PUBLIC, typeClass, BeeNamingUtil.getGetter(varName));

					}
					getter.body()._return(JExpr.ref(JExpr._this(), varName));

					// setter
					JMethod setter;
					setter = cls.method(JMod.PUBLIC, cm.VOID, BeeNamingUtil.getSetter(varName));
					JVar para = setter.param(typeClass, varName);
					setter.body().assign(JExpr.ref(JExpr._this(), varName), para);

				}
			}
		}

		// restored constructor
		// made by the first action

		// restored method
		List<BMethod> methods = bclass.getMethods();
		for (BMethod method : methods) {
			this.generateMethod(method, cm, cls);
		}
		Collection<JMethod> ms = cls.methods();
		if (methods.size() > 0) {
			int i = 0;
			for (JMethod m : ms) {
				generateLogicBody(methods.get(i).getLogicBody(), cm, m.body(), cls);
				i++;
			}
		}
		return cls;
	}

	private void generateInnerClass(JDefinedClass parent, List<BClass> inners, JCodeModel cm)
			throws JClassAlreadyExistsException, ClassNotFoundException, IOException, BeeClassExistsException {
		log("generateInnerClass");
		for (BClass bclass : inners) {
			String name = bclass.getQualifiedName();
			name = name.substring(name.indexOf("$") + 1);
			JDefinedClass cls = parent._class(bclass.getModifier(), name, ClassType.CLASS);
			this.makeClassHeader(cls, bclass, cm);
			if (bclass.isData()) {
				generateDataClass(bclass, cm, cls);
			} else if (bclass.isLogic()) {
				generateLogicClass(bclass, cm, cls);
			}
		}
	}

	public JDefinedClass generateLogicClass(Object obj, JCodeModel cm, JDefinedClass cls)
			throws JClassAlreadyExistsException, ClassNotFoundException, IOException, BeeClassExistsException {
		log("generateLogicClass");
		BClass bclass = null;
		if (obj instanceof ClassSet) {
			ClassSet set = (ClassSet) obj;
			bclass = set.getBclass();
		} else if (obj instanceof BClass) {
			bclass = (BClass) obj;
		}

		if (cm == null) {
			cm = new JCodeModel();
		}
		if (cls == null) {
			cls = this.generateClassCommonInfo(cm, bclass);
		}

		this.generateClassBody(bclass, cm, cls);

		if (obj instanceof ClassSet) {

			ClassSet set = (ClassSet) obj;

			this.generateInnerClass(cls, set.getInners(), cm);

			CodeWriter writer = new BeeCodeWriter(cls.name(), cls.getPackage().name(), project);

			cm.build(writer);
		}

		return cls;
	}

	private void generateClassBody(BClass bclass, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateClassBody");
		List<BAssignment> variables = bclass.getVariables();

		for (BAssignment variable : variables) {
			generateClassAssign(variable, cm, cls);
		}
		List<BLogicBody> blocks = bclass.getBlocks();
		for (BLogicBody block : blocks) {

			JBlock bblock;
			if (block instanceof BModifiedBlock) {
				BModifiedBlock bm = (BModifiedBlock) block;
				BValuable var = bm.getVariable();
				if (var == null) {
					bblock = cls.instanceInit();
				} else {
					bblock = cls.init();
				}
			} else {
				bblock = cls.init();
			}

			this.generateLogicBody(block, cm, bblock, cls);
		}
		List<BMethod> methods = bclass.getMethods();
		for (BMethod method : methods) {
			generateMethod(method, cm, cls);
		}
		if (!cls.isInterface()) {

			Collection<JMethod> ms = cls.methods();
			int i = 0;
			for (JMethod m : ms) {

				if (!Modifier.isAbstract(methods.get(i).getModifier())) {
					generateLogicBody(methods.get(i).getLogicBody(), cm, m.body(), cls);
				}
				i++;
			}
		}
	}

	public void generateAnnotation(BObject object, JCodeModel cm, JAnnotatable annotable, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		if (annotable == null) {
			Debug.a();
			// TODO
			return;
		}
		log("generateAnnotation");
		if (object instanceof BAnnotable) {
			BAnnotable annble = (BAnnotable) object;
			List<BAnnotation> annos = annble.getAnnotations();
			if (annos != null) {
				for (BAnnotation anno : annos) {
					if (anno == null) {
						// TODO
						Debug.a();
						return;
					}
					JAnnotationUse use = annotable.annotate(this.getType(cm, anno.getBClass(), cls).boxify());
					this.generateAnnotationUnit(cm, anno, cls, use);
				}
			}
		}
	}

	public void generateAnnotationUnit(JCodeModel cm, BAnnotation anno, JDefinedClass cls, JAnnotationUse use)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		List<BAnnotationParameter> parameter = anno.getParameters();
		for (BAnnotationParameter s : parameter) {
			BValuable v = s.getValue();
			if (v instanceof BAnnotation) {
				BAnnotation a = (BAnnotation) v;

				@SuppressWarnings("unchecked")
				JAnnotationUse u = use.annotationParam(s.getLogicName(), (Class<? extends Annotation>) CodecUtils
						.getClassByName(a.getBClass().getQualifiedName(), project));

				this.generateAnnotationUnit(cm, a, cls, u);
			} else {
				JExpression value = this.generateObject(cm, v, cls);
				use.param(s.getLogicName(), value);
			}
		}
	}

	public JDefinedClass generateClassCommonInfo(JCodeModel cm, BClass bclass)
			throws ClassNotFoundException, JClassAlreadyExistsException, BeeClassExistsException, IOException {

		log("generate Class common info:" + bclass.getLogicName());

		String name = bclass.getLogicName();
		if (name == null) {
			name = CodecUtils.makeClassName(bclass.getLogicName(), bclass.getName());
			bclass.setLogicName(name);
		}
		int m = bclass.getModifier();

		String pack = "";
		if (bclass.getPackage() != null) {
			pack = bclass.getPackage();
		}

		JDefinedClass cls = cm._package(pack)._class(m, name,
				bclass.isInterface() ? ClassType.INTERFACE : ClassType.CLASS);
		List<BType> paras = bclass.getParameterizedTypes();
		for (BType para : paras) {
			List<String> bouds = para.getBounds();

			if (bouds.isEmpty()) {
				cls.generify(para.getLogicName());
			} else {
				List<JClass> bc = new ArrayList<JClass>();
				for (String type : bouds) {
					JType bcl = this.getTypeByName(cm, type, cls);
					if (bcl != null) {
						bc.add(bcl.boxify());
					}
				}
				if (bc.isEmpty()) {
					cls.generify(para.getLogicName());
				} else {
					for (JClass j : bc) {
						cls.generify(para.getLogicName(), j);
					}
				}
			}
		}

		this.generateAnnotation(bclass, cm, cls, cls);

		this.makeClassHeader(cls, bclass, cm);
		return cls;

	}

	private void makeClassHeader(JDefinedClass cls, BClass bclass, JCodeModel cm)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("makeClassHeader");
		if (bclass.getSuperClass() != null) {
			cls._extends(this.getType(cm, bclass.getSuperClass(), cls).boxify());
		}
		List<BValuable> interfaces = bclass.getInterfaces();
		for (BValuable inter : interfaces) {
			cls._implements(this.getType(cm, inter, cls).boxify());
		}

		cls.javadoc().add(bclass.getName().trim());

		List<BConstructor> constructors = bclass.getConstructors();

		if (constructors != null) {
			for (BConstructor con : constructors) {
				generateConstructor(con, cm, cls);
			}
		}
	}

	private void generateConstructor(BConstructor constructor, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateMethod:" + constructor.getLogicName());

		JMethod method = cls.constructor(constructor.getModifier());
		method.javadoc().add(constructor.getName().trim());

		List<BParameter> parameters = constructor.getParameter();
		List<BVariable> thrs = constructor.getThrows();

		if (parameters != null) {
			for (BParameter para : parameters) {

				String paraName = para.getLogicName();
				JVar var = method.param(para.getModifier(), this.getType(cm, para, cls), paraName);
				method.javadoc().addParam(var).append(para.getName());
			}
		}

		if (thrs != null) {
			for (BVariable t : thrs) {
				// BClass bclass = t.getBClass();
				JClass jclas = this.getType(cm, t, cls).boxify();
				method._throws(jclas);
				method.javadoc().addThrows(jclas);// .add(t.getName());
			}
		}
		generateLogicBody(constructor.getLogicBody(), cm, method.body(), cls);
	}

	public JMethod generateMethod(BMethod bmethod, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateMethod:" + bmethod.getLogicName());

		BValuable valuable = bmethod.getReturn();
		List<BVariable> thrs = bmethod.getThrows();

		JType type = this.getType(cm, bmethod.getReturn(), cls);
		if (type == null) {
			type = cm.VOID;
		}

		JMethod method = cls.method(bmethod.getModifier(), type, bmethod.getLogicName());

		List<BType> definedTypes = bmethod.getDefinedTypes();
		for (BType t : definedTypes) {

			List<String> bouds = t.getBounds();

			if (bouds.isEmpty()) {
				method.generify(t.getLogicName());
			} else {
				List<JClass> bc = new ArrayList<JClass>();
				for (String btype : bouds) {
					JType bcl = this.getTypeByName(cm, btype, cls);
					if (bcl != null) {
						bc.add(bcl.boxify());
					}
				}
				if (bc.isEmpty()) {
					method.generify(t.getLogicName());
				} else {
					for (JClass j : bc) {
						method.generify(t.getLogicName(), j);
					}
				}
			}
		}

		this.generateAnnotation(bmethod, cm, method, cls);

		String name = CodecUtils.makeMethodName(method, bmethod.getLogicName());
		bmethod.setLogicName(name);
		method.name(name);
		method.javadoc().add(bmethod.getName().trim());

		List<BParameter> parameters = bmethod.getParameter();
		if (parameters != null) {
			for (BParameter para : parameters) {

				String paraName = para.getLogicName();
				JVar var = method.param(para.getModifier(), this.getType(cm, para, cls), paraName);
				method.javadoc().addParam(var).add(para.getName());
			}
		}
		if (thrs != null) {
			for (BVariable t : thrs) {
				JClass jclas = this.getType(cm, t, cls).boxify();
				method._throws(jclas);
				method.javadoc().addThrows(jclas);// .add(t.getName());
			}
		}
		if (valuable != null && valuable.getBClass() != null
				&& !valuable.getBClass().getLogicName().equals(BClass.VOID)) {
			method.javadoc().addReturn().add(valuable.getBClass().getName());
		}
		return method;
	}

	public void generateLogicBody(BLogicBody body, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateLogicBody child:" + body.getUnits());

		List<BLogicUnit> units = body.getUnits();
		for (BLogicUnit unit : units) {
			this.generateUnit(unit, cm, block, cls);
		}

	}

	public void generateUnit(BLogicUnit unit, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		boolean writeBumber = false;
		boolean staticGen = false;
		if (unit.getAlert() != null && unit.getAlert().equals("GEN")) {
			staticGen = true;
		} else {
			if (unit instanceof BInvoker) {
				BInvoker in = (BInvoker) unit;
				if (in.isLinker()) {
					writeBumber = false;
				}
			}
		}
		if (unit instanceof IUnit && writeBumber) {
			IUnit iunit = (IUnit) unit;
			if (iunit.getNumber() != null) {
				block.directStatement("//" + iunit.getNumber().toString());

			}
		}

		if (unit.getLabel() != null && !(unit instanceof BOnewordLine)) {
			block.label(unit.getLabel());
		}
		if (unit instanceof BEmptyUnit) {
			block.directStatement(";");
		} else if (unit instanceof BNote) {
			BNote note = (BNote) unit;
			this.generateNoteUnit(note, cm, block, cls);
		} else if (unit instanceof BAssignment) {
			BAssignment assin = (BAssignment) unit;
			generateAssignUnit(assin, cm, block, cls);
		} else if (unit instanceof BAssignExpression) {
			BAssignExpression assin = (BAssignExpression) unit;
			JExpression ex = this.generateAssignExpression(assin, cm, cls);
			block.add((JStatement) ex);
		} else if (unit instanceof BInvoker) {
			BInvoker invoker = (BInvoker) unit;
			if (!invoker.isLinker() || staticGen) {
				JExpression ex = this.generateInvoker(cm, invoker, cls);
				if (ex instanceof JStatement) {
					block.add((JStatement) ex);
				} else {
					// Debug.d();
				}

			}
		} else if (unit instanceof BMultiCondition) {
			BMultiCondition bmc = (BMultiCondition) unit;
			this.generateIfUnit(bmc, cm, block, cls);
		} else if (unit instanceof BSwitchUnit) {
			BSwitchUnit bmc = (BSwitchUnit) unit;
			this.generateSwitchUnit(bmc, cm, block, cls);
		} else if (unit instanceof BTryUnit) {
			BTryUnit btry = (BTryUnit) unit;
			this.generateTryUniit(btry, block, cm, cls);
		} else if (unit instanceof BReturnUnit) {
			BReturnUnit re = (BReturnUnit) unit;
			this.generateReturnUnit(re, cm, block, cls);
		} else if (unit instanceof BAssert) {
			BAssert re = (BAssert) unit;
			this.generateAssertUnit(re, cm, block, cls);
		} else if (unit instanceof BLoopUnit) {
			BLoopUnit loop = (BLoopUnit) unit;
			this.generateLoopUnit(loop, cm, block, cls);
		} else if (unit instanceof BModifiedBlock) {
			BModifiedBlock mb = (BModifiedBlock) unit;
			this.generateModifiedBlock(mb, cm, block, cls);
		} else if (unit instanceof BOnewordLine) {
			BOnewordLine loop = (BOnewordLine) unit;
			this.generateOnewordLine(loop, block);
		} else if (unit instanceof BThrow) {
			BThrow bthrow = (BThrow) unit;
			this.generateThrow(cm, bthrow, block, cls);
		} else if (unit instanceof BSingleExpressionUnit) {
			BSingleExpressionUnit single = (BSingleExpressionUnit) unit;

			block.add((JStatement) this.generateSingleExpression(single, cm, cls));
		}
	}

	private void generateThrow(JCodeModel cm, BThrow bthrow, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateThrow :" + bthrow);
		block._throw(this.generateObject(cm, bthrow.getException(), cls));

	}

	private void generateOnewordLine(BOnewordLine loop, JBlock block) {
		log("generateOnewordLine :" + loop);
		String label = loop.getLabel();
		if (loop.getWord().equals(BOnewordLine.WORD_BREAK)) {
			if (label == null) {
				block._break();
			} else {
				block._break(new JLabel(label));
			}
		} else if (loop.getWord().equals(BOnewordLine.WORD_CONTINUE)) {
			if (label == null) {
				block._continue();
			} else {
				block._continue(new JLabel(label));
			}
		}
	}

	private void generateModifiedBlock(BModifiedBlock mb, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateModifiedBlock :" + mb);
		BValuable obj = mb.getVariable();

		if (Modifier.isSynchronized(mb.getMods())) {
			JExpression ex = this.generateObject(cm, obj, cls);
			// LinePrinter p = new LinePrinter();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(output);
			ex.generate(new JFormatter(writer));
			writer.flush();
			try {
				output.flush();
				block.directStatement("synchronized(" + new String(output.toByteArray()) + ")");
				output.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		JBlock bblock = block.block();
		this.generateLogicBody(mb, cm, bblock, cls);
	}

	private void generateSwitchUnit(BSwitchUnit bmc, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateSwitchUnit :" + bmc);
		BValuable var = bmc.getVariable();
		JSwitch swi = block._switch(this.generateObject(cm, var, cls));
		List<BConditionUnit> units = bmc.getConditionUnits();
		for (BConditionUnit unit : units) {
			BValuable ex = unit.getCondition();
			JCase jcase;
			if (ex != null) {
				if (ex instanceof BExpression) {
					BExpression e = (BExpression) ex;
					jcase = swi._case(this.generateObject(cm, e.getExRight(), cls));
				} else {

					jcase = swi._case(this.generateObject(cm, ex, cls));
				}

			} else {
				jcase = swi._default();
			}
			this.generateLogicBody(unit.getLogicBody(), cm, jcase.body(), cls);

		}
	}

	private void generateNoteUnit(BNote note, JCodeModel cm, JBlock block, JDefinedClass cls) {
		log("generateNoteUnit :" + note);
		String comment = note.getNote();

		block.directStatement(" ");
		int index = comment.indexOf("<br/>");
		if (index > 0) {
			String[] ss = comment.split("<br/>");
			for (String s : ss) {
				block.directStatement("//" + s);
			}
		} else {
			block.directStatement("//" + comment);
		}
	}

	private void generateLoopUnit(BLoopUnit loop, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateLoopUnit");
		if (loop.getLoopType() == BLoopUnit.TYPE_ENHANCED) {

			BValuable expression = loop.getEnhanceExpression();
			BParameter left = loop.getEnhanceVariable();

			if (left.getName() != null) {
				block.directStatement("//" + left.getName());
			}

			JType type = this.getType(cm, left, cls);

			JExpression exp = this.generateObject(cm, expression, cls);
			JForEach floop = block.forEach(type, left.getLogicName(), exp);
			generateLogicBody(loop.getEditor(), cm, floop.body(), cls);
		} else if (loop.getLoopType() == BLoopUnit.TYPE_WHILE) {
			BValuable ex = loop.getCondition();
			JWhileLoop wp = block._while(this.generateObject(cm, ex, cls));

			generateLogicBody(loop.getEditor(), cm, wp.body(), cls);
		} else if (loop.getLoopType() == BLoopUnit.TYPE_DOWHILE) {
			BValuable ex = loop.getCondition();
			JDoLoop doloop = block._do(this.generateObject(cm, ex, cls));
			generateLogicBody(loop.getEditor(), cm, doloop.body(), cls);
		} else if (loop.getLoopType() == BLoopUnit.TYPE_FORLOOP) {

			BValuable condition = loop.getCondition();
			List<BValuable> updates = loop.getUpdates();
			List<BAssign> inits = loop.getForLoopInitializers();
			for (BAssign assign : inits) {
				if (assign instanceof BAssignment) {
					BAssignment bas = (BAssignment) assign;
					BVariable bv = (BVariable) bas.getLeft();
					block.directStatement("//" + bv.getName());

				}
			}
			JForLoop jloop = block._for();
			for (BAssign assign : inits) {

				if (assign instanceof BAssignment) {
					BAssignment bas = (BAssignment) assign;
					JType type = this.getType(cm, bas.getLeft(), cls);
					BVariable var = ((BVariable) bas.getLeft());
					jloop.init(type, var.getLogicName(), this.generateObject(cm, assign.getRight(), cls));
				} else {
					jloop.init(this.generateAssignExpression((BAssignExpression) assign, cm, cls));
				}
			}

			for (BValuable obj : updates) {
				JExpression ex = this.generateObject(cm, obj, cls);
				jloop.update(ex);
			}
			JExpression ex = this.generateObject(cm, condition, cls);
			if (ex != null) {
				if (ex.equals(JExpr._null())) {
					ex = null;
				}
			}
			jloop.test(ex);
			generateLogicBody(loop.getEditor(), cm, jloop.body(), cls);
		}

	}

	private void generateAssertUnit(BAssert re, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateAssertUnit :" + re);
		BValuable obj = re.getExpression();
		if (obj != null) {

			block._assert(this.generateObject(cm, obj, cls));
		}
	}

	private void generateReturnUnit(BReturnUnit re, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateReturnUnit :" + re);
		BValuable obj = re.getReturnValue();
		if (obj != null) {
			block._return(this.generateObject(cm, obj, cls));
		} else {
			block._return();
		}
	}

	private JExpression generateInvoker(JCodeModel cm, BInvoker invoker, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateInvoker:" + invoker);

		BValuable parent = invoker.getInvokeParent();
		BValuable child = invoker.getInvokeChild();
		List<BValuable> parameters = invoker.getParameters();

		JInvocation invk = null;
		// constructor
		if (child instanceof BConstructor) {
			BConstructor con = (BConstructor) child;

			BClass bclass = con.getBody();
			if (bclass != null) {
				JDefinedClass anony = cm.anonymousClass(this.getType(cm, con.getReturn(), cls).boxify());
				this.generateClassBody(bclass, cm, anony);
				invk = JExpr._new(anony);
			} else {
				invk = JExpr._new(this.getType(cm, parent, cls));
			}
		} else if (parent == null || parent.getBClass() == null) {

			if (child instanceof BMethod) {
				BMethod method = (BMethod) child;
				if (method.getLogicName().equals("this")) {
					invk = JExpr.invoke("this");
				}
				if (method.getLogicName().equals("super")) {
					invk = JExpr.invoke("super");
				}
			}

			if (invk == null) {
				// parent is null
				JExpression exp = this.generateObject(cm, child, cls);
				if (exp instanceof JInvocation) {
					invk = (JInvocation) exp;
				} else {
					return exp;
				}
			}

		} else {
			// parent is not null

			boolean isStatic = false;
			if (parent instanceof BVariable) {
				BVariable var = (BVariable) parent;
				if (var.isClass() && !var.isCaller()) {
					isStatic = true;
				}
			}
			if (isStatic) {
				// static invoker
				// JExpression parentExp = this.generateObject(cm, parent, cls);

				JType type = this.getType(cm, parent, cls);

				JClass jclass = type.boxify();

				boolean dotted = false;
				if (child instanceof BMethod) {
					BMethod method = (BMethod) child;
					if (parent.getBClass().getQualifiedName().equals(Class.class.getName())) {
						BVariable var = (BVariable) parent;
						String logicName = var.getLogicName();
						if (var.getLogicName().endsWith(".class")) {
							type = this.getTypeByName(cm, logicName.substring(0, logicName.length() - 6), cls);
							JExpression dot = JExpr.dotclass(type.boxify());
							invk = new JInvocation(dot, method.getLogicName());
							dotted = true;
						}

					}

					if (!dotted) {
						invk = jclass.staticInvoke(method.getLogicName());
					}

				} else if (child instanceof BVariable) {
					BVariable v = (BVariable) child;

					return jclass.staticRef(v.getLogicName());

				} else {
					Debug.d();
				}

			} else {
				// not static invoker
				if (child instanceof BMethod) {
					BMethod method = (BMethod) child;

					// this class
					if (parent.getBClass().getQualifiedName().equals(cls.fullName())) {

						if (parent instanceof BVariable) {
							BVariable var = (BVariable) parent;
							if (var.getLogicName().toLowerCase().equals("super")) {
								invk = JExpr.invoke(JExpr._super(), method.getLogicName());
							} else {
								if (!var.getLogicName().toLowerCase().equals("this")) {
									JExpression parentExp = this.generateObject(cm, parent, cls);
									invk = JExpr.invoke(parentExp, method.getLogicName());
								} else {
									invk = JExpr.invoke(null, method.getLogicName());
								}
							}
						} else {
							// new self().xxx
							JExpression parentExp = this.generateObject(cm, parent, cls);
							invk = JExpr.invoke(parentExp, method.getLogicName());
						}
					} else {
						if (invoker.isInnerClassCall()) {
							invk = JExpr.invoke(null, method.getLogicName());
						} else {
							JExpression parentExp = this.generateObject(cm, parent, cls);
							invk = JExpr.invoke(parentExp, method.getLogicName());
						}
					}

				} else if (child instanceof BVariable) {
					BVariable v = (BVariable) child;

					// data class
					if (parent.getBClass().isData()) {
						String dataMethodName = invoker.getDataMethodName();

						// data restored a name,because there is not guaranteed to be named
						// correctly(the case for generated design)
						if (dataMethodName != null) {
							JExpression parentExp = this.generateObject(cm, parent, cls);
							invk = JExpr.invoke(parentExp, dataMethodName);
						} else {
							// normal designed
							String method = v.getLogicName();
							if (parameters != null && parameters.size() > 0) {
								method = "set" + method.substring(0, 1).toUpperCase() + method.substring(1);
								invk = JExpr.invoke(generateObject(cm, parent, cls), method);
							} else {
								method = method.substring(0, 1).toUpperCase() + method.substring(1);
								if (v.getBClass().getQualifiedName().equals("boolean")) {
									method = "is" + method;
								} else {
									method = "get" + method;
								}
								JExpression parentExp = this.generateObject(cm, parent, cls);
								invk = JExpr.invoke(parentExp, method);
							}
						}
					} else {
						// not data class
						JExpression parentExp = this.generateObject(cm, parent, cls);

						return JExpr.ref(parentExp, v.getLogicName());
					}

				} else {
					Debug.d();
				}

			}

		}

		if (parameters != null)

		{
			for (BValuable arg : parameters) {
				JExpression a = this.generateObject(cm, arg, cls);
				if (a != null) {

					invk.arg(a);
				}
			}
		}
		return invk;

	}

	private void generateIfUnit(BMultiCondition bmc, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateIfUnit");
		List<BConditionUnit> bunits = bmc.getConditionUnits();
		JConditional con = null;

		for (BConditionUnit bunit : bunits) {
			BValuable ex = bunit.getCondition();
			if (con == null) {

				con = block._if(this.generateObject(cm, ex, cls));
			} else {
				if (bunit.isLast()) {
					generateLogicBody(bunit.getLogicBody(), cm, con._else(), cls);
					continue;
				} else {
					con = con._elseif(this.generateObject(cm, ex, cls));
				}
			}
			generateLogicBody(bunit.getLogicBody(), cm, con._then(), cls);

		}
	}

	private void generateTryUniit(BTryUnit btry, JBlock block, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		JTryBlock tblock = block._try();
		generateLogicBody(btry.getTryEditor(), cm, tblock.body(), cls);
		List<BCatchUnit> list = btry.getCatches();

		for (BCatchUnit cat : list) {

			BParameter var = cat.getVariable();
			List<BVariable> types = var.getUnionTypes();
			JCatchBlock cb;
			if (types != null && !types.isEmpty()) {
				List<JClass> union = new ArrayList<JClass>();
				for (BVariable t : types) {
					JType type = this.getType(cm, t.getBClass(), cls);
					union.add(type.boxify());
				}
				cb = tblock._catch(union);
			} else {
				JType type = this.getType(cm, var, cls);
				cb = tblock._catch(type.boxify());
			}

			cb.param(var.getLogicName());
			JBlock b = cb.body();

			generateLogicBody(cat.getEditor(), cm, b, cls);

		}
		BLogicBody fbody = btry.getFinalEditor();
		if (fbody != null) {
			generateLogicBody(fbody, cm, tblock._finally(), cls);
		}
	}

	private JExpression generateExpression(BExpression ex, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		JExpression jex = this.generateExpressionOnly(ex, cm, cls);
		// if (ex.isParenthesized()) {
		// Application.TODO("isParenthesized");
		// }

		return jex;
	}

	private JExpression generateExpressionOnly(BExpression ex, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateExpressionOnly-> " + ex.toString());
		BValuable left = ex.getExLeft();
		BValuable right = ex.getExRight();
		String m = ex.getExMiddle().getLogicName();

		JExpression l = generateObject(cm, left, cls);

		JExpression r = generateObject(cm, right, cls);

		if (m.equals("_instanceof")) {
			return l._instanceof(this.getType(cm, right, cls));
		}

		if (m.equals(BLogiker.GREATTHAN.getLogicName())) {
			return l.gt(r);
		} else if (m.equals(BLogiker.INSTANCEOF.getLogicName())) {
			return l._instanceof(this.getType(cm, right, cls));
		} else if (m.equals(BLogiker.GREATTHANEQUAL.getLogicName())) {
			return l.gte(r);
		} else if (m.equals(BLogiker.LESSTHAN.getLogicName())) {
			return l.lt(r);
		} else if (m.equals(BLogiker.LESSTHANEQUAL.getLogicName())) {
			return l.lte(r);
		} else if (m.equals(BLogiker.EQUAL.getLogicName())) {

			if (left.getBClass() != null && !r.equals(JExpr._null())) {
				BClass bclass = (BClass) left.getBClass();
				Class<?> clss = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
				Class<?> rclss = CodecUtils.getClassByName(right.getBClass().getQualifiedName(), project);
				if (clss != null && rclss != null) {
					if (!clss.isPrimitive() && !rclss.isPrimitive()) {
						JInvocation in = l.invoke("equals");
						return in.arg(r);
					}
				}
			}
			return l.eq(r);
		} else if (m.equals(BLogiker.LOGICAND.getLogicName())) {
			return l.cand(r);
		} else if (m.equals(BLogiker.LOGICOR.getLogicName())) {
			return l.cor(r);
		} else if (m.equals(BLogiker.PLUS.getLogicName())) {
			return l.plus(r);
		} else if (m.equals(BLogiker.MINUS.getLogicName())) {
			return l.minus(r);
		} else if (m.equals(BLogiker.MULTIPLY.getLogicName())) {
			return l.mul(r);
		} else if (m.equals(BLogiker.DIVIDE.getLogicName())) {
			return l.div(r);
		} else if (m.equals(BLogiker.MOD.getLogicName())) {
			return l.mod(r);
		} else if (m.equals(BLogiker.NOT.getLogicName())) {
			return l.not();
		} else if (m.equals(BLogiker.COMPLEMENT.getLogicName())) {
			return l.complement();
		} else if (m.equals(BLogiker.NOTQUEAL.getLogicName())) {
			if (left.getBClass() != null && !r.equals(JExpr._null())) {
				BClass bclass = (BClass) left.getBClass();
				Class<?> clss = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
				Class<?> rclss = CodecUtils.getClassByName(right.getBClass().getQualifiedName(), project);
				if (clss != null && rclss != null) {

					if (!clss.isPrimitive() && !rclss.isPrimitive()) {
						JInvocation in = l.invoke("equals");
						return in.arg(r).not();
					}
				}
			}
			return l.ne(r);
		} else if (m.equals(BLogiker.SHEFTLEFT.getLogicName())) {
			return l.shl(r);
		} else if (m.equals(BLogiker.SHEFTRIGHT.getLogicName())) {
			return l.shr(r);
		} else if (m.equals(BLogiker.SHEFTLEFTPLUS.getLogicName())) {
			return l.shlz(r);
		} else if (m.equals(BLogiker.SHEFTRIGHTPLUS.getLogicName())) {
			return l.shrz(r);
		} else if (m.equals(BLogiker.BITAND.getLogicName())) {
			return l.band(r);
		} else if (m.equals(BLogiker.BITOR.getLogicName())) {
			return l.bor(r);
		}

		throw new ClassNotFoundException();
	}

	private void generateAssignUnit(BAssignment assin, JCodeModel cm, JBlock block, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateUnitAssign:" + assin);
		BParameter left = assin.getLeft();
		BValuable value = assin.getRight();
		BLogiker assignment = assin.getAssignment();
		String logiker = null;
		if (assignment != null) {
			logiker = assignment.getLogicName();
		}
		if (left instanceof BInvoker) {
			BInvoker invoker = (BInvoker) left;
			if (invoker.isData()) {
				invoker = (BInvoker) invoker.cloneAll();
				invoker.addParameter(value);
				block.add((JStatement) this.generateInvoker(cm, invoker, cls));
			} else {
				JExpression ex = this.generateInvoker(cm, invoker, cls);
				JAssignmentTarget target = (JAssignmentTarget) ex;
				block.assignPlus(target, generateObject(cm, value, cls), logiker);
			}
		} else if (left instanceof BVariable) {

			BVariable name = (BVariable) left;

			BClass type = name.getBClass();
			JType jclass = this.getType(cm, name, cls);

			String logicNm = name.getLogicName();
			if (logicNm == null) {

				logicNm = CodecUtils.makeMethodName(logicNm, name.getName());
				name.setLogicName(logicNm);
			}
			JVar var = null;
			if (value == null) {
				var = block.decl(left.getModifier(), jclass, name.getLogicName(), null);
			} else {
				if (name.isCaller()) {
					JAssignmentTarget target = (JAssignmentTarget) this.generateObject(cm, name, cls);
					block.assignPlus(target, generateObject(cm, value, cls), logiker);
				} else if (name.getArrayObject() != null) {
					JAssignmentTarget target = (JAssignmentTarget) this.generateObject(cm, name, cls);
					block.assignPlus(target, generateObject(cm, value, cls), logiker);
				} else {
					if (type.isArray()) {
						log(jclass.fullName());
						var = block.decl(left.getModifier(), jclass, name.getLogicName(),
								generateObject(cm, value, cls));
					} else {
						int modifer = left.getModifier();
						// TODO TODO
						if (modifer != 0) {
							// Debug.a();
							modifer = 0;
						}

						var = block.decl(modifer, jclass, name.getLogicName(), generateObject(cm, value, cls));
					}
				}
			}
		}
	}

	private JExpression generateAssignExpression(BAssignExpression assin, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateUnitAssignExpression:" + assin);

		BValuable left = assin.getLeft();
		BValuable value = assin.getRight();
		BLogiker assignment = assin.getAssignment();
		String logiker = null;
		if (assignment != null) {
			logiker = assignment.getLogicName();
		}
		if (left instanceof BInvoker) {
			BInvoker invoker = (BInvoker) left;
			if (invoker.isData()) {
				invoker = (BInvoker) invoker.cloneAll();
				invoker.addParameter(value);
				return this.generateInvoker(cm, invoker, cls);
			}
		}

		if (value == null) {
			JAssignmentTarget target = (JAssignmentTarget) this.generateObject(cm, left, cls);
			return JExpr.assignPlus(target, JExpr._null(), logiker);

		} else {
			JExpression obj = this.generateObject(cm, left, cls);
			if (obj instanceof JAssignmentTarget) {
				JAssignmentTarget target = (JAssignmentTarget) obj;
				return JExpr.assignPlus(target, generateObject(cm, value, cls), logiker);
			} else if (obj instanceof JInvocation) {
				JExpression v = generateObject(cm, value, cls);

				JInvocation anno = (JInvocation) obj;
				anno.arg(v);

				return anno;
			} else {
				return null;
			}

		}

	}

	private JExpression generateSingleExpression(BSingleExpressionUnit unit, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("generateSingleExpression");
		BValuable var = unit.getVariable();
		if (unit.getOperator().equals(BSingleExpressionUnit.INCREMENT)) {

			return this.generateObject(cm, var, cls).incr();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.DECREMENT)) {
			return this.generateObject(cm, var, cls).decr();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.INCREMENT_BEFORE)) {
			return this.generateObject(cm, var, cls).decrBefore();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.DECREMENT_BEFORE)) {
			return this.generateObject(cm, var, cls).decrBefore();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.COMPLEMENT)) {
			return this.generateObject(cm, var, cls).complement();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.UNARY_MINUS)) {
			return this.generateObject(cm, var, cls).minus();
		} else if (unit.getOperator().equals(BSingleExpressionUnit.UNARY_PLUS)) {
			return this.generateObject(cm, var, cls);
		} else {
			Debug.d();
			return null;
		}
	}

	public JFieldVar generateClassAssign(BAssignment variable, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateClassAssign:" + variable);

		BParameter name = variable.getLeft();
		BValuable value = variable.getRight();

		JType jtype = getType(cm, name, cls);

		String logicNm = name.getLogicName();
		if (logicNm == null) {
			logicNm = CodecUtils.makeMethodName(logicNm, name.getName());
			name.setLogicName(logicNm);
		}

		JFieldVar f = null;
		if (value == null) {
			f = cls.field(name.getModifier(), jtype, name.getLogicName());
		} else {
			try {
				f = cls.field(name.getModifier(), jtype, name.getLogicName(), generateObject(cm, value, cls));

			} catch (Exception e) {
				return null;
				// trying to create the same field twice
			}
		}

		this.generateAnnotation(variable, cm, f, cls);
		if (f != null && name != null) {
			f.javadoc().add(name.getName());
		}

		return f;
	}

	private JExpression generateObject(JCodeModel cm, BValuable value, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		JExpression exp = this.generateObjectExpression(cm, value, cls);
		if (value != null && value.getCast() != null) {
			return JExpr.cast(this.getType(cm, value.getCast(), cls), exp);
		}
		return exp;
	}

	public JExpression generateObjectExpression(JCodeModel cm, BValuable value, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("generateObject:" + value);
		if (value == null) {
			return null;
		}
		if (value.getBClass() == null) {
			if (value instanceof BMethod) {
				BMethod method = (BMethod) value;
				if (method.getLogicName().equals("this")) {
					return JExpr._this();
				}
				if (method.getLogicName().equals("super")) {
					return JExpr._super();
				}
			}
			return null;
		}
		if (value.getBClass().getQualifiedName().equals(BClass.NULL)) {

			return JExpr._null();
		}

		if (value.getArrayObject() != null) {
			BValuable var = value.getArrayObject();
			return this.generateObject(cm, var, cls).component(this.generateObject(cm, value.getArrayIndex(), cls));
		}

		if (value instanceof BVariable && value.getBClass().isArray()) {
			BVariable v = (BVariable) value;
			if (v.isNewClass()) {

				JArray array = null;

				if (v.getArrayDimensions().size() != 0) {
					List<BValuable> ds = v.getArrayDimensions();

					JArrayCompRef ref = null;
					for (BValuable d : ds) {
						JExpression index = this.generateObjectExpression(cm, d, cls);
						if (array == null) {

							BType pressent = v.getBClass().getArrayPressentClass();
							JType type = this.getType(cm, pressent, cls);
							array = JExpr.newArray(type, index);
							array.setTitled(v.isArrayTitled());
						} else {
							if (ref == null) {
								ref = array.component(index);
							} else {
								ref = ref.component(index);
							}
						}
					}
					if (ref != null) {
						return ref;
					}

				} else {

					array = JExpr.newArray(this.getType(cm, v.getBClass().getArrayPressentClass(), cls));
					array.setTitled(v.isArrayTitled());
				}
				List<BValuable> values = v.getInitValues();

				for (BValuable init : values) {
					array.add(this.generateObjectExpression(cm, init, cls));
				}
				return array;
			}
		} else {
			// if not new
		}

		if (value instanceof BExpressionLine) {
			BExpressionLine line = (BExpressionLine) value;
			BValuable condition = line.getCondition();
			BValuable trueObj = line.getTrue();
			BValuable falseObj = line.getFalse();
			return JOp.cond(this.generateObject(cm, condition, cls), this.generateObject(cm, trueObj, cls),
					this.generateObject(cm, falseObj, cls));
		}

		if (value instanceof BSingleExpressionUnit) {
			BSingleExpressionUnit unit = (BSingleExpressionUnit) value;
			return this.generateSingleExpression(unit, cm, cls);
		}

		if (value instanceof BInvoker) {
			return this.generateInvoker(cm, (BInvoker) value, cls);
		}
		if (value instanceof BMethod) {
			BMethod method = (BMethod) value;
			return JExpr.invoke(null, method.getLogicName());
		}

		if (value instanceof BExpression) {
			return this.generateExpression((BExpression) value, cm, cls);
		}
		if (value instanceof BAssignExpression) {
			BAssignExpression assign = (BAssignExpression) value;
			return this.generateAssignExpression(assign, cm, cls);
		}

		if (value instanceof BVariable) {
			BVariable v = (BVariable) value;
			if (v.getLogicName() != null) {

				if (v.isNewClass()) {
					BClass bclass = v.getBClass();
					if (!bclass.isPrimitive() && !bclass.getQualifiedName().equals(String.class.getName())) {
						return JExpr._new(this.getType(cm, v, cls));
					}
				}
				String logicName = v.getLogicName();

				// com.somethid.class
				if (logicName.endsWith(".class")) {
					JType type = this.getTypeByName(cm, logicName.substring(0, logicName.length() - 6), cls);
					return JExpr.dotclass(type.boxify());
				}

				BClass bclass = v.getBClass();
				if (logicName.startsWith("\"") && logicName.endsWith("\"")
						&& bclass.getQualifiedName().equals(String.class.getName())) {
					return JExpr.lit(logicName.substring(1, logicName.length() - 1));
				} else if (bclass.getQualifiedName().equals("byte")) {
					int i = Integer.parseInt(logicName);
					String a = "0x" + Integer.toHexString(i);

					return JExpr.cast(this.getType(cm, CodecUtils.BByte(), cls), JExpr.ref(a));

				}
				return JExpr.ref(logicName);

			} else {
				return JExpr.cast(this.getType(cm, v, cls), JExpr._null());
			}
		}
		Debug.d();
		throw new ClassNotFoundException();
	}

	private JType getType(JCodeModel cm, BType btype, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		log("getType:" + btype.getLogicName());

		JType array = this.getTypeArray(cm, btype, cls);
		if (array != null) {
			return array;
		}
		JType type = null;

		if (btype.isTypeVariable()) {
			if (btype.isParameterValue()) {

				return cm.ref(btype.getLogicName());
			}
			return null;

		}

		if (btype.isWild()) {
			List<String> bound = btype.getBounds();
			if (bound != null && bound.size() == 1) {
				JType wtyp = this.getTypeByName(cm, bound.get(0), cls);

				if (wtyp != null) {
					return JClass.wildcard(cm, wtyp.boxify());
				} else {
					return JClass.wildcard(cm, null);
				}
			} else {
				return JClass.wildcard(cm, null);
			}
		}

		if (btype instanceof BClass) {
			BClass bclass = (BClass) btype;
			type = this.getType(cm, bclass, cls);
		} else {
			if (btype.getLogicName() != null) {
				type = cm.ref(btype.getLogicName());
			} else {
				if (btype.isContainer()) {
					// OK
				} else {
					Debug.d();
				}
			}
		}

		if (type == null) {
			return null;
		}

		JClass jc = type.boxify();
		List<JClass> narrows = new ArrayList<JClass>();

		List<BType> types = btype.getParameterizedTypes();
		for (BType t : types) {
			if (t instanceof BClass) {
				JType n = this.getType(cm, t, cls);
				narrows.add(n.boxify());
			}
		}

		if (narrows.size() > 0) {
			jc = jc.narrow(narrows);
		} else {
			return type;
		}
		return jc;

	}

	public JType getType(JCodeModel cm, BValuable valuable, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {

		if (valuable == null) {
			return null;
		}
		log("getType:" + valuable.toString());
		BClass type = valuable.getBClass();
		BType btype = valuable.getParameterizedTypeValue();
		if (type == null) {
			if (btype == null) {
				return cm.VOID;
			} else {
				return this.getType(cm, btype, cls);
			}
		}

		JType array = this.getTypeArray(cm, type, cls);
		if (array != null) {
			return array;
		}

		if (type.getQualifiedName().equals(Object.class.getName())) {
			if (btype != null) {
				if (btype.isTypeVariable()) {
					return cm.ref(btype.getLogicName());
				}

			}
		}

		JType t = this.getType(cm, type, cls);
		if (t == null) {
			return null;
		}

		if (btype == null) {
			return t;
		}
		List<BType> types = btype.getParameterizedTypes();
		List<JClass> narrows = this.getTypeParameterizeTypes(types, cm, cls);
		if (narrows.isEmpty()) {
			return t;
		} else {
			return t.boxify().narrow(narrows);
		}
	}

	private JType getTypeArray(JCodeModel cm, BType bclass, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("getType:" + bclass.getLogicName());
		if (bclass.isArray()) {

			BType array = bclass.getArrayPressentClass();
			JType type = this.getTypeArray(cm, array, cls);
			if (type == null) {
				if (array instanceof BClass) {
					BClass bc = (BClass) array;
					type = cm.ref(bc, project);// this.getTypeByName(cm, bc.getQualifiedName(), cls);
				} else {
					type = this.getTypeByName(cm, array.getLogicName(), cls);
				}
			}
			return type.array();

		}
		return null;
	}

	private JType getType(JCodeModel cm, BClass type, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		if (type == null) {
			return cm.VOID;
		}
		log("getType:" + type.getQualifiedName());

		JType array = this.getTypeArray(cm, type, cls);
		if (array != null) {
			return array;
		}

		JType jtype = cm.ref(type, project);// this.getTypeByName(cm, type.getQualifiedName(), cls);

		if (jtype == null) {
			List<BClass> list = new ArrayList<BClass>();
			list.add(type);
			JavaGen g = new JavaGen(this.project);
			g.generate(list);
		}
		List<BType> ParameterizedTypeNames = type.getParameterizedTypes();
		if (ParameterizedTypeNames != null) {

			if (ParameterizedTypeNames.size() != 0) {
				JClass jc = jtype.boxify();
				List<JClass> narrows = new ArrayList<JClass>();
				for (BType btype : ParameterizedTypeNames) {
					if (btype == null) {
						continue;
					}
					if (btype.isTypeVariable()) {
						continue;
					}

					JType sub = this.getType(cm, btype, cls);
					if (sub == null) {
						List<JClass> jclass = this.getTypeParameterizeTypes(btype.getParameterizedTypes(), cm, cls);
						narrows.addAll(jclass);
					} else {
						if (btype.isParameterValue()) {
							narrows.add(sub.boxify());
						}
					}

				}
				if (narrows.size() > 0) {
					jc = jc.narrow(narrows);
				} else {
					return jtype;
				}
				return jc;
			}
		}
		return jtype;

	}

	private List<JClass> getTypeParameterizeTypes(List<BType> ParameterizedTypes, JCodeModel cm, JDefinedClass cls)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("getTypeByNameWidthParameter:" + ParameterizedTypes);

		List<JClass> narrows = new ArrayList<JClass>();
		if (ParameterizedTypes != null && ParameterizedTypes.size() != 0) {

			for (BType value : ParameterizedTypes) {

				JType type = this.getType(cm, value, cls);
				if (type != null) {
					JClass jclass = type.boxify();
					narrows.add(jclass);
				}

			}
		}
		return narrows;
	}

	private JType getTypeByName(JCodeModel cm, String name, JDefinedClass jclass)
			throws ClassNotFoundException, JClassAlreadyExistsException, IOException, BeeClassExistsException {
		log("getTypeByName:" + name);
		log("getTypeByName jclass name:" + jclass.name());

		if (name.equals(jclass.fullName())) {
			return jclass;
		}

		if (this.classes != null) {

			for (ClassSet set : this.classes) {
				if (set.getBclass().getQualifiedName().equals(name)) {
					JDefinedClass generated = cm._getClass(name);
					if (generated == null) {
						this.generate(set);
					}
				}
			}
		}

		JType jtype = cm.refExist(name);

		if (jtype == null) {

			System.err.println("getClassByName:" + name);
			Class<?> cls = CodecUtils.getClassByName(name, project);
			if (cls == null) {
				return null;

			}
			if (cls.isPrimitive()) {
				try {
					return JPrimitiveType.parse(cm, cls.getName());
				} catch (Exception e) {
				}
			}

			if (cls.isLocalClass()) {
				Debug.d();
				jtype = cm.ref(cls.getSimpleName());
			}

			if (jtype == null) {

				if (name.contains("$")) {
					String parentClassName = name.substring(0, name.indexOf("$"));
					String simpleName = name.substring(parentClassName.length() + 1);

					if (cm._getClass(parentClassName) != null) {
						jtype = cm.ref(simpleName);
					} else {
						Class<?> pclass = CodecUtils.getClassByName(parentClassName, project);

						if (jclass.getBaseClass(pclass) != null) {
							jtype = cm.ref(simpleName);
						}

					}

				}
			}

			if (jtype == null) {
				jtype = cm.ref(cls);
			}
		}
		return jtype;
	}

	private void log(String x) {
		P.check(null);
		System.out.println(x);
	}

	private void error(String x) {
		System.err.println(x);
	}

	private class ClassSet {
		private BClass bclass;
		private List<BClass> inners = new ArrayList<BClass>();

		public BClass getBclass() {
			return bclass;
		}

		public void setBclass(BClass bclass) {
			this.bclass = bclass;
		}

		public List<BClass> getInners() {
			return inners;
		}

	}

}
