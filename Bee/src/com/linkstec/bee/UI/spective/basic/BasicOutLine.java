package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorOutlookExplorer;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.tree.BasicTreeNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicTreeRenderer;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicOutLine extends BEditorOutlookExplorer implements TreeWillExpandListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792420798546822004L;
	private BasicTreeNode root;
	private BasicBookModel book;
	private BasicEdit edit;

	public BasicOutLine(BasicTreeNode node) {
		super(node);
		this.setCellRenderer(new BasicTreeRenderer());
		root = node;

		this.setScrollsOnExpand(true);
		this.expandPath(new TreePath(root));
		this.addTreeWillExpandListener(this);
	}

	public BasicEdit getEdit() {
		return edit;
	}

	public void setEdit(BasicEdit edit) {
		this.edit = edit;
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {

	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	@Override
	public void update() {

	}

	@Override
	public void setEditor(BEditor editor) {

		this.edit.setEditor(editor);
		root.removeAllChildren();
		if (editor instanceof BasicBook) {
			BasicBook book = (BasicBook) editor;
			this.book = book.getBookModel();
			int count = book.getTabCount();
			for (int i = 0; i < count; i++) {
				Component comp = book.getComponent(i);
				if (comp instanceof BEditor) {
					BEditor e = (BEditor) comp;
					this.makeEditor(e);
				}
			}
		} else {
			if (editor instanceof BasicLogicSheet) {
				BasicLogicSheet basic = (BasicLogicSheet) editor;
				this.book = basic.findBook().getBookModel();
			}
			this.makeEditor(editor);
		}
		DefaultTreeModel m = (DefaultTreeModel) this.getModel();
		m.reload(root);
		this.expandPath(new TreePath(root));
	}

	private void makeEditor(BEditor editor) {
		if (editor == null) {
			return;
		}

		BEditorModel model = editor.getEditorModel();
		if (model instanceof BasicModel) {
			BasicModel basic = (BasicModel) model;
			this.makeLogicModel(basic, root, editor.getProject());
		}

	}

	private void makeLogicModel(BasicModel model, BasicTreeNode parent, BProject project) {
		BasicTreeNode c = new BasicTreeNode();
		c.setUserObject(model);
		c.setImageIcon(BeeConstants.CLASSES_ICON);
		c.setInited(true);
		c.setProject(project);

		parent.add(c);

	}

	@Override
	public void setSelected(Object node) {

	}

}
