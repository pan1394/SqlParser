package com.linkstec.bee.UI.spective.basic.properties;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.table.TableModel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicDataSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.IBasicSubsystemOwner;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicPropertySheet extends BasicDataSheet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5026373464214640251L;

	private BasicPropertyModel model;
	private File file;

	public BasicPropertySheet(BasicPropertyModel model, BProject project) {
		super(model, project);
		this.model = model;

		IBasicSubsystemOwner owner = model.getOwnerModel().getOwner();
		if (owner == null) {
			file = new File(project.getRootPath() + File.separator + "basic.conf");
		} else {
			file = new File(project.getRootPath() + File.separator + owner.getLogicName() + File.separator + model.getLogicName());
		}
	}

	@Override
	public String getDisplayPath() {
		return file.getAbsolutePath();
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File save() {
		IBasicSubsystemOwner sys = model.getOwnerModel();

		sys.getSubs().clear();
		TableModel tm = table.getModel();
		int count = tm.getRowCount();
		for (int i = 0; i < count; i++) {
			Object obj = table.getModel().getValueAt(i, 1);

			if (obj != null && !obj.equals("")) {
				SubSystem sub = new SubSystem(sys);
				sub.setId(obj.toString());
				obj = table.getModel().getValueAt(i, 2);
				if (obj != null && !obj.equals("")) {
					sub.setName(obj.toString());

					obj = table.getModel().getValueAt(i, 3);
					if (obj != null && !obj.equals("")) {
						sub.setLogicName(obj.toString());

						obj = table.getModel().getValueAt(i, 4);
						if (obj != null && !obj.equals("")) {
							sub.setDesc(obj.toString());
						}
						sys.getSubs().add(sub);
					}
				}
			}
		}

		sys.save();
		this.setModified(false);
		Application.getInstance().getBasicSpective().updateDataResource(project, sys);
		return file;
	}

	@Override
	public String getLogicName() {
		return this.model.getOwnerModel().getLogicName() + "サブ一覧";
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.PROPERTY_ICON;
	}

}
