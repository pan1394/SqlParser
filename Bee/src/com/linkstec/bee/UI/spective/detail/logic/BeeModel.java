package com.linkstec.bee.UI.spective.detail.logic;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingUtilities;

import com.linkstec.bee.UI.node.AssignmentNode;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.BlockUnitNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ConstructorNode;
import com.linkstec.bee.UI.node.MethodNode;
import com.linkstec.bee.UI.node.view.ObjectMark;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.IBeeTitleUI;
import com.linkstec.bee.UI.spective.detail.action.BeeHandlers;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BImport;
import com.linkstec.bee.core.fw.BObject;
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
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;

import lombok.Data;

public class BeeModel extends mxGraphModel
		implements Serializable, IBeeTitleUI, ILogic, BClass, Cloneable, BEditorModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6821159925103105357L;

	/**
	 * 
	 */
	protected transient mxIEventListener changeTracker = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {

			SwingUtilities.invokeLater(new Thread(new Runnable() {

				@Override
				public void run() {
					BEditor editor = Application.getInstance().getDesignSpective().getGraphSheet();
					if (editor != null)
						editor.setModified(true);
				}

			}));

		}

	};
	private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();
	private String name = "New Design";
	private String logicName;
	private String bpackage;
	private List<BImport> imports = new ArrayList<BImport>();
	private List<BType> parameterizedTypeNames = new ArrayList<BType>();
	private BObject owner;
	private List<TitleChangeListener> titleChangeListeners = new ArrayList<TitleChangeListener>();
	private BAlert alert = new BAlert();
	private BType arrayPressentClass;
	private List<BeeModel> subSheet = new ArrayList<BeeModel>();
	private BeeModel parentView;
	private BValuable superClass;
	private List<BValuable> interfaces = new ArrayList<BValuable>();
	private List<BAnnotation> annotations = new ArrayList<BAnnotation>();
	private Object object = BClass.TYPE_DEFINED;
	private String innerParentClassName = null;
	private int mod = Modifier.PUBLIC;
	private boolean anonymous = false;
	private boolean inter = false;
	private boolean rawType = false;
	private BType ownerType = null;

	public BeeModel() {

	}

	public Hashtable<Object, Object> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public void addUserAttribute(Object key, Object value) {
		this.userAttributes.put(key, value);
	}

	public void removeUserAttribute(Object key) {
		this.userAttributes.remove(key);
	}

	public Object getUserAttribute(Object key) {
		if (this.userAttributes != null) {
			return this.userAttributes.get(key);
		} else {
			return null;
		}
	}

	public BeeModel(Object root) {
		super(root);

	}

	public void installHandlers(BeeGraphSheet sheet) {

		addListener(mxEvent.CHANGE, new BeeHandlers.SelectedHandler(sheet));
		addListener(mxEvent.CHANGE, changeTracker);

	}

	public BeeModel getParentView() {
		return parentView;
	}

	public void setParentView(BeeModel parentView) {
		this.parentView = parentView;
	}

	public List<BasicNode> doSearch(String keyword) {
		List<BasicNode> list = new ArrayList<BasicNode>();
		mxCell root = (mxCell) this.getRoot();
		this.doSearch(keyword, list, root);
		return list;
	}

	private void doSearch(String keyword, List<BasicNode> list, mxICell root) {
		int count = root.getChildCount();
		if (root instanceof BasicNode) {
			boolean added = false;
			if (root.getValue() != null) {
				if (root.getValue().toString().contains(keyword)) {
					list.add((BasicNode) root);
					added = true;
				}
			}
			if (!added) {
				if (root instanceof ILogic) {
					ILogic logic = (ILogic) root;
					if (logic.getLogicName() != null && logic.getLogicName().contains(keyword)) {
						list.add((BasicNode) root);
					} else if (logic.getName() != null && logic.getName().contains(keyword)) {
						list.add((BasicNode) root);
					}
				}
			}
		}
		for (int i = 0; i < count; i++) {
			mxICell child = root.getChildAt(i);
			doSearch(keyword, list, child);
		}
	}

	public Object getCellById(String id) {
		if (id == null) {
			return null;
		}
		return this.getCellById(id, root);

	}

	public mxICell getPossibleCellById(String id) {
		if (id == null) {
			return null;
		}
		return this.getPossibleCellById(id, root);

	}

	private mxICell getPossibleCellById(String id, mxICell parent) {
		if (parent != null) {
			if (parent.getId() != null) {
				if (parent.getId().equals(id)) {
					return parent;
				}
			}
		}
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);
			mxICell cell = getPossibleCellById(id, child);
			if (cell != null) {
				return cell;
			}
		}
		return null;
	}

	private mxICell getCellById(String id, mxICell parent) {
		if (parent != null && parent.getId() != null) {
			if (parent instanceof ComplexNode) {
				ComplexNode node = (ComplexNode) parent;
				if (node.isClass()) {
					Object obj = node.getUserObject();
					if (obj instanceof ObjectMark) {
						ObjectMark mark = (ObjectMark) obj;
						if (mark.getId().equals(id)) {
							return node;
						}
					}
				}

			}

		}
		int count = parent.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = parent.getChildAt(i);
			mxICell cell = getCellById(id, child);
			if (cell != null) {
				return cell;
			}
		}
		return null;
	}

	public void addSubSheet(BeeModel model) {
		this.subSheet.add(model);
	}

	public List<BeeModel> getSubSheets() {
		return this.subSheet;
	}

	@Override
	public String getTitleLabel() {
		return name;
	}

	@Override
	public void setTitleLabel(String title) {
		this.name = title;
		for (TitleChangeListener listener : titleChangeListeners) {
			listener.change(this);
		}
	}

	@Override
	public void addTitleChangeListener(TitleChangeListener listener) {
		this.titleChangeListeners.add(listener);
	}

	public String toString() {
		return this.name;
	}

	@Override
	public void setTitleWithOutListenerAction(String title) {
		this.name = title;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		setTitleLabel(name);
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
	public void setOwener(BObject object) {
		this.owner = object;
	}

	@Override
	public BObject getOwener() {
		return this.owner;
	}

	@Override
	public List<BAssignment> getVariables() {
		List<BAssignment> list = new ArrayList<BAssignment>();
		List<BObject> cells = this.getCellList(AssignmentNode.class.getName());
		for (BObject obj : cells) {
			list.add((BAssignment) obj);
		}
		return list;
	}

	@Override
	public List<BMethod> getMethods() {
		List<BMethod> list = new ArrayList<BMethod>();
		List<BObject> cells = this.getCellList(MethodNode.class.getName());
		for (BObject obj : cells) {
			list.add((BMethod) obj);
		}
		return list;
	}

	@Override
	public BEditor getSheet(BProject project) {
		BeeGraphSheet sheet = new BeeGraphSheet(project);
		sheet.getGraph().setModel(this);
		addListener(mxEvent.UNDO, sheet.getUndoHandler());

		sheet.getGraph().setDefaultParent(findRoot(this.getRoot()));
		return sheet;
	}

	private Object findRoot(Object obj) {

		if (obj instanceof mxICell) {
			mxICell cell = (mxICell) obj;
			int count = cell.getChildCount();
			if (count == 0) {
				return cell;
			}

			mxICell child = cell.getChildAt(0);
			if (child instanceof BasicNode) {
				return cell;
			} else {
				return findRoot(child);
			}

		}
		return obj;
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
		List<BLogicBody> list = new ArrayList<BLogicBody>();
		List<BObject> cells = this.getCellList(BlockUnitNode.class.getName());
		for (BObject obj : cells) {
			list.add((BLogicBody) obj);
		}
		return list;
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

	}

	@Override
	public List<BConstructor> getConstructors() {
		List<BConstructor> list = new ArrayList<BConstructor>();
		List<BObject> cells = this.getCellList(ConstructorNode.class.getName());
		for (BObject obj : cells) {
			list.add((BConstructor) obj);
		}
		return list;
	}

	private List<BObject> getCellList(String className) {
		List<BObject> list = new ArrayList<BObject>();
		BeeModel m = (BeeModel) this.cloneAll();
		mxCell root = (mxCell) m.getRoot();
		mxCell fond = (mxCell) foundRoot(root);
		if (fond != null) {
			root = fond;
		}

		this.lookupCell(className, list, root);

		List<BeeModel> subs = this.getSubSheets();
		for (BeeModel model : subs) {
			model = (BeeModel) model.cloneAll();
			mxCell child = (mxCell) foundRoot((mxICell) model.getRoot());
			if (child == null) {
				continue;
			}
			this.lookupCell(className, list, child);
		}
		return list;

	}

	private void lookupCell(String className, List<BObject> list, mxCell cell) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);
			if (child.getClass().getName().equals(className)) {
				BObject unit = (BObject) child;
				unit = (BObject) unit.cloneAll();
				list.add(unit);
			}
		}
	}

	private static mxICell foundRoot(mxICell node) {
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell obj = node.getChildAt(i);
			if (obj instanceof BasicNode) {
				return node;
			} else {
				mxICell root = foundRoot(obj);
				if (root != null) {
					return root;
				}
			}
		}

		return null;
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
		return this.inter;
	}

	@Override
	public void setInterface(boolean inter) {
		this.inter = inter;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(this.mod);

	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(this.mod);
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
	public void setModifier(int mode) {
		this.mod = mode;
	}

	@Override
	public int getModifier() {
		return this.mod;
	}

	@Override
	public void addAnnotation(BAnnotation annotation) {

		for (BAnnotation anno : annotations) {
			if (anno.getLogicName().equals(annotation.getLogicName())) {
				annotations.remove(anno);
				break;
			}
		}
		annotations.add(annotation);
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
		ArrayList<BType> list = new ArrayList<BType>();
		list.addAll(this.parameterizedTypeNames);
		return list;
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

	private boolean data = false;

	@Override
	public void setData(boolean data) {
		this.data = data;
	}

	@Override
	public boolean isData() {
		if (data) {
			return true;
		}
		List<BAnnotation> annos = this.getAnnotations();
		for (BAnnotation anno : annos) {
			if (anno.getBClass().getQualifiedName().equals(Data.class.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setLogic(boolean logic) {

	}

	@Override
	public boolean isLogic() {
		return true;
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
	public List<TitleChangeListener> getTitleChangeListeners() {
		return this.titleChangeListeners;
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
		if (this.parentView != null) {
			return true;
		}
		return this.logicName.indexOf("$") > 0;
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
		mxICell root = ((mxICell) this.getRoot()).getChildAt(0);
		root.insert((mxICell) var);
	}

	@Override
	public void removeVar(BAssignment var) {
		mxICell root = ((mxICell) this.getRoot()).getChildAt(0);
		int count = root.getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			mxICell child = root.getChildAt(i);
			if (child instanceof BAssignment) {
				BAssignment a = (BAssignment) child;
				if (a.getLeft() != null) {
					if (a.getLeft().getLogicName() != null) {
						if (a.getLeft().getLogicName().equals(var.getLeft().getLogicName())) {
							child.removeFromParent();
							return;
						}
					}
				}
			}
		}

	}

	@Override
	public void addVar(int index, BAssignment var) {
		mxICell root = ((mxICell) this.getRoot()).getChildAt(0);
		root.insert((mxICell) var, index);

	}

	@Override
	public void removeMethod(BMethod method) {

		mxCell root = (mxCell) this.getRoot();
		mxCell fond = (mxCell) foundRoot(root);
		if (fond != null) {
			root = fond;
		}

		if (!this.removeMethod(method, root)) {

			List<BeeModel> subs = this.getSubSheets();
			for (BeeModel model : subs) {
				model = (BeeModel) model.cloneAll();
				mxCell child = (mxCell) foundRoot((mxICell) model.getRoot());
				if (child == null) {
					continue;
				}
				if (this.removeMethod(method, child)) {
					break;
				}
			}
		}
	}

	private boolean removeMethod(BMethod method, mxCell cell) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = cell.getChildAt(i);
			if (child instanceof BMethod) {
				mxICell m = (mxICell) method;
				if (child.getId().equals(m.getId())) {
					child.removeFromParent();
					return true;
				}
			}
		}
		return false;
	}
}
