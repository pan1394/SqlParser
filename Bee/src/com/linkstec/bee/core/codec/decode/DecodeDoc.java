package com.linkstec.bee.core.codec.decode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Dectionery;
import com.linkstec.bee.core.JavaDocCache;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BUtils;
import com.linkstec.bee.core.fw.action.BDocIF;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.javadoc.DocClass;
import com.linkstec.bee.core.javadoc.DocField;
import com.linkstec.bee.core.javadoc.DocMethod;
import com.linkstec.bee.core.javadoc.DocReader;
import com.linkstec.bee.core.javadoc.DocType;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.api.JavacTool;

public class DecodeDoc extends TreePathScanner<Object, Object> implements BDocIF {
	private BProject project;
	private DocTrees trees;

	public DecodeDoc(BProject project) {
		this.project = project;
		javacTool = JavacTool.create();
	}

	public BProject getProject() {
		return this.project;
	}

	public String getClassDoc() {
		return this.getRoot().getClassDoc();
	}

	public String getVariableDoc(String variableName) {
		return this.getRoot().getVariableDoc(variableName);
	}

	public String getMethodDoc(BMethod m) {
		Object doc = this.getRoot().getMethodDoc(m);

		if (doc instanceof String) {
			return (String) doc;

		} else if (doc instanceof Hashtable) {
			@SuppressWarnings("unchecked")
			Hashtable<String, ?> hash = (Hashtable<String, ?>) doc;
			return (String) hash.get("title");
		}
		return null;
	}

	public void makeMothedModel(BMethod m) {
		if (this.getRoot() == null) {
			return;
		}

		DocMethod df = this.getRoot().getMethodDoc(m);
		if (df == null) {
			return;
		}

		String c = df.getComment();
		if (c == null) {
			c = Dectionery.get(m.getLogicName());
		}
		if (c.indexOf("。") > 0) {
			c = c.substring(0, c.indexOf("。"));
		}
		m.setName(c);
		List<DocType> types = df.getParameter();
		List<BParameter> list = m.getParameter();
		if (types != null) {
			for (int i = 0; i < types.size(); i++) {
				DocType type = types.get(i);
				BParameter var = list.get(i);
				var.setName(type.getComment());
			}
		}
	}

	public ClassDocument getRoot() {
		return this.currentDoc;
	}

	public DecodeDoc getDoc(String className) {

		this.trees = null;
		if (this.addClassDoc(className)) {
			scanDocTrees(className);
		}
		return this;
	}

	public void scanDocTrees(String className) {
		try {
			String path = project.getSourcePath() + File.separator + (className.replace('.', File.separatorChar))
					+ ".java";

			File file = new File(path);
			if (!file.exists()) {
				return;
			}
			JavacTask javacTask = BeeCompiler.scan(project, path);
			trees = DocTrees.instance(javacTask);

			Iterable<? extends CompilationUnitTree> result;

			result = javacTask.parse();
			Iterator<? extends CompilationUnitTree> ite = result.iterator();
			while (ite.hasNext()) {

				CompilationUnitTree unit = ite.next();
				this.scan(unit, null);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean addClassDoc(String className) {
		for (ClassDocument doc : docList) {
			if (doc == null) {
				continue;
			}
			if (doc.getClassName().equals(className)) {
				currentDoc = doc;
				return false;
			}
		}

		currentDoc = new ClassDocument();
		currentDoc.setClassName(className);

		docList.add(currentDoc);
		if (JavaDocCache.containsKey(className)) {
			// if (className.startsWith("java.") || className.startsWith("sun.") ||
			// className.startsWith("javax.") || className.startsWith("org.")) {
			// this.readJavaDoc(currentDoc);
			return false;
		}
		if (className.startsWith("[")) {
			currentDoc.setArray(true);
			return false;
		}
		if (BUtils.isPrimeryClass(className)) {
			return false;
		}
		return true;
	}

	private void readJavaDocback(ClassDocument doc) {
		String path = "/" + doc.getClassName().replace('.', '/') + ".d";

		InputStream is = DocReader.class.getResourceAsStream(path);
		if (is != null) {
			try {

				BufferedInputStream bis = new BufferedInputStream(is);

				ObjectInputStream in = new ObjectInputStream(bis);
				DocClass d = (DocClass) in.readObject();
				in.close();

				doc.setJavadoc(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Application.log(doc.getClassName() + " dose not exist");
		}

	}

	private JavacTool javacTool;

	private List<ClassDocument> docList = new ArrayList<ClassDocument>();
	private ClassDocument currentDoc;

	public List<ClassDocument> getAllDocs() {
		return this.docList;
	}

	@Override
	public Object visitClass(ClassTree tree, Object arg1) {

		this.currentDoc.addClassDoc(getCurrentPath(), this.trees, this);
		return super.visitClass(tree, arg1);
	}

	@Override
	public Object visitMethod(MethodTree tree, Object arg1) {
		this.currentDoc.addMethodDoc(getCurrentPath(), tree, trees);
		return super.visitMethod(tree, arg1);
	}

	@Override
	public Object visitVariable(VariableTree tree, Object arg1) {
		this.currentDoc.addVariable(tree, getCurrentPath(), trees);
		return super.visitVariable(tree, arg1);
	}

	public static class ClassDocument implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7831778732361087250L;
		private String className;

		private List<DocField> variables = new ArrayList<DocField>();
		private List<DocMethod> methods = new ArrayList<DocMethod>();
		private String classDoc;
		private boolean isArray = false;
		private DocClass javadoc;

		public ClassDocument() {
		}

		public boolean isArray() {
			return isArray;
		}

		public void setArray(boolean isArray) {
			this.isArray = isArray;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public DocClass getJavadoc() {
			return javadoc;
		}

		public void setJavadoc(DocClass javadoc) {
			this.javadoc = javadoc;
		}

		public String getClassDoc() {

			String cname = className;

			if (javadoc != null) {
				String c = javadoc.getComment();
				if (c.indexOf("。") > 0) {
					c = c.substring(0, c.indexOf("。"));
				}
				if (c.equals("")) {
					return javadoc.getName();
				} else {
					if (c.endsWith("です")) {
						return c.substring(0, c.length() - 2);
					}
				}
				return c;
			}

			if (this.classDoc != null && !this.classDoc.equals("")) {
				if (this.classDoc.indexOf(".") > 0) {
					return this.classDoc.substring(this.classDoc.lastIndexOf(".") + 1, this.classDoc.length());
				}
				return this.classDoc;
			} else {
				String classname = Dectionery.get(cname);
				if (classname != null) {
					if (classname.indexOf(".") > 0) {
						return classname.substring(classname.lastIndexOf(".") + 1, classname.length());
					}
					return classname;

				}
			}
			return null;
		}

		public void addClassDoc(TreePath classPath, DocTrees docTress, DecodeDoc doc) {
			String cname = className;

			try {

				Hashtable hash = (Hashtable) docTress.getDocCommentTree(classPath).accept(new DecodeDoctreeVisitor(),
						null);
				String s = (String) hash.get("title");
				if (s == null || s.trim().equals("")) {
					s = Dectionery.get(cname);
				}
				if (isArray) {
					String arr = Dectionery.get("array");
					this.classDoc = arr + "<" + s.trim() + ">";
				} else {
					this.classDoc = s.trim();
				}
			} catch (Exception e) {
				this.classDoc = Dectionery.get(cname);
			}

		}

		public void addVariable(VariableTree tree, TreePath path, DocTrees trees) {

			if (path.getParentPath().getLeaf() instanceof MethodTree) {
				// do nothing

			} else if (path.getParentPath().getLeaf() instanceof ClassTree) {
				DocField doc = new DocField();
				doc.setName(tree.getName().toString());

				if (trees != null) {
					String s = trees.getDocComment(path);
					if (s == null || s.trim().equals("")) {
						doc.setComment(Dectionery.get(doc.getName()));
					} else {
						doc.setComment(s);
					}
				} else {
					doc.setComment(Dectionery.get(doc.getName()));
				}
				this.variables.add(doc);
			}

		}

		public String getVariableDoc(String var) {

			List<DocField> fileds;
			if (this.javadoc != null) {
				fileds = javadoc.getFieldList();
			} else {
				fileds = this.variables;
			}
			if (fileds != null) {
				for (DocField df : fileds) {
					if (df.getName().equals(var)) {
						String c = df.getComment();
						if (c == null) {
							return Dectionery.get(var);
						}
						if (c.indexOf("。") > 0) {
							c = c.substring(0, c.indexOf("。"));
						}
						if (c.endsWith("です")) {
							c = c.substring(0, c.length() - 2);
						}
						return c;
					}
				}
			}
			return Dectionery.get(var);

		}

		public DocMethod getMethodDoc(BMethod m) {
			if (m == null) {
				return null;
			}
			if (m.getLogicName() == null) {
				return null;
			}
			List<DocMethod> mds;
			if (this.javadoc != null) {
				mds = javadoc.getMethodList();
			} else {
				mds = this.methods;
			}
			if (mds == null) {
				return null;
			}
			for (DocMethod df : mds) {
				if (df.getName().equals(m.getLogicName())) {

					List<BParameter> paras = m.getParameter();
					List<DocType> docParas = df.getParameter();
					boolean go = false;
					if (((paras == null || paras.isEmpty()) && (docParas == null || docParas.isEmpty()))) {
						go = true;
					}

					if (paras != null && docParas != null && paras.size() == docParas.size()) {
						for (BParameter var : paras) {
							for (DocType type : docParas) {
								if (var.getBClass() != null) {
									if (var.getBClass().getQualifiedName().equals(type.getType())) {
										go = true;
									}
								}
							}
						}
					}

					if (go) {
						return df;

					}
				}
			}

			return null;
		}

		public void addMethodDoc(TreePath path, MethodTree tree, DocTrees docTress) {

			String name = tree.getName().toString();
			DocMethod doc = new DocMethod();
			doc.setName(name);
			if (docTress == null) {
				return;
			}

			DocCommentTree docTree = docTress.getDocCommentTree(path);
			if (docTree != null) {
				@SuppressWarnings("unchecked")
				Hashtable<String, ?> hash = (Hashtable<String, ?>) docTree.accept(new DecodeDoctreeVisitor(), null);

				this.methods.add(doc);

				if (!tree.getName().toString().equals("<init>")) {
					String title = (String) hash.get("title");
					if (title != null) {
						String comment = title;
						if (comment.indexOf("\n") > 0) {
							comment = comment.substring(0, comment.indexOf("\n"));
						}
						doc.setName(comment);

					} else {
						doc.setName(tree.getName().toString());

					}
				}

				Hashtable<?, ?> paras = (Hashtable<?, ?>) hash.get("param");

				if (tree.getParameters().size() > 0) {

					for (int i = 0; i < tree.getParameters().size(); i++) {

						DocType type = new DocType();
						VariableTree v = tree.getParameters().get(i);
						type.setName(v.getName().toString());

						String logicName = v.getName().toString();
						if (paras != null) {
							String fname = (String) paras.get(logicName);
							if (fname != null && !name.equals("")) {
								type.setComment(fname);
							} else {
								type.setComment(logicName);
							}
						}
						type.setType(v.getType().toString());

						if (doc.getParameter() == null) {
							doc.setParameter(new ArrayList<DocType>());
						}
						doc.getParameter().add(type);
					}
				}
			}
		}
	}
}
