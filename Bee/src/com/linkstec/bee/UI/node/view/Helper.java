package com.linkstec.bee.UI.node.view;

import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class Helper {

	public static mxCell cloneAll(mxCell cell) {

		mxCell clone = null;
		try {
			clone = (mxCell) cell.clone();
			int count = cell.getChildCount();
			for (int i = 0; i < count; i++) {
				mxCell child = (mxCell) cell.getChildAt(i);
				if (child instanceof BasicNode) {
					BasicNode b = (BasicNode) child;
					clone.insert((mxICell) b.cloneAll(), i);
				} else {
					clone.insert(cloneAll(child), i);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return clone;
	}

	public static BasicNode makeBodyLabel(String name) {
		BasicNode label = new BasicNode();
		label.setOpaque(false);
		label.setRound();
		label.makeBorder();
		mxGeometry geo = new mxGeometry(0, 0, BeeUIUtils.getDefaultFontSize() * 2, BeeConstants.LINE_HEIGHT);
		geo.setRelative(true);
		label.setGeometry(geo);
		label.setValue(name);
		// label.addStyle("align=center");
		label.addStyle("elbow=vertical");
		label.addStyle("portConstraint=west");
		label.setOffsetX(10);
		label.setOffsetY(-20);
		label.addUserAttribute("BodyTitle", "BodyTitle");

		HorizonalLayout layout = new HorizonalLayout();
		label.setLayout(layout);

		return label;
	}

	public static void findLinkers(List<BInvoker> list, BasicNode node, BasicNode container) {

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = node.getChildAt(i);

			if (child instanceof LinkNode) {
				LinkNode link = (LinkNode) child;
				BasicNode obj = link.getLinkNode();
				if (obj instanceof BInvoker && !obj.equals(container)) {
					list.add(0, (BInvoker) obj);
				}
			}
			if ((!(child instanceof IUnit)) && child instanceof BasicNode) {
				Helper.findLinkers(list, (BasicNode) child, container);
			}
		}
	}

	public static void findLinkers(List<LinkNode> list, List<BInvoker> invokers) {
		for (LinkNode link : list) {
			BInvoker invoker = findInvoker(link.getParent(), link);
			if (invoker != null) {
				invokers.add(invoker);
			} else {
				throw new RuntimeException("Linker lost");
			}
		}

	}

	private static BInvoker findInvoker(mxICell cell, LinkNode link) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);
			if (child.equals(link.getLinkNode())) {
				return (BInvoker) child;
			}
		}
		if (cell.getParent() instanceof BasicNode) {
			return findInvoker(cell.getParent(), link);
		}
		return null;
	}

	public static void findAllLinkers(List<BInvoker> list, BasicNode node) {

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = node.getChildAt(i);
			if (child instanceof ReferNode) {
				ReferNode refer = (ReferNode) child;
				if (refer.isLinker()) {
					list.add(0, refer);
				}
			}
			if (child instanceof BasicNode) {
				Helper.findAllLinkers(list, (BasicNode) child);
			}
		}
	}

	public static class Spliter extends BasicNode {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7116583599316253040L;

		public Spliter() {
			this.setFixedWidth(600);
			this.setOpaque(false);
			this.getGeometry().setHeight(BeeConstants.NODE_SPACING);
			VerticalLayout layout = new VerticalLayout();
			layout.setSpacing(0);
			this.setLayout(layout);

			BasicNode line = new BasicNode();
			line.setFixedWidth(600);
			line.getGeometry().setHeight(4);
			line.addStyle("fillColor=" + BeeConstants.ELEGANT_GREEN_COLOR);
			layout.addNode(line);
		}

		@Override
		public ImageIcon getIcon() {
			return BeeConstants.DATA_ICON;
		}

	}

}
