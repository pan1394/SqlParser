package com.linkstec.bee.UI.spective.basic.config;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.button.BeeRadio;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;
import com.linkstec.bee.UI.spective.basic.config.utils.ActionButton;
import com.linkstec.bee.UI.spective.basic.config.utils.LayerConfig;
import com.linkstec.bee.UI.spective.basic.config.utils.Tab;
import com.linkstec.bee.UI.spective.basic.config.utils.UIUtils;
import com.linkstec.bee.UI.spective.basic.config.utils.ValueRow;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class A2ActionConfig extends Config {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1926313256997583548L;

	private JPanel actionArea = new JPanel();

	private JComboBox<ComponentTypeModel> from = new JComboBox<ComponentTypeModel>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6788211824620633035L;

		@Override
		public Insets getInsets() {
			int s = BeeUIUtils.getDefaultFontSize() / 3;
			return new Insets(s, s, s, s);
		}

	};

	private JComboBox<ComponentTypeModel> to = new JComboBox<ComponentTypeModel>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2973479144276466439L;

		@Override
		public Insets getInsets() {
			int s = BeeUIUtils.getDefaultFontSize() / 3;
			return new Insets(s, s, s, s);
		}
	};

	private JPanel editArea;

	public A2ActionConfig(BProject project, ConfigModel model) {
		super(project, model);
		this.setLayout(new BorderLayout());

		JScrollPane pane = new JScrollPane(actionArea);
		pane.getVerticalScrollBar().setUnitIncrement(50);
		// pane.setBorder(null);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

		pane.setPreferredSize(new Dimension(size.width / 9, size.height / 9));

		this.add(pane, BorderLayout.CENTER);
		BoxLayout layout = new BoxLayout(actionArea, BoxLayout.Y_AXIS);
		actionArea.setLayout(layout);
		editArea = this.makeButtons();
		this.add(editArea, BorderLayout.NORTH);

		EntityTree tree = new EntityTree(new BeeTreeNode(null));
		tree.updateUI();
		this.add(tree, BorderLayout.EAST);

		List<ActionModel> actions = model.getActions();
		for (ActionModel m : actions) {
			this.addNewAction(m);
		}

	}

	private JPanel makeButtons() {

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(s, s, s * 2, s));

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.CENTER);
		panel.setLayout(flow);

		this.initFromTo();

		panel.add(from);

		JComponent right = new JComponent() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3727896261485613813L;

			@Override
			public void paint(Graphics g) {

				Image img = BeeConstants.RIGHT_ICON.getImage();
				g.setColor(Color.LIGHT_GRAY);

				int iw = img.getWidth(this);

				int x1 = 0;
				int x2 = this.getWidth() - iw;

				int y1 = this.getHeight() / 2;
				int y2 = y1;
				g.drawLine(x1, y1, x2, y2);

				Graphics2D gg = (Graphics2D) g;

				int s = BeeUIUtils.getDefaultFontSize();
				gg.setStroke(new BasicStroke(s / 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

				gg.drawImage(img, x2, (this.getHeight() - img.getHeight(this)) / 2, this);

			}

		};

		right.setPreferredSize(new Dimension(s * 15, s * 2));

		panel.add(right);
		panel.add(to);

		JLabel space = new JLabel();
		space.setPreferredSize(new Dimension(s * 5, s * 2));
		panel.add(space);

		JButton b = UIUtils.createButton("Action追加");
		panel.add(b);

		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ActionModel model = new ActionModel();
				model.setFrom((ComponentTypeModel) from.getSelectedItem());
				model.setTo((ComponentTypeModel) to.getSelectedItem());
				addNewAction(model);
			}

		});

		return panel;
	}

	private void initFromTo() {
		List<ComponentTypeModel> types = model.getComponentTypes();
		from.removeAllItems();
		to.removeAllItems();

		for (ComponentTypeModel type : types) {
			from.addItem(type);
			to.addItem(type);
		}
	}

	@Override
	public void onSelected() {
		this.initFromTo();
		from.updateUI();
		to.updateUI();
		editArea.updateUI();
	}

	private void addNewAction(ActionModel action) {
		if (this.added(action)) {
			return;
		}
		Tab tab = new Tab(action.getFrom().getName() + "TO" + action.getTo().getName());

		ActionButton addPara = UIUtils.createButton("パラメータタイプ", "パラメータ追加", "PARA", project, "IN DTO", false);
		ActionButton setReturn = UIUtils.createButton("戻り値タイプ", "戻り値タイプを設定する", "RETURN", project, "OUT DTO", false);
		ActionButton addAnno = UIUtils.createButton("Annotation", "Annotationを追加する", "ANNOTATION", project, null,
				false);
		ActionButton addException = UIUtils.createButton("Exception", "Exceptionを追加する", "EXCEPTION", project, null,
				false);
		JPanel center = tab.setEditable(new ActionButton[] { addPara, setReturn, addAnno, addException });
		tab.setUserObject(action);

		List<Object> paras = action.getParameters();
		for (Object obj : paras) {
			ValueRow row = tab.addRow("IN DTO", project, "パラメータタイプ", "PARA");

			if (obj instanceof String) {
				row.setTextValue((String) obj);
			} else if (obj instanceof NamingModel[]) {
				row.setTextValue("IN DTO");
				row.setNamingValue("IN DTO", (NamingModel[]) obj);
			}
		}

		Object type = action.getReturnType();
		if (type != null) {
			ValueRow row = tab.addRow("OUT DTO", project, "戻り値タイプ", "RETURN");
			if (type instanceof String) {
				row.setTextValue((String) type);
			} else if (type instanceof NamingModel[]) {
				row.setTextValue("OUT DTO");
				row.setNamingValue("OUT DTO", (NamingModel[]) type);
			}
		}

		List<String> exes = action.getExceptions();
		for (String s : exes) {
			ValueRow row = tab.addRow(null, project, "Exception", "EXCEPTION");
			row.setTextValue(s);
		}

		List<String> annos = action.getAnnotations();
		for (String s : annos) {
			ValueRow row = tab.addRow(null, project, "Annotation", "ANNOTATION");
			row.setTextValue(s);
		}

		center.add(this.makeContents(action));
		center.add(this.makeProvider(action));
		actionArea.add(tab);
		actionArea.updateUI();
	}

	private boolean added(ActionModel model) {
		Component[] comps = this.actionArea.getComponents();
		for (Component comp : comps) {
			if (comp instanceof Tab) {
				Tab tab = (Tab) comp;
				if (tab.getTitle().equals(model.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	private JPanel makeContents(ActionModel model) {
		JPanel panel = this.createPanel();
		int s = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(s, s, s, s));
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		panel.setLayout(layout);

		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < 6; i++) {
			this.makeDefault(group, panel, i, model);
		}

		JButton button = new JButton("編集");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LayerConfig config = new LayerConfig(project, getConfigModel(), model);
				addConfig(config);
			}

		});

		panel.add(button);

		return panel;
	}

	private JPanel makeProvider(ActionModel model) {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		List<BProject> projects = config.getProjects();

		JComboBox<BProject> ps = new JComboBox<BProject>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6788211824620633035L;

			@Override
			public Insets getInsets() {
				int s = BeeUIUtils.getDefaultFontSize() / 3;
				return new Insets(s, s, s, s);
			}

		};

		BProject savedProject = null;
		for (BProject bp : projects) {
			ps.addItem(bp);
			if (bp.getName().equals(model.getProviderProject())) {
				ps.setSelectedItem(bp);
				savedProject = bp;
			}
		}
		ValueRow provider = new ValueRow(savedProject, "ロジックプロバイダー", null);
		ps.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object obj = e.getItem();
				if (obj instanceof BProject) {
					BProject b = (BProject) obj;
					model.setProviderProject(b.getName());
					provider.setProject(b);
				}

			}

		});

		provider.setTextValue(model.getProviderName());

		JPanel pp = new JPanel();
		pp.setName("Provider");
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.CENTER);

		pp.add(ps);
		pp.add(provider);

		return pp;
	}

	public void makeDefault(ButtonGroup group, JPanel panel, int index, ActionModel model) {
		BeeRadio radio = new BeeRadio((index + 1) + "階層");
		radio.setUserObject(index);
		radio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.getLayers().clear();
				int size = (int) ((BeeRadio) e.getSource()).getUserObject();
				for (int i = 0; i <= size; i++) {
					LayerModel layer = new LayerModel(model);
					layer.setIndex(i);
					model.getLayers().add(layer);
				}
			}

		});
		group.add(radio);
		panel.add(radio);

		if (model == null) {
			return;
		}

		if (model.getLayers() == null) {
			return;
		}

		if (model.getLayers() != null) {

			if (model.getLayers().size() - 1 == index) {
				radio.setSelected(true);
			}
		}

	}

	@Override
	public void beforeSave() {
		this.model.getActions().clear();

		Component[] comps = this.actionArea.getComponents();
		for (Component comp : comps) {
			if (comp instanceof Tab) {
				Tab tab = (Tab) comp;
				JPanel view = tab.getView();
				Component[] vs = view.getComponents();
				ActionModel action = (ActionModel) tab.getUserObject();
				action.getExceptions().clear();
				action.getParameters().clear();
				action.getAnnotations().clear();

				for (Component c : vs) {
					if ("Model".equals(c.getName())) {
						JPanel panel = (JPanel) c;
						Component[] rows = panel.getComponents();

						for (Component row : rows) {
							if (row instanceof ValueRow) {

								ValueRow r = (ValueRow) row;
								if ("PARA".equals(r.getProperty("TYPE"))) {
									if (r.getValue() != null) {
										action.getParameters().add(r.getValue());
									}
								} else if ("RETURN".equals(r.getProperty("TYPE"))) {
									if (r.getValue() != null) {
										action.setReturnType(r.getValue());
									}
								} else if ("EXCEPTION".equals(r.getProperty("TYPE"))) {
									String s = (String) r.getValue();
									if (s != null) {
										action.getExceptions().add(s);
									}
								} else if ("ANNOTATION".equals(r.getProperty("TYPE"))) {
									String s = (String) r.getValue();
									if (s != null) {
										action.getAnnotations().add(s);
									}
								}
							} else if ("Provider".equals(row.getName())) {
								JPanel pro = (JPanel) row;
								Component[] dd = pro.getComponents();
								for (Component rr : dd) {
									if (rr instanceof ValueRow) {
										ValueRow r = (ValueRow) rr;
										String s = (String) r.getValue();
										if (s != null && !s.equals("")) {
											action.setProviderName(s);
										}

									}
								}
							}
						}

					}
				}

				this.model.getActions().add(action);
			}
		}
	}

	@Override
	public String getTitle() {
		return "2.Action設定";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ACTION_ICON;
	}

}
