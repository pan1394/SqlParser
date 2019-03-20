package com.linkstec.bee.UI.spective.basic.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicComponentModel extends BasicDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2906965976042876024L;

	public BasicComponentModel(ComponentTypeModel type) {
		super(type);
	}

	public String getFilePath(SubSystem sub) {
		return sub.getPath() + File.separator + this.getLogicName() + ".cm";
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

	public static BasicComponentModel load(SubSystem sub, String logicName) {
		String path = sub.getPath() + File.separator + logicName + ".cm";
		try {
			return (BasicComponentModel) ObjectFileUtils.readObject(new File(path));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
