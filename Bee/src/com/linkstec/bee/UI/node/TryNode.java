package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.UI.node.view.Helper;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.Space;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BCatchUnit;
import com.linkstec.bee.core.fw.logic.BTryUnit;
import com.mxgraph.model.mxCell;

public class TryNode extends BasicNode implements Serializable, IUnit, BTryUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3295414429727912560L;

	private String tryEditorBID, CatchEditorBID, finalBID, tryEditorLabelBID;
	private String label;
	private String numberBID;

	private String finalbodyLabelBID;

	public TryNode() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		layout.setBetweenSpacing(5);
		this.setLayout(layout);

		// title
		this.makeTitle();

		// try editor
		this.makeTryEditor();

		// cathes
		this.makeCatchArea();

		// first catch
		this.addCatch(new CatchNode());
	}

	private void makeTitle() {
		BasicNode title = new BasicNode();
		HorizonalLayout h = new HorizonalLayout();
		title.setLayout(h);
		this.getLayout().addNode(title);
		this.makeNumberedLabel(h);
	}

	private void makeTryEditor() {
		BLockNode tryEditor = new BLockNode();
		this.getLayout().addNode(tryEditor);
		this.tryEditorBID = tryEditor.getId();
		tryEditor.setOffsetX(40);

		BasicNode label = Helper.makeBodyLabel("Watch");
		label.setOffsetX(-40);
		label.setOffsetY(20);
		tryEditorLabelBID = label.getId();

		tryEditor.getLayout().addNode(label);
	}

	public BasicNode getTryEditorLabel() {
		return (BasicNode) this.getCellByBID(tryEditorLabelBID);
	}

	private void makeCatchArea() {
		BasicNode catches = new BasicNode();
		catches.setRelative();
		VerticalLayout v2 = new VerticalLayout();
		v2.setSpacing(0);
		v2.setBetweenSpacing(5);
		catches.setLayout(v2);
		this.getLayout().addNode(catches);
		this.CatchEditorBID = catches.getId();
	}

	private void makeNumberedLabel(ILayout layout) {
		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		LabelNode node = new LabelNode();
		node.setValue("以下処理のエラー可能性を観察する");
		layout.addNode(node);
	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	public int getCatchIndex(CatchNode node) {
		mxCell cell = this.getCellByBID(this.CatchEditorBID);
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			if (cell.getChildAt(i).equals(node)) {
				return i;
			}
		}
		return -1;
	}

	public void addFinalEditor() {
		BasicNode row = new BasicNode();
		VerticalLayout layout = new VerticalLayout();
		row.setLayout(layout);

		Space space = LayoutUtils.makeSpace();
		layout.addNode(space);

		BLockNode finalEditor = new BLockNode();
		layout.addNode(finalEditor);
		this.finalBID = finalEditor.getId();

		BasicNode label = Helper.makeBodyLabel("Finally");
		this.finalbodyLabelBID = label.getId();
		finalEditor.getLayout().addNode(label);

		this.getLayout().addNode(row);

		Connector c = new Connector();
		c.setEntityConnnector();
		this.insert(c);

		c.setSource(getTryEditorLabel());
		c.setTarget(label);
	}

	public BLockNode getTryEditor() {
		return ((BLockNode) this.getCellByBID(this.tryEditorBID));
	}

	public List<BCatchUnit> getCatches() {
		List<BCatchUnit> cathes = new ArrayList<BCatchUnit>();
		mxCell cell = this.getCellByBID(this.CatchEditorBID);
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			cathes.add((CatchNode) cell.getChildAt(i));
		}
		return cathes;
	}

	public BLockNode getFinalEditor() {
		return ((BLockNode) this.getCellByBID(this.finalBID));
	}

	public void delteFinalEditor() {
		mxCell cell = this.getCellByBID(this.finalBID);
		if (cell != null) {
			cell.removeFromParent();
		}
	}

	@Override
	public void addCatch(BCatchUnit catchnode) {
		CatchNode node = (CatchNode) catchnode;
		((BasicNode) this.getCellByBID(this.CatchEditorBID)).getLayout().addNode(node);
		node.added(this);

	}

	public void addCatchAt(BCatchUnit catchnode, int index) {
		CatchNode node = (CatchNode) catchnode;
		((BasicNode) this.getCellByBID(this.CatchEditorBID)).getLayout().addNode(node, index);
		node.added(this);

	}

	@Override
	public String getNodeDesc() {
		return "処理中のエラーを監視し、エラーが発生したら指定した処理を行う";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;

	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void clearCatches() {
		((BasicNode) this.getCellByBID(this.CatchEditorBID)).removeAll();
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_CATCH_ICON;
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
