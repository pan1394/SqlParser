package com.linkstec.bee.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import com.link.test.ClassPathesLooker;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ZipUtil;
import com.linkstec.bee.core.io.ZipUtil.ByteClassProperty;

public class BeeClassLoader extends ClassLoader {
	private static final int BUFFER_SIZE = 8192;

	private Hashtable<String, URL> pathes = new Hashtable<String, URL>();
	private BProject project;

	public BeeClassLoader(BProject project) {
		this.project = project;
		this.updateClassUrls();
	}

	public BProject getProject() {
		return project;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		URL url = getResource(name);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			return null;
		}
	}

	protected URL findResource(String name) {

		String path = this.project.getClassPath() + File.separator + name.replace('/', File.separatorChar);
		File file = new File(path);

		if (file.exists()) {
			try {
				return file.toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		path = this.project.getSourcePath() + name.replace('/', File.separatorChar);

		file = new File(path);
		if (file.exists()) {
			try {
				return file.toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public void updateClassUrls() {
		this.pathes.clear();
		String root = project.getClassPath();

		ClassPathesLooker.makeNormalFileUrl(root, new File(root), pathes);
		ClassPathesLooker.makeLibraryUrl(project.getLibPath(), pathes);

		String path = project.getRootPath();
		path = path + File.separator + "lib";
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			ClassPathesLooker.makeNormalFileUrl(path, dir, pathes);
		}

	}

	public Hashtable<String, URL> getPathes() {
		return pathes;
	}

	public void addClassPath(String name, String path) throws MalformedURLException {
		pathes.put(name, new URL("file:/" + path));
	}

	public void deleteClassPath(String name) {
		pathes.remove(name);
	}

	public boolean contains(String className) {
		return this.pathes.containsKey(className);
	}

	protected synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		log("Loading class: " + className + ", resolve: " + resolve);

		if (className == null || className.equals("null")) {
			return null;
		}

		if (className.startsWith("com.linkstec.bee")) {

			Class<?> cls = this.getParent().loadClass(className);// Class.forName(className);//
																	// getSystemClassLoader().loadClass(className);
			if (cls != null) {
				return cls;
			}
		} else if (className.startsWith("org.apache.poi") || className.startsWith("org.openxmlformats")) {
			Class<?> cls = this.getParent().loadClass(className);
			if (cls != null) {
				return cls;
			}
		}

		Class<?> cls = ClassCache.getLoadedClass(project, className);
		if (cls != null) {
			return cls;
		}

		if (this.pathes.containsKey(className)) {
			byte[] classBytes = null;
			try {
				classBytes = this.getClassBytes(pathes.get(className).openStream());
			} catch (IOException e) {
				log("ERROR loading class file: " + e);
			}

			if (classBytes == null) {
				throw new ClassNotFoundException("Cannot load class: " + className);
			}
			cls = this.getClassByBytes(className, classBytes, resolve);
			ClassCache.addClass(project, cls);

			return cls;
		}

		try {
			cls = Class.forName(className);

			URL url = cls.getResource("/" + className.replace('.', '/') + ".class");
			pathes.put(className, url);
			ClassCache.addClass(project, cls);
			return cls;
		} catch (Exception e) {

		}

		try {
			cls = getSystemClassLoader().loadClass(className);
			URL url = cls.getResource("/" + className.replace('.', '/') + ".class");
			pathes.put(className, url);
			ClassCache.addClass(project, cls);
			return cls;
		} catch (Exception e) {

		}

		// find from class folder

		String root = project.getClassPath();
		String clsFile = root + File.separator + className.replace('.', File.separatorChar) + ".class";
		File file = new File(clsFile);
		if (!file.exists() || file.isDirectory()) {
			clsFile = root + File.separator + className.replace('.', File.separatorChar) + ".propeties";
			file = new File(clsFile);
			if (!file.exists() || file.isDirectory()) {
				file = null;
			}
		}

		if (file != null) {
			byte[] classBytes = null;
			try {
				classBytes = this.getClassBytes(new FileInputStream(file));
			} catch (IOException e) {
				log("ERROR loading class file: " + e);
			}

			if (classBytes == null) {
				throw new ClassNotFoundException("Cannot load class: " + className);
			}
			cls = this.getClassByBytes(className, classBytes, resolve);
			if (cls != null) {
				ClassCache.addClass(project, cls);
				return cls;
			}
		}

		throw new ClassNotFoundException("Cannot load class: " + className);

	}

	private Class<?> backups(String className, boolean resolve) throws ClassNotFoundException {
		Class<?> cls = null;
		String root = project.getClassPath();
		String name = className;
		if (name.startsWith("[L")) {
			name = name.substring(2);
			name = name.substring(0, name.length() - 1);
		}

		// 2. get class file name from class name
		boolean fondfromlibpath = false;
		String clsFile = root + File.separator + name.replace('.', File.separatorChar) + ".class";
		File file = new File(clsFile);
		if (!file.exists() || file.isDirectory()) {
			clsFile = root + File.separator + name.replace('.', File.separatorChar) + ".propeties";
			file = new File(clsFile);
			if (!file.exists() || file.isDirectory()) {
				fondfromlibpath = true;
			}

		}
		if (fondfromlibpath) {
			String path = project.getLibPath();
			if (path != null && !path.equals("")) {
				if (path.indexOf(File.pathSeparator) > -1) {
					String[] paths = path.split(File.pathSeparator);
					for (String p : paths) {
						File f = new File(p);
						if (f.exists() && f.isDirectory()) {
							File target = new File(
									p + File.separator + name.replace('.', File.separatorChar) + ".class");
							if (target.exists()) {
								file = target;
								break;
							} else {
								target = new File(
										p + File.separator + name.replace('.', File.separatorChar) + ".properties");
								if (target.exists()) {
									file = target;
									break;
								}
							}
						} else if (f.exists() && f.isFile()) {
							try {
								ByteClassProperty bytes = ZipUtil.getClassByte(f.getAbsolutePath(), className);
								if (bytes == null) {
									continue;
								} else {
									String bname = bytes.getClassName();
									if (!bname.equals(name)) {
										cls = this.loadClass(bname);
									}
									if (cls == null) {
										cls = this.getClassByBytes(bytes.getClassName(), bytes.getClassContents(),
												resolve);
										ClassCache.addClass(project, cls);
										return cls;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
						}
					}
				} else {
					File f = new File(path + File.separator + name.replace('.', File.separatorChar) + ".class");
					if (f.exists() && !f.isDirectory()) {
						file = f;
					} else {
						f = new File(path + File.separator + name.replace('.', File.separatorChar) + ".propeties");
						if (f.exists() && !f.isDirectory()) {
							file = f;
						}
					}
				}
			}
		}

		// 3. get bytes for class
		byte[] classBytes = null;
		try {
			classBytes = this.getClassBytes(new FileInputStream(file));
		} catch (IOException e) {
			log("ERROR loading class file: " + e);
		}

		if (classBytes == null) {
			throw new ClassNotFoundException("Cannot load class: " + className);
		}
		cls = this.getClassByBytes(className, classBytes, resolve);
		ClassCache.addClass(project, cls);

		return cls;

	}

	private byte[] getClassBytes(InputStream in) {
		byte[] classBytes = null;
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int n = -1;
			while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
				out.write(buffer, 0, n);
			}
			classBytes = out.toByteArray();
			in.close();
		} catch (IOException e) {
			log("ERROR loading class file: " + e);
		}
		return classBytes;
	}

	private Class<?> getClassByBytes(String className, byte[] classBytes, boolean resolve)
			throws ClassNotFoundException {
		// 4. turn the byte array into a Class
		Class<?> cls = null;
		try {
			cls = defineClass(className, classBytes, 0, classBytes.length);
			if (resolve) {
				resolveClass(cls);
			}
		} catch (SecurityException e) {
			cls = super.loadClass(className, resolve);
		}
		// if (cls != null) {
		// hash.put(cls.getName(), cls);
		// }

		return cls;
	}

	private static void log(String s) {
		// System.out.println(s);
	}
}
