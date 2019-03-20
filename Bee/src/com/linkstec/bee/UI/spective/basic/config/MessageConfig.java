package com.linkstec.bee.UI.spective.basic.config;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.core.fw.editor.BProject;

public class MessageConfig extends Config {

	/**
	 * 
	 */
	private static final long serialVersionUID = -509875839562219533L;

	public MessageConfig(BProject project, ConfigModel model) {
		super(project, model);
	}

	@Override
	public String getTitle() {
		return "エラーメッセージ定義";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.NODE_MESSAGE_ICON;
	}

	@Override
	public void beforeSave() {

	}

	public boolean Debug() {
		return true;
	}
}
