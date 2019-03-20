package com.linkstec.bee.UI.spective.detail.tree;

import java.awt.Component;

import javax.swing.JTree;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTreeRenderer;
import com.linkstec.bee.core.fw.BObject;

public class ValueRender extends BeeTreeRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6794035588434753591L;

	public ValueRender() {
		panel.add(label);
		panel.add(logicName);
		panel.setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		ValueNode node = (ValueNode) value;
		node.setSelected(sel);

		Object obj = node.getUserObject();

		if (obj instanceof BObject) {
			return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}

		label.setIcon(node.getImgeIcon());

		String name = node.getNameValue();
		String typeName = node.getTypeNameValue();

		if (node.getDisplay() != null) {
			label.setText(node.getDisplay());
		} else {
			label.setText(name);
		}
		if (typeName == null) {
			logicName.setText("");
		} else {
			logicName.setText(":" + typeName);
		}
		panel.add(logicName);

		label.setToolTipText(node.getLineString());
		// panel.setToolTipText(node.getLineString());
		if (!sel) {
			panel.setOpaque(false);
			panel.setBackground(tree.getBackground());
		} else {
			panel.setOpaque(true);
			panel.setBackground(BeeConstants.SELECTED_BACKGROUND_COLOR);
		}
		panel.doLayout();

		return panel;

	}

}
