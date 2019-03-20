package com.linkstec.bee.core.fw;

import java.io.Serializable;

public interface BAlertor extends Serializable {

	public static final String TYPE_WARNING = "WARNING";
	public static final String TYPE_ERROR = "ERROR";

	public String getMessage();

	public void setMessage(String message);

	public String getType();

	public void setType(String type);

}
