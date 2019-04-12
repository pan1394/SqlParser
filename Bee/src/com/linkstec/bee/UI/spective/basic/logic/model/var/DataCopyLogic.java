package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class DataCopyLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422546061804936090L;

	private BValuable source;
	private BValuable target;

	public DataCopyLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	@Override
	public String getName() {
		String s = "移行元〔コピー元〕";
		String t = "移行先〔コピー先〕";

		if (source != null) {
			s = BValueUtils.createValuable(source, false);
		}
		if (target != null) {
			t = BValueUtils.createValuable(target, false);
		}
		return "データ移行: " + s + " ===> " + t;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.REFERENCE_ICON;
	}

	public BValuable getSource() {
		return source;
	}

	public void setSource(BValuable source) {
		this.source = source;
	}

	public BValuable getTarget() {
		return target;
	}

	public void setTarget(BValuable target) {
		this.target = target;
	}

	@Override
	public String getDesc() {
		return this.getName();
	}

	@Override
	public List<BLogicUnit> createUnit() {
		if (source == null) {
			return null;
		}
		if (target == null) {
			return null;
		}
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		BLogicProvider provider = this.getPath().getProvider();
		BInvoker invoker = provider.getDataCopyLogic();
		if (invoker == null) {
			return null;
		}

		NoteNode note = new NoteNode();
		note.setValue(this.getName());
		units.add(note);

		invoker.addParameter((BValuable) target.cloneAll());
		invoker.addParameter((BValuable) source.cloneAll());

		units.add(invoker);
		return units;

	}

}
