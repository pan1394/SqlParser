package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BProperties;
import com.linkstec.bee.core.fw.editor.BProject;

public class ActionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1600198736120342810L;
	private ComponentTypeModel from;
	private ComponentTypeModel to;
	private String providerName;
	private String providerProject;

	private List<String> annotations = new ArrayList<String>();
	private List<String> exceptions = new ArrayList<String>();
	private List<Object> parameters = new ArrayList<Object>();
	private Object returnType;
	private List<LayerModel> Layers = new ArrayList<LayerModel>();

	public List<LayerModel> getLayers() {
		return Layers;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderProject() {
		return providerProject;
	}

	public void setProviderProject(String providerProject) {
		this.providerProject = providerProject;
	}

	public void setLayers(List<LayerModel> layers) {
		Layers = layers;
	}

	public ComponentTypeModel getFrom() {
		return from;
	}

	public void setFrom(ComponentTypeModel from) {
		this.from = from;
	}

	public ComponentTypeModel getTo() {
		return to;
	}

	public void setTo(ComponentTypeModel to) {
		this.to = to;
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	public Object getReturnType() {
		return returnType;
	}

	public void setReturnType(Object returnType) {
		this.returnType = returnType;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public String toString() {
		return this.from.getName() + "TO" + this.to.getName();
	}

	private transient Class<?> cls = null;
	private transient BLogicProvider provider = null;

	public BLogicProvider getProvider() {
		return provider;
	}

	public BLogicProvider getProvider(boolean reload, BProperties properties) {
		if (this.providerName != null) {
			if (this.providerProject != null) {
				if (provider != null && !reload) {
					return provider;
				}
				Configuration config = Application.getInstance().getConfigSpective().getConfig();
				List<BProject> projects = config.getProjects();
				for (BProject p : projects) {
					if (p.getName().equals(providerProject)) {

						try {

							cls = ProjectClassLoader.getClassLoader(p).loadClass(providerName);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (cls != null) {
							try {

								Constructor<?> c = cls.getConstructors()[0];
								provider = (BLogicProvider) c.newInstance(PatternCreatorFactory.createView(),
										properties);
								return provider;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ActionModel) {
			ActionModel a = (ActionModel) obj;
			if (a.getFrom().getName().equals(this.getFrom().getName())) {
				if (a.getTo().getName().equals(this.getTo().getName())) {
					return true;
				}
			}
		}
		return false;
	}

}
