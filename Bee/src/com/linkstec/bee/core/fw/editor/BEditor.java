package com.linkstec.bee.core.fw.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public interface BEditor extends Printable {

	public String getDisplayPath();

	public BFileExplorer getFileExplore();

	public BOutLook getOutlookExplore();

	public BProject getProject();

	public void setFile(File file);

	public File getFile();

	public void refresh();

	public File save();

	public BManager getManager();

	public void makeTabPopupItems(BManager manager);

	// the main component at which the key operation will be installed
	public JComponent getContents();

	public String getName();

	public String getLogicName();

	public ImageIcon getImageIcon();

	public void saveAs(ActionEvent e);

	public void deleteSelect(ActionEvent e);

	public void selectAll(ActionEvent e);

	public void setModified(boolean modified);

	public boolean isModified();

	public void onSelected();

	public void updateView();

	public BEditorModel getEditorModel();

	public PageFormat getPageFormat();

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException;

	public void setPageFormat(PageFormat format);

	public void windowDeactived();

	public void zoom(double scale);

	public Dimension getSize();

	public void beforeSave();

	public String getID();

	public void setProject(BProject project);

	public void removeErrorLine(Object cell);
}
