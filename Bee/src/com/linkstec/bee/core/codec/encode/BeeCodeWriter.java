package com.linkstec.bee.core.codec.encode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.linkstec.bee.core.fw.editor.BProject;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class BeeCodeWriter extends CodeWriter {

	byte[] buffer = new byte[4096];
	ByteArrayOutputStream output;
	private String className;
	private String packageName;
	private BProject project;

	public BeeCodeWriter(String className, String packageName, BProject project) {
		this.project = project;
		this.className = className;
		output = new ByteArrayOutputStream();
		this.packageName = packageName;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		return output;
	}

	@Override
	public void close() throws IOException {
		byte[] bytes = output.toByteArray();
		String ss = new String(bytes, "UTF-8");
		ss = ss.trim();
		bytes = ss.getBytes("UTF-8");

		if (project == null) {
			return;
		}

		String root = project.getSourcePath();

		// test
		// root = "D:\\Bee\\references\\1020slrsrc";

		String p = this.packageName.replace('.', File.separatorChar);
		String path = root + File.separator + p;
		File dir = new File(path);
		dir.mkdirs();
		File s = new File(path + File.separator + className + ".java");
		if (s.exists()) {
			s.delete();
		}

		FileOutputStream fos = new FileOutputStream(s);
		fos.write(bytes);
		fos.close();

	}

}