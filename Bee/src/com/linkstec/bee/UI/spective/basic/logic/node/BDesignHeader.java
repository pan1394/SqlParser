package com.linkstec.bee.UI.spective.basic.logic.node;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.node.action.BCall;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BDesignHeader extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4548573288962293855L;

	private int height = 20;
	private int top = 10;
	private int left = 10;

	public BDesignHeader(SubSystem sub, BasicModel model) {

		mxGeometry geo = this.getGeometry();
		geo.setRelative(false);
		geo.setRect(0, 0, 850, height * 2);
		this.makeHeader();
		this.makeValue(sub, model);
	}

	@Override
	public boolean isLocked() {
		return true;
	}

	private void makeHeader() {
		BLabelNode node = this.makeHeaderCell(null, "プロジェクト名", 150, top, null, true);
		node.setMoveable(false);
		node.setSelectable(false);
		node = this.makeHeaderCell(node, "サブシステム名", 150, top, null, true);
		node = this.makeHeaderCell(node, "ドキュメント名", 150, top, null, true);
		node = this.makeHeaderCell(node, "作成日", 100, top, null, true);
		node = this.makeHeaderCell(node, "作成者", 100, top, null, true);
		node = this.makeHeaderCell(node, "更新日", 100, top, null, true);
		node = this.makeHeaderCell(node, "更新者", 100, top, null, true);
	}

	private BLabelNode makeHeaderCell(BLabelNode pre, String name, int width, int top, BCall call, boolean gray) {
		if (pre == null) {
			return this.makeHeaderCell(name, width, left, top, call, gray);
		} else {
			return this.makeHeaderCell(name, width, pre.getGeometry().getOffset().getX() + pre.getGeometry().getWidth(),
					top, call, gray);
		}
	}

	private BLabelNode makeHeaderCell(String name, int width, double left, double top, BCall call, boolean gray) {
		BLabelNode node = new BLabelNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1902695451046747161L;

			@Override
			public void setValue(Object value) {
				super.setValue(value);
				if (call != null) {
					call.call(this, value);
				}
			}

		};
		if (call != null) {
			node.setEditable(true);
		}
		this.insert(node);
		node.setValue(name);
		node.setMoveable(false);
		node.setSelectable(false);
		mxGeometry geo = node.getGeometry();
		geo.setWidth(width);
		geo.setHeight(height);
		geo.setOffset(new mxPoint(left, top));
		if (gray) {
			node.setStyle(node.getStyle() + ";fillColor=lightgray");
		}
		return node;
	}

	public void nameChanged(String name) {

	}

	private void makeValue(SubSystem sub, BasicModel model) {
		String subName = sub.getName();
		String projectName = sub.getProjectName();
		BLabelNode node = this.makeHeaderCell(null, projectName, 150, top + height, null, false);
		node = this.makeHeaderCell(node, subName, 150, top + height, null, false);
		node = this.makeHeaderCell(node, model.getName(), 150, top + height, new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1274031861025539538L;

			@Override
			public void call(Object source, Object value) {
				if (value instanceof String) {
					// nameChanged((String) value);
					model.setName((String) value);
				}
			}

		}, false);

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String d = format.format(Calendar.getInstance().getTime());
		node = this.makeHeaderCell(node, d, 100, top + height, null, false);
		node = this.makeHeaderCell(node, "", 100, top + height, null, false);
		node = this.makeHeaderCell(node, d, 100, top + height, null, false);
		node = this.makeHeaderCell(node, "", 100, top + height, null, false);
	}

}
