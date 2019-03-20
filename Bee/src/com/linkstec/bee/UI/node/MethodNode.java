package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.DeleteAction;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.ILayout;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.Connector;
import com.linkstec.bee.UI.node.view.IClassMember;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.ModifierNode;
import com.linkstec.bee.UI.node.view.ParametersNode;
import com.linkstec.bee.UI.node.view.ThrowsNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.util.BeeName;
import com.linkstec.bee.core.codec.util.BeeNamingUtil;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class MethodNode extends BasicNode implements Serializable, IClassMember, ILogic, IUnit, BMethod {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String titleRowBID, inConnectorBID, headerBID, EditorBID, titleBID, logicTitleBID, parameterBID, numberBID,
			titleBarBID, modifierBID, throwsBID, returnValueBID, returnAreaBID;

	private String declearedParentName;

	private List<BType> definedTypes = new ArrayList<BType>();

	private int mods = 0;

	private String label;

	@Override
	public void addAnnotation(BAnnotation annotation) {
		this.getLayout().addNode((BasicNode) annotation, 0);

	}

	@Override
	public List<BAnnotation> getAnnotations() {
		int count = this.getChildCount();
		List<BAnnotation> list = new ArrayList<BAnnotation>();
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj instanceof BAnnotation) {
				list.add((BAnnotation) obj);
			}
		}
		return list;
	}

	public Connector getInConnector() {
		return (Connector) this.getCellByBID(this.inConnectorBID);
	}

	public BasicNode getTitle() {
		return (BasicNode) this.getCellByBID(this.titleBID);
	}

	public MethodNode() {
		mxGeometry SEGMENT_DEFAULT_SIZE = new mxGeometry(0, 0, BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH,
				BeeConstants.SEGMENT_EDITOR_DEFAULT_HEIGHT + BeeConstants.LINE_HEIGHT);
		this.setGeometry(SEGMENT_DEFAULT_SIZE);
		VerticalLayout layout = new VerticalLayout();
		layout.setMaxWidth(BeeConstants.SEGMENT_MAX_WIDTH);
		this.setLayout(layout);
		layout.setChildFitWidth(true);
		layout.setSpacing(0);
		// for the display when folded
		this.addStyle("spacingLeft=70");
		this.addStyle("textOpacity=0");
		this.setOpaque(false);

		MethodHeader header = new MethodHeader();
		layout.addNode(header);
		this.headerBID = header.getId();

		ModifierNode modifier = new ModifierNode(this);
		this.modifierBID = modifier.getId();

		// title
		this.makeTitle(header.getLayout());

		// header connector
		Connector mxInConnector = new Connector();
		header.insert(mxInConnector);
		this.inConnectorBID = mxInConnector.getId();

		// body
		BLockNode mxEditor = new BLockNode();
		mxEditor.getGeometry().setWidth(BeeConstants.SEGMENT_MAX_WIDTH);
		layout.addNode(mxEditor);
		this.EditorBID = mxEditor.getId();

	}

	public void setDeclearedParentName(String name) {
		this.declearedParentName = name;
	}

	public String getDeclearedParentName() {
		return this.declearedParentName;
	}

	private void makeParameterArea() {
		if (this.parameterBID == null) {
			ParametersNode paratemeters = new ParametersNode();

			if (this.returnAreaBID == null) {
				this.getHeader().getLayout().addNode(paratemeters);
			} else {
				mxICell cell = this.getCellByBID(returnAreaBID);
				int index = cell.getParent().getIndex(cell);
				this.getHeader().getLayout().addNode(paratemeters, index);
			}
			this.parameterBID = paratemeters.getId();
		}
	}

	private MethodHeader getHeader() {
		return (MethodHeader) this.getCellByBID(headerBID);
	}

	private void makeThrows(ILayout titleLayout) {
		if (throwsBID == null) {
			BasicNode row = new BasicNode();
			mxGeometry geo = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
			row.setGeometry(geo);
			VerticalLayout layout = new VerticalLayout();
			layout.setSpacing(0);
			layout.setBetweenSpacing(0);
			row.setLayout(layout);
			this.throwsBID = row.getId();
			titleLayout.addNode(row);
			row.setVisible(false);
		}
	}

	private void makeTitle(ILayout titleLayout) {
		// title row
		BasicNode titleRow = new BasicNode();
		titleRowBID = titleRow.getId();
		mxGeometry titleLabel = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
		titleRow.setGeometry(titleLabel);
		titleRow.setTitled();
		HorizonalLayout rowLayout = new HorizonalLayout();
		titleRow.setLayout(rowLayout);
		titleLayout.addNode(titleRow);

		// title row-number
		LabelNode numberLabel = new LabelNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		rowLayout.addNode(numberLabel);

		// title row-title
		LabelNode mxTitleLable = new LabelNode();
		mxTitleLable.setOpaque(false);
		mxTitleLable.setEditable(true);
		mxTitleLable.setTextOnly(true);
		this.titleBID = mxTitleLable.getId();
		rowLayout.addNode(mxTitleLable);

		// logic title row
		BasicNode logictitleRow = new BasicNode();
		mxGeometry logictitleLabel = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
		logictitleRow.setGeometry(logictitleLabel);
		logictitleRow.setTitled();
		HorizonalLayout logicrowLayout = new HorizonalLayout();
		logictitleRow.setLayout(logicrowLayout);
		titleLayout.addNode(logictitleRow);

		// logic title row-name
		LabelNode logicnameLabel = new LabelNode();
		logicnameLabel.setOpaque(false);
		logicnameLabel.setValue("物理名");
		logicnameLabel.setFixedWidth(100);
		logicrowLayout.addNode(logicnameLabel);

		// logic title row-value
		LabelNode logicnamevalue = new LabelNode();
		logicnamevalue.setOpaque(false);
		this.logicTitleBID = logicnamevalue.getId();
		logicnamevalue.setEditable(true);
		logicrowLayout.addNode(logicnamevalue);
	}

	private void makeReturnArea() {
		if (returnAreaBID == null) {
			// title row
			BasicNode titleRow = new BasicNode();
			this.returnAreaBID = titleRow.getId();
			mxGeometry titleLabel = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
			titleRow.setGeometry(titleLabel);
			titleRow.setTitled();
			HorizonalLayout rowLayout = new HorizonalLayout();
			titleRow.setLayout(rowLayout);
			this.getHeader().getLayout().addNode(titleRow);

			// title row-name
			LabelNode numberLabel = new LabelNode();
			numberLabel.setOpaque(false);
			numberLabel.setValue("戻り値タイプ");
			numberLabel.setFixedWidth(100);
			rowLayout.addNode(numberLabel);
		}

	}

	@Override
	public NodeNumber getNumber() {
		return (NodeNumber) this.getCellByBID(this.numberBID).getValue();
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	public BLockNode getEditor() {
		return (BLockNode) this.getCellByBID(this.EditorBID);
	}

	private String nodeDesc = "処理ブロック、処理の塊であり、外部公開と内部専用とすることができる。処理するためのパラメータは必要に応じて追加する";

	@Override
	public String getNodeDesc() {
		return nodeDesc;
	}

	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}

	public String toString() {
		NodeNumber number = this.getNumber();
		String s = "";
		if (number != null) {
			s = number.toString();
		}
		if (this.getTitle() == null) {
			return s;
		}
		return s + this.getTitle().toString();
	}

	@Override
	public String getName() {
		return (String) this.getCellByBID(titleBID).getValue();
	}

	@Override
	public String getLogicName() {
		mxCell cell = this.getCellByBID(logicTitleBID);
		if (cell == null) {
			return null;
		}
		return (String) cell.getValue();
	}

	@Override
	public void setLogicName(String name) {
		this.getCellByBID(logicTitleBID).setValue(name);
	}

	@Override
	public void setReturn(BValuable value) {
		this.makeReturnArea();
		BasicNode node = (BasicNode) this.getCellByBID(returnAreaBID);

		TypeNode type = new TypeNode((BVariable) value);
		if (returnValueBID == null) {
			node.getLayout().addNode(type);
			returnValueBID = type.getId();
		} else {
			BasicNode n = (BasicNode) this.getCellByBID(returnValueBID);
			n.replace(type);
		}

	}

	@Override
	public BValuable getReturn() {
		if (returnValueBID != null) {
			TypeNode type = (TypeNode) this.getCellByBID(returnValueBID);
			return type.getObject();
		}
		return null;
	}

	@Override
	public BClass getBClass() {
		if (this.getCast() != null) {
			return this.getCast().getBClass();
		}
		BValuable value = this.getReturn();

		if (value == null) {
			return null;
		}
		BClass bclass = value.getBClass();
		if (bclass == null) {
			return null;
		}

		return value.getBClass();
	}

	@Override
	public void addParameter(BParameter parameter) {
		makeParameterArea();
		ParametersNode node = (ParametersNode) getCellByBID(parameterBID);
		node.addParameter(parameter);
	}

	@Override
	public List<BParameter> getParameter() {
		ParametersNode node = (ParametersNode) this.getCellByBID(this.parameterBID);
		List<BParameter> vars = new ArrayList<BParameter>();
		if (node != null) {
			vars.addAll(node.getParameters());
		}
		return vars;
	}

	@Override
	public void setLogicBody(BLogicBody body) {
		if (body == null) {
			mxCell b = this.getCellByBID(EditorBID);
			if (b != null) {
				b.removeFromParent();
			}
		}
	}

	@Override
	public BLogicBody getLogicBody() {
		return this.getEditor();
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void onAdd(BeeGraphSheet sheet) {
		if (this.getLogicName() == null) {
			BeeName name = BeeNamingUtil.makeName(sheet.getModel(), BeeName.TYPE_METHOD);
			this.setName(name.getName());
			this.setLogicName(name.getLogicName());
		}
		// connectModifier();
	}

	public void connectModifier() {
		ModifierNode modifier = (ModifierNode) this.getCellByBID(modifierBID);
		if (!modifier.isConnected()) {
			modifier.connect(headerBID);
		}
	}

	@Override
	public EditAction getAction() {
		AddAction action = new AddAction();
		action.addAction("パラメータ追加", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1943270290586259577L;

			@Override
			public void call() {
				ParameterNode node = new ParameterNode();

				node.setBClass(CodecUtils.BString());
				List<BParameter> paras = getParameter();
				String logicName = "arg";
				boolean fix = false;
				int i = 0;
				while (!fix) {
					String name = logicName + i;
					i++;
					fix = true;
					for (BParameter b : paras) {
						String l = b.getLogicName();
						if (l != null && name.equals(l)) {
							fix = false;
							break;
						}
					}
					if (fix) {
						logicName = name;
					}
				}
				node.setLogicName(logicName);
				addParameter(node);

				DeleteAction action = new DeleteAction();

				BasicNode row = (BasicNode) node.getParent();
				action.addAction("パラメータ削除", new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8832101807166585333L;

					@Override
					public void call() {

						mxICell parent = row.getParent();
						row.removeFromParent();
						if (parent.getChildCount() == 0) {
							parent.removeFromParent();
							parameterBID = null;
						}
					}

				});
				row.setAction(action);
			}

		});
		if (!(this instanceof BConstructor)) {
			if (this.getReturn() == null) {
				action.addAction("戻り値タイプ設定", new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -1943270290586259577L;

					@Override
					public void call() {
						ComplexNode node = new ComplexNode();
						node.setBClass(CodecUtils.BString().cloneAll());
						setReturn(node);
					}

				});
			}
		}
		action.addAction("投げエラー追加", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8198622764020411489L;

			@Override
			public void call() {
				ComplexNode node = new ComplexNode() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 612713334287819318L;

					@Override
					public EditAction getAction() {
						return null;
					}
				};
				node.setBClass(CodecUtils.BException().cloneAll());
				node.setLogicName(Exception.class.getSimpleName());
				addThrows(node);
			}

		});

		action.addAction("Annotation追加", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4976548980119073155L;

			@Override
			public void call() {
				AnnotationNode anno = new AnnotationNode();
				anno.setBClass(CodecUtils.getClassFromJavaClass(null, Override.class.getName()));
				anno.setLogicName(Override.class.getSimpleName());
				addAnnotation(anno);
			}

		});
		return action;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_METHOD_ICON;
	}

	@Override
	public List<BVariable> getThrows() {
		List<BVariable> vars = new ArrayList<BVariable>();
		BasicNode node = (BasicNode) this.getCellByBID(this.throwsBID);
		if (node == null) {
			return vars;
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			ThrowsNode obj = (ThrowsNode) node.getChildAt(i);
			vars.add(obj.getVariable());
		}
		return vars;
	}

	@Override
	public void addThrows(BVariable exception) {
		this.makeThrows(this.getHeader().getLayout());
		BasicNode node = (BasicNode) this.getCellByBID(this.throwsBID);
		node.setVisible(true);
		ThrowsNode thrs = new ThrowsNode();
		thrs.setVariable(exception);
		node.getLayout().addNode(thrs);
	}

	public class MethodHeader extends BasicNode implements Serializable {// , NodeActions.Add {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7575586980109253848L;

		public MethodHeader() {
			this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
			this.getGeometry().setWidth(BeeConstants.SEGMENT_EDITOR_DEFAULT_WIDTH);

			VerticalLayout headerLayout = new VerticalLayout() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void beforeContainerLayout() {

					beforeLayout(this.getContainer());
				}

			};
			headerLayout.setSpacing(0);
			headerLayout.setBetweenSpacing(0);
			this.setLayout(headerLayout);
		}
	}

	public void beforeLayout(BasicNode node) {
		mxICell parent = node.getParent();
		if (parent != null) {
			if (parent.getChildAt(0).equals(node)) {
				node.setOffsetY(0);
			} else {
				node.setOffsetY(3);
			}
			if (parent instanceof MethodNode) {
				MethodNode m = (MethodNode) parent;
				m.connectModifier();
			}
		}

	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		String logic = this.getLogicName();
		if (logic == null || logic.equals("")) {
			this.setAlert("物理名が設定されていません").setType(BAlert.TYPE_ERROR);
		} else {
			String name = this.getName();
			if (name == null || name.equals("")) {
				this.setAlert("論理名が設定されていません");
			}
		}
	}

	@Override
	public void setCast(BValuable cast) {
		this.getReturn().setCast(cast);

	}

	@Override
	public BValuable getCast() {
		if (this.getReturn() == null) {
			return null;
		}
		return this.getReturn().getCast();
	}

	@Override
	public BValuable getArrayIndex() {
		return this.getReturn().getArrayIndex();
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.getReturn().setArrayIndex(index);
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.getReturn().setArrayObject(object);
	}

	@Override
	public BValuable getArrayObject() {
		if (this.getReturn() == null) {
			Debug.d();
			return null;
		}
		return this.getReturn().getArrayObject();
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.getCast() != null) {
			return this.getCast().getParameterizedTypeValue();
		}
		BValuable value = this.getReturn();
		// super(),this();
		if (value == null) {
			return null;
		}
		BType bclass = value.getParameterizedTypeValue();
		if (bclass == null) {
			return null;
		}

		return value.getParameterizedTypeValue();
	}

	@Override
	public boolean isDropTarget(BasicNode node) {
		return false;
	}

	@Override
	public void addDefinedType(BType type) {
		this.definedTypes.add(type);
	}

	@Override
	public List<BType> getDefinedTypes() {
		return this.definedTypes;
	}

	@Override
	public void makeDefualtValue(Object target) {
		this.mods = Modifier.PUBLIC;
	}

	@Override
	public void setModifier(int mods) {
		ModifierNode modifier = (ModifierNode) this.getCellByBID(modifierBID);
		modifier.setModifier(mods);
		if (Modifier.isAbstract(mods)) {
			this.getEditor().removeFromParent();
		}
	}

	@Override
	public int getModifier() {
		ModifierNode modifier = (ModifierNode) this.getCellByBID(modifierBID);
		return modifier.getModifier();
		// return this.mods;
	}

	@Override
	public void setName(String name) {
		this.getCellByBID(titleBID).setValue(name);
	}

	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell obj = this.getChildAt(i);
			if (obj instanceof BAnnotation) {
				BAnnotation anno = (BAnnotation) obj;
				if (anno.getLogicName().equals(annotion.getLogicName())) {
					obj.removeFromParent();
					break;
				}
			}
		}
	}
}
