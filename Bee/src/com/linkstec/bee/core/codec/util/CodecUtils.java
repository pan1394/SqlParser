package com.linkstec.bee.core.codec.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.code.BeeSource;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BeeClassLoader;
import com.linkstec.bee.core.LocalClassPathes;
import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.SourceCache;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.decode.DecodeDocProjects;
import com.linkstec.bee.core.codec.decode.DecodeGen;
import com.linkstec.bee.core.codec.decode.IDecodeResult;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.BClassImpl;
import com.linkstec.bee.core.impl.BClassVariable;
import com.linkstec.bee.core.impl.BTypeImpl;
import com.linkstec.bee.core.impl.BVariableImpl;
import com.mxgraph.util.mxResources;
import com.sun.codemodel.JMethod;

import lombok.Data;

public class CodecUtils {

	public static final String[][] TYPE_MODEL = new String[][] {
			//
			{ String.class.getName(), "string", mxResources.get("string"), BClass.TYPE_STRING },
			//
			{ int.class.getName(), "int", mxResources.get("int"), BClass.TYPE_DIGITAL },
			//
			{ boolean.class.getName(), "boolean", mxResources.get("boolean"), BClass.TYPE_LOGICAL },
			//
			{ long.class.getName(), "long", "ロング整数", BClass.TYPE_DIGITAL },
			//
			{ float.class.getName(), "float", mxResources.get("float"), BClass.TYPE_DIGITAL },
			//
			{ double.class.getName(), "double", mxResources.get("double"), BClass.TYPE_DIGITAL },
			//
			{ byte.class.getName(), "byte", "バイト", BClass.TYPE_OTHERS },
			//
			{ char.class.getName(), "char", "文字", BClass.TYPE_STRING },
			//
			{ System.class.getName(), "system", "System", BClass.TYPE_COMPLEX },
			//
			{ BigDecimal.class.getName(), "bigDecimal", mxResources.get("bigDecimal"), BClass.TYPE_DIGITAL },
			//
			{ Exception.class.getName(), "exception", "エラー", BClass.TYPE_COMPLEX },
			//
			{ SQLException.class.getName(), "SqlException", "SQLエラー", BClass.TYPE_COMPLEX },
			//
			{ List.class.getName(), "list", "リスト", BClass.TYPE_CONTAINER },
			//
			{ Hashtable.class.getName(), "hash", "ハッシュテーブル", BClass.TYPE_CONTAINER },
			//
			{ Date.class.getName(), "date", mxResources.get("date"), BClass.TYPE_COMPLEX },
			//
			{ File.class.getName(), "file", "ファイル", BClass.TYPE_COMPLEX },
			//
			{ null, "null", "Null", BClass.TYPE_COMPLEX },
			//
			{ void.class.getName(), "void", BClass.VOID, BClass.TYPE_OTHERS }

	};

	public static BClass getBClassTemplate(BProject project, String name) {

		String path = project.getSourcePath() + File.separator + name.replace('.', File.separatorChar) + ".java";
		File file = new File(path);
		if (file.exists()) {
			DecodeGen gen = new DecodeGen();
			BookModel model = new BookModel();
			IDecodeResult result = gen.decodeByPath(path, model, project);
			BClass cls = result.getBeeModel();
			return cls;
		}
		return null;
	}

	private static Hashtable<String, BClass> classCache = new Hashtable<String, BClass>();

	public static void addClassCache(BClass bclass, BProject project) {

		if (bclass instanceof BClassImpl) {
			if (!bclass.isArray()) {
				BeeSource source = SourceCache.getSource(bclass.getQualifiedName(), project);
				if (source == null) {
					if (!classCache.contains(bclass.getQualifiedName())) {

						classCache.put(bclass.getQualifiedName(), bclass.cloneAll());

					}
				}
			}
		}
	}

	public static boolean isDigital(String s) {
		String regEx = "^[0-9]{0,}";

		Pattern pattern = Pattern.compile(regEx);
		return pattern.matcher(s).matches();
	}

	private static BClass bboolean, bstring, bint, bchar, bfloat, bvoid, bshort, bexception, bobject, bdouble, blong,
			bbyte;

	public static BClass getStaticClass(Class<?> cls, BProject project) {
		BClass bclass = getClassFromJavaClass(cls, project);
		return bclass.cloneAll();
	}

	public static final BClass BException() {
		if (bexception == null) {
			bexception = getStaticClass(Exception.class, null);
		}
		return bexception.cloneAll();
	}

	public static final BClass BObject() {
		if (bobject == null) {
			bobject = getStaticClass(Object.class, null);
		}
		return bobject.cloneAll();
	};

	public static final BClass BString() {
		if (bstring == null) {
			bstring = getStaticClass(String.class, null);
		}
		return bstring.cloneAll();
	};

	public static final BClass BInt() {
		if (bint == null) {
			bint = getStaticClass(int.class, null);
		}
		return bint.cloneAll();
	};

	public static final BClass BDouble() {
		if (bdouble == null) {
			bdouble = getStaticClass(double.class, null);
		}
		return bdouble.cloneAll();
	};

	public static final BClass BFloat() {
		if (bfloat == null) {
			bfloat = getStaticClass(float.class, null);
		}
		return bfloat.cloneAll();
	};

	public static final BClass BLong() {
		if (blong == null) {
			blong = getStaticClass(long.class, null);
		}
		return blong.cloneAll();

	};

	public static final BClass BShort() {
		if (bshort == null) {
			bshort = getStaticClass(short.class, null);
		}
		return bshort.cloneAll();
	};

	public static final BClass BByte() {
		if (bbyte == null) {
			bbyte = getStaticClass(byte.class, null);
		}
		return bbyte.cloneAll();
	};

	public static final BClass BChar() {
		if (bchar == null) {
			bchar = getStaticClass(char.class, null);
		}
		return bchar.cloneAll();
	};

	public static final BClass BBoolean() {
		if (bboolean == null) {
			bboolean = getStaticClass(boolean.class, null);
		}
		return bboolean.cloneAll();

	};

	public static final BClass BVoid() {
		if (bvoid == null) {
			bvoid = getStaticClass(void.class, null);
		}
		return bvoid.cloneAll();
	};

	public static final BClass BNull = PatternCreatorFactory.createTempPattern().createClass();

	static {
		BNull.setLogicName(BClass.NULL);
		BNull.setName(BClass.NULL);
	}

	public static BVariable getNullValue() {
		BVariable BNullValue = PatternCreatorFactory.createView().createVariable();

		BNullValue.setBClass(BNull);
		BNullValue.setLogicName(BClass.NULL);
		BNullValue.setName(BClass.NULL);

		return BNullValue;
	}

	public static List<File> getAllSourceFile(BProject project) {
		return CodecUtils.getAllSourceFile(null, null, project);
	}

	public static List<File> getAllFile(List<File> list, File file, BProject project) {
		if (list == null) {
			list = new ArrayList<File>();
			String root = project.getSourcePath();
			file = new File(root);
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				getAllFile(list, f, project);
			}
		} else {

			list.add(file);

		}

		return list;
	}

	private static List<File> getAllSourceFile(List<File> list, File file, BProject project) {
		if (list == null) {
			list = new ArrayList<File>();
			String root = project.getSourcePath();
			file = new File(root);
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				getAllSourceFile(list, f, project);
			}
		} else {
			if (file.getName().endsWith(".java")) {
				list.add(file);
			}
		}

		return list;
	}

	public static List<File> getAllDesignFile(List<File> list, File file, BProject project) {
		if (list == null) {
			list = new ArrayList<File>();
			String root = project.getDesignPath();
			file = new File(root);
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				getAllDesignFile(list, f, project);
			}
		} else {
			if (file.getName().endsWith(".bee")) {
				list.add(file);
			}
		}

		return list;
	}

	public static Class<?> getPrimeryBoxClass(Class<?> name) {
		if (name == null) {
			return void.class;
		}
		if (name.equals(double.class)) {
			return Double.class;
		} else if (name.equals(int.class)) {
			return Integer.class;
		} else if (name.equals(float.class)) {
			return Float.class;
		} else if (name.equals(long.class)) {
			return Long.class;
		} else if (name.equals(short.class)) {
			return Short.class;
		} else if (name.equals(byte.class)) {
			return Byte.class;
		} else if (name.equals(void.class)) {
			return Void.class;
		} else if (name.equals(boolean.class)) {
			return Boolean.class;
		} else if (name.equals(char.class)) {
			return Character.class;
		}

		return name;
	}

	public static Class<?> getPrimeryClass(Class<?> name) {
		if (name == null) {
			return void.class;
		}
		if (name.equals(Double.class)) {
			return double.class;
		} else if (name.equals(Integer.class)) {
			return int.class;
		} else if (name.equals(Float.class)) {
			return float.class;
		} else if (name.equals(Long.class)) {
			return long.class;
		} else if (name.equals(Short.class)) {
			return short.class;
		} else if (name.equals(Byte.class)) {
			return byte.class;
		} else if (name.equals(Void.class)) {
			return void.class;
		} else if (name.equals(Boolean.class)) {
			return boolean.class;
		} else if (name.equals(Character.class)) {
			return char.class;
		}

		return name;
	}

	public static boolean isData(Class<?> cls) {

		Annotation[] annos = cls.getAnnotations();
		for (Annotation anno : annos) {
			if (anno.getClass().getName().equals(Data.class.getName())) {
				return true;
			}
		}

		Method[] ms = cls.getDeclaredMethods();
		boolean isdata = true;
		if (ms.length == 0) {
			isdata = false;
		}

		int num = 0;
		for (Method m : ms) {
			if (Modifier.isPublic(m.getModifiers())) {
				num++;
				String name = m.getName();

				if (name.startsWith("set")) {
					name = name.substring(3);
					if (m.getParameterTypes().length == 1) {
						Class<?> type = m.getParameterTypes()[0];
						try {
							Method m1 = cls.getMethod("get" + name);
							if (m1.getReturnType().equals(type)) {
								num++;
							} else {
								return false;
							}
						} catch (Exception e) {
							try {
								Method m2 = cls.getMethod("is" + name);
								if (m2.getReturnType().equals(type)) {
									num++;
								} else {
									return false;
								}
							} catch (Exception e1) {
								try {
									Method m3 = cls.getMethod("has" + name);
									if (m3.getReturnType().equals(type)) {
										num++;
									} else {
										return false;
									}
								} catch (Exception e2) {
									return false;
								}
							}
						}
					} else {
						return false;
					}
				} else if (name.startsWith("get")) {
					name = name.substring(3);
					if (m.getParameterTypes().length != 0) {
						return false;
					} else {
						Class<?> re = m.getReturnType();
						try {
							cls.getMethod("set" + name, re);
						} catch (NoSuchMethodException e) {
							return false;
						}
					}
				} else if (name.startsWith("is")) {
					name = name.substring(2);
					if (m.getParameterTypes().length != 0) {
						return false;
					} else {
						Class<?> re = m.getReturnType();
						try {
							cls.getMethod("set" + name, re);
						} catch (NoSuchMethodException e) {
							return false;
						}
					}
				} else if (name.startsWith("has")) {
					name = name.substring(3);
					if (m.getParameterTypes().length != 0) {
						return false;
					} else {
						Class<?> re = m.getReturnType();
						try {
							cls.getMethod("set" + name, re);
						} catch (NoSuchMethodException e) {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		}
		if (num == 0) {
			return false;
		}
		return isdata;
	}

	public static BClass getInnerClass(String name, BClass bclass, BProject project) {
		Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
		cls = CodecUtils.getInnerClass(name, cls, project);
		if (cls != null) {
			return CodecUtils.getClassFromJavaClass(cls, project);
		} else {

		}
		return null;
	}

	public static Class<?> getInnerClass(String name, Class<?> cls, BProject project) {
		if (cls == null) {
			return null;
		}
		while (cls != null) {
			Class<?>[] clss = null;
			try {
				clss = cls.getDeclaredClasses();
			} catch (IllegalAccessError e) {

			}
			if (clss != null) {
				for (Class<?> c : clss) {
					if (c.getSimpleName().equals(name)) {
						return c;
					}

				}
			}

			cls = cls.getSuperclass();
		}
		return null;

	}

	public static Class<?> getType(String type, BClass info, BProject project) {

		// if there is parameterized
		if (type != null && type.indexOf("<") > 0 && type.indexOf(">") > 0) {
			type = type.substring(0, type.indexOf("<"));
		}

		Class<?> cls = null;
		BeeClassLoader loader = ProjectClassLoader.getClassLoader(project);
		Hashtable<String, URL> localPathes = LocalClassPathes.getPathes();

		cls = CodecUtils.loadClass(type, loader, localPathes);

		// inner class
		if (cls == null) {
			String name = info.getQualifiedName() + "$" + type;
			cls = CodecUtils.loadClass(name, loader, localPathes);
		}
		if (cls == null) {
			if (info.getPackage() != null && info.getPackage() != null) {
				String name = info.getPackage() + "." + type;
				cls = CodecUtils.loadClass(name, loader, localPathes);
			}
		}

		// imported class
		if (cls == null) {
			List<String> names = BUtils.getImport(info, type);
			for (String name : names) {

				cls = CodecUtils.loadClass(name, loader, localPathes);

				if (cls == null) {
					int index = name.lastIndexOf('.');
					if (index > 0 && name.length() > index) {
						String packag = name.substring(0, index);
						name = packag + "$" + name.substring(index + 1);

						cls = CodecUtils.loadClass(name, loader, localPathes);
						if (cls == null) {
							cls = CodecUtils.loadClass(packag, loader, localPathes);
							if (cls != null && !cls.isEnum()) {
								cls = null;
							}
						}
					}
				}
			}

		}

		// inner class but write with com.xxx.xxx
		// inner class
		if (cls == null) {
			int index = type.lastIndexOf('.');
			if (index > 0 && type.length() > index) {
				String name = type.substring(0, index) + "$" + type.substring(index + 1);
				cls = CodecUtils.loadClass(name, loader, localPathes);
			}
		}

		// inner class at parent
		if (cls == null) {

			BValuable superClass = info.getSuperClass();
			if (superClass != null) {
				String name = superClass.getBClass().getQualifiedName() + "$" + type;
				cls = CodecUtils.loadClass(name, loader, localPathes);

				if (cls == null) {
					Class<?> sclass = CodecUtils.getClassByName(superClass.getBClass().getQualifiedName(), project);
					Class<?> ssclass = sclass.getSuperclass();
					while (ssclass != null) {
						name = ssclass.getName() + "$" + type;
						if (cls == null) {
							cls = CodecUtils.loadClass(name, loader, localPathes);
						}
						ssclass = ssclass.getSuperclass();
					}
				}
			}
		}
		// at same package
		if (cls == null) {
			String pack = info.getPackage();
			if (pack != null) {
				String name = pack + "." + type;
				cls = CodecUtils.loadClass(name, loader, localPathes);
			}
		}

		// java class
		if (cls == null) {
			if (type.indexOf(".") < 0) {
				String name = "java.lang." + type;
				cls = CodecUtils.loadClass(name, loader, localPathes);
			}
		}

		// inner class at imports
		if (cls == null) {
			List<BImport> imports = info.getImports();
			for (BImport im : imports) {
				String logicName = im.getLogicName();
				if (!logicName.endsWith(".*")) {
					String className = logicName + "$" + type;
					cls = CodecUtils.loadClass(className, loader, localPathes);
					if (cls != null) {
						break;
					}
				}
			}
		}

		if (cls == null) {
			cls = CodecUtils.getClassByName(type, project);
		}
		return cls;
	}

	private static Class<?> loadClass(String className, BeeClassLoader loader, Hashtable<String, URL> localPathes) {
		if (loader.contains(className)) {
			try {
				return loader.loadClass(className);
			} catch (ClassNotFoundException e) {
			}
		}
		if (localPathes.containsKey(className)) {
			try {
				return loader.loadClass(className);
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
	}

	public static void fillConstructors(BClass bclass, BProject project) {
		if (bclass.isPrimitive()) {
			return;
		}
		bclass.getConstructors().clear();
		String name = bclass.getQualifiedName();
		if (bclass.isArray()) {
			return;
		}
		Class<?> cls = CodecUtils.getClassByName(name, project);
		Constructor<?>[] cons = cls.getConstructors();
		for (Constructor<?> con : cons) {

			BConstructor view = PatternCreatorFactory.createView().createConstructor();
			view.setOwener(bclass);
			Parameter[] paras = con.getParameters();
			for (Parameter p : paras) {

				BParameter var = PatternCreatorFactory.createView().createParameter();
				var.setLogicName(p.getName());
				var.setVarArgs(p.isVarArgs());
				var.setModifier(p.getModifiers());
				var.setBClass(CodecUtils.getClassFromJavaClass(p.getType(), project));
				var.setParameterizedTypeValue(CodecUtils.makeValuableByType(p.getParameterizedType(), project));

				view.addParameter(var);
			}
			BVariable var = PatternCreatorFactory.createView().createVariable();

			var.setBClass(bclass.cloneAll());
			view.setReturn(var);
			bclass.getConstructors().add(view);
		}

	}

	public static BClass getClassFromJavaClass(Class<?> cls, BProject project) {
		if (cls == null) {
			return null;
		}
		String classname = cls.getName();
		BClass bcls = classCache.get(classname);
		if (bcls != null) {
			return bcls.cloneAll();
		}
		if (cls.isArray()) {
			Class<?> array = cls.getComponentType();
			BClass bclass = getClassFromJavaClass(array, project);

			// not affect the class in the cache
			bclass = bclass.cloneAll();
			BClass pressent = bclass.cloneAll();
			bclass.setArrayPressentClass(pressent);
			return bclass;
		}

		BClassImpl impl = (BClassImpl) PatternCreatorFactory.createTempPattern().createClass();

		impl.setLogicName(classname.substring(classname.lastIndexOf(".") + 1));

		String name = cls.getName();
		String p = null;
		if (name.indexOf('.') > 0) {
			name = name.substring(0, name.lastIndexOf('.'));
			p = name;
		}

		impl.setPackage(p);

		for (int i = 0; i < TYPE_MODEL.length; i++) {
			if (cls.getName().equals(TYPE_MODEL[i][0])) {
				impl.setName(TYPE_MODEL[i][2]);
				break;
			}
		}

		if (impl.getName() == null || impl.getName().equals("")) {
			if (project == null) {
				impl.setName(cls.getSimpleName());
			} else {
				String cname = DecodeDocProjects.getDoc(project).getDoc(cls.getName()).getClassDoc();
				if (cname != null && !cname.trim().equals("")) {
					impl.setName(cname);
				} else {
					impl.setName(cls.getSimpleName());
				}
			}
		}

		impl.setInterface(cls.isInterface());
		impl.setException(isException(cls));
		impl.setModifier(cls.getModifiers());
		if (cls.isEnum()) {
			impl.setEnum();
		}

		boolean isdata = CodecUtils.isData(cls);
		if (isdata) {
			impl.setData(true);
		} else {
			impl.setLogic(true);
		}

		if (!cls.isArray()) {
			addClassCache(impl, project);
		}

		BClassImpl cached = (BClassImpl) classCache.get(cls.getName());
		if (cached != null) {
			impl = cached;
		}

		TypeVariable<?>[] types = cls.getTypeParameters();
		for (TypeVariable<?> var : types) {
			impl.addParameterizedType(CodecUtils.makeValuableByType(var, project));
		}

		Class<?> superClass = cls.getSuperclass();

		if (superClass != null) {
			impl.setSuperClass(CodecUtils.makeClassValuableByType(superClass, cls.getGenericSuperclass(), project));
		}
		Class<?>[] inters = cls.getInterfaces();
		for (Class<?> in : inters) {
			impl.addInterface(CodecUtils.makeClassValuableByType(in, in.getGenericSuperclass(), project));
		}

		if (!cls.isArray()) {
			addClassCache(impl, project);
		}
		return impl;
	}

	public static boolean isException(Class<?> cls) {
		if (cls.getName().equals(Exception.class.getName())) {
			return true;
		}
		Class<?> parent = cls.getSuperclass();
		if (parent == null) {
			return false;
		}
		return isException(parent);
	}

	public static BClass getClassFromJavaClass(BProject project, String clsName) {

		Class<?> cls = BUtils.getPrimeryClass(clsName.toLowerCase());

		if (cls == null) {
			try {
				cls = Class.forName(clsName);
			} catch (ClassNotFoundException e) {

			}
		}
		if (cls == null && project != null) {
			try {
				cls = Application.loadClass(project, clsName);
			} catch (ClassNotFoundException e) {

			}
		}
		if (cls == null) {
			// for inner class
			int index = clsName.lastIndexOf('.');
			if (index > 0 && clsName.length() > index) {
				clsName = clsName.substring(0, index) + "$" + clsName.substring(index + 1);
				BClass bclass = getClassFromJavaClass(project, clsName);
				if (bclass != null) {
					index = clsName.lastIndexOf('.');
					if (index > 0 && clsName.length() > index + 1) {
						clsName = clsName.substring(index + 1);
						bclass.setLogicName(clsName);
					}
					return bclass;
				}
			}
		}

		return getClassFromJavaClass(cls, project);

	}

	public static List<BConstructor> getClassConstructors(String className, BProject project) {
		Class<?> cls = getClassByName(className, project);
		List<BConstructor> list = new ArrayList<BConstructor>();
		if (cls != null) {

			Constructor<?>[] values = cls.getConstructors();
			for (Constructor<?> value : values) {
				BConstructor c = PatternCreatorFactory.createView().createConstructor();
				CodecUtils.copyConstructorToBConstructor(value, c, project);
				list.add(c);
			}
		}

		return list;
	}

	public static BConstructor getClassConstructor(String className, List<BValuable> parameters, BProject project,
			boolean includeProtected) {
		Class<?> cls = getClassByName(className, project);

		if (cls != null) {
			Class<?>[] clss = new Class[parameters.size()];
			for (int i = 0; i < parameters.size(); i++) {

				BValuable value = parameters.get(i);
				if (value.getBClass() == null) {
					clss[i] = null;
				} else {
					String cName = parameters.get(i).getBClass().getQualifiedName();
					BValuable v = getValueClassName(parameters.get(i), project);
					if (v != null) {
						cName = v.getBClass().getQualifiedName();
					}
					clss[i] = CodecUtils.getClassByName(cName, project);
				}
			}

			Constructor<?> value = null;
			try {
				value = CodecUtils.getConsrucotr(cls, clss, project, includeProtected);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (value != null) {
				BConstructor bmethod = PatternCreatorFactory.createView().createConstructor();
				CodecUtils.copyConstructorToBConstructor(value, bmethod, project);
				return bmethod;
			}
		}

		return null;
	}

	public static BValuable getMethodReturnByDefinedType(BInvoker invoker, BMethod method, List<BValuable> paras,
			BProject project) {
		BValuable parent = invoker.getInvokeParent();
		if (parent instanceof BInvoker) {
			BInvoker in = (BInvoker) parent;
			BValuable c = in.getInvokeChild();
			BValuable p = in.getInvokeParent();
			if (c instanceof BMethod) {
				BMethod m = (BMethod) c;
				BVariable var = CodecUtils.getValueable(p, m.getReturn(), project);
				if (var != null) {
					parent = var;
				}
			} else {
				parent = c;
			}

		}

		BValuable targetValue = method.getReturn();

		BVariable var = CodecUtils.getValueable(parent, targetValue, project);
		if (var != null) {
			if (var.getBClass().getQualifiedName().equals(Object.class.getName())) {
				List<BType> types = method.getDefinedTypes();
				for (BType type : types) {
					if (type.isTypeVariable()) {
						return parent;
					}
				}
			}
			return var;
		} else {
			// <T> T[] toArray(T[])
			BType type = targetValue.getParameterizedTypeValue();

			List<BParameter> methodParas = method.getParameter();
			if (type != null && paras != null) {
				if (type.isArray()) {
					BType arrayType = type.getArrayPressentClass();
					if (!(arrayType instanceof BClass)) {
						int i = 0;
						for (BValuable para : paras) {
							BClass pbclass = para.getBClass();
							BParameter methodPara = methodParas.get(i);
							BType methodParaType = methodPara.getParameterizedTypeValue();
							if (pbclass.isArray() && methodParaType.isArray()) {

								BType methodParameterType = methodParaType.getArrayPressentClass();
								if (arrayType.getLogicName().equals(methodParameterType.getLogicName())) {
									return para;
								}
							}
							i++;
						}
					}

				} else {
					if (!(type instanceof BClass)) {
						int i = 0;
						for (BValuable para : paras) {

							BParameter methodPara = methodParas.get(i);
							BType methodParaType = methodPara.getParameterizedTypeValue();

							if (methodParaType != null) {

								if (methodParaType.isRawType()) {
									List<BType> types = methodParaType.getParameterizedTypes();

									for (BType t : types) {
										if (t.getLogicName().equals(type.getLogicName())) {
											if (para instanceof BVariable) {
												BVariable paraV = (BVariable) para;
												String logicName = paraV.getLogicName();
												if (logicName.endsWith(".class")) {
													logicName = logicName.substring(0, logicName.length() - 6);
													BClass bc = CodecUtils.getClassFromJavaClass(project, logicName);
													BVariable result = PatternCreatorFactory.createView()
															.createVariable();
													result.setBClass(bc);
													result.setLogicName(bc.getLogicName());
													return result;

												}
											}
											return para;
										}

									}
								}

								if (type.getLogicName().equals(methodParaType.getLogicName())) {
									return para;
								}
							}

							i++;
						}
					}
				}
			}
		}

		// if (var.getBClass().getQualifiedName().equals(Object.class.getName())) {
		// List<BType> types = method.getDefinedTypes();
		// for (BType type : types) {
		// if (type.isTypeVariable()) {
		// return parent;
		// }
		// }
		// }

		return null;
	}

	private static BValuable getValueClassName(BValuable value, BProject project) {
		BClass bclass = value.getBClass();

		String cName = bclass.getQualifiedName();
		if (cName.equals(Object.class.getName()) || cName.equals(Object[].class.getName())) {
			if (value instanceof BInvoker) {
				BInvoker invoker = (BInvoker) value;
				BValuable child = invoker.getInvokeChild();
				if (child instanceof BMethod) {
					BMethod method = (BMethod) child;
					List<BValuable> paras = invoker.getParameters();
					return CodecUtils.getMethodReturnByDefinedType(invoker, method, paras, project);
				}
			} else if (value instanceof BVariable) {
				BVariable var = (BVariable) value;
				BType type = var.getParameterizedTypeValue();
				if (type != null && type.isTypeVariable()) {
					List<String> bounds = type.getBounds();
					if (bounds.size() == 1) {
						String b = bounds.get(0);
						BClass bc = CodecUtils.getClassFromJavaClass(project, b);
						if (bc != null) {
							BVariable result = PatternCreatorFactory.createView().createVariable();
							result.setBClass(bc);
							result.setLogicName(bc.getLogicName());
							return result;
						}
					}
				}
			}
		}
		return value;

	}

	public static BMethod getMethod(BClass bclass, String name, List<BValuable> parameters, BProject project) {

		List<BMethod> methods = bclass.getMethods();
		for (BMethod m : methods) {

			if (m.getLogicName().equals(name)) {
				List<BParameter> list = m.getParameter();
				if (list.size() == 0 && (parameters == null || parameters.isEmpty())) {
					return m;
				} else if (parameters != null && list.size() == parameters.size()) {
					int count = parameters.size();
					boolean hit = true;

					for (int i = 0; i < count; i++) {
						BValuable obj = parameters.get(i);
						BParameter var = list.get(i);

						if (obj != null && obj.getBClass() != null) {
							String cName = obj.getBClass().getQualifiedName();
							BValuable v = getValueClassName(obj, project);
							if (v != null) {
								cName = v.getBClass().getQualifiedName();
							}
							String pName = var.getBClass().getQualifiedName();
							if (var.isVarArgs()) {
								BClass ArrayClass = (BClass) var.getBClass().getArrayPressentClass();
								if (ArrayClass != null) {
									pName = ArrayClass.getQualifiedName();
								}
							}
							if (!CodecUtils.isClassMatch(pName, cName, project)) {
								hit = false;

							}
						}
					}
					if (hit) {
						m = (BMethod) m.cloneAll();
						m.setLogicBody(null);
						return m;
					}
				} else if (parameters != null && list.size() != parameters.size()) {
					if (list.size() > 0) {
						if (list.size() - parameters.size() == 1) {
							BParameter last = list.get(list.size() - 1);
							if (last.isVarArgs()) {
								int count = parameters.size();
								boolean hit = true;

								for (int i = 0; i < count - 1; i++) {
									BValuable obj = parameters.get(i);
									BParameter var = list.get(i);

									if (obj != null && obj.getBClass() != null) {
										String cName = getValueClassName(obj, project).getBClass().getQualifiedName();
										String pName = var.getBClass().getQualifiedName();
										if (var.isVarArgs()) {
											BClass ArrayClass = (BClass) var.getBClass().getArrayPressentClass();
											if (ArrayClass != null) {
												pName = ArrayClass.getQualifiedName();
											}
										}
										if (!CodecUtils.isClassMatch(pName, cName, project)) {
											hit = false;

										}
									}
								}
								if (hit) {
									m = (BMethod) m.cloneAll();
									m.setLogicBody(null);
									return m;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static BMethod getClassMethod(String className, String childName, List<BValuable> parameters,
			BProject project, boolean includeSuperProteced) {

		Class<?> cls = getClassByName(className, project);

		if (cls != null) {

			Method value = null;
			Class<?>[] clss = new Class[0];
			if (parameters != null) {
				clss = new Class[parameters.size()];
				for (int i = 0; i < parameters.size(); i++) {
					if (parameters.get(i).getBClass() == null) {
						clss[i] = null;
					} else {
						String cName = parameters.get(i).getBClass().getQualifiedName();
						BValuable v = getValueClassName(parameters.get(i), project);
						if (v != null) {
							cName = v.getBClass().getQualifiedName();
						}
						clss[i] = CodecUtils.getClassByName(cName, project);
					}
				}
			}

			try {
				value = CodecUtils.getMethod(cls, childName, clss, project, includeSuperProteced);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value != null) {
				BMethod bmethod = PatternCreatorFactory.createTempPattern().createMethod();
				CodecUtils.copyMethodToBMethod(className, value, bmethod, project);

				String mName = DecodeDocProjects.getDoc(project).getDoc(className).getMethodDoc(bmethod);
				if (mName != null && !mName.equals("")) {
					bmethod.setName(mName);
				} else {
					bmethod.setName(bmethod.getLogicName());
				}
				return bmethod;
			}
		}
		if (cls != null && cls.isInterface()) {
			return CodecUtils.getClassMethod(Object.class.getName(), childName, parameters, project, false);
		}
		return null;
	}

	public static boolean bclassContainsMethod(BClass bclass, BMethod method, BProject project,
			boolean includeSuperProtected) {
		if (bclass == null || method == null) {
			return false;
		}
		Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
		if (cls != null) {
			List<BParameter> parameters = method.getParameter();
			Class<?>[] clss = new Class[parameters.size()];
			for (int i = 0; i < parameters.size(); i++) {
				clss[i] = CodecUtils.getClassByName(parameters.get(i).getBClass().getQualifiedName(), project);
			}

			try {
				return CodecUtils.getMethod(cls, method.getLogicName(), clss, project, includeSuperProtected) != null;
			} catch (Exception e) {
			}
		}
		return false;
	}

	private static Method getMethod(Class<?> cls, String name, Class<?>[] parameters, BProject project,
			boolean includeSuperProteced) {

		Method[] methods = cls.getDeclaredMethods();
		for (Method method : methods) {

			if (method.getName().equals(name)) {
				Class<?>[] paras = method.getParameterTypes();
				Parameter[] paraMeters = method.getParameters();
				if (CodecUtils.isParameterMatch(paras, paraMeters, parameters, project)) {
					// TODO if inherited from super class it will be volatile?what about if the
					// volatile decleared?
					if (!Modifier.isVolatile(method.getModifiers())) {
						return method;
					}
				}
			}
		}
		if (cls.isInterface()) {
			Class<?>[] interfaces = cls.getInterfaces();
			for (Class<?> c : interfaces) {
				Method m = CodecUtils.getMethod(c, name, parameters, project, includeSuperProteced);
				if (m != null) {
					return m;
				}
			}
		}
		Class<?> superClass = cls.getSuperclass();
		if (superClass != null) {
			Method m = CodecUtils.getSuperPublicMethod(cls, name, parameters, project);
			if (m != null) {
				return m;
			}
		}
		if (includeSuperProteced) {
			if (superClass != null) {
				return CodecUtils.getSuperProtecedMethod(superClass, name, parameters, project);
			}
		}
		return null;
	}

	private static Method getSuperPublicMethod(Class<?> cls, String name, Class<?>[] parameters, BProject project) {
		Method[] methods = cls.getDeclaredMethods();
		for (Method method : methods) {
			if (Modifier.isPublic(method.getModifiers())) {
				if (method.getName().equals(name)) {
					Class<?>[] paras = method.getParameterTypes();
					Parameter[] paraMeters = method.getParameters();
					if (CodecUtils.isParameterMatch(paras, paraMeters, parameters, project)) {
						return method;
					}
				}
			}
		}

		Class<?> superClass = cls.getSuperclass();
		if (superClass != null) {
			return CodecUtils.getSuperPublicMethod(superClass, name, parameters, project);
		}
		if (cls.isInterface()) {
			return CodecUtils.getSuperPublicMethod(Object.class, name, parameters, project);
		}
		return null;
	}

	private static Method getSuperProtecedMethod(Class<?> cls, String name, Class<?>[] parameters, BProject project) {
		Method[] methods = cls.getDeclaredMethods();
		for (Method method : methods) {
			if (Modifier.isProtected(method.getModifiers())) {
				if (method.getName().equals(name)) {
					Class<?>[] paras = method.getParameterTypes();
					Parameter[] paraMeters = method.getParameters();
					if (CodecUtils.isParameterMatch(paras, paraMeters, parameters, project)) {
						return method;
					}
				}
			}
		}

		Class<?> superClass = cls.getSuperclass();
		if (superClass != null) {
			return CodecUtils.getSuperProtecedMethod(superClass, name, parameters, project);
		}
		if (cls.isInterface()) {
			return CodecUtils.getSuperProtecedMethod(Object.class, name, parameters, project);
		}
		return null;
	}

	private static boolean isParameterMatch(Class<?>[] paras, Parameter[] paraMeters, Class<?>[] parameters,
			BProject project) {

		if (paraMeters.length > 0) {
			Parameter last = paraMeters[paraMeters.length - 1];
			if (!last.isVarArgs()) {
				if (paras.length == parameters.length) {
					boolean match = true;
					for (int i = 0; i < paras.length; i++) {
						Class<?> target = paras[i];
						Class<?> object = parameters[i];
						if (object != null) {
							if (!CodecUtils.isClassMatch(target, object, project)) {
								match = false;
								break;
							}
						}
					}
					if (match) {
						return true;
					}
				}
			} else {
				// String...
				boolean match = true;
				for (int i = 0; i < paras.length - 1; i++) {
					Class<?> target = paras[i];
					Class<?> object = parameters[i];
					if (object != null) {
						if (!CodecUtils.isClassMatch(target, object, project)) {
							match = false;
							break;
						}
					}
				}
				if (match) {
					Class<?> target = paras[paras.length - 1];
					Class<?> component = target.getComponentType();

					for (int i = paras.length - 1; i < parameters.length; i++) {
						Class<?> object = parameters[i];
						if (object != null) {
							if (object.isArray()) {
								object = object.getComponentType();
							}
							if (!CodecUtils.isClassMatch(component, object, project)) {
								match = false;
								break;
							}
						}
					}
				}
				if (match) {
					return true;
				}
			}
		} else {
			if (parameters.length == 0) {
				return true;
			}
		}
		return false;
	}

	private static Constructor<?> getConsrucotr(Class<?> cls, Class<?>[] parameters, BProject project,
			boolean includeProtected) {
		Constructor<?>[] methods = cls.getConstructors();
		if (includeProtected) {
			Constructor<?>[] protecteds = cls.getDeclaredConstructors();
			Constructor<?>[] publics = methods;
			methods = new Constructor<?>[protecteds.length + publics.length];
			for (int i = 0; i < methods.length; i++) {
				if (i < publics.length) {
					methods[i] = publics[i];
				} else {
					methods[i] = protecteds[i - publics.length];
				}
			}
		}

		for (Constructor<?> method : methods) {

			Class<?>[] paras = method.getParameterTypes();
			Parameter[] paraMeters = method.getParameters();
			if (cls.isMemberClass() || cls.isEnum()) {
				Class<?>[] paras1 = new Class<?>[paras.length - 1];
				Parameter[] paraMeters1 = new Parameter[paraMeters.length - 1];
				for (int i = 1; i < paras.length; i++) {
					paras1[i - 1] = paras[i];
					paraMeters1[i - 1] = paraMeters[i];
				}
				paras = paras1;
				paraMeters = paraMeters1;

			}
			if (CodecUtils.isParameterMatch(paras, paraMeters, parameters, project)) {
				return method;
			}
		}
		if (methods.length == 1) {
			return methods[0];
		}
		return null;
	}

	// parameter might be null,so do not use it to judge
	public static BValuable getDataClassMethodSetterParameterType(Class<?> cls, String name, BProject project) {

		Method[] methods = cls.getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(name)) {

				Parameter[] paraMeters = method.getParameters();
				Parameter p = paraMeters[0];

				BVariable var = PatternCreatorFactory.createView().createVariable();
				var.setBClass(CodecUtils.getClassFromJavaClass(p.getType(), project));
				var.setParameterizedTypeValue(CodecUtils.makeValuableByType(p.getParameterizedType(), project));
				return var;
			}
		}
		if (cls.getSuperclass() != null) {
			return CodecUtils.getDataClassMethodSetterParameterType(cls.getSuperclass(), name, project);
		}
		return null;
	}

	public static BValuable getMethodRuturnType(String className, String childName, List<BValuable> parameters,
			BProject project, boolean includeSuperProtected) {

		BMethod method = CodecUtils.getClassMethod(className, childName, parameters, project, includeSuperProtected);
		if (method != null) {
			return method.getReturn();
		}
		return null;
	}

	public static void main(String[] args) {

		Class<?> cls = StringBuilder.class;
		try {
			// cls.getMethod("getList", Hashtable.class, List.class, Object.class);
			Method[] methods = cls.getDeclaredMethods();
			for (Method method : methods) {

				Parameter[] paras = method.getParameters();
				for (Parameter para : paras) {
					Type type = para.getParameterizedType();
					System.out.println("--------type name --------------");
					System.out.println(type.getTypeName());
					if (type instanceof ParameterizedType) {

						System.out.println("--------ParameterizedType --------------");
						ParameterizedType p = (ParameterizedType) type;
						Type[] types = p.getActualTypeArguments();
						System.out.println(p.getOwnerType());
						System.out.println(p.getRawType());
						for (Type t : types) {

							System.out.println(t.getClass().getName());
							System.out.println(t.getTypeName());
						}
					} else if (type instanceof TypeVariable) {
						System.out.println("--------TypeVariable --------------");

						TypeVariable<?> var = (TypeVariable<?>) type;
						System.out.println(var.getTypeName());
						Type[] bounds = var.getBounds();
						for (Type t : bounds) {
							System.out.println(t.getClass().getName());
							System.out.println(t.getTypeName());
						}
					}

				}
				System.out.println("--------return --------------");
				Type type = method.getGenericReturnType();
				System.out.println(type.getClass().getName());
				System.out.println(type.getTypeName());
				System.out.println(method.getReturnType().getName());

				System.out.println("----------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isClassMatch(String target, String object, BProject project) {
		if (object.equals("null")) {
			return true;
		}
		if (target != null && object != null) {
			if (target.equals(object)) {
				return true;
			}

			if (target.equals("Lambd") || object.equals("Lambd")) {
				return true;
			}

			Class<?> tclass = getClassByName(target, project);
			Class<?> oclass = getClassByName(object, project);
			if (tclass == null || oclass == null) {
				return false;
			}
			return CodecUtils.isClassMatch(tclass, oclass, project);
		} else {
			return true;
		}

	}

	public static Class<?> getAllParent(List<Class<?>> list) {
		if (list.size() > 0) {
			Class<?> cls = list.get(0);
			List<Class<?>> clss = CodecUtils.getAllClass(cls, new ArrayList<Class<?>>());
			for (Class<?> possible : clss) {

				boolean get = true;
				for (Class<?> target : list) {
					if (!possible.isAssignableFrom(target)) {
						get = false;
					}
				}
				if (get) {
					return possible;
				}
			}
		}
		return Object.class;
	}

	private static List<Class<?>> getAllClass(Class<?> cls, List<Class<?>> list) {
		Class<?> superClass = cls.getSuperclass();
		if (superClass != null && !superClass.equals(Object.class)) {
			if (!list.contains(superClass)) {
				list.add(superClass);
			}
			getAllClass(superClass, list);
		}
		Class<?>[] clss = cls.getInterfaces();
		for (Class<?> c : clss) {
			if (!list.contains(c)) {
				list.add(c);
			}
			getAllClass(c, list);
		}

		return list;
	}

	public static boolean isClassMatch(Class<?> tclass, Class<?> oclass, BProject project) {
		if (oclass == null && !tclass.isPrimitive()) {
			return true;
		}

		if (tclass.isArray() && oclass.isArray()) {
			Class<?> target = tclass.getComponentType();
			Class<?> object = oclass.getComponentType();
			return CodecUtils.isClassMatch(target, object, project);
		}

		Class<?> pt = CodecUtils.getPrimeryClass(tclass);
		Class<?> ot = CodecUtils.getPrimeryClass(oclass);

		Class<?> npt = CodecUtils.getPrimeryBoxClass(pt);
		Class<?> not = CodecUtils.getPrimeryBoxClass(ot);

		if (npt.getName().equals(Class.class.getName()) && not.getClass().getName().equals(Class.class.getName())) {
			return true;
		}

		if (Number.class.isAssignableFrom(npt) && Number.class.isAssignableFrom(not)) {
			// TODO
			return true;
		}

		if (npt.isAssignableFrom(not)) {
			return true;
		}

		if (npt.equals(String.class) && not.equals(Character.class)) {
			return true;
		}

		if (pt.isArray() && ot.isArray()) {
			String target = pt.getName().substring(2, pt.getName().length() - 1);
			String object = ot.getName().substring(2, ot.getName().length() - 1);
			return isClassMatch(target, object, project);
		}
		return false;
	}

	public static BValuable getVariableType(String className, String childName, BProject project, boolean findPrivite) {

		Class<?> cls = CodecUtils.getClassByName(className, project);

		if (cls != null) {
			if (cls.isArray()) {
				if (childName.equals("length")) {
					BVariable var = PatternCreatorFactory.createView().createVariable();
					var.setBClass(CodecUtils.BInt());
					var.setLogicName(childName);
					return var;
				}
			}
			if (cls.isEnum()) {
				BVariable var = PatternCreatorFactory.createView().createVariable();
				var.setBClass(CodecUtils.getClassFromJavaClass(cls, project));
				var.setLogicName(childName);
				return var;
			}
			try {

				Field f = cls.getField(childName);
				Type t = f.getGenericType();
				BVariable var = CodecUtils.makeValuableByType(f.getType(), t, project);
				var.setClass(false);
				var.setLogicName(childName);
				return var;
			} catch (Exception e) {

			}

			if (findPrivite) {
				try {

					Field f = cls.getDeclaredField(childName);
					Type t = f.getGenericType();
					BVariable var = CodecUtils.makeValuableByType(f.getType(), t, project);
					var.setClass(false);
					var.setLogicName(childName);
					return var;
				} catch (Exception e) {

				}
			}

		}
		return null;
	}

	public static BParameter makeParameterByType(Class<?> cls, Type type, BProject project) {
		BParameter var = PatternCreatorFactory.createView().createParameter();
		BClass bclass = CodecUtils.getClassFromJavaClass(cls, project);

		var.setBClass(bclass);
		var.setParameterizedTypeValue(CodecUtils.makeValuableByType(type, project));

		try {
			var.setLogicName(cls.getSimpleName());
		} catch (IllegalAccessError e) {
			var.setLogicName(cls.getName().substring(cls.getName().indexOf("$") + 1));
		}

		var.setName(bclass.getName());
		var.setClass(true);
		return var;
	}

	public static BVariable makeValuableByType(Class<?> cls, Type type, BProject project) {

		BVariable var = PatternCreatorFactory.createView().createVariable();
		BClass bclass = CodecUtils.getClassFromJavaClass(cls, project);

		var.setBClass(bclass);

		var.setParameterizedTypeValue(CodecUtils.makeValuableByType(type, project));

		try {
			var.setLogicName(cls.getSimpleName());
		} catch (IllegalAccessError e) {
			var.setLogicName(cls.getName().substring(cls.getName().indexOf("$") + 1));
		}

		var.setName(bclass.getName());
		var.setClass(true);
		return var;

	}

	public static BClass getBClassInValueTypeback(BValuable value, BValuable defined, BProject project) {
		// defined -> Map<String,Integer>

		// types -> <E,F>
		List<BType> types = defined.getBClass().getParameterizedTypes();

		// type <String,Integer>
		BType type = defined.getParameterizedTypeValue();

		if (type == null) {
			return null;
		}

		// values String,Integer
		List<BType> values = type.getParameterizedTypes();

		// stype -> E
		BType stype = value.getParameterizedTypeValue();

		int i = 0;
		for (BType t : types) {
			if (t.getLogicName().equals(stype.getLogicName())) {
				BType typeValue = values.get(i);
				if (typeValue instanceof BClass) {
					return (BClass) typeValue.cloneAll();
				}
			}
			i++;
		}
		return null;
	}

	public static BClass getBClassInValueType(BValuable value, BValuable defined, BProject project) {

		BValuable v = CodecUtils.getValueable(defined, value, project);
		if (v != null) {
			return v.getBClass();
		}
		return null;
	}

	public static BVariable getValueable(BValuable classValue, BValuable targetValue, BProject project) {

		if (classValue == null) {
			return null;
		}
		// target type
		BType targetType = targetValue.getParameterizedTypeValue();
		//
		BVariable v = CodecUtils.getValueable(targetType, classValue, project);
		if (v == null) {

			BType ptype = classValue.getParameterizedTypeValue();
			if (ptype == null) {
				return null;
			}
			BType o = ptype.getOwnerType();
			List<BType> types = null;
			if (o != null) {
				types = o.getParameterizedTypes();

				int index = -1;
				int run = 0;
				for (BType type : types) {
					if (type.isTypeVariable()) {
						if (type.getLogicName().equals(targetType.getLogicName())) {
							index = run;
						}
						run++;
					}
				}
				if (index != -1) {
					run = 0;
					for (BType type : types) {
						if (type.isParameterValue()) {
							if (run == index) {
								BType found = type;

								BVariable var = PatternCreatorFactory.createView().createVariable();
								if (found instanceof BClass) {
									BClass clss = (BClass) found;
									BType raw = clss.cloneAll();

									clss = CodecUtils.getClassFromJavaClass(project, clss.getQualifiedName());
									var.setBClass(clss);

									raw.setRowType(true);
									var.setParameterizedTypeValue(raw);
								} else {
									var.setBClass(CodecUtils.BObject().cloneAll());
									var.setParameterizedTypeValue(found);
								}

								return var;
							}
							run++;
						}
					}
				}
			}
		} else {
			return v;
		}
		return null;

	}

	private static BVariable getValueable(BType targetType, BValuable classValue, BProject project) {
		if (targetType == null) {
			return null;
		}

		if (classValue == null) {
			return null;
		}

		// parent parameters
		BType ptype = classValue.getParameterizedTypeValue();

		// parent class
		BClass vclass = classValue.getBClass();

		if (ptype != null) {

			// parent parameters
			List<BType> paras = CodecUtils.getTypeValues(ptype);
			// parent class parameters
			List<BType> classParalist = vclass.getParameterizedTypes();

			// class List<E>, List list
			if (targetType.isRawType()) {
				BVariable var = PatternCreatorFactory.createView().createVariable();
				var.setBClass(CodecUtils.getClassFromJavaClass(project, ((BClass) targetType).getQualifiedName()));
				BTypeImpl rawType = new BTypeImpl();
				if (ptype.getOwnerType() != null) {
					rawType.setOwnerType(ptype.getOwnerType());
				} else {
					rawType.setOwnerType(targetType.getOwnerType());
				}
				rawType.setContainer();
				var.setParameterizedTypeValue(rawType);
				List<BType> types = targetType.getParameterizedTypes();
				for (BType sub : types) {
					boolean OK = false;
					if (paras.size() == classParalist.size()) {
						for (int i = 0; i < classParalist.size(); i++) {
							BType paraname = classParalist.get(i);
							if (sub.getLogicName().equals(paraname.getLogicName())) {
								BType type = paras.get(i);
								OK = true;
								rawType.addParameterizedType(type);
							}
						}
					}
					if (!OK) {
						return null;
					}
				}
				return var;
			} else {
				if (paras.size() == classParalist.size()) {
					for (int i = 0; i < classParalist.size(); i++) {
						BType paraname = classParalist.get(i);

						if (targetType.getLogicName().equals(paraname.getLogicName())) {
							BType type = paras.get(i);
							BVariable var = PatternCreatorFactory.createView().createVariable();
							if (type instanceof BClass) {
								BClass clss = (BClass) type;
								BType raw = clss.cloneAll();

								clss = CodecUtils.getClassFromJavaClass(project, clss.getQualifiedName());
								var.setBClass(clss);

								// raw.setRowType(true);

								raw.setOwnerType(ptype.getOwnerType());
								var.setParameterizedTypeValue(raw);
							} else {
								var.setBClass(CodecUtils.BObject().cloneAll());
								var.setParameterizedTypeValue(type);
							}

							return var;
						}
					}

				}

			}

		}

		BValuable superValue = vclass.getSuperClass();
		if (superValue != null) {
			// OK?
			if (ptype != null && superValue.getParameterizedTypeValue() != null) {
				superValue.getParameterizedTypeValue().setOwnerType(ptype.getOwnerType());
			}
			BVariable var = CodecUtils.getValueable(targetType, superValue, project);
			if (var != null) {

				BClass bclas = var.getBClass();
				if (bclas != null && bclas.getQualifiedName().equals(Object.class.getName())) {
					BType found = var.getParameterizedTypeValue();
					BVariable v = CodecUtils.getValueable(found, classValue, project);
					if (v != null) {
						return v;
					}
				} else {
					if (ptype != null) {
						// var.getParameterizedTypeValue().setOwnerType(ptype.getOwnerType());
					}
					return var;
				}
			}
		}

		return null;

	}

	public static List<BType> getTypeValues(BType type) {

		List<BType> paras = new ArrayList<BType>();
		if (type == null) {
			return paras;
		}
		List<BType> values = type.getParameterizedTypes();
		for (BType b : values) {
			if (b.isParameterValue()) {

				if (b.isContainer()) {
					paras.addAll(getTypeValues(b));
				} else {
					paras.add(b);
				}
			}
		}
		return paras;
	}

	public static BValuable makeClassValuableByType(Class<?> cls, Type type, BProject project) {
		BClassVariable var = new BClassVariable();
		var.setBClass(cls.getName(), project);
		if (!cls.equals(type)) {
			var.setParameterizedTypeValue(CodecUtils.makeValuableByType(type, project));
		}
		return var;
	}

	public static BType makeValuableByType(Type type, BProject project) {

		BType btype = new BTypeImpl();
		if (type == null) {
			return null;
		}

		btype.setLogicName(type.getTypeName());

		if (type instanceof GenericArrayType) {
			GenericArrayType gt = (GenericArrayType) type;

			Type t = gt.getGenericComponentType();
			btype.setArrayPressentClass(makeValuableByType(t, project));

		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;

			Type t1 = pt.getRawType();
			// temporary TODO
			if (!t1.equals(Enum.class)) {
				btype = CodecUtils.makeValuableByType(t1, project);
				btype.setRowType(true);
				// btype.clearParameterTypes();
				Type[] ps = pt.getActualTypeArguments();
				for (Type t : ps) {
					BType value = CodecUtils.makeValuableByType(t, project);
					value.setParameterValue(true);
					btype.addParameterizedType(value);
				}
			}

			Type ower = pt.getOwnerType();
			if (ower != null) {
				btype.setOwnerType(CodecUtils.makeValuableByType(ower, project));
			}

		} else if (type instanceof TypeVariable) {
			TypeVariable<?> tvar = (TypeVariable<?>) type;
			Type[] bounds = tvar.getBounds();
			for (Type bt : bounds) {
				btype.addBound(bt.getTypeName());
			}
			((BTypeImpl) btype).setTypeVariable();
		} else if (type instanceof Class) {
			Class<?> cls = (Class<?>) type;
			btype = CodecUtils.getClassFromJavaClass(cls, project);
		}
		return btype;

	}

	public static BVariable getReachableStaticVariable(String className, String childName, BProject project) {

		Class<?> cls = getClassByName(className, project);
		if (cls != null) {
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				if (Modifier.isStatic(f.getModifiers())) {
					if (f.getName().equals(childName)) {
						Type t = f.getGenericType();

						BVariable var = CodecUtils.makeValuableByType(f.getType(), t, project);
						var.setClass(false);
						var.setLogicName(childName);

						String mName = DecodeDocProjects.getDoc(project).getDoc(className).getVariableDoc(childName);
						if (mName != null && !mName.equals("")) {
							var.setName(mName);
						} else {
							var.setName(childName);
						}
						return var;
					}
				}
			}
			if (cls.getSuperclass() != null) {
				return getReachableStaticVariable(cls.getSuperclass().getName(), childName, project);
			}
		}
		return null;
	}

	public static BVariable getReachableVariable(String className, String childName, BProject project) {
		if (className.startsWith("[") && className.endsWith(";")) {
			if (childName.equals("length")) {
				BVariable var = PatternCreatorFactory.createView().createVariable();
				var.setBClass(BInt());
				var.setName("長さ");
				var.setLogicName("length");
				return var;
			}
		}
		Class<?> cls = getClassByName(className, project);
		if (cls != null) {
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				if (!Modifier.isPrivate(f.getModifiers())) {
					if (f.getName().equals(childName)) {

						BVariable var = CodecUtils.makeValuableByType(f.getType(), f.getGenericType(), project);
						var.setClass(false);
						var.setLogicName(childName);
						var.setName(childName);
						String typeName = DecodeDocProjects.getDoc(project).getDoc(cls.getName()).getClassDoc();
						if (typeName != null && !typeName.equals("")) {
							var.setName(typeName);
						}

						String mName = DecodeDocProjects.getDoc(project).getDoc(className).getVariableDoc(childName);
						if (mName != null && !mName.equals("")) {
							var.setName(mName);
						} else {
							var.setName(childName);
						}

						return var;
					}
				}
			}
			if (cls.getSuperclass() != null) {
				return getReachableVariable(cls.getSuperclass().getName(), childName, project);
			}
		}
		return null;
	}

	public static Class<?> getClassByName(String name, BProject project) {

		if (name == null) {
			return null;
		}

		if (name.equals("int")) {
			return int.class;
		} else if (name.equals("long")) {
			return long.class;
		} else if (name.equals("float")) {
			return float.class;
		} else if (name.equals("double")) {
			return double.class;
		} else if (name.equals("boolean")) {
			return boolean.class;
		} else if (name.equals("short")) {
			return short.class;
		} else if (name.equals("byte")) {
			return byte.class;
		} else if (name.equals("char")) {
			return char.class;
		} else if (name.equals("void")) {
			return void.class;
		}

		BeeClassLoader loader = ProjectClassLoader.getClassLoader(project);
		// if (loader.contains(name)) {
		try {
			Class<?> cls = loader.loadClass(name);
			if (cls != null) {
				return cls;
			}
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		// }

		if (name.indexOf(".") > 0 || name.startsWith("[")) {
			try {
				return Class.forName(name);
			} catch (ClassNotFoundException e) {
			}
		}

		return null;
	}

	public static boolean isString(String s) {
		String regEx = "[a-zA-Z_]";

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	public static boolean isInteger(String s) {
		String regEx = "[0-9]";

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	public static boolean isFloat(String s) {
		String regEx = "[1-9]\\d*\\.?\\d*";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	public static String getNameByBclass(BClass clz) {

		if (clz != null) {
			BClass[] blass = getAllBClasses();
			for (int i = 0; i < blass.length; i++) {
				if (clz.getQualifiedName() != null) {
					if (clz.getQualifiedName().equals(blass[i].getQualifiedName())) {
						return blass[i].getName();
					}
				}
			}
			return clz.getQualifiedName();
		}
		return null;

	}

	public static void fillStaticMethod(BClass clz, BProject project) {
		clz.getMethods().clear();
		Class<?> cls = CodecUtils.getClassByName(clz.getQualifiedName(), project);
		Method[] fs = cls.getMethods();

		for (Method f : fs) {
			int m = f.getModifiers();
			if (Modifier.isStatic(m)) {

				BMethod mm = PatternCreatorFactory.createView().createMethod();
				CodecUtils.copyMethodToBMethod(clz.getQualifiedName(), f, mm, project);
				DecodeDocProjects.getDoc(project).getDoc(clz.getQualifiedName()).makeMothedModel(mm);
				clz.getMethods().add(mm);
			}
		}
	}

	public static void fillMethod(BClass clz, BProject project) {
		clz.getMethods().clear();
		String name = clz.getQualifiedName();
		if (clz.isArray()) {
			BMethod mm = PatternCreatorFactory.createView().createMethod();
			mm.setLogicName("length");
			mm.setName("長さを取得する");
			clz.getMethods().add(mm);
			return;
		} else if (name.startsWith("[") && name.endsWith(";")) {
			BMethod mm = PatternCreatorFactory.createView().createMethod();
			mm.setLogicName("length");
			mm.setName("長さを取得する");
			clz.getMethods().add(mm);
			return;
		}
		Class<?> cls = CodecUtils.getClassByName(name, project);

		Method[] fs = cls.getMethods();
		for (Method f : fs) {
			BMethod mm = PatternCreatorFactory.createView().createMethod();
			CodecUtils.copyMethodToBMethod(clz.getQualifiedName(), f, mm, project);
			DecodeDocProjects.getDoc(project).getDoc(clz.getQualifiedName()).makeMothedModel(mm);
			clz.getMethods().add(mm);
		}
	}

	public static void copyMethodToBMethod(String mbclass, Method m, BMethod b, BProject project) {

		b.setLogicName(m.getName());
		b.setName(m.getName());
		Parameter[] paras = m.getParameters();
		for (Parameter p : paras) {

			BParameter var = CodecUtils.makeParameterByType(p.getType(), p.getParameterizedType(), project);
			var.setLogicName(p.getName());
			var.setModifier(p.getModifiers());
			var.setVarArgs(p.isVarArgs());

			b.addParameter(var);
		}

		// if (m.getName().equals("messageInterpolator")) {
		// Debug.a();
		// }

		Type t = m.getGenericReturnType();
		b.setModifier(m.getModifiers());

		if (mbclass != null) {
			// jre1.8' bug on StringBuilder
			if (mbclass.equals(StringBuilder.class.getName()) && m.getName().equals("append")) {
				t = StringBuilder.class;
			}
		}

		BVariable var = CodecUtils.makeValuableByType(m.getReturnType(), t, project);
		b.setReturn(var);

		Class<?>[] types = m.getExceptionTypes();
		for (Class<?> cls : types) {
			BVariable v = new BVariableImpl();
			v.setLogicName(cls.getSimpleName());
			v.setBClass(CodecUtils.getClassFromJavaClass(cls, project));
			b.addThrows(v);
		}

	}

	public static void copyConstructorToBConstructor(Constructor<?> m, BConstructor b, BProject project) {

		b.setLogicName(m.getName());
		Parameter[] paras = m.getParameters();
		for (Parameter p : paras) {
			BParameter var = CodecUtils.makeParameterByType(p.getType(), p.getParameterizedType(), project);
			var.setLogicName(p.getName());
			var.setModifier(p.getModifiers());
			b.addParameter(var);
		}

		b.setModifier(m.getModifiers());

		// TODO
		BVariable var = CodecUtils.makeValuableByType(m.getDeclaringClass(),
				m.getDeclaringClass().getGenericSuperclass(), project);

		b.setReturn(var);

		Class<?>[] types = m.getExceptionTypes();
		for (Class<?> cls : types) {
			BVariable v = new BVariableImpl();
			v.setLogicName(cls.getSimpleName());
			v.setBClass(CodecUtils.getClassFromJavaClass(cls, project));
			b.addThrows(v);
		}

	}

	private static boolean init = false;

	public static BClass[] getAllBClasses() {
		if (init) {
			BClass[] bs = new BClass[classCache.size()];
			Enumeration<String> enu = classCache.keys();
			int k = 0;
			while (enu.hasMoreElements()) {
				bs[k] = classCache.get(enu.nextElement()).cloneAll();
				k++;
			}
			return bs;
		}
		List<BClass> list = getAllTypeFromExplorer();
		if (list == null) {
			list = new ArrayList<BClass>();
		}

		BClass[] models = new BClass[TYPE_MODEL.length];
		if (list != null) {
			models = new BClass[TYPE_MODEL.length + list.size()];
		}
		for (int i = 0; i < TYPE_MODEL.length; i++) {
			if (TYPE_MODEL[i][0] == null) {
				models[i] = CodecUtils.BNull;
			} else {
				models[i] = getClassFromJavaClass(null, TYPE_MODEL[i][0]);
			}
			models[i].setName(TYPE_MODEL[i][2]);
			models[i].setUserObject(TYPE_MODEL[i][3]);
		}
		int j = TYPE_MODEL.length;
		for (BClass bclass : list) {
			models[j] = bclass;
			j++;
		}

		for (BClass bclass : models) {

			addClassCache(bclass, null);
		}
		init = true;
		return models;
	}

	private static List<BClass> getAllTypeFromExplorer() {
		return getAllTypeFromExplorer(null, null);
	}

	private static List<BClass> getAllTypeFromExplorer(List<BClass> list, BeeTreeNode parent) {
		if (list == null) {
			list = new ArrayList<BClass>();
		}
		if (parent == null) {
			if (Application.INSTANCE_COMPLETE) {
				parent = Application.getInstance().getDesignSpective().getFileExplore().getRoot();
			} else {
				return null;
			}
		}

		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode bnode = (BeeTreeNode) parent.getChildAt(i);

			Object model = bnode.getUserObject();
			if (model != null) {
				if (model instanceof BClass) {
					BClass node = (BClass) model;
					list.add(node);
				} else {
					String path = bnode.getFilePath();
					if (path != null) {

						ObjectInputStream ois;
						try {
							File file = new File(path);
							if (!file.exists()) {
								continue;
							}
							if (file.isDirectory()) {
								continue;
							}
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));

							ois = new ObjectInputStream(bis);
							Object obj = ois.readObject();
							ois.close();
							bis.close();

							if (obj instanceof BookModel) {
								BookModel book = (BookModel) obj;
								List<BEditorModel> models = book.getList();
								for (BEditorModel m : models) {

									list.add(makeTempClass((BClass) m));
								}
								// FileCache.put(path, book);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						// }
					}
				}
			}
			if (!bnode.isLeaf()) {
				getAllTypeFromExplorer(list, bnode);
			}

		}

		return list;
	}

	// for the sake of memory
	private static BClass makeTempClass(BClass m) throws BeeClassExistsException, ClassNotFoundException {
		BClass bclass = m;
		if (bclass != null) {
			return copyClassToTemp(bclass);
		}
		return null;

	}

	public static BClass copyClassToSimpleTemp(BClass bclass) {
		if (bclass == null) {
			return null;
		}
		BClass temp = new BClassImpl();

		temp.setPackage(bclass.getPackage());
		temp.setData(bclass.isData());
		temp.setLogic(bclass.isLogic());
		temp.setName(bclass.getName());
		temp.setInterface(bclass.isInterface());
		temp.setAnonymous(bclass.isAnonymous());
		temp.setArrayPressentClass(bclass.getArrayPressentClass());
		temp.setImports(bclass.getImports());
		temp.setInnerParentClassName(bclass.getInnerParentClassName());
		temp.setModifier(bclass.getModifier());
		temp.setParameterValue(bclass.isParameterValue());
		temp.getParameterizedTypes().addAll(bclass.getParameterizedTypes());
		temp.setSuperClass(bclass.getSuperClass());

		temp.setLogicName(bclass.getLogicName());
		temp.setUserObject(bclass.getUserObject());

		return temp;
	}

	public static BClass copyClassToTemp(BClass bclass) {
		BClass temp = new BClassImpl();

		temp.setPackage(bclass.getPackage());
		temp.setData(bclass.isData());
		temp.setLogic(bclass.isLogic());
		temp.setName(bclass.getName());
		temp.setInterface(bclass.isInterface());
		temp.setAnonymous(bclass.isAnonymous());
		temp.setArrayPressentClass(bclass.getArrayPressentClass());
		temp.setImports(bclass.getImports());
		temp.setInnerParentClassName(bclass.getInnerParentClassName());
		temp.setModifier(bclass.getModifier());
		temp.setParameterValue(bclass.isParameterValue());
		temp.getParameterizedTypes().addAll(bclass.getParameterizedTypes());
		temp.setSuperClass(bclass.getSuperClass());

		temp.setLogicName(bclass.getLogicName());
		temp.setUserObject(bclass.getUserObject());

		List<BMethod> methods = bclass.getMethods();
		for (BMethod m : methods) {
			if (m.getLogicBody() instanceof BasicNode) {
				BasicNode b = (BasicNode) m.getLogicBody();
				b.removeAll();

			}
			temp.getMethods().add(m);
		}

		List<BAssignment> vars = bclass.getVariables();
		for (BAssignment m : vars) {
			temp.getVariables().add(m);
		}

		return temp;
	}

	public static String makeMethodName(String name, String nName) {
		if (name != null) {
			variables.put(name, name);
			return name;
		}
		String aName = "method";
		for (int i = 0; i < 1000; i++) {
			String s = aName + i;
			if (!variables.containsKey(s)) {
				variables.put(s, s);
				return s;
			}
		}
		return null;
	}

	public static String makeMethodName(JMethod method, String nName) {
		String name = method.name();

		if (name != null) {
			variables.put(name, method);
			return name;
		}
		String aName = "method";
		for (int i = 0; i < 1000; i++) {
			String s = aName + i;
			if (!variables.containsKey(s)) {
				variables.put(s, method);
				method.name(s);
				return s;
			}
		}
		return null;
	}

	private static Hashtable<String, Object> variables = new Hashtable<String, Object>();

	public static String makeClassName(String name, String nName) throws BeeClassExistsException {
		String makeName = getLogicNamefromTranslator(name);
		if (makeName != null) {
			if (variables.containsKey(makeName)) {
				throw new BeeClassExistsException("");
			}
			variables.put(name, name);
			return makeName;
		}
		if (name != null) {
			variables.put(name, name);
			return name;
		}
		String aName = "Class";
		for (int i = 0; i < 1000; i++) {
			String s = aName + i;

			if (!variables.containsKey(s)) {
				variables.put(s, s);
				return s;
			}
		}
		return null;
	}

	private static String getLogicNamefromTranslator(String name) {
		return null;
	}

	public static BClass getSuperClass(BClass bclass, BProject project) {
		if (bclass == null) {
			return null;
		}
		if (bclass.getQualifiedName() == null) {
			return null;
		}
		Class<?> cls = getClassByName(bclass.getQualifiedName(), project);

		Class<?> scls = cls.getSuperclass();
		return getClassFromJavaClass(scls, project);

	}

	public static void setVarValue(BVariable var, String s) {
		if (!s.endsWith("L") && !s.endsWith("l")) {

			if (!s.endsWith("F") && !s.endsWith("f")) {

				if (!s.endsWith("'") && !s.endsWith("'")) {
					int len = s.length();
					boolean isInt = true;

					for (int i = 0; i < len; i++) {
						char c = s.charAt(i);
						if (!Character.isDigit(c)) {
							isInt = false;
						}
					}

					if (isInt) {
						if (len <= 10) {
							var.setBClass(CodecUtils.BInt());
						} else {
							var.setBClass(CodecUtils.BLong());
						}
					} else {
						if (!s.startsWith("\"")) {
							s = "\"" + s;
						}
						if (!s.endsWith("\"")) {
							s = s + "\"";
						}
					}
				} else {
					var.setBClass(CodecUtils.BChar());
				}
			} else {
				var.setBClass(CodecUtils.BFloat());
			}
		} else {
			var.setBClass(CodecUtils.BLong());
		}
		var.setLogicName(s);
		var.setName(s);
	}

}
