package com.linkstec.bee.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.linkstec.bee.UI.config.BeeProject;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.decode.DecodeDocProjects;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.BClassImpl;
import com.linkstec.bee.core.javadoc.DocClass;

public class ZipUtil {

	private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws Exception {
		if (file == null) {
			return;
		}

		if (file.isFile()) {
			int count, bufferLen = 1024;
			byte data[] = new byte[bufferLen];

			String subPath = file.getAbsolutePath();
			int index = subPath.indexOf(srcRootDir);
			if (index != -1) {
				subPath = subPath.substring(srcRootDir.length() + File.separator.length());
			}
			ZipEntry entry = new ZipEntry(subPath);
			zos.putNextEntry(entry);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			while ((count = bis.read(data, 0, bufferLen)) != -1) {
				zos.write(data, 0, count);
			}
			bis.close();
			zos.closeEntry();
		}

		else {

			File[] childFileList = file.listFiles();
			for (int n = 0; n < childFileList.length; n++) {
				childFileList[n].getAbsolutePath().indexOf(file.getAbsolutePath());
				zip(srcRootDir, childFileList[n], zos);
			}
		}
	}

	public static void zip(String srcPath, String zipPath, String zipFileName) throws Exception {
		if (srcPath == null || srcPath.equals("") || zipPath == null || zipPath.equals("") || zipFileName == null || zipFileName.equals("")) {
			throw new Exception("Path is not valid");
		}
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		try {
			File srcFile = new File(srcPath);

			if (srcFile.isDirectory() && zipPath.indexOf(srcPath) != -1) {
				throw new Exception("zipPath must not be the child directory of srcPath.");
			}

			File zipDir = new File(zipPath);
			if (!zipDir.exists() || !zipDir.isDirectory()) {
				zipDir.mkdirs();
			}

			String zipFilePath = zipPath + File.separator + zipFileName;
			File zipFile = new File(zipFilePath);
			if (zipFile.exists()) {
				SecurityManager securityManager = new SecurityManager();
				securityManager.checkDelete(zipFilePath);
				zipFile.delete();
			}

			cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
			zos = new ZipOutputStream(cos);

			String srcRootDir = srcPath;
			if (srcFile.isFile()) {
				int index = srcPath.lastIndexOf(File.separator);
				if (index != -1) {
					srcRootDir = srcPath.substring(0, index);
				}
			}
			zip(srcRootDir, srcFile, zos);
			zos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<BClass> getAllStaticMethodedJavaClass(String word, BeeProject project) throws Exception {
		List<BClass> list = new ArrayList<BClass>();
		JavaClassAction action = new JavaClassAction() {
			@Override
			public void execute(BClass clz, String comment) {

				Class<?> cls = CodecUtils.getClassByName(clz.getQualifiedName(), project);
				if (cls == null || cls.isInterface()) {
					return;
				}
				Method[] fs = cls.getMethods();
				boolean staticed = false;
				for (Method f : fs) {
					int m = f.getModifiers();
					if (Modifier.isStatic(m)) {

						BMethod mm = PatternCreatorFactory.createView().createMethod();
						CodecUtils.copyMethodToBMethod(clz.getQualifiedName(), f, mm, project);
						mm.setOwener(clz);
						DecodeDocProjects.getDoc(project).getDoc(clz.getQualifiedName()).makeMothedModel(mm);
						clz.getMethods().add(mm);

						if (word != null && !word.equals("") && maches(mm.getName() + mm.getLogicName(), word)) {
							staticed = true;
						}

					}
				}

				if (staticed) {
					list.add(clz);
				}

			}

		};
		readAllJavaFile(action);
		return list;
	}

	public static List<BClass> getAllJavaFile(String word) throws Exception {
		List<BClass> list = new ArrayList<BClass>();
		JavaClassAction action = new JavaClassAction() {
			@Override
			public void execute(BClass bclass, String comment) {

				if (maches(bclass.getQualifiedName() + comment, word)) {
					list.add(bclass);
				}

			}

		};
		readAllJavaFile(action);
		return list;
	}

	public static void readAllJavaFile(JavaClassAction action) throws Exception {

		String path = "/" + String.class.getName().replace('.', '/') + ".d";
		String test = ZipUtil.class.getResource(path).getFile();
		test = test.substring(6, test.indexOf("!"));

		String zipFilePath = test;
		if (zipFilePath == null || zipFilePath.equals("")) {
			throw new Exception("Path is not valid");
		}
		File zipFile = new File(zipFilePath);

		ZipEntry entry = null;
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			if (!entry.isDirectory()) {
				try {
					String name = entry.getName();
					String packageName = name.substring(0, name.lastIndexOf("/"));

					packageName = packageName.replace("/", ".");
					BufferedInputStream bis = new BufferedInputStream(File.class.getResourceAsStream("/" + entry.getName()));

					ObjectInputStream in = new ObjectInputStream(bis);
					DocClass d = (DocClass) in.readObject();
					bis.close();
					in.close();
					String comment = d.getComment();

					BClass bclass = new BClassImpl();
					bclass.setLogic(true);
					bclass.setPackage(packageName);
					bclass.setLogicName(d.getName());
					if (comment != null) {
						if (comment.indexOf("。") > -1) {
							comment = comment.substring(0, comment.indexOf("。"));
						}
						bclass.setName(comment);
						bclass.setUserObject(BClass.TYPE_LOGICAL);
						action.execute(bclass, comment);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static interface JavaClassAction {
		public void execute(BClass bclass, String comment);
	}

	public static boolean maches(String target, String word) {

		// System.out.println(target.toLowerCase());
		// System.err.println(word.toLowerCase());
		// return target.toLowerCase().contains(word.toLowerCase());
		return target.toLowerCase().indexOf(word.toLowerCase()) > -1;
		// return target.contains(word);
		// int l = word.length();
		// for (int i = 0; i < l; i++) {
		// if (!target.toLowerCase().contains(word.substring(i, i + 1).toLowerCase())) {
		// return false;
		// }
		// }
		// return true;
	}

	public static class ByteClassProperty {
		byte[] classContents;
		String className;

		public byte[] getClassContents() {
			return classContents;
		}

		public void setClassContents(byte[] classContents) {
			this.classContents = classContents;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

	}

	public static ByteClassProperty getClassByte(String zipFilePath, String className) throws Exception {
		if (zipFilePath == null || zipFilePath.equals("") || className == null || className.equals("")) {
			throw new Exception("Path is not valid");
		}
		File zipFile = new File(zipFilePath);

		ZipEntry entry = null;

		int count = 0, bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zip.entries();

		while (entries.hasMoreElements()) {
			entry = entries.nextElement();

			String name = entry.getName();

			if (entry.isDirectory()) {
				continue;
			}

			String cname = className.replace('.', '/') + ".class";
			boolean hit = false;
			if (name.equals(cname)) {
				hit = true;
			}
			if (!hit) {
				cname = className.replace('.', '/') + ".properties";
				if (name.equals(cname)) {
					hit = true;
				}
			}
			cname = className;
			// int index = cname.lastIndexOf(".");
			// while (!hit && index > -1) {
			//
			// cname = cname.substring(0, index) + "$" + cname.substring(index + 1);
			// String c = cname.replace('.', '/') + ".class";
			// if (name.equals(c)) {
			// hit = true;
			// break;
			// }
			// index = cname.lastIndexOf(".");
			// }

			cname = cname.replace('/', '.');

			if (hit) {
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				bos = new BufferedOutputStream(byteOut);

				bis = new BufferedInputStream(zip.getInputStream(entry));
				while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
					bos.write(buffer, 0, count);
				}
				bos.flush();
				bos.close();

				ByteClassProperty property = new ByteClassProperty();
				property.setClassContents(byteOut.toByteArray());
				property.setClassName(cname);
				return property;

			}
		}
		zip.close();
		return null;
	}

	public static void main(String[] args) {

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}