package com.linkstec.bee.UI.spective.basic.data;

import java.awt.Component;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JTree;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTreeRenderer;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;

public class DataRender extends BeeTreeRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8591602982208899112L;
	private JCheckBox box;
	private DataTreeNode currentNode;

	public DataRender() {
		box = new JCheckBox();

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		DataTreeNode node = (DataTreeNode) value;
		node.setSelected(sel);
		currentNode = node;

		// panel.add(box);
		panel.add(label);
		panel.add(logicName);

		Object obj = node.getUserObject();
		label.setIcon(node.getImgeIcon());

		if (obj == null) {
			label.setText("");
		} else {
			String s = obj.toString();
			if (s != null) {
				if (s.indexOf(".") > 0) {
					s = s.substring(0, s.lastIndexOf("."));
					if (s.indexOf(File.separator) > 0) {
						s = s.substring(s.lastIndexOf(File.separator) + 1);
					}
				}
				label.setText(s);
			}

		}

		box.setSelected(node.isChecked());

		logicName.setText("");
		if (obj instanceof SubSystem) {
			SubSystem sub = (SubSystem) obj;
			logicName.setText(":" + sub.getLogicName());
		} else if (obj instanceof BasicComponentModel) {
			BasicComponentModel model = (BasicComponentModel) obj;
			label.setText(model.getName());
			logicName.setText(":" + model.getLogicName());
		}

		if (!sel) {
			panel.setOpaque(false);
			panel.setBackground(tree.getBackground());
		} else {
			panel.setOpaque(true);
			panel.setBackground(BeeConstants.SELECTED_BACKGROUND_COLOR);
		}

		return panel;

	}
}
