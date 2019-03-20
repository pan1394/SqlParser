package com.linkstec.bee.UI.node;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.linkstec.bee.UI.BEditorActions;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.AddAction;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.BCallback;
import com.linkstec.bee.UI.editor.action.ConditionAction;
import com.linkstec.bee.UI.editor.action.DeleteAction;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.node.view.ModifierNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.LabelAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.tip.TipAction;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.BAlert;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BClassHeader;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.BMethodImpl;
import com.mxgraph.model.mxICell;

public class ClassHeaderNode extends BasicNode implements BClassHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = -107327236934864981L;
	private BClass bclass;
	private String modifierBID;
	private String packageBID;

	public ClassHeaderNode() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setLayout(layout);

		AddAction action = new AddAction();
		action.addAction("インターフェース追加", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4976548980119073155L;

			@Override
			public void call() {
				ComplexNode node = new ComplexNode();
				node.setBClass(CodecUtils
						.getClassFromJavaClass(Application.getInstance().getCurrentProject(), Cloneable.class.getName())
						.cloneAll());
				bclass.addInterface(node);
				addInterface(node);
				getLayout().layout();
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
				getLayout().addNode(anno, 0);
				getLayout().layout();

				addAnnotation(anno);
			}

		});

		boolean superAdded = false;
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = getChildAt(i);
			if (child instanceof BasicNode) {
				BasicNode node = (BasicNode) child;
				if (node.getUserAttribute("SUPERCLASS") != null) {
					superAdded = true;
				}
			}
		}

		if (!superAdded) {

			action.addAction("親クラス設定", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 6169909785580003814L;

				@Override
				public void call() {
					ComplexNode node = new ComplexNode();
					BClass ObjectClass = CodecUtils.getClassFromJavaClass(Application.getInstance().getCurrentProject(),
							Object.class.getName());
					node.setBClass(ObjectClass.cloneAll());

					setSuperClass(node);
					bclass.setSuperClass(node);
					getLayout().layout();
				}

			});
		}

		this.setAction(action);
	}

	@Override
	public void setBClass(BClass bclass) {
		this.bclass = bclass;
		List<BAnnotation> annos = bclass.getAnnotations();
		for (BAnnotation anno : annos) {
			this.getLayout().addNode((BasicNode) anno);
		}
		this.setPackage(bclass.getPackage());
		this.setName(bclass.getName() == null ? bclass.getLogicName() : bclass.getName());
		this.setLogicName(bclass.getLogicName());
		if (bclass.getSuperClass() != null) {
			this.setSuperClass(bclass.getSuperClass());
		}

		List<BValuable> interfaces = bclass.getInterfaces();
		for (BValuable name : interfaces) {
			this.addInterface(name);
		}
		this.getLayout().layout();
	}

	public void setPackage(String name) {
		BasicNode node = this.makeRow("パッケージ", name, new LabelAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3391788515884290346L;

			@Override
			public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
				if (value != null && value instanceof String) {
					bclass.setPackage((String) value);

					if (sheet.getFile() != null) {
						sheet.getFile().delete();
						BEditorActions.SaveAction save = new BEditorActions.SaveAction(sheet);

						save.actionPerformed(null);
					}
					return true;
				}
				return false;
			}

		}, new BCallback() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1863933745620581538L;

			@Override
			public void call(Object obj) {
				BasicNode node = (BasicNode) obj;
				mxICell parent = node.getParent();
				if (parent != null) {

					if (parent.getChildAt(0).equals(node)) {
						node.setOffsetY(0);
					} else {
						node.setOffsetY(3);
					}
					if (parent instanceof ClassHeaderNode) {
						ClassHeaderNode header = (ClassHeaderNode) parent;
						header.connectModifier();
					}
				}

			}

		});

		ModifierNode modifier = new ModifierNode(this);
		modifier.getGeometry().getOffset().setY(0);
		this.modifierBID = modifier.getId();
		this.packageBID = node.getId();
	}

	public void connectModifier() {
		ModifierNode modifier = (ModifierNode) this.getCellByBID(modifierBID);
		if (!modifier.isConnected()) {
			modifier.connect(packageBID);
		}
	}

	public void setLogicName(String name) {
		this.makeRow("クラス名", name, new LabelAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3391788515884290346L;

			@Override
			public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
				if (value != null && value instanceof String) {
					String name = (String) value;
					bclass.setLogicName(name);
					((BeeModel) bclass).setTitleLabel(name);

					if (sheet.getFile() != null) {
						sheet.getFile().delete();
						BEditorActions.SaveAction save = new BEditorActions.SaveAction(sheet);

						save.actionPerformed(null);
					}
					return true;
				}
				return false;
			}

		}, null);
	}

	// for edit use
	public void addAnnotation(BAnnotation node) {
		bclass.addAnnotation(node);
	}

	public void setName(String name) {
		this.makeRow("説明", name, new LabelAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3391788515884290346L;

			@Override
			public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
				if (value != null && value instanceof String) {
					bclass.setName((String) value);
					return true;
				}
				return false;
			}

		}, null);
	}

	public void setSuperClass(BValuable value) {
		BasicNode node = this.makeTypeRow("親クラス名", (BVariable) value, new ConditionAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8410045472330719103L;

			@Override
			public boolean will(BValuable value) {
				if (value instanceof BValuable) {
					BValuable v = (BValuable) value;
					if (!v.getBClass().isInterface()) {
						if (v.getBClass().isFinal()) {
							JOptionPane.showMessageDialog(null, v.getBClass().getQualifiedName() + "は継承できないクラスです");
							return false;
						}
						return true;
					} else {
						JOptionPane.showMessageDialog(null,
								v.getBClass().getQualifiedName() + "はインターフェースであるため親クラスと設定できません");
					}
				}
				return false;
			}

		});
		node.addUserAttribute("SUPERCLASS", "SUPERCLASS");

		DeleteAction action = new DeleteAction();
		action.addAction("削除", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4151595146515671822L;

			@Override
			public void call() {
				node.removeFromParent();
				getLayout().layout();
				getBClass().setSuperClass(null);
			}
		});

		node.setAction(action);
	}

	public void addInterface(BValuable name) {
		BasicNode node = this.makeTypeRow("インターフェース", (BVariable) name, new ConditionAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8410045472330719103L;

			@Override
			public boolean will(BValuable v) {

				BClass b = v.getBClass();
				if (b.isInterface()) {
					List<BValuable> inters = getBClass().getInterfaces();
					for (BValuable inter : inters) {
						if (inter.getBClass().getQualifiedName().equals(b.getQualifiedName())) {
							JOptionPane.showConfirmDialog(null, b.getQualifiedName() + "はすでに追加されています");
							return false;
						}
					}
					return true;
				} else {
					JOptionPane.showMessageDialog(null, b.getQualifiedName() + "はインターフェースではありません");
					return false;
				}

			}

		});
		DeleteAction action = new DeleteAction();
		action.addAction("削除", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4151595146515671822L;

			@Override
			public void call() {
				node.removeFromParent();
				getLayout().layout();
				getBClass().getInterfaces().remove(name);
			}
		});

		node.setAction(action);
	}

	private BasicNode makeRow(String title, String value, ValueAction action, BCallback call) {
		int titleWidth = 100;
		int height = BeeConstants.VALUE_NODE_SPACING * 2 + BeeConstants.LINE_HEIGHT;
		BasicNode row = new BasicNode();
		row.getGeometry().setHeight(height);
		HorizonalLayout hl = new HorizonalLayout() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5972466652065856508L;

			@Override
			public void beforeContainerLayout() {
				if (call != null) {
					call.call(row);
				}
			}

		};
		hl.setSpacing(0);
		hl.setBetweenSpacing(0);
		row.setLayout(hl);
		row.setTitled();

		BasicNode name = new BasicNode();
		name.setOpaque(false);
		name.setFixedWidth(titleWidth);
		name.setValue(title);
		name.getGeometry().setHeight(height);

		hl.addNode(name);
		BasicNode valueLabel = new BasicNode();
		valueLabel.setValue(value);
		valueLabel.getGeometry().setHeight(height);
		valueLabel.setFixedWidth((int) (BeeConstants.SEGMENT_MAX_WIDTH - titleWidth));
		valueLabel.setEditable(true);
		valueLabel.setTitled();
		valueLabel.setValueAction(action);
		hl.addNode(valueLabel);
		this.getLayout().addNode(row);
		return row;
	}

	private BasicNode makeTypeRow(String title, BVariable value, ConditionAction action) {
		int titleWidth = 100;
		int height = BeeConstants.VALUE_NODE_SPACING * 2 + BeeConstants.LINE_HEIGHT;
		BasicNode row = new BasicNode();
		row.getGeometry().setHeight(height);
		HorizonalLayout hl = new HorizonalLayout();
		hl.setSpacing(0);
		hl.setBetweenSpacing(0);
		row.setLayout(hl);
		row.setTitled();

		BasicNode name = new BasicNode();
		name.setOpaque(false);
		name.setFixedWidth(titleWidth);
		name.setValue(title);
		name.getGeometry().setHeight(height);

		hl.addNode(name);
		TypeNode type = new TypeNode(value) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4925420544328344409L;

			@Override
			protected boolean typeCanChange(BValuable v) {
				return action.will(v);
			}

		};
		type.getGeometry().setHeight(height);
		type.setFixedWidth((int) (BeeConstants.SEGMENT_MAX_WIDTH - titleWidth));
		type.setTitled();
		hl.addNode(type);
		type.getLayout().setSpacing(BeeConstants.VALUE_NODE_SPACING);
		this.getLayout().addNode(row);
		return row;
	}

	@Override
	public BClass getBClass() {
		return this.bclass;
	}

	@Override
	public void deleteAnnotation(BAnnotation annotion) {
		this.bclass.deleteAnnotation(annotion);
	}

	@Override
	public List<BAnnotation> getAnnotations() {
		return this.bclass.getAnnotations();
	}

	@Override
	public ImageIcon getIcon() {
		int mod = this.bclass.getModifier();
		if (bclass.isInterface()) {
			return BeeConstants.INTERFACE_ICON;
		}
		if (Modifier.isPrivate(mod)) {
			return BeeConstants.CLASS_PRIVATE_ICON;
		} else if (Modifier.isPublic(mod)) {
			return BeeConstants.CLASSES_ICON;
		} else if (Modifier.isProtected(mod)) {
			return BeeConstants.CLASS_PROTECTED_ICON;
		}

		return BeeConstants.CLASS_ATTRIBUTE_ICON;
	}

	@Override
	public void Verify(BeeGraphSheet sheet, BProject project) {
		List<BValuable> interfaces = this.bclass.getInterfaces();
		for (BValuable value : interfaces) {
			BClass b = value.getBClass();
			if (b.isInterface()) {
				Class<?> cls = CodecUtils.getClassByName(b.getQualifiedName(), project);
				if (cls == null) {
					return;
				}
				Method[] methods = cls.getMethods();
				for (Method m : methods) {
					if (verifyAbstractMethod(cls, m, project, sheet)) {
						break;
					}
				}
			} else {
				this.setAlert("インターフェースでないクラスを実装しようとています。").setType(BAlert.TYPE_ERROR);
			}
		}
		BValuable value = this.bclass.getSuperClass();
		if (value != null) {
			BClass b = value.getBClass();
			Class<?> cls = CodecUtils.getClassByName(b.getQualifiedName(), project);
			if (Modifier.isAbstract(cls.getModifiers())) {
				Method[] methods = cls.getMethods();
				for (Method m : methods) {
					verifyAbstractMethod(cls, m, project, sheet);
				}
			}
		}
	}

	private boolean verifyAbstractMethod(Class<?> cls, Method m, BProject project, BeeGraphSheet sheet) {
		if (Modifier.isAbstract(m.getModifiers())) {
			BMethod bmethod = new BMethodImpl();
			CodecUtils.copyMethodToBMethod(cls.getName(), m, bmethod, project);
			List<BValuable> paras = new ArrayList<BValuable>();
			paras.addAll(bmethod.getParameter());
			BMethod method = CodecUtils.getMethod(bclass, m.getName(), paras, project);
			if (method == null) {
				boolean test = true;
				if (!test) {
					this.setAlert(cls.getName() + "のメソッドが実装されていません").setType(BAlert.TYPE_ERROR);
					this.getAlertObject().getActions().add(new TipAction() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public String getTitle() {
							return cls.getName() + "のメソッドが実装します";
						}

						@Override
						public void clicked() {

							addAllInterfaceMethod(project);
							LayoutUtils.makeView((mxICell) sheet.getGraph().getDefaultParent());
							LayoutUtils.makeNumber((mxICell) sheet.getGraph().getDefaultParent(), null);
							LayoutUtils.RelayoutAll(sheet);
							sheet.hideTip();
						}

						@Override
						public ImageIcon getIcon() {
							return BeeConstants.ACTION_ICON;
						}

					});
					return true;
				}
			}
		}
		return false;
	}

	private void addAbstractMethod(BClass b, Method m, BProject project) {
		MethodNode bmethod = new MethodNode();
		CodecUtils.copyMethodToBMethod(b.getQualifiedName(), m, bmethod, project);

		bmethod.setModifier(b.getModifier() ^ Modifier.ABSTRACT);

		AnnotationNode anno = new AnnotationNode();
		anno.setBClass(CodecUtils.getClassFromJavaClass(Override.class, project));
		bmethod.addAnnotation(anno);
		anno.setLogicName(Override.class.getSimpleName());

		List<BValuable> paras = new ArrayList<BValuable>();
		paras.addAll(bmethod.getParameter());
		BMethod method = CodecUtils.getMethod(bclass, m.getName(), paras, project);
		if (method == null) {
			mxICell parent = this.getParent();
			parent.insert(bmethod);
		}
	}

	private void addAllInterfaceMethod(BProject project) {
		List<BValuable> interfaces = this.bclass.getInterfaces();
		for (BValuable value : interfaces) {
			BClass b = value.getBClass();
			if (b.isInterface()) {
				Class<?> cls = CodecUtils.getClassByName(b.getQualifiedName(), project);
				Method[] methods = cls.getMethods();
				for (Method m : methods) {
					if (Modifier.isAbstract(m.getModifiers())) {
						this.addAbstractMethod(b, m, project);
					}
				}
			}
		}
	}

}
