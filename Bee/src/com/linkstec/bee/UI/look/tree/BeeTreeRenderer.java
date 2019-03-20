package com.linkstec.bee.UI.look.tree;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BeeTreeRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5353991871174018532L;
	protected BoxLayout layout;
	protected JPanel panel = new JPanel() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3068659296002060823L;
		protected MenuTooltip tip;

		@Override
		public JToolTip createToolTip() {
			if (tip == null) {
				tip = new MenuTooltip();
			}
			return tip;
		}

		class MenuTooltip extends JToolTip {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1148872955462497632L;

			@Override
			public void setTipText(String tipText) {
				super.setTipText("<html>" + tipText + "</html>");

			}

			@Override
			public void setToolTipText(String text) {
				super.setToolTipText("<html>" + text + "</html>");

			}

		}

	};
	protected JLabel label = new JLabel();
	protected JLabel logicName = new JLabel();

	public BeeTreeRenderer() {
		panel.setOpaque(true);
		layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.setFocusable(true);

		label.setFont(new Font(BeeUIUtils.getDefaultFontFamily(), Font.PLAIN, BeeUIUtils.getDefaultFontSize()));
		logicName.setBorder(new EmptyBorder(0, 5, 0, 0));
		logicName.setForeground(BeeConstants.EXPLORE_VALUE_COLOR);
		logicName.setFont(new Font(BeeUIUtils.getDefaultFontFamily(), Font.PLAIN, BeeUIUtils.getDefaultFontSize()));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		BeeTreeNode node = (BeeTreeNode) value;
		Object cell = node.getUserObject();
		panel.removeAll();
		panel.add(label);

		if (node.isError()) {
			label.setName("ERROR");
		} else if (node.isAlert()) {
			label.setName("ALERT");
		} else {
			label.setName(null);
		}
		BeeTree bt = (BeeTree) tree;
		List<File> errors = bt.getError();
		if (errors != null) {
			for (File f : errors) {
				if (node.isLeaf()) {
					if (f.getAbsolutePath().equals(node.getFilePath())) {
						label.setName("ERROR");
					}
				} else {
					if (node.getFilePath() != null && f.getAbsolutePath().startsWith(node.getFilePath())) {
						label.setName("ERROR");
					}
				}
			}
		}

		if (node.getDisplay() != null) {
			label.setText(node.getDisplay());
		} else {
			label.setText(value.toString());
		}

		if (cell != null && cell instanceof ILogic && node.getDisplay() == null) {

			ILogic logic = (ILogic) cell;
			String name = logic.getName();
			logicName.setText("");
			if (name != null) {
				name = (logic.getNumber() == null ? "" : logic.getNumber().toString()) + name.trim();
				logicName.setText(": " + name);
			}

			label.setText(null);
			label.setIcon(null);

			if (logic.getLogicName() == null) {
				logicName.setIcon(BeeConstants.ALERT_ICON);
			} else {
				String s = logic.getLogicName();
				if (cell instanceof BMethod) {

					BMethod b = (BMethod) cell;
					List<BParameter> paras = b.getParameter();
					if (paras != null) {
						s = s + "(";
						boolean first = true;
						for (BParameter var : paras) {

							if (first) {
								first = false;
								s = s + var.getBClass().getLogicName();
							} else {
								s = s + "," + var.getBClass().getLogicName();
							}
						}
						s = s + ")";
					}
				}
				label.setText(s);
			}
			panel.add(logicName);

			if (cell instanceof BValuable) {
				BValuable var = (BValuable) cell;
				BClass bclass = var.getBClass();
				if (bclass != null && bclass.getLogicName() != null) {
					logicName.setText(logicName.getText() + ": " + bclass.getLogicName());
				}
			}

		}

		if (node.getImgeIcon() != null) {
			label.setIcon(node.getImgeIcon());
		} else {
			if (node.isLeaf()) {
				label.setIcon(BeeConstants.TREE_NODE_ICON);
			} else {
				label.setIcon(BeeConstants.TREE_FOLDER_ICON);
			}
		}
		panel.setOpaque(true);
		if (!sel) {
			panel.setBackground(tree.getBackground());
		} else {
			panel.setBackground(BeeConstants.SELECTED_BACKGROUND_COLOR);
		}
		panel.setToolTipText(label.getText() + logicName.getText());
		return panel;

	}

}
