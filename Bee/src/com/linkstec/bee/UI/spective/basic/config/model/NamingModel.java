package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.NamingType;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.logic.BMethod;

public class NamingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2229455421898728594L;
	private List<NamingType> list = new ArrayList<NamingType>();

	private boolean dotted = true;

	public boolean isDotted() {
		return dotted;
	}

	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}

	public List<NamingType> getList() {
		return list;
	}

	public void setList(List<NamingType> list) {
		this.list = list;
	}

	public String getName(SubSystem sub, BasicComponentModel component, BClass cls, BMethod method) {
		if (component == null) {
			return null;
		}
		String Naming = "";
		for (int i = 0; i < list.size(); i++) {
			NamingType type = list.get(i);
			String name = null;
			if (type.getType() == NamingType.TYPE_SUB_ID) {
				name = sub.getId();
			} else if (type.getType() == NamingType.TYPE_SUB_NAME) {
				name = sub.getLogicName();
			} else if (type.getType() == NamingType.TYPE_COMPONENT_ID) {
				name = component.getId();
			} else if (type.getType() == NamingType.TYPE_COMPONENT_NAME) {
				name = component.getLogicName();
			} else if (type.getType() == NamingType.TYPE_FIXED) {
				name = type.getName();
			} else if (type.getType() == NamingType.TYPE_CLASS_NAME) {
				if (cls != null) {
					name = cls.getLogicName();
				}
			} else if (type.getType() == NamingType.TYPE_METHOD_NAME) {
				if (method != null) {
					name = method.getLogicName();
				}
			}
			name = type.getNaming(name);

			if (i == 0) {
				Naming = name;
			} else {
				if (dotted) {
					Naming = Naming + "." + name;
				} else {
					Naming = Naming + name;
				}
			}
		}
		return Naming;
	}

	public String getLocalName(SubSystem sub, BasicComponentModel component, BClass cls, BMethod method) {
		if (component == null) {
			return null;
		}
		String Naming = "";
		for (int i = 0; i < list.size(); i++) {
			NamingType type = list.get(i);
			String name = null;
			if (type.getType() == NamingType.TYPE_SUB_ID) {
				name = sub.getId();
			} else if (type.getType() == NamingType.TYPE_SUB_NAME) {
				name = sub.getName();
			} else if (type.getType() == NamingType.TYPE_COMPONENT_ID) {
				name = component.getId();
			} else if (type.getType() == NamingType.TYPE_COMPONENT_NAME) {
				name = component.getName();
			} else if (type.getType() == NamingType.TYPE_FIXED) {
				name = type.getName();
			} else if (type.getType() == NamingType.TYPE_CLASS_NAME) {
				if (cls != null) {
					name = cls.getName();
				}
			} else if (type.getType() == NamingType.TYPE_METHOD_NAME) {
				if (method != null) {
					name = method.getName();
				}
			}
			name = type.getNaming(name);

			if (i == 0) {
				Naming = name;
			} else {
				if (dotted) {
					Naming = Naming + "." + name;
				} else {
					Naming = Naming + name;
				}
			}
		}
		return Naming;
	}

}
