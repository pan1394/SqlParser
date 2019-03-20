package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.MapAddLogic;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.codec.basic.BasicUtils;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BMapAddNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8115371768908382012L;
	private MapAddLogic logic;

	public BMapAddNode() {

		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;rounded=1;fillColor=white");

		this.setVertex(true);
		this.getGeometry().setWidth(250);
		this.getGeometry().setHeight(40);

		MapCellKey key = new MapCellKey();

		MapCellValue v = new MapCellValue();
		this.insert(key);
		this.insert(v);
		this.resized(null);
	}

	@Override
	public Object getValue() {
		BValuable key = logic.getKey();
		BValuable v = logic.getValue();

		String s = "<";
		if (key != null) {
			s = s + BValueUtils.createValuable(key, false);
		} else {
			s = s + "key=null";
		}
		s = s + ",";
		if (v != null) {
			s = s + BValueUtils.createValuable(v, false);
		} else {
			s = s + "value=null";
		}
		s = s + ">";
		return s;

	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		double h = 40;
		mxGeometry g = this.getGeometry();
		g.setHeight(h);
		mxICell key = this.getChildAt(0);
		mxICell v = this.getChildAt(1);

		mxGeometry keyg = key.getGeometry();
		keyg.setX(0);
		keyg.setY(0);
		keyg.setHeight(h);

		mxGeometry vg = v.getGeometry();
		vg.setX(this.getGeometry().getWidth() - 40);
		vg.setY(0);
		vg.setHeight(h);

	}

	public void setLogic(MapAddLogic logic) {
		this.logic = logic;
		MapCell key = (MapCell) this.getChildAt(0);
		key.setLogic(logic);
		MapCell v = (MapCell) this.getChildAt(1);
		v.setLogic(logic);

	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return false;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	public static class MapCell extends BNode {

		/**
		 * 
		 */
		private static final long serialVersionUID = -23265780877908353L;
		protected MapAddLogic logic;

		public MapCell() {

			this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;rounded=1;fillColor="
					+ BeeConstants.ELEGANT_BRIGHTER_GREEN_COLOR);
			this.getGeometry().setWidth(40);
			this.getGeometry().setHeight(40);
			this.setVertex(true);
			this.setResizable(false);
			this.setSelectable(false);
			this.setMoveable(false);
			this.setConnectable(false);
		}

		public MapAddLogic getLogic() {
			return logic;
		}

		public void setLogic(MapAddLogic logic) {
			this.logic = logic;
		}

		@Override
		public boolean isValidDropTarget(Object[] cells) {
			return true;
		}

		@Override
		public void childAdded(BNode node, BasicLogicSheet sheet) {
			if (node instanceof BDetailNodeWrapper) {
				BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
				BasicNode detail = wrapper.getNode();
				this.cellAdded(detail);
			} else if (node instanceof BTansferHolderNode) {
				BTansferHolderNode t = (BTansferHolderNode) node;
				List<BNode> nodes = t.getNodes();
				for (BNode n : nodes) {
					this.childAdded(n, sheet);
				}
			}
			node.removeFromParent();
		}

		@Override
		public void cellAdded(mxICell cell) {
			if (cell == null) {
				return;
			}
			if (cell instanceof BValuable) {
				BValuable value = (BValuable) cell;
				BClass bclass = value.getBClass();
				if (bclass != null) {
					this.setDragValue(value);
					BClass b = bclass.cloneAll();
					if (value instanceof BVariable) {
						BVariable v = (BVariable) value;
						b.setName(v.getName());
					}
					BValuable key = logic.getKey();
					BValuable v = logic.getValue();

					if (key != null && v != null) {
						new BeeThread(new Runnable() {

							@Override
							public void run() {
								BasicUtils.findDefinedMap(key.getBClass(), v.getBClass(), logic.getTarget(),
										logic.getPath());
							}

						}).start();
					}
				}
			} else if (cell instanceof BAssignment) {
				BAssignment assign = (BAssignment) cell;
				cellAdded((mxICell) assign.getLeft());
			}
			cell.removeFromParent();

		}

		public void setDragValue(BValuable value) {

		}

		@Override
		public void resized(BasicLogicSheet sheet) {
			this.getGeometry().setWidth(40);
			this.getGeometry().setHeight(40);
		}

	}

	public static class MapCellKey extends MapCell {

		/**
		 * 
		 */
		private static final long serialVersionUID = -23265780877908353L;

		public MapCellKey() {

		}

		@Override
		public Object getValue() {
			return "K";
		}

		public void setDragValue(BValuable value) {
			this.logic.setKey(value);
		}

	}

	public static class MapCellValue extends MapCell {

		/**
		 * 
		 */
		private static final long serialVersionUID = -23265780877908353L;

		public MapCellValue() {
		}

		@Override
		public Object getValue() {
			return "V";
		}

		public void setDragValue(BValuable value) {
			this.logic.setValue(value);
		}
	}

}
