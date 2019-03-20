package com.linkstec.bee.UI.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BProject;

public class Configuration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4059819135161618411L;
	private Hashtable<String, String> values = new Hashtable<String, String>();
	public List<BProject> projects = new ArrayList<BProject>();
	public String workSpace;

	public BProject getProject(String name) {
		for (BProject project : projects) {
			if (project.getName().equals(name)) {
				return project;
			}
		}
		return null;
	}

	public Hashtable<String, String> getValues() {
		return values;
	}

	public void setValues(Hashtable<String, String> values) {
		this.values = values;
	}

	public List<BProject> getProjects() {
		return projects;
	}

	public void setProjects(List<BProject> projects) {
		this.projects = projects;
	}

	public void putProperty(String name, String value) {
		this.values.put(name, value);
	}

	public String getProperty(String name) {
		return this.values.get(name);
	}

	public String getWorkSpace() {
		return workSpace;
	}

	public void setWorkSpace(String workSpace) {
		this.workSpace = workSpace;
	}

}
