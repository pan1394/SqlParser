package com.linkstec.bee.UI.spective.basic.tree;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;

import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.detail.tree.ValueRender;

public class BasicTreeRenderer extends ValueRender {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4898880195617978722L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		BasicTreeNode node = (BasicTreeNode) value;

		Object obj = node.getUserObject();
		if (obj instanceof BasicModel) {
			BasicModel model = (BasicModel) obj;
			String name = model.getName();

			JLabel label = new JLabel(name);
			label.setIcon(node.getImgeIcon());
			return label;
		} else if (obj instanceof BasicDataModel) {
			BasicDataModel model = (BasicDataModel) obj;

			if (model.isList()) {
				Component r = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				label.setText(model.getName() + "[List]");
				panel.doLayout();
				panel.updateUI();
				return r;
			}
		}

		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}

}
