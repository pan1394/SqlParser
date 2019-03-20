package com.linkstec.bee.UI.spective.basic.config.utils;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.config.model.LayerModel;
import com.linkstec.bee.UI.spective.basic.config.model.NamingModel;
import com.linkstec.bee.core.fw.editor.BProject;

public class BetweenLayerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6087558081473183507L;

	private int s = BeeUIUtils.getDefaultFontSize();
	private BProject project;
	int rowWidth = s * 5;
	int arrowHeight = rowWidth * 2;

	private Image up, down;
	private LayerModel model;
	private JPanel parameterPanel;
	private JPanel returnPanel;

	public BetweenLayerPanel(BProject project, LayerModel model) {
		this.project = project;
		this.model = model;

		up = new ImageIcon(this.getClass().getResource("/com/linkstec/bee/UI/images/upRow.png")).getImage();
		down = new ImageIcon(this.getClass().getResource("/com/linkstec/bee/UI/images/downRow.png")).getImage();

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(layout);

		JPanel toPanel = this.makeToPanel();
		JPanel returnPanel = this.makeReturnPanel();

		this.setOpaque(false);

		this.add(toPanel);
		this.add(returnPanel);
	}

	@Override
	public Insets getInsets() {
		int m = s * 5;
		return new Insets(m, m, m, m);
	}

	@Override
	public void paint(Graphics g) {

		int width = this.getWidth();
		int w = width / 2;
		int x = w / 2 - rowWidth / 2;
		int y = 0;
		int height = this.getHeight();

		g.drawImage(down, x, y, rowWidth, height, this);

		x = x + this.getWidth() / 2;
		g.drawImage(up, x, y, rowWidth, height, this);

		super.paint(g);
	}

	private JPanel makeToPanel() {
		parameterPanel = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3937760094785567373L;

			@Override
			public Insets getInsets() {
				int m = s;
				return new Insets(m, m, 0, m);
			}
		};

		JPanel buttonRow = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		buttonRow.setLayout(flow);

		UIUtils.setVerticalLayout(parameterPanel);

		ActionButton addPara = UIUtils.createButton("パラメータタイプ", "パラメータ追加", "PARA", project, "IN DTO", false);
		buttonRow.add(addPara);

		ActionListener listner = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ActionButton b = (ActionButton) e.getSource();
				addRow(parameterPanel, b.getFixedValue(), b.getProject(), b.getName(), b.getType());
			}

		};

		addPara.addActionListener(listner);

		parameterPanel.add(buttonRow);

		List<Object> paras = model.getParameters();
		for (Object obj : paras) {
			ValueRow row = addRow(parameterPanel, "IN DTO", project, "パラメータタイプ", "PARA");

			if (obj instanceof String) {
				row.setTextValue((String) obj);
			} else if (obj instanceof NamingModel[]) {
				row.setTextValue("IN DTO");
				row.setNamingValue("IN DTO", (NamingModel[]) obj);
			}
		}

		return parameterPanel;
	}

	public ValueRow addRow(JPanel center, String fiexedValue, BProject project, String name, String type) {

		ValueRow row = new ValueRow(project, name, fiexedValue);
		row.addProperty("TYPE", type);
		center.add(row);
		center.updateUI();

		return row;
	}

	private JPanel makeReturnPanel() {
		returnPanel = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3937760094785567373L;

			@Override
			public Insets getInsets() {
				int m = s;
				return new Insets(m, m, m, m);
			}
		};
		UIUtils.setVerticalLayout(returnPanel);

		ValueRow row = new ValueRow(project, "戻り値タイプ", "OUT DTO");

		Object type = model.getReturnType();
		if (type != null) {
			if (type instanceof String) {
				row.setTextValue((String) type);
			} else if (type instanceof NamingModel[]) {
				row.setTextValue("OUT DTO");
				row.setNamingValue("OUT DTO", (NamingModel[]) type);
			}
		}

		returnPanel.add(row);
		return returnPanel;
	}

	public void beforeSave() {
		List<Object> paras = this.getValue(parameterPanel);
		model.setParameters(paras);

		List<Object> returns = this.getValue(this.returnPanel);
		if (returns.size() == 1) {
			model.setReturnType(returns.get(0));
		}
	}

	private List<Object> getValue(JPanel panel) {
		List<Object> list = new ArrayList<Object>();
		Component[] rows = panel.getComponents();
		for (Component row : rows) {
			if (row instanceof ValueRow) {
				ValueRow r = (ValueRow) row;
				if (r.getValue() != null) {
					list.add(r.getValue());
				}

			}
		}
		return list;
	}
}
