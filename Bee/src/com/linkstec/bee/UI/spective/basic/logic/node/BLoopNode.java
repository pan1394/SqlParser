package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.basic.BLoopLogic;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ILoopCell;
import com.mxgraph.view.mxCellState;

public class BLoopNode extends BLogicNode implements ILoopCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6600303244803236280L;

	public BLoopNode(BLoopLogic logic) {
		super(logic);
		logic.getPath().setCell(this);
		this.setStyle("strokeWidth=0.5;strokeColor=gray;fillColor=F0F8FF;");
		this.getGeometry().setWidth(300);

	}

	public Object getValue() {
		return null;
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public boolean isDropTarget(BNode source) {
		return true;
	}

	@Override
	public void paint(Graphics g, mxCellState state) {
		Rectangle rect = state.getRectangle();
		g.setColor(Color.BLACK);

		FontMetrics mericts = g.getFontMetrics();

		int height = mericts.getHeight();
		Image img = BeeConstants.P_LOOP_ICON.getImage();
		g.drawImage(img, rect.x + height / 3, rect.y + 10, height, height, null);
		g.drawString(logic.getDesc(), (int) (rect.x + height * 1.5), rect.y + mericts.getAscent() + 10);

		height = (int) (height * 1.6);

		g.drawLine(rect.x, rect.y + height, rect.x + rect.width, rect.y + height);
	}

	@Override
	public ILogicCell getStart() {
		return BasicGenUtils.getStart(this);
	}

}
