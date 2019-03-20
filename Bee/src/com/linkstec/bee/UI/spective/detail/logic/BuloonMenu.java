package com.linkstec.bee.UI.spective.detail.logic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenu;
import com.linkstec.bee.UI.editor.sidemenu.BasicMenuItem;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BuloonMenu extends BasicMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5878789407148054744L;

	public BuloonMenu(BProject project) {
		super(project);
		this.removeAll();
		JScrollPane scroll = new JScrollPane(contents) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintBorder(Graphics g) {

			}

		};
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.CENTER);
	}

	@Override
	protected void addAllItems(String text) {
		if (text == null) {
			return;
		}
	}

	@Override
	protected void init() {
		this.addItems(this);
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_TITLE_COMMENT_ICON;
	}

	public void addItems(BuloonMenu shapesPalette) {

		shapesPalette.addTemplate("長方形", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/rectangle.png")), null, 160, 120, "");
		shapesPalette.addTemplate("楕円", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/rounded.png")), "rounded=1", 160, 120, "");
		shapesPalette.addTemplate("ダブル枠長方形", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/doublerectangle.png")), "rectangle;shape=doubleRectangle", 160, 120, "");
		shapesPalette.addTemplate("エクリプス", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/ellipse.png")), "ellipse", 160, 160, "");
		shapesPalette.addTemplate("ダブルエクリプス", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/doubleellipse.png")), "ellipse;shape=doubleEllipse", 160, 160, "");
		shapesPalette.addTemplate("三角", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/triangle.png")), "triangle", 120, 160, "");
		shapesPalette.addTemplate("ロムバス", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/rhombus.png")), "rhombus", 160, 160, "");
		shapesPalette.addTemplate("横ライン", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/hline.png")), "line", 160, 10, "");
		shapesPalette.addTemplate("多辺形", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/hexagon.png")), "shape=hexagon", 160, 120, "");
		shapesPalette.addTemplate("柱状", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/cylinder.png")), "shape=cylinder", 120, 160, "");
		shapesPalette.addTemplate("アクター", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/actor.png")), "shape=actor", 120, 160, "");
		shapesPalette.addTemplate("曇", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/cloud.png")), "ellipse;shape=cloud", 160, 120, "");

		shapesPalette.addEdgeTemplate("ストライト", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/straight.png")), "straight", 120, 120, "");
		shapesPalette.addEdgeTemplate("横コネクター", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/connect.png")), null, 100, 100, "");
		shapesPalette.addEdgeTemplate("縦コネクター", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/vertical.png")), "vertical", 100, 100, "");
		shapesPalette.addEdgeTemplate("エンティティリレーション", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/entity.png")), "entity", 100, 100, "");
		shapesPalette.addEdgeTemplate("矢印", new ImageIcon(BeeEditor.class.getResource("/com/linkstec/bee/UI/images/arrow.png")), "arrow", 120, 120, "");
	}

	private void addEdgeTemplate(String string, ImageIcon imageIcon, String style, int i, int j, String value) {
		mxGeometry geometry = new mxGeometry(0, 0, i, j);
		geometry.setTerminalPoint(new mxPoint(0, i), true);
		geometry.setTerminalPoint(new mxPoint(j, 0), false);
		geometry.setRelative(true);

		mxCell cell = new mxCell(value, geometry, style + ";strokeColor=gray");
		cell.setEdge(true);

		BuloonPanel panel = new BuloonPanel(this, cell, string, imageIcon);
		this.contents.add(panel);
	}

	private void addTemplate(String string, ImageIcon imageIcon, String style, int i, int j, String value) {
		mxCell cell = new mxCell(value, new mxGeometry(0, 0, i, j), style + ";strokeColor=gray");
		cell.setVertex(true);
		BuloonPanel panel = new BuloonPanel(this, cell, string, imageIcon);
		this.contents.add(panel);

	}

	public static class BuloonPanel extends BasicMenuItem {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6219767437547932608L;

		public BuloonPanel(BasicMenu menu, mxICell cell, String text, ImageIcon icon) {
			super(menu);
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEADING);
			setLayout(layout);

			this.setMouseAction();
			this.setTransfer(cell);
			JLabel label = new JLabel();
			label.setText(text);
			label.setIcon(icon);
			this.add(label);
		}

	}

}
