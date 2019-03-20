package com.linkstec.bee.UI.spective.detail.action;

import java.awt.Point;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BLockNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.BasicNodeHelper;
import com.linkstec.bee.UI.node.view.ILink;
import com.linkstec.bee.UI.node.view.ObjectMark;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraph;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent.mxGraphControl;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class BeeHandlers {

	public static class LabelChangeHandler implements mxIEventListener {

		@Override
		public void invoke(Object sender, mxEventObject evt) {
			String name = evt.getName();
			if (name != null && name.equals(mxEvent.LABEL_CHANGED)) {

			}
		}
	}

	public static class CellRemoveHandler implements mxIEventListener {
		private BeeGraphSheet comp;

		public CellRemoveHandler(BeeGraphSheet comp) {
			this.comp = comp;
		}

		@Override
		public void invoke(Object sender, mxEventObject evt) {
			String name = evt.getName();
			if (name != null && name.equals(mxEvent.REMOVE_CELLS)) {

				Object[] cells = (Object[]) evt.getProperty("cells");
				if (cells != null && cells.length == 1) {
					return;
				}

			}
		}
	}

	public static class CellRemovedHandler implements mxIEventListener {

		private mxGraph graph;

		public CellRemovedHandler(mxGraph graph) {
			this.graph = graph;
		}

		@Override
		public void invoke(Object sender, mxEventObject evt) {
			String name = evt.getName();
			if (name != null && name.equals(mxEvent.CELLS_REMOVED)) {
				Object[] cells = (Object[]) evt.getProperty("cells");
				if (cells != null && cells.length == 1) {
					Object cell = cells[0];
					if (cell instanceof BasicNode) {
						BasicNode node = (BasicNode) cell;
						node.afterRemoved((BeeGraph) graph);
					}
				}
			}
		}
	}

	public static class CellMoveHandler implements mxIEventListener {

		mxGraph graph;
		BeeGraphSheet sheet;

		public CellMoveHandler(BeeGraphSheet sheet) {
			this.sheet = sheet;
			this.graph = sheet.getGraph();
		}

		@Override
		public void invoke(Object sender, mxEventObject evt) {

			String name = evt.getName();
			if (name != null && name.equals(mxEvent.MOVE_CELLS)) {
				Object[] cells = (Object[]) evt.getProperty("cells");

				Object target = evt.getProperty("target");
				Point location = (Point) evt.getProperty("location");
				double dx = (double) evt.getProperty("dx");
				double dy = (double) evt.getProperty("dy");

				this.process(cells, dx, dy, target, location);
			}

		}

		public void process(Object[] cells, double dx, double dy, Object target, Point location) {
			if (location == null) {
				return;
			}

			if (cells != null && cells.length == 1) {
				if (cells[0] instanceof BasicNode) {
					BasicNode node = (BasicNode) cells[0];

					if (node.getUserAttribute("MENU_ITEM_LOGIC") != null) {
						node.removeUserAttribute("MENU_ITEM_LOGIC");
						if (node instanceof IUnit) {
							IUnit unit = (IUnit) node;
							unit.makeDefualtValue(target);
						}
					}
					// if the target exists
					if (target != null) {
						if (target instanceof BasicNode) {

							BasicNode t = (BasicNode) target;

							// replace value
							if (t instanceof BValuable && node instanceof BValuable) {
								mxICell parentNode = t.getParent();
								t.replace(node);

								String id = node.getId();

								if (parentNode instanceof BasicNode) {
									BasicNode p = (BasicNode) parentNode;

									if (LayoutUtils.doInvokerLayout(p)) {

										mxCell afterReplace = p.getCellByBID(id);
										if (afterReplace != null && afterReplace instanceof BasicNode) {
											BasicNodeHelper.cloneAttributes((BasicNode) afterReplace, node);
										}
									}
								}

								LayoutUtils.makeNumber((mxICell) sheet.getGraph().getDefaultParent(), null);
								LayoutUtils.RelayoutAll(sheet);
								return;
							}
							if (t.isDropTarget(node)) {
								DropAction action = t.getDropAction();
								BasicNode b = action.beforeDrop(node, sheet);
								if (b != null) {
									node = b;
								}
								int index = ((BeeGraphHandler) sheet.getGraphHandler()).getDropPosition().getIndex();
								action.onDrop(node, sheet, index);

								LayoutUtils.makeNumber((mxICell) sheet.getGraph().getDefaultParent(), null);

							}

						} else {
							// if target is not basic node
							BasicNode bn = BLockNode.difineVar(node);
							if (bn != null) {
								node = bn;
								if (node instanceof BAssignment) {
									BAssignment a = (BAssignment) node;
									BParameter left = a.getLeft();
									if (left != null) {
										left.setModifier(Modifier.PRIVATE);
									}
								}
							}

							mxCell root = (mxCell) target;
							int index = ((BeeGraphHandler) sheet.getGraphHandler()).getDropPosition().getIndex();
							if (index >= 0) {
								root.insert(node, index);
							} else {
								root.insert(node);
							}
							// node.getGeometry().setRelative(false);
							node.onAdd(sheet);

							// if target drop into body or method
							if (bn != null) {
								LayoutUtils.makeNumber((mxICell) sheet.getGraph().getDefaultParent(), null);
							}
						}

					}

					LayoutUtils.RelayoutAll(sheet);

				}
			}
		}

	}

	public static class ConnectionListener implements mxIEventListener {
		BeeGraphSheet sheet;

		public ConnectionListener(BeeGraphSheet sheet) {
			this.sheet = sheet;
		}

		@Override
		public void invoke(Object sender, mxEventObject evt) {
			String name = evt.getName();
			if (name != null && name.equals(mxEvent.CONNECT)) {
				mxCell cell = (mxCell) evt.getProperty("cell");
				mxICell source = cell.getSource();
				mxICell target = cell.getTarget();
				if (target == null) {
					return;
				}
				// delete other edges to keep the connection is once
				if (source instanceof BasicNode) {
					BasicNode node = (BasicNode) source;

					if (node.isSingleOut()) {
						int count = node.getEdgeCount();
						for (int i = count - 1; i >= 0; i--) {
							mxICell edge = node.getEdgeAt(i);
							if (!edge.equals(cell)) {
								if (edge.getTerminal(true).equals(node)) {
									// node.removeEdge(edge, true);
									sheet.getGraph().removeCells(new Object[] { edge });
								}
							}
						}
					}
				}

				if (target instanceof BasicNode) {
					BasicNode t = (BasicNode) target;

					if (source instanceof ComplexNode) {
						ComplexNode complex = (ComplexNode) source;
						if (t.getLayout() != null) {

							t.getLayout().addNode((BasicNode) complex.cloneAll());

							// set the edge to be invisible
							cell.setStyle("opacity=0");
							t.addStyle("textOpacity=0");
						}
					}
				}
			}

		}

	}

	public static class ResizeHandler implements mxIEventListener {

		public void invoke(Object source, mxEventObject evt) {

			Application.getLogger().info("ResizeHandler");
			String name = evt.getName();

			if (name.equals(mxEvent.CELLS_RESIZED)) {

				Object[] cells = (Object[]) evt.getProperty("cells");
				Object[] bounds = (Object[]) evt.getProperty("bounds");
				int i = 0;
				for (Object c : cells) {

					if (c instanceof BasicNode) {

						mxRectangle rect = (mxRectangle) bounds[i];
						BasicNode node = (BasicNode) c;
						if (node.getStyle() != null && node.getStyle().contains("resizeble=true")) {
							node.getGeometry().setRelative(true);
						}
						node.layout(rect);
					}

					i++;
				}

			}
		}
	}

	public static class SelectedHandler implements mxIEventListener {
		BeeGraphSheet sheet;

		public SelectedHandler(BeeGraphSheet sheet) {
			this.sheet = sheet;
		}

		public void invoke(Object source, mxEventObject evt) {
			String name = evt.getName();
			Object cellObject = null;
			// Object addedObject = null;
			if (name != null && name.equals(mxEvent.CHANGE)) {
				@SuppressWarnings("unchecked")
				Collection<Object> tmp = (Collection<Object>) evt.getProperty("removed");
				if (tmp != null && tmp.size() == 1) {
					Iterator<Object> it = tmp.iterator();
					while (it.hasNext()) {
						cellObject = it.next();
					}
				}
			} else if (name != null && name.equals(mxEvent.CELLS_ADDED)) {
				Object[] tmp = (Object[]) evt.getProperty("cells");
				if (tmp != null && tmp.length == 1) {
					cellObject = tmp[0];
				}
			}
			if (cellObject != null && cellObject instanceof mxCell) {
				if (cellObject instanceof ILink) {
					ILink link = (ILink) cellObject;
					BasicNode node = (BasicNode) link.getLinkNode();
					sheet.getGraph().setSelectionCell(node);
					sheet.scrollCellToVisible(node);
					return;
				}
				ComplexNode cm = null;
				if (cellObject instanceof ComplexNode) {
					cm = (ComplexNode) cellObject;
					BValuable value = cm.getArrayObject();
					if (value != null && value instanceof ComplexNode) {
						cm = (ComplexNode) value;
					}
				} else if (cellObject instanceof ObjectNode) {
					ObjectNode on = (ObjectNode) cellObject;
					Object onv = on.getValue();
					if (onv instanceof ComplexNode) {
						cm = (ComplexNode) onv;
					}
				}
				if (cm != null) {
					String selfId = cm.getId();
					if (cm.isCaller()) {
						Object userObject = cm.getUserObject();
						if (userObject instanceof ObjectMark) {
							ObjectMark mark = (ObjectMark) userObject;
							String id = mark.getId();
							if (!selfId.equals(id)) {

								Object target = sheet.getModel().getCellById(id);
								if (target != null) {
									sheet.getGraph().setSelectionCell(target);
									sheet.scrollCellToVisible(target);
									return;
								}
							}
						}
					}
				}
				mxCell node = (mxCell) cellObject;
				if (node.isEdge()) {
					return;
				}
				if (node instanceof IUnit) {

				} else {
					IUnit member = BeeActions.getUnit(node);
					if (member != null) {
						sheet.getGraph().setSelectionCell(member);
					}
				}

				if (node instanceof BAssignment || node instanceof BMethod) {
					Application.getInstance().getDesignSpective().getOutline().setSelected((BasicNode) node);
				}
			}

			mxGraphControl comp = sheet.getGraphControl();
			int width = sheet.getGraphBorder().getWidth();
			int height = comp.getHeight();
			comp.paintImmediately(0, 0, width, height);

		}

	}

	public static class CellFoldHandler implements mxIEventListener {
		private BeeGraphSheet comp;

		public CellFoldHandler(BeeGraphSheet comp) {
			this.comp = comp;
		}

		public void invoke(Object source, mxEventObject evt) {
			String name = evt.getName();

			if (name != null && name.equals(mxEvent.CELLS_FOLDED)) {
				Object[] cells = (Object[]) evt.getProperty("cells");
				Boolean collapse = (Boolean) evt.getProperty("collapse");
				boolean c = collapse.booleanValue();
				if (cells.length == 1) {
					Object cell = cells[0];
					if (cell instanceof MethodNode) {
						MethodNode node = (MethodNode) cell;
						if (c) {
							String title = node.getTitle().getValue().toString();
							node.setValue(title);

							node.getGeometry().setWidth(BeeConstants.FOLDED_WIDTH);
							node.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
							node.addStyle("opacity=100");
						} else {
							node.setValue(null);
							node.addStyle("opacity=0");
						}
						node.setFolded(c);

					}
				}
			}
		}
	}
}
