package com.linkstec.bee.UI.node.view;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.node.BasicNode;
import com.mxgraph.model.mxGeometry;

public class LabelNode extends BasicNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8530553007096951355L;

	public LabelNode() {
		this.addStyle("fontColor=black");
		mxGeometry geo = new mxGeometry(0, 0, BeeUIUtils.getDefaultFontSize() * 2, BeeConstants.LINE_HEIGHT);
		geo.setRelative(true);
		this.setGeometry(geo);
		this.setOpaque(false);
	}

	public String toString() {
		if (this.getChildCount() == 0) {
			return super.toString();
		} else {
			return "";
		}
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_NOTE_ICON;
	}
}
