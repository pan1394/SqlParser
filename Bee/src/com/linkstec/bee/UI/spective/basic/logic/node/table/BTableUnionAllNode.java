package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.core.fw.basic.BPath;

public class BTableUnionAllNode extends BTableUnionNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520238685522635778L;

	public BTableUnionAllNode(BPath path) {
		super(path);
		String title = "UNION ALL(ダブルクリックし、編集してください)";
		this.setValue(title);
		this.setTitle(title);
	}

	@Override
	protected String getUnionTitle() {
		return "UNION ALL";
	}
}
