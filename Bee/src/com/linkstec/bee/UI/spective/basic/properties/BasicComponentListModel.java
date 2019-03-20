package com.linkstec.bee.UI.spective.basic.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.look.menu.BeeObjectItem;
import com.linkstec.bee.UI.look.table.BeeTableModel;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicComponentListModel extends BasicDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3829543618143606647L;
	private String[] names = { "No", "ID", "名称", "英名", "種類", "リスト", "説明" };
	private Class<?>[] types = { BeeTableModel.class, String.class, String.class, String.class, String.class, String.class, String.class };
	private BeeTableNode root = new BeeTableNode(null, names.length);
	private SubSystem sub;
	private BProject project;
	private List<BeeObjectItem> typeList = new ArrayList<BeeObjectItem>();
	private List<BeeObjectItem> listList = new ArrayList<BeeObjectItem>();
	private ConfigModel config;

	public BasicComponentListModel(ComponentTypeModel type, SubSystem sub, BProject project) {
		super(type);
		config = ConfigModel.load(project);
		this.project = project;
		this.makeTypeList();
		this.makeListData();
		this.initialize(root, names, types);
		this.sub = sub;
		this.makeRows();
		for (int i = 0; i < 100; i++) {
			BeeTableNode child = new BeeTableNode(root, root.getColumnCount());
			root.addChild(child);
		}
	}

	private void makeListData() {
		listList = new ArrayList<BeeObjectItem>();
		BeeObjectItem item = new BeeObjectItem();
		item.setText("");
		item.setUserObject(false);
		listList.add(item);

		item = new BeeObjectItem();
		item.setText("〇");
		item.setUserObject(true);
		listList.add(item);
	}

	private void makeTypeList() {

		List<ComponentTypeModel> types = config.getComponentTypes();

		for (ComponentTypeModel f : types) {

			BeeObjectItem item = new BeeObjectItem();
			item.setText(f.getName());
			item.setIcon(f.getIcon());
			item.setUserObject(f);
			this.typeList.add(item);

		}
	}

	public SubSystem getSub() {
		return sub;
	}

	public static List<BasicComponentModel> getComponents(SubSystem sub) {
		List<BasicComponentModel> models = new ArrayList<BasicComponentModel>();
		String path = sub.getPath();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		File[] files = file.listFiles();
		for (File f : files) {
			String name = f.getName();
			name = name.substring(name.lastIndexOf(".") + 1);

			if (name.startsWith("cm") && name.length() == 2) {
				Object obj;
				try {
					obj = ObjectFileUtils.readObject(f);
					if (obj instanceof BasicComponentModel) {
						BasicComponentModel data = (BasicComponentModel) obj;
						models.add(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return models;
	}

	private void makeRows() {

		List<BasicComponentModel> list = BasicComponentListModel.getComponents(this.getSub());

		for (BasicComponentModel data : list) {
			BeeTableNode item = new BeeTableNode(root, names.length);
			item.setValueAt(data.getId(), 1);
			item.setValueAt(data.getName(), 2);
			item.setValueAt(data.getLogicName(), 3);
			item.setValueAt(data.getType(), 4);
			item.setValueAt(data.isList() ? "〇" : "", 5);
			item.setValueAt(data.getNote(), 6);
			root.addChild(item);
		}
	}

	@Override
	public List<BeeObjectItem> getPulldownList(int column) {
		if (column == 4) {
			return this.typeList;
		} else if (column == 5) {
			return this.listList;
		}
		return null;
	}

}
