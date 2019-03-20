package com.linkstec.bee.UI.spective.basic.properties;

import java.util.List;

import com.linkstec.bee.UI.look.table.BeeTableModel;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.IBasicSubsystemOwner;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;

public class BasicPropertyModel extends BasicDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546732338780553122L;
	private String[] names = { "No", "ID", "名称", "英名", "説明" };
	private Class<?>[] types = { BeeTableModel.class, String.class, String.class, String.class, String.class, String.class };
	private BeeTableNode root = new BeeTableNode(null, names.length);
	private IBasicSubsystemOwner model;

	public BasicPropertyModel(ComponentTypeModel type, IBasicSubsystemOwner model) {
		super(type);
		this.model = model;

		List<SubSystem> subs = model.getSubs();
		for (SubSystem sub : subs) {
			this.initSub(sub);
		}
		this.initialize(root, names, types);

		for (int i = 0; i < 100; i++) {
			BeeTableNode child = new BeeTableNode(root, root.getColumnCount());
			root.addChild(child);
		}
	}

	public IBasicSubsystemOwner getOwnerModel() {
		return this.model;
	}

	private void initSub(SubSystem sub) {
		BeeTableNode item = new BeeTableNode(root, names.length);
		item.setValueAt(sub.getId(), 1);
		item.setValueAt(sub.getName(), 2);
		item.setValueAt(sub.getLogicName(), 3);
		item.setValueAt(sub.getDesc(), 4);
		root.addChild(item);
	}

}
