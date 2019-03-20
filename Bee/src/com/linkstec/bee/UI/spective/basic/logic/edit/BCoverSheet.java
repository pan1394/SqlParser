package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BCoverActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BCoverClassNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BCoverTitleNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDesignHeader;
import com.linkstec.bee.UI.spective.basic.logic.node.BLabelNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BCoverSheet extends BasicLogicSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 255159934121791463L;

	public BCoverSheet(BCoverModel model, BProject project, SubSystem sub) {
		super(project, sub);
		this.setModel(model);
		this.updateView();
	}

	public void updateView() {
		BasicBook book = this.findBook();
		if (book != null) {
			BasicBookModel model = book.getBookModel();
			model.validateLayers(book);
			mxICell root = this.getRoot();
			int count = root.getChildCount();
			for (int i = count - 1; i >= 0; i--) {
				mxICell child = root.getChildAt(i);
				child.removeFromParent();
			}

			BasicModel thisModel = new BasicModel(this.getSub());
			thisModel.setName(model.getName());
			thisModel.setLogicName(model.getLogicName());
			BDesignHeader header = new BDesignHeader(this.getSub(), thisModel);
			root.insert(header);

			BCoverTitleNode title = new BCoverTitleNode(model, this);
			root.insert(title);

			// BCoverClassNode
			List<BClass> list = model.getLogics();

			int i = 0;
			double y = title.getGeometry().getY() + title.getGeometry().getHeight() + 50;
			if (list.size() > 0) {
				BLabelNode classTitle = this.makeTitle(y, "ロジッククラス");
				root.insert(classTitle);
				y = y + classTitle.getGeometry().getHeight();
			}
			for (BClass m : list) {
				BCoverClassNode row = new BCoverClassNode(i + 1, m);
				row.getGeometry().setX(20);
				row.getGeometry().setY(y);
				i++;
				root.insert(row);

				y = row.getGeometry().getY() + row.getGeometry().getHeight();

			}
			y = y + 30;
			list = model.getDatas();
			if (list.size() > 0) {
				BLabelNode classTitle = this.makeTitle(y, "DTOクラス");
				root.insert(classTitle);
				y = y + classTitle.getGeometry().getHeight();
			}
			i = 0;
			for (BClass m : list) {
				BCoverClassNode row = new BCoverClassNode(i + 1, m);
				row.getGeometry().setX(20);
				row.getGeometry().setY(y);
				i++;
				root.insert(row);
				y = row.getGeometry().getY() + row.getGeometry().getHeight();

			}

			int c = book.getTabCount();
			y = y + 30;

			BLabelNode actionTitle = this.makeTitle(y, "アクション");
			y = y + actionTitle.getGeometry().getHeight();

			int k = 0;

			List<BActionModel> actions = new ArrayList<BActionModel>();

			for (int ii = 0; ii < c; ii++) {
				Component comp = book.getComponentAt(ii);
				if (comp instanceof BEditor) {
					BEditor editor = (BEditor) comp;
					BEditorModel bm = editor.getEditorModel();
					if (bm instanceof BasicModel) {
						BasicModel m = (BasicModel) bm;
						List<BNode> nodes = m.getBNodes();
						for (BNode node : nodes) {
							BPath path = null;
							if (node instanceof BActionPropertyNode) {
								BActionPropertyNode a = (BActionPropertyNode) node;
								path = a.getLogic().getPath();

							} else if (node instanceof BActionNode) {
								BActionNode action = (BActionNode) node;
								BActionPropertyNode a = action.getProperty();
								if (a != null && a.getLogic() != null) {
									path = a.getLogic().getPath();
								}
							}

							if (path != null) {
								BActionModel action = (BActionModel) path.getAction();
								if (action != null) {
									if (!actions.contains(action)) {
										actions.add(action);
									}
								}
							}
						}
					}
				}
			}

			for (BActionModel action : actions) {
				BCoverActionNode anode = new BCoverActionNode(k + 1, action);
				anode.getGeometry().setX(20);
				anode.getGeometry().setY(y);
				k++;
				root.insert(anode);
				y = anode.getGeometry().getY() + anode.getGeometry().getHeight();
			}

			if (k > 0) {
				root.insert(actionTitle);
			}

			double layoutArea = this.getLayoutAreaSize().getHeight();
			double pageHeight = layoutArea / this.getVerticalPageCount();
			double workspaceHeight = y + 400;

			double gap = layoutArea - workspaceHeight;

			int pageSize = this.getVerticalPageCount();

			int gapPage = 0;
			if (gap > 0) {
				gapPage = (int) (gap / pageHeight);
			} else {
				gapPage = (int) (gap / pageHeight) - 1;
			}

			pageSize = pageSize - gapPage;

			this.setVerticalPageCount(pageSize + 1);
			graph.refresh();

			this.zoomTo(graph.getView().getScale(), false);

		}
	}

	private BLabelNode makeTitle(double y, String title) {
		BLabelNode classTitle = new BLabelNode();
		classTitle.setStyle("strokeColor=gray;fillColor=lightgray;align=center");
		classTitle.setValue(title);
		classTitle.getGeometry().setRelative(false);
		classTitle.getGeometry().setX(20);
		classTitle.getGeometry().setY(y);
		classTitle.getGeometry().setOffset(new mxPoint(0, 0));
		classTitle.getGeometry().setWidth(100);
		classTitle.getGeometry().setHeight(30);
		return classTitle;
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.BASIC_DESIGN_ICON;
	}

	@Override
	public void onSelected() {
		super.onSelected();
		new BeeThread(new Runnable() {

			@Override
			public void run() {
				updateView();
			}

		}).start();
	}

	@Override
	public boolean tabCloseable() {
		return false;
	}
}
