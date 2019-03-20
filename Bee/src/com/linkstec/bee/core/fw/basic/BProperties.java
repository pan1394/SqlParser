package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;

public interface BProperties extends Serializable {

	public Hashtable<Object, Object> getUserAttributes();

	public void setUserAttributes(Hashtable<Object, Object> userAttributes);

	public void addUserAttribute(Object key, Object value);

	public void removeUserAttribute(Object key);

	public Object getUserAttribute(Object key);

	public int getLayerDepth();

	public String getInputComponentLogicName();

	public String getInputComponentLocalName();

	public String getSubsystemLogicName();

	public String getSubsystemLocalName();

	public int getActionType();

	public BClass getBClass(String name);

	public BClass getBClass(Class<?> name);

	public BProject getProject();

	public BClass getTemplate(String name);

	public BClass getTemplate(Class<?> name);

	public BClass createClass(String pack, String name);

	public void setClassList(List<BClass> list);

	public List<BClass> getClassList();

	public Object copy(Object obj);

	public void setCurrentDeclearedClass(BClass bclass);

	public BClass getCurrentDeclearedClass();

	public void save(BClass bclass);

	public boolean isReload();

	public void setReload(boolean reload);

	public List<BClass> getGenerableLogics();

	public void setGenerableLogics(List<BClass> logics);

	public List<BClass> getGenerableDatas();

	public void setGenerableDatas(List<BClass> datas);

	public void addThreadScopeAttribute(Object key, Object value);

	public Object getThreadScopeAttribute(Object key);

	public BAssignment createInstance(BClass bclass);

}
