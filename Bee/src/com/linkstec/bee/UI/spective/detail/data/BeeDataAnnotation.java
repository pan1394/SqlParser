package com.linkstec.bee.UI.spective.detail.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.table.BeeTableModel;
import com.linkstec.bee.UI.look.table.BeeTableNode;
import com.linkstec.bee.UI.look.table.BeeTableUndoEvent;
import com.linkstec.bee.UI.node.AnnotationNode;
import com.linkstec.bee.UI.node.AnnotationParameterNode;
import com.linkstec.bee.UI.node.ComplexNode;
import com.linkstec.bee.UI.node.ReferNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeDataAnnotation extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3274405604673412306L;
	public static Color BACK_COLOR = Color.decode("#FFFFCC");
	int s = BeeUIUtils.getDefaultFontSize();
	private JPanel contents = new JPanel();
	private BeeTableNode node = null;
	private BeeTableModel model;
	private int tablerow;

	public int getRow() {
		return tablerow;
	}

	public void setRow(int row) {
		this.tablerow = row;
	}

	public BeeDataAnnotation() {
		this.setViewportView(this.makeView());
	}

	public BeeTableModel getModel() {
		return model;
	}

	public void setModel(BeeTableModel model) {
		this.model = model;
	}

	private JPanel makeView() {
		JPanel panel = new JPanel();
		panel.setBackground(BACK_COLOR);
		panel.setLayout(new BorderLayout());
		panel.add(this.makeContents(), BorderLayout.CENTER);
		panel.add(this.makeButtons(), BorderLayout.NORTH);
		return panel;
	}

	public JPanel makeButtons() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		panel.setLayout(layout);

		JButton button = new JButton("Annotation追加");
		button.setUI(new BeeButtonUI());
		panel.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BProject project = Application.getInstance().getCurrentProject();
				String s = JOptionPane.showInputDialog("Annotationクラスのフルネームをいれてください", "java.lang.Override");

				if (s == null) {
					return;
				}
				Class<?> cls = CodecUtils.getClassByName(s, project);
				if (cls == null) {
					JOptionPane.showMessageDialog(null, s + "は有効なクラスではありません");
					return;
				}
				if (!cls.isAnnotation()) {

					JOptionPane.showMessageDialog(null, s + "はAnnotationではありません");
					return;
				}
				AnnotationNode anno = new AnnotationNode();
				anno.setBClass(CodecUtils.getClassFromJavaClass(cls, project));
				anno.setLogicName(cls.getSimpleName());
				anno.setName(cls.getSimpleName());

				node.setValueAt(node.getAnnotations(), 5);
				node.getAnnotations().add(anno);

				if (model != null) {
					BeeTableUndoEvent event = new BeeTableUndoEvent(node.getAnnotations(), tablerow, 5);
					model.getUndo().undoableEditHappened(event);
				}
				update();
			}

		});

		return panel;
	}

	private JPanel makeContents() {
		contents.setOpaque(false);
		contents.setPreferredSize(new Dimension(s * 30, 0));
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		contents.setLayout(layout);

		return contents;
	}

	public void addAnnotation(BAnnotation anno) {
		JPanel panel = this.makeAnnotation(anno);
		if (panel != null) {
			contents.add(panel);
		}
	}

	private JPanel makeAnnotation(BAnnotation anno) {

		int width = s * 35;
		JPanel panel = new JPanel();

		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);
		panel.setOpaque(true);
		panel.setBackground(Color.LIGHT_GRAY.brighter());
		panel.setBorder(new EtchedBorder());

		JPanel row = new JPanel();

		row.setPreferredSize(new Dimension(width, s * 2));
		row.setOpaque(false);

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		row.setLayout(flow);

		JTextField label = makeLabel(anno.getBClass().getQualifiedName());
		row.add(label);

		JButton button = new JButton("削除");
		row.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				node.getAnnotations().remove(anno);

				if (model != null) {
					if (model != null) {
						BeeTableUndoEvent event = new BeeTableUndoEvent(node.getAnnotations(), tablerow, 5);
						model.getUndo().undoableEditHappened(event);
					}
					update();
				}
				update();
			}

		});

		BProject project = Application.getInstance().getCurrentProject();
		BClass bclass = anno.getBClass();
		Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), project);
		if (cls == null) {
			return null;
		}
		Method[] methods = cls.getDeclaredMethods();

		List<BAnnotationParameter> paras = anno.getParameters();

		for (Method m : methods) {
			String name = m.getName();
			boolean contains = false;

			for (BAnnotationParameter para : paras) {
				if (para.getLogicName().equals(name)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				JButton b = new JButton(m.getName() + "追加");
				row.add(b);
				b.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						AnnotationParameterNode para = new AnnotationParameterNode();
						para.setLogicName(name);
						para.setName(name);
						BClass bclass = CodecUtils.getClassFromJavaClass(m.getReturnType(), project);
						para.setBClass(bclass);

						para.setValue(ComplexNode.makeDefaultValue(bclass));
						anno.addParameter(para);

						update();
					}

				});
			}
		}

		panel.add(row, BorderLayout.NORTH);
		JPanel center = new JPanel();
		center.setOpaque(false);
		FlowLayout flow1 = new FlowLayout();
		flow1.setAlignment(FlowLayout.LEFT);
		center.setLayout(flow1);
		int i = 0;
		panel.add(center, BorderLayout.CENTER);
		if (paras.size() > 0) {

			for (BAnnotationParameter para : paras) {
				center.add(this.makeParameter(anno, para));
				i++;
			}

		}
		center.setPreferredSize(new Dimension(width, (int) (i * s * 3)));
		return panel;
	}

	private JTextField makeLabel(String title) {
		JTextField label = new JTextField(title);
		label.setBorder(null);
		label.setOpaque(false);
		label.setEditable(false);
		label.setToolTipText(title);
		int s = BeeUIUtils.getDefaultFontSize();
		label.setPreferredSize(new Dimension(s * 13, s * 2));

		return label;
	}

	private JPanel makeParameter(BAnnotation anno, BAnnotationParameter para) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		panel.setLayout(layout);

		String name = para.getLogicName();
		BClass bclass = para.getBClass();
		JTextField value = new JTextField(para.getValue().toString());

		if (bclass.isArray()) {
			BClass array = (BClass) bclass.getArrayPressentClass();
			name = name + "(" + array.getQualifiedName() + "[])";
			value.setText("{" + value.getText() + "}");
		} else {
			name = name + "(" + bclass.getQualifiedName() + ")";
		}
		JTextField type = makeLabel(name);

		int s = BeeUIUtils.getDefaultFontSize();
		value.setPreferredSize(new Dimension(s * 15, s * 2));
		panel.add(type);
		panel.add(value);

		value.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				char c = e.getKeyChar();
				if (Character.isJavaIdentifierPart(c)) {
					String txt = value.getText();
					parameterValueChange(bclass, para, txt);

				}

			}

		});

		JButton button = new JButton("削除");
		panel.add(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<BAnnotationParameter> paras = anno.getParameters();
				for (BAnnotationParameter para : paras) {
					if (para.getLogicName().equals(para.getLogicName())) {
						anno.deleteParameter(para);
						update();
						break;
					}
				}
			}

		});

		return panel;
	}

	private void parameterValueChange(BClass type, BAnnotationParameter parameter, String text) {
		BValuable value = ComplexNode.makeDefaultValue(type);
		if (type.isArray()) {
			if (text.startsWith("{") && text.endsWith("}")) {
				text = text.substring(1);
				text = text.substring(0, text.length() - 1);
			}
			ComplexNode node = (ComplexNode) value;
			node.setArrayTitle(false);
			node.removeAll();
			BClass bclass = (BClass) type.getArrayPressentClass();
			if (text.indexOf(",") > 0) {
				String[] values = text.split(",");

				for (String s : values) {
					BValuable v = this.getValue(bclass, s);
					if (v != null) {
						node.addInitValue(v);
					}
				}
			} else {
				BValuable v = this.getValue(bclass, text);
				if (v != null) {
					node.addInitValue(v);
				}
			}

		} else {
			ComplexNode node = (ComplexNode) value;
			node.setLogicName(text);
		}
		parameter.setValue(value);
	}

	private BValuable getValue(BClass bclass, String text) {
		BValuable value = ComplexNode.makeDefaultValue(bclass);
		Class<?> cls = CodecUtils.getClassByName(bclass.getQualifiedName(), Application.getInstance().getCurrentProject());
		if (cls.isEnum()) {
			if (text.indexOf("#") > 0) {
				String[] values = text.split("#");
				if (values.length == 2) {
					if (values[0].equals(bclass.getLogicName())) {

						try {
							cls.getField(values[1]);
						} catch (Exception e) {
							return null;
						}
						ReferNode node = new ReferNode();
						ComplexNode parent = new ComplexNode();
						parent.setLogicName(bclass.getLogicName());
						parent.setBClass(bclass);
						parent.setClass(true);

						node.setInvokeParent(parent);

						ComplexNode child = new ComplexNode();
						child.setLogicName(values[1]);
						child.setBClass(bclass);

						node.setInvokeChild(child);
						return node;

					}
				}
			}
		} else {
			ComplexNode node = (ComplexNode) value;
			node.setLogicName(text);
		}

		return value;
	}

	public void setValue(Object value) {
		contents.removeAll();
		contents.setPreferredSize(new Dimension(s * 45, 0));
		if (value instanceof BeeTableNode) {
			node = (BeeTableNode) value;
			List<BAnnotation> annos = node.getAnnotations();
			for (BAnnotation anno : annos) {
				this.addAnnotation(anno);
			}
		}
		this.revalidate();
		this.updateUI();
	}

	private void update() {
		setValue(this.node);
	}
}
