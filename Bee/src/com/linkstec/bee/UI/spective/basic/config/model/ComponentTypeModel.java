package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.icon.BeeIcon;

public class ComponentTypeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 714360142243756987L;

	private String name;
	private String iconPath;
	private int inputType;
	private int outputType;
	private transient ImageIcon icon;
	private static ComponentTypeModel dataModel;

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public int getOutputType() {
		return outputType;
	}

	public void setOutputType(int outputType) {
		this.outputType = outputType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public ImageIcon getIcon() {
		if (iconPath != null) {
			if (icon == null) {
				icon = new BeeIcon(BeeConstants.class.getResource(iconPath));
			}
			return icon;
		}
		return null;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String toString() {
		return this.name;
	}

	public static ComponentTypeModel getDataModel() {
		if (dataModel == null) {
			dataModel = new ComponentTypeModel();
			dataModel.setIconPath("/com/linkstec/bee/UI/images/icons/datasheet.gif");
			dataModel.setInputType(-1);
			dataModel.setName("Dto");
			dataModel.setOutputType(-1);
		}
		return dataModel;
	}

}
