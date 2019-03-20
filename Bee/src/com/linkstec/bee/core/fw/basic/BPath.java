package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.config.model.ProviderManager;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.editor.BProject;

public class BPath implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8503127387210971309L;

	private BPath parent;
	private BLogic logic;
	private ILogicCell cell;
	private Hashtable<String, Object> attributes = new Hashtable<String, Object>();
	private List<BParameter> usedVariables = new ArrayList<BParameter>();
	// private List<BParameter> outputs = new ArrayList<BParameter>();
	private List<BParameter> inputs = new ArrayList<BParameter>();
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();
	private BProject project;
	private IActionModel action;
	private long uniqueKey;

	public BPath(BPath parent, ILogicCell cell) {
		this.parent = parent;
		this.cell = cell;
		uniqueKey = System.currentTimeMillis() + this.hashCode();
	}

	public boolean is(BPath path) {
		return path.uniqueKey == this.uniqueKey;
	}

	public long getUniqueKey() {
		return this.uniqueKey;
	}

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	public ILogicCell getCell() {
		return this.cell;
	}

	public void setCell(ILogicCell cell) {
		this.cell = cell;
	}

	public BProject getProject() {
		if (project == null) {
			if (parent != null) {
				return parent.getProject();
			}
		}
		return project;
	}

	public List<BParameter> getInputs() {
		return inputs;
	}

	public void setInputs(List<BParameter> inputs) {
		this.inputs = inputs;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public IActionModel getAction() {
		if (action == null) {
			if (parent != null) {
				return parent.getAction();
			}
		}
		return action;
	}

	public IActionModel getSelfAction() {
		return action;
	}

	public void setAction(IActionModel action) {
		this.action = action;
	}

	public Hashtable<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Hashtable<String, Object> attributes) {
		this.attributes = attributes;
	}

	public BPath getParent() {
		return parent;
	}

	public void setParent(BPath parent) {
		this.parent = parent;
	}

	public BLogic getLogic() {
		return logic;
	}

	public void setLogic(BLogic logic) {
		this.logic = logic;
	}

	public List<BParameter> getUsedVariables() {
		return usedVariables;
	}

	public void setUsedVariables(List<BParameter> usedVariables) {
		this.usedVariables = usedVariables;
	}

	// public List<BParameter> getOutputs() {
	// return outputs;
	// }

	// public void setOutputs(List<BParameter> outputs) {
	// this.outputs = outputs;
	// }

	public BLogicProvider getProvider() {
		boolean newProvider = false;
		if (this.action == null) {
			if (this.parent != null) {
				return this.parent.getProvider();
			} else {
				newProvider = true;
			}
		} else {
			newProvider = true;
		}
		if (newProvider) {
			BActionModel model = (BActionModel) action;
			BLogicProvider provider = null;
			if (model != null) {
				provider = model.getProcessModel().getProvider();

			}
			if (provider == null) {
				provider = ProviderManager.getProvider(model, project,
						Application.getInstance().getBasicSpective().getSelection().isProviderReload());
			}
			return provider;
		}
		return null;
	}

}
