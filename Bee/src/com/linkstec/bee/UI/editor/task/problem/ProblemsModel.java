package com.linkstec.bee.UI.editor.task.problem;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ProblemsModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -632054972487134562L;

	public ProblemsModel() {

	}

	// Names of the columns.
	static protected String[] cNames = { "内容", "対象箇所", "対象ファイル" };

	// Types of the columns.
	static protected Class<?>[] cTypes = { String.class, String.class, String.class };

	public boolean isLeaf(Object node) {
		ProblemNode p = (ProblemNode) node;
		return p.isLeaf();
	}

	public int getColumnCount() {
		return cNames.length;
	}

	public String getColumnName(int column) {
		return cNames[column];
	}

	public Class<?> getColumnClass(int column) {
		return cTypes[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (this.dataVector.size() > row) {
			ProblemNode node = (ProblemNode) this.dataVector.get(row);
			return this.getValueAt(node, column);
		}
		return null;
	}

	public void clearAll() {
		this.dataVector.clear();
	}

	@SuppressWarnings("unchecked")
	public List<ProblemNode> getData() {
		return this.dataVector;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// super.setValueAt(aValue, row, column);
	}

	@SuppressWarnings("unchecked")
	public boolean addNode(ProblemNode p) {
		boolean contained = false;

		int size = this.dataVector.size();
		for (int i = 0; i < size; i++) {

			Object obj = this.dataVector.get(i);
			ProblemNode node = (ProblemNode) obj;
			if (node.equals(p)) {
				contained = true;
			}

		}

		if (!contained) {
			this.dataVector.add(p);
		}
		return !contained;

	}

	public Object getValueAt(ProblemNode p, int column) {

		switch (column) {
		case 0:
			return p;
		case 1:
			if (p.getLocation() != null) {
				return p.getLocation();
			} else {
				return "";
			}
		case 2:
			return p.getName() == null ? "" : p.getName();
		}

		return null;
	}
}
