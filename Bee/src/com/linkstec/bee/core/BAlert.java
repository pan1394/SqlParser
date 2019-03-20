package com.linkstec.bee.core;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.tip.TipAction;
import com.linkstec.bee.core.fw.BAlertor;

public class BAlert implements BAlertor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5270524888179485287L;
	private String message;
	private transient List<TipAction> actions = new ArrayList<TipAction>();
	private String type = TYPE_WARNING;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<TipAction> getActions() {
		return actions;
	}

	public void setActions(List<TipAction> actions) {
		this.actions = actions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
