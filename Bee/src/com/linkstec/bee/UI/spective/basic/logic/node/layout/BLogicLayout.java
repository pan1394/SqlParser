package com.linkstec.bee.UI.spective.basic.logic.node.layout;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.mxgraph.util.mxUtils;

public class BLogicLayout {

	public static void layoutNodes(BasicLogicSheet sheet) {

		double height = sheet.layoutNode();

		double layoutArea = sheet.getLayoutAreaSize().getHeight();
		double pageHeight = layoutArea / sheet.getVerticalPageCount();
		double workspaceHeight = height;

		double gap = layoutArea - workspaceHeight;

		int pageSize = sheet.getVerticalPageCount();

		int gapPage = 0;
		if (gap > 0) {
			gapPage = (int) (gap / pageHeight);
		} else {
			gapPage = (int) (gap / pageHeight) - 1;
		}

		pageSize = pageSize - gapPage;

		sheet.setVerticalPageCount(pageSize + 1);
		sheet.getGraph().refresh();

		sheet.zoomTo(sheet.getGraph().getView().getScale(), false);

	}

	public static BNode reshape(BNode n, String value) {
		String styles = n.getStyle();
		if (styles.indexOf("fontSize") < 0) {
			styles = styles + ";fontSize=11";
		}
		Hashtable<String, Object> map = new Hashtable<String, Object>();
		if (styles.indexOf(";") > 0) {
			String[] ss = styles.split(";");
			for (String s : ss) {
				if (s.indexOf("=") > 0) {
					String[] values = s.split("=");
					map.put(values[0], values[1]);
				}
			}
		}

		double scale = Application.getInstance().getEditor().getToolbar().scale;

		BEditor editor = Application.getInstance().getCurrentEditor();
		if (editor instanceof BasicLogicSheet) {
			BasicLogicSheet sheet = (BasicLogicSheet) editor;
			scale = sheet.getGraph().getView().getScale();
		}
		Font font = mxUtils.getFont(map, scale);
		FontMetrics metrics = mxUtils.getFontMetrics(font);
		BNode holder = new BNode();
		holder.setGeometry(n.getGeometry());

		value = value.replace("\r\n", "");
		String oldValue = value;
		int length = value.length();
		double width = holder.getGeometry().getWidth() * 1.8;

		List<String> list = new ArrayList<String>();

		int index = 0;
		for (int i = 5; i < length; i++) {
			String s = value.substring(0, i);
			int l = metrics.stringWidth(s);
			if (l > width) {
				list.add(value.substring(0, i - 1));
				value = value.substring(i - 1);
				length = value.length();
				index = index + i - 1;
				i = 0;

			}
		}
		if (index > 0 && index < oldValue.length()) {
			list.add(oldValue.substring(index));
		}

		String finalValue = "";
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (i != list.size() - 1) {
				finalValue = finalValue + s + "\r\n";
			} else {
				finalValue = finalValue + s;
			}
		}

		if (list.size() > 1) {
			holder.setValue(finalValue);
		} else {
			holder.setValue(oldValue);
		}

		return holder;

	}
}
