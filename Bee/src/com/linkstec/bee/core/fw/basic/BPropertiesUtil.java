package com.linkstec.bee.core.fw.basic;

import java.util.Hashtable;
import java.util.Properties;

public class BPropertiesUtil {

	private Hashtable<String, Properties> properties = new Hashtable<String, Properties>();

	public Hashtable<String, Properties> getProperties() {
		return properties;
	}

	public void setProperties(Hashtable<String, Properties> properties) {
		this.properties = properties;
	}

}
