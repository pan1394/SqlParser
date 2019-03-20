package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ReturnType;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.basic.IActionModel;

public class BActionModel implements IActionModel, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4139830175946715851L;

	private List<BasicComponentModel> inputModels = new ArrayList<BasicComponentModel>();
	private List<BasicComponentModel> outputModels = new ArrayList<BasicComponentModel>();

	private BasicComponentModel input;
	private BasicComponentModel output;
	private String name;
	private String logicName;

	private BActionModel parent;
	private ReturnType returnType = null;
	private ProcessType processType;
	private ActionModel processModel;
	// private List<BLogic> logics = new ArrayList<BLogic>();
	private SubSystem sub;
	private BClass declearedClass;

	public BActionModel(ActionModel processModel, BActionModel parent, ProcessType processType) {
		this.processType = processType;
		this.processModel = processModel;
		this.parent = parent;
	}

	public SubSystem getSubSystem() {
		if (sub == null) {
			if (parent != null) {
				return parent.getSubSystem();
			}
		}
		return this.sub;
	}

	public void setSubSystem(SubSystem sub) {
		this.sub = sub;
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

	public ReturnType getReturnType() {
		return returnType;
	}

	public void setReturnType(ReturnType returnType) {
		this.returnType = returnType;
	}

	// public List<BLogic> getLogics() {
	// return logics;
	// }
	//
	// public void setLogics(List<BLogic> logics) {
	// this.logics = logics;
	// }

	public ActionModel getProcessModel() {
		return this.processModel;
	}

	public ProcessType getProcessType() {
		return this.processType;
	}

	public BActionModel getParentModel() {
		return this.parent;
	}

	public List<BasicComponentModel> getInputModels() {
		return inputModels;
	}

	public void setInputModels(List<BasicComponentModel> inputModes) {
		this.inputModels = inputModes;
	}

	public List<BasicComponentModel> getOutputModels() {
		return outputModels;
	}

	public void setOutputModels(List<BasicComponentModel> outputModes) {
		this.outputModels = outputModes;
	}

	public BClass getDeclearedClass() {
		return declearedClass;
	}

	public void setDeclearedClass(BClass declearedClass) {
		this.declearedClass = declearedClass;
	}

	public BasicComponentModel getOutput() {
		if (output == null) {
			if (this.parent != null) {
				return this.parent.getOutput();
			}
		}
		return output;
	}

	public void setOutput(BasicComponentModel output) {
		this.output = output;
	}

	public BasicComponentModel getInput() {
		if (input == null) {
			if (this.parent != null) {
				return this.parent.getInput();
			}
		}
		return input;
	}

	public void setInput(BasicComponentModel input) {
		this.input = input;
	}

	public int getActionDepth() {
		if (this.parent == null) {
			return 0;
		} else {
			return this.parent.getActionDepth() + 1;
		}
	}

	public String toString() {
		return this.name + ":" + this.logicName;
	}

}
