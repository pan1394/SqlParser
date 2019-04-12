package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BExpression;

public class ComputerLogic extends ExpressionLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8852284761570017643L;

	private BExpression expression;

	public ComputerLogic(BPath parent) {
		super(parent, null);
	}

	@Override
	public String getName() {
		return "複雑計算";
	}

	@Override
	public String getDesc() {

		return BValueUtils.createValuable(expression, false);

	}

	public BExpression getExpression() {
		return expression;
	}

	public void setExpression(BExpression expression) {
		this.expression = expression;
	}

	@Override
	public BValuable getExpression(ITableSql tsql) {
		return this.expression;
	}

	@Override
	public JComponent getEditor() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel();
		label.setText("<html>" + BValueUtils.createValuable(expression, false) + "</html>");
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

}
