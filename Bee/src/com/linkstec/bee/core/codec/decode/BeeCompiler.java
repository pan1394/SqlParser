package com.linkstec.bee.core.codec.decode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.editor.BProject;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;

public class BeeCompiler {

	public static void comileAllWithThread(BProject project, TaskListener taskListener) {
		new BeeThread(new Runnable() {

			@Override
			public void run() {
				BeeCompiler.compile(project, null, taskListener);

			}

		}).start();
	}

	public static CompileListener compileAll(BProject project, TaskListener taskListener) {
		return BeeCompiler.compile(project, null, taskListener);
	}

	public static CompileListener compileSources(BProject project, List<String> pathes, TaskListener taskListener) {

		Context context = new Context();
		JavacFileManager fileManager = new JavacFileManager(context, true, Charset.defaultCharset());

		context = new Context();

		JavacTool tool = JavacTool.create();
		Iterable<String> options = BeeCompiler.getOptions(project);

		List<File> list = new ArrayList<File>();
		if (pathes == null) {
			List<File> all = CodecUtils.getAllFile(null, null, project);
			for (File f : all) {
				if (f.isFile()) {
					if (f.getName().endsWith(".java")) {
						list.add(f);
					} else {
						String destDir = project.getClassPath() + File.separator;
						String sDir = f.getParentFile().getAbsolutePath();
						sDir = sDir.substring(project.getSourcePath().length() + 1);
						destDir = destDir + sDir;
						BeeCompiler.copyFile(f.getAbsolutePath(), destDir, project);
					}
				}
			}
		} else {
			for (String path : pathes) {
				File f = new File(path);
				if (f.isFile()) {
					if (f.getName().endsWith(".java")) {
						list.add(f);
					} else {
						String destDir = project.getClassPath() + File.separator;
						String sDir = f.getParentFile().getAbsolutePath();
						sDir = sDir.substring(project.getSourcePath().length() + 1);
						destDir = destDir + sDir;
						BeeCompiler.copyFile(f.getAbsolutePath(), destDir, project);
					}
				}
			}
		}
		Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromFiles(list);
		CompileListener listener = new CompileListener(project);
		JavacTaskImpl task = (JavacTaskImpl) tool.getTask(null, fileManager, listener, options, null, files, context);
		if (taskListener == null) {
			task.setTaskListener(new TaskListener() {

				@Override
				public void finished(TaskEvent t) {
					P.check(null);

					if (t.getKind().equals(Kind.GENERATE)) {
						P.go();
						String name = t.getSourceFile().getName();
						name = name.substring(project.getSourcePath().length() + 1);
						name = name.substring(0, name.lastIndexOf('.'));
						String calssName = name.replace(File.separatorChar, '.');
						name = project.getClassPath() + File.separator + name + ".class";

						try {
							ProjectClassLoader.getClassLoader(project).addClassPath(calssName, name);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}

					} else if (t.getKind().equals(Kind.PARSE)) {
						P.go();
					}
				}

				@Override
				public void started(TaskEvent t) {

				}

			});
		} else {
			if (taskListener instanceof BeeCompilerTaskListener) {
				BeeCompilerTaskListener b = (BeeCompilerTaskListener) taskListener;
				b.setSourceNumber(list.size());
			}
			task.setTaskListener(taskListener);
		}
		listener.setSourceNumber(list.size());
		if (list.size() == 0) {
			return listener;
		}

		P.start(list.size() * 2);

		task.doCall();
		P.end();

		Application.getInstance().getJavaSourceSpective().getTask().getProblems().addErrors(listener.getErrors());

		return listener;
	}

	public static CompileListener compile(BProject project, String path, TaskListener taskListener) {

		Context context = new Context();
		JavacFileManager fileManager = new JavacFileManager(context, true, Charset.defaultCharset());

		context = new Context();

		JavacTool tool = JavacTool.create();
		Iterable<String> options = BeeCompiler.getOptions(project);

		List<File> list = new ArrayList<File>();
		if (path == null) {
			List<File> all = CodecUtils.getAllFile(null, null, project);
			for (File f : all) {
				if (f.isFile()) {
					if (f.getName().endsWith(".java")) {
						list.add(f);
					} else {
						String destDir = project.getClassPath() + File.separator;
						String sDir = f.getParentFile().getAbsolutePath();
						sDir = sDir.substring(project.getSourcePath().length() + 1);
						destDir = destDir + sDir;
						BeeCompiler.copyFile(f.getAbsolutePath(), destDir, project);
					}
				}
			}
		} else {
			File f = new File(path);
			if (f.isFile()) {
				if (f.getName().endsWith(".java")) {
					list.add(f);
				} else {
					String destDir = project.getClassPath() + File.separator;
					String sDir = f.getParentFile().getAbsolutePath();
					sDir = sDir.substring(project.getSourcePath().length() + 1);
					destDir = destDir + sDir;
					BeeCompiler.copyFile(f.getAbsolutePath(), destDir, project);
				}
			}
		}
		Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromFiles(list);
		CompileListener listener = new CompileListener(project);
		JavacTaskImpl task = (JavacTaskImpl) tool.getTask(null, fileManager, listener, options, null, files, context);
		if (taskListener == null) {
			task.setTaskListener(new TaskListener() {

				@Override
				public void finished(TaskEvent t) {
					P.check(null);

					if (t.getKind().equals(Kind.GENERATE)) {
						P.go();
						String name = t.getSourceFile().getName();
						name = name.substring(project.getSourcePath().length() + 1);
						name = name.substring(0, name.lastIndexOf('.'));
						String calssName = name.replace(File.separatorChar, '.');
						name = project.getClassPath() + File.separator + name + ".class";

						try {
							ProjectClassLoader.getClassLoader(project).addClassPath(calssName, name);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}

					} else if (t.getKind().equals(Kind.PARSE)) {
						P.go();
					}
				}

				@Override
				public void started(TaskEvent t) {

				}

			});
		} else {
			if (taskListener instanceof BeeCompilerTaskListener) {
				BeeCompilerTaskListener b = (BeeCompilerTaskListener) taskListener;
				b.setSourceNumber(list.size());
			}
			task.setTaskListener(taskListener);
		}
		listener.setSourceNumber(list.size());
		if (list.size() == 0) {
			return listener;
		}

		P.start(list.size() * 2);

		task.doCall();
		P.end();

		Application.getInstance().getJavaSourceSpective().getTask().getProblems().addErrors(listener.getErrors());

		return listener;
	}

	public static JavacTask scan(BProject project, String path) {
		if (project == null) {
			return null;
		}

		Context context = new Context();
		JavacFileManager fileManager = new JavacFileManager(context, true, Charset.defaultCharset());

		context = new Context();

		JavacTool tool = JavacTool.create();
		Iterable<String> options = BeeCompiler.getOptions(project);

		List<File> list = new ArrayList<File>();
		if (path == null) {
			list = CodecUtils.getAllSourceFile(project);
		} else {
			list.add(new File(path));
		}
		Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromFiles(list);

		JavaCompiler.CompilationTask compilationTask = tool.getTask(null, fileManager, null, options, null, files,
				context);

		return (JavacTask) compilationTask;

	}

	public static Iterable<String> getOptions(BProject project) {
		String libPath = project.getLibPath();
		if (libPath == null) {// -Xlint:unchecked
			return Arrays.asList("-encoding", "UTF-8", "-d", project.getClassPath(), "-sourcepath",
					project.getSourcePath());
		} else {

			return Arrays.asList("-encoding", "UTF-8", "-d", project.getClassPath(), "-sourcepath",
					project.getSourcePath(), "-classpath", project.getLibPath());
		}
	}

	private static boolean copyFile(String srcPath, String destDir, BProject project) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) {
			return false;
		}

		File destFileDir = new File(destDir);
		if (!destFileDir.exists()) {
			destFileDir.mkdirs();
		}

		String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));

		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) {
			return false;
		}
		File destFile = new File(destPath);
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}
		return flag;
	}

	public static CompileListener compileString(BProject project, String name, String source) {
		name = name.substring(project.getSourcePath().length() + 1);
		name = name.replace(File.separatorChar, '/');
		Context context = new Context();
		JavacFileManager fileManager = new JavacFileManager(context, true, Charset.defaultCharset());

		context = new Context();

		JavacTool tool = JavacTool.create();
		Iterable<String> options = BeeCompiler.getOptions(project);

		JavaFileObject file = new JavaSourceFromString(name, source);
		Iterable<? extends JavaFileObject> files = Arrays.asList(file);

		CompileListener listener = new CompileListener(project);

		JavaCompiler.CompilationTask task = tool.getTask(null, fileManager, listener, options, null, files, context);
		task.call();

		Application.getInstance().getJavaSourceSpective().getTask().getProblems().clear();
		Application.getInstance().getJavaSourceSpective().getTask().getProblems().addErrors(listener.getErrors());
		return listener;
	}

	public static class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		public JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

}
