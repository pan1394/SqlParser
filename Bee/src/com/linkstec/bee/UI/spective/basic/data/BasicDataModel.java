package com.linkstec.bee.UI.spective.basic.data;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicDataModel extends BeeDataModel implements BEditorModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8666229229895417555L;

	private ComponentTypeModel type;
	private String id;
	private String note;
	private boolean list = false;

	public BasicDataModel(ComponentTypeModel type) {
		this.type = type;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ComponentTypeModel getType() {
		return type;
	}

	public void setType(ComponentTypeModel type) {
		this.type = type;
	}

	public ImageIcon getIcon() {

		if (type == null) {
			return BeeConstants.DATA_ICON;
		}
		return type.getIcon();
	}

	public String getFilePath(SubSystem sub) {
		return sub.getPath() + File.separator + this.getLogicName() + ".dt";
	}

	public void save(SubSystem sub) {
		File f = new File(this.getFilePath(sub));

		File dir = f.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			ObjectFileUtils.writeObject(f, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BasicDataModel load(SubSystem sub, String logicName) {
		String path = sub.getPath() + File.separator + logicName + ".dt";
		try {
			return (BasicDataModel) ObjectFileUtils.readObject(new File(path));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
