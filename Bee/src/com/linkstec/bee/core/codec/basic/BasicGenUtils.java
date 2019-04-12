package com.linkstec.bee.core.codec.basic;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ReturnType;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BGroupLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.BasicNaming;
import com.linkstec.bee.UI.spective.basic.logic.node.BFlowStart;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicConnector;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IBodyCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.BMethodImpl;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class BasicGenUtils {
	private static IPatternCreator view = PatternCreatorFactory.createView();

	public static final String INVOKER_SOURCE = "SOURCEPATH";

	public static final String STATIC_CALL = "STATIC_CALL";

	public static BeeModel createClass(BActionModel action, BProject project) {
		if (project == null) {
			project = Application.getInstance().getCurrentProject();
		}
		ActionModel actionModel = action.getProcessModel();
		if (actionModel == null) {
			return null;
		}

		int dept = action.getActionDepth();

		List<LayerModel> layers = actionModel.getLayers();
		if (dept < layers.size()) {
			LayerModel layer = layers.get(dept);

			BeeModel impl = new BeeModel();

			impl.addUserAttribute("PROCESS_TYPE", action.getProcessType().getTitle());

			BasicComponentModel type = action.getInput();

			NamingModel cname = layer.getName();
			String className = cname.getName(action.getSubSystem(), type, null, null);

			impl.setLogicName(className);
			className = cname.getLocalName(action.getSubSystem(), type, null, null);
			impl.setName(className);

			// package
			NamingModel pack = layer.getPackegeName();
			String packageName = pack.getName(action.getSubSystem(), type, null, null);
			if (packageName != null) {
				impl.setPackage(packageName);
			}
			action.setDeclearedClass(impl);

			// interface
			List<String> inters = layer.getInterfaces();
			impl.getInterfaces().clear();
			for (String inter : inters) {
				BVariable var = view.createVariable();
				BClass bclss = CodecUtils.getClassFromJavaClass(project, inter);
				var.setBClass(bclss);
				var.setLogicName(bclss.getLogicName());
				var.setName(bclss.getName());
				impl.addInterface(var);
			}

			// superclass
			String superClass = layer.getSuperClass();
			if (superClass != null) {
				BVariable var = view.createVariable();
				BClass bclss = CodecUtils.getClassFromJavaClass(project, superClass);
				var.setBClass(bclss);
				var.setLogicName(bclss.getLogicName());
				var.setName(bclss.getName());
				impl.setSuperClass(var);
			}
			// annotation
			List<BAnnotation> annos = layer.getAnnotations();
			impl.getAnnotations().clear();
			for (BAnnotation anno : annos) {
				impl.addAnnotation(anno);
			}
			return impl;
		}
		return null;
	}

	public static BMethod createMethod(BPath path, BClass bclass, BActionModel action, BLogicProvider provider,
			BProject project) {
		BasicComponentModel input = action.getInput();

		BMethod method = view.createMethod();
		method.setModifier(Modifier.PUBLIC);
		method.addUserAttribute("PATH_ID", path.getUniqueKey());

		method.setLogicName(action.getLogicName());
		method.setName(action.getName());

		ActionModel model = action.getProcessModel();
		// List<Object> parameters = model.getParameters();

		int dept = action.getActionDepth();

		List<LayerModel> layers = model.getLayers();
		if (dept < layers.size()) {
			LayerModel layer = layers.get(dept);

			// BActionModel model = action;
			List<Object> parameters;
			Object returnObj;
			if (layer.getIndex() != 0) {
				parameters = layer.getParameters();
				returnObj = layer.getReturnType();
			} else {
				parameters = model.getParameters();
				returnObj = model.getReturnType();
			}

			for (Object obj : parameters) {
				BParameter para = createMethodParameter(action.getSubSystem(), obj, bclass, input, method, project);
				TransferCell cell = new TransferCell(path, bclass);
				para.addUserAttribute(INVOKER_SOURCE, cell);
				method.addParameter(para);
				if (provider != null) {
					provider.onMethodParameterCreated(bclass, method, para);
				}
			}

			BParameter returnValue = createMethodParameter(action.getSubSystem(), returnObj, bclass, action.getInput(),
					method, project);

			method.setReturn(returnValue);

			BClass returnBClass = returnValue.getBClass();

			BVariable var = view.createVariable();
			var.setBClass(returnBClass);
			var.setLogicName("m" + bclass.getLogicName().toLowerCase());
			var.setName(action.getName() + "の処理結果");

			ReturnType spacifiedTye = action.getReturnType();

			if (spacifiedTye == null) {
				method.setReturn(returnValue);
			} else {
				method.addUserAttribute("RETURN_TYPE", spacifiedTye.getType());
				if (spacifiedTye.getType() == ReturnType.TYPE_LIST_DATA) {
					BClass list = CodecUtils.getClassFromJavaClass(List.class, project);
					list.addParameterizedType(returnBClass.cloneAll());
					var.setBClass(list);
					var.setParameterizedTypeValue(returnBClass);
					method.setReturn(var);
				} else if (spacifiedTye.getType() == ReturnType.TYPE_INT) {
					var.setBClass(CodecUtils.BInt());
					method.setReturn(var);

				} else if (spacifiedTye.getType() == ReturnType.TYPE_STRING) {
					var.setBClass(CodecUtils.BString());
					method.setReturn(var);
				} else if (spacifiedTye.getType() == ReturnType.TYPE_DELETE) {
					var.setBClass(CodecUtils.BInt());
					method.setReturn(var);
				} else if (spacifiedTye.getType() == ReturnType.TYPE_INSERT) {
					var.setBClass(CodecUtils.BInt());
					method.setReturn(var);
				} else if (spacifiedTye.getType() == ReturnType.TYPE_UPDATE) {
					var.setBClass(CodecUtils.BInt());
					method.setReturn(var);
				} else if (spacifiedTye.getType() == ReturnType.TYPE_SINGLE_DATA) {
					method.setReturn(var);
				}
			}

			if (provider != null) {
				provider.onMethodCreate(bclass, method);
			}

			return method;
		}
		return null;

	}

	public static BParameter createMethodParameter(SubSystem sub, Object obj, BClass model,
			BasicComponentModel component, BMethod method, BProject project) {
		BParameter para = view.createParameter();

		if (obj instanceof String) {
			String s = (String) obj;
			BClass bclass = CodecUtils.getClassFromJavaClass(project, s);
			para.setBClass(bclass);
			para.setLogicName("m" + bclass.getLogicName().toLowerCase());
		} else if (obj instanceof NamingModel[]) {
			NamingModel[] type = (NamingModel[]) obj;
			String s = type[0].getName(sub, component, model, method);

			BeeDataModel data = new BeeDataModel();
			data.setLogicName(s);
			data.setName(s);

			String packageName = type[1].getName(sub, component, model, method);
			data.setPackage(packageName);

			para.setBClass(data);
			para.setLogicName("m" + data.getLogicName().toLowerCase());
		}
		return para;
	}

	public static String getInputDtoInstanceName(BClass bclass, String methodLogicName) {
		return "m" + bclass.getLogicName().toLowerCase() + "in" + methodLogicName.toLowerCase();
	}

	public static String getOutputDtoInstanceName(BClass bclass, String methodLogicName) {
		return "m" + bclass.getLogicName().toLowerCase() + "out" + methodLogicName.toLowerCase();
	}

	public static String getInputDtoInstanceName(BVariable var, String methodLogicName) {
		return var.getLogicName() + "In" + methodLogicName;
	}

	public static String getOutputDtoInstanceName(BVariable var, String methodLogicName) {
		return var.getLogicName() + "Out" + methodLogicName;
	}

	public static String getOutputDtoInstanceName(String varName, String methodLogicName) {
		return varName + "Out" + methodLogicName;
	}

	public static String makeName(String name) {
		String tableName = name;
		if (tableName.indexOf("_") > 0) {
			String[] ss = tableName.split("_");
			tableName = "";
			for (String s : ss) {
				s = s.substring(0, 1).toUpperCase() + s.substring(1);
				tableName = tableName + s;
			}
		} else {
			tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
		}
		return tableName;
	}

	public static BAssignment createInstance(BClass bclass, BLogicProvider provider) {

		BVariable value = view.createVariable();
		value.setBClass(bclass);
		value.setLogicName(bclass.getLogicName());
		value.setNewClass(true);

		return createInstanceWidthValue(bclass, value, provider);
	}

	public static BAssignment createInstanceWidthValue(BClass bclass, BValuable value, BLogicProvider provider) {
		BAssignment var = view.createAssignment();
		BParameter left = view.createParameter();
		left.setBClass(bclass);

		String name = bclass.getLogicName();

		if (name.length() > 2) {
			name = name.substring(0, 2).toLowerCase() + name.substring(2);
			left.setLogicName(name);
			left.setName(name);
		} else {
			left.setLogicName(BasicNaming.getVarName(bclass));
			left.setName(bclass.getName() + "インスタンス作成");
		}

		if (provider == null) {
			Thread t = Thread.currentThread();
			if (t instanceof BeeThread) {
				BeeThread b = (BeeThread) t;
				provider = (BLogicProvider) b.getUserAttribute("PROVIDER");
			}
		}

		if (provider != null) {
			String n = provider.getVariableName(left, null);
			if (n != null) {
				left.setLogicName(n);
				left.setName(n);
			}
		}

		var.setLeft(left);
		var.setRight(value, null);
		return var;
	}

	public static void makeLogics(ILogicCell start, List<BLogic> list, boolean withGroupTitle) {

		if (start == null) {
			return;
		}

		if (start instanceof IBodyCell) {
			IBodyCell node = (IBodyCell) start;
			BLogic g = node.getLogic();
			if (g != null) {

				BGroupLogic logic = new BGroupLogic(null);
				logic.addUserAttribute("START", "START");
				logic.setPath(g.getPath());
				list.add(logic);
				//////////////////////

				ILogicCell s = node.getStart();

				makeLogics(s, list, withGroupTitle);

				/////////////////
				logic = new BGroupLogic(null);
				logic.addUserAttribute("END", "END");
				logic.setPath(g.getPath());
				list.add(logic);
				//////////////////////

			}

		} else if (start instanceof ILogicCell) {
			ILogicCell end = (ILogicCell) start;
			BLogic logic = end.getLogic();
			if (logic != null) {
				logic.getPath().setCell(end);
				list.add(logic);
			}
			forTest(end);
		}
		makeLogics(BasicGenUtils.getNext((BNode) start, start.getLogic().getPath()), list, withGroupTitle);

	}

	public static void forTest(ILogicCell cell) {

	}

	public static void makeLogics(List<BLogic> list, mxICell root, boolean withGroupTitle) {

		list.clear();

		BFlowStart start = getStart((mxICell) root);
		if (start != null) {
			BasicGenUtils.makeLogics(start, list, withGroupTitle);
		}
	}

	public static BFlowStart getStart(mxICell parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);
			if (child instanceof BFlowStart) {
				return (BFlowStart) child;
			}
			BFlowStart start = getStart(child);
			if (start != null) {
				return start;
			}
		}
		return null;
	}

	public static ILogicCell getStart(BNode node) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = node.getChildAt(i);
			if (child instanceof ILogicCell) {
				ILogicCell cell = (ILogicCell) child;
				int edges = child.getEdgeCount();
				if (edges == 0) {
					return cell;
				}

				boolean possible = false;
				int index = 0;
				for (int j = 0; j < edges; j++) {
					mxCell edge = (mxCell) child.getEdgeAt(j);
					mxICell target = edge.getTarget();
					mxICell source = edge.getSource();

					if (target == null || source == null) {
						continue;
					}

					if ((source instanceof ILogicCell && target.equals(child)) && source.getParent().equals(node)) {
						possible = false;
						index++;
						break;
					}
					if (source.equals(child) && target.getParent().equals(node)) {
						possible = true;
						index++;
					} else {

					}

				}

				if (possible || index == 0) {
					return cell;
				}
			}
		}
		return null;
	}

	public static List<ILogicCell> getNexts(BNode node, BPath parent) {
		List<ILogicCell> cells = new ArrayList<ILogicCell>();

		int count = node.getEdgeCount();
		for (int i = 0; i < count; i++) {
			mxICell child = node.getEdgeAt(i);
			if (child instanceof BLogicConnector) {
				BLogicConnector connector = (BLogicConnector) child;
				if (connector.getType() > 0) {
					continue;
				}
			}
			mxICell target = child.getTerminal(false);
			mxICell source = child.getTerminal(true);
			if (source.equals(node)) {
				if (target != null) {
					if (target instanceof ILogicCell) {
						ILogicCell logic = (ILogicCell) target;
						logic.getLogic().getPath().setParent(parent);
						logic.getLogic().getPath().setCell(logic);
						cells.add(logic);

						forTest(logic);

					}
				}
			}
		}

		return cells;
	}

	public static ILogicCell getNext(BNode node, BPath parent) {
		List<ILogicCell> logics = getNexts(node, parent);
		if (logics.size() == 1) {
			return logics.get(0);
		}
		return null;
	}

	public static BClass createParameterBClass(Object obj, SubSystem sub, LayerModel layer, BProject project,
			BClass impl, BActionModel action) {
		if (obj instanceof String) {

			String s = (String) obj;
			BClass bclass = CodecUtils.getClassFromJavaClass(project, s);

			return bclass;
		} else if (obj instanceof NamingModel[]) {
			NamingModel[] type = (NamingModel[]) obj;

			BasicDataModel data = new BasicDataModel(ComponentTypeModel.getDataModel());

			BMethod method = new BMethodImpl();
			method.setLogicName(action.getLogicName());
			method.setName(action.getName());

			String s = type[0].getName(sub, action.getInput(), impl, method);
			data.setLogicName(s);
			s = type[0].getLocalName(sub, action.getInput(), impl, method);
			data.setName(s);

			String pName = type[1].getName(sub, action.getInput(), impl, method);
			data.setPackage(pName);

			return data;
		}
		return null;
	}

	public static List<Comsumer> findUsers(BeeModel model) {
		List<Comsumer> list = new ArrayList<Comsumer>();
		mxICell root = (mxICell) model.getRoot();

		scanUsers(root, list, null, model);

		return list;
	}

	private static void scanUsers(mxICell parent, List<Comsumer> list, BMethod scope, BClass comsumerClass) {
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);
			if (child instanceof ObjectNode) {
				ObjectNode var = (ObjectNode) child;
				Object v = var.getValue();

				// inject
				BasicGenUtils.injectConsumer(v, list, scope, comsumerClass);

				if (v instanceof mxICell) {
					scanUsers((mxICell) v, list, scope, comsumerClass);
				}

			} else {
				// inject
				BasicGenUtils.injectConsumer(child, list, scope, comsumerClass);
			}
			BMethod method = scope;
			if (child instanceof BMethod) {
				method = (BMethod) child;
			}
			scanUsers(child, list, method, comsumerClass);
		}

	}

	public static void injectConsumer(Object cell, List<Comsumer> list, BMethod scope, BClass comsumerClass) {
		if (cell instanceof BInvoker) {
			BInvoker invoker = (BInvoker) cell;

			// if (invoker.getUserAttribute("PARENT_FIXED_INVOKER") != null) {
			// System.out.println(BValueUtils.createValuable(invoker, true));
			// System.out.println(BValueUtils.createValuable(invoker, false));
			// Debug.a();
			// return;
			// }
			BValuable value = invoker.getInvokeParent();
			if (value != null) {
				Object obj = value.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);

				if (obj instanceof TransferCell) {
					TransferCell t = (TransferCell) obj;
					Comsumer c = makeConsumer(invoker, t, scope, comsumerClass);
					list.add(c);
				}
			} else {
				Comsumer c = makeConsumer(invoker, null, scope, comsumerClass);
				list.add(c);
			}
		}
	}

	public static Comsumer makeConsumer(BInvoker invoker, TransferCell cell, BMethod scope, BClass comsumer) {
		Comsumer consumer = new Comsumer();
		consumer.setTarget(invoker);
		consumer.setScope(scope);
		consumer.setCell(cell);
		consumer.setComsumerClass(comsumer);
		if (scope != null) {
			List<BParameter> params = scope.getParameter();
			for (int i = 0; i < params.size(); i++) {
				BParameter para = params.get(i);
				if (para.getBClass().isData()) {
					// look up for first one
					consumer.setParameter(para);
					break;
				}
			}
		}
		return consumer;
	}

	public static Producer findProducer(mxICell parent, long pathId, BLogicBody scope) {
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);

			if (child instanceof ObjectNode) {
				ObjectNode node = (ObjectNode) child;
				Producer producer = findProducer(node, pathId, scope);
				if (producer != null) {
					return producer;
				}
			}

			if (scope != null) {

				// BLogicBody body = scope.getLogicBody();
				List<BLogicUnit> units = scope.getUnits();

				for (int index = 0; index < units.size(); index++) {
					BLogicUnit u = units.get(index);
					if (u instanceof BAssignment) {
						BAssignment assing = (BAssignment) u;
						BValuable right = assing.getRight();
						if (right instanceof BInvoker) {
							BInvoker target = (BInvoker) right;
							BValuable invokeChild = target.getInvokeChild();
							boolean match = false;
							if (invokeChild instanceof BMethod) {
								BMethod invokeMethod = (BMethod) invokeChild;
								Object obj = invokeMethod.getUserAttribute("PATH_ID");
								if (obj instanceof Long) {
									long id = (long) obj;
									long scopeId = pathId;
									if (id == scopeId) {
										match = true;
									}
								}
							}
							if (match) {
								Producer producer = new Producer();
								BLogicUnit last = units.get(index - 1);
								if (last instanceof BAssignment) {
									BAssignment a = (BAssignment) last;
									producer.setProducerTransferParameter(a.getLeft());
								}
								producer.setScope(scope);
								producer.setTarget(target);
								producer.setLocation(index);

								BAssignment a = findInputDto(scope, index);

								if (a == null) {
									List<BValuable> params = target.getParameters();
									for (BValuable pa : params) {
										if (pa.getBClass().isData()) {
											// find first one

											BParameter para = (BParameter) pa.cloneAll();
											para.setCaller(true);
											para.setClass(false);
											para.addUserAttribute("PARENT_HASH_CODE", pa.hashCode());
											producer.setTargetTransferParameter(para);
											break;
										}
									}
								} else {
									BParameter para = (BParameter) a.getLeft().cloneAll();
									para.setCaller(true);
									para.setClass(false);
									para.addUserAttribute("PARENT_HASH_CODE", a.getLeft().hashCode());
									producer.setTargetTransferParameter(para);
								}
								return producer;
							}
						}
					}
				}
			}

			BLogicBody b = scope;
			if (child instanceof BLogicBody) {
				scope = (BLogicBody) child;
			}
			Producer producer = findProducer(child, pathId, b);
			if (producer != null) {
				return producer;
			}
		}
		return null;

	}

	public static Producer findProducer(mxICell parent, Comsumer comsumer, BLogicBody scope) {
		int count = parent.getChildCount();
		// BPath path = comsumer.getProducerPath();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);

			BVariable value = null;
			if (child instanceof BVariable) {
				value = (BVariable) child;
			} else if (child instanceof BAssignment) {
				BAssignment assign = (BAssignment) child;
				value = assign.getLeft();
			} else if (child instanceof ObjectNode) {
				ObjectNode node = (ObjectNode) child;

				Producer producer = findProducer(node, comsumer, scope);
				if (producer != null) {
					return producer;
				}
			}
			if (value != null) {
				Object p = value.getUserAttribute(BasicGenUtils.INVOKER_SOURCE);

				if (p instanceof TransferCell) {
					TransferCell t = (TransferCell) p;

					if (t.getPathKey() == comsumer.getCell().getPathKey()) {

						if (scope != null) {
							Producer producer = BasicGenUtils.findTargetProducer(scope, comsumer, value);
							if (producer != null) {
								return producer;
							} else {
								producer = findNesScopeAndProducer((mxICell) scope, comsumer, value);
								if (producer != null) {
									return producer;
								}
							}
						} else {
							Debug.a();
						}
					}
				}
			}
			BLogicBody b = scope;
			if (child instanceof BLogicBody) {
				b = (BLogicBody) child;
			} else if (child instanceof BMethod) {
				// consider :use parameter
				BMethod method = (BMethod) child;
				b = method.getLogicBody();
			}
			Producer producer = findProducer(child, comsumer, b);
			if (producer != null) {
				return producer;
			}
		}
		return null;
	}

	private static Producer findNesScopeAndProducer(mxICell parent, Comsumer comsumer, BVariable value) {
		int count = parent.getChildCount();
		// BPath path = comsumer.getProducerPath();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);
			if (child instanceof BLogicBody) {
				Producer producer = findTargetProducer((BLogicBody) child, comsumer, value);
				if (producer != null) {
					return producer;
				}
			}
			Producer producer = findNesScopeAndProducer(child, comsumer, value);
			if (producer != null) {
				return producer;
			}
		}
		return null;
	}

	private static Producer findTargetProducer(BLogicBody body, Comsumer comsumer, BVariable value) {
		List<BLogicUnit> units = body.getUnits();
		for (int index = 0; index < units.size(); index++) {
			BLogicUnit u = units.get(index);
			if (u instanceof BAssignment) {
				BAssignment assing = (BAssignment) u;
				BParameter left = assing.getLeft();
				BValuable right = assing.getRight();
				if (right instanceof BInvoker) {
					BInvoker target = (BInvoker) right;
					BValuable invokeChild = target.getInvokeChild();
					boolean match = false;
					if (invokeChild instanceof BMethod) {
						BMethod invokeMethod = (BMethod) invokeChild;
						Object obj = invokeMethod.getUserAttribute("PATH_ID");
						if (obj instanceof Long) {
							long id = (long) obj;
							long scopeId = (long) comsumer.getScope().getUserAttribute("PATH_ID");
							if (id == scopeId) {
								match = true;
							}
						}
					}
					if (match) {
						Producer producer = new Producer();
						producer.setScope(body);
						producer.setProducerTransferParameter((BVariable) value.cloneAll());
						producer.setTargetTranferData((BVariable) comsumer.getTarget().getInvokeChild());
						producer.setTarget(target);
						producer.setLocation(index);
						producer.setAltiveTargetTransferParameter(left);

						BAssignment a = findInputDto(body, index);

						if (a == null) {
							List<BValuable> params = target.getParameters();
							for (BValuable pa : params) {
								if (pa.getBClass().isData()) {
									// find first one

									BParameter para = (BParameter) pa.cloneAll();
									para.setCaller(true);
									para.setClass(false);
									para.addUserAttribute("PARENT_HASH_CODE", pa.hashCode());
									producer.setTargetTransferParameter(para);

									break;
								}
							}
						} else {
							BParameter para = (BParameter) a.getLeft().cloneAll();
							para.setCaller(true);
							para.setClass(false);
							para.addUserAttribute("PARENT_HASH_CODE", a.getLeft().hashCode());
							producer.setTargetTransferParameter(para);
						}

						return producer;
					}
				}
			}
		}
		return null;
	}

	public static BAssignment findInputDto(BLogicBody body, int bottom) {
		List<BLogicUnit> units = body.getUnits();

		for (int index = bottom - 1; index >= 0; index--) {
			BLogicUnit u = units.get(index);
			if (u instanceof BAssignment) {
				BAssignment assign = (BAssignment) u;
				if (u.getUserAttribute("INPUT_DTO") != null) {

					return (BAssignment) u;
				}

				// break when go and got previous invoker(layer)
				BValuable value = assign.getRight();
				if (value instanceof BInvoker) {
					BInvoker bin = (BInvoker) value;
					if (bin.getInvokeChild().getUserAttribute("PATH_ID") != null) {
						// BAssignment b = (BAssignment) units.get(bottom);

						Debug.a();
						break;
					}
				}
			}

		}
		return null;
	}

	public static class Comsumer implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6085602147497142703L;

		// the target,which is used at the needed place
		private BInvoker target;

		// the place method
		private BMethod scope;

		// to replace the invoker parent
		private BParameter parameter;

		// the place class
		private BClass comsumerClass;

		// to remember info about producer
		private TransferCell cell;

		public BClass getComsumerClass() {
			return comsumerClass;
		}

		public void setComsumerClass(BClass comsumerClass) {
			this.comsumerClass = comsumerClass;
		}

		public TransferCell getCell() {
			return cell;
		}

		public void setCell(TransferCell cell) {
			this.cell = cell;
		}

		public BInvoker getTarget() {
			return target;
		}

		public void setTarget(BInvoker target) {
			this.target = target;
		}

		public BMethod getScope() {
			return scope;
		}

		public void setScope(BMethod scope) {
			this.scope = scope;
		}

		public BParameter getParameter() {
			return parameter;
		}

		public void setParameter(BParameter parameter) {
			this.parameter = parameter;
		}

	}

	public static class Producer implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -254356984142318395L;

		private BLogicBody scope;

		// the consumer instance class
		private BInvoker target;

		// to invoke the consumer,use the parameter,
		private BVariable targetTransferParameter;

		// the parameter catch the data to be transferred
		private BVariable producerTransferParameter;

		// the location the setter should be inserted(targetPosition)
		private int location;

		// target data to be transfer
		private BVariable targetTranferData;

		private BVariable altiveTargetTransferParameter;

		// targetTransferParameter.set[targetTranferData](producerTransferParameter.get[targetTranferData]())

		public BVariable getTargetTranferData() {
			return targetTranferData;
		}

		public BVariable getProducerTransferParameter() {
			return producerTransferParameter;
		}

		public void setProducerTransferParameter(BVariable producerTransferParameter) {
			this.producerTransferParameter = producerTransferParameter;
		}

		public BVariable getAltiveTargetTransferParameter() {
			return altiveTargetTransferParameter;
		}

		public void setAltiveTargetTransferParameter(BVariable altiveTargetTransferParameter) {
			this.altiveTargetTransferParameter = altiveTargetTransferParameter;
		}

		public void setTargetTranferData(BVariable targetTranferData) {
			this.targetTranferData = targetTranferData;
		}

		public int getLocation() {
			return location;
		}

		public void setLocation(int location) {
			this.location = location;
		}

		public BLogicBody getScope() {
			return scope;
		}

		public void setScope(BLogicBody scope) {
			this.scope = scope;
		}

		public BInvoker getTarget() {
			return target;
		}

		public void setTarget(BInvoker target) {
			this.target = target;
		}

		public BVariable getTargetTransferParameter() {
			return targetTransferParameter;
		}

		public void setTargetTransferParameter(BVariable targetTransferParameter) {
			this.targetTransferParameter = targetTransferParameter;
		}

	}

	public static class TransferCell implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8139580671232966869L;
		private long pathKey;
		private BClass producerClass;

		public TransferCell(BPath path, BClass producerClass) {
			this.pathKey = path.getUniqueKey();
			// for the sake of memory
			this.producerClass = CodecUtils.copyClassToSimpleTemp(producerClass);
		}

		public long getPathKey() {
			return pathKey;
		}

		public BClass getProducerClass() {
			return producerClass;
		}

		public void setProducerClass(BClass producerClass) {
			this.producerClass = producerClass;
		}

	}

	private static BMethod findUnit(mxICell child) {
		mxICell parent = child.getParent();
		if (child instanceof BMethod) {

			return (BMethod) child;

		}

		if (parent != null) {
			return findUnit(parent);
		}
		return null;
	}

	public static List<BClass> createLayer(BPath path, BProject project) {
		List<BClass> list = new ArrayList<BClass>();

		BActionModel action = (BActionModel) path.getAction();
		if (action == null) {
			return list;
		}

		BasicComponentModel type = action.getInput();

		if (type != null && action.getOutput() != null) {

			ActionModel actionModel = action.getProcessModel();
			if (actionModel == null) {
				return list;
			}

			int dept = action.getActionDepth();

			List<LayerModel> layers = actionModel.getLayers();
			if (dept < layers.size()) {
				LayerModel layer = layers.get(dept);
				List<Object> parameters;
				Object returnType;
				if (layer.getIndex() != 0) {
					parameters = layer.getParameters();
					returnType = layer.getReturnType();
				} else {
					parameters = actionModel.getParameters();
					returnType = actionModel.getReturnType();
				}

				////////////////////// make class/////////////////////
				BeeModel impl = BasicGenUtils.createClass(action, project);
				list.add(impl);
				/////////////////////// make class end//////////////////

				/////////////////////// make parameter//////////////////
				for (Object obj : parameters) {
					BClass bclass = getBClass(obj, action.getSubSystem(), layer, project, impl, action);
					if (bclass instanceof BasicDataModel) {
						BasicDataModel d = (BasicDataModel) bclass;
						list.add(d);
					}
				}

				/////////////////////// make return///////////////////////
				if (returnType != null) {
					BClass bclass = getBClass(returnType, action.getSubSystem(), layer, project, impl, action);
					if (bclass instanceof BasicDataModel) {
						BasicDataModel d = (BasicDataModel) bclass;
						list.add(d);
					}
				}

			}
		}
		return list;

	}

	public static BClass getBClass(Object obj, SubSystem sub, LayerModel layer, BProject project, BClass impl,
			BActionModel action) {
		if (obj instanceof String) {

			String s = (String) obj;
			BClass bclass = CodecUtils.getClassFromJavaClass(project, s);

			return bclass;
		} else if (obj instanceof NamingModel[]) {
			NamingModel[] type = (NamingModel[]) obj;

			BasicDataModel data = new BasicDataModel(ComponentTypeModel.getDataModel());

			BMethod method = new BMethodImpl();
			method.setLogicName(action.getLogicName());
			method.setName(action.getName());

			String s = type[0].getName(sub, action.getInput(), impl, method);
			data.setLogicName(s);
			s = type[0].getLocalName(sub, action.getInput(), impl, method);
			data.setName(s);

			String pName = type[1].getName(sub, action.getInput(), impl, method);
			data.setPackage(pName);

			return data;
		}
		return null;
	}

	public static BVariable toView(BVariable var) {

		if (var instanceof mxICell) {
			return var;
		} else {
			BParameter cellChild = view.createParameter();
			cellChild.setBClass(var.getBClass());
			cellChild.setName(var.getName());
			cellChild.setLogicName(var.getLogicName());
			cellChild.setCaller(var.isCaller());
			return cellChild;
		}
	}

	public static class Pair implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Producer producer;
		private Comsumer comsumer;

		public Producer getProducer() {
			return producer;
		}

		public void setProducer(Producer producer) {
			this.producer = producer;
		}

		public Comsumer getComsumer() {
			return comsumer;
		}

		public void setComsumer(Comsumer comsumer) {
			this.comsumer = comsumer;
		}

	}
}
