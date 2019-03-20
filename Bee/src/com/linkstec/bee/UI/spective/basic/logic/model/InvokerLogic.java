package com.linkstec.bee.UI.spective.basic.logic.model;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ValueChangeListener;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ValueEditor;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class InvokerLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2214065396803158901L;

	private BMethod method;
	private BClass invokeParentBClass;
	private Hashtable<String, ValueInfo> params = new Hashtable<String, ValueInfo>();

	public InvokerLogic(BPath parent, ILogicCell cell, BMethod method, BClass invokeParentBClass) {
		super(parent, cell);
		this.method = method;
		this.invokeParentBClass = invokeParentBClass;
	}

	@Override
	public String getName() {
		return this.method.getName() + "を呼び出す";
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.METHOD_ICON;
	}

	@Override
	public String getDesc() {
		return this.getName();
	}

	@Override
	public JComponent getEditor() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		List<BParameter> parameters = method.getParameter();
		for (int i = 0; i < parameters.size(); i++) {
			BParameter param = parameters.get(i);
			this.makeParameterEditor(panel, param, i);
		}
		return panel;
	}

	private void makeParameterEditor(JPanel panel, BParameter param, int index) {
		BClass bclass = param.getBClass();
		// TODO CodecUtils.isData
		if (bclass.isData()) {

			List<BAssignment> vars = bclass.getVariables();
			int i = 0;
			for (BAssignment var : vars) {
				BParameter left = var.getLeft();
				if (!left.getLogicName().equals("serialVersionUID")) {
					Parameter p = new Parameter(index + "", i, left, params);
					panel.add(p);
					i++;
				}
			}

		} else {
			Parameter p = new Parameter("N", index, param, params);
			panel.add(p);
		}
	}

	private void makeParameter(BInvoker invoker, List<BLogicUnit> units) {
		Enumeration<String> keys = params.keys();
		BMethod method = (BMethod) invoker.getInvokeChild();
		Hashtable<Integer, BObject> values = new Hashtable<Integer, BObject>();
		IPatternCreator view = PatternCreatorFactory.createView();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			ValueInfo value = params.get(key);
			String[] ss = key.split("_");
			String parentNumber = ss[0];
			String number = ss[1];

			if (!parentNumber.equals("N")) {
				int n = Integer.parseInt(parentNumber);

				BAssignment assign = (BAssignment) values.get(n);
				if (assign == null) {
					BClass bclass = method.getParameter().get(n).getBClass();
					assign = BasicGenUtils.createInstance(bclass);

					BNote note = view.createComment();
					note.setNote("共通関数「" + method.getName() + "」のパラメータを作成する");
					units.add(note);
					units.add(assign);
					values.put(n, assign);
				}

				if (!value.getValue().getBClass().getLogicName().equals(BClass.NULL)) {

					BInvoker bin = view.createMethodInvoker();
					BParameter left = assign.getLeft();
					bin.setInvokeParent((BValuable) left.cloneAll());
					BParameter p = value.getParam();
					bin.setInvokeChild((BValuable) p.cloneAll());
					bin.addParameter(value.getValue());

					BNote note = view.createComment();
					note.setNote("パラメータの「" + p.getName().trim() + "」に値を設定する");
					units.add(note);

					units.add(bin);
				}

			} else {
				int n = Integer.parseInt(number);
				values.put(n, value.getValue());
			}
		}

		Enumeration<Integer> valueNumers = values.keys();
		List<BObject> orderedValue = new ArrayList<BObject>();
		while (valueNumers.hasMoreElements()) {
			int index = valueNumers.nextElement();
			BObject object = values.get(index);
			orderedValue.add(index, object);
		}
		for (BObject obj : orderedValue) {
			if (obj instanceof BValuable) {
				BValuable bv = (BValuable) obj;
				invoker.addParameter(bv);
			} else if (obj instanceof BAssignment) {
				BAssignment assign = (BAssignment) obj;
				invoker.addParameter((BValuable) assign.getLeft().cloneAll());
			}
		}
	}

	@Override
	public List<BLogicUnit> createUnit() {
		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		BLogicProvider provider = this.getPath().getProvider();

		// invoker
		BInvoker invoker = view.createMethodInvoker();

		BValuable parent = provider.getInvokeParent(invokeParentBClass);
		if (parent != null) {
			invoker.setInvokeParent(parent);
			invoker.setInvokeChild(method);

			this.makeParameter(invoker, units);

			List<BLogicUnit> punits = provider.beforeMethodInvoker(invoker);
			if (punits != null) {
				units.addAll(punits);
			}

			BAssignment assign = BasicGenUtils.createInstanceWidthValue(method.getReturn().getBClass(), invoker);
			assign.getLeft().setName(method.getName() + "の処理結果");

			this.addMark(assign.getLeft());

			BNote note = view.createComment();
			note.setNote(method.getName() + "を呼び出す");
			note.addUserAttribute("DESC", note.getNote());
			units.add(note);

			units.add(assign);
		}

		return units;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();
		IPatternCreator view = PatternCreatorFactory.createView();
		BParameter left = view.createParameter();
		BClass bclass = method.getReturn().getBClass();
		left.setBClass(bclass);
		left.setLogicName(BasicNaming.getVarName(bclass));
		left.setName(method.getName() + "の処理結果");

		this.addMark(left);
		outputs.add(left);
		return outputs;
	}

	public static class Parameter extends JPanel implements ValueChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4962439467051066509L;
		private int s = BeeUIUtils.getDefaultFontSize();
		private Hashtable<String, ValueInfo> params;
		private BParameter param;

		public Parameter(String parentNumber, int index, BParameter param, Hashtable<String, ValueInfo> params) {
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			this.setLayout(flow);
			this.params = params;
			this.param = param;

			this.setOpaque(false);

			String storedName = parentNumber + "_" + index;
			ValueInfo value = this.params.get(storedName);
			if (value == null) {
				BVariable var = CodecUtils.getNullValue();
				var.setName("");

				ValueInfo info = new ValueInfo();
				info.setParam(param);
				info.setValue(var);
				this.params.put(storedName, info);
			}

			index++;

			JLabel No = new JLabel(index + "");
			No.setPreferredSize(new Dimension(s * 2, s * 2));
			this.add(No);
			JTextField name = new JTextField(param.getName());
			name.setEditable(false);
			name.setOpaque(false);
			name.setBorder(null);
			name.setPreferredSize(new Dimension(s * 10, s * 2));
			name.setToolTipText(name + "\r\n" + param.getLogicName() + "\r\n" + param.getBClass().getQualifiedName());
			this.add(name);

			index--;

			ValueEditor editor = new ValueEditor(parentNumber, index, null);
			if (value != null) {
				editor.setValue(value.getValue());

				if (!value.getValue().getBClass().getLogicName().equals(BClass.NULL)) {
					editor.setFireEvent(false);
					editor.setText(BValueUtils.createValuable(value.getValue(), false));
					editor.setFireEvent(true);
				}

			}
			this.add(editor);

			editor.setListener(this);
		}

		@Override
		public void changed(String parentNumber, int index, BValuable value) {
			String name = parentNumber + "_" + index;
			ValueInfo info = new ValueInfo();
			info.setParam(param);
			info.setValue(value);
			params.put(name, info);
		}
	}

	public static class ValueInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2186266560037085325L;
		public BValuable value;
		private BParameter param;

		public BValuable getValue() {
			return value;
		}

		public void setValue(BValuable value) {
			this.value = value;
		}

		public BParameter getParam() {
			return param;
		}

		public void setParam(BParameter param) {
			this.param = param;
		}

	}

}
