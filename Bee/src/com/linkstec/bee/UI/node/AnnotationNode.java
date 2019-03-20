package com.linkstec.bee.UI.node;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.editor.action.BCall;
import com.linkstec.bee.UI.editor.action.EditAction;
import com.linkstec.bee.UI.editor.action.MixAction;
import com.linkstec.bee.UI.node.layout.LayoutUtils;
import com.linkstec.bee.UI.node.layout.VerticalLayout;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.edit.TypeAction;
import com.linkstec.bee.UI.spective.detail.edit.ValueAction;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotable;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.mxgraph.model.mxICell;

public class AnnotationNode extends ComplexNode implements BAnnotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4298274016630321024L;

	private String logicNameBID;

	public AnnotationNode() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(0);
		this.setOffsetX(3);
		this.setOffsetY(3);

		this.setLayout(layout);

		BasicNode logicName = new BasicNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7773263820134947009L;

			@Override
			public ValueAction getValueAction() {

				TypeAction action = new TypeAction() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 2684389957510084738L;
					private boolean sucess = false;

					@Override
					public boolean onValueSet(Object value, BasicNode source, BeeGraphSheet sheet) {
						sucess = false;
						if (value instanceof BValuable) {
							BClass old = getBClass();
							BValuable b = (BValuable) value;
							BClass bclass = b.getBClass();
							if (old != null && old.getQualifiedName().equals(bclass.getQualifiedName())) {
								sucess = true;
								return false;
							}

							Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), sheet.getProject());
							if (cls.isAnnotation()) {
								setBClass(bclass);
								setLogicName(bclass.getLogicName());
								sucess = true;
								if (old != null) {
									removeAllParameters();
									LayoutUtils.layoutNode(sheet.getGraph().getDefaultParent());
								}
								addAnnotationToParent();
								return false;
							} else {
								JOptionPane.showMessageDialog(null, bclass.getQualifiedName() + "はAnnotationクラスではありません");
							}
						} else {
							JOptionPane.showMessageDialog(null, "有効なAnnotationクラスではありません");
						}
						return false;
					}

					@Override
					public void afterActFalse(BeePopupTreeMenu menu, BeeGraphSheet sheet) {
						if (sucess) {
							menu.setVisible(false);
							sheet.getGraph().refresh();
						}
					}

				};
				return action;
			}

		};
		logicName.setTitled();
		logicName.setEditable(true);
		int titleWidth = 150;
		logicName.setFixedWidth(titleWidth);

		this.logicNameBID = logicName.getId();
		layout.addNode(logicName);

		int height = BeeConstants.VALUE_NODE_SPACING * 2 + BeeConstants.LINE_HEIGHT;

		logicName.getGeometry().setHeight(height);

	}

	@Override
	public EditAction getAction() {

		BClass bclass = this.getBClass();
		if (bclass == null) {
			return null;
		}
		String className = bclass.getQualifiedName();
		Class<?> cls = CodecUtils.getClassByName(className, Application.getInstance().getCurrentProject());
		Method[] mthods = cls.getDeclaredMethods();

		List<BAnnotationParameter> parameters = this.getParameters();
		List<String> names = new ArrayList<String>();
		for (BAnnotationParameter p : parameters) {
			names.add(p.getLogicName());
		}

		MixAction action = new MixAction();
		action.addAction("削除", new BCall() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8521001736588228419L;

			@Override
			public void call() {
				deleteAnnotationToParent();
			}

		});

		for (Method method : mthods) {
			String name = method.getName();
			if (names.contains(name)) {
				continue;
			}
			Class<?> type = method.getReturnType();
			action.addAction(name + "追加", new BCall() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 8264650953453806111L;

				@Override
				public void call() {
					AnnotationParameterNode node = new AnnotationParameterNode();
					BClass bclass = CodecUtils.getClassFromJavaClass(type, Application.getInstance().getCurrentProject());

					node.setBClass(bclass);
					node.setLogicName(name);
					node.setValue(ComplexNode.makeDefaultValue(bclass));
					addParameter(node);
				}

			});
		}

		return action;
	}

	@Override
	public void addParameter(BAnnotationParameter parameter) {
		this.getLayout().addNode((BasicNode) parameter);
	}

	@Override
	public List<BAnnotationParameter> getParameters() {
		int count = this.getChildCount();
		List<BAnnotationParameter> list = new ArrayList<BAnnotationParameter>();
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj instanceof BAnnotationParameter) {
				list.add((BAnnotationParameter) obj);
			}
		}
		return list;
	}

	// for edit use
	private void removeAllParameters() {
		int count = this.getChildCount();

		for (int i = count - 1; i >= 0; i--) {
			mxICell obj = this.getChildAt(i);
			if (obj instanceof BAnnotationParameter) {
				obj.removeFromParent();
			}
		}
	}

	// for edit use
	private void addAnnotationToParent() {
		mxICell parent = this.getParent();
		if (parent instanceof BAnnotable) {
			BAnnotable node = (BAnnotable) parent;
			node.addAnnotation(this);
		}
	}

	// for edit use
	private void deleteAnnotationToParent() {
		mxICell parent = this.getParent();
		if (parent instanceof BAnnotable) {
			BAnnotable node = (BAnnotable) parent;
			node.deleteAnnotation(this);
		}
		if (this.getParent() != null) {
			this.removeFromParent();
		}
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ANNOTATION_ICON;
	}

	@Override
	public String getLogicName() {
		String s = (String) this.getCellByBID(logicNameBID).getValue();
		if (s != null && s.length() > 1) {
			return s.substring(1);
		} else {
			return s;
		}
	}

	@Override
	public void setLogicName(String name) {
		this.getCellByBID(logicNameBID).setValue("@" + name);
	}

	public String toString() {
		return this.getLogicName();
	}

	@Override
	public void deleteParameter(BAnnotationParameter parameter) {
		((BasicNode) parameter).removeFromParent();
	}

}
