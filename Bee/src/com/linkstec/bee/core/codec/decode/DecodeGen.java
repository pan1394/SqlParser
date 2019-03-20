package com.linkstec.bee.core.codec.decode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;

public class DecodeGen {

	private List<IDecodeResult> sheetList = new ArrayList<IDecodeResult>();
	private List<BookModel> models = new ArrayList<BookModel>();

	private String errorCode;

	public static final String ERROR_INTERFACE = "ERROR_INTERFACE";
	public static final String ERROR_NOCLASS = "ERROR_NOCLASS";
	public static final String ERROR_COMPILE_FAIL = "ERROR_COMPILE_FAIL";
	public static final String ERROR_DECODE_FAIL = "ERROR_DECODE_FAIL";

	public DecodeGen() {

	}

	public IDecodeResult parseJavaFiles(String path, BookModel model, BProject project) {
		P.start(2);
		if (path.endsWith(".java")) {
			String className = project.getClassPath() + path.substring(project.getSourcePath().length());
			className = className.substring(0, className.lastIndexOf('.')) + ".class";
			File file = new File(className);
			if (!file.exists()) {
				CompileListener listener = BeeCompiler.compile(project, path, null);
				if (!listener.successed()) {
					listener.showError();
					this.setErrorCode(ERROR_COMPILE_FAIL);
					return null;
				}
			}
		}

		P.go();
		SourceInfo info = new SourceInfo(path, project);

		Class<?> cls = info.getTargetClass();
		if (cls != null) {
			boolean test = false;
			if (test) {
				if (cls.isInterface()) {
					this.setErrorCode(ERROR_INTERFACE);
					return null;
				}
			}
		} else {
			this.setErrorCode(ERROR_NOCLASS);
			return null;
		}

		IDecodeResult sheet = this.getDecodedSheet(cls.getName());
		if (sheet != null) {
			return sheet;
		}

		JavacTask javacTask = BeeCompiler.scan(project, path);
		DocTrees trees = DocTrees.instance(javacTask);

		try {

			Iterable<? extends CompilationUnitTree> result = javacTask.parse();
			Iterator<? extends CompilationUnitTree> ite = result.iterator();
			while (ite.hasNext()) {

				CompilationUnitTree unit = ite.next();

				LineMap map = unit.getLineMap();

				if (CodecUtils.isData(info.getTargetClass())) {
					DecodeData data = new DecodeData(this, project, map, trees, info, model);
					unit.accept(data, unit);
					sheetList.add(data);
					P.end();
					return data;
				} else {

					DecodeSource visitor = new DecodeSource(this, project, map, trees, info, model);
					unit.accept(visitor, unit);
					sheetList.add(visitor);
					P.end();
					return visitor;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			this.setErrorCode(ERROR_DECODE_FAIL);
			return null;

		}
		return null;
	}

	public List<IDecodeResult> getList() {
		return this.sheetList;
	}

	public BClass decodeByName(String className, BookModel model, BProject project) {

		String path = project.getSourcePath() + File.separator + (className.replace('.', File.separatorChar)) + ".java";

		File f = new File(path);
		if (f.exists()) {
			if (this.getDecodedBClass(className) == null) {
				IDecodeResult result = execute(path, model, project);
				if (result == null) {
					return null;
				}
				return result.getBeeModel();
			} else {
				return this.getDecodedBClass(className);
			}
		}
		return null;
	}

	public IDecodeResult decodeByPath(String path, BookModel model, BProject project) {
		return parseJavaFiles(path, model, project);
	}

	public BClass getDecodedBClass(String className) {
		for (IDecodeResult result : sheetList) {
			BClass bs = result.getBeeModel();
			if (bs.getQualifiedName().equals(className)) {
				return bs;
			}
		}
		return null;
	}

	public IDecodeResult getDecodedSheet(String className) {
		for (IDecodeResult result : sheetList) {
			BClass bs = result.getBeeModel();
			if (bs.getQualifiedName().equals(className)) {
				return result;
			}
		}
		return null;
	}

	public File[] executeAndSave(String path, BProject project) {
		P.start(2);
		this.execute(path, null, project);
		P.go();
		File[] files = new File[(this.models.size())];
		int i = 0;

		for (BookModel model : this.models) {
			LayoutUtils.makeBook(model);
			files[i] = BeeActions.saveModel(project.getDesignPath() + File.separator + model.getLogicName() + ".bee", model, project);
			model = null;
			i++;
		}
		P.go();
		P.end();
		this.models = null;
		return files;

	}

	public IDecodeResult execute(String path, BookModel model, BProject project) {
		if (model == null) {
			model = new BookModel();
		}

		IDecodeResult result = decodeByPath(path, model, project);
		if (result == null) {
			return null;
		}

		model = result.getBookModel();

		boolean added = false;
		for (BookModel bm : this.models) {
			if (bm.getName().equals(model.getName())) {
				added = true;
			}
		}
		if (!added) {
			this.models.add(model);
		}

		return result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
