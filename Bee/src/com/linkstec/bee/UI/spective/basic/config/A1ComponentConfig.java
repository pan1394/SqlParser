package com.linkstec.bee.UI.spective.basic.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.button.BeeRadio;
import com.linkstec.bee.UI.look.icon.BeeIcon;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.InputType;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.OutputType;
import com.linkstec.bee.UI.spective.basic.config.utils.Tab;
import com.linkstec.bee.core.fw.editor.BProject;

public class A1ComponentConfig extends Config {

	private JPanel view;

	public A1ComponentConfig(BProject project, ConfigModel model) {
		super(project, model);

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		this.setLayout(flow);

		view = new JPanel();
		view.setOpaque(false);
		this.add(view);

		BoxLayout layout = new BoxLayout(view, BoxLayout.Y_AXIS);
		view.setLayout(layout);

		view.add(this.makeEditor());
		List<ComponentTypeModel> types = model.getComponentTypes();
		for (ComponentTypeModel type : types) {
			Tab tab = new Tab(type.getName());
			tab.getView().add(new Item(tab, type));
			view.add(tab);
		}

	}

	private JPanel makeEditor() {
		JPanel panel = new JPanel();
		int s = BeeUIUtils.getDefaultFontSize();
		panel.setBorder(new EmptyBorder(s, s, s * 2, s));
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		panel.setLayout(layout);

		JLabel name = new JLabel("コンポーネントタイプ名");
		JTextField text = new JTextField();

		name.setPreferredSize(new Dimension(s * 20, s * 2));
		text.setPreferredSize(new Dimension(s * 40, s * 2));
		panel.add(name);
		panel.add(text);

		JButton b = new JButton("追加");
		panel.add(b);
		b.setUI(new BeeButtonUI());

		b.setPreferredSize(new Dimension(s * 5, s * 2));

		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = text.getText();
				s = s.trim();
				if (!s.equals("")) {
					Tab tab = new Tab(text.getText());
					tab.getView().add(new Item(tab, new ComponentTypeModel()));
					view.add(tab);
					text.setText("");
					view.updateUI();

				}
			}

		});

		return panel;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8796373084599669192L;

	@Override
	public String getTitle() {
		return "1.コンポーネントタイプ定義";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.IO_CONFIG_ICON;
	}

	public static class Item extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1934596454154055011L;
		private Tab tab;
		private int intput = -1;
		private int output = -1;
		private String iconPath = null;

		private ComponentTypeModel model;

		@Override
		public Insets getInsets() {
			int s = BeeUIUtils.getDefaultFontSize();
			return new Insets(s, s, s, s);
		}

		public Item(Tab tab, ComponentTypeModel model) {
			this.model = model;

			this.intput = model.getInputType();
			this.output = model.getOutputType();
			this.iconPath = model.getIconPath();

			this.tab = tab;

			BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(layout);
			this.makeContetns();
		}

		private void makeContetns() {
			this.add(this.makeInputRow());
			this.add(this.makeOutputRow());
			this.add(this.makeIconRow());
			this.add(this.makeButton());
		}

		public ComponentTypeModel getModel() {
			model.setName(tab.getTitle());
			model.setIconPath(iconPath);
			model.setInputType(intput);
			model.setOutputType(output);
			return model;
		}

		private JPanel makeButton() {
			JPanel panel = new JPanel();
			panel.setOpaque(false);

			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.RIGHT);
			panel.setLayout(flow);

			JButton button = new JButton("削除");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tab.getParent().remove(tab);
				}

			});

			panel.add(button);
			return panel;
		}

		private JPanel makeOutputRow() {
			int s = BeeUIUtils.getDefaultFontSize();
			ItemPanel row = new ItemPanel();
			JLabel label = new JLabel("情報をAppから取得する方式");
			label.setPreferredSize(new Dimension(s * 20, s * 2));

			row.addSelection(label);
			ButtonGroup group = new ButtonGroup();

			OutputType[] values = OutputType.values();
			for (OutputType in : values) {
				BeeRadio radio = new BeeRadio(in.toString());
				if (this.output == in.getType()) {
					radio.setSelected(true);
				}
				radio.setUserObject(in);
				group.add(radio);
				radio.addActionListener(this);
				row.addSelection(radio);
			}
			return row;
		}

		private JPanel makeInputRow() {
			int s = BeeUIUtils.getDefaultFontSize();
			ItemPanel row = new ItemPanel();
			JLabel label = new JLabel("情報をAppへ引き渡す方式");
			label.setPreferredSize(new Dimension(s * 20, s * 2));
			row.addSelection(label);

			InputType[] values = InputType.values();

			ButtonGroup group = new ButtonGroup();
			for (InputType out : values) {
				BeeRadio radio = new BeeRadio(out.toString());
				radio.setUserObject(out);
				if (this.intput == out.getType()) {
					radio.setSelected(true);
				}
				group.add(radio);
				radio.addActionListener(this);
				row.addSelection(radio);
			}

			return row;
		}

		private JPanel makeIconRow() {
			int s = BeeUIUtils.getDefaultFontSize();
			ItemPanel row = new ItemPanel();
			JLabel label = new JLabel("アイコン");
			label.setPreferredSize(new Dimension(s * 20, s * 2));
			row.addSelection(label);

			String[] values = { ModelConstants.DB_ICON, ModelConstants.FILE_ICON, ModelConstants.APPLICATION_ICON, ModelConstants.MESSAGE_ICON, ModelConstants.SESSION_ICON };

			ButtonGroup group = new ButtonGroup();
			for (String out : values) {

				BeeIcon icon = new BeeIcon(BeeConstants.class.getResource(out));
				BeeRadio radio = new BeeRadio();
				radio.setUserObject(out);

				if (out.equals(this.iconPath)) {
					radio.setSelected(true);
				}

				group.add(radio);
				radio.addActionListener(this);
				row.addSelection(radio);
				JLabel l = new JLabel("     ");
				l.setIcon(icon);
				row.addSelection(l);
			}

			return row;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if (obj instanceof BeeRadio) {
				BeeRadio bee = (BeeRadio) obj;
				Object t = bee.getUserObject();
				int type = -1;
				if (t instanceof InputType) {
					InputType input = (InputType) t;
					type = input.getType();
					this.intput = type;
				} else if (t instanceof OutputType) {
					OutputType output = (OutputType) t;
					type = output.getType();
					this.output = type;
				} else if (t instanceof String) {
					this.iconPath = (String) t;
				}
				ItemPanel container = (ItemPanel) bee.getParent().getParent();
				container.clearSub();
				if (type == ModelConstants.TYPE_PAREMETE_INVOKE) {
					container.setSub(this.getParameterInvokerEditor());
				} else if (type == ModelConstants.TYPE_STATIC) {

				}
			}

		}

		private JPanel getParameterInvokerEditor() {
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);
			panel.setLayout(layout);

			JLabel name = new JLabel("パラメータClass名");
			JTextField text = new JTextField();

			int s = BeeUIUtils.getDefaultFontSize();
			name.setPreferredSize(new Dimension(s * 20, s * 2));
			text.setPreferredSize(new Dimension(s * 40, s * 2));
			panel.add(name);
			panel.add(text);

			return panel;
		}
	}

	public static class ItemPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3019340542912900305L;
		private JPanel selection = new JPanel();
		private JPanel sub = new JPanel();

		public ItemPanel() {

			this.setOpaque(false);

			this.setLayout(new BorderLayout());

			selection.setOpaque(false);
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);
			selection.setLayout(layout);

			sub.setLayout(new BorderLayout());
			sub.setBackground(SystemColor.control);

			this.add(selection, BorderLayout.CENTER);
			this.add(sub, BorderLayout.SOUTH);
		}

		public void addSelection(JComponent comp) {
			this.selection.add(comp);
		}

		public void setSub(JComponent comp) {
			sub.removeAll();
			sub.add(comp, BorderLayout.CENTER);

			sub.setBorder(new EtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.LIGHT_GRAY));
			this.updateUI();

		}

		public void clearSub() {
			sub.setBorder(null);
			sub.removeAll();
			this.updateUI();
		}
	}

	@Override
	public void beforeSave() {
		this.model.getComponentTypes().clear();
		Component[] comps = view.getComponents();
		for (Component c : comps) {
			if (c instanceof Tab) {
				Tab tab = (Tab) c;
				Component[] cs = tab.getView().getComponents();
				for (Component cc : cs) {
					if (cc instanceof Item) {
						Item item = (Item) cc;
						this.model.getComponentTypes().add(item.getModel());
					}
				}
			}
		}

	}

}
