package com.linkstec.bee.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import com.linkstec.bee.UI.BWorkspace;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeEditor;
import com.linkstec.bee.UI.BeeSpectiveHolder;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.filechooser.BeeFileChooser;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.spective.BeeBasicSpective;
import com.linkstec.bee.UI.spective.BeeDetailSpective;
import com.linkstec.bee.UI.spective.BeeSourceSpective;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.config.CodeConfig;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.code.BeeSourceWorkspace;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.VerifyClass;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.codec.CodecAction.ActionProperty;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.codec.decode.DecodeDoc;
import com.linkstec.bee.core.codec.decode.DecodeDocProjects;
import com.linkstec.bee.core.codec.encode.JavaGenUnit;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.action.BJavaGen;
import com.linkstec.bee.core.fw.action.BProcess;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.FileUtils;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.mxgraph.util.mxResources;

public class Application {

	/**
	 * Adds required resources for i18n
	 */

	static {
		try {
			mxResources.add("com/linkstec/bee/UI/resources/editor");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private BProject currentProject;
	private BeeSpectiveHolder spectiveContainer = new BeeSpectiveHolder();
	private static Logger logger = Logger.getLogger(Application.class.getName());
	private BeeEditor editor;
	private BEditor currentEditor;
	private BeeDetailSpective designSpective = null;
	private BeeSourceSpective JavaSourceSpective;
	private BeeBasicSpective basicSpective;
	private BeeConfig config;

	public static JFrame FRAME = null;

	public BeeEditor getEditor() {
		return editor;
	}

	public void setEditor(BeeEditor editor) {
		this.editor = editor;
	}

	public BEditor getCurrentEditor() {
		return currentEditor;
	}

	public void setCurrentEditor(BEditor currentEditor) {
		if (currentEditor == null) {
			this.currentEditor = null;
			this.setCurrentProject(null);
			this.editor.updateFrameTitle(null);
			this.editor.getToolbar().refreshItems();
			return;
		}
		String path = currentEditor.getDisplayPath();

		this.editor.updateFrameTitle(path);
		this.currentEditor = currentEditor;
		this.setCurrentProject(currentEditor.getProject());
		this.editor.getToolbar().refreshItems();
	}

	public BeeDetailSpective getDesignSpective() {
		return designSpective;
	}

	public BeeBasicSpective getBasicSpective() {
		return this.basicSpective;
	}

	public void setDesignSpective(BeeDetailSpective designSpective) {
		this.designSpective = designSpective;
	}

	public void setCurrentProject(BProject currentProject) {
		this.currentProject = currentProject;
	}

	private static Application instance;

	public static boolean INSTANCE_COMPLETE = false;
	public static boolean EDITOR_INSTANCED = false;
	private String currentSpactive = null;

	public void setSpactive(String spective) {
		if (spective.equals(BeeSpective.DETAIL_DESIGN)) {
			this.spectiveContainer.setContents(designSpective);
		} else if (spective.equals(BeeSpective.JAVA_SOURCE)) {
			this.spectiveContainer.setContents(JavaSourceSpective);
		} else if (spective.equals(BeeSpective.BASIC_DESIGN)) {
			this.spectiveContainer.setContents(this.basicSpective);
		}
		this.currentSpactive = spective;
		this.spectiveContainer.updateUI();
		if (editor != null) {
			this.editor.getToolbar().repaint();
		}
	}

	public BProject getCurrentProject() {
		if (this.currentProject != null) {
			return currentProject;
		}
		return null;

	}

	public BeeConfig getConfigSpective() {
		return this.config;
	}

	public String getCurrentSpactive() {
		return currentSpactive;
	}

	public void setCurrentSpactive(String currentSpactive) {
		this.currentSpactive = currentSpactive;
	}

	public BeeSpectiveHolder getSpectiveContainer() {
		return spectiveContainer;
	}

	public BeeSourceSpective getJavaSourceSpective() {
		return JavaSourceSpective;
	}

	public static class log {
		public static void info(String s) {
			logger.info(s);
		}

		public static void error(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public static Application getInstance() {
		if (instance == null) {
			instance = new Application();
		}
		return instance;
	}

	public void addNewProject(BProject project) {
		designSpective.getFileExplore().addProject(project);
		JavaSourceSpective.getFileExplore().addProject(project);
		basicSpective.getFileExplore().addProject(project);
		DecodeDocProjects.addDoc(new DecodeDoc(project));
		ProjectClassLoader.addClassLoder(new BeeClassLoader(project));
	}

	public void deleteProject(BProject project) {
		BeeConfig config = Application.getInstance().getConfigSpective();
		List<BProject> projects = config.getConfig().getProjects();
		for (BProject p : projects) {
			if (p.getName().equals(project.getName())) {
				projects.remove(p);
				break;
			}
		}
		config.save();

		boolean deleteFile = true;

		designSpective.getFileExplore().deleteProject(project, deleteFile);
		JavaSourceSpective.getFileExplore().deleteProject(project, deleteFile);
		basicSpective.getFileExplore().deleteProject(project, deleteFile);
		if (deleteFile) {
			FileUtils.deleteAllFile(new File(project.getSourcePath()));
			FileUtils.deleteAllFile(new File(project.getClassPath()));
			FileUtils.deleteAllFile(new File(project.getDesignPath()));
		}
	}

	private Application() {
		BProcess.p = new P();
		BJavaGen.gen = new JavaGenUnit();
		// BeeLogger.initialize();

		config = new BeeConfig();

		inilializeSystem();

		JavaSourceSpective = new BeeSourceSpective(config);
		designSpective = new BeeDetailSpective(config);
		basicSpective = new BeeBasicSpective(config);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.SEVERE);

		installFocusManager();

		Application.INSTANCE_COMPLETE = true;
	}

	public void inilializeSystem() {
		// java doc
		JavaDocCache.pushAll();
		LocalClassPathes.pushAll();
		Configuration c = config.getConfig();

		List<BProject> projects = c.getProjects();
		DecodeDocProjects.clear();
		ProjectClassLoader.clear();
		for (BProject project : projects) {
			DecodeDocProjects.addDoc(new DecodeDoc(project));
			ProjectClassLoader.addClassLoder(new BeeClassLoader(project));
		}

	}

	public static void refreshClassLoader(BProject project) {
		ProjectClassLoader.removeClassLoader(project.getName());
		ProjectClassLoader.addClassLoder(new BeeClassLoader(project));
	}

	private void installFocusManager() {
		FocusManager.getCurrentManager().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String name = evt.getPropertyName();
				Object newObj = evt.getNewValue();
				// Object oldObj = evt.getOldValue();
				// Point p = Application.FRAME.getMousePosition();

				// System.out.println("------");
				// System.out.println(p);

				Window[] windows = JWindow.getWindows();
				for (Window win : windows) {
					if (win.isVisible() && !(win instanceof JFrame)) {
						if (newObj != null) {
							if (newObj instanceof JComponent) {
								JComponent comp = (JComponent) newObj;
								Container ans = comp.getTopLevelAncestor();

								if (ans.getParent() != null && !ans.equals(win) && !ans.getParent().equals(win)) {
									if (ans instanceof CodeConfig) {
										return;
									}
									win.setVisible(false);
									return;
								} else if (ans.equals(FRAME) && !ans.equals(win)) {
									// if (win.getName() != null && win.getName().equals("HINT")) {
									//
									// win.setVisible(false);
									// return;
									//
									// }
									if (!(win instanceof BeePopupTreeMenu)) {
										if (win.getName() != null
												&& win.getName().equals(BeeFileChooser.class.getName())) {
											return;
										} else if (win.getName() != null && win.getName().equals("Popup")) {
											win.setVisible(false);
											return;
										}
										if (win.getName() != null && win.getName().equals("Visible")) {
											if (!(win.equals(newObj))) {
												win.setVisible(false);
												return;
											}
										} else {
											return;
										}
										// }
									}
								}

							}
						}
					}

				}
				// if (newObj != null) {
				//
				// System.out.println("new :" + newObj.getClass().getName());
				// }
				// if (oldObj != null) {
				// System.out.println("old:" + oldObj.getClass().getName());
				// }
				//
				// if (name.equals("activeWindow")) {
				// if (newObj == null) {
				// if (editor != null) {
				// editor.hideAllHints();
				// }
				// }
				// }
				if (name.equals("focusOwner")) {

					BeeTabbedPane pane = BeeActions.getContainer(newObj);
					if (pane != null) {
						pane.setFocusGained(true);
					}
					// for (BeeTabbedPane p : BeeTabbedPane.panes) {
					// if (newObj != null) {
					// if (!newObj.equals(p)) {
					// if (!newObj.getClass().getName().equals(BeePopupMenuItem.class.getName())) {
					// p.getActionMenu().setVisible(false);
					// }
					// }
					// }
					// }

				}

			}

		});
	}

	public static Class<?> loadClass(BProject project, String className) throws ClassNotFoundException {
		return ProjectClassLoader.getClassLoader(project).loadClass(className);
	}

	public BeeClassLoader getBeeClassLoader(BProject project) {
		return ProjectClassLoader.getClassLoader(project);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void log(Object obj) {
		if (obj == null) {
			logger.info("null");
		} else {
			logger.info(obj.toString());
		}
	}

	public static void PrintTrace() {

		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		String s = "";
		for (StackTraceElement ste : st) {
			s = s + ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + "\n";
		}
		getLogger().info(s);
	}

	public static void PrintLastTrace() {

		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		String s = "";
		StackTraceElement ste = st[3];
		s = s + ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + "\n";

		getLogger().info(s);
	}

	public static void TODO(String s) {
		getLogger().info(s);
	}

	private static ArrayList<Class<?>> list = new ArrayList<Class<?>>();

	public static void analyzeObjectTest(Object obj) {
		if (obj == null) {
			return;
		}
		Class<?> clz = obj.getClass();
		if (list.contains(clz)) {
			return;
		}
		// System.out.println(clz.getName());
		if (obj instanceof List) {

			for (Object o : (List<?>) obj) {
				analyzeObjectTest(o);
			}

		} else {
			list.add(clz);
			Field[] filds = clz.getDeclaredFields();
			for (Field f : filds) {
				try {
					System.out.println(clz.getName() + "." + f.getName() + "->" + f.getType().getName());
					f.setAccessible(true);
					analyzeObjectTest(f.get(obj));
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}

	}

	public void ProjectAdded(BProject project) {
		this.updateProject(project);
	}

	public void updateProject(BProject project) {
		getConfigSpective().readConfig();
		// this.DesignSpective.getta
		this.designSpective.getTask().getConsole().addText("プロジェクトを最新化しています…", project);
		inilializeSystem();
		try {

			this.designSpective.getTask().getConsole().addText("設計ファイルを最新化しています…", project);
			this.designSpective.getTask().getConsole().addText("ソースファイルを最新化しています…", project);

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						designSpective.getTask().getConsole().addText("ソースファイルを最新化しています…", project);
						Application.getInstance().getJavaSourceSpective().getFileExplore().doRefresh();
						designSpective.getTask().getConsole().addText("ソースをコンパイルしています…", project);
						BeeCompiler.compileAll(project, null);
						designSpective.getTask().getConsole().addText("設計ファイルを検証しています…", project);
						compileDesign(project);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void compileDesign(BProject project) throws IOException, ClassNotFoundException {
		List<File> files = CodecUtils.getAllDesignFile(null, null, project);
		for (File f : files) {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object obj = ois.readObject();
			ois.close();
			bis.close();

			if (obj instanceof BookModel) {
				BookModel b = (BookModel) obj;
				List<BEditorModel> list = b.getList();
				for (BEditorModel model : list) {
					if (model instanceof BeeModel) {
						BeeGraphSheet sheet = (BeeGraphSheet) model.getSheet(project);
						new VerifyClass(sheet);
					}
				}
			}

		}
	}

	public void ProjectChanged(BProject project) {

	}

	public void ProjectDeleted(BProject project) {

	}

	public static void addProjectStatus(BProject project, String key, Object value) {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		BeeStatus status = BeeStatus.read(config, project.getName());
		if (status == null) {
			status = new BeeStatus(project.getName());
			status.save(config);
		}
		status.addProperty(key, value);
		status.save(config);
	}

	public static BeeStatus getProjectStatus(BProject project) {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		BeeStatus status = BeeStatus.read(config, project.getName());
		if (status == null) {
			status = new BeeStatus(project.getName());
			status.save(config);
		}
		return status;
	}

	public static void saveStatus(BeeStatus status) {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		status.save(config);
	}

	public static void beforeApplicationClose() {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		BeeSourceWorkspace source = (BeeSourceWorkspace) Application.getInstance().getJavaSourceSpective()
				.getWorkspace();
		BWorkspace space = Application.getInstance().getDesignSpective().getWorkspace();

		Application.closeWorkSapce(space);
		Application.closeWorkSapce(source);

		int count = space.getTabCount();
		List<BProject> projects = config.getProjects();
		for (BProject project : projects) {
			BeeStatus status = Application.getProjectStatus(project);

			// source
			List<String> openedSource = new ArrayList<String>();
			count = source.getTabCount();
			for (int i = 0; i < count; i++) {
				Component comp = source.getComponentAt(i);
				if (comp instanceof BeeSourceSheet) {
					BeeSourceSheet sheet = (BeeSourceSheet) comp;
					if (sheet.getProject().getName().equals(project.getName())) {
						openedSource.add(sheet.getFile().getAbsolutePath());
					}
				}
			}
			status.addProperty("source.openedlist", openedSource);

			// design
			List<String> openedDesign = new ArrayList<String>();
			count = space.getTabCount();
			for (int i = 0; i < count; i++) {
				Component comp = space.getComponentAt(i);
				if (comp instanceof EditorBook) {
					EditorBook sheet = (EditorBook) comp;
					if (sheet.getProject().getName().equals(project.getName())) {
						if (sheet.getFile() != null) {
							openedDesign.add(sheet.getFile().getAbsolutePath());
						}
					}
				}
			}
			status.addProperty("design.openedlist", openedDesign);
			status.addProperty("codec.action.properties", CodecAction.getActionProperty(project));

			// basic
			space = Application.getInstance().getBasicSpective().getWorkspace();
			Application.closeWorkSapce(space);
			List<String> opendbasic = new ArrayList<String>();
			count = space.getTabCount();
			for (int i = 0; i < count; i++) {
				Component comp = space.getComponentAt(i);
				if (comp instanceof BasicBook) {
					BasicBook sheet = (BasicBook) comp;
					if (sheet.getProject().getName().equals(project.getName())) {
						if (sheet.getFile() != null) {
							opendbasic.add(sheet.getFile().getAbsolutePath());
						}
					}
				}
			}
			status.addProperty("basic.openedlist", opendbasic);
			Application.saveStatus(status);

			// ClassCache.write();
		}
	}

	private static void closeWorkSapce(BWorkspace space) {
		List<BEditor> editors = space.getAllOpenningEditors();
		for (BEditor editor : editors) {
			if (editor.isModified()) {

				int r = JOptionPane.showConfirmDialog(space, editor.getLogicName() + "が修正されています。保存します。よろしいでしょうか",
						"クローズワーニング", JOptionPane.YES_NO_OPTION);
				if (r == JOptionPane.YES_OPTION) {
					if (editor instanceof EditorBook) {
						EditorBook book = (EditorBook) editor;
						BeeActions.saveModel(book.getBookModel().getName(), book.getBookModel(), book.getProject());
					} else {
						// TODO
					}

				}
			}
		}
	}

	public static void afterEditorOpened() {
		BeeEditor editor = Application.getInstance().getEditor();
		editor.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		List<BProject> projects = config.getProjects();
		for (BProject project : projects) {
			BeeStatus status = Application.getProjectStatus(project);

			// source
			@SuppressWarnings("unchecked")
			List<String> openedSource = (List<String>) status.getProperties().get("source.openedlist");
			if (openedSource != null) {
				BeeSourceSpective reader = Application.getInstance().getJavaSourceSpective();
				for (String path : openedSource) {
					File f = new File(path);
					if (f.exists()) {
						reader.setSelected(f, project);
					}
				}
			}

			// design
			@SuppressWarnings("unchecked")
			List<String> openedDesign = (List<String>) status.getProperties().get("design.openedlist");
			if (openedDesign != null) {
				for (String path : openedDesign) {
					File f = new File(path);
					if (f.exists()) {
						BeeActions.addDetailPane(f, project);
					}
				}
			}

			CodecAction.ActionProperty propeties = (ActionProperty) status.getProperties()
					.get("codec.action.properties");
			if (propeties != null) {
				CodecAction.addProperty(project, propeties);
			}

			// design
			@SuppressWarnings("unchecked")
			List<String> openedBasic = (List<String>) status.getProperties().get("basic.openedlist");
			if (openedBasic != null) {
				for (String path : openedBasic) {
					File f = new File(path);
					if (f.exists()) {
						try {
							Object o = ObjectFileUtils.readObject(f);
							if (o instanceof BasicBookModel) {
								BasicBookModel model = (BasicBookModel) o;
								BEditor e = model.getEditor(project, f,
										Application.getInstance().getBasicSpective().getWorkspace());
								Application.getInstance().getBasicSpective().getWorkspace().insertTab(e.getLogicName(),
										e.getImageIcon(), (Component) e, e.getName(), 0);
							}
						} catch (Exception e1) {

							e1.printStackTrace();
						}
						test = true;
					}
				}
			}
		}
		editor.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public static boolean test = false;

	public void exit() {

		beforeApplicationClose();

		if (Application.FRAME != null) {
			Application.FRAME.dispose();
		}

	}

}
