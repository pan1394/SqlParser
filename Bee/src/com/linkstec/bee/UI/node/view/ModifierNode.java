package com.linkstec.bee.UI.node.view;

import java.lang.reflect.Modifier;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.Menuable;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ClassHeaderNode;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.sun.codemodel.JMod;

public class ModifierNode extends mxCell implements Menuable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1667649922604298950L;
	private int mod = 0;
	private boolean connected = false;

	private static int VAR = JMod.FINAL;
	private static int FIELD = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED | JMod.STATIC | JMod.FINAL | JMod.TRANSIENT | JMod.VOLATILE);
	private static int METHOD = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED | JMod.FINAL | JMod.ABSTRACT | JMod.STATIC | JMod.NATIVE | JMod.SYNCHRONIZED);
	private static int CLASS = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED | JMod.STATIC | JMod.FINAL | JMod.ABSTRACT);
	private static int INTERFACE = JMod.PUBLIC;

	private int type = 0;

	public ModifierNode(BasicNode node) {
		mxGeometry g = new mxGeometry(0, 0, 30, 40);
		g.setRelative(true);
		g.setOffset(new mxPoint(-60, 20));
		String id = BeeUIUtils.createID();
		this.setValue("");
		this.setGeometry(g);
		this.setStyle("name=indicator;id=" + id + ";shape=actor;strokeColor=gray;verticalAlign=bottom;align=center;fontSize=8;portConstraint=east");
		this.setVertex(true);
		this.setId(id);

		node.insert(this);

		Connector c = new Connector();
		c.addStyle("edgeStyle=sideToSideEdgeStyle");
		this.insert(c);
		c.setSource(this);

		if (node instanceof ClassHeaderNode) {
			ClassHeaderNode p = (ClassHeaderNode) parent;
			mod = p.getBClass().getModifier();
			type = CLASS;
			if (p.getBClass().isInterface()) {
				type = INTERFACE;
			}
		} else if (node instanceof AssignmentNode) {
			AssignmentNode p = (AssignmentNode) parent;
			mod = p.getLeft().getModifier();
			type = FIELD;
		} else if (node instanceof BConstructor) {
			type = METHOD;
		} else if (node instanceof BMethod) {
			type = METHOD;
		} else if (node instanceof BParameter) {
			type = VAR;
		}
	}

	public void connect(String bid) {
		BasicNode node = (BasicNode) this.getParent();
		Connector c = (Connector) this.getChildAt(0);
		c.setTarget(node.getCellByBID(bid));
		connected = true;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setModifier(int mod) {
		this.mod = mod;
	}

	public int getModifier() {
		return this.mod;
	}

	public EditAction getAction() {
		MixAction action = new MixAction();
		if (!Modifier.isPublic(mod)) {
			this.addAction(action, "publicと設定する", Modifier.PUBLIC, true);
		}
		if (!Modifier.isPrivate(mod)) {
			this.addAction(action, "privateと設定する", Modifier.PRIVATE, true);
		}
		if (!Modifier.isProtected(mod)) {
			this.addAction(action, "protectedと設定する", Modifier.PROTECTED, true);
		}
		if (!Modifier.isStatic(mod)) {
			this.addAction(action, "staticと設定する", Modifier.STATIC, false);
		} else {
			this.deleteAction(action, "staticを外す", Modifier.STATIC);
		}
		if (!Modifier.isAbstract(mod)) {
			this.addAction(action, "abstractと設定する", Modifier.ABSTRACT, false);
		} else {
			this.deleteAction(action, "abstractを外す", Modifier.ABSTRACT);
		}
		if (!Modifier.isSynchronized(mod)) {
			this.addAction(action, "synchronizedと設定する", Modifier.SYNCHRONIZED, false);
		} else {
			this.deleteAction(action, "synchronizedを外す", Modifier.SYNCHRONIZED);
		}
		if (!Modifier.isFinal(mod)) {
			this.addAction(action, "finalyと設定する", Modifier.FINAL, false);
		} else {
			this.deleteAction(action, "finalを外す", Modifier.FINAL);
		}
		return action;
	}

	public String getCurrent() {
		if (mod == 0) {
			return null;
		}
		return Modifier.toString(mod);
	}

	private void addAction(MixAction action, String name, int modifier, boolean firstLayer) {
		if ((modifier & ~type) != 0) {
			return;
		}
		action.addAction(name, new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3450026550016692886L;

			@Override
			public void call() {
				if (firstLayer) {
					if (Modifier.isPrivate(mod)) {
						mod = mod ^ Modifier.PRIVATE;
					} else if (Modifier.isPublic(mod)) {
						mod = mod ^ Modifier.PUBLIC;
					} else if (Modifier.isProtected(mod)) {
						mod = mod ^ Modifier.PROTECTED;
					}
				}
				mod = mod | modifier;
				updateValue();
			}

		});
	}

	private void deleteAction(MixAction action, String name, int modifier) {
		if ((modifier & ~mod) != 0) {
			return;
		}
		action.addAction(name, new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3450026550016692886L;

			@Override
			public void call() {
				mod = mod ^ modifier;
				updateValue();
			}

		});
	}

	public void updateValue() {
		mxICell parent = this.getParent();
		if (parent instanceof ClassHeaderNode) {
			ClassHeaderNode node = (ClassHeaderNode) parent;
			node.getBClass().setModifier(mod);
		} else if (parent instanceof AssignmentNode) {
			AssignmentNode node = (AssignmentNode) parent;
			node.getLeft().setModifier(mod);
		}
	}
}
