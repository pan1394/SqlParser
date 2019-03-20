package com.linkstec.bee.UI.spective.basic;

import java.awt.Toolkit;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.linkstec.bee.UI.BEditorExplorer;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicTreeNode;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;

public class BasicExplorer extends BEditorExplorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5636324062768798052L;

	private BasicEdit edit;
	private JScrollPane editScroll;
	private JScrollPane propetiesScroll;
	private JScrollPane selelectionScroll;

	private JSplitPane editSplite;
	private JSplitPane selectSplite;

	private BasicLogicProperties properties;
	private BasicEditDataSelection selection;

	public BasicExplorer(Configuration config) {
		super(config);

		selection = new BasicEditDataSelection(new BasicDataSelectionNode(null));
		selelectionScroll = new JScrollPane(selection);
		selelectionScroll.setBorder(null);

		properties = new BasicLogicProperties();
		propetiesScroll = new JScrollPane(properties);
		propetiesScroll.setBorder(null);

		edit = new BasicEdit(new BasicEditNode(null), selection, properties);
		editScroll = new JScrollPane(edit);
		editScroll.setBorder(null);

		selectSplite = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.selelectionScroll, editScroll);
		selectSplite.setBorder(null);
		selectSplite.setContinuousLayout(true);
		selectSplite.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.3));

		editSplite = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectSplite, propetiesScroll);
		editSplite.setBorder(null);
		editSplite.setContinuousLayout(true);
		editSplite.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.6));

		pane.insertTab("ロジック", BeeConstants.TYPE_LOGICAL_ICON, editSplite, null, 0);

		((BasicOutLine) this.getOutline()).setEdit(edit);
	}

	public BasicLogicProperties getProperties() {
		return this.properties;
	}

	public BasicEdit getEdit() {
		return this.edit;
	}

	public BasicEditDataSelection getSelection() {
		return this.selection;
	}

	public void setEditSelected() {
		pane.setSelectedComponent(editSplite);
	}

	@Override
	public BEditorOutlookExplorer createOutline() {
		return new BasicOutLine(new BasicTreeNode());
	}

	@Override
	public BEditorFileExplorer createFileTree() {
		return new BasicFileExplore(new BeeTreeFileNode(null, null), this.config);
	}

}
