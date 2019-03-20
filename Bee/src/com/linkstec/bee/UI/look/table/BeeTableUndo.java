package com.linkstec.bee.UI.look.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BEditorUndo;

public class BeeTableUndo implements BEditorUndo {

	private int update = 0;
	private int current = 0;
	private BeeTableNode root;
	private BeeTable table;
	private int maxUndo = 50;
	private List<BeeTableUndoEvent> history = new ArrayList<BeeTableUndoEvent>();

	private UndoListener listener;

	public BeeTableUndo(BeeTable table) {
		this.table = table;
	}

	private void beginUpdate() {
		current = 0;
		if (update == 0) {

			if (history.size() > maxUndo) {
				history.remove(0);
			}

		}
		update++;
	}

	public UndoListener getListener() {
		return listener;
	}

	public void setListener(UndoListener listener) {
		this.listener = listener;
	}

	private void endUpdate() {
		update--;
		if (this.listener != null) {
			this.listener.undoOcurred();
		}
	}

	public void undo() {

		current++;
		if (history.size() >= current) {
			BeeTableUndoEvent event = history.get(history.size() - current);
			table.setValueAt(event.getValue(), event.getRow(), event.getColumn());
			table.repaint();
			if (this.listener != null) {
				this.listener.undoOcurred();
			}
		}
		if (this.table != null) {
			table.updateUI();
		}
	}

	public void redo() {
		if (current == 0) {
			return;
		}
		current--;
		if (current > 0) {

			if (history.size() >= current) {
				BeeTableUndoEvent event = history.get(history.size() - current);
				table.setValueAt(event.getValue(), event.getRow(), event.getColumn());
				if (this.listener != null) {
					this.listener.undoOcurred();
				}
			}
		}
		if (this.table != null) {
			table.updateUI();
		}
	}

	public BeeTable getTable() {
		return table;
	}

	public void setTable(BeeTable table) {
		this.table = table;
	}

	public interface UndoListener {
		public void undoOcurred();
	}

	@Override
	public void undoableEditHappened(Object obj) {
		if (obj instanceof BeeTableUndoEvent) {
			this.beginUpdate();
			BeeTableUndoEvent event = (BeeTableUndoEvent) obj;
			this.history.add(event);
			this.endUpdate();
		}
	}

	// @Override
	// public void addListener(String eventName, mxIEventListener listener) {
	//
	// }

}
