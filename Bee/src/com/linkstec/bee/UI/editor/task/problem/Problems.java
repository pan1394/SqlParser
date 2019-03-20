package com.linkstec.bee.UI.editor.task.problem;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ToolTipManager;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.task.TaskPane;
import com.linkstec.bee.UI.look.tree.BeeTree;

public class Problems extends TaskPane implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5514850598606751729L;
	// private ProblemNode root = new ProblemNode(null);
	private ProblemsModel model;
	private JTable table;

	public Problems() {
		table = new JTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 9040231074184964042L;

			@Override
			public String getToolTipText() {
				Point point = this.getMousePosition();
				if (point != null) {
					int row = this.rowAtPoint(point);
					if (row > -1) {
						this.getModel().getValueAt(row, 0);
					}
				}
				return null;
			}

		};
		table.setRowHeight((int) (BeeUIUtils.getDefaultFontSize() * 2));
		ToolTipManager.sharedInstance().registerComponent(table);
		table.setRowMargin(BeeUIUtils.getDefaultFontSize() / 4);
		table.setDefaultRenderer(String.class, new ProblemCellCanvas());
		this.getScroll().getViewport().setView(table);
		model = new ProblemsModel();
		table.setModel(model);
		table.addMouseListener(this);
		table.getColumnModel().getColumn(1).setMaxWidth(BeeUIUtils.getDefaultFontSize() * 12);
		table.getColumnModel().getColumn(1).setMinWidth(BeeUIUtils.getDefaultFontSize() * 12);
	}

	public boolean hasProblem() {
		return model.getRowCount() != 0;
	}

	public synchronized void addError(BeeEditorError error) {
		ProblemNode node = new ProblemNode(error);
		this.addProblem(node);
		linkToTree();
	}

	public synchronized void addErrors(List<? extends BeeEditorError> errors) {
		for (BeeEditorError error : errors) {
			ProblemNode node = new ProblemNode(error);
			this.addProblem(node);
		}
		linkToTree();
	}

	private void addProblem(ProblemNode node) {
		ProblemsModel model = (ProblemsModel) table.getModel();
		if (model.addNode(node)) {
			model.fireTableDataChanged();
		}
	}

	private void linkToTree() {
		List<File> list = new ArrayList<File>();
		List<ProblemNode> nodes = model.getData();
		for (ProblemNode node : nodes) {
			this.makeChildLinkToTree(node, list);
		}
		if (nodes.size() > 0) {
			BeeTree tree = nodes.get(0).getUserObject().getLinkedFileTree();
			tree.setError(list);
		}
	}

	private void makeChildLinkToTree(ProblemNode node, List<File> list) {
		Object obj = node.getUserObject();
		if (obj instanceof BeeEditorError) {
			BeeEditorError error = (BeeEditorError) obj;
			String file = error.getFilePath();
			if (file != null) {
				boolean added = false;
				for (File f : list) {
					if (f.getAbsolutePath().equals(file)) {
						added = true;
					}
				}
				if (!added) {
					list.add(new File(file));
				}
			}
		}
	}

	public void clear() {
		ProblemsModel model = (ProblemsModel) table.getModel();
		model.clearAll();
		model.fireTableDataChanged();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int row = table.getSelectedRow();
		if (row > -1) {
			ProblemNode pnode = (ProblemNode) table.getValueAt(row, 0);
			Object obj = pnode.getUserObject();
			if (obj instanceof BeeEditorError) {
				BeeEditorError error = (BeeEditorError) obj;
				error.showErrorInEditor();
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
