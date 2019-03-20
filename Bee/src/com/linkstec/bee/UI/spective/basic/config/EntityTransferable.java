package com.linkstec.bee.UI.spective.basic.config;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

public class EntityTransferable implements Transferable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3736263185017698786L;

	public DataFlavor[] flavors = new DataFlavor[1];

	private Object target;

	public EntityTransferable(Object target) {
		flavors[0] = DataFlavor.stringFlavor;
		this.target = target;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(DataFlavor.stringFlavor)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

		if (flavor.equals(DataFlavor.stringFlavor)) {
			return this.target;
		}
		return null;
	}

}
