package com.linkstec.bee.UI.spective.detail.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.UI.BEditorClipboardReceiver;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxRectangle;

import sun.awt.datatransfer.ClipboardTransferable;

public class BeeTransferHandler extends mxGraphTransferHandler {

	private static mxGraphTransferable staticObject;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8773636133591166633L;

	/*
	 * test for doc ,this is BeeTransferHandler
	 * 
	 * @see
	 * com.mxgraph.swing.handler.mxGraphTransferHandler#importCells(com.mxgraph.
	 * swing.mxGraphComponent, com.mxgraph.swing.util.mxGraphTransferable, double,
	 * double)
	 */
	protected Object[] importCells(mxGraphComponent graphComponent, mxGraphTransferable gt, double dx, double dy) {
		clearStatic();
		return super.importCells(graphComponent, gt, dx, dy);
	}

	public mxGraphTransferable getStaticObject() {
		return staticObject;
	}

	public void clearStatic() {
		staticObject = null;
	}

	@Override
	public mxGraphTransferable createGraphTransferable(mxGraphComponent graphComponent, Object[] cells,
			mxRectangle bounds, ImageIcon icon) {
		mxGraphTransferable t = super.createGraphTransferable(graphComponent, cells, bounds, icon);
		staticObject = t;
		return t;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (!super.importData(c, t)) {
			if (t instanceof ClipboardTransferable) {
				ClipboardTransferable clip = (ClipboardTransferable) t;
				if (c instanceof BEditorClipboardReceiver) {
					BEditorClipboardReceiver receiver = (BEditorClipboardReceiver) c;
					try {
						Object value = clip.getTransferData(DataFlavor.stringFlavor);
						if (value instanceof String) {
							String s = (String) value;
							receiver.receiveClipboard(s);
						}
					} catch (UnsupportedFlavorException | IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		}
		return true;
	}
}
