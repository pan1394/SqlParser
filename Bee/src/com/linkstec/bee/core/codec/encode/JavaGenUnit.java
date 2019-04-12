package com.linkstec.bee.core.codec.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.linkstec.bee.core.codec.decode.DecodeDocProjects;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.action.BDocIF;
import com.linkstec.bee.core.fw.action.BJavaGenIF;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class JavaGenUnit implements BJavaGenIF {

	private static String toGen(JDeclaration generable) {
		if (generable == null) {
			// TODO
			return "";
		}
		try {

			ByteArrayOutputStream output = new ByteArrayOutputStream();

			PrintWriter writer = new PrintWriter(output);
			JFormatter f = new JFormatter(writer);
			f.setSimplePrinting();

			generable.declare(f);

			writer.flush();
			output.flush();
			String s = new String(output.toByteArray());
			output.close();
			writer.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getTypeSource(BProject project, BClass bclass, BValuable value) {
		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);

			JType jtype = gen.getType(cls.owner(), value, cls);
			if (jtype == null) {
				return null;
			}

			return toGen(jtype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getClassHeaderSource(BProject project, BClass bclass) {
		try {

			JavaGen gen = new JavaGen(project);
			JCodeModel cm = new JCodeModel();
			BClass b = bclass.cloneAll();

			JDefinedClass cls = gen.generateClassCommonInfo(cm, b);

			return toGen((JDeclaration) cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getValuableSource(BProject project, BClass bclass, BValuable value) {

		if (value == null) {
			return null;
		}

		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);

			JExpression ex = gen.generateObjectExpression(cls.owner(), value, cls);

			if (ex == null) {
				return null;
			}

			return toGen(ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getAnnotation(BProject project, BClass bclass) {

		List<String> list = new ArrayList<String>();

		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);

			Collection<JAnnotationUse> annos = cls.annotations();

			Iterator<JAnnotationUse> ite = annos.iterator();
			while (ite.hasNext()) {
				JAnnotationUse use = ite.next();
				list.add(toGen(use));
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUnitSource(BProject project, BClass bclass, BLogicUnit value) {

		if (value == null) {
			return null;
		}
		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);

			if (value instanceof BAssignment) {
				JFieldVar f = gen.generateClassAssign((BAssignment) value, cls.owner(), cls);
				return toGen((JDeclaration) f);
			} else {
				JBlock block = cls.init();
				value = (BLogicUnit) value.cloneAll();
				value.setAlert("GEN");
				gen.generateUnit(value, cls.owner(), block, cls);
				return toGen(block);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMethodSource(BProject project, BClass bclass, BMethod value) {

		if (value == null) {
			return null;
		}
		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);

			JMethod m = gen.generateMethod(value, cls.owner(), cls);

			if (!Modifier.isAbstract(value.getModifier())) {
				gen.generateLogicBody(value.getLogicBody(), cls.owner(), m.body(), cls);
			}

			return toGen(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAllSource(BProject project, BClass bclass) {

		try {

			JavaGen gen = new JavaGen(project);
			JDefinedClass cls = getGenClass(bclass, gen);
			cls = gen.generateLogicClass(bclass, cls.owner(), cls);
			return toGenAll(cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JDefinedClass getGenClass(BClass bclass, JavaGen gen)
			throws ClassNotFoundException, JClassAlreadyExistsException, BeeClassExistsException, IOException {

		JCodeModel cm = new JCodeModel();
		BClass b = bclass.cloneAll();
		JDefinedClass cls = gen.generateClassCommonInfo(cm, b);

		return cls;

	}

	private static String toGenAll(JDefinedClass cls) {
		try {

			ByteArrayOutputStream output = new ByteArrayOutputStream();

			CodeWriter w = new CodeWriter() {
				String s = null;

				@Override
				public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
					return output;
				}

				@Override
				public void close() throws IOException {
					s = new String(output.toByteArray());
				}

				public String toString() {
					return s;
				}
			};

			cls.owner().build(w);

			return w.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String toGen(JGenerable generable) {
		try {

			ByteArrayOutputStream output = new ByteArrayOutputStream();

			PrintWriter writer = new PrintWriter(output);
			JFormatter f = new JFormatter(writer);
			f.setSimplePrinting();

			generable.generate(f);

			writer.flush();
			output.flush();
			String s = new String(output.toByteArray());
			output.close();
			writer.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getBTypeSource(BProject project, BClass bclass, BValuable value) {

		return JavaGenUnit.getTypeSource(project, bclass, value);
	}

	@Override
	public String getBClassHeaderSource(BProject project, BClass bclass) {

		return JavaGenUnit.getClassHeaderSource(project, bclass);
	}

	@Override
	public String getBValuableSource(BProject project, BClass bclass, BValuable value) {

		return JavaGenUnit.getValuableSource(project, bclass, value);
	}

	@Override
	public String getBUnitSource(BProject project, BClass bclass, BLogicUnit value) {
		return JavaGenUnit.getUnitSource(project, bclass, value);
	}

	@Override
	public String getBMethodSource(BProject project, BClass bclass, BMethod value) {

		return JavaGenUnit.getMethodSource(project, bclass, value);
	}

	@Override
	public String getBAllSource(BProject project, BClass bclass) {

		return JavaGenUnit.getAllSource(project, bclass);
	}

	@Override
	public Class<?> getClassByName(BProject project, String name) {
		return CodecUtils.getClassByName(name, project);
	}

	@Override
	public BVariable makeValuableByType(Class<?> cls, Type type, BProject project) {
		return CodecUtils.makeValuableByType(cls, type, project);
	}

	@Override
	public BDocIF getDoc(BProject project, String name) {
		return DecodeDocProjects.getDoc(project).getDoc(name);
	}

	@Override
	public List<String> getAnnotationSource(BProject project, BClass bclass) {
		return JavaGenUnit.getAnnotation(project, bclass);
	}
}
