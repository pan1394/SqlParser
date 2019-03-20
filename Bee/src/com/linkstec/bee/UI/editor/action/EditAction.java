package com.linkstec.bee.UI.editor.action;

import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;

public interface EditAction extends Serializable {

	public ImageIcon getIcon();

	public List<BAction> getActions();
}
