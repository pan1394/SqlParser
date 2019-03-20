package com.linkstec.bee.UI.spective.detail.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.core.fw.editor.BProject;

public class FileExplorTree extends BEditorFileExplorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5756660726638664241L;

	private MouseAdapter adapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getSource() == FileExplorTree.this && e.getClickCount() == 2) {

				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if (selPath != null) {
					Object obj = selPath.getLastPathComponent();
					if (obj instanceof BeeTreeFileNode) {
						BeeTreeFileNode node = (BeeTreeFileNode) obj;
						BeeActions.addDetailPane(node, node.getProject());

						e.consume();
					}
				}
			}
		}
	};

	public FileExplorTree(BeeTreeFileNode node, Configuration config) {
		super(node, config);

		this.addMouseListener(this.adapter);

	}

	public void removeMouseLisener() {
		this.removeMouseListener(adapter);
	}

	@Override
	public String getRoot(BProject project) {
		return project.getDesignPath();
	}

}
