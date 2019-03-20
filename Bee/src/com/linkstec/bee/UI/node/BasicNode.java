package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.LayoutManager;
import com.linkstec.bee.UI.node.view.Helper;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.DropAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraph;
import com.linkstec.bee.UI.tip.TipAction;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxRectangle;

public class BasicNode extends mxCell implements Serializable, Cloneable, LayoutManager, BObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4704584635061747101L;

	private boolean folded = false;
	private BAlert alert = new BAlert();
	private boolean nullable = false;
	private boolean textOnly = false;
	private BObject owner;
	private Object userObject;
	private boolean resizeable = false;
	private boolean deleteable = true;
	private boolean selecteable = true;

	// for assign continuous
	private boolean nextToLast = false;
	private NodeNumber number;
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();

	private EditAction action;
	private ValueAction valueAction;
	private DropAction dropAction;

	public EditAction getAction() {
		return action;
	}

	public void setDropAction(DropAction dropAction) {
		this.dropAction = dropAction;
	}

	public DropAction getDropAction() {
		return dropAction;
	}

	public void setAction(EditAction action) {
		this.action = action;
	}

	public void afterReplace(BasicNode source) {

	}

	public BasicNode() {

		com.linkstec.bee.UI.node.view.BasicNodeHelper.init(this);
	}

	public boolean isNextToLast() {
		return nextToLast;
	}

	public void setNextToLast(boolean nextToLast) {
		this.nextToLast = nextToLast;
	}

	public NodeNumber getNumber() {
		return number;
	}

	public void setNumber(NodeNumber number) {
		this.number = number;
	}

	public void setFixedWidth(int width) {
		this.addUserAttribute("fixedWidth", new Double(width));
		this.addStyle("overflow=hidden");
		this.getGeometry().setWidth(width);
	}

	public void makeBorder() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.makeBorder(this);
	}

	public void setRound() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.setRound(this);
	}

	public void makeDottedBorder() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.makeDottedBorder(this);
	}

	public void setOffsetX(int x) {
		this.addUserAttribute("offsetX", x);
	}

	public void setOffsetY(int y) {
		this.addUserAttribute("offsetY", y);
	}

	public void setAbsoluteX(int x) {
		this.addUserAttribute("AbsoluteX", x);
	}

	public void setAbsoluteY(int y) {
		this.addUserAttribute("AbsoluteY", y);
	}

	public int getOffsetX() {
		Object obj = this.getUserAttribute("offsetX");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getOffsetY() {
		Object obj = this.getUserAttribute("offsetY");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getAbsoluteX() {
		Object obj = this.getUserAttribute("AbsoluteX");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getAbsoluteY() {
		Object obj = this.getUserAttribute("AbsoluteY");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	public void setRelative() {
		this.getGeometry().setRelative(true);
	}

	public ImageIcon getIcon() {
		return null;
	}

	public void setEnglishInput() {
		this.addUserAttribute("ENGLISH", "ENGLISH");
	}

	public void setTitled() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.setTitled(this);
	}

	public void setYellowTitled() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.setYellowTitled(this);
	}

	public void setGreenTitled() {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.setGreenTitled(this);
	}

	public boolean isSelecteable() {
		return selecteable;
	}

	public void setSelecteable(boolean selecteable) {
		this.selecteable = selecteable;
	}

	public boolean isTextOnly() {
		return textOnly;
	}

	public void setTextOnly(boolean textOnly) {
		this.textOnly = textOnly;
	}

	public boolean isFolded() {
		return folded;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isDeleteable() {
		return deleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}

	private boolean layoutInit = false;

	public boolean layoutInited() {
		return layoutInit;
	}

	public void keyPressed(int code) {

	}

	public void setLayoutInited(boolean init) {
		layoutInit = init;
	}

	public String getAlert() {
		if (alert == null) {
			return null;
		}
		return alert.getMessage();
	}

	public BAlert setAlert(String alert) {
		if (alert == null) {
			this.alert = null;
			return null;
		}
		this.alert = new BAlert();
		this.alert.setMessage(alert);
		return this.alert;
	}

	public void Verify(BeeGraphSheet sheet, BProject project) {

	}

	public void setFolded(boolean folded) {
		this.folded = folded;
	}

	private boolean editable = false;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		com.linkstec.bee.UI.node.view.BasicNodeHelper.setEditable(editable, this);
	}

	/**
	 * Utility method
	 * 
	 * @param name
	 * @param cell
	 */
	protected void setCellName(String name, mxCell cell) {
		cell.setStyle("name=" + name + ";" + cell.getStyle());
	}

	/**
	 * Utility method
	 * 
	 * @param name
	 * @param cell
	 */
	protected boolean isCellByname(String name, mxCell cell) {
		return (cell.getStyle().contains("name=" + name));

	}

	public boolean isResizeable() {
		return resizeable;
	}

	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
		if (resizeable) {
			this.addStyle("resizeble=true");
		} else {
			this.removeStyle("resizeble=true");
		}
	}

	private String originalColor = null;

	public String getOriginalColor() {
		return originalColor;
	}

	public void setOriginalColor(String originalColor) {
		this.originalColor = originalColor;
	}

	public boolean isDropTarget(BasicNode node) {
		if (node.getParent() != null) {
			if (this instanceof BLogicBody) {
				return true;
			} else {
				return false;
			}
		}

		if (this instanceof BValuable && node instanceof BValuable) {
			if (this instanceof BInvoker) {
				return false;
			}
			return true;
		}
		return com.linkstec.bee.UI.node.view.BasicNodeHelper.isDropTarget(node, this);

	}

	public void afterRemoved(BeeGraph graph) {

	}

	public boolean childRemovable(mxICell child, BeeGraph graph) {
		return true;
	}

	public void removeAll() {
		int count = this.getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			this.getChildAt(i).removeFromParent();
		}
	}

	public void removeStyle(String style) {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.removeStyle(style, this);
	}

	public void addStyle(String style) {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.addStyle(style, this);
	}

	public List<BInvoker> getLinkers() {
		List<BInvoker> list = new ArrayList<BInvoker>();
		Helper.findLinkers(list, this, this);
		return list;
	}

	public List<BInvoker> getAllLinkers() {
		List<BInvoker> list = new ArrayList<BInvoker>();
		Helper.findAllLinkers(list, this);
		return list;
	}

	private ILayout layout;

	@Override
	public void layout() {
		if (layout != null) {
			layout.setContainer(this);
			layout.layout();
		}
	}

	@Override
	public void layout(mxRectangle rect) {
		if (layout != null) {
			layout.setContainer(this);
			layout.layout(rect);
		}
	}

	@Override
	public void setLayout(ILayout layout) {
		this.layout = layout;
		this.layout.setContainer(this);

	}

	@Override
	public ILayout getLayout() {
		if (layout != null) {
			layout.setContainer(this);
		}
		return layout;
	}

	// for tooltip
	public String getNodeDesc() {
		return null;
	}

	// for tooltip
	public ArrayList<TipAction> getActions() {
		return null;
	}

	private boolean singleOut = false;

	// if true it will be single edage to connect to this cell
	public boolean isSingleOut() {
		return singleOut;
	}

	public void setSingleOut(boolean singleOut) {
		this.singleOut = singleOut;
	}

	// it will be overrided consider the action differs on model
	public void setModel(Object BasicModel) {
		this.setValue(BasicModel);
	}

	public mxCell getCellByBID(String bid) {
		return com.linkstec.bee.UI.node.view.BasicNodeHelper.getCellByBID(this, bid);
	}

	public BasicNode getCellByObjectID(String bid) {
		return com.linkstec.bee.UI.node.view.BasicNodeHelper.getCellByObjectID(this, bid);
	}

	// for link node;
	public void applyStyle() {

	}

	public void replace(mxCell cell) {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.replace(cell, this);
	}

	public String toString() {
		return com.linkstec.bee.UI.node.view.BasicNodeHelper.getString(this);
	}

	public void reshape() {

		com.linkstec.bee.UI.node.view.BasicNodeHelper.reshape(this);

	}

	public void reshape(String label) {
		com.linkstec.bee.UI.node.view.BasicNodeHelper.reshape(label, this);

	}

	public void setMarginRight(int spacing) {
		this.addUserAttribute("MarginRight", spacing);
	}

	public void setMarginLeft(int spacing) {
		this.addUserAttribute("MarginLeft", spacing);
	}

	public void setSpacingRight(int spacing) {
		this.addStyle("spacingRight=" + spacing);
		this.addUserAttribute("SpacingRight", spacing);
	}

	public void setSpacingLeft(int spacing) {
		this.addStyle("spacingLeft=" + spacing);
		this.addUserAttribute("SpacingLeft", spacing);
	}

	public int getMarginRight() {
		Object obj = this.getUserAttribute("MarginRight");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getMarginLeft() {
		Object obj = this.getUserAttribute("MarginLeft");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getSpacingRight() {
		Object obj = this.getUserAttribute("SpacingRight");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public int getSpacingLeft() {
		Object obj = this.getUserAttribute("SpacingLeft");
		if (obj == null) {
			return 0;
		} else {
			return (int) obj;
		}
	}

	public void setOpaque(boolean opaque) {
		if (opaque) {
			this.addStyle("opacity=100");
		} else {
			this.addStyle("opacity=0");
		}
	}

	@Override
	public void setOwener(BObject object) {
		this.owner = object;
	}

	@Override
	public BObject getOwener() {
		return this.owner;
	}

	public void makeChildTransparent() {
		makeChildTransparent(null);
	}

	public void makeChildTransparent(BasicNode parent) {
		if (parent == null) {
			parent = this;
		}
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = parent.getChildAt(i);
			if (obj instanceof BasicNode) {
				BasicNode node = (BasicNode) obj;
				node.addStyle("opacity=0");
				makeChildTransparent(node);
			}
		}
	}

	public Object cloneAll() {
		BasicNode clone = null;
		try {
			clone = (BasicNode) this.clone();
			if (this.getLayout() != null) {
				this.getLayout().setContainer(clone);
			}
			int count = this.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell child = this.getChildAt(i);
				if (child instanceof BasicNode) {
					BasicNode b = (BasicNode) child;
					clone.insert((mxICell) b.cloneAll(), i);
				} else {
					clone.insert((mxICell) child.clone(), i);
				}
			}
			// clone.parent = this.parent;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return clone;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		BasicNode obj = (BasicNode) super.clone();
		obj.setUserObject(this.userObject);
		obj.action = this.action;
		obj.userAttributes = this.userAttributes;

		return obj;
	}

	public ValueAction getValueAction() {
		return valueAction;
	}

	public void setValueAction(ValueAction valueAction) {
		this.valueAction = valueAction;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	@Override
	public BAlert getAlertObject() {
		return this.alert;
	}

	public void beforeRemoved(BeeGraph beeGraph) {

	}

	public void onAdd(BeeGraphSheet sheet) {

	}
}
