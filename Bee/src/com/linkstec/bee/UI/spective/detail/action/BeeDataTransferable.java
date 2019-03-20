package com.linkstec.bee.UI.spective.detail.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.plaf.UIResource;

public class BeeDataTransferable implements Transferable, UIResource, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2660257190801392335L;
	private Object data;
	private Object[] values;
	public static DataFlavor valueFlavor;

	public BeeDataTransferable(Object data) {
		this.data = data;
		valueFlavor = new DataFlavor(BeeDataCellTranferable.class, "values");
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = new DataFlavor[] { DataFlavor.stringFlavor, valueFlavor };
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.stringFlavor)) {
			return this.data;
		} else if (flavor.equals(valueFlavor)) {
			return this.values;
		}
		return null;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

}
