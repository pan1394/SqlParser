package com.linkstec.bee.UI.spective.code;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BSpective;
import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.core.fw.editor.BEditor;

public class BeeSourceWorkspace extends BWorkspace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3949911871449738761L;

	public BeeSourceWorkspace(BSpective spective) {
		super(spective);
		this.setBorder(null);
	}

	public List<BeeSourceSheet> getAllSheets() {
		List<BeeSourceSheet> sheets = new ArrayList<BeeSourceSheet>();
		int count = this.getTabCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponentAt(i);
			if (comp instanceof BeeSourceSheet) {
				sheets.add((BeeSourceSheet) comp);
			}
		}
		return sheets;
	}

	@Override
	protected void editorChanged(BEditor editor) {
		if (editor != null) {
			editor.onSelected();
		}
	}

}
