package com.linkstec.bee.UI.spective.basic.properties;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.table.TableModel;

import com.linkstec.bee.UI.spective.basic.BasicDataSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicComponentListSheet extends BasicDataSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4368555656387222206L;
	private File file;
	private BasicComponentListModel model;

	public BasicComponentListSheet(BasicComponentListModel model, BProject project) {
		super(model, project);
		this.model = model;
		file = new File(project.getRootPath() + File.separator + model.getLogicName());
	}

	@Override
	public String getDisplayPath() {
		return project.getName() + "/basic/" + model.getLogicName() + "コンポーネント一覧";
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File save() {

		String path = model.getSub().getPath();
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				String name = f.getName();
				if (name.indexOf('.') > 0) {
					String suffix = name.substring(name.indexOf('.') + 1);
					if (suffix.length() == 2) {
						if (suffix.startsWith("cm")) {
							f.delete();
						}
					}
				}

			}
		}
		TableModel tm = table.getModel();
		int count = tm.getRowCount();
		for (int i = 0; i < count; i++) {
			Object obj = table.getModel().getValueAt(i, 1);

			if (obj != null && !obj.equals("")) {
				BasicComponentModel data = new BasicComponentModel(null);
				data.setId(obj.toString());
				obj = table.getModel().getValueAt(i, 2);
				if (obj != null && !obj.equals("")) {
					data.setName(obj.toString());

					obj = table.getModel().getValueAt(i, 3);
					if (obj != null && !obj.equals("")) {
						data.setLogicName(obj.toString());

						obj = table.getModel().getValueAt(i, 4);
						if (obj instanceof ComponentTypeModel) {
							data.setType((ComponentTypeModel) obj);

							obj = table.getModel().getValueAt(i, 5);
							if (obj != null) {
								if (obj.equals("")) {
									data.setList(false);
								} else {
									data.setList(true);
								}
							} else {
								data.setList(false);
							}

							obj = table.getModel().getValueAt(i, 6);
							if (obj != null && !obj.equals("")) {
								data.setNote(obj.toString());
							}

							data.save(model.getSub());
						}
					}
				}
			}
		}
		this.setModified(false);
		Application.getInstance().getBasicSpective().updateDataResource(project, model.getSub());
		return null;
	}

	@Override
	public ImageIcon getImageIcon() {
		return this.model.getIcon();
	}

	@Override
	public String getLogicName() {
		return this.model.getName() + "コンポーネント一覧";
	}

}
