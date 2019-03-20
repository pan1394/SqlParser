package com.linkstec.bee.UI.spective.code;

import java.io.File;

import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeSourceComplier implements Runnable {

	// private JavacFileManager fileManager;
	// private JavacTool tool;
	// private Iterable<String> options;
	private BProject project;
	private BeeSourceSheet sheet;

	public BeeSourceComplier(BeeSourceSheet sheet) {
		this.project = sheet.getProject();
		this.sheet = sheet;
		// fileManager = new JavacFileManager(new Context(), true,
		// Charset.defaultCharset());
		// tool = JavacTool.create();
		// options = BeeCompiler.getOptions(project);
	}

	private boolean doing = false;
	private boolean compiling = false;
	private Thread thread;

	public synchronized void compile() {
		if (thread == null) {
			thread = new Thread(this);
		}
		try {
			if (doing) {
				thread.interrupt();
			} else if (compiling) {
				int count = 0;
				while (compiling) {
					count++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (count > 5) {
						return;
					}
				}
			}
		} finally {
			doing = false;
		}
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		doing = true;
		Application.getInstance().getEditor().getStatusBar().startProgress(project.getName() + "をコンパイル中…");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		this.doing = false;
		compiling = true;
		try {
			File file = sheet.getFile();

			BeeCompiler.compileString(project, file.getAbsolutePath(), sheet.getSource());
		} finally {
			compiling = false;
		}
		Application.getInstance().getEditor().getStatusBar().endProgress();
	}
}
