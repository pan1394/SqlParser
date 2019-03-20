package com.linkstec.bee.UI.spective.basic.config;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.core.fw.editor.BProject;

public class ConstantsConfig extends Config {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4800757434422858775L;

	public ConstantsConfig(BProject project, ConfigModel model) {
		super(project, model);
	}

	@Override
	public String getTitle() {
		return "定数定義";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.CONSTANT_ICON;
	}

	public boolean Debug() {
		return true;
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub

	}

}
