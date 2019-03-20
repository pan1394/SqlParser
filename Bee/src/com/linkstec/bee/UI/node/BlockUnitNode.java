package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BModifiedBlock;

public class BlockUnitNode extends BLockNode implements BModifiedBlock, Serializable, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2087783674330035753L;
	private String label;
	private BValuable variable;

	public BlockUnitNode() {
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	private int mods;

	@Override
	public void setMods(int mods) {
		this.mods = mods;
		if (Modifier.isStatic(mods)) {
			this.addStyle("strokeColor=E8F1FB");
		}
		if (Modifier.isSynchronized(mods)) {

			this.addStyle("strokeColor=E8F1FB");
			if (this.variable != null) {
				Connector cell = new Connector();
				cell.setStyle(cell.getStyle() + ";fillColor=E8F1FB;color=white");
				cell.setValue(this.variable);
				this.insert(cell);
			}
		}
	}

	@Override
	public int getMods() {
		return this.mods;
	}

	@Override
	public void setVariable(BValuable obj) {
		this.variable = obj;
	}

	@Override
	public BValuable getVariable() {
		return this.variable;
	}

	@Override
	public String getNodeDesc() {
		return "処理の最初に変数を作っておいたり、マルチスレッドを実施したりする場合に利用する";
	}

	@Override
	public void makeDefualtValue(Object target) {

	}
}
