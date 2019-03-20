package com.linkstec.bee.UI.spective.basic.logic.node;

import java.io.File;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BCoverSheet;
import com.linkstec.bee.core.Application;

public class BCoverTitleNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2600491088029172331L;
	private transient BCoverSheet sheet;

	public BCoverTitleNode(BasicBookModel model, BCoverSheet sheet) {
		this.sheet = sheet;
		this.setValue(model.getName());
		this.setVertex(true);
		this.getGeometry().setWidth(610);
		this.getGeometry().setHeight(250);
		this.getGeometry().setRelative(false);
		this.getGeometry().setX(150);
		this.getGeometry().setY(200);
		this.setConnectable(false);
		this.setEditable(true);

		this.setStyle("strokeColor=gray;strokeWidth=0.5;fontSize=30;align=center;verticalAlign=middle");
	}

	@Override
	public boolean labelChanged(String label) {
		if (label != null) {

			BasicBook book = sheet.findBook();
			BasicBookModel model = book.getBookModel();
			String name = model.getName();
			if (!name.equals(label)) {
				File f = book.getFile();
				if (f.exists()) {
					f.delete();
				}
				book.setTitleLabel(label);
				model.setLogicName(label);
				model.setName(label);
				book.save();
				this.setValue(model.getName());
				sheet.getGraph().refresh();
				File file = book.getFile();
				if (file != null && file.getParentFile() != null) {
					Application.getInstance().getBasicSpective().getFileExplore().updateNode(file.getParentFile(),
							sheet.getProject());
				}
			}
		}
		return true;
	}

}
