package com.linkstec.bee.core.impl;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BClassImpl extends BTypeImpl implements BClass, Serializable, Cloneable, BEditorModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6858467285645201588L;

	private String bpackage;
	private List<BImport> imports = new ArrayList<BImport>();
	private List<BAssignment> variables = new ArrayList<BAssignment>();
	private List<BMethod> methods = new ArrayList<BMethod>();
	private List<BConstructor> constructors = new ArrayList<BConstructor>();

	// private List<String> parameterizedTypeNames = new ArrayList<String>();
	// private BType type;

	private List<BLogicBody> blocks = new ArrayList<BLogicBody>();
	private BValuable superClass;
	private List<BValuable> interfaces = new ArrayList<BValuable>();

	private boolean exception = false;
	private boolean interfa = false;
	private boolean isenum = false;
	private String innerParentClassName = null;
	private int mod = Modifier.PUBLIC;
	private boolean anonymous = false;
	private boolean data = false;
	private boolean logic = true;

	public BClassImpl() {
		this.setUserObject(BClass.TYPE_COMPLEX);
	}

	public void setVariables(List<BAssignment> variables) {
		this.variables = variables;
	}

	@Override
	public List<BAssignment> getVariables() {
		return this.variables;
	}

	public void setMethods(List<BMethod> methods) {
		this.methods = methods;
	}

	@Override
	public List<BMethod> getMethods() {
		return this.methods;
	}

	@Override
	public String getPackage() {
		return this.bpackage;
	}

	@Override
	public void setImports(List<BImport> imports) {
		this.imports = imports;
	}

	public String toString() {
		return this.getLogicName();
	}

	@Override
	public String getQualifiedName() {
		return BUtils.getClassQulifiedName(this, this.logicName);

	}

	@Override
	public List<BImport> getImports() {
		return this.imports;
	}

	public void addBlock(BLogicBody block) {
		this.blocks.add(block);
	}

	@Override
	public List<BLogicBody> getBlocks() {
		return this.blocks;
	}

	@Override
	public void setConstructors(List<BConstructor> constructors) {
		this.constructors = constructors;
	}

	@Override
	public List<BConstructor> getConstructors() {
		return this.constructors;
	}

	@Override
	public BClass cloneAll() {
		try {

			BClassImpl b = (BClassImpl) this.clone();
			this.cloneList(b);
			return b;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isException() {
		return this.exception;
	}

	@Override
	public boolean isInterface() {
		return this.interfa;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(mod);
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(mod);
	}

	@Override
	public void setInterface(boolean interfa) {
		this.interfa = interfa;
	}

	public void setException(boolean exception) {
		this.exception = exception;
	}

	@Override
	public void setInnerParentClassName(String name) {
		this.innerParentClassName = name;
	}

	@Override
	public String getInnerParentClassName() {
		return this.innerParentClassName;
	}

	@Override
	public void setSuperClass(BValuable name) {
		this.superClass = name;
	}

	@Override
	public BValuable getSuperClass() {
		return this.superClass;
	}

	@Override
	public void addInterface(BValuable name) {
		this.interfaces.add(name);
	}

	@Override
	public List<BValuable> getInterfaces() {
		return this.interfaces;
	}

	@Override
	public void setModifier(int mode) {
		this.mod = mode;
	}

	@Override
	public int getModifier() {
		return this.mod;
	}

	@Override
	public void addUnionType(String type) {
		this.unions.add(type);

	}

	private List<String> unions = new ArrayList<String>();

	@Override
	public List<String> getUnionTypes() {
		return this.unions;
	}

	@Override
	public boolean isNullClass() {
		if (this.getQualifiedName().equals(BClass.NULL)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return BUtils.isPrimeryClass(this.getLogicName());
	}

	@Override
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;

	}

	@Override
	public boolean isAnonymous() {
		return this.anonymous;
	}

	@Override
	public void setData(boolean data) {
		this.data = data;

	}

	@Override
	public boolean isData() {

		return this.data;
	}

	@Override
	public void setLogic(boolean logic) {
		this.logic = logic;
	}

	@Override
	public boolean isLogic() {
		return this.logic;
	}

	@Override
	public void setPackage(String bpackage) {
		this.bpackage = bpackage;
	}

	@Override
	public boolean isClass() {
		return true;
	}

	@Override
	public boolean isInnerClass() {
		return this.logicName.indexOf("$") > 0;
	}

	public void setEnum() {
		this.isenum = true;
	}

	@Override
	public boolean isEnum() {
		return this.isenum;
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		return null;
	}

	@Override
	public Object doSearch(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BEditor getSheet(BProject project) {

		return null;
	}

	@Override
	public void addVar(BAssignment var) {
		this.variables.add(var);
	}

	@Override
	public void removeVar(BAssignment var) {
		this.variables.remove(var);
	}

	@Override
	public void addVar(int index, BAssignment var) {
		this.variables.add(index, var);
	}

	@Override
	public void removeMethod(BMethod method) {
		this.methods.remove(method);
	}

}
