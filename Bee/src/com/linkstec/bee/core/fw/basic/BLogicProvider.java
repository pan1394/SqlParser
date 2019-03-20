package com.linkstec.bee.core.fw.basic;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;

public abstract class BLogicProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6709301993293944135L;
	protected BProperties properties;

	public BLogicProvider(IPatternCreator creator, BProperties propeties) {
		this.properties = propeties;
	}

	public String getTableName(String tableName) {
		return null;
	}

	public final BProperties getProperties() {
		return this.properties;
	}

	public String getReturnValue(BClass bclass, int layer, String componentLogicName, String subSystemLogicNamne) {
		return null;
	}

	public boolean onDataTransfer(BInvoker setter, BValuable parameter) {
		return false;
	}

	public BValuable getInvokeParent(BClass bclass) {
		return null;
	}

	public void onClassCreated(BClass bclass) {

	}

	public void onClassCreate(BClass bclass) {

	}

	public void onMethodCreated(BClass bclass, BMethod method) {

	}

	public void onMethodCreate(BClass bclass, BMethod method) {

	}

	public void onMethodParameterCreated(BClass bclass, BMethod method, BParameter parameter) {

	}

	public List<BLogicUnit> beforeMethodInvoker(BInvoker invoker) {
		return null;
	}

	public List<BUtilMethod> getCommonLogics(BClass bclass) {
		return null;
	}

	public BPropertiesUtil getPropeties(BClass bclass) {
		return null;
	}

	public List<BUtilMethod> getDataLogics(int layer) {
		return null;
	}

	public void createNexLayer(BClass bclass) {

	}

	public boolean isClassCreatable(BClass bclass) {
		return true;
	}

	public BClass manageGenerableClass(List<?> inputs, List<BClass> targets) {
		return null;
	}

	public BAssignment getNextLayerActionInstance(BClass bclass, BMethod method) {
		return null;
	}

	public BValuable getMessageID(String id) {
		return null;
	}

	public List<BLogicUnit> getGroupStartLogics(String desc) {
		return null;
	}

	public List<BLogicUnit> getGroupEndLogics(String desc) {
		return null;
	}

	public File doDetailExport(BProject project, BModule module, List<BSQLSet> set) {
		return null;
	}

}
