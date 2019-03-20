package com.linkstec.bee.UI.editor.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;

public abstract class BaseAction implements EditAction, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4979285984284410825L;
	protected List<BAction> actions = new ArrayList<BAction>();

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.ADD_ICON;
	}

	@Override
	public List<BAction> getActions() {
		return actions;
	}

	public void addAction(String name, BCall call) {
		BAction addInterface = new BAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7895464399402388563L;

			@Override
			public String getName() {
				return name;
			}

			@Override
			public void act() {

				call.call();

				BEditor editor = Application.getInstance().getCurrentEditor();
				if (editor instanceof BeeGraphSheet) {
					editor.setModified(true);
				}
			}

		};
		actions.add(addInterface);
	}

	public void addAction(String name, BCallback call, Object obj) {
		BAction addInterface = new BAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7895464399402388563L;

			@Override
			public String getName() {
				return name;
			}

			@Override
			public void act() {

				call.call(obj);

				BEditor editor = Application.getInstance().getCurrentEditor();
				if (editor instanceof BeeGraphSheet) {
					editor.setModified(true);
				}
			}

		};
		actions.add(addInterface);
	}
}
