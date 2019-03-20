package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linkstec.bee.UI.spective.basic.logic.LogicMenuMessage;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BMethod;

public class LogMessagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1326486956184327692L;

	private String id;
	private int parameterSize = 0;
	private List<String> cells = new ArrayList<String>();
	private ValueChangeListener listener;
	private LogicMenuMessage menuMessage;

	public LogMessagePanel(LogicMenuMessage menuMessage, ValueChangeListener listener) {
		this.menuMessage = menuMessage;
		this.id = menuMessage.getId();
		this.listener = listener;
		this.spliteMessage(menuMessage.getValue());
		this.setOpaque(false);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		this.setLayout(flow);

		this.makeEditor();
	}

	public void setListener(ValueChangeListener listener) {
		this.listener = listener;

		int count = this.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponent(i);
			if (comp instanceof ValueEditor) {
				ValueEditor text = (ValueEditor) comp;
				text.setListener(listener);
			}
		}
	}

	public LogicMenuMessage getMessage() {
		return this.menuMessage;
	}

	private void spliteMessage(String message) {
		int left = message.indexOf('{');
		if (left > 0) {
			int right = message.indexOf('}');
			if (right > 0) {
				String s1 = message.substring(0, left);
				cells.add(s1);
				cells.add("{}");
				parameterSize++;
				String s2 = message.substring(right + 1, message.length());
				spliteMessage(s2);
			}
		} else {
			cells.add(message);
		}
	}

	public int getParameterSize() {
		return parameterSize;
	}

	public String getID() {
		return this.id;
	}

	public void makeEditor() {
		int index = 0;
		for (String s : cells) {
			if (s.equals("{}")) {
				ValueEditor text = new ValueEditor(id, index, this.listener);
				this.add(text);
				index++;
			} else {
				JLabel label = new JLabel(s);
				this.add(label);
			}
		}

	}

	public List<BValuable> getParameters() {
		List<BValuable> parameters = new ArrayList<BValuable>();

		int count = this.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponent(i);
			if (comp instanceof ValueEditor) {
				ValueEditor text = (ValueEditor) comp;
				BValuable value = text.getValue();
				parameters.add(value);
			}
		}

		return parameters;
	}

	public void setParameters(Hashtable<Integer, BValuable> values) {
		int count = this.getComponentCount();
		int index = 0;
		for (int i = 0; i < count; i++) {
			Component comp = this.getComponent(i);
			if (comp instanceof ValueEditor) {
				ValueEditor text = (ValueEditor) comp;
				BValuable cell = values.get(index);

				if (cell instanceof BInvoker) {
					BInvoker invoker = (BInvoker) cell;
					BValuable v = invoker.getInvokeChild();

					if (v instanceof BVariable) {
						BVariable var = (BVariable) v;
						text.setText(var.getName());
					} else if (v instanceof BMethod) {
						BMethod method = (BMethod) v;
						text.setText(method.getName());
					}

					text.setValue(invoker);
					text.updateUI();
				} else if (cell instanceof BVariable) {
					BVariable var = (BVariable) cell;
					text.setText(var.getName());
					text.setValue(var);
					text.updateUI();
				}
				index++;

			}
		}
	}

}