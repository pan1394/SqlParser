package com.linkstec.bee.UI.node.view;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.BDropAction;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class ObjectNode extends BasicNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7046911135727842622L;

	public ObjectNode() {
		mxGeometry g = new mxGeometry(0, 0, 50, BeeConstants.LINE_HEIGHT);
		g.setRelative(true);
		HorizonalLayout h = new HorizonalLayout();
		h.setContainerPack(true);
		this.setLayout(h);
		this.addStyle("verticalAlign=middle");
		this.setOpaque(false);
		this.addStyle("overflow=hidden");
		this.setGeometry(g);
		this.setConnectable(false);
		this.setEditable(false);
		this.setDeleteable(false);
		this.setSelecteable(false);
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.TYPE_OBJECT_ICON;
	}

	@Override
	public DropAction getDropAction() {
		mxICell parent = this.getParent();
		if (parent != null && parent instanceof BasicNode) {
			BasicNode node = (BasicNode) parent;
			BDropAction action = new BDropAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onDrop(BasicNode source, BeeGraphSheet sheet, int index) {
					if (source instanceof BValuable && source instanceof BasicNode) {

						if (source.getParent() != null) {
							source.removeFromParent();
						}

						BasicNode b = (BasicNode) source;
						if (b instanceof ReferNode) {
							ReferNode referNode = (ReferNode) b;
							ReferNode p = this.findReferNode(node);
							if (p != null) {
								BasicNode node = LayoutUtils.makeInvokerChild(referNode, p, 0, p, true);
								ObjectNode.this.replace(node);
								node.makeBorder();
							} else {
								setValue(source);

							}
						} else {
							setValue(source);

						}

					}
				}

				private ReferNode findReferNode(BasicNode node) {
					if (node instanceof ReferNode) {
						return (ReferNode) node;
					}
					mxICell parent = node.getParent();
					if (parent != null && parent instanceof BasicNode) {
						return this.findReferNode((BasicNode) parent);
					}
					return null;
				}

				@Override
				public boolean isDropTarget(BasicNode source) {
					if (source instanceof BValuable) {
						return true;
					}
					return false;
				}

			};
			return action;
		}
		return null;
	}

}
