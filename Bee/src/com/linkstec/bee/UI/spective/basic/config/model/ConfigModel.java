package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class ConfigModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1277788499374494347L;

	private List<ActionModel> actions = new ArrayList<ActionModel>();

	private List<ComponentTypeModel> componentTypes = new ArrayList<ComponentTypeModel>();

	public List<ComponentTypeModel> getComponentTypes() {
		return componentTypes;
	}

	public void setComponentTypes(List<ComponentTypeModel> componentTypes) {
		this.componentTypes = componentTypes;
	}

	public ActionModel getAction(ComponentTypeModel from, ComponentTypeModel to) {
		for (ActionModel action : actions) {
			if (action.getFrom().getName().equals(from.getName()) && action.getTo().getName().equals(to.getName())) {
				return action;
			}

		}

		for (ActionModel action : actions) {
			if (action.getFrom().getName().equals(from.getName())) {
				return action;
			}

		}

		return null;
	}

	public List<ActionModel> getActions() {
		return actions;
	}

	public void setActions(List<ActionModel> actions) {
		this.actions = actions;
	}

	public static ConfigModel load(BProject project) {
		String path = project.getRootPath() + File.separator + "code.conf";
		File file = new File(path);
		if (file.exists()) {
			try {
				return (ConfigModel) ObjectFileUtils.readObject(file);
			} catch (Exception e) {
				e.printStackTrace();
				return new ConfigModel();
			}
		} else {
			return new ConfigModel();
		}
	}

	public void save(BProject project) {
		String path = project.getRootPath() + File.separator + "code.conf";
		try {
			ObjectFileUtils.writeObject(new File(path), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
