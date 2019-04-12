package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ReturnType;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionaryModel;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionarySheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.basic.BLayerLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLoopLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IBodyCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BLogicEditActions {
	public static void addNewFlow(BProject project, SubSystem sub) {

		BCoverModel cover = new BCoverModel(sub);
		cover.setLogicName("表紙");
		cover.setName(cover.getLogicName());
		BCoverSheet csheet = new BCoverSheet(cover, project, sub);

		BFlowModel model = new BFlowModel(sub);
		model.setName("遷移設計");
		model.setLogicName(model.getName());

		BFlowSheet c = (BFlowSheet) model.getEditor(project, null, null);
		c.setModel(model);

		BasicBookModel bm = new BasicBookModel(sub);

		bm.setName(sub.getName() + "基本設計");
		bm.setLogicName(sub.getName() + "基本設計");

		BasicBook book = new BasicBook(bm, project, Application.getInstance().getBasicSpective().getWorkspace(), sub);

		book.addTab(cover.getName(), csheet.getImageIcon(), csheet, cover.getLogicName());
		book.addTab(model.getName(), c.getImageIcon(), c, model.getLogicName());

		// add dictionary

		String path = sub.getDictionaryPath(project);
		File file = new File(path);

		BasicDataModel subDict = null;
		if (file.exists()) {
			try {
				subDict = (BasicDataModel) ObjectFileUtils.readObject(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		BasicDataDictionaryModel dm = new BasicDataDictionaryModel(null, sub);
		if (subDict != null) {
			dm.getRoot().getChildren().clear();
			List<BAssignment> vars = subDict.getVariables();
			dm.setVariables(vars, false);
			dm.add100Blank();
		}
		dm.setLogicName("データモデル");
		dm.setName("データモデル");

		BasicDataDictionarySheet data = new BasicDataDictionarySheet(dm, project, sub);
		book.addTab(dm.getName(), data.getImageIcon(), data, dm.getLogicName());

		Application.getInstance().getBasicSpective().getWorkspace().addEditor(book);
		Application.getInstance().setSpactive(BeeSpective.BASIC_DESIGN);

	}

	public static Hashtable<BPath, List<BParameter>> getUsefulData(BPath actionPath) {
		Hashtable<BPath, List<BParameter>> hash = getUsefulData(actionPath, null);

		return hash;
	}

	public static Hashtable<BPath, List<BParameter>> getUsefulData(BPath actionPath, BPath child) {

		Hashtable<BPath, List<BParameter>> hash = new Hashtable<BPath, List<BParameter>>();
		BLogic logic = actionPath.getLogic();

		BPath parent = actionPath.getParent();
		if (parent != null) {

			// this will control the first one not executed
			if (parent.getParent() != null) {
				if (logic != null) {
					BPath path = Application.getInstance().getBasicSpective().getSelection().getActionPath();
					if (path == null || !path.equals(actionPath)) {
						hash.put(actionPath, logic.getOutputs());
					}

					if (logic.getPath().getCell() instanceof IBodyCell) {
						if (child != null) {
							BNode childNode = (BNode) child.getCell();
							BNode parntNode = (BNode) actionPath.getCell();
							if (childNode.getParent().equals(parntNode.getParent())) {
								IBodyCell body = (IBodyCell) logic.getPath().getCell();
								List<BLogic> list = new ArrayList<BLogic>();
								ILogicCell s = BasicGenUtils.getStart((BNode) body);
								BasicGenUtils.makeLogics(s, list, false);
								for (BLogic l : list) {
									if (l instanceof BLoopLogic) {
										// Debug.a();
									} else {
										hash.put(l.getPath(), l.getOutputs());
									}
								}
							}
						}
					}
				}
				if (!parent.equals(actionPath)) {

					Hashtable<BPath, List<BParameter>> sub = getUsefulData(parent, actionPath);
					if (sub != null) {
						hash.putAll(sub);
					}
				}

				// just for marubeny
				BActionModel action = (BActionModel) actionPath.getAction();
				if (action.getActionDepth() == 0) {
					BLogicEditActions.makeInputParameter(logic, hash, actionPath);
				}

			} else {
				if (logic instanceof BLayerLogic) {
					BLogicEditActions.makeInputParameter(logic, hash, actionPath);
				} else {
					return getUsefulData(parent, actionPath);
				}
			}
		} else {
			BLogicEditActions.makeInputParameter(logic, hash, actionPath);
		}
		return hash;
	}

	private static void makeInputParameter(BLogic logic, Hashtable<BPath, List<BParameter>> hash, BPath actionPath) {
		if (logic instanceof BLayerLogic) {
			BLayerLogic layer = (BLayerLogic) logic;
			hash.put(actionPath, layer.getParameters());
		}
	}

	public static BEditor addNewTypeEditor(BProject project, BPath path, BasicBook book, ProcessType type) {

		String title = type.getTitle();
		if (path != null) {
			// title = title + "[" + path.getLogic().getName() + "]";
			title = path.getLogic().getName();
		}

		BActionModel action = (BActionModel) path.getAction();

		ReturnType returnType = action.getReturnType();
		if (returnType == null) {
			returnType = ReturnType.SINGLE_RETURN;
			action.setReturnType(returnType);
		}
		// logic.getPath().addUserAttribute("NES_SELECT", "NES_SELECT");
		Object select = path.getUserAttribute("NES_SELECT");
		if (select != null) {
			returnType = ReturnType.SINGLE_RETURN;
			path.removeUserAttribute("NES_SELECT");
		}

		// find model
		BPatternModel model = null;
		BEditor target = null;
		int count = book.getTabCount();
		for (int i = 0; i < count; i++) {
			Component c = book.getComponentAt(i);
			if (c instanceof BEditor) {
				BEditor editor = (BEditor) c;
				BEditorModel bm = editor.getEditorModel();
				if (bm instanceof BPatternModel) {
					BPatternModel p = (BPatternModel) bm;
					BPath a = p.getActionPath();
					if (a.equals(path)) {
						model = p;
						book.setSelectedIndex(i);
						target = editor;
					}
				}
			}
		}

		// if not found,make new model
		if (model == null) {

			if (type.getType() == ProcessType.TYPE_PROCESS_FLOW) {
				model = new BControllerModel(path);
			} else if (type.getType() == ProcessType.TYPE_PROCESS_LOGIC) {
				model = new BLogicModel(path);
			} else if (type.getType() == ProcessType.TYPE_PROCESS_TABLE) {
				if (returnType.getType() > 0 && returnType.getType() < 20) {
					model = new BTableSelectModel(path);
				} else if (returnType.getType() == ReturnType.TYPE_UPDATE) {
					model = new BTableUpdateModel(path);
				} else if (returnType.getType() == ReturnType.TYPE_INSERT) {
					model = new BTableInsertModel(path);
				} else if (returnType.getType() == ReturnType.TYPE_DELETE) {
					model = new BTableDeleteModel(path);
				}
			}
			if (model != null) {

				BEditor c = model.getEditor(project, null, null);
				Component comp = (Component) c;
				book.addTab(model.getName(), c.getImageIcon(), comp);
				book.setSelectedComponent(comp);

				if (c instanceof BPatternSheet) {
					BPatternSheet logic = (BPatternSheet) c;
					logic.insertStart(path);
					logic.layoutNode();
					logic.getGraph().refresh();
				}

				target = c;
			}
		}

		// make display
		if (model != null) {
			model.setName(title);
			model.setLogicName(title);
		}
		if (target != null) {
			book.setTitleAt(book.getSelectedIndex(), title);
		}
		return target;
	}

	public static BasicDataDictionary openDictionary(BProject project, SubSystem sub) {
		String path = sub.getDictionaryPath(project);
		File file = new File(path);

		BasicDataModel dm;
		if (file.exists()) {
			try {
				dm = (BasicDataModel) ObjectFileUtils.readObject(file);
			} catch (Exception e) {

				e.printStackTrace();
				return null;
			}
		} else {
			dm = new BasicDataModel(null);
			dm.setLogicName("Dictionary");
			dm.setName("辞書");
			try {
				ObjectFileUtils.writeObject(file, dm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BasicDataDictionary data = new BasicDataDictionary(dm, project, sub);
		return (BasicDataDictionary) Application.getInstance().getBasicSpective().getWorkspace().addEditor(data);

	}
}
