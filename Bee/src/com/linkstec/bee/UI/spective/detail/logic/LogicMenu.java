package com.linkstec.bee.UI.spective.detail.logic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenuItem;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.LoopNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.node.view.BasicNodeHelper;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BLoopUnit;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxResources;

public class LogicMenu extends BasicMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9024703447690354660L;
	private List<NodePanel> nodes;

	public LogicMenu(BProject project) {
		super(project);
		nodes = new ArrayList<NodePanel>();
		BoxLayout layout = new BoxLayout(contents, BoxLayout.Y_AXIS);

		contents.setLayout(layout);
		this.EditPaletteInit(this);
	}

	@Override
	protected void createSearch() {

	}

	public void addAllItems(String text) {
		if (nodes == null) {
			return;
		}
		this.contents.removeAll();
		for (NodePanel panel : nodes) {
			BasicNode node = panel.getNode();
			String title = panel.getTitle();
			String desc = node.getNodeDesc();
			boolean match = this.maches(title + node.getClass().getName(), text);
			if (!match) {
				if (desc != null) {
					match = this.maches(desc, text);
				}
			}
			if (match) {
				this.contents.add(panel);
			}
		}
	}

	public void EditPaletteInit(LogicMenu palette) {

		IPatternCreator view = PatternCreatorFactory.createView();
		makeNodePalatte("初期化処理", palette, view.createConstructor(), BeeConstants.INITIALIZE_ICON);
		makeNodePalatte(mxResources.get("logic"), palette, view.createMethod(), BeeConstants.P_METHOD_ICON);

		// makeNodePalatte("条件・算式", palette, view.createExpression(),
		// BeeConstants.P_FORMULA_ICON);
		// cases
		makeNodePalatte(mxResources.get("cases"), palette, view.createMultiCondition(), BeeConstants.P_CHOICE_ICON);

		// loop2
		BLoopUnit loop2 = view.createLoop();
		loop2.setLoopType(BLoopUnit.TYPE_FORLOOP);
		((LoopNode) loop2).removeConnector();
		makeNodePalatte("カウンターつき" + mxResources.get("loop"), palette, loop2, BeeConstants.P_LOOP_ICON);
		// loop3
		BLoopUnit loop3 = view.createLoop();
		loop3.setLoopType(BLoopUnit.TYPE_WHILE);
		((LoopNode) loop3).removeConnector();
		makeNodePalatte("条件つき" + mxResources.get("loop"), palette, loop3, BeeConstants.P_LOOP_ICON);
		// variable
		// makeNodePalatte("値付与式", palette, view.createAssignExpression(),
		// BeeConstants.ASSIGN_ICON);
		// makeNodePalatte("戻り値", palette, view.createMethodReturn(),
		// BeeConstants.P_RETURN_ICON);
		// makeNodePalatte("エラー監視", palette, view.createTry(),
		// BeeConstants.P_CATCH_ICON);

		// OnewordNode bnode = new OnewordNode();
		// bnode.setWord(OnewordNode.WORD_BREAK);
		// makeNodePalatte("ループ中断", palette, bnode, BeeConstants.P_BREAK_ICON);
		//
		// OnewordNode nnode = new OnewordNode();
		// nnode.setWord(OnewordNode.WORD_CONTINUE);
		// makeNodePalatte("ループ処理の次へ", palette, nnode, BeeConstants.P_CONTINUE_ICON);

		// makeNodePalatte("特別ブロック処理", palette, view.createModifiedBlock(),
		// BeeConstants.P_FRONT_ICON);

		// makeNodePalatte("シンプル設定式", palette, view.createExpressionLine(),
		// BeeConstants.P_TRUEFALSE_ICON);

		// SingleExpressionNode isingle = new SingleExpressionNode();
		// isingle.setOperator(BSingleExpressionUnit.INCREMENT);
		// makeNodePalatte("インクリメント", palette, isingle, BeeConstants.P_INCREASE_ICON);

		// SingleExpressionNode dsingle = new SingleExpressionNode();
		// dsingle.setOperator(BSingleExpressionUnit.DECREMENT);
		// makeNodePalatte("デクリメント", palette, dsingle, BeeConstants.P_DECREASE_ICON);

		makeNodePalatte(mxResources.get("note"), palette, new NoteNode(), BeeConstants.P_NOTE_ICON);
	}

	private void makeNodePalatte(String title, LogicMenu palette, Object node, ImageIcon icon) {
		if (node instanceof BasicNode) {
			BasicNode b = (BasicNode) node;
			b.addUserAttribute("MENU_ITEM_LOGIC", "MENU_ITEM_LOGIC");
		}
		palette.addNodeTemplate(title, icon, (BasicNode) node);
	}

	public void addNodeTemplate(String title, ImageIcon imageIcon, BasicNode node) {

		NodePanel panel = new NodePanel(title, imageIcon, node, this);
		this.contents.add(panel);
		nodes.add(panel);
	}

	public static class NodePanel extends BasicMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2934965627739234268L;
		private BasicNode node;
		private String text;
		private JPanel editor;

		public NodePanel(String text, ImageIcon imageIcon, BasicNode node, LogicMenu menu) {
			super(menu);
			this.node = node;
			this.text = text;
			this.setLayout(new BorderLayout());
			JPanel panel = new JPanel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 2533306332702807693L;

				@Override
				public void paint(Graphics g) {
					if (selected) {
						Graphics2D g2d = (Graphics2D) g;
						GradientPaint grdp = new GradientPaint(0, 0, BeeConstants.MOUSEOVER_BACKGROUND_COLOR, 0,
								getHeight(), BeeConstants.SELECTED_BACKGROUND_COLOR);
						g2d.setPaint(grdp);
						g2d.fillRect(0, 0, getWidth(), getHeight());

					}
					super.paint(g);
				}

			};
			panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			panel.setOpaque(false);
			panel.setBorder(
					new EmptyBorder(0, BeeUIUtils.getDefaultFontSize() / 5, 0, BeeUIUtils.getDefaultFontSize() / 3));
			int gap = BeeUIUtils.getDefaultFontSize() / 2;
			editor = new JPanel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1982206320611588600L;

				@Override
				public void paint(Graphics g) {
					int s = gap;
					Graphics2D g2d = (Graphics2D) g;
					g2d.setColor(Color.WHITE);
					g2d.fillRoundRect(s, s / 2, this.getWidth() - s * 2, this.getHeight() - s, s * 3, s * 3);
					super.paint(g);
				}

			};
			FlowLayout l = new FlowLayout();
			l.setAlignment(FlowLayout.LEADING);
			editor.setLayout(l);
			editor.setOpaque(false);
			this.add(editor, BorderLayout.SOUTH);
			editor.setVisible(false);
			editor.setBorder(new EmptyBorder(0, gap, 0, gap));

			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEADING);
			panel.setLayout(layout);
			this.add(panel, BorderLayout.CENTER);

			JLabel title = new JLabel(text);
			title.setIcon(imageIcon);
			title.setFont(BeeUIUtils.getDefaultFont());

			title.setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
			panel.add(title);

			JLabel ltitle = new JLabel(node.getNodeDesc() == null ? "" : ("<html>:" + node.getNodeDesc() + "</html>"));
			ltitle.setFont(ltitle.getFont().deriveFont((float) ltitle.getFont().getSize() * 2 / 3));
			ltitle.setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
			ltitle.setForeground(Color.GRAY);
			panel.add(ltitle);

			this.setToolTipText(node.getNodeDesc());

			this.setMouseAction();
			this.setTransfer(node);

		}

		@Override
		protected void beforeTransfer(mxICell node) {
			if (node instanceof BasicNode) {
				BasicNode b = (BasicNode) node;
				BasicNodeHelper.init(b);
				// b.setUserObject(new ObjectMark(b));
			}

		}

		public String getTitle() {
			return this.text;
		}

		public BasicNode getNode() {
			return this.node;
		}

		@Override
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			editor.removeAll();
			if (selected) {

			} else {

				editor.setVisible(false);
			}
			this.updateUI();
		}

	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.USER_ACTION_ICON;
	}

}
