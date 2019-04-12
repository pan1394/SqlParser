package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLayerLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class NewLayerClassLogic extends BasicLogic implements BLayerLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8317387859065943342L;

	private String name;

	public NewLayerClassLogic(BPath parent, BasicComponentModel input, BActionPropertyNode node) {
		super(null, node);
		if (input == null) {
			if (parent != null) {
				BActionModel action = (BActionModel) parent.getAction();
				ActionModel am = action.getProcessModel();
				List<LayerModel> layers = am.getLayers();
				ProcessType type = layers.get(action.getActionDepth() + 1).getTargetProcessType();
				this.name = type.getName();
			}
		}
	}

	@Override
	public String getName() {
		return this.getDesc();
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.METHOD_ICON;
	}

	@Override
	public String getDesc() {

		BActionModel action = (BActionModel) this.getPath().getAction();
		if (action != null) {
			return action.getName();
		}

		return name;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		BPath path = this.getPath();

		BActionModel action = (BActionModel) path.getAction();

		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		BLogicProvider provider = this.getPath().getProvider();

		BClass bclass = BasicGenUtils.createClass(action, this.getPath().getProject());
		bclass.addUserAttribute("TEMP", "TEMP");

		// invoker
		BInvoker invoker = view.createMethodInvoker();

		// method for invoker to be child
		BMethod method = BasicGenUtils.createMethod(this.getPath(), bclass, action, provider, path.getProject());

		// keep info because it may change at provider.getNextLayerActionInstance
		BMethod old = (BMethod) ObjectFileUtils.deepCopy(method);
		List<BParameter> paras = this.getParameters();
		String oldParameterLogicName = null;
		String oldParameterName = null;
		for (BParameter para : paras) {
			BClass b = para.getBClass();
			if (b.isData()) {

				oldParameterLogicName = para.getLogicName();
				oldParameterName = para.getName();
				provider.getProperties().addThreadScopeAttribute("INPUT_PARAMETER_NAME", oldParameterName);
				provider.getProperties().addThreadScopeAttribute("INPUT_PARAMETER_LOGIC_NAME", oldParameterLogicName);
			}
		}

		List<BasicComponentModel> models = new ArrayList<BasicComponentModel>();
		models.addAll(action.getInputModels());
		models.addAll(action.getOutputModels());
		// method.addUserAttribute("MODELS", models);
		provider.getProperties().addThreadScopeAttribute("MODELS", models);

		BNote note = view.createComment();
		String name = action.getName();
		if (!name.endsWith("。")) {
			name = name + "処理";
		}
		note.setNote(name);
		units.add(note);
		note.addUserAttribute("DESC", note.getNote());

		provider.onMethodCreated(bclass, method);
		// new instance for invoker to be parent
		BAssignment var = provider.getNextLayerActionInstance(bclass, method);
		if (var == null) {
			Debug.a();
			var = BasicGenUtils.createInstance(bclass, provider);
			units.add(var);
		}

		// create instance
		BParameter instance = (BParameter) var.getLeft().cloneAll();
		instance.setCaller(true);
		instance.setClass(false);
		invoker.setInvokeParent(instance);
		invoker.setInvokeChild(method);

		Object ipara = provider.getProperties().getThreadScopeAttribute("INVOKER_PARAMETER");
		if (ipara instanceof BValuable) {
			BValuable param = (BValuable) ipara;
			invoker.addParameter(param);
		} else {
			for (BParameter para : paras) {
				BClass b = para.getBClass();
				if (b.isData()) {
					BAssignment data = BasicGenUtils.createInstance(b, provider);
					data.addUserAttribute("INPUT_DTO", this.getPath().getUniqueKey());
					units.add(data);

					BParameter p = data.getLeft();
					p.addUserAttribute("INPUT_PARAMETER", "INPUT_PARAMETER");
					this.addMark(p);
					if (oldParameterLogicName != null) {
						p.setLogicName(oldParameterLogicName);
					}
					if (oldParameterName != null) {
						p.setName(oldParameterName);
					}
					BParameter param = (BParameter) p.cloneAll();
					param.setClass(false);
					param.setCaller(true);
					invoker.addParameter(param);
				} else if (b.getQualifiedName().equals(List.class.getName())) {
					Debug.d("TODO");
				} else {
					Debug.d("TODO");
					invoker.addParameter(CodecUtils.getNullValue());
				}
			}
		}

		// table change logic

		Object obj = provider.getProperties().getThreadScopeAttribute("NEXT_LAYER_NEEDED_UNITS");
		if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			for (Object o : list) {
				if (o instanceof BLogicUnit) {
					BLogicUnit unit = (BLogicUnit) o;
					if (unit.getUserAttribute("INPUT_DTO") != null) {
						unit.addUserAttribute("INPUT_DTO", this.getPath().getUniqueKey());
						if (unit instanceof BAssignment) {
							BAssignment data = (BAssignment) unit;
							BParameter p = data.getLeft();
							p.addUserAttribute("INPUT_PARAMETER", "INPUT_PARAMETER");
							this.addMark(p);
						}
					}
					units.add(unit);
				}
			}
		}

		BValuable returnValue = method.getReturn();
		if (returnValue != null) {

			BAssignment result = BasicGenUtils.createInstanceWidthValue(returnValue.getBClass(), invoker, provider);

			note = view.createComment();
			note.setNote("「" + action.getName() + "」を呼び出して実施する");
			units.add(note);

			BActionModel model = (BActionModel) path.getAction();

			Object presult = provider.getProperties().getThreadScopeAttribute("INVOKER_RESULT");

			if (presult instanceof BAssignment) {
				// kill info
				provider.getProperties().addThreadScopeAttribute("INVOKER_RESULT", "NOTHING");
				// result.getLeft()
				// .setLogicName(BasicGenUtils.getOutputDtoInstanceName(result.getLeft(),
				// method.getLogicName()));
				units.add(result);

				BAssignment pr = (BAssignment) presult;

				BParameter resultParent = result.getLeft();
				resultParent = (BParameter) resultParent.cloneAll();
				resultParent.setClass(false);
				resultParent.setCaller(true);
				resultParent.getBClass().setData(true);

				note = view.createComment();
				note.setNote(model.getName() + "の処理結果");
				units.add(note);
				((BInvoker) pr.getRight()).setInvokeParent(resultParent);
				result = pr;

			}
			// use old name,because it might be used before the name changed
			result.getLeft().setLogicName(BasicGenUtils
					.getOutputDtoInstanceName(BasicNaming.getVarName(old.getReturn().getBClass()), old.getLogicName()));

			result.getLeft().addUserAttribute("OUTPUT_PARAMETER", "OUTPUT_PARAMETER");
			result.getLeft().setName(model.getName() + "の処理結果");

			units.add(result);

			BParameter parameter = result.getLeft();

			this.addMark(parameter);

		} else {
			units.add(invoker);
		}

		// System.out.println(action.getLogicName());

		return units;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();

		BPath path = this.getPath();
		BActionModel action = (BActionModel) path.getAction();

		IPatternCreator view = PatternCreatorFactory.createView();
		BLogicProvider provider = this.getPath().getProvider();
		BProject project = this.getPath().getProject();
		if (project == null) {
			project = Application.getInstance().getCurrentProject();
		}
		BClass bclass = BasicGenUtils.createClass(action, project);

		// invoker
		BInvoker invoker = view.createMethodInvoker();

		// method for invoker to be child
		BMethod method = BasicGenUtils.createMethod(this.getPath(), bclass, action, provider, path.getProject());

		BValuable returnValue = method.getReturn();
		if (returnValue != null) {

			BAssignment result = BasicGenUtils.createInstanceWidthValue(returnValue.getBClass(), invoker, provider);
			BActionModel model = (BActionModel) path.getAction();

			BParameter parameter = result.getLeft();
			parameter.setLogicName(BasicGenUtils.getOutputDtoInstanceName(result.getLeft(), method.getLogicName()));
			parameter.setName(model.getName() + "の処理結果");
			this.addMark(parameter);
			outputs.add(parameter);
		}

		return outputs;
	}

	public List<BParameter> getParameters() {
		BPath path = this.getPath();
		BActionModel action = (BActionModel) path.getAction();
		int dept = action.getActionDepth();

		ActionModel actionModel = action.getProcessModel();

		List<LayerModel> layers = actionModel.getLayers();
		if (dept < layers.size()) {
			LayerModel layer = layers.get(dept);
			List<Object> parameters;
			if (layer.getIndex() != 0) {
				parameters = layer.getParameters();
			} else {
				parameters = actionModel.getParameters();
			}

			BProject project = this.getPath().getProject();
			if (project == null) {
				project = Application.getInstance().getCurrentProject();
			}

			BeeModel impl = BasicGenUtils.createClass(action, project);
			impl.addUserAttribute("TEMP", "TEMP");

			List<BParameter> list = new ArrayList<BParameter>();
			for (Object obj : parameters) {
				BParameter d = BasicGenUtils.createMethodParameter(action.getSubSystem(), obj, impl, action.getInput(),
						BasicGenUtils.createMethod(path, impl, action, path.getProvider(), path.getProject()),
						path.getProject());

				d.setName(action.getName() + "入力情報");

				this.addMark(d);

				d.addUserAttribute("INPUT_PARAMETER", "INPUT_PARAMETER");
				list.add(d);

			}
			return list;
		}
		return null;

	}

}
