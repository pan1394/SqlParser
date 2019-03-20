package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableSelectItemsNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableValueNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.mxgraph.model.mxICell;

public class BTableSelectModel extends BTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9066130710125450760L;

	private String[] names = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ", "KK",
			"LL", "MM", "NN", "OO", "PP", "QQ", "RR", "SS", "TT", "UU", "VV", "WW", "XX", "YY", "ZZ" };

	private int parentNameGeneIndex = 0;
	private String parentName;// AS A,B etc

	public BTableSelectModel(BPath action) {
		super(action);
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String generaParentName() {
		if (parentNameGeneIndex < names.length) {
			String s = names[parentNameGeneIndex];
			parentNameGeneIndex++;
			return s;
		} else {
			int re = parentNameGeneIndex % names.length;
			int num = parentNameGeneIndex / names.length;
			String s = names[re];
			String name = "";
			for (int i = 0; i < num; i++) {
				name = name + s;
			}
			return name;
		}
	}

	public List<BTableValueNode> getSelectTargets() {
		mxICell cell = ((mxICell) this.getRoot()).getChildAt(0);
		int count = cell.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell node = cell.getChildAt(i);
			if (node instanceof BTableSelectItemsNode) {
				BTableSelectItemsNode g = (BTableSelectItemsNode) node;
				List<BTableValueNode> records = g.getRecords();
				return records;
			}
		}
		return null;
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableSelectSheet c = new BTableSelectSheet(project, this);
		return c;
	}

}
