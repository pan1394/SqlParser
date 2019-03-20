package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.view.IClassMember;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.IUnit;
import com.mxgraph.model.mxGeometry;

public class NoteNode extends BasicNode implements Serializable, IClassMember, IUnit, BNote {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5325500689366745180L;

	public NoteNode() {
		mxGeometry g = new mxGeometry(0, 0, 600, BeeConstants.LINE_HEIGHT);
		this.setGeometry(g);
		this.addStyle("fontColor=green");
		g.setRelative(true);
		this.setOpaque(false);

		this.setValue("インライン説明、実際のロジックに影響を与えない");
	}

	public String toString() {
		if (this.getValue() == null) {
			return "";
		} else {
			return this.getValue().toString();
		}
	}

	@Override
	public void onAdd(BeeGraphSheet sheet) {
		this.getGeometry().setRelative(false);
	}

	@Override
	public String getNote() {
		return this.toString();
	}

	private String nodeDesc = "インライン説明、実際のロジックに影響を与えない";

	@Override
	public ValueAction getValueAction() {
		LabelAction action = new LabelAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6037796342765476029L;

			@Override
			public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
				if (value instanceof String) {
					String s = (String) value;
					setNote(s);
					return true;
				}
				return false;
			}

		};
		return action;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public String getNodeDesc() {
		return nodeDesc;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_NOTE_ICON;
	}

	@Override
	public void setNote(String note) {
		this.setValue(note);
	}

	@Override
	public void setLabel(String label) {

	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
