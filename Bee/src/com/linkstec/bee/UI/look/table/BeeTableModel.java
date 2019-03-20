package com.linkstec.bee.UI.look.table;

import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.look.menu.BeeObjectItem;
import com.linkstec.bee.UI.look.text.BeeTextField;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.impl.BObjectImpl;

public class BeeTableModel extends BObjectImpl implements IBeeTableModel, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 978838167339220789L;
	protected BeeTableNode root;
	protected EventListenerList listenerList = new EventListenerList();
	// Names of the columns.
	private String[] cNames;

	// Types of the columns.
	private Class<?>[] cTypes;

	private transient BeeTableUndo undo;

	public BeeTableModel() {

	}

	public void setRoot(BeeTableNode root) {
		this.root = root;
	}

	public void initialize(BeeTableNode root, String[] names, Class<?>[] types) {
		this.root = root;
		this.cNames = names;
		this.cTypes = types;
	}

	public void setUndo(BeeTableUndo undo) {
		this.undo = undo;
	}

	public BeeTableUndo getUndo() {
		return undo;
	}

	public BeeTableNode getRoot() {
		return root;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public String getAddColumnBeforeButtonName() {
		return "当該列前へ列を追加する";
	}

	public String getAddColumnAfterButtonName() {
		return "当該列後ろへ列を追加する";
	}

	public String getDleteColumnButtonName() {
		return "当該列を削除する";
	}

	public boolean beforeColumnAdd(int index, String name) {
		return true;
	}

	public boolean afterColumnAdd(int index, String name) {
		return true;
	}

	public List<BeeObjectItem> getPulldownList(int column) {
		return null;
	}

	// This is not called in the JTree's default mode: use a naive implementation.
	public int getIndexOfChild(Object parent, Object child) {
		for (int i = 0; i < getChildCount(parent); i++) {
			if (getChild(parent, i).equals(child)) {
				return i;
			}
		}
		return -1;
	}

	public void addTreeModelListener(TreeModelListener l) {
		if (listenerList == null) {
			listenerList = new EventListenerList();
		}
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	/*
	 * Notify all listeners that have registered interest for notification on this
	 * event type. The event instance is lazily created using the parameters passed
	 * into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on this
	 * event type. The event instance is lazily created using the parameters passed
	 * into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on this
	 * event type. The event instance is lazily created using the parameters passed
	 * into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on this
	 * event type. The event instance is lazily created using the parameters passed
	 * into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	protected List<BeePopupMenuItem> getPopMenus(int column) {
		// BasicTypeModel.getAllModel()
		return null;
	}

	public boolean isCellEditable(Object node, int column) {
		return true;
		// return getColumnClass(column) == String.class || getColumnClass(column) ==
		// Annotation.class;
	}

	public Object getValueAt(Object node, int column) {
		if (node instanceof BeeTableNode) {
			BeeTableNode p = (BeeTableNode) node;
			return p.getValueAt(column);
		}
		return node.toString();
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		BeeTableNode p = (BeeTableNode) node;

		p.setValueAt(value, column);

	}

	public void setIconAt(ImageIcon icon, Object node, int column) {
		BeeTableNode p = (BeeTableNode) node;
		p.setIconAt(icon, column);
	}

	public ImageIcon getIconAt(Object node, int column) {
		BeeTableNode p = (BeeTableNode) node;
		return p.getIconAt(column);
	}

	public int getChildCount(Object node) {
		BeeTableNode p = (BeeTableNode) node;
		return p.getChildCount();
	}

	public Object getChild(Object node, int i) {
		BeeTableNode p = (BeeTableNode) node;
		return p.getChild(i);
	}

	// The superclass's implementation would work, but this is more efficient.
	public boolean isLeaf(Object node) {
		BeeTableNode p = (BeeTableNode) node;
		return !p.hasChild();
	}

	public int getColumnCount() {
		return cNames.length;
	}

	public String getColumnName(int column) {
		if (column < 0) {
			return null;
		}
		if (column >= cNames.length) {
			return null;
		}
		return cNames[column];
	}

	public Class<?> getColumnClass(int column) {
		if (column < 0) {
			return null;
		}
		if (column >= cTypes.length) {
			return null;
		}
		Class<?> c = cTypes[column];

		if (c == null) {
			Debug.a();
		}

		return c;
	}

	public boolean isValid(int column, int row, Object obj) {
		return true;
	}

	public void deleteColumn(int index) {
		String[] names = new String[cNames.length - 1];
		Class<?>[] types = new Class<?>[cNames.length - 1];
		for (int i = 0; i < names.length; i++) {
			if (i >= index) {
				names[i] = cNames[i + 1];
				types[i] = cTypes[i + 1];
			} else if (i < index) {
				names[i] = cNames[i];
				types[i] = cTypes[i];
			}
		}
		cNames = names;
		cTypes = types;
		this.deleteColumnData(root, index);
	}

	private void deleteColumnData(BeeTableNode parent, int index) {
		List<Object> l = parent.getRowList();
		if (index >= l.size()) {
			return;
		}
		l.remove(index);
		List<BeeTableNode> list = parent.children;
		for (BeeTableNode node : list) {
			this.deleteColumnData(node, index);
		}
	}

	public void addColumn(int index, String name, Class<?> type) {
		String[] names = new String[cNames.length + 1];
		Class<?>[] types = new Class<?>[cNames.length + 1];
		for (int i = 0; i < names.length; i++) {
			if (i == index) {
				names[i] = name;
				types[i] = type;
			} else if (i > index) {
				names[i] = cNames[i - 1];
				types[i] = cTypes[i - 1];
			} else {
				names[i] = cNames[i];
				types[i] = cTypes[i];
			}
		}
		cNames = names;
		cTypes = types;

		this.addColumnData(root, index, type);
	}

	private void addColumnData(BeeTableNode parent, int index, Class<?> type) {
		List<Object> l = parent.getRowList();
		l.add(index, "");
		List<BeeTableNode> list = parent.children;
		for (BeeTableNode node : list) {
			this.addColumnData(node, index, type);
		}
	}

	public void fireError(String name, int line, boolean valid, String message, Object object) {

	}

	public void editText(BeeTextField text, int column, int row) {

	}
}
