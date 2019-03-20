package com.linkstec.bee.UI.spective.basic.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicDataSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicDataDictionary extends BasicDataSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1851494492758836137L;
	private File file;
	private SubSystem sub;
	private BasicDataModel model;

	public BasicDataDictionary(BasicDataModel model, BProject project, SubSystem sub) {
		super(model, project);
		this.model = model;
		this.sub = sub;
		file = new File(sub.getDictionaryPath(project));
		TableColumnModel clumn = table.getColumnModel();
		int count = clumn.getColumnCount();

		for (int i = 1; i < count; i++) {
			clumn.getColumn(i).setMinWidth(200);
		}

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public TableColumn addData(BClass data) {

		TableColumn model = super.addData(data);
		if (model != null) {
			model.setMinWidth(400);
		}
		return model;
	}

	@Override
	public String getDisplayPath() {
		return project.getName() + "/" + sub.getLogicName() + "/" + "Dictionary";
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File save() {
		try {
			BasicDataModel m = this.beforeModelSave(true);
			ObjectFileUtils.writeObject(file, m);

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setModified(false);
		return this.file;
	}

	public List<BasicDataModel> getDataModels() {
		TableColumnModel model = this.table.getColumnModel();
		int count = model.getColumnCount();
		List<BasicDataModel> list = new ArrayList<BasicDataModel>();
		for (int i = 0; i < count; i++) {
			TableColumn c = model.getColumn(i);
			Object value = c.getHeaderValue();
			if (value instanceof BasicDataModel) {
				BasicDataModel data = (BasicDataModel) value;
				list.add(data);

			}
		}
		return list;
	}

	public BasicDataModel beforeModelSave(boolean saveSub) {
		BasicDataModel m = (BasicDataModel) ObjectFileUtils.deepCopy(this.model);
		List<Integer> removes = this.fillData(m, saveSub);
		for (int index : removes) {
			m.deleteColumn(index);
		}
		return m;
	}

	public List<Integer> fillData(BasicDataModel m, boolean save) {
		BasicBook book = this.findBook();

		TableColumnModel model = this.table.getColumnModel();
		int count = model.getColumnCount();
		BeeTableNode root = m.getRoot();
		List<Integer> removes = new ArrayList<Integer>();
		for (int i = 0; i < count; i++) {
			TableColumn c = model.getColumn(i);
			Object value = c.getHeaderValue();
			if (value instanceof BClass) {
				BClass data = (BClass) value;
				if (data.isData()) {
					if (data instanceof BeeModel) {
						continue;
					}
					// before add
					List<BAssignment> vars = data.getVariables();
					for (int k = vars.size() - 1; k >= 0; k--) {
						BAssignment var = vars.get(k);
						if (!var.getLeft().getLogicName().equals("serialVersionUID")) {
							data.removeVar(var);
						}
					}

					int rows = this.table.getRowCount();
					for (int row = 0; row < rows; row++) {
						Object v = this.table.getValueAt(row, i);
						if ("〇".equals(v)) {
							BeeTableNode node = (BeeTableNode) root.getChild(row);
							if (node.getLeft() != null) {
								data.addVar(node);
							}
						}
					}
					removes.add(i);
					if (save) {
						if (data instanceof BasicDataModel) {
							BasicDataModel basic = (BasicDataModel) data;
							basic.save(sub);
						}
					}
					if (book != null) {
						book.getBookModel().addDataClass(data);
					}
				}
			}
		}
		return removes;
	}

}
