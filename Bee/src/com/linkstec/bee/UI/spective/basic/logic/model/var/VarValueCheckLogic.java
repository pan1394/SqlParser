package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;

public class VarValueCheckLogic extends VarCheckLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6864333849280262640L;
	private BLogiker selected = BLogiker.EQUAL;
	private BValuable value;
	private List<BValuable> betweenValues = new ArrayList<BValuable>();
	private List<BValuable> notbetweenValues = new ArrayList<BValuable>();
	private List<BValuable> inValues = new ArrayList<BValuable>();
	private List<BValuable> notinValues = new ArrayList<BValuable>();

	public VarValueCheckLogic(BPath parent, BValuable var, BVariable target) {
		super(parent, var, target);
	}

	@Override
	public String getName() {
		if (target == null) {
			return "";
		}
		return target.getName() + "値チェック";
	}

	@Override
	public String getDesc() {
		if (target == null) {
			return "";
		}
		return target.getName() + "の値チェック";
	}

	public BLogiker getSelected() {
		return selected;
	}

	public BValuable getValue() {
		return value;
	}

	public List<BValuable> getBetweenValues() {
		return betweenValues;
	}

	public List<BValuable> getNotbetweenValues() {
		return notbetweenValues;
	}

	public List<BValuable> getInValues() {
		return inValues;
	}

	public List<BValuable> getNotinValues() {
		return notinValues;
	}

	@Override
	protected BExpression getExpression(ITableSql tsql) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BExpression ex = view.createExpression();
		ex.setExLeft(var);
		ex.setExMiddle(selected);
		if (value == null) {
			value = CodecUtils.getNullValue();
		}

		if (tsql != null) {
			if (!selected.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
				tsql.getInfo().setEqualsExceptedExpression();
			}
		}

		BVariable var = CodecUtils.getNullValue();
		var.setLogicName("VALUE_LIST");

		if (selected.equals(BLogiker.BETWEE)) {
			var.addUserAttribute("VALUE_LIST", this.betweenValues);
			ex.setExRight(var);
		} else if (selected.equals(BLogiker.NOTBETWEE)) {
			var.addUserAttribute("VALUE_LIST", this.notbetweenValues);
			ex.setExRight(var);
		} else if (selected.equals(BLogiker.IN)) {
			ex.setExRight(var);
			var.addUserAttribute("VALUE_LIST", this.inValues);
		} else if (selected.equals(BLogiker.NOTIN)) {
			var.addUserAttribute("VALUE_LIST", this.notinValues);
			ex.setExRight(var);
		} else {
			ex.setExRight(value);
		}

		return ex;
	}

	public JComponent getEditor() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);

		JPanel editor = new JPanel();
		editor.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		editor.setLayout(layout);

		JLabel label = new JLabel(target.getName());
		panel.add(label);

		JComboBox<BLogiker> box = new JComboBox<BLogiker>();

		BLogiker[] values = BLogiker.values();
		for (BLogiker logiker : values) {
			if (logiker.getType().equals(BLogiker.TYPE_BOOLEAN)) {
				box.addItem(logiker);
				if (logiker.equals(BLogiker.EQUAL)) {
					box.setSelectedItem(logiker);
				}
			}
		}
		box.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				BLogiker logiker = (BLogiker) e.getItem();
				selected = logiker;
				addTextEditor(editor);

			}

		});
		panel.add(box);

		this.addTextEditor(editor);

		panel.add(editor);

		return panel;
	}

	public void addTextEditor(JPanel panel) {
		panel.removeAll();

		if (selected.equals(BLogiker.BETWEE)) {
			makeEditor(panel, selected.getLogicName(), 0);
			makeEditor(panel, selected.getLogicName(), 1);
		} else if (selected.equals(BLogiker.NOTBETWEE)) {
			makeEditor(panel, selected.getLogicName(), 0);
			makeEditor(panel, selected.getLogicName(), 1);
		} else if (selected.equals(BLogiker.IN)) {
			makeEditor(panel, selected.getLogicName(), 0);
		} else if (selected.equals(BLogiker.NOTIN)) {
			makeEditor(panel, selected.getLogicName(), 0);
		} else {
			makeEditor(panel, selected.getLogicName(), 0);
		}
		panel.updateUI();
	}

	private void makeEditor(JPanel panel, String name, int index) {
		if (name.equals(BLogiker.BETWEE.getLogicName())) {
			betweenValues.add(CodecUtils.getNullValue());
		} else {
			betweenValues.clear();
		}

		if (name.equals(BLogiker.NOTBETWEE.getLogicName())) {
			notbetweenValues.add(CodecUtils.getNullValue());
		} else {
			notbetweenValues.clear();
		}

		if (name.equals(BLogiker.IN.getLogicName())) {
			inValues.add(CodecUtils.getNullValue());
		} else {
			inValues.clear();
		}

		if (name.equals(BLogiker.NOTIN.getLogicName())) {
			notinValues.add(CodecUtils.getNullValue());
		} else {
			notinValues.clear();
		}

		ValueChangeListener listener = new ValueChangeListener() {
			@Override
			public void changed(String name, int index, BValuable v) {
				value = v;
				if (name.equals(BLogiker.BETWEE.getLogicName())) {
					betweenValues.remove(index);
					betweenValues.add(index, value);
				} else if (name.equals(BLogiker.NOTBETWEE.getLogicName())) {
					notbetweenValues.remove(index);
					notbetweenValues.add(index, value);
				} else if (name.equals(BLogiker.IN.getLogicName())) {
					inValues.remove(index);
					inValues.add(index, value);

					boolean filled = true;
					for (BValuable bv : inValues) {
						if (bv.getBClass().getLogicName().equals(BClass.NULL)) {
							filled = false;
							break;
						}
					}
					if (filled) {
						makeEditor(panel, name, inValues.size());
						panel.updateUI();
					}
				} else if (name.equals(BLogiker.NOTIN.getLogicName())) {
					notinValues.remove(index);
					notinValues.add(index, value);

					boolean filled = true;
					for (BValuable bv : notinValues) {
						if (bv.getBClass().getLogicName().equals(BClass.NULL)) {
							filled = false;
							break;
						}
					}
					if (filled) {
						makeEditor(panel, name, notinValues.size());
						panel.updateUI();
					}
				}
			}
		};
		ValueEditor text = new ValueEditor(name, index, listener);
		panel.add(text);

	}

}
