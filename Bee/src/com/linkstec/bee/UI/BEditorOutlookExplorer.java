package com.linkstec.bee.UI;

import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BOutLook;

public abstract class BEditorOutlookExplorer extends BeeTree implements BOutLook {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7894767557166200599L;

	public BEditorOutlookExplorer(BeeTreeNode node) {
		super(node);
	}

	public abstract void update();

	public abstract void setEditor(BEditor editor);

	public abstract void setSelected(Object node);

}
