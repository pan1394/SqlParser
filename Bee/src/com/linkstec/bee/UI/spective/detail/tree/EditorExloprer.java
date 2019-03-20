package com.linkstec.bee.UI.spective.detail.tree;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.config.Configuration;

public class EditorExloprer extends BEditorExplorer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditorExloprer(Configuration config) {

		super(config);
	}

	protected void beforeMenuShow(FileExplorTree tree) {
		actionMenu.setItems(getSearchResutlItem(tree, null));
	}

	@Override
	public BEditorOutlookExplorer createOutline() {
		return new DetailOutLineExplorer();
	}

	@Override
	public BEditorFileExplorer createFileTree() {
		return new FileExplorTree(new BeeTreeFileNode(null, null), this.config);
	}

}
