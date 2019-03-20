package com.linkstec.bee.core.fw.editor;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public interface BManager {
	public BEditor getEditor();

	public int print(Graphics g, PageFormat f, int pageIndex, JComponent comp) throws PrinterException;

	public List<BPopItem> getMenus();

	public void setModified(boolean modified);

	public boolean isModified();

	public BEditorUndo getUndo();

	public boolean selectTabBeforeClosing(int aTabIndex, JTabbedPane pane);

	public void addPopupItem(String string, ImageIcon generateCodeIcon, Action generateSourceSingle);

}
