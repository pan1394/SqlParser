package com.linkstec.bee.core.codec.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.editor.task.console.ConsoleDisplay;
import com.linkstec.bee.UI.node.ClassHeaderNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BFlowModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BSqlModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectModel;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.Comsumer;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.Pair;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.Producer;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.TransferCell;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.basic.BWrpperLogic;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BSqlSetImpl;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class BasicGen {
	private BProject project;
	private List<BClass> datas = new ArrayList<BClass>();
	private List<BClass> logics = new ArrayList<BClass>();
	private ConfigModel config;
	private BLogicProvider provider = null;
	private BasicBookModel model;
	private List<BSQLSet> sqlSet = new ArrayList<BSQLSet>();

	private List<Comsumer> IOConsumers = new ArrayList<Comsumer>();
	private ConsoleDisplay c;

	public BasicGen(BasicBookModel model, BProject project, ConsoleDisplay c) {
		this.c = c;
		this.project = project;
		this.model = model;

		c.addText("Loading model....", project);
		this.config = ConfigModel.load(project);

		List<ActionModel> layers = config.getActions();

		List<BClass> ls = model.getLogics();
		List<BClass> ds = model.getDatas();
		List<BEditorModel> actions = model.getEditors();

		c.addText("Catching targets....", project);

		datas.clear();
		logics.clear();
		for (BClass bclass : ds) {
			if (bclass instanceof BeeDataModel) {
				BeeDataModel d = (BeeDataModel) bclass;
				datas.add((BeeDataModel) ObjectFileUtils.deepCopy(d));
			}
		}

		for (BClass bclass : ls) {
			if (bclass instanceof BeeModel) {
				BeeModel b = (BeeModel) bclass;
				mxICell root = ((mxCell) b.getRoot()).getChildAt(0);
				int count = root.getChildCount();
				for (int i = count - 1; i >= 0; i--) {
					root.remove(i);
				}
				ClassHeaderNode node = new ClassHeaderNode();
				node.setBClass(b);

				root.insert(node);
				logics.add((BeeModel) ObjectFileUtils.deepCopy(b));
			}
		}

		c.addText("start to make model....", project);
		for (BEditorModel m : actions) {
			if (m instanceof BFlowModel) {
				BFlowModel flow = (BFlowModel) m;
				List<BActionPropertyNode> as = flow.getActionNodes();

				BMethod method = null;

				BeeModel target = null;

				for (BActionPropertyNode node : as) {
					BActionModel action = (BActionModel) node.getLogic().getPath().getAction();
					// the main action
					if (action.getProcessModel().getLayers().size() == 1) {

						// last loop
						if (provider != null && target != null) {
							provider.onClassCreated(target);

							if (method != null) {
								provider.onMethodCreated(target, method);
							}
						}

						// new loop
						method = this.makePatternAction(flow, node.getLogic().getPath(), layers, false, false);
						c.addText(method.getName() + " is created", project);

						for (BClass bclass : logics) {
							if (bclass.getQualifiedName().equals(action.getDeclearedClass().getQualifiedName())) {
								target = (BeeModel) bclass;
								break;
							}
						}
						if (target != null) {
							provider = action.getProcessModel().getProvider();
							provider.getProperties().setCurrentDeclearedClass(target);
						}
					} else {
						// child actions
						if (method != null && target != null) {

							NewLayerClassLogic logic = node.getLogic();
							List<BLogicUnit> units = logic.createUnit();
							for (BLogicUnit unit : units) {
								method.getLogicBody().addUnit(unit);
							}
						}

					}
				}

				if (provider != null && target != null) {
					provider.onClassCreated(target);

					if (method != null) {
						provider.onMethodCreated(target, method);
					}
				}

			} else if (m instanceof BPatternModel) {
				BPatternModel p = (BPatternModel) m;
				BPath action = p.getActionPath();
				BMethod method = this.makePatternAction(p, action, layers, true, true);
				if (method != null) {
					c.addText(method.getName() + " is created", project);
				}

			}
		}
	}

	public BMethod makePatternAction(BasicModel p, BPath bpath, List<ActionModel> layers, boolean makeBody,
			boolean callMethodCreated) {
		BeeModel target = null;
		BActionModel action = (BActionModel) bpath.getAction();

		for (BClass bclass : logics) {
			if (bclass.getQualifiedName().equals(action.getDeclearedClass().getQualifiedName())) {
				target = (BeeModel) bclass;
				break;
			}
		}
		if (target != null) {
			for (ActionModel am : layers) {
				if (am.equals(action.getProcessModel())) {
					break;
				}
			}

			BLogicProvider provider = bpath.getProvider();
			if (provider != null) {

				provider.getProperties().setGenerableDatas(datas);
				provider.getProperties().setGenerableLogics(logics);
				provider.getProperties().setCurrentDeclearedClass(target);

				provider.onClassCreate(target);
				boolean thisclasscreate = true;
				List<BClass> removes = new ArrayList<BClass>();
				for (BClass b : datas) {
					if (!provider.isClassCreatable(b)) {
						removes.add(b);
					} else {
						provider.onClassCreated(b);
					}
				}
				for (BClass b : removes) {
					datas.remove(b);
				}

				removes.clear();

				for (BClass b : logics) {
					if (!provider.isClassCreatable(b)) {
						removes.add(b);
					}
				}
				for (BClass b : removes) {
					logics.remove(b);
					if (b.equals(target)) {
						thisclasscreate = true;
					}
				}

				if (!thisclasscreate) {
					return null;
				}
			}

			// make method
			BMethod method = this.makeAction(bpath, ((mxICell) p.getRoot()).getChildAt(0), target, action, provider,
					makeBody);

			// provider setting
			if (provider != null) {
				if (callMethodCreated) {
					if (method != null) {
						c.addText(method.getName() + " is being managing", project);
					}
					provider.onMethodCreated(target, method);
				}

				// after created

				c.addText(target.getName() + " is being managing", project);

				provider.onClassCreated(target);

				List<BClass> list = provider.getProperties().getClassList();
				for (BClass c : list) {
					boolean added = false;
					for (BClass b : logics) {
						if (b.getQualifiedName().equals(c.getQualifiedName())) {
							added = true;
							break;
						}
					}
					if (!added) {
						logics.add(c);
					}
				}
				list.clear();
			}
			if (p instanceof BTableModel) {

				BTableModel table = (BTableModel) p;

				c.addText(p.getName() + " is being managed", project);

				List<BLogicUnit> units = table.getSetterLogics();

				Object path_id = method.getUserAttribute("PATH_ID");
				if (path_id instanceof Long && units != null && !units.isEmpty()) {
					long pathId = (long) path_id;
					BClass parentClass = BasicGenUtils.createClass(action.getParentModel(), bpath.getProject());
					for (BClass logic : logics) {
						if (logic instanceof BeeModel) {
							BeeModel bee = (BeeModel) logic;
							if (!bee.isInterface()) {
								boolean match = false;
								if (logic.getQualifiedName().equals(parentClass.getQualifiedName())) {
									match = true;
								} else {
									List<BValuable> inters = logic.getInterfaces();
									for (BValuable bv : inters) {
										if (bv.getBClass().getQualifiedName().equals(parentClass.getQualifiedName())) {
											match = true;
										}
									}
								}
								if (match) {
									mxICell root = (mxICell) bee.getRoot();

									Producer producer = BasicGenUtils.findProducer(root.getChildAt(0), pathId, null);
									if (producer != null) {
										BLogicBody body = producer.getScope();

										int index = producer.getLocation();
										if (units != null) {
											for (BLogicUnit unit : units) {
												body.addUnit(unit, index);
												index++;
											}
										}
									}
									break;
								}
							}
						}
					}
				}

				BSqlModel info = table.getSqlModel(model, provider);
				List<BInvoker> invokers = info.getInvokers();

				for (BInvoker invoker : invokers) {
					BValuable parent = invoker.getInvokeParent();

					BActionModel prentModel = action.getParentModel();

					BClass parentClass = BasicGenUtils.createClass(prentModel, bpath.getProject());
					TransferCell cell;
					Object obj = parent.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
					// if (obj instanceof TransferCell) {
					// cell = (TransferCell) obj;
					// cell.setProducerClass(parentClass);
					// } else {
					cell = new TransferCell(bpath, parentClass);
					// }
					parent.addUserAttribute(BasicGenUtils.INVOKER_SOURCE, cell);

				}

				for (BInvoker invoker : invokers) {
					BValuable parent = invoker.getInvokeParent();
					Object obj = parent.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
					if (obj instanceof TransferCell) {
						TransferCell t = (TransferCell) obj;
						Comsumer consumer = BasicGenUtils.makeConsumer(invoker, t, method, target);
						this.IOConsumers.add(consumer);
					}

				}
				if (table instanceof BTableSelectModel) {
					if (bpath.getSelfAction() != null) {

						BSqlSetImpl set = new BSqlSetImpl();
						set.setModel(table);
						set.setMethod(method);
						this.sqlSet.add(set);

					}
				}

			}
			return method;

		}
		return null;
	}

	public void saveAll(ConsoleDisplay c) {
		c.addText("Start to generate...", project);
		List<String> sources = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<BookModel> models = this.getDataBookModels();
		for (BookModel model : models) {
			LayoutUtils.makeBook(model);
			File f = this.saveModel(c, model);
			if (f != null) {
				sources.add(f.getAbsolutePath());
				names.add(f.getName());
			}
		}

		BeeCompiler.compileSources(project, sources, null);
		c.addText("[3.コンパイル]" + names.toString() + "件コンパイル完了しました。", project);

		models = this.getLogicBookModels(project);
		sources = new ArrayList<String>();
		names = new ArrayList<String>();
		for (BookModel model : models) {
			LayoutUtils.makeBook(model);
			File f = this.saveModel(c, model);
			if (f != null) {
				sources.add(f.getAbsolutePath());
				names.add(f.getName());
			}
		}

		BeeCompiler.compileSources(project, sources, null);
		c.addText("[3.コンパイル]" + names.toString() + "コンパイル完了しました。", project);

		if (this.provider != null) {

			c.addText("[4.詳細設計Excel]Excelエクスポートが開始します。 ", project);
			// do export

			List<BClass> list = new ArrayList<BClass>();
			list.addAll(logics);
			list.addAll(datas);

			BModule module = new BModule() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 8173249277364393543L;

				@Override
				public List<BClass> getClassList() {
					return list;
				}

				@Override
				public String getLogicName() {
					return model.getLogicName();
				}

				@Override
				public List<BEditorModel> getList() {
					return model.getEditors();
				}

			};
			File file = this.provider.doDetailExport(project, module, this.sqlSet);

			if (file != null) {
				c.addText("[4.詳細設計Excel]<a href='f://" + file.getAbsolutePath() + "'>" + file.getName()
						+ "</a>へExcelエクスポートしました。", project);
			} else {
				c.addText("[4.詳細設計Excel]Excelエクスポートできませんでした。", project);
			}
		}
	}

	private File saveModel(ConsoleDisplay c, BookModel model) {
		File file = BeeActions.saveModelWidoutThead(model.getLogicName(), model, project);
		String msg = "[1.詳細設計生成]" + model.getLogicName() + "を<a href='d://" + file.getAbsolutePath() + "'>"
				+ file.getName() + "</a>" + "へ変換完了しました";
		c.addText(msg, project);

		File f = null;
		try {
			f = CodecAction.generateSource(file.getAbsolutePath(), project);
		} catch (Exception e1) {
			c.addText("[2.ソース生成]" + file.getName() + "の変換が失敗しました", project);
			e1.printStackTrace();
		}

		if (f != null) {
			c.addText("[2.ソース生成]" + file.getName() + "を<a href='j://" + f.getAbsolutePath() + "'>" + f.getName()
					+ "</a>へ変換完了しました。", project);
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	public List<BookModel> getLogicBookModels(BProject project) {
		List<BookModel> models = new ArrayList<BookModel>();

		// make all operation after not affected by original one
		this.logics = (List<BClass>) ObjectFileUtils.deepCopy(this.logics);

		for (BClass d : this.logics) {
			BookModel model = new BookModel();
			model.setLogicName(d.getLogicName());
			model.setName(d.getName());
			model.getList().add((BEditorModel) d);
			models.add(model);
		}
		this.makeDataFlow(project);
		return models;
	}

	@SuppressWarnings("unchecked")
	private void makeDataFlow(BProject project) {
		List<Comsumer> list = new ArrayList<Comsumer>();
		for (BClass model : this.logics) {
			if (!model.isInterface()) {
				List<Comsumer> comsumers = BasicGenUtils.findUsers((BeeModel) model);

				list.addAll(comsumers);
			}
		}

		// for other

		this.sendComsumer(list);

		// for io
		this.sendComsumer((List<Comsumer>) ObjectFileUtils.deepCopy(IOConsumers));
	}

	private void sendComsumer(List<Comsumer> list) {
		List<Pair> dids = new ArrayList<Pair>();
		for (Comsumer consumer : list) {
			for (BClass m : this.logics) {
				if (consumer.getCell() == null) {
					continue;
				}
				BClass producerClass = consumer.getCell().getProducerClass();
				boolean match = false;
				if (!m.isInterface()) {
					if (m.getQualifiedName().equals(producerClass.getQualifiedName())) {
						match = true;
					} else {
						List<BValuable> inters = producerClass.getInterfaces();
						for (BValuable inter : inters) {
							if (inter.getBClass().getQualifiedName().equals(m.getQualifiedName())) {
								match = true;
								break;
							}
						}

						if (!match) {
							inters = m.getInterfaces();
							for (BValuable inter : inters) {
								if (inter.getBClass().getQualifiedName().equals(producerClass.getQualifiedName())) {
									match = true;
									break;
								}
							}
						}
					}
					if (match) {
						BeeModel bm = (BeeModel) m;
						Producer producer = BasicGenUtils.findProducer((mxICell) bm.getRoot(), consumer, null);
						if (producer != null) {
							boolean dup = false;
							for (Pair did : dids) {
								Comsumer c = did.getComsumer();
								Producer p = did.getProducer();

								String done = c.getTarget().toString()
										+ p.getProducerTransferParameter().getLogicName();
								String willDo = consumer.getTarget().toString()
										+ producer.getProducerTransferParameter().getLogicName();

								if (done.equals(willDo)) {
									dup = true;
									break;
								}
							}
							if (!dup) {
								this.addTransfer(producer, consumer, provider);
								Pair pair = new Pair();
								pair.setComsumer(consumer);
								pair.setProducer(producer);
								dids.add(pair);
							}
						}
					}
				}
			}
		}
		// this.changeTrasferGetter(list);
	}

	public void changeTrasferGetter(List<Comsumer> list) {
		for (Comsumer consumer : list) {
			BInvoker target = consumer.getTarget();
			BParameter parent = consumer.getParameter();
			parent.setCaller(true);
			target.setInvokeParent((BValuable) parent.cloneAll());
		}
	}

	public void addTransfer(Producer producer, Comsumer consumer, BLogicProvider provider) {
		BInvoker comsumerInvoker = consumer.getTarget();

		BLogicBody body = producer.getScope();

		IPatternCreator view = PatternCreatorFactory.createView();

		BInvoker setter = view.createMethodInvoker();
		setter.setInvokeParent((BValuable) producer.getTargetTransferParameter().cloneAll());
		BVariable var = producer.getTargetTranferData();

		// because var is impl
		BParameter target = view.createParameter();
		target.setLogicName(var.getLogicName());
		target.setName(var.getName());
		target.setBClass(var.getBClass());

		if (comsumerInvoker.getUserAttribute(BasicGenUtils.STATIC_CALL) != null) {
			BValuable parentValue = comsumerInvoker.getInvokeParent();
			if (parentValue instanceof BInvoker) {
				BInvoker childInvoker = (BInvoker) parentValue;
				BVariable bvar = (BVariable) childInvoker.getInvokeChild();
				target.setLogicName(bvar.getLogicName());
			} else {
				BVariable bvar = (BVariable) parentValue;
				target.setLogicName(bvar.getLogicName());
			}
		}

		setter.setInvokeChild((BValuable) target.cloneAll());

		// getter to be parameter
		// consumer.getTarget();
		BInvoker getter = consumer.getTarget();
		// BVariable getterParenet = producer.getProducerTransferParameter();
		// getter.setInvokeParent((BValuable) getterParenet.cloneAll());
		// getter.setInvokeChild((BValuable) target.cloneAll());

		if (comsumerInvoker.getUserAttribute(BasicGenUtils.STATIC_CALL) != null) {
			BVariable child = (BVariable) comsumerInvoker.getInvokeChild();

			if (!provider.onDataTransfer(setter, child)) {
				setter.addParameter(child);
			}

		} else {
			if (!provider.onDataTransfer(setter, getter)) {
				setter.addParameter(getter);
			}
		}

		body.addUnit(setter, producer.getLocation());

	}

	public List<BookModel> getDataBookModels() {
		List<BookModel> models = new ArrayList<BookModel>();

		for (BClass d : this.datas) {
			BookModel model = new BookModel();
			model.setLogicName(d.getLogicName());
			model.setName(d.getName());
			model.getList().add((BEditorModel) d);
			models.add(model);
		}
		return models;
	}

	private BMethod makeAction(BPath path, mxICell root, BeeModel bclass, BActionModel action, BLogicProvider provider,
			boolean makeBody) {

		BMethod method = BasicGenUtils.createMethod(path, bclass, action, provider, project);
		mxICell cell = ((mxCell) bclass.getRoot()).getChildAt(0);
		cell.insert((mxICell) method);
		if (makeBody) {
			List<BLogic> logics = new ArrayList<BLogic>();
			BasicGenUtils.makeLogics(logics, root, true);

			provider.getProperties().addUserAttribute("SQL_INFO", model.getSqlInfos(path.getProject(), provider));
			this.makeLogic(method.getLogicBody(), action, logics);
			provider.getProperties().removeUserAttribute("SQL_INFO");
		}
		return method;

	}

	private void makeLogic(BLogicBody body, BActionModel action, List<BLogic> logics) {

		for (BLogic logic : logics) {
			String name = logic.getName();

			c.addText(name + " is created", project);
			// start
			if (logic instanceof BWrpperLogic) {
				BWrpperLogic wrapper = (BWrpperLogic) logic;
				if (wrapper.getUserAttribute("START") != null) {
					List<BLogicUnit> units = wrapper.getStartLogics();
					this.makeLogicList(body, units);
				}
			}

			// body
			List<BLogicUnit> units = logic.createUnit();
			this.makeLogicList(body, units);

			// end
			if (logic instanceof BWrpperLogic) {

				BWrpperLogic wrapper = (BWrpperLogic) logic;
				if (wrapper.getUserAttribute("END") != null) {
					List<BLogicUnit> us = wrapper.getEndLogics();
					this.makeLogicList(body, us);
				}
			}
		}
	}

	private void makeLogicList(BLogicBody body, List<BLogicUnit> units) {
		if (units != null) {
			for (BLogicUnit unit : units) {
				body.addUnit((BLogicUnit) unit.cloneAll());
			}
		}
	}

}
