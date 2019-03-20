package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.button.BeeRadio;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.AnnotationNode;
import com.linkstec.bee.UI.spective.basic.config.Config;
import com.linkstec.bee.UI.spective.basic.config.EntityTree;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BAnnotation;
import com.linkstec.bee.core.fw.editor.BProject;

public class LayerConfig extends Config {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3583737187458504851L;
	private JPanel layers = new JPanel();

	private ActionModel action;
	private ConfigModel model;

	public LayerConfig(BProject project, ConfigModel model, ActionModel action) {
		super(project, model);
		this.action = action;

		this.model = model;

		this.setLayout(new BorderLayout());
		this.add(this.makeContents(), BorderLayout.NORTH);

		UIUtils.setVerticalLayout(layers);
		layers.setBackground(SystemColor.windowBorder);
		JScrollPane pane = new JScrollPane(layers);
		pane.setPreferredSize(new Dimension(s * 60, s * 40));
		pane.getVerticalScrollBar().setUnitIncrement(50);
		pane.setBorder(null);
		this.add(pane, BorderLayout.CENTER);
		this.add(new EntityTree(new BeeTreeNode(null)), BorderLayout.EAST);

	}

	private JPanel makeContents() {
		JPanel panel = this.createPanel();
		int s = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(s, s, s, s));
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		panel.setLayout(layout);

		List<LayerModel> list = this.action.getLayers();
		for (int i = 0; i < list.size(); i++) {
			this.makeLayers(list.get(i));
		}

		return panel;
	}

	@Override
	public String getTitle() {
		return action.toString() + "クラス階層定義";
	}

	@Override
	public ImageIcon getIcon() {

		return BeeConstants.CLASSES_ICON;
	}

	public void makeLayers(LayerModel model) {
		int i = model.getIndex();
		Tab tab = new Tab((i + 1) + "階層目");

		if (model.getIndex() != 0) {
			layers.add(new BetweenLayerPanel(project, model));
		}
		this.layers.add(tab);

		int size = action.getLayers().size();
		if (size > 0 && size > i) {
			model = action.getLayers().get(i);
		}

		final LayerModel forTarget = model;

		tab.setUserObject(model);

		ActionButton superClass = UIUtils.createButton("SuperClass名", "SuperClas編集", "SUPER_CLASS", project, null, false);
		ActionButton inter = UIUtils.createButton("Interface名", "Interfaces追加", "INTERFACE", project, null, false);
		ActionButton anno = UIUtils.createButton("Annotaion名", "Annotaion追加", "ANNOTATION", project, null, false);

		JPanel c = tab.setEditable(new ActionButton[] { superClass, inter, anno });

		NamingRow packrow = new NamingRow("パッケージルール", "PACKAGE", true);
		c.add(packrow);
		NamingPanel pack = packrow.getNamingPanel();

		NamingRow classRow = new NamingRow("Class名ルール", "CLASS_NAME", false);
		c.add(classRow);
		NamingPanel namec = classRow.getNamingPanel();

		String superClassName = model.getSuperClass();
		if (superClassName != null) {
			ValueRow row = tab.addRow(null, project, "SuperClass名", "SUPER_CLASS");
			row.setTextValue(superClassName);
		}

		List<String> inters = model.getInterfaces();
		for (String in : inters) {
			ValueRow row = tab.addRow(null, project, "Interface名", "INTERFACE");
			row.setTextValue(in);
		}

		List<BAnnotation> annos = model.getAnnotations();
		for (BAnnotation in : annos) {
			ValueRow row = tab.addRow(null, project, "Annotaion名", "ANNOTATION");
			row.setTextValue(in.getBClass().getQualifiedName());
		}

		NamingModel ppack = model.getPackegeName();
		if (ppack != null) {
			pack.getBar().setModel(ppack);
		}

		NamingModel pname = model.getName();
		if (pname != null) {
			namec.getBar().setModel(pname);
		}

		ProcessType[] types = ProcessType.values();

		JPanel typePanel = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.CENTER);

		JLabel typeName = new JLabel("処理対象");

		typePanel.setLayout(flow);
		typePanel.add(typeName);

		ProcessType target = forTarget.getTargetProcessType();

		ButtonGroup group = new ButtonGroup();
		for (ProcessType type : types) {
			BeeRadio radio = new BeeRadio(type.getName());
			radio.setUserObject(type);
			typePanel.add(radio);
			group.add(radio);
			if (target != null) {
				if (type.getType() == target.getType()) {
					radio.setSelected(true);
				}
			}

			radio.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					BeeRadio b = (BeeRadio) e.getSource();
					ProcessType model = (ProcessType) ((BeeRadio) e.getSource()).getUserObject();
					if (b.isSelected()) {
						forTarget.setTargetProcessType(model);
					}
				}

			});

		}

		c.add(typePanel);
	}

	@Override
	public void beforeSave() {
		action.getLayers().clear();

		Component[] comps = this.layers.getComponents();
		for (Component com : comps) {
			if (com instanceof BetweenLayerPanel) {
				BetweenLayerPanel b = (BetweenLayerPanel) com;
				b.beforeSave();
			} else if (com instanceof Tab) {
				Tab tab = (Tab) com;
				LayerModel model = (LayerModel) tab.getUserObject();
				action.getLayers().add(model);
				model.getInterfaces().clear();
				model.getAnnotations().clear();
				JPanel view = tab.getCeneter();
				Component[] cs = view.getComponents();
				for (Component c : cs) {
					String name = c.getName();
					if (c instanceof NamingRow) {
						NamingRow row = (NamingRow) c;
						NamingModel nm = row.getNamingPanel().getBar().getModel();
						if ("PACKAGE".equals(name)) {
							model.setPackegeName(nm);
						} else if ("CLASS_NAME".equals(name)) {
							model.setName(nm);
						}

					} else if (c instanceof ValueRow) {
						ValueRow row = (ValueRow) c;
						name = row.getProperty("TYPE");
						if ("SUPER_CLASS".equals(name)) {
							model.setSuperClass((String) row.getValue());
						} else if ("INTERFACE".equals(name)) {
							model.getInterfaces().add((String) row.getValue());
						} else if ("ANNOTATION".equals(name)) {
							AnnotationNode node = new AnnotationNode();
							String s = (String) row.getValue();
							if (s != null) {
								node.setBClass(CodecUtils.getClassFromJavaClass(project, s));
								model.getAnnotations().add(node);
							}
						}
					}
				}
			}
		}
	}

}
