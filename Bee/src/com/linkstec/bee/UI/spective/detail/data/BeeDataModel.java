package com.linkstec.bee.UI.spective.detail.data;

import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.scroll.BeeScrollPaneErrorListener;
import com.linkstec.bee.UI.look.table.BeeTableModel;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.look.text.BeeTextField;
import com.linkstec.bee.UI.spective.detail.BeeDataSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BeeDataModel extends BeeTableModel implements Serializable, BClass, Cloneable, ILogic, BEditorModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4906853657376123148L;

	// Names of the columns.
	public static String[] names = { "No", "名称", "物理名称", "タイプ", "デフォルト値", "Annotation", "説明" };
	public static Class<?>[] types = { BeeTableModel.class, String.class, String.class, String.class, String.class,
			Annotation.class, String.class };

	transient BeeScrollPaneErrorListener errorListener;

	private String name = "New Data";
	private String logicName;
	private String bpackage;
	private List<BImport> imports = new ArrayList<BImport>();
	// private BType type;
	private BObject owner;
	private BType ownerType = null;

	private List<BMethod> methods = new ArrayList<BMethod>();
	private List<BConstructor> constructors = new ArrayList<BConstructor>();
	private List<BType> parameterizedTypeNames = new ArrayList<BType>();
	private BAlert alert = new BAlert();
	private BType arrayPressentClass;
	private String innerParentClassName;
	private BValuable superClass;
	private List<BValuable> interfaces = new ArrayList<BValuable>();
	private int mod = Modifier.PUBLIC;

	private Object object = BClass.TYPE_DEFINED;
	private boolean anonymous = false;

	public BeeDataModel() {
		init();
	}

	public void init() {
		BeeTableNode root = this.getRoot();
		if (root == null) {
			root = new BeeTableNode(null, types.length);
			initialize(root, names, types);
			if (root.getChildCount() == 0) {
				this.add100Blank();
			}

		}
	}

	public void add100Blank() {
		BeeTableNode root = this.getRoot();
		for (int i = 0; i < 100; i++) {
			BeeTableNode child = new BeeTableNode(root, root.getColumnCount());
			root.addChild(child);
		}
	}

	@Override
	public ImageIcon getIconAt(Object node, int column) {
		BeeTableNode n = (BeeTableNode) node;
		Object value = n.getValueAt(column);
		if (column == 3) {
			BClass[] btypes = CodecUtils.getAllBClasses();
			for (BClass model : btypes) {
				if (value instanceof BClass) {
					BClass b = (BClass) value;
					if (model.getQualifiedName().equals(b.getQualifiedName())) {
						return BeeConstants.CLASSES_ICON;
					}
				}
			}
		}
		return super.getIconAt(node, column);
	}

	@Override
	public void editText(BeeTextField text, int column, int row) {
		if (column == 3) {
			text.setProject(Application.getInstance().getCurrentProject());
		} else {
			text.setProject(null);
		}
	}

	public BeeScrollPaneErrorListener getErrorListener() {
		return errorListener;
	}

	public void setErrorListener(BeeScrollPaneErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	@Override
	public boolean isValid(int column, int row, Object value) {
		if (value == null) {
			return true;
		}

		String s = value.toString();
		if (s == null) {
			return true;
		}
		if (s.equals("")) {
			return true;
		}
		if (column == 3) {

		} else if (column == 2) {

			// String regEx = "^[a-zA-Z_]{1,}[0-9]{0,}";
			//
			// Pattern pattern = Pattern.compile(regEx);
			// Matcher matcher = pattern.matcher(s);
			// return matcher.matches();
		} else if (column == 4 || column == 5) {

		} else if (column == 6) {

			// if (s.equals("Y") || s.equals("N")) {
			// return true;
			// }
			// return false;
		}
		return super.isValid(column, row, value);
	}

	@Override
	public void fireError(String name, int line, boolean valid, String message, Object object) {
		if (errorListener != null) {
			if (valid) {
				errorListener.clearError(name);
			} else {
				errorListener.error(name, line, message, object);
			}
		}
	}

	@Override
	public void setOwener(BObject object) {
		this.owner = object;
	}

	@Override
	public BObject getOwener() {
		return this.owner;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getLogicName() {
		return this.logicName;
	}

	@Override
	public void setLogicName(String name) {
		this.logicName = name;

	}

	@Override
	public void setPackage(String bpackage) {
		this.bpackage = bpackage;
	}

	@Override
	public String getPackage() {
		return this.bpackage;
	}

	@Override
	public void setImports(List<BImport> imports) {
		this.imports = imports;
	}

	@Override
	public List<BImport> getImports() {
		return this.imports;
	}

	@Override
	public List<BAssignment> getVariables() {
		List<BAssignment> variables = new ArrayList<BAssignment>();

		BeeTableNode root = this.getRoot();
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTableNode node = (BeeTableNode) root.getChild(i);
			if (node.getLeft() != null) {
				BAssignment parameter = node;
				variables.add(parameter);
			}
		}
		return variables;
	}

	public void clearBlank() {

		BeeTableNode root = this.getRoot();
		int count = root.getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			BeeTableNode node = (BeeTableNode) root.getChild(i);
			if (node.getLeft() == null) {
				root.removeChild(node);
			}
		}

	}

	public void setVariables(List<BAssignment> variables, boolean methodhad) {
		for (BAssignment assign : variables) {
			this.addVariable(assign, methodhad);
		}
	}

	public void addVariable(BAssignment assign, boolean methodRestored) {

		root.addChild(this.makeNode(assign, methodRestored));
	}

	public void addVariableAt(int index, BAssignment assign, boolean methodRestored) {
		root.addChildAt(this.makeNode(assign, methodRestored), index);
	}

	private BeeTableNode makeNode(BAssignment assign, boolean methodRestored) {
		assign = (BAssignment) assign.cloneAll();
		BeeTableNode node = new BeeTableNode(root, this.getColumnCount());
		BParameter var = assign.getLeft();

		node.setValueAt(node, 0);
		node.setValueAt(var.getName(), 1);
		node.setValueAt(var.getLogicName(), 2);
		node.setValueAt(var.getBClass(), 3);
		node.setValueAt(assign.getRight(), 4);
		node.setMehodResotred(methodRestored);
		node.setMod(var.getModifier());
		List<BAnnotation> annos = assign.getAnnotations();
		for (BAnnotation anno : annos) {
			node.addAnnotation(anno);
		}

		BType type = var.getParameterizedTypeValue();
		if (type != null) {
			List<BType> paras = type.getParameterizedTypes();
			this.makeChild(node, paras);
		}
		return node;
	}

	public void addEditableRows() {

		for (int i = 0; i < 100; i++) {
			BeeTableNode child = new BeeTableNode(root, this.getColumnCount());
			root.addChild(child);
		}
	}

	private void makeChild(BeeTableNode node, List<BType> paras) {

		for (BType bclss : paras) {
			// BClass bclss = value.getBClass();
			BeeTableNode child = new BeeTableNode(node, this.getColumnCount());
			child.setValueAt(child, 0);
			child.setValueAt(bclss.getName(), 1);
			child.setValueAt(bclss.getLogicName(), 2);
			child.setValueAt(bclss, 3);
			node.addChild(child);
			this.makeChild(child, bclss.getParameterizedTypes());
		}
	}

	public void setMethods(List<BMethod> methods) {
		this.methods = methods;
	}

	@Override
	public List<BMethod> getMethods() {
		return this.methods;
	}

	// public BType getType() {
	// return type;
	// }
	//
	// public void setType(BType type) {
	// this.type = type;
	// }

	public String toString() {
		return this.name;
	}

	@Override
	public BEditor getSheet(BProject project) {

		this.init();
		BeeDataSheet sheet = new BeeDataSheet(this, project);
		return sheet;
	}

	@Override
	public String getQualifiedName() {
		return BUtils.getClassQulifiedName(this, this.logicName);

	}

	@Override
	public String getAlert() {
		return this.alert.getMessage();
	}

	@Override
	public BAlert setAlert(String alert) {
		this.alert = new BAlert();
		this.alert.setMessage(alert);
		return this.alert;
	}

	@Override
	public boolean isArray() {
		return this.arrayPressentClass != null;
	}

	@Override
	public BType getArrayPressentClass() {
		return this.arrayPressentClass;
	}

	@Override
	public void setArrayPressentClass(BType bclass) {
		this.arrayPressentClass = bclass;
	}

	@Override
	public List<BLogicBody> getBlocks() {
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void setUserObject(Object object) {
		this.object = object;

	}

	@Override
	public Object getUserObject() {
		return this.object;
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
			return (BClass) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isException() {
		return false;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isFinal() {
		return false;
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
	public BAlert getAlertObject() {
		return this.alert;
	}

	@Override
	public Object doSearch(String keyword) {
		return null;
	}

	@Override
	public void setModifier(int mode) {
		this.mod = mode;
	}

	@Override
	public int getModifier() {
		return this.mod;
	}

	private List<BAnnotation> annotations = new ArrayList<BAnnotation>();

	@Override
	public void addAnnotation(BAnnotation annotation) {
		this.annotations.add(annotation);
	}

	@Override
	public List<BAnnotation> getAnnotations() {
		return this.annotations;
	}

	@Override
	public void addParameterizedType(BType bclass) {
		this.parameterizedTypeNames.add(bclass);

	}

	@Override
	public void setParameterTypes(List<BType> types) {
		this.parameterizedTypeNames = types;
	}

	@Override
	public List<BType> getParameterizedTypes() {
		return this.parameterizedTypeNames;
	}

	@Override
	public void addUnionType(String type) {
		this.unions.add(type);

	}

	private List<String> unions = new ArrayList<String>();

	private boolean rawType = false;

	@Override
	public List<String> getUnionTypes() {

		return this.unions;
	}

	@Override
	public NodeNumber getNumber() {
		return null;
	}

	@Override
	public boolean isNullClass() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return BUtils.isPrimeryClass(logicName);
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
	public void setInterface(boolean inter) {

	}

	@Override
	public void setData(boolean data) {

	}

	@Override
	public boolean isData() {
		return true;
	}

	@Override
	public void setLogic(boolean logic) {

	}

	@Override
	public boolean isLogic() {
		return false;
	}

	@Override
	public void addBound(String type) {
	}

	@Override
	public List<String> getBounds() {
		return null;
	}

	@Override
	public boolean isWild() {
		return false;
	}

	@Override
	public boolean isParameterized() {
		return this.parameterizedTypeNames.size() > 0;
	}

	@Override
	public boolean isTypeVariable() {
		return false;
	}

	@Override
	public boolean isClass() {
		return true;
	}

	@Override
	public void clearParameterTypes() {
		this.parameterizedTypeNames.clear();
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isParameterValue() {
		return true;
	}

	@Override
	public void setParameterValue(boolean value) {

	}

	@Override
	public boolean isRawType() {

		return this.rawType;
	}

	@Override
	public void setRowType(boolean raw) {
		this.rawType = raw;
	}

	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		for (BAnnotation anno : this.annotations) {
			if (anno.getLogicName().equals(annotion.getLogicName())) {
				this.annotations.remove(anno);
				break;
			}
		}
	}

	@Override
	public boolean isInnerClass() {
		return false;
	}

	@Override
	public boolean isEnum() {
		return false;
	}

	@Override
	public void setOwnerType(BType type) {
		this.ownerType = type;
	}

	@Override
	public BType getOwnerType() {
		return this.ownerType;
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {

		return this.getSheet(project);
	}

	@Override
	public void addVar(BAssignment var) {
		this.addVariable(var, false);
	}

	@Override
	public void removeVar(BAssignment var) {
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeTableNode node = (BeeTableNode) root.getChild(i);
			BParameter left = node.getLeft();
			if (left != null) {
				if (left.getLogicName().equals(var.getLeft().getLogicName())) {
					this.root.removeChild(node);
					break;
				}
			}
		}

	}

	@Override
	public void addVar(int index, BAssignment var) {
		this.addVariableAt(index, var, false);
	}

	@Override
	public void removeMethod(BMethod method) {

	}
}
