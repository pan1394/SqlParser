package com.linkstec.bee.UI.look.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class BeeFileTransferHander extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8732283475428601759L;
	private BeeTree tree;

	public BeeFileTransferHander(BeeTree tree) {
		this.tree = tree;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {

		try {
			Object obj = t.getTransferData(DataFlavor.javaFileListFlavor);
			if (obj != null) {
				tree.importFiles((List<File>) obj);
				return true;
			}
			obj = t.getTransferData(DataFlavor.stringFlavor);
			if (obj != null) {
				if (obj instanceof String) {
					tree.importData((String) obj);
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean canImport(TransferSupport support) {
		DataFlavor[] flavors = support.getDataFlavors();

		if (this.canImport(flavors)) {
			return tree.canImportFiles();
		}
		return false;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

		if (this.canImport(transferFlavors)) {
			return tree.canImportData();
		}
		return false;
	}

	private boolean canImport(DataFlavor[] transferFlavors) {
		for (DataFlavor df : transferFlavors) {
			if (df.isFlavorJavaFileListType()) {
				return true;
			}
			Class cls = df.getRepresentationClass();
			if (cls.equals(String.class)) {
				return true;
			}
		}
		return false;
	}
}
