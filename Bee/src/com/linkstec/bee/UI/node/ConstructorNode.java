package com.linkstec.bee.UI.node;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConstructor;

public class ConstructorNode extends MethodNode implements BConstructor, Serializable, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8403898116746676671L;
	private BClass body;

	public ConstructorNode() {
		this.setName("インスタンス作成");
		this.setLogicName("initialize");
	}

	@Override
	public String getNodeDesc() {
		return "初期化処理";
	}

	@Override
	public BClass getBClass() {
		if (this.getReturn() != null) {
			return this.getReturn().getBClass();
		}
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.INITIALIZE_ICON;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		if (this.getParent() instanceof BasicNode) {
			this.setAlert("初期処理の場所は正しくありません").setType(BAlert.TYPE_ERROR);
		}
	}

	@Override
	public void setBody(BClass bclass) {
		this.body = bclass;
	}

	@Override
	public BClass getBody() {
		return this.body;
	}

}
