package com.linkstec.bee.UI.spective.basic.logic.model.provider;

import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.view.ObjectMark;
import com.linkstec.bee.UI.spective.basic.logic.LogicMenuMessage;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BLogicUtils;
import com.linkstec.bee.UI.spective.basic.logic.model.var.DraggableMessagePanel;
import com.linkstec.bee.UI.spective.basic.logic.model.var.LogMessagePanel;
import com.linkstec.bee.UI.spective.basic.logic.model.var.MessageDropListener;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ValueChangeListener;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BUtilMethod;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.model.mxICell;

public class ProviderContentsLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8698878038139994194L;
	private BMethod method;
	private static transient IPatternCreator view = PatternCreatorFactory.createView();

	// for message editor
	private LogicMenuMessage menuMessage;
	private BVariable param;
	private Hashtable<Integer, BValuable> values = new Hashtable<Integer, BValuable>();

	public ProviderContentsLogic(BPath parent, BUtilMethod util) {
		super(parent, null);

		BLogicNode node = new BLogicNode(this);
		this.getPath().setCell(node);

		this.method = (BMethod) ObjectFileUtils.deepCopy(util.getBMethod());
	}

	@Override
	public String getName() {
		if (method != null) {
			return method.getName();
		}
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.SHEET_LOGIC_ICON;
	}

	@Override
	public String getDesc() {
		if (method != null) {
			return method.getName();
		}
		return null;
	}

	@Override
	public List<BLogicUnit> createUnit() {
		if (this.method != null) {
			BMethod m = (BMethod) ObjectFileUtils.deepCopy(method);
			return m.getLogicBody().getUnits();
		}
		return null;
	}

	public void setValue(BParameter parameter, BValuable value) {

		ObjectMark mark = (ObjectMark) value.getUserObject();
		ObjectMark old = (ObjectMark) parameter.getUserObject();
		mark.setId(old.getId());

		BLogicBody body = method.getLogicBody();

		BLogicUtils.replaceValue((mxICell) body, value);

	}

	@Override
	public JComponent getEditor() {

		if (this.method == null) {
			return null;
		}

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(box);

		List<BParameter> parameters = method.getParameter();

		if (parameters != null && !parameters.isEmpty()) {
			boolean message = false;
			if (parameters.size() == 2) {
				BParameter first = parameters.get(0);
				BParameter second = parameters.get(1);

				if (this.param == null) {
					param = view.createVariable();
					BClass bclass = CodecUtils.BString().cloneAll();
					bclass.setArrayPressentClass(CodecUtils.BString());
					param.setBClass(bclass);
				} else {
					param = (BVariable) param.cloneAll();
					Enumeration<Integer> keys = this.values.keys();
					while (keys.hasMoreElements()) {
						int key = keys.nextElement();
						BValuable value = values.get(key);
						param.replaceInitValue(key, value);
					}
					setValue(second, param);
				}
				List<BValuable> inits = param.getInitValues();

				if (first.getName().equals("{messageID}") && second.getName().equals("{messageParameter}")) {
					message = true;

					ValueChangeListener valueListener = new ValueChangeListener() {

						@Override
						public void changed(String messageID, int index, BValuable value) {
							param.replaceInitValue(index, value);
							BLogicProvider provider = getPath().getProvider();
							if (provider == null) {
								BPath path = getPath();
								BActionModel action = (BActionModel) path.getAction();
								provider = path.getProvider();
								provider.getProperties().setCurrentDeclearedClass(action.getDeclearedClass());
							}

							BValuable idValue = provider.getMessageID(messageID);
							if (idValue == null) {
								BVariable fvar = view.createVariable();
								fvar.setBClass(CodecUtils.BString());
								fvar.setLogicName("\"" + messageID + "\"");
								fvar.setName("\"" + messageID + "\"");
								idValue = fvar;
							}
							values.put(index, value);

							setValue(first, idValue);

							setValue(second, param);

						}

					};

					MessageDropListener dropListener = new MessageDropListener() {

						@Override
						public void dropped(LogMessagePanel panel) {
							menuMessage = panel.getMessage();
							int size = panel.getParameterSize();
							param.clearInitValues();
							for (int i = 0; i < size; i++) {
								BVariable var = view.createVariable();
								var.setBClass(CodecUtils.BString());
								var.setLogicName("\"TODO\"");
								var.setName("\"TODO\"");
								BValuable defined = values.get(i);
								if (defined == null) {
									param.addInitValue(var);
								} else {
									param.addInitValue(defined);
								}
								BPath path = getPath();
								if (path.getParent() == null) {
									path.setParent(Application.getInstance().getBasicSpective().getSelection()
											.getActionPath());
								}
								BLogicProvider provider = path.getProvider();

								BActionModel action = (BActionModel) path.getAction();

								provider.getProperties()
										.setCurrentDeclearedClass(BasicGenUtils.createClass(action, path.getProject()));

								BValuable idValue = provider.getMessageID(panel.getID());
								if (idValue == null) {
									BVariable fvar = view.createVariable();
									fvar.setBClass(CodecUtils.BString());
									fvar.setLogicName("\"" + panel.getID() + "\"");
									fvar.setName("\"" + panel.getID() + "\"");
									idValue = fvar;
								}

								setValue(first, idValue);
								setValue(second, param);
							}
						}

					};
					DraggableMessagePanel messag = new DraggableMessagePanel(dropListener);
					if (menuMessage == null) {
						BVariable fvar = view.createVariable();
						fvar.setBClass(CodecUtils.BString());
						fvar.setLogicName("\"TODO\"");
						fvar.setName("\"TODO");
						setValue(first, fvar);
					} else {
						messag.setLogMessage(menuMessage);
					}
					if (messag.getLogMessagePanel() != null) {
						messag.getLogMessagePanel().setParameters(values);
					}

					messag.setListener(valueListener);

					panel.add(messag);
				}
			}

			if (message) {
				return panel;
			}

			for (BParameter type : parameters) {
				JPanel row = new JPanel();

				row.setOpaque(false);
				FlowLayout flow = new FlowLayout();
				flow.setAlignment(FlowLayout.LEFT);
				row.setLayout(flow);

				JLabel space = new JLabel("    ");
				row.add(space);

				String name = type.getName();
				if (name != null) {
					JLabel label = new JLabel(name);
					label.setIcon(BeeConstants.PARAMETERS_ICON);
					row.add(label);
					JTextField text = new JTextField();
					text.setText("\"@TODO value\"");
					row.add(text);
					panel.add(row);

					BVariable var = view.createVariable();
					var.setBClass(type.getBClass());
					var.setLogicName(text.getText());
					var.setName(text.getText());

					setValue(type, var);

					text.getDocument().addDocumentListener(new DocumentListener() {

						@Override
						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							changed();
						}

						private void changed() {
							BVariable var = view.createVariable();
							var.setBClass(type.getBClass());
							var.setLogicName(text.getText());
							var.setName(text.getText());
							setValue(type, var);
						}
					});
				}
			}
		}

		return panel;
	}

	public String toString() {
		return this.getName();
	}
}
