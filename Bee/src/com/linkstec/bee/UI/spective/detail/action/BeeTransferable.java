package com.linkstec.bee.UI.spective.detail.action;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.core.Application;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxRectangle;

public class BeeTransferable extends mxGraphTransferable implements Serializable {
	private static Object type;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8201068582178833857L;

	public BeeTransferable(Object[] cells, mxRectangle bounds) {
		super(cells, bounds);
		if (cells != null && cells.length == 1 && cells[0] instanceof ComplexNode) {
			// Application.getLogger().info("TODO:static transfer here:" + ((ComplexNode)
			// cells[0]).getValue().hashCode());
			// BeeTransferable.type = ((ComplexNode) cells[0]).getValue();
		}
	}

	public BeeTransferable(Object[] cells, mxRectangle bounds, ImageIcon image) {
		super(cells, bounds, image);
		if (cells != null && cells.length == 1 && cells[0] instanceof ComplexNode) {
			// Application.getLogger().info("TODO:static transfer here:" + ((ComplexNode)
			// cells[0]).getValue().hashCode());
			// BeeTransferable.type = ((ComplexNode) cells[0]).getValue();
		}
	}

	public void transferStatic() {

		if (cells != null && cells.length == 1 && cells[0] instanceof ComplexNode) {
			if (BeeTransferable.type != null) {
				Application.getLogger().info("TODO:static transfered here:" + BeeTransferable.type.hashCode());
				((ComplexNode) cells[0]).setValue(BeeTransferable.type);
				BeeTransferable.type = null;
			}

			// Application.getLogger()
			// .info("TODO:getCells static transfer here:" + ((ComplexNode)
			// cells[0]).getValue().hashCode());
		}
	}

}
