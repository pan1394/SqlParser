package com.linkstec.bee.UI;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.linkstec.bee.UI.look.tab.BeeTabCloseButton;
import com.linkstec.bee.UI.look.tab.BeeTabClosingListener;
import com.linkstec.bee.UI.look.tab.BeeTabPop;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.popup.BeePopupMenuItem;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorUndo;
import com.linkstec.bee.core.fw.editor.BManager;
import com.linkstec.bee.core.fw.editor.BOutLook;
import com.linkstec.bee.core.fw.editor.BPopItem;

public class BEditorManager implements BManager, BeeTabPop, BeeTabClosingListener, MouseListener {

	private List<BPopItem> popItems = new ArrayList<BPopItem>();
	private BEditor editor;
	private BEditorUndo undo;

	public BEditorManager(BEditor editor, BEditorUndo undo) {
		this.editor = editor;
		this.undo = undo;
		editor.getContents().addMouseListener(this);
		this.installKeyhander();
	}

	public void installKeyhander() {
		new BEditorKeyboardHandler(this);
	}

	public void makeNew() {

	}

	public BEditor getEditor() {
		return editor;
	}

	private void installTabPopup() {
		popItems.clear();
		this.addPopupItem("削除", BeeConstants.DELETE_ICON, new BEditorActions.DeleteAction(editor));
		this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new BEditorActions.RefreshAction(editor));
		this.addPopupItem("プロパティ", BeeConstants.PROPERTY_ICON, new BEditorActions.FilePropertyAction(editor.getFile()));
		this.editor.makeTabPopupItems(this);
	}

	public void addPopupItem(String name, ImageIcon icon, Action action) {
		BeePopupMenuItem item = new BeePopupMenuItem();
		item.setName(name);
		item.setIcon(icon);
		item.setValue(action);
		this.popItems.add(item);
	}

	public int print(Graphics g, PageFormat f, int pageIndex, JComponent comp) throws PrinterException {
		int width = comp.getWidth();
		int height = comp.getHeight();

		// Disables double-buffering before printing
		// RepaintManager currentManager = RepaintManager.currentManager(comp);
		// currentManager.setDoubleBufferingEnabled(false);

		int margin = 30;

		double ratio = f.getWidth() / width;
		double imageHeigt = height * ratio;

		Image img = comp.createImage(width, (int) (f.getHeight() / ratio));
		Graphics gg = img.getGraphics();
		gg.setColor(Color.WHITE);
		gg.fillRect(0, 0, img.getWidth(comp), img.getHeight(comp));
		gg.translate(0, (int) (-pageIndex * f.getHeight() / ratio));
		comp.paint(gg);
		gg.dispose();

		int fw = (int) (f.getWidth() - margin * 2);
		int fh = (int) (f.getHeight() - margin * 2);
		g.drawImage(img, margin, margin, fw, fh, comp);
		g.setColor(Color.LIGHT_GRAY);
		FontMetrics metric = g.getFontMetrics();
		g.drawString(this.editor.getDisplayPath(), margin, margin - metric.getHeight() + metric.getAscent());
		g.drawRect(margin, margin, fw, fh);

		int pages = (int) (imageHeigt / f.getHeight()) + 1;
		String pagesInfo = (pageIndex + 1) + "/" + pages;
		g.drawString(pagesInfo, ((int) f.getWidth() - metric.stringWidth(pagesInfo)) / 2, (int) f.getHeight() - margin + g.getFontMetrics().getAscent());

		if (pageIndex * f.getHeight() < imageHeigt) {
			Application.getInstance().getEditor().getStatusBar().setMessag(pagesInfo + "ページ目を印刷しています…");
			return Printable.PAGE_EXISTS;
		}
		Application.getInstance().getEditor().getStatusBar().setMessag("印刷完了");
		return Printable.NO_SUCH_PAGE;

	}

	@Override
	public List<BPopItem> getMenus() {
		installTabPopup();
		return this.popItems;
	}

	public void setModified(boolean modified) {
		if (editor instanceof JComponent) {
			JComponent comp = (JComponent) editor;
			Container parent = comp.getParent();
			if (parent instanceof BeeTabbedPane) {
				BeeTabbedPane pane = (BeeTabbedPane) parent;
				int index = pane.indexOfComponent(comp);
				BeeTabCloseButton button = (BeeTabCloseButton) pane.getTabComponentAt(index);
				button.setModified(modified);
			}
		}
	}

	public boolean isModified() {
		return this.editor.isModified();
	}

	public BEditorUndo getUndo() {
		return this.undo;
	}

	@Override
	public boolean tabClosing(int index, JTabbedPane pane) {
		return false;
	}

	@Override
	public boolean selectTabBeforeClosing(int aTabIndex, JTabbedPane pane) {

		if (!this.isModified()) {
			BOutLook outlook = this.editor.getOutlookExplore();
			if (outlook != null) {
				outlook.update();
			}
			return true;
		}

		int isDelete = JOptionPane.showConfirmDialog(Application.FRAME, editor.getLogicName() + "は修正されています。保存してクローズしますか？", "確認", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, BeeConstants.WARNING_ICON);
		if (isDelete == JOptionPane.YES_OPTION) {
			editor.save();
			BOutLook outlook = this.editor.getOutlookExplore();
			if (outlook != null) {
				outlook.update();
			}
			return true;
		} else if (isDelete == JOptionPane.CANCEL_OPTION) {
			return false;
		} else if (isDelete == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Application.getInstance().setCurrentEditor(editor);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
