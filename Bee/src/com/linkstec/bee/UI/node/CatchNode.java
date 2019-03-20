package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.UI.node.view.Helper;
import com.linkstec.bee.UI.node.view.Space;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class CatchNode extends BasicNode implements Serializable, BCatchUnit, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3260120754679261201L;

	private String editorBID, variableBID;

	private String bodyLabelBID, titleBID;

	public CatchNode() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setLayout(layout);
		this.setOpaque(false);

		// title
		this.makeTitle();

		// space
		this.makeSpace();

		// body
		this.makeBody();

		// connector
		this.makeConnector();
	}

	private void makeTitle() {
		BasicNode titlebar = new BasicNode();
		titlebar.setRound();
		titlebar.setYellowTitled();
		this.titleBID = titlebar.getId();
		this.getLayout().addNode(titlebar);
		HorizonalLayout hl = new HorizonalLayout();
		titlebar.setLayout(hl);

		ParameterNode var = new ParameterNode();
		var.setOpaque(false);
		var.setBClass(CodecUtils.getClassFromJavaClass(Exception.class, null));
		var.setName("エラー");
		var.setLogicName("e");
		hl.addNode(var);
		this.variableBID = var.getId();
	}

	private void makeSpace() {
		Space space = LayoutUtils.makeSpace();
		this.getLayout().addNode(space);
	}

	private void makeBody() {
		BLockNode node = new BLockNode();
		editorBID = node.getId();
		this.setEditor(node);

		BasicNode label = Helper.makeBodyLabel("Action");
		this.bodyLabelBID = label.getId();
		node.getLayout().addNode(label);
	}

	private void makeConnector() {
		Connector edge = new Connector();
		edge.setValue("Catch");
		edge.setSource(this.getCellByBID(this.titleBID));
		edge.setTarget(this.getCellByBID(this.bodyLabelBID));
		edge.addStyle("labelBackgroundColor=white");
		this.insert(edge);
	}

	public void setEditor(BLogicBody catchEditor) {
		this.getLayout().addNode((BasicNode) catchEditor);
		this.editorBID = ((BasicNode) catchEditor).getId();
	}

	public void added(TryNode node) {
		Connector c = new Connector();
		c.setEntityConnnector();
		node.insert(c);

		c.setSource(node.getTryEditorLabel());
		c.setTarget(this.getCellByBID(this.titleBID));
	}

	public BLockNode getEditor() {
		return (BLockNode) this.getCellByBID(editorBID);
	}

	public void deleteEditor() {
		this.getCellByBID(editorBID).removeFromParent();
	}

	@Override
	public void setVariable(BParameter a) {

		BasicNode node = (BasicNode) this.getCellByBID(this.variableBID);
		node.replace((mxCell) a);

	}

	@Override
	public BParameter getVariable() {
		return (BParameter) this.getCellByBID(this.variableBID);
	}

	@Override
	public boolean isDeleteable() {
		if (this.getParent() != null) {
			mxICell cell = this.getParent();
			int count = cell.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell c = cell.getChildAt(i);
				if (c instanceof CatchNode && !c.equals(this)) {
					return true;
				}
			}
			return false;
		}
		return super.isDeleteable();
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.CATCH_ICON;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}

}
