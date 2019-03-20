package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;
import com.linkstec.bee.UI.spective.utils.ClassPopup;
import com.linkstec.bee.UI.spective.utils.ClassPopup.DTextActionImpl;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;

public class ValueRow extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -325620306423967072L;
	private BProject project;
	private int s = BeeUIUtils.getDefaultFontSize();
	private Hashtable<String, String> properties = new Hashtable<String, String>();

	private Object value;
	private String fixedValue;
	private NamingBar bar, packageBar;
	private JTextField text;
	private JPanel classTypePanel;

	public ValueRow(BProject project, String name, String fixedValue) {
		this.project = project;
		this.fixedValue = fixedValue;

		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		this.setLayout(flow);

		JLabel label = new JLabel("<html>" + name + "</html>");
		label.setPreferredSize(new Dimension(s * 5, s * 3));
		this.add(label);
		classTypePanel = this.createClassTypePanel(fixedValue);
		this.add(classTypePanel);

		JButton button = new JButton("削除");

		button.setPreferredSize(new Dimension(s * 4, s * 2));
		this.add(button);

		button.setOpaque(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ValueRow.this.getParent().remove(ValueRow.this);
			}

		});
	}

	public void setTextValue(String value) {
		this.text.setText(value);
	}

	private JPanel createClassTypePanel(String fiexedValue) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		JPanel panel = UIUtils.createFlowRow();
		panel.setBackground(Color.WHITE);
		text = new JTextField() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8874746501899852162L;

			@Override
			public Container getTopLevelAncestor() {
				return ValueRow.this.getTopLevelAncestor();
			}

		};
		text.setPreferredSize(new Dimension(s * 50, s * 2));
		ClassPopup.DTextAction action = new DTextActionImpl() {

			@Override
			public void valueChanged(BValuable v) {
				text.setText(v.getBClass().getQualifiedName());
				value = text.getText();
			}

			@Override
			public void fixedSelected(String fixedValue) {
				text.setEditable(false);
				p.add(makeNamingConfig(fixedValue), BorderLayout.CENTER);
				p.updateUI();
			}

		};
		new ClassPopup(text, action, this.project, fiexedValue);
		panel.add(text);

		p.add(panel, BorderLayout.NORTH);
		return p;
	}

	private JPanel makeNamingConfig(String fixedValue) {
		JPanel panel = new JPanel();
		UIUtils.setVerticalLayout(panel);
		this.bar = this.makeDtoNaming(panel, fixedValue, false);
		this.packageBar = this.makeDtoNaming(panel, fixedValue + "Package", true);

		return panel;
	}

	private NamingBar makeDtoNaming(JPanel panel, String name, boolean dotted) {
		JPanel row = UIUtils.createFlowRow();
		JLabel pack = new JLabel(name + "名ルール");
		int s = BeeUIUtils.getDefaultFontSize();
		pack.setPreferredSize(new Dimension(s * 10, s * 2));
		row.add(pack);
		NamingPanel naming = new NamingPanel(dotted);
		NamingBar bar = naming.getBar();
		bar.setPreferredSize(new Dimension(s * 50, s * 2));
		row.add(bar);
		panel.add(row);

		return bar;
	}

	public Object getValue() {
		if (this.fixedValue != null) {
			if (value == null && bar != null) {
				NamingModel[] ms = new NamingModel[2];
				ms[0] = this.bar.getModel();
				ms[1] = this.packageBar.getModel();
				return ms;
			}
		}
		if (value == null) {
			return this.text.getText();
		}
		return value;
	}

	public void setNamingValue(String fixedValue, NamingModel[] model) {
		JPanel p = makeNamingConfig(fixedValue);
		classTypePanel.add(p);
		this.bar.setModel(model[0]);
		this.packageBar.setModel(model[1]);
		this.updateUI();
	}

	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public Hashtable<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Hashtable<String, String> properties) {
		this.properties = properties;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

}
