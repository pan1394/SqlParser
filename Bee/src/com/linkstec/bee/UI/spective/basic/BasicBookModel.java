package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BCoverSheet;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BasicNaming;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BSQLModel;
import com.linkstec.bee.core.fw.basic.ITableSqlInfo;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicBookModel implements Serializable, BEditorModel, BModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8019852087403210524L;
	private String name;
	private String logicName;
	private List<BEditorModel> editors = new ArrayList<BEditorModel>();
	private List<BClass> logics = new ArrayList<BClass>();
	private List<BClass> datas = new ArrayList<BClass>();
	private BasicNaming naming;
	private SubSystem sub;

	public BasicBookModel(SubSystem sub) {
		this.naming = new BasicNaming();
		this.sub = sub;
	}

	public SubSystem getSubSystem() {
		return this.sub;
	}

	public BasicNaming getNaming() {
		return this.naming;
	}

	public void addDataClass(BClass model) {
		if (model == null) {
			return;
		}
		for (BClass m : datas) {

			String name = m.getQualifiedName();
			if (name.equals(model.getQualifiedName())) {
				datas.remove(m);
				break;
			}

		}
		datas.add(model);

	}

	public List<BClass> getLogics() {
		return this.logics;
	}

	public List<BClass> getDatas() {
		return this.datas;
	}

	public void validateLayers(BasicBook book) {
		this.logics.clear();
		List<BClass> datas = new ArrayList<BClass>();
		int count = book.getTabCount();
		if (count == 1) {
			return;
		}
		for (int i = 0; i < count; i++) {
			Component comp = book.getComponentAt(i);
			if (comp instanceof BCoverSheet) {
				continue;
			}
			if (comp instanceof BEditor) {
				BEditor sheet = (BEditor) comp;
				BEditorModel model = sheet.getEditorModel();
				if (model instanceof BTableModel) {
					BTableModel table = (BTableModel) model;
					BPath path = table.getActionPath();
					if (path.getSelfAction() == null) {
						continue;
					}
				}
				if (model instanceof BasicModel) {
					BasicModel flow = (BasicModel) model;
					List<BNode> list = flow.getBNodes();
					for (BNode node : list) {
						BActionPropertyNode p = null;
						if (node instanceof BActionNode) {
							BActionNode action = (BActionNode) node;
							p = action.getProperty();
						} else if (node instanceof BActionPropertyNode) {
							p = (BActionPropertyNode) node;
						}
						if (p != null) {

							List<BClass> clss = BasicGenUtils.createLayer(p.getLogic().getPath(), sheet.getProject());
							BActionModel action = (BActionModel) p.getLogic().getPath().getAction();

							NewLayerClassLogic logic = p.getLogic();

							book.getDefinedParamters().addAll(logic.getOutputs());
							book.getDefinedParamters().addAll(logic.getParameters());

							BLogicProvider provider = p.getLogic().getPath().getProvider();
							if (provider == null) {
								continue;
							}
							provider.getProperties().setCurrentDeclearedClass(
									book.getBookModel().getLogic(BasicGenUtils.createClass(action, book.getProject())));

							List<BasicComponentModel> models = new ArrayList<BasicComponentModel>();
							models.addAll(action.getInputModels());
							models.addAll(action.getOutputModels());

							BClass bclass = provider.manageGenerableClass(models, clss);

							if (bclass != null) {
								action.setDeclearedClass(bclass);
							}
							addClasses(clss, datas, book.getProject());
						}
					}
				}

			}
		}
		int size = datas.size();
		for (int i = 0; i < size; i++) {
			BClass bclass = datas.get(i);
			for (BClass b : this.datas) {
				if (b.getQualifiedName().equals(bclass.getQualifiedName())) {
					datas.remove(i);
					datas.add(i, b);
				}
			}
		}
		this.datas = datas;
	}

	private transient Hashtable<String, BClass> templates = new Hashtable<String, BClass>();

	private void addClasses(List<BClass> list, List<BClass> datas, BProject project) {
		if (templates == null) {
			templates = new Hashtable<String, BClass>();
		}
		for (BClass bclass : list) {

			if (bclass.isData()) {
				boolean contained = false;
				for (BClass b : datas) {
					if (b.getQualifiedName().equals(bclass.getQualifiedName())) {
						contained = true;
					}
				}
				if (!contained) {
					if (bclass.getUserAttribute("READ") == null) {
						String name = bclass.getQualifiedName();
						BClass exists = null;
						if (!Application.getInstance().getBasicSpective().getSelection().isProviderReload()) {
							BClass template = templates.get(name);
							if (template != null) {
								exists = template.cloneAll();
							}
						}
						if (exists == null) {
							exists = CodecUtils.getBClassTemplate(project, name);
						}
						if (exists != null) {
							exists.setName(bclass.getName());
							bclass = exists;
							bclass.addUserAttribute("READ", "TRUE");
							if (!templates.containsKey(name)) {
								templates.put(name, bclass);
							}
						}

					}
					datas.add(bclass);
				}
			} else {

				boolean contained = false;
				for (BClass b : logics) {
					if (b.getQualifiedName().equals(bclass.getQualifiedName())) {
						contained = true;
					}
				}
				if (!contained) {
					this.logics.add(bclass);
				}
			}
		}
	}

	public BClass getLogic(BClass logic) {
		for (BClass m : logics) {

			String name = m.getQualifiedName();
			if (name.equals(logic.getQualifiedName())) {

				return m;

			}

		}
		return null;
	}

	public BClass getData(BClass data) {
		for (BClass m : datas) {

			String name = m.getQualifiedName();
			if (name.equals(data.getQualifiedName())) {
				if (m.isData()) {
					return m;
				}
			}

		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public List<BEditorModel> getEditors() {
		return editors;
	}

	public BasicBook getEditor(BProject project, File file, BWorkSpace space) {
		BasicSystemModel basic = BasicSystemModel.load(project);
		SubSystem sub = new SubSystem(basic);
		sub.setLogicName(file.getParentFile().getParentFile().getName());
		Application.test = false;
		BasicBook book = new BasicBook(this, project, space, sub);
		book.setOpening(true);
		List<BEditorModel> list = this.getEditors();
		int i = 0;

		for (BEditorModel m : list) {
			BEditor editor = m.getEditor(project, file, space);
			book.insertTab(m.getName(), editor.getImageIcon(), (Component) editor, m.getName(), i);
			i++;

		}
		book.setTitleLabel(this.getLogicName());
		Application.test = true;
		//////////////////////////////// for test///////////////////////
		// int k = i;
		// new BeeThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// forTest(file, project, book, space, k);
		// }
		//
		// }).start();

		//////////////////////////////////
		book.setOpening(false);
		return book;

	}

	private void forTest(File file, BProject project, BasicBook book, BWorkSpace space, int i) {
		//////////////////////////////// for test///////////////////////
		String path = file.getAbsolutePath();
		path = path.substring(0, path.lastIndexOf("."));
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				try {
					if (Application.INSTANCE_COMPLETE) {
						Application.getInstance().getEditor().getStatusBar()
								.setMessag("openning " + f.getName() + "...");
					}
					BEditorModel m = (BEditorModel) ObjectFileUtils.readObject(f);
					BEditor editor = m.getEditor(project, file, space);
					book.insertTab(m.getName(), editor.getImageIcon(), (Component) editor, m.getName(), i);
					i++;
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (Application.INSTANCE_COMPLETE) {
			Application.getInstance().getEditor().getStatusBar().setMessag("");
		}
		//////////////////////////////////
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public Object doSearch(String keyword) {
		return null;
	}

	@Override
	public BEditor getSheet(BProject project) {
		return null;
	}

	@Override
	public List<BClass> getClassList() {
		List<BClass> list = new ArrayList<BClass>();
		list.addAll(logics);
		list.addAll(datas);
		return list;
	}

	@Override
	public List<BEditorModel> getList() {
		return this.getEditors();
	}

	public Hashtable<String, ITableSqlInfo> getSqlInfos(BProject project, BLogicProvider provider) {
		Hashtable<String, ITableSqlInfo> infos = new Hashtable<String, ITableSqlInfo>();
		List<BEditorModel> list = this.getEditors();

		// for java.util.ConcurrentModificationException
		List<BEditorModel> alist = new ArrayList<BEditorModel>();
		alist.addAll(list);
		for (BEditorModel editor : alist) {
			if (editor instanceof BPatternModel) {
				BPatternModel p = (BPatternModel) editor;
				BActionModel action = (BActionModel) p.getActionPath().getSelfAction();
				if (action == null) {
					continue;
				}
				if (editor instanceof BSQLModel) {

					BSQLModel sql = (BSQLModel) editor;
					ITableSqlInfo info = sql.getSqlInfo(this, provider);
					// BClass bclass = BasicGenUtils.createClass(action, project);
					infos.put(p.getActionPath().getUniqueKey() + "", info);
				}
			}
		}
		return infos;
	}
}
