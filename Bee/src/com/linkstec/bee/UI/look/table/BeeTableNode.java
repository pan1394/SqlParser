package com.linkstec.bee.UI.look.table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.impl.BObjectImpl;
import com.linkstec.bee.core.impl.BParameterImpl;
import com.linkstec.bee.core.impl.BTypeImpl;

public class BeeTableNode extends BObjectImpl implements BAssignment, Serializable, Cloneable, ILogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4444186033078609047L;

	protected List<BeeTableNode> children;

	private BeeTableNode parent;
	private List<Object> list = new ArrayList<Object>();

	private boolean expanded = false;
	private int columnCount;
	private int mod = Modifier.PRIVATE;
	private Object userObject;
	private BObject owener;
	private BAlert alert = new BAlert();

	public int getMod() {
		return mod;
	}

	public void setMod(int mod) {
		this.mod = mod;
	}

	public BeeTableNode(BeeTableNode parent, int columnCount) {
		this.parent = parent;
		children = new ArrayList<BeeTableNode>();

		this.columnCount = columnCount;

		list.add(this);
		for (int i = 1; i < columnCount; i++) {
			list.add("");
		}

	}

	private boolean methodRestord = false;

	@Override
	public void setMehodResotred(boolean restored) {
		this.methodRestord = restored;
	}

	@Override
	public boolean isMethodRestored() {
		return methodRestord;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public BeeTableNode getParent() {
		return parent;
	}

	public void addChild(BeeTableNode node) {
		children.add(node);
	}

	public void addChildAt(BeeTableNode node, int index) {
		children.add(index, node);
	}

	public void removeChild(BeeTableNode node) {
		children.remove(node);
	}

	public void removeChildAt(int index) {
		children.remove(index);
	}

	public void setValueAt(Object value, int column) {
		list.remove(column);
		list.add(column, value);
	}

	public void setIconAt(ImageIcon icon, int column) {
		// this.icons.put("I" + column, icon);
	}

	public ImageIcon getIconAt(int column) {
		// return this.icons.get("I" + column);
		return null;
	}

	public Object getValueAt(int column) {
		if (column > -1 && column < list.size()) {
			return list.get(column);
		} else {
			return null;
		}
	}

	public List<Object> getRowList() {
		return this.list;
	}

	public boolean hasChild() {
		return !this.children.isEmpty();
	}

	public int getChildCount() {
		return this.children.size();
	}

	public List<BeeTableNode> getChildren() {
		return this.children;
	}

	public void setChildren(List<BeeTableNode> children) {
		this.children = children;
	}

	public Object getChild(int index) {
		return this.children.get(index);
	}

	public int getIndex() {
		return this.parent.getChildren().indexOf(this);
	}

	public String toString() {
		if (parent != null) {
			String p = parent.toString();
			if (p.equals("ROOT")) {
				return Integer.toString(this.parent.getChildren().indexOf(this) + 1);
			}
			return p + "." + Integer.toString(this.parent.getChildren().indexOf(this) + 1);
		} else {
			return "ROOT";
		}
	}

	public Object clone() throws CloneNotSupportedException {
		BeeTableNode node = (BeeTableNode) super.clone();
		List<BeeTableNode> c = new ArrayList<BeeTableNode>();
		for (BeeTableNode cc : this.getChildren()) {
			c.add((BeeTableNode) cc.clone());
		}
		node.setChildren(c);

		node.parent = this.parent;
		List<Object> value = new ArrayList<Object>();

		for (Object obj : list) {
			if (obj instanceof BeeTableNode) {
				value.add(node);
			} else {
				value.add(deepCopy(obj));
			}
		}
		node.list = value;

		return node;
	}

	public static Object deepCopy(Object src) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(src);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			Object dest = in.readObject();
			return dest;
		} catch (Exception e) {
			e.printStackTrace();
			return src;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addAnnotation(BAnnotation annotation) {
		List<BAnnotation> annotations;
		Object obj = this.getValueAt(5);
		if (obj == null || (!(obj instanceof List))) {
			annotations = new ArrayList<BAnnotation>();
		} else {
			annotations = (List<BAnnotation>) obj;
		}
		this.setValueAt(annotations, 5);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BAnnotation> getAnnotations() {
		Object obj = this.getValueAt(5);
		if (obj instanceof List) {
			return (List<BAnnotation>) obj;
		} else {
			List<BAnnotation> annotations = new ArrayList<BAnnotation>();
			this.setValueAt(annotations, 5);
			return annotations;
		}
	}

	@Override
	public void setOwener(BObject object) {
		this.owener = object;
	}

	@Override
	public BObject getOwener() {
		return owener;
	}

	@Override
	public String getAlert() {
		return this.alert.getMessage();
	}

	@Override
	public BAlert setAlert(String alert) {
		this.alert = new BAlert();
		this.alert.setMessage(alert);
		return this.alert;
	}

	@Override
	public BAlert getAlertObject() {
		return this.alert;
	}

	@Override
	public void setUserObject(Object object) {
		this.userObject = object;
	}

	@Override
	public Object getUserObject() {
		return this.userObject;
	}

	@Override
	public Object cloneAll() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	public BType getParameterizedTypeValue() {
		BTypeImpl type = new BTypeImpl();
		type.setContainer();
		for (BeeTableNode node : this.children) {
			BType sub = (BType) node.getValueAt(3);
			if (sub != null) {
				sub.setParameterValue(true);
				type.addParameterizedType(sub);
			}
		}

		return type;
	}

	@Override
	public BLogiker getAssignment() {
		return null;
	}

	@Override
	public void setRight(BValuable right, BLogiker assign) {
		this.setValueAt(right, 4);
	}

	@Override
	public BValuable getRight() {
		Object obj = this.getValueAt(4);
		if (obj != null && obj instanceof BValuable) {
			return (BValuable) obj;
		}
		return null;
	}

	@Override
	public void setLabel(String label) {

	}

	@Override
	public String getLabel() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		List<BAnnotation> annotations;
		Object obj = this.getValueAt(5);
		if (obj != null) {

			annotations = (List<BAnnotation>) obj;
			annotations.remove(annotion);
		}

	}

	@Override
	public void setLeft(BParameter parameter) {
		this.setValueAt(parameter.getName(), 1);
		this.setValueAt(parameter.getLogicName(), 2);
		this.setValueAt(parameter.getBClass(), 3);

	}

	@Override
	public BParameter getLeft() {
		BParameter left = new BParameterImpl();
		String name = (String) this.getValueAt(1);
		String logicName = (String) this.getValueAt(2);
		Object obj = this.getValueAt(3);
		if (obj instanceof BClass) {
			BClass type = (BClass) obj;

			if (name != null && logicName != null && !name.equals("") && !logicName.equals("") && type != null) {
				left.setName(name);
				left.setLogicName(logicName);
				left.setBClass(type);
				left.setModifier(this.getMod());
				left.setParameterizedTypeValue(this.getParameterizedTypeValue());
				return left;
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	@Override
	public String getName() {
		Object obj = this.getValueAt(1);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}

	@Override
	public String getLogicName() {
		Object obj = this.getValueAt(2);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}

	@Override
	public NodeNumber getNumber() {
		return null;
	}

}
