package com.linkstec.bee.UI.editor.sidemenu;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.TransferHandler;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.spective.detail.action.BeeTransferable;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;

public class BasicMenuItem extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 237318571740376171L;
	protected boolean selected = false;
	protected BasicMenu menu;
	protected MenuTooltip tip;

	public BasicMenuItem(BasicMenu menu) {
		setBackground(Color.WHITE);
		this.menu = menu;
		tip = new MenuTooltip();
	}

	public void setMouseAction() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!selected)
					setBackground(BeeConstants.MOUSEOVER_BACKGROUND_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!selected)
					setBackground(Color.WHITE);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				selected = true;
				menu.setSelected(BasicMenuItem.this);
			}

		});
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (!selected) {
			setBackground(Color.WHITE);
		} else {
			setBackground(BeeConstants.SELECTED_BACKGROUND_COLOR);
		}
		this.repaint();
	}

	protected void setTransfer(Object obj) {
		setTransferHandler(new TransferHandler() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3808525782242227637L;

			public boolean canImport(JComponent comp, DataFlavor[] flavors) {
				return true;
			}
		});
		DragGestureListener dragGestureListener = new DragGestureListener() {
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e) {

				mxICell node = null;
				if (obj instanceof String) {
					String fullName = (String) obj;
					BClass bclass = CodecUtils.getClassFromJavaClass(Application.getInstance().getCurrentProject(), fullName);
					ComplexNode c = new ComplexNode();
					c.setBClass(bclass);
					node = c;

				} else if (obj instanceof mxICell) {
					node = (mxICell) obj;
				}
				if (node != null) {
					beforeTransfer(node);
					mxRectangle bounds = (mxGeometry) node.getGeometry().clone();
					BeeTransferable t = new BeeTransferable(new Object[] { node }, bounds);
					e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);
				}
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGestureListener);

	}

	protected void beforeTransfer(mxICell node) {

	}

	@Override
	public JToolTip createToolTip() {
		return tip;
	}

	public class MenuTooltip extends JToolTip {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1148872955462497632L;

		public MenuTooltip() {

		}

		@Override
		public void setTipText(String tipText) {
			super.setTipText("<html>" + tipText + "</html>");

		}

		@Override
		public void setToolTipText(String text) {
			super.setToolTipText("<html>" + text + "</html>");

		}

	}
}
