package com.linkstec.bee.UI.node.layout;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.UI.node.view.LinkNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.node.view.Space;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraph;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class LayoutUtils {
	public static void makeBook(BookModel model) {
		List<BEditorModel> list = model.getList();
		List<BEditorModel> newList = new ArrayList<BEditorModel>();
		newList.addAll(list);
		int count = newList.size();
		for (int i = 0; i < count; i++) {
			BEditorModel bee = newList.get(i);

			if (bee instanceof BeeModel) {
				BeeModel bm = (BeeModel) bee;
				LayoutUtils.makeModel(((mxICell) bm.getRoot()).getChildAt(0), bm, model);
			}
		}
	}

	public static void makeModel(mxICell object, BeeModel model, BookModel book) {
		LayoutUtils.makeView(object);
		LayoutUtils.makeNumber(object, null);
		LayoutUtils.Relayout(object, model, model, book);
	}

	public static boolean doInvokerLayout(BasicNode parent) {

		int count = parent.getChildCount();
		boolean did = false;

		for (int i = 0; i < count; i++) {
			Object cell = parent.getChildAt(i);
			if (cell instanceof ReferNode) {
				if (parent instanceof BLogicBody) {
					continue;
				}

				ReferNode refer = (ReferNode) cell;
				if (refer.isLinker()) {
					continue;
				}
				if (refer.getInvokeChild() instanceof BVariable || refer.isData()) {
					ObjectNode node = new ObjectNode();
					node.setValue(refer);

					LayoutUtils.changeID(refer, node);

					refer.removeFromParent();
					parent.insert(node, i);
					did = true;
				} else {

					BasicNode bparent = findTopParent(parent);
					int index = 0;

					if (bparent instanceof BLogicBody && parent.getParent().equals(bparent)) {
						index = bparent.getIndex(parent);
					}
					BasicNode node = LayoutUtils.makeInvokerChild(refer, bparent, index, parent, true);

					// insert one,so the child increased
					Object inserted = node.getUserAttribute("Inserted");

					if (inserted != null) {
						// node.removeUserAttribute("Inserted");
						if (bparent.equals(parent)) {
							parent.insert(node, i + 1);
						} else {
							parent.insert(node, i);
						}
						doInvokerLayout(bparent);
						break;

					}
					parent.insert(node, i);
					did = true;
				}

			} else if (cell instanceof BasicNode) {
				doInvokerLayout((BasicNode) cell);
			}
		}
		return did;
	}

	private static BasicNode findUnit(BasicNode parent) {

		mxICell cp = (mxICell) parent.getParent();
		boolean c1 = parent instanceof IUnit;
		boolean c2 = cp != null && cp instanceof BLogicBody;
		boolean c3 = cp != null && (!(cp instanceof BasicNode));

		if (c1 && (c2 || c3)) {
			return parent;
		} else {
			if (cp != null && cp instanceof BasicNode) {
				return findUnit((BasicNode) cp);
			}
		}
		// return parent;
		return null;
	}

	public static BasicNode makeInvokerChild(ReferNode referNode, BasicNode container, int referNesPosition,
			BasicNode numberparent, boolean foundParent) {

		if (foundParent) {
			numberparent = findUnit(numberparent);
		}

		if (referNode.getInvokeChild() instanceof BVariable || referNode.isData()) {
			BValuable parent = referNode.getInvokeParent();
			if (parent instanceof ReferNode) {
				ReferNode r = (ReferNode) parent;
				if (r.isLinker()) {
					container.getLayout().addNode(r, 0);
				}
			}

			ObjectNode node = new ObjectNode();
			node.setValue(referNode);
			LayoutUtils.changeID(referNode, node);
			referNode.removeFromParent();
			return node;

		} else {

			if (numberparent == null) {
				return null;
			}

			referNode.addSpliter();
			referNode.setReturnMark(numberparent);
			NodeNumber number = new NodeNumber();
			number.setParent((IUnit) numberparent);
			Object obj = container.getUserAttribute("INUMBER");
			int pnumber = 1;
			if (obj != null) {
				pnumber = (int) obj;
				pnumber = pnumber + 1;
			}
			container.addUserAttribute("INUMBER", pnumber);
			number.setTitle("P" + pnumber);
			referNode.setNumber(number);

			referNode.setOpaque(false);
			container.getLayout().addNode(referNode, referNesPosition);

			referNode.getLayout().layout();
			LinkNode label = new LinkNode();
			label.setLinkNode(referNode);
			label.addUserAttribute("Inserted", "Inserted");

			LayoutUtils.changeID(referNode, label);

			return label;
		}
	}

	private static void changeID(BasicNode s, BasicNode t) {
		String sid = s.getId();
		String tid = t.getId();

		s.setId(tid);
		s.addStyle("BID=" + tid);

		t.setId(sid);
		t.addStyle("BID=" + sid);
	}

	private static BasicNode findTopParent(mxICell child) {
		if (child == null) {
			return null;
		}
		mxICell parent = child.getParent();
		// class member
		if ((!(parent instanceof BasicNode)) && child instanceof BasicNode) {
			return (BasicNode) child;
		}

		BasicNode b = (BasicNode) child;
		if (b.getLayout() != null) {
			if (b.getLayout() instanceof VerticalLayout) {
				if (b instanceof BLogicBody) {
					return b;
				}
				if (b instanceof IUnit && parent instanceof BLogicBody) {
					return b;
				} else {
					return findTopParent(parent);
				}
			} else {

				return findTopParent(parent);
			}
		} else {
			return findTopParent(parent);
		}

	}

	public static void layoutNode(Object object) {
		if (object == null) {
			return;
		}
		mxCell cell = (mxCell) object;
		int count = cell.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell obj = cell.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode b = (BasicNode) obj;
				if (b.isVertex()) {
					layoutNode(b);
				}
			}
		}
		if (cell instanceof BasicNode) {
			BasicNode node = (BasicNode) cell;
			if (node.getLayout() != null) {
				node.getLayout().layout();
			} else {
				node.reshape();
			}
		}
	}

	public static void RelayoutAll(BeeGraphSheet comp) {

		if (comp == null) {
			comp = Application.getInstance().getDesignSpective().getGraphSheet();
		}
		if (comp == null) {
			return;
		}

		BeeGraph graph = (BeeGraph) comp.getGraph();
		mxCell root = (mxCell) graph.getDefaultParent();
		double height = LayoutUtils.Relayout(root, null, null, null);

		height = height + 400;

		double layoutArea = comp.getLayoutAreaSize().getHeight();
		double pageHeight = layoutArea / comp.getVerticalPageCount();
		double workspaceHeight = height;

		double gap = layoutArea - workspaceHeight;

		int pageSize = comp.getVerticalPageCount();

		int gapPage = 0;
		if (gap > 0) {
			gapPage = (int) (gap / pageHeight);
		} else {
			gapPage = (int) (gap / pageHeight) - 1;
		}

		pageSize = pageSize - gapPage;

		comp.setVerticalPageCount(pageSize + 1);
		graph.refresh();

		comp.zoomTo(graph.getView().getScale(), false);

		Application.getInstance().getDesignSpective().getOutline().update();

	}

	public static void makeView(mxICell object) {
		if (object == null) {
			return;
		}
		mxCell cell = (mxCell) object;
		int count = cell.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell b = cell.getChildAt(i);
			if (b instanceof BasicNode) {

				LayoutUtils.doInvokerLayout((BasicNode) b);

			}
			makeView(b);
		}
	}

	public static void makeNumber(mxICell object, BasicNode numberParent) {
		if (object == null) {
			return;
		}
		mxCell cell = (mxCell) object;
		int count = cell.getChildCount();

		int n = 1;
		BasicNode last = null;
		for (int i = 0; i < count; i++) {
			mxICell obj = cell.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode b = (BasicNode) obj;

				if (b.isVertex()) {
					BasicNode nextParent = numberParent;
					// unit
					boolean c1 = b instanceof IUnit && !(b instanceof NoteNode);
					// position
					boolean c2 = (!(cell instanceof BasicNode)) || cell instanceof BLogicBody;
					// not refer liker
					boolean c3 = (!(b instanceof ReferNode))
							|| ((b instanceof ReferNode && !((ReferNode) b).isLinker()));
					if (c1 && c2 && c3) {
						boolean continuos = false;
						if (b instanceof ReferNode) {
							if (b.getUserAttribute("INUMBER") == null) {
								if (last != null && last instanceof ReferNode) {
									ReferNode lastRefer = (ReferNode) last;
									ReferNode thisRefer = (ReferNode) b;
									if (lastRefer.isAssign() && thisRefer.isAssign()) {
										thisRefer.removeAssignTitles();
										continuos = true;
									}
								}
							}
						}

						if (!continuos) {
							NodeNumber number = new NodeNumber();
							number.setNumber(n);
							number.setParent((IUnit) numberParent);
							n++;
							b.setNumber(number);
							nextParent = b;
						}

					}
					makeNumber(b, nextParent);
					last = b;
				}
			}

		}
	}

	private static double Relayout(mxICell root, BeeModel parent, BeeModel model, BookModel book) {
		mxICell fon = BeeActions.foundRoot(root);
		if (fon != null) {
			root = (mxCell) fon;
		}
		// for test
		// LayoutUtils.makeView(root);
		LayoutUtils.layoutNode(root);

		int count = root.getChildCount();
		double height = BeeConstants.PAGE_SPACING_TOP;

		for (int i = 0; i < count; i++) {

			if (LayoutUtils.makeNewModel(root, parent, model, book, height, i)) {
				break;
			}

			mxICell obj = root.getChildAt(i);
			if (obj instanceof BasicNode) {
				if (obj.isVertex()) {
					BasicNode node = (BasicNode) obj;

					if (!(node.getParent() instanceof BasicNode)) {
						node.getGeometry().setRelative(false);
					}

					node.getGeometry().setX(BeeConstants.NODE_SPACING + BeeConstants.PAGE_SPACING_LEFT);
					node.getGeometry().setY(height);
					height = height + node.getGeometry().getHeight() + BeeConstants.PAGE_SEGMENT_GAP;
				}
			}

		}
		return height;

	}

	private static boolean makeNewModel(mxICell root, BeeModel parent, BeeModel model, BookModel book, double height,
			int breakIndex) {
		int count = root.getChildCount();
		BeeModel newModel = null;
		if (model != null && book != null) {
			if (height > BeeConstants.PAGE_MAX_HEIGHT) {
				if (newModel == null) {
					newModel = new BeeModel();

					mxICell first = root.getChildAt(breakIndex);
					String name = parent.getName() + "(" + parent.getSubSheets().size() + ")";
					if (first instanceof IUnit) {
						IUnit unit = (IUnit) first;
						NodeNumber number = unit.getNumber();
						if (number != null) {
							String s = number.toString();
							name = parent.getName() + "(" + s.substring(0, s.length() - 2) + ")";
						}
					}

					newModel.setName(name);
					newModel.setLogicName(parent.getLogicName());
					newModel.setParentView(parent);

					parent.addSubSheet(newModel);

					Object newModelRoot = newModel.getRoot();
					mxCell newRoot = (mxCell) newModelRoot;
					book.getList().add(newModel);

					List<mxICell> cells = new ArrayList<mxICell>();
					for (int i = breakIndex; i < count; i++) {
						mxICell cell = root.getChildAt(i);
						cells.add(cell);

					}
					for (mxICell cell : cells) {
						cell.removeFromParent();
						newRoot.insert(cell);
					}
					LayoutUtils.Relayout(newRoot, parent, newModel, book);

				}
				return true;
			}
		}
		return false;
	}

	public static BValuable getValueNode(BasicNode node, String id) {
		BasicNode value = (BasicNode) node.getCellByBID(id);
		if (value == null) {
			Object parent = node.getParent();
			if (parent != null && parent instanceof BasicNode) {
				return getValueNode((BasicNode) parent, id);
			}
			return null;
		}
		return getValueNode(value);
	}

	public static BValuable getValueNode(BasicNode value) {
		if (value instanceof ObjectNode) {
			Object obj = value.getValue();
			if (obj instanceof BValuable) {
				return (BValuable) obj;
			} else if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				return getValueNode(node);
			} else {
				return null;
			}
		} else if (value instanceof LinkNode) {
			LinkNode link = (LinkNode) value;
			BasicNode node = link.getLinkNode();
			return getValueNode(node);
		} else if (value instanceof BValuable) {
			return (BValuable) value;
		} else {
			return null;
		}
	}

	public static Space makeSpace() {

		Space space = new Space();
		space.getGeometry().setHeight(50);
		space.getGeometry().setWidth(100);
		return space;

	}
}
