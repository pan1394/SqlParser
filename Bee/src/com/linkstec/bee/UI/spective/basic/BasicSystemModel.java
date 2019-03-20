package com.linkstec.bee.UI.spective.basic;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.basic.properties.BasicComponentListModel;
import com.linkstec.bee.UI.spective.basic.properties.BasicComponentListSheet;
import com.linkstec.bee.UI.spective.basic.properties.BasicPropertyModel;
import com.linkstec.bee.UI.spective.basic.properties.BasicPropertySheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicSystemModel implements Serializable, IBasicSubsystemOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1528631581866446604L;
	private String projectName;
	private List<SubSystem> subs = new ArrayList<SubSystem>();
	private Hashtable<Object, Object> properties = new Hashtable<Object, Object>();
	private String rootPath;

	public BasicSystemModel(BProject project) {
		this.projectName = project.getName();
		this.rootPath = project.getRootPath();
	}

	public Hashtable<Object, Object> getProperties() {
		return properties;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<SubSystem> getSubs() {
		return subs;
	}

	public void setSubs(List<SubSystem> subs) {
		this.subs = subs;
	}

	public String toString() {
		return this.projectName;
	}

	public File save() {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		BProject project = config.getProject(projectName);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					File f = new File(project.getRootPath() + File.separator + "basic.conf");
					ObjectFileUtils.writeObject(f, BasicSystemModel.this);

				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "保存が失敗しました。", "エラー", JOptionPane.OK_OPTION);
					e1.printStackTrace();
				}

			}

		}).start();
		return null;
	}

	@Override
	public BEditor getListSheet(BProject project) {
		return this.getSheet(project);
	}

	public BasicPropertySheet getSheet(BProject project) {
		BasicPropertyModel model = new BasicPropertyModel(null, this);
		BasicPropertySheet sheet = new BasicPropertySheet(model, project);
		return sheet;
	}

	public static BasicSystemModel load(BProject project) {
		File f = new File(project.getRootPath() + File.separator + "basic.conf");
		try {
			Object obj = ObjectFileUtils.readObject(f);
			if (obj == null) {
				return null;
			}
			return (BasicSystemModel) obj;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static class SubSystem implements Serializable, IBasicSubsystemOwner {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5633468388497177407L;
		private String name;
		private String logicName;

		private String desc;
		private String id;
		private Hashtable<Object, Object> properties = new Hashtable<Object, Object>();
		private List<SubSystem> subs = new ArrayList<SubSystem>();
		private IBasicSubsystemOwner owner;

		public SubSystem(IBasicSubsystemOwner owner) {
			this.owner = owner;

		}

		public List<SubSystem> getSubs() {
			return subs;
		}

		public void setSubs(List<SubSystem> subs) {
			this.subs = subs;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public void setProperties(Hashtable<Object, Object> properties) {
			this.properties = properties;
		}

		public Hashtable<Object, Object> getProperties() {
			return properties;
		}

		public IBasicSubsystemOwner getOwner() {
			return owner;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLogicName() {
			return logicName;
		}

		public void setLogicName(String logicName) {
			this.logicName = logicName;
		}

		public String toString() {
			return this.name;
		}

		public BasicComponentListSheet getSheet(BProject project) {
			BasicComponentListModel model = new BasicComponentListModel(null, this, project);
			model.setLogicName(logicName);
			model.setName(name);
			BasicComponentListSheet sheet = new BasicComponentListSheet(model, project);
			return sheet;
		}

		public String getDictionaryPath(BProject project) {
			return project.getRootPath() + File.separator + "basic" + File.separator + this.getLogicName() + File.separator + "dictionary.d";
		}

		@Override
		public BEditor getListSheet(BProject project) {
			BasicPropertyModel model = new BasicPropertyModel(null, this);
			BasicPropertySheet sheet = new BasicPropertySheet(model, project);
			return sheet;
		}

		@Override
		public File save() {
			return owner.save();
		}

		@Override
		public String getProjectName() {
			return this.owner.getProjectName();
		}

		public String getPath() {
			return this.owner.getPath() + File.separator + this.getLogicName();
		}
	}

	@Override
	public String getName() {
		return this.projectName;
	}

	@Override
	public String getLogicName() {
		return this.projectName;
	}

	@Override
	public IBasicSubsystemOwner getOwner() {
		return null;
	}

	@Override
	public String getPath() {
		return this.rootPath + File.separator + "basic";
	}

}
