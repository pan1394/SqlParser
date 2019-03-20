package com.linkstec.bee.core.impl.basic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.TransferCell;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BLogicUnit;

public class BasicLogic implements BLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1925788739920160654L;
	private BPath path;
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();

	public BasicLogic(BPath parent, ILogicCell cell) {
		path = new BPath(parent, cell);
		path.setLogic(this);
	}

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		if (userAttributes == null) {
			userAttributes = new Hashtable<Object, Object>();
		}
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		if (userAttributes == null) {
			userAttributes = new Hashtable<Object, Object>();
		} else {
			this.userAttributes.remove(key);
		}
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public boolean hasException() {
		return false;
	}

	@Override
	public boolean isReturnBoolean() {
		return false;
	}

	public ImageIcon getIcon() {
		return BeeConstants.ACTION_ICON;
	}

	@Override
	public String getDesc() {
		return "Undefined Logic";
	}

	@Override
	public List<BLogic> getSubLogics() {
		return null;
	}

	@Override
	public void setPath(BPath path) {
		this.path = path;
	}

	@Override
	public BPath getPath() {
		return this.path;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		NoteNode note = new NoteNode();
		note.setValue(this.getName() + " is Under Construction");
		units.add(note);
		return units;
	}

	@Override
	public JComponent getEditor() {
		return null;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();
		return outputs;
	}

	protected void addMark(BParameter left) {
		BLogicProvider provider = this.getPath().getProvider();

		BClass c = provider.getProperties().getCurrentDeclearedClass();
		if (c == null) {
			BPath path = this.getPath();
			BActionModel action = (BActionModel) path.getAction();
			c = BasicGenUtils.createClass(action, path.getProject());
		}

		TransferCell cell = new TransferCell(this.getPath(), c);
		left.addUserAttribute(BasicGenUtils.INVOKER_SOURCE, cell);
	}

}
