package com.linkstec.bee.UI.spective.basic.tree;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTreeRenderer;
import com.linkstec.bee.UI.spective.basic.BasicEdit;
import com.linkstec.bee.core.fw.ILogic;

public class BasicEditRenderer extends BeeTreeRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8591602982208899112L;
	private JCheckBox box;
	private BasicEditNode currentNode;
	private BasicEdit edit;

	public BasicEditRenderer(BasicEdit edit) {
		box = new JCheckBox();
		this.edit = edit;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		BasicEditNode node = (BasicEditNode) value;
		node.setSelected(sel);
		currentNode = node;

		panel.add(box);
		panel.add(label);
		panel.add(logicName);
		logicName.setText("");
		box.setVisible(true);

		Object obj = node.getUserObject();
		label.setIcon(node.getImgeIcon());

		if (node.getDisplay() != null) {

			label.setText(node.getDisplay());
		} else {

			if (obj == null) {
				label.setText("");
			} else {
				if (obj instanceof ILogic) {
					ILogic logic = (ILogic) obj;
					label.setText(logic.getName());
					logicName.setText(":" + logic.getLogicName());
					box.setVisible(false);
				} else {
					label.setText(obj.toString());
				}
			}
		}

		box.setSelected(node.isChecked());

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
