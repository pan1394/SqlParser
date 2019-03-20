package com.linkstec.bee.core.fw.export;

import java.io.File;

import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.editor.BProject;

public interface IExcelExport {

	public File export(BModule model, BProject project, String template, boolean doInvoker, boolean doStatic)
			throws Exception;

}
