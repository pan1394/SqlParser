package com.linkstec.bee.UI.node.view;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.TypeNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxICell;

public class InvokerParametersNode extends BasicNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6997954666033745843L;

	public InvokerParametersNode() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setLayout(layout);

		BasicNode title = new BasicNode();
		HorizonalLayout hl = new HorizonalLayout();
		hl.setBetweenSpacing(0);
		hl.setSpacing(0);
		title.setLayout(hl);
		LabelNode name = new LabelNode();

		name.addUserAttribute("fixedWidth", new Double(250));

		name.setValue("クラス名");
		name.setTitled();
		hl.addNode(name);
		LabelNode value = new LabelNode();
		value.setValue("値");
		value.addUserAttribute("fixedWidth", new Double(300));

		value.setTitled();
		hl.addNode(value);
		layout.addNode(title);
	}

	public void addParameter(BParameter var, BObject object) {
		BasicNode row = new BasicNode();
		HorizonalLayout hl = new HorizonalLayout();
		hl.setBetweenSpacing(0);
		hl.setSpacing(0);

		row.setLayout(hl);

		TypeNode name = new TypeNode(var);
		name.setEditable(false);
		name.setFixedWidth(250);
		name.makeBorder();

		hl.addNode(name);
		BasicNode value = new ObjectNode();

		if (object instanceof ObjectNode || object instanceof LinkNode) {
			value = (BasicNode) object;
		} else {
			value.setValue(object);
		}

		value.setFixedWidth(300);
		value.addUserAttribute("ParameterValue", "ParameterValue");
		value.makeBorder();
		hl.addNode(value);
		this.getLayout().addNode(row);

	}

	public List<BValuable> getParameters() {
		List<BValuable> parameters = new ArrayList<BValuable>();
		this.getParameters(this, parameters);
		return parameters;
	}

	public void clearParameters() {
		this.clearParameters(this);
	}

	public void clearParameters(mxICell cell) {
		int count = cell.getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			mxICell p = cell.getChildAt(i);
			if (p instanceof BasicNode) {
				BasicNode b = (BasicNode) p;
				if (b.getUserAttribute("ParameterValue") != null) {
					b.removeFromParent();
				} else {
					clearParameters(b);
				}
			}
		}
	}

	private void getParameters(mxICell cell, List<BValuable> parameters) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell p = cell.getChildAt(i);
			if (p instanceof BasicNode) {
				BasicNode b = (BasicNode) p;
				if (b.getUserAttribute("ParameterValue") != null) {
					BValuable value = LayoutUtils.getValueNode(b);
					if (value != null) {
						parameters.add(value);
					}
				}
			}
			this.getParameters(p, parameters);
		}
	}

}
