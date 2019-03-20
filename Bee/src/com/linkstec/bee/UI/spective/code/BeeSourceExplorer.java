package com.linkstec.bee.UI.spective.code;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.code.tree.BeeSourceTree;
import com.linkstec.bee.UI.spective.code.tree.SourceOutLineExplorer;

public class BeeSourceExplorer extends BEditorExplorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2026220334022997579L;

	public BeeSourceExplorer(Configuration config) {
		super(config);
	}

	@Override
	public BEditorOutlookExplorer createOutline() {

		return new SourceOutLineExplorer();
	}

	@Override
	public BEditorFileExplorer createFileTree() {
		return new BeeSourceTree(this.config, false);
	}

}
