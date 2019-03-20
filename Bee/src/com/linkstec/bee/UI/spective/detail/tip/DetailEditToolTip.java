package com.linkstec.bee.UI.spective.detail.tip;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.popup.BeePopUI;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.tip.TipAction;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.mxgraph.util.mxPoint;

public class DetailEditToolTip extends BeePopUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1383778653094339465L;

	private static DetailEditToolTip instance;

	private DetailEditTipPane pane;

	private int x;

	private int y;

	private int width = 0;

	private int status = 0;

	private int STATUS_WATING_TOSHOW = 1;

	private int STARUS_RUNNING = 2;

	private int STATUS_OFF = 0;

	private BasicNode target;

	private int minWidth = BeeUIUtils.getDefaultFontSize() * 30;

	private boolean showPosionUp = false;

	private TooltipTimer delayTimer;

	private BeeGraphSheet bee;

	public BasicNode getTargetNode() {
		return this.target;
	}

	public DetailEditToolTip(BeeGraphSheet bee) {
		super(bee);
		this.bee = bee;
		delayTimer = new TooltipTimer(this);
		pane = new DetailEditTipPane(this);
		pane.setBackground(BACK_COLOR);
		pane.addMouseListener(adapter);
		pane.addAncestorListener(ancstorListener);
		inner.add(pane);

	}

	public void showTip(BasicNode node, int x, int y, JComponent comp) {
		this.startShow(node, x, y, comp, true);
	}

	public void hideTip() {
		this.endShow();
	}

	private BasicNode recongnize(BasicNode node) {
		if (node == null) {
			return null;
		}

		if (node.getAlert() != null) {
			return node;
		}

		if (node.getClass().equals(BasicNode.class) || node.getClass().equals(LabelNode.class)) {
			if (node.getParent() != null) {
				if (node.getParent() instanceof BasicNode) {
					return recongnize((BasicNode) node.getParent());
				}
			}
		}

		if (node.getNodeDesc() == null && node.getAlert() == null) {
			if (node.getParent() != null) {
				if (node.getParent() instanceof BasicNode) {
					return recongnize((BasicNode) node.getParent());
				}
			}
		}

		if (!(node instanceof BLogicUnit)) {
			if (node.getParent() != null) {
				if (node.getParent() instanceof BasicNode) {
					return recongnize((BasicNode) node.getParent());
				}
			}
		}
		return node;
	}

	public void showImmediatelyWithLocation(BasicNode node, int x, int y, JComponent comp) {
		this.startShow(node, x, y, comp, false);
	}

	private int offSetY = BeeUIUtils.getDefaultFontSize();

	private void startShow(BasicNode node, int ex, int ey, JComponent comp, boolean stickToNode) {
		if (bee.getCellEditor().getEditingCell() != null) {
			this.endShowEmmdiately();
			return;
		}
		if (!comp.isVisible()) {
			return;
		}

		if (!comp.isShowing()) {
			return;
		}

		Point point = comp.getLocationOnScreen();
		if (this.isVisible()) {
			Rectangle rect = this.getBounds();
			if (this.showPosionUp) {
				rect.y = rect.y - point.y;
			} else {
				rect.y = rect.y - this.offSetY - point.y;
			}
			rect.x = rect.x - point.x;
			rect.height = rect.height + this.offSetY;
			if (rect.contains(ex, ey)) {
				return;
			}
		}
		node = this.recongnize(node);
		if (target == null) {
			this.target = node;
		} else {
			this.delayTimer.stop();
			this.target = node;
			this.setVisible(false);
		}
		this.delayTimer.start(ex, ey);
		pane.setSize(new Dimension(0, 0));
		this.status = this.STATUS_WATING_TOSHOW;

		if (stickToNode) {
			mxPoint p = BeeActions.getTranslateToRoot(node);
			this.x = point.x + (int) (p.getX() * bee.getGraph().getView().getScale());
			this.y = ey + point.y + this.offSetY;
			this.width = (int) (node.getGeometry().getWidth() * bee.getGraph().getView().getScale());
		} else {
			this.x = point.x + ex;
			this.y = point.y + ey;
			this.delayTimer.stop();
			this.width = minWidth;
		}

		if (width < this.minWidth) {
			this.width = minWidth;
		}

		if (!stickToNode) {
			this.toShow(true);
		}
	}

	public void addComp(JComponent comp) {
		this.pane.addComp(comp, bee);
	}

	public void addLine() {
		this.pane.addLine();
	}

	public void addLink(TipAction action) {
		this.pane.addLink(action, bee);
	}

	public int getTipWidth() {
		return this.width;
	}

	public MouseAdapter getAdapter() {
		return this.adapter;
	}

	private void toShow(boolean force) {
		if (this.bee.isDraggingOver()) {
			return;
		}

		// if (!this.mouseIsOver() && !force) {
		// return;
		// }
		this.unstick();
		this.delayTimer.stop();
		this.setContents();

		if (this.pane.getComponentCount() == 0) {
			return;
		}
		int bottom = y + this.getHeight() + this.handlerHeight;
		int screenBottom = BeeUIUtils.getScreenSize().height;
		if (bottom > screenBottom) {
			y = y - this.getHeight() - this.offSetY - this.offSetY;
			this.showPosionUp = true;
		} else {
			this.showPosionUp = false;
		}

		if (y < 100) {
			y = 100;
		}

		int b = y + this.getHeight() + this.handlerHeight;
		if (b > screenBottom) {
			this.setSize(this.getWidth(), (screenBottom - y - this.handlerHeight) / 2);
		}

		this.setLocation(x, y);
		this.setVisible(true);
		this.status = this.STARUS_RUNNING;

	}

	private void endShow() {
		endShowEmmdiately();

	}

	public void endShowEmmdiately() {
		if (this.status != this.STATUS_OFF) {
			this.delayTimer.stop();
			this.setVisible(false);
			this.status = this.STATUS_OFF;
			this.pane.removeAll();
			this.target = null;
		}
		this.unstick();
	}

	public void setContents() {
		this.pane.removeAll();
		DetailEditTip.makeTipContents(target, bee, this);
		pane.setSize(pane.getPreferredSize());
		this.setSize(this.width, pane.getHeight() + this.borderWidth * 2 + BeeUIUtils.getDefaultFontSize() * 2);

	}

	public static class TooltipTimer implements ActionListener {

		private DetailEditToolTip tip;
		private int startX, startY;
		private JComponent source;
		private Timer timer = new Timer(1500, this);

		public TooltipTimer(DetailEditToolTip tip) {
			this.tip = tip;
			this.source = tip.bee.getGraphControl();
		}

		public void stop() {
			timer.stop();
		}

		public void start(int x, int y) {
			this.startX = x;
			this.startY = y;
			timer.start();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Point point = this.source.getMousePosition();
			if (point != null) {
				tip.toShow(false);
			}
		}

	}

}
