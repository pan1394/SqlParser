package com.linkstec.bee.UI.spective.basic.data;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.IBasicSubsystemOwner;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.properties.BasicComponentListModel;
import com.linkstec.bee.UI.spective.basic.properties.BasicComponentListSheet;
import com.linkstec.bee.UI.spective.basic.properties.BasicDataDictionary;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicDataActions {

	public static class BasicAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6452339288773273578L;
		protected BProject project;

		public BasicAction(BProject project) {
			this.project = project;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	public static class BasicSystemAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;
		private IBasicSubsystemOwner model;

		public BasicSystemAction(BProject project, IBasicSubsystemOwner model) {
			super(project);
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BEditor sheet = model.getListSheet(project);
			Application.getInstance().getBasicSpective().getWorkspace().addEditor(sheet);
		}

	}

	public static class UpdateAllDataAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;

		public UpdateAllDataAction(BProject project) {
			super(project);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BasicSystemModel model = BasicSystemModel.load(project);
			Application.getInstance().getBasicSpective().updateDataResource(project, model);
		}

	}

	public static class SubSystemEditAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;
		private SubSystem model;

		public SubSystemEditAction(BProject project, SubSystem model) {
			super(project);
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BasicComponentListSheet sheet = model.getSheet(project);
			Application.getInstance().getBasicSpective().getWorkspace().addEditor(sheet);
		}

	}

	public static class AddNewAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;
		private SubSystem model;

		public AddNewAction(BProject project, SubSystem model) {
			super(project);
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BLogicEditActions.addNewFlow(project, model);
		}

	}

	public static class DictionaryAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;
		private SubSystem model;
		private BasicComponentModel data;

		public DictionaryAction(BProject project, SubSystem model, BasicComponentModel data) {
			super(project);
			this.model = model;
			this.data = data;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BasicDataDictionary d = BLogicEditActions.openDictionary(project, model);
			if (data == null) {
				List<BasicComponentModel> list = BasicComponentListModel.getComponents(model);
				for (BasicComponentModel bc : list) {
					d.addData(bc);
				}
			} else {
				d.addData(data);
			}
		}

	}

	public static class DataEditAction extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950245120270916733L;
		private SubSystem model;
		private BasicDataModel data;

		public DataEditAction(BProject project, SubSystem model, BasicDataModel data) {
			super(project);
			this.model = model;
			this.data = data;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BasicDataDictionary d = BLogicEditActions.openDictionary(project, model);
			d.addData(data);
		}

	}
}
