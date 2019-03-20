package com.linkstec.bee.UI.spective.basic.config.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.decode.DecodeGen;
import com.linkstec.bee.core.codec.decode.IDecodeResult;
import com.linkstec.bee.core.codec.encode.JavaGen;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BProperties;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class ProviderManager {
	private ConfigModel model;

	public ProviderManager(BProject project) {
		model = ConfigModel.load(project);
	}

	public static BLogicProvider getProvider(BActionModel model, BProject project, boolean reload) {
		BProperties p = new BProperties() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3301292701936999643L;

			private List<BClass> list = new ArrayList<BClass>();

			private List<BClass> logics;

			private List<BClass> datas;

			private boolean reload;

			private Hashtable<Object, Object> userAttributes = new Hashtable<Object, Object>();

			public Hashtable<Object, Object> getUserAttributes() {
				return userAttributes;
			}

			public void setUserAttributes(Hashtable<Object, Object> userAttributes) {
				this.userAttributes = userAttributes;
			}

			public void addUserAttribute(Object key, Object value) {
				this.userAttributes.put(key, value);
			}

			public void removeUserAttribute(Object key) {
				this.userAttributes.remove(key);
			}

			public Object getUserAttribute(Object key) {
				if (this.userAttributes != null) {
					return this.userAttributes.get(key);
				} else {
					return null;
				}
			}

			@Override
			public int getLayerDepth() {
				return model.getActionDepth();
			}

			@Override
			public String getInputComponentLogicName() {
				return model.getInput().getLogicName();
			}

			@Override
			public String getSubsystemLogicName() {
				return model.getSubSystem().getLogicName();
			}

			@Override
			public int getActionType() {
				return model.getActionDepth();
			}

			@Override
			public String getInputComponentLocalName() {
				return model.getInput().getName();
			}

			@Override
			public String getSubsystemLocalName() {
				return model.getSubSystem().getLogicName();
			}

			@Override
			public BClass getBClass(String name) {
				return CodecUtils.getClassFromJavaClass(project, name);
			}

			@Override
			public BClass getBClass(Class<?> name) {
				return CodecUtils.getClassFromJavaClass(name, project);
			}

			@Override
			public BProject getProject() {
				Configuration config = Application.getInstance().getConfigSpective().getConfig();
				List<BProject> projects = config.getProjects();
				for (BProject p : projects) {
					if (p.getName().equals(model.getProcessModel().getProviderProject())) {
						return p;
					}
				}

				return null;
			}

			@Override
			public BClass getTemplate(Class<?> name) {

				return this.getTemplate(name.getName());
			}

			@Override
			public BClass createClass(String pack, String name) {
				BeeModel model = new BeeModel();
				model.setLogicName(pack);
				model.setPackage(name);
				return model;
			}

			@Override
			public void setClassList(List<BClass> list) {
				this.list = list;
			}

			@Override
			public List<BClass> getClassList() {
				return this.list;
			}

			@Override
			public Object copy(Object obj) {

				return ObjectFileUtils.deepCopy(obj);
			}

			@Override
			public void setCurrentDeclearedClass(BClass bclass) {
				if (bclass == null) {
					return;
				}
				Thread t = Thread.currentThread();
				if (t instanceof BeeThread) {
					BeeThread bee = (BeeThread) t;
					bee.addUserAttribute("CUREENT_CLASS", bclass);
				}
			}

			@Override
			public BClass getCurrentDeclearedClass() {
				Thread t = Thread.currentThread();
				if (t instanceof BeeThread) {
					BeeThread bee = (BeeThread) t;
					return (BClass) bee.getUserAttribute("CUREENT_CLASS");
				}
				return null;
			}

			@Override
			public void save(BClass bclass) {
				JavaGen gen = new JavaGen(project);
				List<BClass> bclasses = new ArrayList<BClass>();
				try {
					bclasses.add(bclass);
					gen.generate(bclasses);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

			@Override
			public BClass getTemplate(String name) {
				BProject project = this.getProject();
				String path = project.getSourcePath() + File.separator + name.replace('.', File.separatorChar)
						+ ".java";
				File file = new File(path);
				if (file.exists()) {
					DecodeGen gen = new DecodeGen();
					BookModel model = new BookModel();
					IDecodeResult result = gen.decodeByPath(path, model, project);
					BClass cls = result.getBeeModel();
					return cls;
				}
				return null;
			}

			@Override
			public boolean isReload() {
				if (this.reload) {
					return Application.getInstance().getBasicSpective().getSelection().isProviderReload();
				}
				return this.reload;

			}

			@Override
			public void setReload(boolean reload) {
				this.reload = reload;
			}

			@Override
			public List<BClass> getGenerableLogics() {
				return this.logics;
			}

			@Override
			public void setGenerableLogics(List<BClass> logics) {
				this.logics = logics;
			}

			@Override
			public List<BClass> getGenerableDatas() {
				return this.datas;
			}

			@Override
			public void setGenerableDatas(List<BClass> datas) {
				this.datas = datas;
			}

			@Override
			public void addThreadScopeAttribute(Object key, Object value) {
				if (key != null) {
					Thread t = Thread.currentThread();
					if (t instanceof BeeThread) {
						BeeThread bee = (BeeThread) t;
						bee.addUserAttribute(key, value);
					}
				}

			}

			@Override
			public Object getThreadScopeAttribute(Object key) {
				Thread t = Thread.currentThread();
				if (t instanceof BeeThread) {
					BeeThread bee = (BeeThread) t;
					return bee.getUserAttribute(key);
				}
				return null;
			}

			@Override
			public BAssignment createInstance(BClass bclass) {
				return BasicGenUtils.createInstance(bclass);
			}

		};
		p.setReload(reload);
		BLogicProvider provider = model.getProcessModel().getProvider(reload, p);
		provider.getProperties().setReload(reload);
		return provider;

	}

}
