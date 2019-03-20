package com.linkstec.bee.UI.tip;

import java.io.Serializable;

import javax.swing.ImageIcon;

public interface TipAction extends Serializable {

	public String getTitle();

	public void clicked();

	public ImageIcon getIcon();
}
