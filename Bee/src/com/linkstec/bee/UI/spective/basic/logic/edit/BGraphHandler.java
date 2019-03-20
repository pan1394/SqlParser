package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphComponent.mxGraphControl;
import com.mxgraph.swing.handler.mxCellMarker;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BGraphHandler extends mxGraphHandler {

	private Object draggingOver = null;
	// private Point draggingPoint = null;
	private int dragMarkerHeight = 10;
	private int dragMarkerMargin = 10;
	private Rectangle dragDropMarker = null;

	private DragDropPosition position;
	private BNode targetScope;

	public BGraphHandler(mxGraphComponent graphComponent) {
		super(graphComponent);
		position = new DragDropPosition();
	}

	@Override
	public void dragOver(DropTargetDragEvent e) {

		super.dragOver(e);
		makeMarker(e);
	}

	public Rectangle getDragDropMarker() {
		return dragDropMarker;
	}

	private boolean makeMarker(DropTargetDragEvent e) {
		mxGraphComponent graph = this.getGraphComponent();

		Point pt = SwingUtilities.convertPoint(graphComponent, e.getLocation(), graphComponent.getGraphControl());

		pt = graphComponent.snapScaledPoint(new mxPoint(pt)).getPoint();
		draggingOver = graph.getCellAt(pt.x, pt.y);

		if (dragDropMarker != null) {
			Rectangle r = dragDropMarker;
			dragDropMarker = null;
			graph.getGraphControl().repaint(r);
		}

		mxCellMarker marker = this.getMarker();
		if (this.dragCells != null) {
			if (this.dragCells.length != 1) {
				return false;
			}
			BNode source = null;
			if (!(this.dragCells[0] instanceof BasicNode)) {
				return false;
			} else {
				Object obj = this.dragCells[0];
				if (obj instanceof BNode) {
					source = (BNode) obj;
				}
			}

			if (source == null) {
				return false;
			}

			this.targetScope = null;
			String scopeID = (String) source.getUserAttribute("SCOPE_ID");
			if (scopeID != null) {
				BasicModel model = (BasicModel) graphComponent.getGraph().getModel();
				mxICell scope = model.getPossibleCellById(scopeID);
				if (scope instanceof BNode) {
					this.targetScope = (BNode) scope;
				}
			}

			double scale = graphComponent.getPageScale();
			int hheight = (int) (this.dragMarkerHeight * scale);
			int hmargin = (int) (this.dragMarkerMargin * scale);

			boolean blockLine = false;
			if (marker != null) {
				mxCellState state = marker.getMarkedState();
				if (state != null) {
					blockLine = true;
					Object object = state.getCell();
					if (object instanceof BLockNode) {
						position.setParent(object);
						BLockNode node = (BLockNode) object;

						Rectangle rect = new Rectangle(0, 0, graph.getGraphControl().getWidth(), pt.y);
						Object[] cells = graph.getCells(rect, object);

					}
				}
			}

			if (!blockLine) {
				if (draggingOver == null && this.targetScope == null) {

					if (!(source instanceof BValuable || source instanceof BNote)) {
						return false;
					}

					position.setParent(graph.getGraph().getDefaultParent());
					Rectangle rect = new Rectangle(0, 0, graph.getGraphControl().getWidth(), (int) (pt.y));

					Object[] cells = graph.getCells(rect);

					BasicNode target = null;
					if (cells != null && cells.length > 0) {
						for (int i = cells.length - 1; i >= 0; i--) {
							if (cells[i] instanceof BasicNode) {
								BasicNode node = (BasicNode) cells[i];
								if (!(node.getParent() instanceof BasicNode)) {

									target = node;
									position.setIndex(getIndex(target));
									break;

								}
							}
						}
						if (target != null) {
							mxRectangle box = graph.getGraph().getBoundingBox(target);
							dragDropMarker = new Rectangle((int) box.getX(),
									(int) box.getY() + (int) box.getHeight() + hmargin / 2, (int) box.getWidth(),
									hheight);
						} else {
							Debug.a();
						}
					} else {

						mxICell root = (mxICell) graph.getGraph().getDefaultParent();
						boolean hasHeader = false;
						if (root.getChildCount() > 0) {
							mxICell first = root.getChildAt(0);
							if (first instanceof BClassHeader) {
								mxRectangle box = graph.getGraph().getBoundingBox(first);
								dragDropMarker = new Rectangle((int) box.getX(),
										(int) box.getY() + (int) box.getHeight() + hmargin / 2, (int) box.getWidth(),
										hheight);
								position.setIndex(1);
								hasHeader = true;
							}
						}
						if (!hasHeader) {
							position.setIndex(0);
							dragDropMarker = new Rectangle((int) (BeeConstants.PAGE_SPACING_LEFT * scale), hmargin / 2,
									(int) (BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH * scale), hheight);
						}
					}
				}
			}
		}

		if (dragDropMarker != null) {
			if (this.targetScope != null) {
				graph.getGraphControl().repaint();
			} else {
				graph.getGraphControl().repaint(dragDropMarker);
			}
		} else {
			graph.getGraphControl().repaint();
		}

		return true;
	}

	private int getIndex(mxICell cell) {
		mxICell parent = cell.getParent();
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			if (parent.getChildAt(i).equals(cell)) {
				return i + 1;
			}
		}
		return -1;
	}

	private boolean inScope(BNode node) {
		if (this.targetScope != null) {
			while (node != null) {
				if (node.equals(targetScope)) {
					return true;
				}
				mxICell parent = node.getParent();
				if (parent instanceof BasicNode) {
					node = (BNode) parent;
				} else {
					node = null;
				}
			}
		}
		return true;
	}

	@Override
	public void dragExit(DropTargetEvent e) {
		super.dragExit(e);
		dragDropMarker = null;
		this.targetScope = null;
	}

	@Override
	public void drop(DropTargetDropEvent e) {
		super.drop(e);
		dragDropMarker = null;
		targetScope = null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

	}

	public BNode getTargetScope() {
		return targetScope;
	}

	public DragDropPosition getDropPosition() {
		return this.position;
	}

	public boolean isDragging() {
		return dragCells != null;
	}

	public static class DragDropPosition {
		private Object parent;
		private int index = -1;

		public Object getParent() {
			return parent;
		}

		public void setParent(Object parent) {
			this.parent = parent;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}

	public void paintTargetMarker(Graphics g, mxGraphControl c) {
		Rectangle rect = this.getDragDropMarker();
		if (rect != null) {
			g.setColor(Color.GREEN.darker().darker());
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
		if (this.targetScope != null) {
			Rectangle all = new Rectangle(0, 0, c.getWidth(), c.getHeight());
			mxRectangle box = graphComponent.getGraph().getBoundingBox(targetScope);
			Area allArea = new Area(all);
			Area scope = new Area(box.getRectangle());
			allArea.subtract(scope);

			Color color = Color.GRAY;
			Color back = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);

			g.setColor(back);
			Graphics2D g2d = (Graphics2D) g;
			g2d.fill(allArea);
		}
	}

}
