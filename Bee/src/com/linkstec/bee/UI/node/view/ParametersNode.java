package com.linkstec.bee.UI.node.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.TypeNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.core.fw.BParameter;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class ParametersNode extends BasicNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5998064740146685397L;

	public ParametersNode() {

		this.setRelative();
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setDeleteable(false);

		this.setLayout(layout);
		mxGeometry g = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
		g.setRelative(true);
		this.setGeometry(g);

		this.setDeleteable(false);
	}

	public void addParameter(BParameter node) {
		BasicNode row = new BasicNode();
		mxGeometry g = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
		g.setRelative(true);
		row.setGeometry(g);
		row.setTitled();
		HorizonalLayout layout = new HorizonalLayout();
		row.setLayout(layout);
		ILayout parameterLayout = this.getLayout();
		parameterLayout.addNode(row);

		BasicNode label = new BasicNode();
		label.setValue("パラメータ");
		label.setOpaque(false);
		label.setFixedWidth(100);
		layout.addNode(label);

		TypeNode classLabel = new TypeNode(node);

		classLabel.setOpaque(false);
		classLabel.setFixedWidth(400);
		layout.addNode(classLabel);

		BasicNode b = (BasicNode) node;
		layout.addNode(b);
		b.setOpaque(false);
		b.setValueAction(new LabelAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1982377852861558505L;

			@Override
			public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
				if (value != null && value instanceof String) {
					node.setLogicName((String) value);
					return true;
				}
				return false;
			}

		});
	}

	public List<BParameter> getParameters() {
		List<BParameter> list = new ArrayList<BParameter>();
		this.makeParameterList(this, list);
		return list;
	}

	private void makeParameterList(mxICell p, List<BParameter> list) {
		int count = p.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell c = p.getChildAt(i);
			if (c instanceof BParameter) {
				list.add((BParameter) c);
			} else {
				this.makeParameterList(c, list);
			}
		}
	}

	public String toString() {
		int count = this.getChildCount();
		String s = "";
		for (int i = 0; i < count; i++) {
			mxICell cell = this.getChildAt(i);
			if (cell instanceof BasicNode) {
				BasicNode node = (BasicNode) cell;
				if (node.isVisible()) {
					s = s + node.toString();
				}
			}
		}
		return s;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.PARAMETERS_ICON;
	}
}
