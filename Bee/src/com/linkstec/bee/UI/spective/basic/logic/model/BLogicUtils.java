package com.linkstec.bee.UI.spective.basic.logic.model;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.view.LinkNode;
import com.linkstec.bee.UI.node.view.ObjectMark;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxICell;

public class BLogicUtils {

	/**
	 * 
	 * @param c
	 *            container
	 * @param n
	 *            new
	 */
	public static void replaceValue(mxICell c, BValuable n) {

		int count = c.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = c.getChildAt(i);
			if (child instanceof BValuable) {
				BValuable target = (BValuable) child;
				replace(n, target);
			} else if (child instanceof ObjectNode) {
				ObjectNode node = (ObjectNode) child;
				Object value = node.getValue();
				if (value instanceof BValuable) {
					BValuable target = (BValuable) value;
					ObjectMark mark = (ObjectMark) n.getUserObject();
					ObjectMark oldMark = (ObjectMark) target.getUserObject();
					if (oldMark != null && !n.equals(target)) {
						if (mark.getId().equals(oldMark.getId())) {
							node.setValue(n);
						}
					}
				}
			} else if (child instanceof LinkNode) {
				LinkNode node = (LinkNode) child;
				BasicNode basic = node.getLinkNode();
				replaceValue(basic, n);
			}
			replaceValue(child, n);
		}
	}

	private static void replace(BValuable newOne, BValuable oldOne) {

		ObjectMark mark = (ObjectMark) newOne.getUserObject();
		ObjectMark oldMark = (ObjectMark) oldOne.getUserObject();
		if (!newOne.equals(oldOne)) {
			if (mark.getId().equals(oldMark.getId())) {

				if (newOne instanceof BasicNode && oldOne instanceof BasicNode) {
					BasicNode nnode = (BasicNode) newOne;
					BasicNode onode = (BasicNode) oldOne;
					onode.replace(nnode);
				} else {
					Debug.d();
				}
			}
		}

	}
}
