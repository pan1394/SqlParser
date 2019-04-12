package com.linkstec.bee.UI.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.BasicNodeHelper;
import com.linkstec.bee.UI.node.view.InvokerParametersNode;
import com.linkstec.bee.UI.node.view.InvokerReturnLinkNode;
import com.linkstec.bee.UI.node.view.LabelNode;
import com.linkstec.bee.UI.node.view.LinkNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.logic.BeeGraph;
import com.linkstec.bee.UI.spective.detail.logic.VerifyHelper;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.encode.JavaGenUnit;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

/**
 * 
 * 3 patterns:method invoker,data getter,data setter when,the it is data,the the
 * child will be BVariable
 *
 */
public class ReferNode extends BasicNode implements Serializable, BInvoker, IUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3000247351621748377L;

	private String titleBID, nameAreaBID, parentBID, childBID, parametersAreaBID, returnAreaBID, parametersBID,
			returnValueBID, returnMarkBID, numberBID;
	// assignTypeBID=null?is not data:is data
	private String assignTypeBID, assignTitleBID, assignValueBID, anonymousAeaBID, anonymouseBodyBID;

	private String classNameLabelBID, methodNameLabelBID, returnNameLabelBID, spliterBID, throwsAreaBID;
	// excepts title and splitter
	private String bodyBID;
	private boolean isData = false;

	private boolean innerCall = false;
	private String label;
	private boolean isStatic = false;
	private String dataMethodName = null;
	private boolean linker = false;

	private String assignTitleRowBID;

	private String titelAreaBID;

	public ReferNode() {

		this.getGeometry().setWidth(600);
		this.setFixedWidth(600);
		VerticalLayout all = new VerticalLayout();
		this.setLayout(all);
		all.setBetweenSpacing(0);
		all.setSpacing(0);

		this.makeTitleArea();
		this.makeBody();
	}

	public boolean isLinker() {
		return linker;
	}

	public void setLinker(boolean linker) {
		this.linker = linker;
	}

	@Override
	public BClass getBClass() {
		if (this.getCast() != null) {
			return this.getCast().getBClass();
		}
		if (this.getInvokeChild() != null) {
			return this.getInvokeChild().getBClass();
		}
		return null;
	}

	@Override
	public void afterRemoved(BeeGraph graph) {
		if (this.isLinker()) {
			BasicNode node = this.getLinker();
			LinkNode n = BasicNodeHelper.findLinkNode(node, this);
			if (n != null) {
				n.removeFromParent();
			}
		}
	}

	private boolean folded = false;

	@Override
	public EditAction getAction() {
		if (this.isLinker()) {
			BasicNode body = (BasicNode) this.getCellByBID(bodyBID);
			MixAction action = new MixAction();
			String name = "折りたたむ";
			if (folded) {
				name = "展開";
			}
			action.addAction(name, new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 4280496236064343431L;

				@Override
				public void call() {
					body.setVisible(folded);
					if (folded) {
						folded = false;
					} else {
						folded = true;
					}
				}

			});
			return action;
		}

		return null;
	}

	public BasicNode getNameArea() {
		return (BasicNode) this.getCellByBID(nameAreaBID);
	}

	public BasicNode getParameterArea() {
		return (BasicNode) this.getCellByBID(parametersAreaBID);
	}

	public BasicNode getTitle() {
		return (BasicNode) this.getCellByBID(this.titleBID);
	}

	private void makeBody() {
		BasicNode bar = new BasicNode();
		bar.setOpaque(false);
		bar.setFixedWidth(600);

		VerticalLayout v = new VerticalLayout();
		bar.setLayout(v);
		v.setBetweenSpacing(0);
		v.setSpacing(0);

		this.bodyBID = bar.getId();
		this.getLayout().addNode(bar);

		this.makeNameArea();
		this.makeReturnArea();
	}

	private void addBodyUnit(BasicNode node) {
		BasicNode body = (BasicNode) this.getCellByBID(bodyBID);
		body.getLayout().addNode(node);
	}

	private void addBodyUnit(BasicNode node, int index) {
		BasicNode body = (BasicNode) this.getCellByBID(bodyBID);
		body.getLayout().addNode(node, index);
	}

	public void makeTitleArea() {
		BasicNode bar = new BasicNode();
		this.titelAreaBID = bar.getId();
		bar.setOpaque(false);
		bar.setFixedWidth(600);
		HorizonalLayout layout = new HorizonalLayout();
		bar.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);

		BasicNode numberLabel = new BasicNode();
		numberLabel.setOpaque(false);
		this.numberBID = numberLabel.getId();
		layout.addNode(numberLabel);

		InvokerReturnLinkNode returnMark = new InvokerReturnLinkNode();
		returnMark.setOpaque(false);
		this.returnMarkBID = returnMark.getId();
		layout.addNode(returnMark);

		LabelNode title = new LabelNode();
		title.setOpaque(false);
		this.titleBID = title.getId();
		layout.addNode(title);

		this.getLayout().addNode(bar);
	}

	public String getDataMethodName() {
		return dataMethodName;
	}

	public void setDataMethodName(String dataMethodName) {
		this.dataMethodName = dataMethodName;
	}

	@Override
	public NodeNumber getNumber() {
		mxICell cell = this.getCellByBID(this.numberBID);
		if (cell != null) {
			return (NodeNumber) cell.getValue();
		}
		return null;
	}

	@Override
	public void setNumber(NodeNumber number) {
		this.getCellByBID(this.numberBID).setValue(number);
	}

	public void makeNameArea() {
		BasicNode title = new BasicNode();
		HorizonalLayout layout = new HorizonalLayout();
		title.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);
		layout.setContainerPack(true);
		layout.setHeightSame(true);
		this.nameAreaBID = title.getId();
		addBodyUnit(title);

		this.makeParentLabel();
		this.makeChildLabel();
	}

	private BasicNode getThrowsArea() {
		if (this.throwsAreaBID == null) {
			BasicNode area = new BasicNode();
			VerticalLayout layout = new VerticalLayout();
			layout.setBetweenSpacing(0);
			layout.setSpacing(0);
			area.setLayout(layout);
			this.throwsAreaBID = area.getId();

			mxCell returnArea = this.getCellByBID(returnAreaBID);
			int index = returnArea.getParent().getIndex(returnArea);
			returnArea.getParent().insert(area, index);
		}
		return (BasicNode) this.getCellByBID(throwsAreaBID);
	}

	private void addThrows(BVariable var) {
		BasicNode row = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8085525808304097853L;

			@Override
			public EditAction getAction() {

				ReferNode find = null;
				mxICell parent = this.getParent();
				while (parent != null) {
					if (parent instanceof ReferNode) {
						find = (ReferNode) parent;
						break;
					} else {
						parent = parent.getParent();
					}
				}
				if (find == null) {
					return null;
				}

				final ReferNode real = find;
				boolean catched = VerifyHelper.errorCatched(var, real, Application.getInstance().getCurrentProject());
				if (catched) {
					return null;
				}
				MixAction action = new MixAction();
				action.addAction("本エラーをこの場で処理する", new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 194101446546045965L;

					@Override
					public void call() {
						BLogicUnit unit = VerifyHelper.findUnit(real, real);
						TryNode tn = new TryNode();
						mxICell parent = ((mxICell) unit).getParent();
						int index = parent.getIndex((mxICell) unit);
						parent.insert(tn, index);

						CatchNode canode = new CatchNode();
						ParameterNode para = new ParameterNode();
						para.setBClass(var.getBClass());
						para.setLogicName(var.getLogicName());
						canode.setVariable(para);

						tn.clearCatches();
						tn.addCatch(canode);

						tn.getTryEditor().addUnit(unit);
						LayoutUtils.makeNumber(VerifyHelper.findRoot(real), null);
					}

				});
				action.addAction("本エラーを上へ投げる", new BCall() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 399429177579897710L;

					@Override
					public void call() {
						BMethod method = VerifyHelper.findMethod(real);
						ParameterNode para = new ParameterNode();
						para.setBClass(var.getBClass());
						para.setLogicName(var.getLogicName());
						method.addThrows(para);
					}

				});
				return action;
			}

		};
		HorizonalLayout layout = new HorizonalLayout();
		row.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);

		BasicNode title = new BasicNode();
		title.setTitled();
		title.setFixedWidth(50);
		title.setValue("投げエラー");
		layout.addNode(title);

		LabelNode node = new LabelNode();
		node.setValue(var);
		node.setFixedWidth(550);
		node.makeBorder();
		layout.addNode(node);

		BasicNode area = this.getThrowsArea();
		area.getLayout().addNode(row);
	}

	private void makeReturnArea() {
		BasicNode area = new BasicNode();
		HorizonalLayout layout = new HorizonalLayout();
		area.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);

		this.returnAreaBID = area.getId();
		addBodyUnit(area);

		BasicNode returnTitle = new BasicNode();
		HorizonalLayout l = new HorizonalLayout();
		l.setBetweenSpacing(0);
		l.setSpacing(0);
		returnTitle.setLayout(l);
		returnTitle.setTitled();
		this.returnNameLabelBID = returnTitle.getId();
		returnTitle.setFixedWidth(50);

		LabelNode returnLabel = new LabelNode();
		returnLabel.setValue("戻り値");
		returnLabel.setOpaque(false);
		l.addNode(returnLabel);

		layout.addNode(returnTitle);

		LabelNode node = new LabelNode();
		this.returnValueBID = node.getId();
		node.setFixedWidth(550);
		node.makeBorder();
		layout.addNode(node);

	}

	public void setReturnMark(BasicNode linker) {
		InvokerReturnLinkNode node = (InvokerReturnLinkNode) this.getCellByBID(returnMarkBID);
		if (node != null) {
			node.setLinkNode(linker);
			BValuable parent = this.getInvokeParent();
			if (parent != null) {
				BValuable child = this.getInvokeChild();
				if (child != null && child instanceof BMethod) {
					BMethod method = (BMethod) child;
					if (method instanceof BConstructor) {
						this.getTitle().setValue("のため、インスタンスを作っておく");
					} else {
						this.getTitle().setValue("のため、" + parent.toString() + "#" + method.getName() + "で戻り値の値を作っておく");
					}
				}
			}
		}
	}

	public BasicNode getLinker() {
		InvokerReturnLinkNode node = (InvokerReturnLinkNode) this.getCellByBID(returnMarkBID);
		if (node != null) {
			return node.getLinkNode();
		}
		return null;
	}

	public void makeParameterArea() {
		if (this.parametersAreaBID == null) {
			BasicNode body = new BasicNode();
			body.setFixedWidth(600);
			body.makeBorder();
			HorizonalLayout layout = new HorizonalLayout();
			body.setLayout(layout);
			layout.setSpacing(20);
			layout.setBetweenSpacing(0);
			this.parametersAreaBID = body.getId();
			addBodyUnit(body, 2);
		}

	}

	private void makeAssignTitle() {
		BasicNode row = new BasicNode();
		this.assignTitleRowBID = row.getId();
		HorizonalLayout layout = new HorizonalLayout();
		layout.setSpacing(0);
		row.setLayout(layout);

		LabelNode typeLable = new LabelNode();
		typeLable.setValue("対象データ");
		typeLable.setFixedWidth(200);
		typeLable.setTitled();
		layout.addNode(typeLable);

		LabelNode titleLable = new LabelNode();
		titleLable.setValue("変数");
		layout.addNode(titleLable);
		titleLable.setFixedWidth(200);
		titleLable.setTitled();

		LabelNode valueLable = new LabelNode();
		valueLable.setValue("値");
		layout.addNode(valueLable);
		valueLable.setFixedWidth(200);
		valueLable.setTitled();

		this.getLayout().addNode(row);
	}

	private void makeAssignValue() {
		if (assignTypeBID == null) {
			BasicNode row = new BasicNode();
			HorizonalLayout layout = new HorizonalLayout();
			layout.setSpacing(0);
			row.setLayout(layout);

			ComplexNode type = new ComplexNode();
			type.setFixedWidth(200);
			this.assignTypeBID = type.getId();
			layout.addNode(type);
			type.makeBorder();

			ComplexNode mxTitle = new ComplexNode();
			mxTitle.setFixedWidth(200);
			this.assignTitleBID = mxTitle.getId();
			layout.addNode(mxTitle);
			mxTitle.makeBorder();

			ComplexNode mxParaValue = new ComplexNode();
			this.assignValueBID = mxParaValue.getId();
			mxParaValue.setFixedWidth(200);
			layout.addNode(mxParaValue);
			mxParaValue.makeBorder();

			this.getLayout().addNode(row);
		}
	}

	private void makeChildLabel() {
		if (this.methodNameLabelBID == null) {
			LabelNode label = new LabelNode();
			label.setTitled();

			methodNameLabelBID = label.getId();
			label.setValue("メソッド名");
			label.setFixedWidth(50);
			this.getNameArea().getLayout().addNode(label);
		}

		if (this.childBID == null) {
			ObjectNode mxChild = new ObjectNode();
			mxChild.setFixedWidth(250);
			mxChild.makeBorder();
			this.getNameArea().getLayout().addNode(mxChild);
			this.childBID = mxChild.getId();
		}
	}

	private void makeParentLabel() {

		if (this.classNameLabelBID == null) {
			LabelNode beforeParent = new LabelNode();
			beforeParent.setValue("クラス名");
			beforeParent.setTitled();
			beforeParent.addUserAttribute("fixedWidth", new Double(50));
			this.classNameLabelBID = beforeParent.getId();
			this.getNameArea().getLayout().addNode(beforeParent);
		}

		ObjectNode mxParent = new ObjectNode();
		mxParent.setValue(parent);
		this.getNameArea().getLayout().addNode(mxParent);
		mxParent.setFixedWidth(250);
		mxParent.makeBorder();
		this.parentBID = mxParent.getId();
	}

	public String toString() {
		if (this.isLinker()) {

			if (this.getNumber() != null) {
				return this.getNumber().toString();
			}
		}
		BValuable parent = this.getInvokeParent();
		// super(),this();
		if (parent == null) {
			return this.getInvokeChild().toString();
		}

		return parent.toString() + "#" + this.getInvokeChild().toString();
	}

	public Object getValue() {
		return super.getValue();
	}

	@Override
	public BValuable getInvokeChild() {
		if (this.assignValueBID == null) {
			return (BValuable) LayoutUtils.getValueNode(this, this.childBID);
		} else {
			return (BValuable) LayoutUtils.getValueNode(this, this.assignTitleBID);
		}

	}

	@Override
	public void setInvokeChild(BValuable child) {
		this.getCellByBID(childBID).setValue(child);

		// return value
		LabelNode node = (LabelNode) this.getCellByBID(returnValueBID);
		if (child != null && child instanceof BMethod) {
			BMethod method = (BMethod) child;
			BValuable value = method.getReturn();
			BClass bclass = null;
			if (value != null) {
				bclass = value.getBClass();
			}
			if (bclass != null && !bclass.getQualifiedName().equals(BClass.VOID)) {
				if (bclass.isArray()) {
					BType type = bclass.getArrayPressentClass();
					node.setValue(type.getLogicName() + "[]");
				} else {
					BType type = value.getParameterizedTypeValue();

					List<BType> types = CodecUtils.getTypeValues(type);
					if (types.size() > 0) {
						String s = JavaGenUnit.getTypeSource(Application.getInstance().getCurrentProject(),
								CodecUtils.BObject(), value);
						node.setValue(s);
					} else {
						node.setValue(bclass.getLogicName());
					}
				}

			} else {
				node.setValue("-");
			}
			List<BVariable> ths = method.getThrows();
			for (BVariable var : ths) {
				this.addThrows(var);
			}
			BObject parent = this.getInvokeParent();
			if (parent != null) {
				this.getTitle().setValue(parent.toString() + "#" + method.getName() + "を呼び出す");
			}
			if (child instanceof BConstructor) {
				BConstructor con = (BConstructor) child;
				BClass bbclass = con.getBody();
				if (bbclass != null) {
					this.makeAnonymousBodyLink(bbclass);
				}
			}
		} else if (child != null && child instanceof BVariable) {
			// do nothing because it is setter,then change view at adding parameter,
			// if it is getter ,it will called by others and at that time uses toString
		}

	}

	public void makeAnonymousBodyLink(BClass bclass) {
		BasicNode area = new BasicNode();
		HorizonalLayout layout = new HorizonalLayout();
		area.setLayout(layout);
		layout.setSpacing(0);
		layout.setBetweenSpacing(0);

		this.anonymousAeaBID = area.getId();
		addBodyUnit(area);

		LabelNode nameLabel = new LabelNode();
		nameLabel.setValue("クラス");
		nameLabel.setOpaque(false);
		nameLabel.setTitled();
		nameLabel.setFixedWidth(50);
		layout.addNode(nameLabel);

		ObjectNode node = new ObjectNode();
		this.anonymouseBodyBID = node.getId();
		node.setFixedWidth(550);
		node.makeBorder();
		node.setValue(bclass);
		layout.addNode(node);

	}

	private InvokerParametersNode getParametersNode() {

		if (parametersBID == null || this.getCellByBID(this.parametersBID) == null) {
			InvokerParametersNode parameters = new InvokerParametersNode();
			parametersBID = parameters.getId();
			BasicNode node = this.getParameterArea();
			if (node != null) {
				node.getLayout().addNode(parameters);
			}
			return parameters;
		}
		return (InvokerParametersNode) this.getCellByBID(this.parametersBID);
	}

	@Override
	public List<BValuable> getParameters() {
		if (this.assignTitleBID == null) {
			InvokerParametersNode paramters = this.getParametersNode();
			if (paramters == null) {
				new ArrayList<BValuable>();
			}
			return paramters.getParameters();
		} else {
			List<BValuable> list = new ArrayList<BValuable>();
			list.add(LayoutUtils.getValueNode(this, assignValueBID));

			return list;
		}
	}

	@Override
	public void addParameter(BValuable parameter) {

		BObject child = this.getInvokeChild();
		if (child instanceof BMethod) {

			this.makeParameterArea();
			InvokerParametersNode parameters = this.getParametersNode();

			BMethod method = (BMethod) child;
			List<BParameter> list = method.getParameter();
			List<BValuable> paras = parameters.getParameters();
			BParameter var;
			if (paras.size() > list.size() - 1) {
				// TODO make the parameter is args to be
				// validateFields(String screenId, String... fields)
				if (list.size() == 0) {
					Debug.a();
				}

				var = list.get(list.size() - 1);

			} else {
				var = list.get(paras.size());
			}

			if (var.isVarArgs()) {
				if (var.getBClass().isArray()) {
					var.getBClass().setArrayPressentClass(null);
				}
			}

			BasicNode b = (BasicNode) parameter;
			if (b instanceof ReferNode) {
				ReferNode referNode = (ReferNode) b;
				BasicNode node = LayoutUtils.makeInvokerChild(referNode, this, 0, this, false);

				if (node == null) {
					parameters.addParameter(var, (BasicNode) parameter);
				} else {
					parameters.addParameter(var, node);
					node.makeBorder();
				}
			} else {
				parameters.addParameter(var, (BasicNode) parameter);
			}
		} else if (child instanceof BVariable) {
			BVariable var = (BVariable) child;
			BasicNode parent = (BasicNode) this.getInvokeParent();
			if (this.assignTitleBID == null) {
				this.changeInvokeToAssign();
			}
			ComplexNode type = (ComplexNode) this.getCellByBID(assignTypeBID);
			ComplexNode title = (ComplexNode) this.getCellByBID(assignTitleBID);
			BasicNode value = (BasicNode) this.getCellByBID(assignValueBID);
			type.replace(parent);
			parent.makeBorder();

			if (!(child instanceof mxICell)) {
				child = BasicGenUtils.toView(var);
			}

			BasicNode bchild = (BasicNode) child;
			title.replace(bchild);
			bchild.makeBorder();

			BasicNode b = (BasicNode) parameter;
			if (b instanceof ReferNode) {
				ReferNode referNode = (ReferNode) b;
				BasicNode node = LayoutUtils.makeInvokerChild(referNode, this, 0, this, false);
				if (node != null) {
					value.replace(node);
					node.makeBorder();
				} else {
					value.replace(b);
				}
			} else {
				value.replace(b);
			}
			this.getCellByBID(this.titleBID).setValue("以下の通りに値を設定する");
		}
	}

	private void changeInvokeToAssign() {
		this.getCellByBID(this.bodyBID).removeFromParent();
		this.makeAssignTitle();
		this.makeAssignValue();
		expression = true;
	}

	private boolean expression = false;

	public boolean isAssign() {
		return expression;
	}

	// for continuous display
	public void removeAssignTitles() {
		if (this.assignTitleRowBID == null) {
			return;
		}
		mxICell cell = this.getCellByBID(assignTitleRowBID);
		if (cell != null) {
			cell.removeFromParent();
		}
		cell = this.getCellByBID(this.titelAreaBID);
		if (cell != null) {
			cell.removeFromParent();
		}
		this.setNextToLast(true);
	}

	public boolean isData() {
		return isData;
	}

	public void setData(boolean isData, boolean setter) {
		this.isData = isData;
	}

	@Override
	public void setInvokeParent(BValuable parent) {
		BasicNode node = (BasicNode) this.getCellByBID(parentBID);
		if (node == null) {
			node = (BasicNode) this.getCellByBID(this.assignTypeBID);
		}
		// this(),super()
		if (parent == null) {
			node.setValue("-");
			return;
		}
		if (parent instanceof ReferNode) {
			ReferNode r = (ReferNode) parent;
			BasicNode b = LayoutUtils.makeInvokerChild(r, this, 0, this, false);
			if (b != null) {
				node.replace(b);
				b.makeBorder();
			} else {
				node.replace(r);
				// node.setValue(parent);
			}
		} else {
			node.replace((mxCell) parent);
			// node.setValue(parent);
		}
	}

	@Override
	public BValuable getInvokeParent() {
		if (assignTitleBID == null) {
			return (BValuable) LayoutUtils.getValueNode(this, parentBID);
		} else {
			return (BValuable) LayoutUtils.getValueNode(this, assignTypeBID);
		}

	}

	@Override
	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public void setStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}

	@Override
	public String getNodeDesc() {
		return "処理呼び出し";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;

	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public void onAddSheet(BeeGraphSheet sheet) {
		if (this.getInvokeParent() != null && this.getInvokeParent() instanceof BVariable) {
			BVariable var = (BVariable) this.getInvokeParent();
			BClass bclass = var.getBClass();
			if (bclass != null && bclass.getQualifiedName().equals(sheet.getModel().getQualifiedName())) {
				var.setLogicName("this");
				var.setName("当該シート");
			}
		}
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_TITLE_INVOKE_ICON;
	}

	@Override
	public void setInnerClassCall(boolean innerCall) {
		this.innerCall = innerCall;
	}

	@Override
	public boolean isInnerClassCall() {
		return this.innerCall;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		VerifyHelper.Verify(this, sheet, project);

	}

	@Override
	public boolean isDropTarget(BasicNode node) {
		return false;
	}

	public void addSpliter() {
		if (this.spliterBID == null) {
			com.linkstec.bee.UI.node.view.Helper.Spliter node = new com.linkstec.bee.UI.node.view.Helper.Spliter();
			this.spliterBID = node.getId();
			this.getLayout().addNode(node);
			this.setLinker(true);
			BasicNode body = (BasicNode) this.getCellByBID(this.bodyBID);
			if (body == null) {
				return;
			}
			body.setOpaque(true);
		}
	}

	private BValuable cast;

	@Override
	public void setCast(BValuable cast) {
		this.cast = cast;

	}

	@Override
	public BValuable getCast() {
		return this.cast;
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.getInvokeChild().setArrayIndex(object);
	}

	@Override
	public BValuable getArrayObject() {
		return this.getInvokeChild().getArrayObject();
	}

	private BValuable arrayIndex;

	@Override
	public BValuable getArrayIndex() {
		return this.arrayIndex;
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.arrayIndex = index;
	}

	@Override
	public BType getParameterizedTypeValue() {
		if (this.getCast() != null) {
			return this.getCast().getParameterizedTypeValue();
		}
		if (this.getInvokeChild() != null) {
			return this.getInvokeChild().getParameterizedTypeValue();
		}
		return null;
	}

	@Override
	public void makeDefualtValue(Object target) {
		BValuable child = this.getInvokeChild();
		if (child instanceof BMethod) {
			BMethod method = (BMethod) child;
			List<BParameter> parameters = method.getParameter();
			for (BParameter parameter : parameters) {
				BClass bclass = parameter.getBClass();

				if (child instanceof BConstructor) {
					BClass b = this.getBClass();
					if (b.getQualifiedName().equals(bclass.getQualifiedName())) {
						bclass = CodecUtils.BNull.cloneAll();
					}
				}

				BValuable value = ComplexNode.makeDefaultValue(bclass);
				this.addParameter(value);
			}
		}
	}

	@Override
	public void clearParameters() {
		InvokerParametersNode parameters = this.getParametersNode();
		if (parameters != null) {
			parameters.clearParameters();
		}

	}
}
