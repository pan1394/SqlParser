package com.linkstec.bee.core.codec.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.editor.task.console.ConsoleDisplay;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.ClassHeaderNode;
import com.linkstec.bee.UI.node.TypeNode;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.view.LinkNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BFlowModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BSqlModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableModel;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.Comsumer;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.Producer;
import com.linkstec.bee.core.codec.basic.BasicGenUtils.TransferCell;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
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
import com.linkstec.bee.core.fw.logic.BAssignment;
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
	private List<Comsumer> IOSetter = new ArrayList<Comsumer>();
	private ConsoleDisplay c;

	private List<BParameter> definedParameters;
	private static IPatternCreator view = PatternCreatorFactory.createView();

	private Hashtable<String, List<BInvoker>> invokersPerMethod = new Hashtable<String, List<BInvoker>>();

	public BasicGen(BasicBookModel model, BProject project, ConsoleDisplay c, List<BParameter> definedParameters) {
		this.c = c;
		this.project = project;
		this.model = model;
		this.definedParameters = new ArrayList<BParameter>();
		// this.definedParameters = definedParameters;

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
						method = this.makePatternAction(flow, node.getLogic().getPath(), layers, false, false, actions);
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
							Thread t = Thread.currentThread();
							if (t instanceof BeeThread) {
								BeeThread b = (BeeThread) t;
								b.addUserAttribute("PROVIDER", provider);
							}
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
				BMethod method = this.makePatternAction(p, action, layers, true, true, actions);
				if (method != null) {
					c.addText(method.getName() + " is created", project);
				}
			}
		}
	}

	public BMethod makePatternAction(BasicModel p, BPath bpath, List<ActionModel> layers, boolean makeBody,
			boolean callMethodCreated, List<BEditorModel> actions) {
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

				Thread t = Thread.currentThread();
				if (t instanceof BeeThread) {
					BeeThread b = (BeeThread) t;
					b.addUserAttribute("PROVIDER", provider);
				}

				provider.getProperties().setCurrentDeclearedClass(target);
				provider.getProperties().addThreadScopeAttribute("MODULES", this.model);

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

					List<BasicComponentModel> models = new ArrayList<BasicComponentModel>();
					models.addAll(action.getInputModels());
					models.addAll(action.getOutputModels());

					provider.getProperties().addThreadScopeAttribute("MODELS", models);
					provider.getProperties().addUserAttribute("SQL_INFO", model.getSqlInfos(this.project, provider));

					provider.onMethodCreated(target, method);
					provider.getProperties().removeUserAttribute("SQL_INFO");
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

				// insert or update logic
				List<BLogicUnit> units = table.getSetterLogics();

				if (units != null && !units.isEmpty()) {

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
											if (bv.getBClass().getQualifiedName()
													.equals(parentClass.getQualifiedName())) {
												match = true;
											}
										}
									}
									if (match) {
										mxICell root = (mxICell) bee.getRoot();

										Producer producer = BasicGenUtils.findProducer(root.getChildAt(0), pathId,
												null);
										if (producer != null) {
											BLogicBody body = producer.getScope();

											int index = producer.getLocation();
											if (units != null) {
												for (BLogicUnit unit : units) {

													if (unit instanceof BInvoker) {
														BInvoker bin = (BInvoker) unit;
														List<BValuable> params = bin.getParameters();
														if (bin.getInvokeChild() instanceof BVariable
																&& bin.getInvokeParent() instanceof BVariable) {
															BVariable bp = (BVariable) bin.getInvokeParent();
															TransferCell cell = new TransferCell(bpath, parentClass);
															bp.addUserAttribute(BasicGenUtils.INVOKER_SOURCE, cell);

															if (params.size() == 1) {
																// BInvoker b = (BInvoker) bin.cloneAll();
																// BValuable para = params.get(0);
																// b.clearParameters();

																BVariable bparam = producer
																		.getTargetTransferParameter();
																if (bparam != null) {
																	if (!bp.getBClass().getQualifiedName().equals(
																			bparam.getBClass().getQualifiedName())) {
																		BParameter newParent = (BParameter) bparam
																				.cloneAll();
																		newParent.setClass(false);
																		newParent.setCaller(true);
																		bin.setInvokeParent(newParent);

																	}
																}

																// if (provider.onDataTransfer(b, para)) {
																// withProvider = true;
																// }
															}
														}
														// this.validateData(bin);
													}

													// if (unit instanceof BInvoker) {
													// BInvoker bin = (BInvoker) unit;
													// //if (!this.InvokerDoubled(pathId + "", bin)) {
													// body.addUnit(unit, index);
													// index++;
													// //}
													// } else {

													body.addUnit(unit, index);
													index++;
													// }

												}

											}
										}
										break;
									}
								}
							}
						}
					}
				}

				BActionModel prentModel = action.getParentModel();

				BClass parentClass = BasicGenUtils.createClass(prentModel, bpath.getProject());

				BSqlModel info = table.getSqlModel(model, provider);
				List<BInvoker> invokers = info.getInvokers();
				List<BInvoker> selects = info.getSelectInfos();

				for (BInvoker invoker : selects) {
					// BInvoker bin = (BInvoker) invoker.cloneAll();
					BInvoker b = (BInvoker) ObjectFileUtils.deepCopy(invoker);
					BVariable var = (BVariable) b.getInvokeParent();
					BClass returnBClass = method.getReturn().getBClass();
					if (returnBClass.getQualifiedName().equals(List.class.getName())) {
						returnBClass = returnBClass.cloneAll();
						List<BType> types = returnBClass.getParameterizedTypes();
						for (BType type : types) {
							if (type instanceof BClass) {
								BClass bc = (BClass) type;
								if (bc.isData()) {
									var.setBClass(bc);
									this.validateData(b);
								}
								break;
							}
						}
					} else if (returnBClass.isData()) {
						var.setBClass(returnBClass);
						this.validateData(b);
					}

					// TransferCell cell = new TransferCell(bpath, parentClass);
					// }
					// b.addUserAttribute(BasicGenUtils.INVOKER_SOURCE, cell);

					// Comsumer consumer = BasicGenUtils.makeConsumer(invoker, cell, method,
					// target);
					// this.IOSetter.add(consumer);
				}

				for (BInvoker invoker : invokers) {
					BValuable parent = invoker.getInvokeParent();

					// Object obj = parent.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
					// if (obj instanceof TransferCell) {
					// cell = (TransferCell) obj;
					// cell.setProducerClass(parentClass);
					// } else {
					TransferCell cell = new TransferCell(bpath, parentClass);
					// }
					// if (table instanceof BTableDeleteModel) {
					// System.out.println(bpath.getUniqueKey());
					// System.out.println(cell.getPathKey());
					// System.out.println(action.getName());
					// Debug.a();
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

				if (bpath.getSelfAction() != null) {

					BSqlSetImpl set = new BSqlSetImpl();
					set.setModel(table);
					set.setMethod(method);

					this.sqlSet.add(set);

				}

			}
			return method;

		}
		return null;
	}

	private boolean InvokerDoubled(String pathId, BInvoker invoker) {
		if (invoker.getParameters().size() != 1) {
			return false;
		}

		List<BInvoker> invokers = this.invokersPerMethod.get(pathId);
		if (invokers == null) {
			List<BInvoker> bins = new ArrayList<BInvoker>();
			bins.add(invoker);
			invokersPerMethod.put(pathId, bins);
			return false;
		} else {
			String name = BValueUtils.createValuable(invoker, true);
			Object hashCode = invoker.getInvokeParent().getUserAttribute("PARENT_HASH_CODE");

			BValuable paramValue = invoker.getParameters().get(0);
			String allName = name + BValueUtils.createValuable(paramValue, true)
					+ BValueUtils.createValuable(paramValue, false);

			for (BInvoker bin : invokers) {
				String n = BValueUtils.createValuable(bin, true);
				BValuable pValue = bin.getParameters().get(0);
				String aName = n + BValueUtils.createValuable(pValue, true) + BValueUtils.createValuable(pValue, false);

				if (allName.equals(aName)) {
					Object hashC = bin.getInvokeParent().getUserAttribute("PARENT_HASH_CODE");
					if (hashC instanceof Integer && hashCode instanceof Integer) {
						int hc1 = (int) hashC;
						int hc2 = (int) hashCode;
						if (hc1 == hc2) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
			invokers.add(invoker);
		}
		return false;
	}

	public void saveAll(ConsoleDisplay c) {
		c.addText("Start to generate...", project);
		List<String> sources = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<BookModel> logicModels = this.getLogicBookModels(project);
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

		models = logicModels;
		// for (BookModel mb : models) {
		// List<BClass> ds = mb.getClassList();
		// for (BClass d : ds) {
		// this.validate((BeeModel) d);
		// }
		// }
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
			// export complete
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
			if (d instanceof BeeModel) {
				BeeModel bee = (BeeModel) d;
				this.scanNodesForDefinedParameter((mxICell) bee.getRoot());
			}
		}

		for (BParameter p : this.definedParameters) {
			p.setClass(false);
			String name = provider.getVariableName(p, null);
			if (name != null) {
				p.setLogicName(name);
				p.setName(name);
			}

		}

		this.makeDataFlow(project);

		for (BClass d : this.logics) {
			this.validate((BeeModel) d);
		}

		for (BClass d : this.logics) {
			this.validateAssiangmen((BeeModel) d);
		}

		for (BClass d : this.logics) {
			BookModel model = new BookModel();
			model.setLogicName(d.getLogicName());
			model.setName(d.getName());
			model.getList().add((BEditorModel) d);
			models.add(model);
		}
		return models;
	}

	private void validateAssiangmen(BeeModel d) {
		new BasicScanner(d);
		this.validate(d);
	}

	private void validate(BeeModel d) {
		mxICell root = ((mxCell) d.getRoot()).getChildAt(0);
		this.scanNodes(root);
	}

	public void makeDefinedParameter(BVariable target) {
		if (target instanceof BParameter) {
			BParameter p = (BParameter) target;

			Object obj = target.getUserAttribute("INPUT_PARAMETER");
			if (obj == null) {
				obj = target.getUserAttribute("OUTPUT_PARAMETER");
			}
			if (obj != null) {
				Object c = target.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
				if (c instanceof TransferCell) {
					this.definedParameters.add(p);
				}
			}
		}
	}

	public void scanNodesForDefinedParameter(mxICell c) {
		P.check(null);
		int count = c.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = c.getChildAt(i);
			if (child instanceof BMethod) {
				if (child instanceof mxICell) {
					mxICell cell = (mxICell) child;
					if (!(cell.getParent() instanceof BasicNode)) {
						BMethod method = (BMethod) cell;
						List<BParameter> params = method.getParameter();
						for (BParameter param : params) {
							this.makeDefinedParameter(param);
						}
					}
				}
			} else if (child instanceof BAssignment) {
				BAssignment node = (BAssignment) child;
				BVariable target = node.getLeft();
				this.makeDefinedParameter(target);
			} else if (child instanceof LinkNode) {
				LinkNode node = (LinkNode) child;
				BasicNode basic = node.getLinkNode();
				scanNodesForDefinedParameter(basic);
			}

			Object value = child.getValue();
			if (value instanceof mxICell) {
				mxICell cell = (mxICell) value;
				this.scanNodesForDefinedParameter(cell);
			}

			scanNodesForDefinedParameter(child);
		}
	}

	public void scanNodes(mxICell c) {
		P.check(null);
		int count = c.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = c.getChildAt(i);
			if (child instanceof BVariable) {
				BVariable target = (BVariable) child;

				this.validateParaemterName(target);

			} else if (child instanceof ObjectNode) {
				ObjectNode node = (ObjectNode) child;
				Object value = node.getValue();
				if (value instanceof BVariable) {
					BVariable target = (BVariable) value;
					this.validateParaemterName(target);
				}
			} else if (child instanceof BAssignment) {
				BAssignment node = (BAssignment) child;
				BVariable target = node.getLeft();
				this.validateParaemterName(target);
			} else if (child instanceof LinkNode) {
				LinkNode node = (LinkNode) child;
				BasicNode basic = node.getLinkNode();
				scanNodes(basic);
			} else if (child instanceof BInvoker) {
				BInvoker invoker = (BInvoker) child;

				// parent
				BValuable parent = invoker.getInvokeParent();
				if (parent instanceof BVariable) {
					BVariable var = (BVariable) parent;
					if (!var.isClass()) {

						this.validateParaemterName(var);

					} else {
						// rescue
						BValuable method = invoker.getInvokeChild();
						if (method instanceof BMethod) {
							BMethod m = (BMethod) method;
							if (m.getLogicName().equals("size")) {
								var.setClass(false);
								var.setCaller(true);
							}
						}
						// rescue
					}

				}

				// child

				BValuable bc = invoker.getInvokeChild();
				if (bc instanceof BVariable) {
					BVariable b = (BVariable) bc;
					this.validateParaemterName(b);
				}

				this.validateData(invoker);

				// parameter
				List<BValuable> params = invoker.getParameters();

				for (BValuable param : params) {

					if (param instanceof BInvoker) {
						BInvoker bin = (BInvoker) param;

						parent = bin.getInvokeParent();
						if (parent instanceof BVariable) {
							BVariable var = (BVariable) parent;
							if (!var.isClass()) {
								this.validateParaemterName(var);
							}
						}

						this.validateData(bin);

					} else if (param instanceof BVariable) {

						BVariable var = (BVariable) param;
						if (var instanceof BasicNode) {
							if (var.getLogicName() != null && var.getLogicName().indexOf(")") > 0) {
								if (bc instanceof BVariable) {
									BVariable bb = (BVariable) bc;
									var.addUserAttribute("FIXED_INPUT_VALUE_NAME", bb);
								}
							}
							BasicNode b = (BasicNode) var;
							if (var.getUserAttribute("FIXED_INPUT_VALUE_NAME") != null) {
								if (!var.isClass()) {
									BValuable value = provider.getConstantsName(var);
									if (value instanceof mxCell) {
										b.replace((mxCell) value);
									}
								}
							} else {
								this.validateParaemterName(var);
							}
						}
					}
				}
			}
			Object value = child.getValue();
			if (value instanceof mxICell) {
				mxICell cell = (mxICell) value;
				this.scanNodes(cell);
			}

			scanNodes(child);
		}
	}

	public void validateData(BInvoker invoker) {
		BValuable parent = invoker.getInvokeParent();
		BValuable child = invoker.getInvokeChild();

		if (parent instanceof BVariable && child instanceof BVariable) {
			BVariable p = (BVariable) parent;
			BVariable c = (BVariable) child;
			BClass bclass = p.getBClass();
			if (bclass.isData()) {
				for (BClass data : this.datas) {
					if (data.getQualifiedName().equals(bclass.getQualifiedName())) {
						// boolean has = false;
						List<BAssignment> vars = data.getVariables();
						for (BAssignment var : vars) {
							BParameter left = var.getLeft();
							if (left.getLogicName().equals(c.getLogicName())) {
								// has = true;
								data.removeVar(var);
								break;
							}
						}
						// if (!has) {
						BAssignment a = view.createAssignment();
						BParameter var = view.createParameter();
						var.setBClass(c.getBClass());
						var.setLogicName(c.getLogicName());
						var.setName(c.getName());
						a.setLeft(var);
						data.addVar(a);
						// }
						this.model.addDataClass(data);
					}

				}
			}
		}
	}

	public void validateParaemterName(BVariable parameter) {
		if (parameter == null) {
			return;
		}
		if (parameter.getLogicName() == null) {
			return;
		}

		BClass bclass = parameter.getBClass();

		if (bclass != null) {
			// rescue////////////////////////////////////

			if (bclass.getQualifiedName().equals(List.class.getName())) {
				List<BType> types = bclass.getParameterizedTypes();
				int number = 0;
				for (BType type : types) {
					if (type instanceof BClass) {
						number++;
						if (number == 2) {
							types.remove(type);
							bclass.setParameterTypes(types);
							break;
						}
					}
				}
			}

			////////////////////////////////////////////
			String name = provider.getVariableName(parameter, null);
			if (name != null) {
				if (parameter instanceof TypeNode) {
					TypeNode type = (TypeNode) parameter;
					type.getObject().setLogicName(name);
				} else {
					parameter.setLogicName(name);
					parameter.setName(name);
				}
			}

		}

		TransferCell mark = (TransferCell) parameter.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
		if (mark != null) {

			boolean in = false;
			if (parameter.getUserAttribute("INPUT_PARAMETER") != null) {
				in = true;
			}

			for (BParameter defined : definedParameters) {
				String logicName = defined.getLogicName();
				TransferCell cell = (TransferCell) defined.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);
				if (cell.getPathKey() == mark.getPathKey()) {
					if (in) {
						if (defined.getUserAttribute("INPUT_PARAMETER") != null) {
							if (parameter.getBClass().getQualifiedName()
									.equals(defined.getBClass().getQualifiedName())) {
								if (!parameter.getLogicName().equals(logicName)) {

									parameter.setLogicName(logicName);

								}
							} else {
								if (parameter.getLogicName().equals("lmWPos1060TblDto")) {
									Debug.a();
								}
								if (defined.getUserAttribute("PARAMETER_TYPE_CHANGED") != null) {
									parameter.setLogicName(logicName);
									parameter.setBClass(defined.getBClass());
									parameter.addUserAttribute("PARAMETER_TYPE_CHANGED", "PARAMETER_TYPE_CHANGED");
								}

							}
						}
					} else {
						if (defined.getUserAttribute("INPUT_PARAMETER") == null) {
							if (parameter.getBClass().getQualifiedName()
									.equals(defined.getBClass().getQualifiedName())) {
								if (!parameter.getLogicName().equals(logicName)) {

									parameter.setLogicName(logicName);
									if (defined.getBClass().getQualifiedName().equals(List.class.getName())) {
										parameter.setParameterizedTypeValue(defined.getParameterizedTypeValue());
										parameter.setBClass(defined.getBClass().cloneAll());
									}
								}
							} else {
								if (defined.getUserAttribute("PARAMETER_TYPE_CHANGED") != null) {
									parameter.setLogicName(logicName);
									parameter.setBClass(defined.getBClass());
									parameter.addUserAttribute("PARAMETER_TYPE_CHANGED", "PARAMETER_TYPE_CHANGED");
								}
							}
						}
					}
				}
			}
		}
		if (parameter instanceof BasicNode) {
			BasicNode b = (BasicNode) parameter;
			if (parameter.getLogicName().indexOf(")") > 0) {
				BValuable value = provider.getConstantsName(parameter);
				if (value instanceof mxCell) {
					b.replace((mxCell) value);
				}
			}
		}
		Object targetType = parameter.getUserAttribute("TARGET_TYPE_PARAMETER");
		if (targetType instanceof BVariable) {
			BVariable var = (BVariable) targetType;
			BType type = var.getParameterizedTypeValue();
			if (type != null) {
				parameter.setParameterizedTypeValue(type);
				parameter.addUserAttribute("PARAMETER_TYPE_CHANGED", "PARAMETER_TYPE_CHANGED");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void makeDataFlow(BProject project) {
		List<Comsumer> list = new ArrayList<Comsumer>();
		for (BClass model : this.logics) {
			if (!model.isInterface()) {
				List<Comsumer> comsumers = BasicGenUtils.findUsers((BeeModel) model);
				for (Comsumer c : comsumers) {

					list.add(c);

				}

			}
		}

		// for other
		this.sendComsumer(list);

		// for io
		this.sendComsumer((List<Comsumer>) ObjectFileUtils.deepCopy(IOConsumers));

		this.changeDuplicate();

		this.doAddTransfer();

		this.makeIOSetter();
	}

	private void makeIOSetter() {
		@SuppressWarnings("unchecked")
		List<Comsumer> setter = (List<Comsumer>) ObjectFileUtils.deepCopy(IOSetter);
		for (Comsumer consumer : setter) {
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

							BInvoker invoker = consumer.getTarget();
							invoker.setInvokeParent(producer.getTargetTransferParameter());

							this.validateData(invoker);

						}
					}
				}
			}
		}
		// this.changeTrasferGetter(list);
	}

	private void sendComsumer(List<Comsumer> list) {

		for (Comsumer consumer : list) {
			boolean matched = false;

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
						matched = true;
						BeeModel bm = (BeeModel) m;
						Producer producer = BasicGenUtils.findProducer((mxICell) bm.getRoot(), consumer, null);
						if (producer != null) {

							this.deleteDuplicate(producer, consumer, provider);

						} else {
							System.out.println(consumer.getTarget());
							Debug.a();
						}
					}
				}
			}
			if (!matched) {
				System.out.println(consumer.getTarget());
				System.out.println(consumer.getCell().getPathKey());
				P.check(null);
				Debug.a();
			}
		}

		// this.changeTrasferGetter(list);
	}

	// public void changeTransferGetter(List<Comsumer> list) {
	// for (Comsumer consumer : list) {
	// BInvoker target = consumer.getTarget();
	// BParameter parent = consumer.getParameter();
	// parent.setCaller(true);
	// target.setInvokeParent((BValuable) parent.cloneAll());
	// }
	// }

	List<SetterInfo> pairs = new ArrayList<SetterInfo>();

	public void changeDuplicate() {
		this.invokersPerMethod.clear();
		for (SetterInfo p : pairs) {
			BInvoker invoker = p.getInvoker();
			long pathId = p.getPathId();
			if (invoker.getParameters().size() != 1) {
				continue;
			}

			List<BInvoker> invokers = this.invokersPerMethod.get(pathId + "");
			if (invokers == null) {
				invokers = new ArrayList<BInvoker>();
				invokersPerMethod.put(pathId + "", invokers);
			}

			String name = BValueUtils.createValuable(invoker, true);
			Object hashCode = invoker.getInvokeParent().getUserAttribute("PARENT_HASH_CODE");
			BInvoker old = (BInvoker) invoker.cloneAll();
			for (BInvoker bin : invokers) {
				String n = BValueUtils.createValuable(bin, true);
				if (name.equals(n)) {
					Object hashC = bin.getInvokeParent().getUserAttribute("PARENT_HASH_CODE");
					if (hashC instanceof Integer && hashCode instanceof Integer) {
						int hc1 = (int) hashC;
						int hc2 = (int) hashCode;
						if (hc1 == hc2) {
							this.changeDuplicate(bin, invoker);
						}
					} else {
						this.changeDuplicate(bin, invoker);
					}
				}
			}

			invokers.add((BInvoker) invoker.cloneAll());
			invokers.add(old);
		}

	}

	public void changeDuplicate(BInvoker doubled, BInvoker invoker) {

		// TODO when change name ,remove from the class the old one
		// and change the first one
		BVariable aVar = (BVariable) doubled.getInvokeChild();
		BVariable bVar = (BVariable) invoker.getInvokeChild();

		String bName = BValueUtils.createValuable(bVar, true);

		int index = 1;
		char c = bName.charAt(bName.length() - 1);
		if (Character.isDigit(c)) {
			index = c;
		} else {
			bVar.setLogicName(bVar.getLogicName() + index);
			bVar.setName(bVar.getName() + index);
			index++;
		}
		aVar.setLogicName(aVar.getLogicName() + index);
		aVar.setName(aVar.getName() + index);

	}

	public void doAddTransfer() {
		for (SetterInfo p : pairs) {
			p.getBody().addUnit(p.getInvoker(), p.getLocation());
			this.validateData(p.getInvoker());
		}
	}

	public void deleteDuplicate(Producer producer, Comsumer consumer, BLogicProvider provider) {
		BInvoker comsumerInvoker = consumer.getTarget();

		BLogicBody body = producer.getScope();

		IPatternCreator view = PatternCreatorFactory.createView();

		BInvoker setter = view.createMethodInvoker();

		BVariable ttp = producer.getTargetTransferParameter();
		setter.setInvokeParent((BValuable) ttp.cloneAll());

		BVariable var = producer.getTargetTranferData();

		// because var is impl
		BParameter target = view.createParameter();
		target.setLogicName(var.getLogicName());
		target.setName(var.getName());
		target.setBClass(var.getBClass());
		target.setUserAttributes(var.getUserAttributes());

		if (comsumerInvoker.getUserAttribute(BasicGenUtils.STATIC_CALL) != null) {
			BValuable parentValue = comsumerInvoker.getInvokeParent();
			if (parentValue instanceof BInvoker) {
				BInvoker childInvoker = (BInvoker) parentValue;
				BVariable bvar = (BVariable) childInvoker.getInvokeChild();
				target.setName(bvar.getName());
				target.setLogicName(bvar.getLogicName());
			} else {
				BVariable bvar = (BVariable) parentValue;
				target.setName(bvar.getName());
				target.setLogicName(bvar.getLogicName());
			}
		}

		setter.setInvokeChild((BValuable) target.cloneAll());

		// getter to be parameter
		// consumer.getTarget();
		BInvoker getter = consumer.getTarget();

		if (comsumerInvoker.getUserAttribute(BasicGenUtils.STATIC_CALL) != null) {
			BVariable child = (BVariable) comsumerInvoker.getInvokeChild();

			if (child.getBClass() == null || child.getBClass().getQualifiedName().equals(BClass.NULL)) {
				return;
			}

			if (!provider.onDataTransfer(setter, child)) {
				setter.addParameter(child);
			}

		} else {
			if (!provider.onDataTransfer(setter, getter)) {
				setter.addParameter(getter);
			}
		}

		if (!this.InvokerDoubled(consumer.getCell().getPathKey() + "", setter)) {
			SetterInfo info = new SetterInfo();
			info.setBody(body);
			info.setInvoker(setter);
			info.setLocation(producer.getLocation());
			info.setPathId(consumer.getCell().getPathKey());
			this.pairs.add(info);
		}

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

	public static class SetterInfo {
		private BLogicBody body;
		private BInvoker invoker;
		private int location;
		private long pathId;

		public long getPathId() {
			return pathId;
		}

		public void setPathId(long pathId) {
			this.pathId = pathId;
		}

		public BLogicBody getBody() {
			return body;
		}

		public void setBody(BLogicBody body) {
			this.body = body;
		}

		public BInvoker getInvoker() {
			return invoker;
		}

		public void setInvoker(BInvoker invoker) {
			this.invoker = invoker;
		}

		public int getLocation() {
			return location;
		}

		public void setLocation(int location) {
			this.location = location;
		}

	}

}
