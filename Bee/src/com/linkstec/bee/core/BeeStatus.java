package com.linkstec.bee.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5314318683949672259L;

	private String projectName;
	private Hashtable<String, Object> properties = new Hashtable<String, Object>();

	public BeeStatus(String projectName) {
		this.projectName = projectName;
	}

	public void addProperty(String key, Object value) {
		if (value != null)
			this.properties.put(key, value);
	}

	public Hashtable<String, Object> getProperties() {
		return this.properties;
	}

	public void save(Configuration config) {
		BProject project = config.getProject(this.projectName);
		if (project == null) {
			return;
		}
		String root = project.getRootPath();
		File f = new File(root);
		try {
			if (!f.exists()) {
				f.mkdirs();
			}
			File file = new File(root + File.separator + ".config");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BeeStatus read(Configuration config, String projectName) {
		BProject project = config.getProject(projectName);
		String root = project.getRootPath();
		try {

			File file = new File(root + File.separator + ".config");
			if (!file.exists()) {
				return null;
			}
			FileInputStream fos = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fos);
			Object obj = oos.readObject();
			oos.close();

			return (BeeStatus) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
