package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BConditionUnit;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.fw.logic.BMultiCondition;

public class VarSizeCheckLogic extends VarCheckLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6864333849280262640L;

	public VarSizeCheckLogic(BPath parent, BValuable var, BVariable target) {
		super(parent, var, target);
	}

	private BLogiker selected = BLogiker.EQUAL;
	private String value = "0";

	@Override
	public String getName() {
		return target.getName() + "桁数チェック";
	}

	@Override
	public String getDesc() {
		if (target == null) {
			return "";
		}
		return target.getName() + "の桁数=" + value;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = super.createUnit();
		if (value != null) {

			BMultiCondition ifs = (BMultiCondition) units.get(0);
			BConditionUnit condition = ifs.getConditionUnits().get(0);
			BExpression ex = (BExpression) condition.getCondition();

			ex.setExMiddle(selected);
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable v = view.createVariable();
			v.setLogicName(value);
			v.setName(value);
			v.setBClass(CodecUtils.BInt());
			ex.setExRight(v);

			BInvoker invoker = view.createMethodInvoker();
			invoker.setInvokeParent(var);
			BMethod method = view.createMethod();
			method.setLogicName("length");
			method.setName("桁数");
			invoker.setInvokeChild(method);
			ex.setExLeft(invoker);

		}
		return units;
	}

	public JComponent getEditor() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);

		JLabel label = new JLabel(target.getName() + "の桁数");
		panel.add(label);

		JComboBox<BLogiker> box = new JComboBox<BLogiker>();
		BLogiker[] values = BLogiker.values();
		for (BLogiker logiker : values) {
			if (logiker.equals(BLogiker.EQUAL) || logiker.equals(BLogiker.NOTQUEAL)
					|| logiker.equals(BLogiker.GREATTHAN) || logiker.equals(BLogiker.GREATTHANEQUAL)
					|| logiker.equals(BLogiker.LESSTHAN) || logiker.equals(BLogiker.LESSTHANEQUAL)) {
				box.addItem(logiker);
			}

			if (logiker.equals(BLogiker.EQUAL)) {
				box.setSelectedItem(logiker);
			}
		}
		box.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				BLogiker logiker = (BLogiker) e.getItem();
				selected = logiker;
			}

		});

		panel.add(box);

		JTextField text = new JTextField();
		text.setText("0");
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				value = text.getText();
			}
		});

		panel.add(text);

		return panel;
	}

}
