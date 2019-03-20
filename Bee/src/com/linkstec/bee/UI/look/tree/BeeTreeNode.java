package com.linkstec.bee.UI.look.tree;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxCell;

/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 * 
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class BeeTreeNode extends DefaultMutableTreeNode implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4816704492774592665L;

	private Icon icon;

	private String tip;

	private boolean error = false;

	private boolean alert = false;

	private String display;

	private BProject project;

	private boolean expanded = false;

	private String uniqueKey;

	private boolean selected = false;

	private boolean leaf = true;

	private Hashtable<String, Object> properties = new Hashtable<String, Object>();

	public Hashtable<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Hashtable<String, Object> properties) {
		this.properties = properties;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public Icon getImgeIcon() {
		return icon;
	}

	public void setImageIcon(Icon icon) {
		this.icon = icon;
	}

	// the path at which the file present this node is saved
	private String filePath;

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private Serializable data;

	public BeeTreeNode(Serializable data) {
		super(data);
		this.data = data;
	}

	public Serializable getValue() {
		return this.data;
	}

	public mxCell getTransferNode() {
		return null;
	}

	@Override
	public void setUserObject(Object obj) {
		if (obj instanceof ILogic) {
			ILogic logic = (ILogic) obj;
			if (logic.getLogicName() == null) {
				this.error = true;
			} else {
				this.error = false;
			}
		}
		if (obj instanceof BasicNode) {
			BasicNode b = (BasicNode) obj;
			// this.error = this.hasErrorChild(b);

			BAlert alert = b.getAlertObject();
			if (alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_ERROR)) {
				setError(true);
			} else if (alert != null && alert.getType() != null && alert.getType().equals(BAlert.TYPE_WARNING)) {
				setAlert(true);
			}
		}

		super.setUserObject(obj);
	}

	private boolean hasErrorChild(BasicNode node) {
		if (node.getAlert() != null) {
			return true;
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			Object child = node.getChildAt(i);
			if (child instanceof BasicNode) {
				BasicNode b = (BasicNode) child;
				if (hasErrorChild(b)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isLeaf() {

		if (super.isLeaf()) {
			return leaf;
		} else {
			return false;
		}
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public String getDisplay() {

		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public String toString() {
		Object cell = this.getUserObject();
		if (cell != null && cell instanceof ILogic && this.getDisplay() == null) {

			ILogic logic = (ILogic) cell;
			String name = logic.getName();
			if (name != null) {
				name = name.trim();
			}

			if (logic.getLogicName() != null) {

				// name = (logic.getNumber() == null ? "" : logic.getNumber().toString()) +
				// logic.getLogicName();
				String s = logic.getLogicName();
				if (cell instanceof BMethod) {

					BMethod b = (BMethod) cell;
					List<BParameter> paras = b.getParameter();
					if (paras != null) {
						s = s + "(";
						boolean first = true;
						for (BParameter var : paras) {

							if (first) {
								first = false;
								s = s + var.getBClass().getName();
							} else {
								s = s + "," + var.getBClass().getName();
							}
						}
						s = s + ")";
					}
				}
				name = name + (": " + s);
			}

			if (cell instanceof BValuable) {
				BValuable var = (BValuable) cell;
				BClass bclass = var.getBClass();
				if (bclass != null && bclass.getName() != null) {
					name = name + ": " + bclass.getName();
				}
			}

			return name;

		} else {
			if (this.getDisplay() != null) {
				return getDisplay();
			} else {
				if (this.getValue() == null) {
					return null;
				}
				return this.getValue().toString();
			}
		}

	}

	public BeeTreeNode cloneAll() {

		BeeTreeNode node = (BeeTreeNode) super.clone();

		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTreeNode child = (BeeTreeNode) this.getChildAt(i);
			node.add(child.cloneAll());
		}
		return node;
	}

}
