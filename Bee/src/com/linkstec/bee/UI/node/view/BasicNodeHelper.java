package com.linkstec.bee.UI.node.view;

import java.util.HashMap;
import java.util.Map;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;

public class BasicNodeHelper {
	public static void init(BasicNode node) {
		node.setId(BeeUIUtils.createID());
		node.addStyle("BID=" + node.getId());
		mxGeometry g = new mxGeometry(0, 0, 0, BeeConstants.LINE_HEIGHT);
		node.setGeometry(g);
		g.setAlternateBounds(new mxRectangle(0, 0, 200, BeeConstants.LINE_HEIGHT));
		node.setUserObject(new ObjectMark(node));
		node.setVertex(true);
		node.setConnectable(false);
	}

	public static void makeBorder(BasicNode node) {
		node.addStyle("overflow=hidden");
		node.addStyle("strokeColor=gray");
		node.addStyle("strokeWidth=0.5");
		node.addStyle("opacity=100");
		node.addStyle("overflow=hidden");
	}

	public static void setRound(BasicNode node) {
		node.addStyle("rounded=1");
		node.addStyle("arcSize=50");
		// node.addStyle("align=center");
		node.addStyle("verticalLabelPosition=middle");
		// node.addStyle("align=center");//labelPosition

		node.setSpacingLeft(5);
		node.setSpacingRight(10);
		HorizonalLayout h = new HorizonalLayout();
		h.setSpacing(0);
		node.setLayout(h);
	}

	public static void makeDottedBorder(BasicNode node) {
		node.addStyle("overflow=hidden");
		node.addStyle("strokeColor=lightgray");
		node.addStyle("strokeWidth=0.5");
		node.addStyle("opacity=100");
		node.addStyle("overflow=hidden");
	}

	public static void setTitled(BasicNode node) {
		node.addStyle("fillColor=lightgray");// + BeeConstants.ELEGANT_BLUE_COLOR);
		// BeeConstants.BLOCK_TITLE_GREDIENT_COLOR
		// this.addStyle("gradientColor=" + BeeConstants.ELEGANT_BRIGHTER_BLUE_COLOR);
		node.addStyle("opacity=100");
		node.addStyle("strokeWidth=0.5");
		node.addStyle("strokeColor=gray");
	}

	public static void setYellowTitled(BasicNode node) {
		node.addStyle("fillColor=" + BeeConstants.ELEGANT_YELLOW_COLOR);
		// this.addStyle("gradientColor=" + BeeConstants.BLOCK_TITLE_GREDIENT_COLOR);
		node.addStyle("opacity=100");
		node.addStyle("strokeWidth=0.5");
		node.addStyle("strokeColor=gray");
	}

	public static void setGreenTitled(BasicNode node) {
		node.addStyle("fillColor=" + BeeConstants.ELEGANT_GREEN_COLOR);
		// this.addStyle("gradientColor=" + BeeConstants.BLOCK_TITLE_GREDIENT_COLOR);
		node.addStyle("opacity=100");
		node.addStyle("fontColor=white");
		node.addStyle("strokeWidth=0.5");
		node.addStyle("strokeColor=gray");
	}

	public static void setEditable(boolean editable, BasicNode node) {

		if (editable) {
			node.addStyle("editable=true");
			node.addStyle("fontColor=990033");
		} else {
			node.removeStyle("editable=true");
			node.addStyle("fontColor=black");
		}
	}

	public static boolean isDropTarget(BasicNode node, BasicNode target) {
		DropAction action = target.getDropAction();
		if (action == null) {
			return false;
		}
		return action.isDropTarget(node);

	}

	public static void removeStyle(String style, BasicNode node) {
		String s = node.getStyle();
		String[] values = style.split(";");

		if (s != null) {
			String[] ss = s.split(";");
			String newStyle = "";
			for (String astyle : ss) {
				if (!astyle.contains(values[0] + "=")) {
					if (newStyle.equals("")) {
						newStyle = astyle;
					} else {
						newStyle = newStyle + ";" + astyle;
					}
				}
			}
			node.setStyle(newStyle);
		}
	}

	public static String removeStyle(String style, String s) {

		String[] values = style.split(";");

		if (s != null) {
			String newStyle = "";
			for (String astyle : values) {
				if (!astyle.contains(s + "=")) {
					if (newStyle.equals("")) {
						newStyle = astyle;
					} else {
						newStyle = newStyle + ";" + astyle;
					}
				}
			}
			style = newStyle;
		}
		return style;
	}

	public static String getStyle(String style, String s) {

		String[] values = style.split(";");

		if (s != null) {
			String[] ss = s.split(";");
			for (String astyle : ss) {
				if (astyle.contains(values[0] + "=")) {
					return astyle;
				}
			}
		}
		return "";
	}

	public static Map<String, Object> getStyleMap(String style) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] values = style.split(";");

		for (String astyle : values) {
			if (astyle.indexOf("=") > 0) {
				String[] ps = astyle.split("=");
				map.put(ps[0], ps[1]);
			} else {
				map.put(astyle, "");
			}
		}

		return map;
	}

	public static void addStyle(String style, BasicNode node) {
		if (style == null || style.equals("")) {
			return;
		}
		String s = node.getStyle();
		String[] values = style.split("=");

		if (s != null) {
			String[] ss = s.split(";");
			String newStyle = "";
			for (String astyle : ss) {
				if (!astyle.contains(values[0] + "=")) {
					if (newStyle.equals("")) {
						newStyle = astyle;
					} else {
						newStyle = newStyle + ";" + astyle;
					}
				}
			}
			newStyle = newStyle + ";" + style;
			node.setStyle(newStyle);
		} else {
			node.setStyle(style);
		}
	}

	public static void replace(mxCell cell, BasicNode node) {
		mxCell parent = (mxCell) node.getParent();
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell c = parent.getChildAt(i);
			if (c.equals(node)) {
				boolean editable = false;
				if (cell instanceof BasicNode) {
					BasicNode b = (BasicNode) cell;
					editable = b.isEditable();
				}
				String id = node.getId();
				String style = node.getStyle();
				cell.setStyle(style);
				cell.setId(id);
				cell.setGeometry((mxGeometry) node.getGeometry().clone());
				if (cell instanceof BasicNode) {
					BasicNode b = (BasicNode) cell;
					b.setEditable(editable);
					b.getUserAttributes().putAll(node.getUserAttributes());
					// b.setUserAttributes(node.getUserAttributes());
					b.applyStyle();
				}

				node.removeFromParent();

				parent.insert(cell, i);
				break;
			}
		}
	}

	public static void cloneAttributes(BasicNode b, BasicNode node) {

		String style = node.getStyle();
		style = BasicNodeHelper.removeStyle(style, "BID");
		b.setStyle("BID=" + b.getId() + ";" + style);
		b.setGeometry((mxGeometry) node.getGeometry().clone());
		b.setEditable(b.isEditable());
		b.setUserAttributes(node.getUserAttributes());
	}

	public static BasicNode getCellByObjectID(mxCell parent, String bid) {

		if (bid == null) {
			return null;
		}
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxCell cell = (mxCell) parent.getChildAt(i);
			if (cell instanceof BasicNode) {
				BasicNode node = (BasicNode) cell;
				ObjectMark mark = (ObjectMark) node.getUserObject();
				if (mark.getId().equals(bid)) {
					return node;
				}

				BasicNode c = getCellByObjectID(node, bid);
				if (c != null) {
					return c;
				}
			}
		}
		return null;
	}

	public static mxCell getCellByBID(mxCell parent, String bid) {

		if (bid == null) {
			return null;
		}
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxCell cell = (mxCell) parent.getChildAt(i);
			String style = cell.getStyle();
			if (style != null) {

				if (style.contains(bid)) {
					return cell;
				}
			}
			mxCell c = getCellByBID(cell, bid);
			if (c != null) {
				return c;
			}

		}
		return null;
	}

	public static String getString(BasicNode node) {

		String s = "";

		int count = node.getChildCount();
		if (count == 0) {
			if (node.getValue() != null) {
				return node.getValue().toString();
			} else {
				return s;
			}
		} else {

			int c = 0;
			BasicNode firstOne = null;
			for (int i = 0; i < count; i++) {
				mxCell cell = (mxCell) node.getChildAt(i);
				if (cell instanceof BasicNode) {
					c++;
					firstOne = (BasicNode) cell;
				}
			}
			if (c == 1) {
				return s + firstOne.toString();
			}

			if (node.getValue() != null) {
				return s + node.getValue().toString();
			} else {

				return s;
			}

		}
	}

	public static void reshape(BasicNode target) {

		Double fixedWidth = (Double) target.getUserAttribute("fixedWidth");
		if (fixedWidth != null) {
			target.getGeometry().setWidth(fixedWidth.doubleValue());
			return;
		}
		if (target.getValue() != null) {
			if (target.getChildCount() == 0) {
				String s = target.getValue().toString();
				s = s.replace("&#60;", "<");
				target.reshape(s);
			}
		}

	}

	public static void reshape(String label, BasicNode target) {
		if (label == null || label.equals("")) {
			if (target.isNullable()) {
				target.getGeometry().setWidth(0);
			}
			return;
		}

		Map<String, Object> style = BasicNodeHelper.getStyleMap(target.getStyle());

		mxRectangle size;
		if (target instanceof NoteNode) {
			size = mxUtils.getSizeForHtml(label, style, 1, -1);
			target.getGeometry().setHeight(size.getHeight() + 5);
		} else {
			size = mxUtils.getLabelSize(label, style, false, 1);
		}

		size.setWidth(size.getWidth() + target.getSpacingLeft() + target.getSpacingRight());
		target.getGeometry().setWidth(size.getWidth() * 1.11);

	}

	public static LinkNode findLinkNode(BasicNode node, BasicNode target) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = node.getChildAt(i);
			if (child instanceof LinkNode) {
				LinkNode link = (LinkNode) child;
				BasicNode t = link.getLinkNode();
				if (t != null && t.getId().equals(target.getId())) {

				}
				return link;
			}
			if (child instanceof BasicNode) {
				BasicNode b = (BasicNode) child;
				LinkNode l = findLinkNode(b, target);
				if (l != null) {
					return l;
				}
			}
		}
		return null;
	}

}
