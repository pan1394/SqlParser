package com.linkstec.bee.core.fw.basic;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.logic.BLogicUnit;

public interface BLogic extends Serializable {

	public List<BParameter> getOutputs();

	public Hashtable<Object, Object> getUserAttributes();

	public void setUserAttributes(Hashtable<Object, Object> userAttributes);

	public void addUserAttribute(Object key, Object value);

	public void removeUserAttribute(Object key);

	public Object getUserAttribute(Object key);

	public String getName();

	public boolean hasException();

	public boolean isReturnBoolean();

	public ImageIcon getIcon();

	public String getDesc();

	public List<BLogic> getSubLogics();

	public void setPath(BPath path);

	public BPath getPath();

	public List<BLogicUnit> createUnit();

	public JComponent getEditor();
}
