package com.linkstec.bee.core.fw.editor;

import java.io.Serializable;

public interface BProject extends Serializable {

	public String getName();

	public String getSourcePath();

	public String getDesignPath();

	public String getLibPath();

	public String getClassPath();

	public String getRootPath();

}
