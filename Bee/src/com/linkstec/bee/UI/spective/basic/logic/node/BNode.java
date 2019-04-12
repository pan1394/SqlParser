package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.BasicCellListeItem;
import com.linkstec.bee.UI.spective.basic.logic.BasicGraph;
import com.linkstec.bee.UI.spective.basic.logic.IBasicCellList;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

public class BNode extends mxCell implements Serializable, IBasicCellList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1126981749917406157L;
	private String bid;
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();
	private boolean editable = false;
	private boolean moveable = true;
	private boolean selectable = true;
	private boolean resizable = true;
	private boolean deletable = true;

	public BNode() {
		mxGeometry geo = new mxGeometry();
		geo.setOffset(new mxPoint(0, 0));
		this.setGeometry(geo);
		this.setId(BeeUIUtils.createID());
		this.bid = this.getId();
	}

	public void cellSelected(BasicLogicSheet sheet) {

	}

	public void cellConnected(BasicLogicSheet sheet, mxICell connector, boolean source) {

	}

	public void cellFolded(BasicGraph graph, boolean fold) {

	}

	public void childAdded(BNode node, BasicLogicSheet sheet) {

	}

	public void cellAdded(mxICell cell) {

	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public boolean isLocked() {
		return false;
	}

	public boolean isValidDropTarget(Object[] cells) {
		return false;
	}

	// if did anything and do not want to change label by default,return true
	public boolean labelChanged(String label) {
		return false;
	}

	public void setEnglishInput() {
		this.addUserAttribute("ENGLISH", "ENGLISH");
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public mxICell getCellById(String bid) {
		if (bid == null) {
			return null;
		}
		return this.getCellById(this, bid);

	}

	public mxCell getCellById(mxICell parent, String bid) {
		if (bid == null) {
			return null;
		}
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxCell cell = (mxCell) parent.getChildAt(i);
			if (cell instanceof BNode) {
				BNode node = (BNode) cell;
				String id = node.bid;
				if (id.equals(bid)) {
					return cell;
				}
			}
			mxCell c = getCellById(cell, bid);
			if (c != null) {
				return c;
			}

		}
		return null;

	}

	public void doLayout(BasicLogicSheet sheet) {

	}

	public void reshape(FontMetrics metrics) {

	}

	public boolean isEditable() {
		return this.editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void paintBefore(Graphics g, mxCellState state, double scale) {

	}

	public void paint(Graphics g, mxCellState state, double scale) {

	}

	public void imported(BasicLogicSheet sheet) {

	}

	public void added(BasicLogicSheet sheet) {
		if (this instanceof ILogicCell) {
			ILogicCell cell = (ILogicCell) this;
			if (sheet instanceof BPatternSheet) {
				BPatternSheet pattern = (BPatternSheet) sheet;
				BPatternModel model = (BPatternModel) pattern.getEditorModel();
				BPath parent = model.getActionPath();
				if (parent != null && cell.getLogic() != null && cell.getLogic().getPath() != null) {
					cell.getLogic().getPath().setParent(parent);
				}
			}
		}

	}

	public void resized(BasicLogicSheet sheet) {

	}

	public void doubleClicked(BasicLogicSheet sheet) {

	}

	public void clicked(BasicLogicSheet sheet) {
		if (this instanceof ILogicCell) {
			ILogicCell cell = (ILogicCell) this;
			BLogic logic = cell.getLogic();
			Application.getInstance().getBasicSpective().getPropeties().setTarget(logic, null);

		}
	}

	public ImageIcon getIcon() {
		return null;
	}

	public EditAction getAction() {
		return null;
	}

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	public boolean isDropTarget(BNode source) {
		return false;
	}

	@Override
	public List<BasicCellListeItem> getListItems(String text, BasicLogicSheet sheet) {
		return null;
	}

	@Override
	public void onMenuSelected(BasicCellListeItem item) {

	}

	@Override
	public void removeFromParent() {
		super.removeFromParent();
	}

}
