package com.linkstec.bee.UI.spective.basic.config;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.NamingSub;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.NamingType;
import com.linkstec.bee.UI.spective.detail.action.BeeActions.DragAndDropDragSourceListener;
import com.mxgraph.swing.util.mxSwingConstants;

public class EntityTree extends BeeTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3274492013638958090L;
	private BeeTreeNode root;

	public EntityTree(BeeTreeNode node) {
		super(node);
		this.root = node;
		this.makeContents();
		this.expandPath(new TreePath(root));
		this.setTransfer(this);
		// EntityNodeRender render = new EntityNodeRender();
		// render.getPanel().addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent e) {
		// Debug.a();
		// }
		// });
		// this.setCellEditor(new EntityNodeEditor());
		// this.setEditable(true);
		// this.setCellRenderer(render);
		// this.setRowHeight(BeeUIUtils.getDefaultFontSize() * 6);
		// this.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mousePressed(MouseEvent e) {
		//
		// int row = getRowForLocation(e.getX(), e.getY());
		// // TreePath path = getPathForRow(row);
		// // BeeTreeNode node = (BeeTreeNode) path.getLastPathComponent();
		// Rectangle rect = getRowBounds(row);
		//
		// Point p = e.getPoint();
		// p.translate(-rect.x, -rect.y);
		// // Point p=SwingUtilities.convertPoint(EntityTree.this, e.getPoint(),
		// render);
		//
		// MouseEvent event = new MouseEvent(render.getPanel(), e.getID(), e.getWhen(),
		// e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
		// // EntityTree.this.dispatchEvent(event);
		// }
		//
		// });

	}

	public void makeContents() {

		NamingType[] types = NamingType.values();
		for (NamingType type : types) {
			this.addNaming(type, root);
		}

	}

	public void addNaming(NamingType type, BeeTreeNode parent) {
		BeeTreeNode node = new BeeTreeNode(type.getName());
		node.setImageIcon(BeeConstants.LABEL_ICON);
		node.setUserObject(type);
		parent.add(node);
		this.addSub(node);

	}

	private void addSub(BeeTreeNode parent) {
		NamingType type = (NamingType) parent.getUserObject();
		if (type.getType() == NamingType.TYPE_FIXED) {
			return;
		}

		NamingSub[] all = NamingSub.All();
		NamingSub[] first = NamingSub.First();

		for (NamingSub sub : all) {
			BeeTreeNode node = new BeeTreeNode(sub.getName());
			node.setUserObject(sub);
			parent.add(node);
			node.setImageIcon(BeeConstants.STRING_ICON);

			for (NamingSub f : first) {
				BeeTreeNode s = new BeeTreeNode(f.getName());
				s.setUserObject(f);
				node.add(s);
				s.setImageIcon(BeeConstants.STRING_ICON);
			}
		}

		for (NamingSub sub : first) {
			BeeTreeNode node = new BeeTreeNode(sub.getName());
			node.setUserObject(sub);
			parent.add(node);
			node.setImageIcon(BeeConstants.STRING_ICON);

			for (NamingSub f : all) {
				BeeTreeNode s = new BeeTreeNode(f.getName());
				s.setUserObject(f);
				node.add(s);
				s.setImageIcon(BeeConstants.STRING_ICON);
			}
		}

	}

	public void setTransfer(JTree comp) {
		DragGestureListener dragGestureListener = new DragGestureListener() {
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e) {
				JTree tree = (JTree) e.getComponent();
				Object obj = null;

				TreePath path = tree.getSelectionPath();
				BeeTreeNode dragTreeNode = null;
				if (path != null) {
					dragTreeNode = (BeeTreeNode) path.getLastPathComponent();
					if (dragTreeNode != null) {
						obj = dragTreeNode.getUserObject();
					}
				}

				if (obj != null) {
					if (obj instanceof NamingType) {
						NamingType type = (NamingType) obj;
						EntityTransferable t = new EntityTransferable(type);
						e.startDrag(DragSource.DefaultMoveDrop, mxSwingConstants.EMPTY_IMAGE, new Point(), t, new DragAndDropDragSourceListener());
					} else if (obj instanceof NamingSub) {
						NamingType type = getNamingType(dragTreeNode);
						EntityTransferable t = new EntityTransferable(type);
						e.startDrag(DragSource.DefaultMoveDrop, mxSwingConstants.EMPTY_IMAGE, new Point(), t, new DragAndDropDragSourceListener());
					}
				}

			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY, dragGestureListener);
	}

	public NamingType getNamingType(BeeTreeNode node) {
		NamingSub sub = (NamingSub) node.getUserObject();
		BeeTreeNode p = (BeeTreeNode) node.getParent();
		Object obj = p.getUserObject();
		if (obj instanceof NamingType) {
			NamingType type = (NamingType) obj;
			type = (NamingType) type.clone();
			type.getSubs().clear();
			type.getSubs().add(sub);
			return type;
		} else {
			// NamingSub s = (NamingSub) obj;
			NamingType type = this.getNamingType(p);
			type.getSubs().add(sub);
			return type;

		}
	}

}
