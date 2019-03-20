package com.linkstec.bee.core.fw;

import java.util.Hashtable;

public interface BObject extends Cloneable {

	public Hashtable<Object, Object> getUserAttributes();

	public void setUserAttributes(Hashtable<Object, Object> userAttributes);

	public void addUserAttribute(Object key, Object value);

	public void removeUserAttribute(Object key);

	public Object getUserAttribute(Object key);

	public void setOwener(BObject object);

	public BObject getOwener();

	public String getAlert();

	public BAlertor setAlert(String alert);

	public BAlertor getAlertObject();

	public void setUserObject(Object object);

	public Object getUserObject();

	public Object cloneAll();

}
