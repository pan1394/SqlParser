package com.linkstec.bee.UI.spective.basic.logic.model.var;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;

public class ValueEditor extends JTextField implements DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 886688650190367088L;

	private int index = 0;
	private BValuable value;
	private ValueChangeListener listener;

	private String messageID;
	private boolean fireEvent = true;

	public ValueEditor(String messageID, int index, ValueChangeListener listener) {
		super("\"@TODO value\"");
		this.index = index;
		this.messageID = messageID;
		this.listener = listener;
		int s = BeeUIUtils.getDefaultFontSize();
		this.setPreferredSize(new Dimension(s * 5, (int) (s * 1.5)));
		this.setBackground(Color.YELLOW.brighter());
		this.getDocument().addDocumentListener(this);
		this.setTransferHandler(new ValueTransferHandler());
	}

	public boolean isFireEvent() {
		return fireEvent;
	}

	public void setFireEvent(boolean fireEvent) {
		this.fireEvent = fireEvent;
	}

	public void setListener(ValueChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	private void documentChanged() {
		if (this.fireEvent) {
			String s = this.getText();
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable var = view.createVariable();
			var.setBClass(CodecUtils.BString());
			CodecUtils.setVarValue(var, s);
			value = var;
			if (this.listener != null) {
				listener.changed(messageID, index, value);
			}
			this.setToolTipText(s);
		}
	}

	public int getIndex() {
		return this.index;
	}

	public BValuable getValue() {
		return this.value;
	}

	public void setValue(BValuable value) {
		this.value = value;
		if (this.listener != null) {
			listener.changed(messageID, index, value);
		}
	}

}