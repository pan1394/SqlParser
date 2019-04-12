package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BLogiker;

/**
 * used by single expression
 * 
 * @author linkage
 *
 */
public class ExpressionLogic extends JudgeLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5035135765849009793L;

	public ExpressionLogic(BPath parent, List<Object> list) {
		super(parent);
		this.list = list;
		BLogicNode node = new BLogicNode(this);
		this.getPath().setCell(node);
	}

	@Override
	public String getName() {
		return "計算処理";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.P_EXPRESSION_ICON;
	}

	@Override
	public String getDesc() {
		return BValueUtils.createValuable(this.getExpression(null), false);
	}

	@Override
	public BValuable getExpression(ITableSql tsql) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BExpression ex = view.createExpression();

		int size = list.size();

		for (int i = 0; i < size; i++) {
			BLogiker l = logikers.get(i);
			BValuable value = (BValuable) list.get(i);

			if (l != null) {
				ex.setExMiddle(l);
				ex.setExRight((BValuable) value.cloneAll());

				if (tsql != null) {
					if (!l.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
						tsql.getInfo().setEqualsExceptedExpression();
					}
				}

				BExpression old = ex;

				ex = view.createExpression();
				ex.setExLeft((BValuable) old.cloneAll());
			} else {
				ex.setExLeft((BValuable) value.cloneAll());
			}
		}
		if (ex.getExRight().getBClass() == null) {
			return ex.getExLeft();
		}
		return ex;
	}

	private Hashtable<Integer, BLogiker> logikers = new Hashtable<Integer, BLogiker>();

	@Override
	public JComponent getEditor() {
		if (list == null) {
			return null;
		}
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);

		int index = 0;
		for (Object invoker : list) {

			if (index > 0) {

				int target = index;
				JComboBox<BLogiker> box = new JComboBox<BLogiker>();
				BLogiker[] values = BLogiker.values();
				for (BLogiker logiker : values) {
					box.addItem(logiker);
					if (logiker.equals(BLogiker.EQUAL)) {
						box.setSelectedItem(logiker);
					}
				}
				logikers.put(1, BLogiker.EQUAL);
				box.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						BLogiker logiker = (BLogiker) e.getItem();
						logikers.put(target, logiker);
					}

				});
				panel.add(box);
			}

			BValuable in = (BValuable) invoker;
			if (in.getUserAttribute("FIXED") != null) {
				ValueEditor editor = new ValueEditor("", index, new ValueChangeListener() {

					@Override
					public void changed(String messageID, int index, BValuable value) {
						list.remove(index);
						list.add(index, value);
					}

				});
				panel.add(editor);
			} else {
				JLabel label = new JLabel(BValueUtils.createValuable(in, false));
				panel.add(label);
			}

			index++;
		}

		return panel;
	}

	public Hashtable<Integer, BLogiker> getLogikers() {
		return logikers;
	}

}
