package com.linkstec.bee.core.codec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.editor.task.console.ConsoleDisplay;
import com.linkstec.bee.UI.look.BeeButtonUI;
import com.linkstec.bee.UI.look.button.BeeCheckBox;
import com.linkstec.bee.UI.look.dialog.BeeListDialog;
import com.linkstec.bee.UI.look.dialog.BeeListDialog.BeforeActionLisener;
import com.linkstec.bee.UI.look.filechooser.BeeFileChooser;
import com.linkstec.bee.UI.look.tree.BeeTree;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.BeeSpective;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.code.BeeSourceWorkspace;
import com.linkstec.bee.UI.spective.code.tree.BeeSourceTree;
import com.linkstec.bee.UI.spective.code.tree.BeeSourceTreeNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.VerifyClass;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.P;
import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.codec.basic.BProvider;
import com.linkstec.bee.core.codec.basic.BasicGen;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.codec.decode.BeeCompilerTaskListener;
import com.linkstec.bee.core.codec.decode.DecodeGen;
import com.linkstec.bee.core.codec.encode.JavaGen;
import com.linkstec.bee.core.codec.excel.ExcelExport;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BeeClassExistsException;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.export.IExcelExport;
import com.linkstec.bee.core.io.ObjectFileUtils;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;

public class CodecAction {
	public static final String TYPE_JAVA_EXPORT = "java";
	public static final String TYPE_EXCEL_EXPORT = "excel";
	public static final String TYPE_EXCEL_PROVIDER_CLASS = "TYPE_EXCEL_PROVIDER_CLASS";
	public static final String TYPE_EXCEL_PROVIDER_PROJECT = "TYPE_EXCEL_PROVIDER_PROJECT";

	public static File generateSource(String path, BProject project)
			throws ClassNotFoundException, IOException, JClassAlreadyExistsException, BeeClassExistsException {
		File f = new File(path);

		Object obj = ObjectFileUtils.readObject(f);

		List<BClass> classes = new ArrayList<BClass>();
		if (obj instanceof BookModel) {
			BookModel b = (BookModel) obj;
			classes = b.getClassList();
		}
		JavaGen gen = new JavaGen(project);

		gen.generate(classes);

		String souce = f.getAbsolutePath();
		souce = souce.substring(project.getDesignPath().length(), souce.length());
		souce = project.getSourcePath() + souce;
		souce = souce.substring(0, souce.lastIndexOf('.')) + ".java";
		File sourceFile = new File(souce);

		return sourceFile;
	}

	public static class ActionProperty implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7839646255369397512L;
		private String javaExportFilePath;
		private String excelExportFilePath;
		private String exportClass;
		private String exportClassProject;
		private BProject project;

		public BProject getProject() {
			return project;
		}

		public String getExportClass() {
			return exportClass;
		}

		public void setExportClass(String exportClass) {
			this.exportClass = exportClass;
		}

		public String getExportClassProject() {
			return exportClassProject;
		}

		public void setExportClassProject(String exportClassProject) {
			this.exportClassProject = exportClassProject;
		}

		public void setProject(BProject project) {
			this.project = project;
		}

		public String getJavaExportFilePath() {
			return javaExportFilePath;
		}

		public void setJavaExportFilePath(String javaExportFilePath) {
			this.javaExportFilePath = javaExportFilePath;
		}

		public String getExcelExportFilePath() {
			return excelExportFilePath;
		}

		public void setExcelExportFilePath(String excelExportFilePath) {
			this.excelExportFilePath = excelExportFilePath;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

	}

	protected static List<ActionProperty> StaticProperties = new ArrayList<ActionProperty>();

	public static void addProperty(BProject project, String path, String type) {
		ActionProperty p = null;
		for (ActionProperty property : StaticProperties) {
			if (property.getProject().getName().equals(project.getName())) {
				p = property;
				break;
			}
		}
		if (p == null) {
			p = new ActionProperty();
			p.setProject(project);
			StaticProperties.add(p);
		}
		if (type.equals(CodecAction.TYPE_EXCEL_EXPORT)) {
			p.setExcelExportFilePath(path);
		} else if (type.equals(TYPE_JAVA_EXPORT)) {
			p.setJavaExportFilePath(path);
		} else if (type.equals(TYPE_EXCEL_PROVIDER_CLASS)) {
			p.setExportClass(path);
		} else if (type.equals(TYPE_EXCEL_PROVIDER_PROJECT)) {
			p.setExportClassProject(path);
		}
	}

	public static void addProperty(BProject project, ActionProperty p) {
		StaticProperties.add(p);
	}

	public static String getProperty(BProject project, String type) {
		ActionProperty p = null;
		for (ActionProperty property : StaticProperties) {
			if (property.getProject().getName().equals(project.getName())) {
				p = property;
				break;
			}
		}
		if (p == null) {
			return null;
		} else {
			if (type.equals(CodecAction.TYPE_EXCEL_EXPORT)) {
				return p.getExcelExportFilePath();
			} else if (type.equals(TYPE_JAVA_EXPORT)) {
				return p.getJavaExportFilePath();
			} else if (type.equals(TYPE_EXCEL_PROVIDER_CLASS)) {
				p.getExportClass();
			} else if (type.equals(TYPE_EXCEL_PROVIDER_PROJECT)) {
				p.getExportClassProject();
			}
			return null;
		}
	}

	public static ActionProperty getActionProperty(BProject project) {
		ActionProperty p = null;
		for (ActionProperty property : StaticProperties) {
			if (property.getProject() != null) {
				if (property.getProject().getName().equals(project.getName())) {
					p = property;
					break;
				}
			}
		}
		return p;
	}

	public static class ThreadParameter implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -271267993211272704L;
		private boolean doLinkedObject;

		public boolean isDoLinkedObject() {
			return doLinkedObject;
		}

		public void setDoLinkedObject(boolean doLinkedObject) {
			this.doLinkedObject = doLinkedObject;
		}

	}

	public static abstract class BasicAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1206359962239262586L;
		private File selectedFile = null;

		private String type;
		private BProject project;

		public JPanel craeteFileSelectDialog(BeeListDialog dialog, String name, int mode, String type,
				BProject project) {
			this.type = type;
			this.project = project;
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			FlowLayout layout = new FlowLayout();
			int margin = BeeUIUtils.getDefaultFontSize();

			panel.setBorder(new EmptyBorder(0, 0, margin * 2, 0));
			layout.setHgap(margin / 3);
			layout.setAlignment(FlowLayout.LEFT);
			JLabel label = new JLabel(name);
			label.setFont(BeeUIUtils.getDefaultFont());

			JTextField text = new JTextField();

			if (selectedFile != null) {
				text.setText(selectedFile.getAbsolutePath());
			} else {
				String s = CodecAction.getProperty(this.getProject(), this.getType());
				if (s != null) {
					text.setText(s);
					this.setSelectedFile(new File(s));
				}
			}
			text.setFont(BeeUIUtils.getDefaultFont());
			text.setPreferredSize(new Dimension(margin * 30, (int) (margin * 1.5)));
			text.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					String s = text.getText();
					if (!s.equals("")) {
						File f = new File(s);
						if (f.exists()) {
							setSelectedFile(f);
							onFileSelected(f);
							return;
						}
					}
					setSelectedFile(null);
					onFileSelected(null);
				}

				@Override
				public void keyPressed(KeyEvent e) {

				}

				@Override
				public void keyReleased(KeyEvent e) {

				}

			});
			panel.setLayout(layout);

			panel.add(label);
			panel.add(text);

			JButton button = new JButton("選択");
			button.setUI(new BeeButtonUI());
			button.setIcon(BeeConstants.FOLDER_ICON);
			button.setFont(BeeUIUtils.getDefaultFont());
			panel.add(button);

			button.addActionListener(new FileChooser(dialog, text, mode, this));

			return panel;

		}

		public BProject getProject() {
			return project;
		}

		public String getType() {
			return this.type;
		}

		public File getSelectedFile() {
			return selectedFile;
		}

		public void setSelectedFile(File selectedFile) {
			this.selectedFile = selectedFile;
		}

		protected void onFileSelected(File file) {

		}

		private static class FileChooser implements ActionListener {
			private BeeListDialog dialog;
			private JTextField text;
			private int mode;
			private BasicAction action;

			// JFileChooser.DIRECTORIES_ONLY
			public FileChooser(BeeListDialog dialog, JTextField text, int mode, BasicAction action) {
				this.dialog = dialog;
				this.mode = mode;
				this.text = text;
				this.action = action;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				BeeFileChooser d = new BeeFileChooser();
				d.setOpaque(true);
				d.setBackground(BeeConstants.BACKGROUND_COLOR);
				d.setFileSelectionMode(mode);

				if (action.getSelectedFile() != null) {
					if (mode == JFileChooser.DIRECTORIES_ONLY) {
						d.setCurrentDirectory(action.getSelectedFile());
					} else {
						d.setSelectedFile(action.getSelectedFile());
					}
				} else {
					String s = CodecAction.getProperty(action.getProject(), action.getType());
					if (s != null) {
						if (action.getType().equals(CodecAction.TYPE_EXCEL_EXPORT)) {
							d.setSelectedFile(new File(s));
						} else if (action.getType().equals(CodecAction.TYPE_JAVA_EXPORT)) {
							d.setCurrentDirectory(new File(s));
						}
					}
				}
				d.showDialog(dialog, "選択");

				File s = d.getSelectedFile();
				if (s != null) {
					CodecAction.addProperty(action.getProject(), s.getAbsolutePath(), action.getType());
					action.onFileSelected(s);
					if (mode == JFileChooser.DIRECTORIES_ONLY) {
						action.setSelectedFile(s.getParentFile());
					} else {
						action.setSelectedFile(s);
					}
					text.setText(s.getAbsolutePath());
				}

			}
		}

		protected List<Object> getAllNode(BeeTree tree, BProject project) {
			List<Object> list = new ArrayList<Object>();
			BeeTreeNode root = tree.getRoot();
			this.addNodeChild(root, list, project);
			return list;
		}

		private void addNodeChild(BeeTreeNode node, List<Object> list, BProject project) {
			int count = node.getChildCount();
			for (int i = 0; i < count; i++) {
				BeeTreeNode child = (BeeTreeNode) node.getChildAt(i);
				if (child.isLeaf()) {
					if (node instanceof BeeSourceTreeNode) {
						if (node.getProject() != null) {
							if (node.getProject().getName().equals(project.getName())) {
								BeeSourceTreeNode source = (BeeSourceTreeNode) node;
								File f = source.getFile();
								if (f != null) {
									if (child.toString().endsWith("java")) {
										list.add(child);
									}
								}
							}
						}
					} else if (node instanceof BeeTreeFileNode) {
						BeeTreeFileNode file = (BeeTreeFileNode) node;
						if (file.getProject().getName().equals(project.getName())) {
							if (file.getFilePath() != null) {
								list.add(child);
							}
						}
					}

				} else {
					addNodeChild(child, list, project);
				}
			}
		}

		protected List<Object> getAllSource(BProject project) {
			BeeSourceTree tree = (BeeSourceTree) Application.getInstance().getJavaSourceSpective().getExplore()
					.getFileExplorer();

			return this.getAllNode(tree, project);
		}

		protected List<Object> getAllOpenedSource(List<Object> all) {
			BeeSourceWorkspace space = (BeeSourceWorkspace) Application.getInstance().getJavaSourceSpective()
					.getWorkspace();
			List<BeeSourceSheet> sheets = space.getAllSheets();

			List<Object> defaults = new ArrayList<Object>();
			for (Object obj : all) {
				BeeSourceTreeNode node = (BeeSourceTreeNode) obj;
				for (BeeSourceSheet sheet : sheets) {
					if (node.getFile() != null) {
						if (sheet.getFile().getAbsolutePath().equals(node.getFile().getAbsolutePath())) {
							defaults.add(obj);
						}
					}
				}
			}
			return defaults;
		}

		protected long startSourceAction(String start) {
			long time = System.currentTimeMillis();
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().clear();
			Application.getInstance().getEditor().getStatusBar().setMessag("");
			Application.getInstance().getEditor().getStatusBar().startProgress(start);
			Application.getInstance().setSpactive(BeeSpective.JAVA_SOURCE);
			Application.getInstance().getJavaSourceSpective().getTask().setTaskConsoleSelected();
			return time;
		}

		protected void endEndSourceAction(String end) {
			Application.getInstance().getEditor().getStatusBar().endProgress();
			Application.getInstance().getEditor().getStatusBar().setMessag(end);
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().end();
			Application.log(end);
		}

		public long doGenerateViewStart(int all) {
			Application.getInstance().getJavaSourceSpective().getTask().setTaskConsoleSelected();
			long time = this.startSourceAction("ソースを設計へ変換しています...");
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().addText(all + "ファイル変換が開始します…",
					project);
			P.start(all);
			return time;
		}

		public void doGenerateView(File f, BProject project, int index, int all) {
			long startTime = System.currentTimeMillis();
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().addText("(" + index + "/" + all
					+ ") <a href='j://" + f.getAbsolutePath() + "'>" + f.getName() + "</a>を変換開始しています...", project);

			DecodeGen gen = new DecodeGen();
			try {
				File[] files = gen.executeAndSave(f.getAbsolutePath(), project);

				long endTime = System.currentTimeMillis();
				long spent = endTime - startTime;
				float span = spent / 1000;
				String msg = "";

				if (gen.getErrorCode() == null) {
					int i = 0;
					for (File file : files) {
						if (i != 0) {
							msg = msg + ",";
						}
						msg = msg + "<a href='d://" + file.getAbsolutePath() + "'>" + file.getName() + "</a>";
						i++;
					}

					msg = msg + "が変換完了、" + span + "秒かかりました";
				} else {
					String code = gen.getErrorCode();
					msg = msg + "<span style='color:red'>";
					if (code.equals(DecodeGen.ERROR_COMPILE_FAIL)) {
						msg = msg + "コンパイルエラーが発生したため変換できませんでした。";
					} else if (code.equals(DecodeGen.ERROR_DECODE_FAIL)) {
						msg = msg + "変換時にエラーが発生したため変換できませんでした。";
					} else if (code.equals(DecodeGen.ERROR_INTERFACE)) {
						msg = msg + "インターフェースであるため変換されません";
					} else if (code.equals(DecodeGen.ERROR_NOCLASS)) {
						msg = msg + "クラスがみかつからなかったため変換できませんでした。";
					} else {
						msg = msg + "未知の原因で変換できませんでした。";
					}
					msg = msg + "</span>";
				}
				Application.getInstance().getJavaSourceSpective().getTask().getConsole().addText(msg, project);
			} catch (Exception e) {
				e.printStackTrace();
				Application.getInstance().getJavaSourceSpective().getTask().getConsole().addText(
						"<a href='j://" + f.getAbsolutePath() + "'>" + f.getName() + "</a>の変換が失敗しました", project);
				throw e;
			}
			P.go();
			gen = null;
		}

		public void doGenerateViewFaild(Exception e) {
			if (((BeeThread) Thread.currentThread()).isToppedIntently(e)) {
				Application.getInstance().getEditor().getStatusBar().setMessag("変換が中断されました");
			} else {
				Application.getInstance().getEditor().getStatusBar().setMessag("変換エラーが発生しました。");
			}
			Application.getInstance().getEditor().getStatusBar().endProgress();
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().end();
		}

		public void doGenerateViewRun(Runnable run) {
			BeeThread t = new BeeThread(run);

			ThreadParameter parameter = new ThreadParameter();
			parameter.setDoLinkedObject(false);

			t.setUserObject(parameter);
			t.setName("BEE_RUN");
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().start(t);
			t.start();
		}

		public void doGenerateViewEnd(long time) {
			P.end();
			long spend = System.currentTimeMillis() - time;
			String msg = "変換するには" + spend / 1000 + "秒かかりました";
			this.endEndSourceAction(msg);

		}

		public void doGenerateSourceRun(Runnable run) {
			BeeThread t = new BeeThread(run);
			t.setName("BEE_RUN");
			Application.getInstance().getDesignSpective().getTask().getConsole().start(t);
			t.start();

		}

		public void doGenerateSourceStart(int all) {
			Application.getInstance().setSpactive(BeeSpective.DETAIL_DESIGN);
			Application.getInstance().getDesignSpective().getTask().setTaskConsoleSelected();
			Application.getInstance().getDesignSpective().getTask().getConsole().clear();
			Application.getInstance().getDesignSpective().getTask().getConsole().addText(all + "ファイル変換が開始します…",
					project);

			Application.getInstance().getEditor().getStatusBar().setMessag("");
			Application.getInstance().getEditor().getStatusBar().startProgress("設計をソースへ変換しています...");
			P.start(all);

		}

		public void doGenerateSourceFaild(Exception e, String path, String display) {
			e.printStackTrace();
			Application.getInstance().getDesignSpective().getTask().getConsole()
					.addText("<a href='d://" + path + "'>" + display + "</a>の変換が失敗しました", project);
			if (((BeeThread) Thread.currentThread()).isToppedIntently(e)) {
				Application.getInstance().getEditor().getStatusBar().setMessag("変換が中断されました");
			} else {
				e.printStackTrace();
				Application.getInstance().getDesignSpective().getTask().getConsole().addText("変換が失敗しました", null);

			}
			Application.getInstance().getDesignSpective().getTask().getConsole().end();
			Application.getInstance().getEditor().getStatusBar().endProgress();
		}

		public void doGenerateSourceEnd(long time) {
			P.end();
			Application.getInstance().getDesignSpective().getTask().getConsole().end();
			Application.getInstance().getEditor().getStatusBar().endProgress();
			long spend = System.currentTimeMillis() - time;
			String msg = "変換するには" + spend / 1000 + "秒かかりました";
			Application.getInstance().getEditor().getStatusBar().setMessag(msg);
			Application.getInstance().getEditor().getStatusBar().endProgress();
			Application.log(msg);
		}

		public File doGenerateSource(int index, int all, String path, BProject project)
				throws IOException, ClassNotFoundException, BeeClassExistsException, JClassAlreadyExistsException {
			long now = System.currentTimeMillis();

			Application.getInstance().getDesignSpective().getTask().getConsole().addText("(" + index + "/" + all
					+ ") <a href='d://" + path + "'>" + new File(path).getName() + "</a>を変換開始しています...", project);
			File sourceFile = CodecAction.generateSource(path, project);

			Application.getInstance().getDesignSpective().getTask().getConsole()
					.addText("(" + index + "/" + all + ") <a href='d://" + path + "'>" + new File(path).getName()
							+ "</a>を<a href='j://" + sourceFile.getAbsolutePath() + "'>" + sourceFile.getName()
							+ "</a>へ変換完了し、" + (System.currentTimeMillis() - now) / 1000 + "秒かかりました。", project);

			P.go();

			return sourceFile;
		}
	}

	public static class ExportJava extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1471921382489161822L;

		public ExportJava() {

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(run).start();
		}

		Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					BProject project = Application.getInstance().getCurrentProject();
					if (project == null) {
						return;
					}
					generate(project);

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

		};

		private void generate(BProject project) throws IOException {
			List<Object> list = this.getAllSource(project);
			List<Object> defaults = this.getAllOpenedSource(list);

			BeeListDialog dialog = new BeeListDialog(list, defaults);
			JPanel panel = this.craeteFileSelectDialog(dialog, "保存先", JFileChooser.DIRECTORIES_ONLY,
					CodecAction.TYPE_JAVA_EXPORT, project);
			dialog.addComponent(panel);
			dialog.showDialog("エクスポートソース対象選択");

			List<Object> selected = dialog.getSelected();
			if (!selected.isEmpty()) {
				long time = startSourceAction("ソースエクスポートしています...");

				for (Object obj : selected) {
					BeeSourceTreeNode node = (BeeSourceTreeNode) obj;

					File f = node.getFile();

					File file = exprort(project, f.getAbsolutePath(), f.getName());

					String target = file.getAbsolutePath();
					Application.getInstance().getJavaSourceSpective().getTask().getConsole().addText("<a href='f://"
							+ file.getParentFile().getAbsolutePath() + "'>" + target + "</a>へエクスポートしました",
							node.getProject());

				}
				long spend = System.currentTimeMillis() - time;
				String msg = "エクスポートするには" + spend / 1000 + "秒かかりました";
				endEndSourceAction(msg);
			}

		}

		private File exprort(BProject project, String path, String name) throws IOException {

			JavacTask javacTask = BeeCompiler.scan(project, path);

			Iterable<? extends CompilationUnitTree> result = javacTask.parse();
			Iterator<? extends CompilationUnitTree> ite = result.iterator();
			String dir = this.getSelectedFile().getAbsolutePath();

			while (ite.hasNext()) {
				CompilationUnitTree unit = ite.next();

				String source = unit.toString();
				String pack = unit.getPackageName().toString();

				String filename = dir + File.separator + pack.replace('.', File.separatorChar) + File.separator + name;
				File file = new File(filename);
				file.getParentFile().mkdirs();
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(source.getBytes());
				fos.flush();
				fos.close();

				return file;
			}
			return null;

		}

	}

	public static class CompileDesign extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1471921382489161822L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BeeGraphSheet sheet = Application.getInstance().getDesignSpective().getGraphSheet();

			if (sheet != null) {
				new VerifyClass(sheet);
			}

		}

	}

	public static class GenerateSource extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7731777466200979325L;

		public void generate() {
			BProject project = Application.getInstance().getCurrentProject();
			if (project == null) {
				return;
			}

			if (Application.getInstance().getDesignSpective().getTask().getProblems().hasProblem()) {
				JOptionPane.showConfirmDialog(Application.FRAME, "設計に不都合があります。ご確認ください。", "確認",
						JOptionPane.PLAIN_MESSAGE);
				Application.getInstance().getDesignSpective().getTask().setTaskProblemsSelected();
				return;
			}
			BeeTree tree = Application.getInstance().getDesignSpective().getFileExplore();

			List<Object> list = this.getAllNode(tree, project);

			List<BEditor> books = Application.getInstance().getDesignSpective().getWorkspace().getAllOpenningEditors();
			List<Object> defaults = new ArrayList<Object>();
			for (Object obj : list) {
				BeeTreeFileNode node = (BeeTreeFileNode) obj;
				for (BEditor book : books) {
					if (node.getFilePath().contains(book.getName() + ".")) {
						defaults.add(obj);
					}
				}
			}

			BeeListDialog dialog = BeeListDialog.showDialog("ソース(" + project.getName() + ")へ変換する設計対象選択", list,
					defaults);
			List<Object> selected = dialog.getSelected();

			if (!selected.isEmpty()) {

				long time = System.currentTimeMillis();
				this.doGenerateSourceStart(selected.size());
				BeeTreeFileNode current = null;
				try {
					int count = 0;
					for (Object object : selected) {
						count++;
						current = (BeeTreeFileNode) object;
						this.doGenerateSource(count, selected.size(), current.getFilePath(), project);
					}
				} catch (Exception e) {
					this.doGenerateSourceFaild(e, current.getFilePath(), current.getDisplay());
				} finally {
					this.doGenerateSourceEnd(time);
				}
				BeeCompiler.comileAllWithThread(project, null);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doGenerateSourceRun(new Runnable() {
				@Override
				public void run() {
					generate();
				}
			});
		}

	}

	public static class GenerateSourceSingle extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6391253105634843117L;
		private File f;
		private BProject project;

		public GenerateSourceSingle(File f, BProject project) {
			this.f = f;
			this.project = project;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doGenerateSourceRun(new Runnable() {
				@Override
				public void run() {
					generate();
				}
			});

		}

		public File generate() {
			long time = System.currentTimeMillis();
			this.doGenerateSourceStart(1);
			try {
				return this.doGenerateSource(1, 1, f.getAbsolutePath(), project);
			} catch (Exception e) {
				this.doGenerateSourceFaild(e, f.getAbsolutePath(), f.getName());
			} finally {
				this.doGenerateSourceEnd(time);
			}
			return null;

		}

	}

	public static class SourceCleanup extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4633320269753030294L;

		@Override
		public void actionPerformed(ActionEvent e) {
			BProject project = Application.getInstance().getCurrentProject();
			if (project == null) {
				return;
			}
			Application.refreshClassLoader(project);
			startSourceAction(project.getName() + "クリア開始");
			Application.getInstance().getJavaSourceSpective().getFileExplore().setError(null);
			BeeThread t = new BeeThread(new Runnable() {

				@Override
				public void run() {

					BeeCompilerTaskListener task = new BeeCompilerTaskListener() {
						private int num = 0;
						private int p = 0;

						@Override
						public void finished(TaskEvent t) {
							P.check(null);

							if (t.getKind().equals(Kind.GENERATE)) {
								P.go();
								p++;
								String name = t.getSourceFile().getName();
								name = name.substring(project.getSourcePath().length() + 1);
								name = name.substring(0, name.lastIndexOf('.'));
								String calssName = name.replace(File.separatorChar, '.');
								name = project.getClassPath() + File.separator + name + ".class";

								try {
									ProjectClassLoader.getClassLoader(project).addClassPath(calssName, name);
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}

								Application.getInstance().getJavaSourceSpective().getTask().getConsole()
										.addText("(" + p + "/" + num + ")<a href='j://" + t.getSourceFile().getName()
												+ "'>" + t.getSourceFile().getName() + "</a>がコンパイル完了", project);
							} else if (t.getKind().equals(Kind.PARSE)) {
								P.go();
								p++;

								Application.getInstance().getJavaSourceSpective().getTask().getConsole()
										.addText("(" + p + "/" + num + ")<a href='j://" + t.getSourceFile().getName()
												+ "'>" + t.getSourceFile().getName() + "</a>が解析完了", project);
							}

						}

						@Override
						public void started(TaskEvent e) {

						}

						@Override
						public void setSourceNumber(int num) {
							this.num = num * 2;

						}

						@Override
						public int getSourceNumber() {
							return num;
						}
					};
					BeeCompiler.compileAll(project, task);
					endEndSourceAction(project.getName() + "クリア終了");
				}

			});
			Application.getInstance().getJavaSourceSpective().getTask().getConsole().start(t);
			t.start();

		}

	}

	public static class GenerateView extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3566104565846588542L;
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					generate();
				} catch (Exception e1) {
					doGenerateViewFaild(e1);
				}

			}
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			doGenerateViewRun(run);
		}

		public void generate() {

			BProject project = Application.getInstance().getCurrentProject();
			if (project == null) {
				return;
			}

			if (Application.getInstance().getJavaSourceSpective().getTask().getProblems().hasProblem()) {
				JOptionPane.showConfirmDialog(Application.FRAME, "ソースにエラーがあります。ご確認ください。", "確認",
						JOptionPane.PLAIN_MESSAGE);
				Application.getInstance().getJavaSourceSpective().getTask().setTaskProblemsSelected();

				return;
			}

			List<Object> list = this.getAllSource(project);
			List<Object> defaults = this.getAllOpenedSource(list);

			BeeListDialog dialog = BeeListDialog.showDialog("設計(" + project.getName() + ")へ変換するソース対象選択", list,
					defaults);
			List<Object> selected = dialog.getSelected();
			dialog = null;
			if (!selected.isEmpty()) {

				long time = this.doGenerateViewStart(selected.size());
				int count = 0;
				for (Object obj : selected) {
					count++;
					BeeSourceTreeNode node = (BeeSourceTreeNode) obj;
					this.doGenerateView(node.getFile(), project, count, selected.size());
				}
				this.doGenerateViewEnd(time);
			}

		}

	}

	public static class GenerateViewSingle extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6129770186181717128L;
		private File f;
		private BProject project;

		public GenerateViewSingle(File f, BProject project) {
			this.f = f;
			this.project = project;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			doGenerateViewRun(new Runnable() {

				@Override
				public void run() {
					try {
						long time = doGenerateViewStart(1);
						doGenerateView(f, project, 1, 1);
						doGenerateViewEnd(time);
					} catch (Exception e) {
						doGenerateViewFaild(e);
					}
				}

			});

		}

	}

	public static class ExportExcel extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6434262868836884763L;
		private BeeCheckBox check;
		private BeeCheckBox staticValue;
		private JPanel conditions;
		private JLabel errorText = null;
		private JTextField threadNum = new JTextField();

		private JPanel createComps(BeeListDialog dialog, BProject project) {
			conditions = new JPanel();
			conditions.setOpaque(false);
			int margin = BeeUIUtils.getDefaultFontSize();
			EtchedBorder border = new EtchedBorder() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1301861336503384455L;

				@Override
				public Insets getBorderInsets(Component c, Insets insets) {
					insets.set(margin, margin, margin, margin);
					return insets;
				}

			};

			conditions.setBorder(border);
			BorderLayout borderLayout = new BorderLayout();

			borderLayout.setVgap(BeeUIUtils.getDefaultFontSize() / 2);
			borderLayout.setHgap(BeeUIUtils.getDefaultFontSize() / 2);
			conditions.setLayout(borderLayout);

			JPanel parameter = new JPanel();
			parameter.setBorder(new EmptyBorder(0, 0, BeeUIUtils.getDefaultFontSize() / 2, 0));
			parameter.setOpaque(false);

			BorderLayout layout = new BorderLayout();
			layout.setHgap(0);
			parameter.setLayout(layout);

			JLabel label = new JLabel("※生成されたExcelは以下のテンプレートと同じ場所へ保存されます。");
			label.setForeground(Color.GRAY);
			parameter.add(label, BorderLayout.NORTH);

			JPanel panel = this.craeteFileSelectDialog(dialog, "テンプレート(*)", JFileChooser.FILES_ONLY,
					CodecAction.TYPE_EXCEL_EXPORT, project);
			panel.setBorder(new EmptyBorder(0, 0, BeeUIUtils.getDefaultFontSize() / 2, 0));
			parameter.add(panel, BorderLayout.CENTER);

			JPanel checks = new JPanel();
			checks.setOpaque(false);
			checks.setLayout(new GridLayout(0, 1));
			parameter.add(checks, BorderLayout.SOUTH);

			check = new BeeCheckBox("呼び出したメソッドの詳細を表示する");
			checks.add(check);

			staticValue = new BeeCheckBox("static値の呼び出しをその値表示する");
			staticValue.setSelected(true);
			checks.add(staticValue);

			JPanel threadNm = new JPanel();
			threadNm.setOpaque(false);
			FlowLayout f = new FlowLayout();
			f.setAlignment(FlowLayout.LEFT);
			threadNm.setLayout(f);

			JLabel tlabel = new JLabel("同時変換数（多くするとパソコンの負荷が高くなる）");
			threadNm.add(tlabel);
			threadNm.add(threadNum);
			threadNum.setText("1");
			int s = BeeUIUtils.getDefaultFontSize();
			threadNum.setPreferredSize(new Dimension(s * 3, s * 2));

			checks.add(threadNm);

			checks.add(this.makeProvider());

			conditions.add(parameter, BorderLayout.CENTER);
			return conditions;
		}

		private JPanel makeProvider() {
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			panel.setLayout(flow);

			JLabel title = new JLabel("Exportクラス");
			panel.add(title);

			BProject p = this.getProject();

			String exportClass = getProperty(p, CodecAction.TYPE_EXCEL_PROVIDER_CLASS);
			String exportClassProject = getProperty(p, CodecAction.TYPE_EXCEL_PROVIDER_PROJECT);

			Configuration config = Application.getInstance().getConfigSpective().getConfig();
			List<BProject> projects = config.getProjects();

			JComboBox<BProject> ps = new JComboBox<BProject>();

			// BProject savedProject = null;
			for (BProject bp : projects) {
				ps.addItem(bp);
				if (bp.getName().equals(exportClassProject)) {
					ps.setSelectedItem(bp);
					// savedProject = bp;
				}
			}
			ps.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					BProject bp = (BProject) e.getItem();
					addProperty(p, bp.getName(), CodecAction.TYPE_EXCEL_PROVIDER_PROJECT);
				}

			});

			panel.add(ps);

			JTextField text = new JTextField(exportClass);
			text.setPreferredSize(new Dimension());

			text.setFont(BeeUIUtils.getDefaultFont());
			int s = BeeUIUtils.getDefaultFontSize();
			text.setPreferredSize(new Dimension(s * 20, (int) (s * 1.5)));
			text.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					String s = text.getText();
					if (!s.equals("")) {
						addProperty(p, s, CodecAction.TYPE_EXCEL_PROVIDER_CLASS);
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {

				}

				@Override
				public void keyReleased(KeyEvent e) {

				}

			});

			panel.add(text);

			return panel;

		}

		private List<Object> selected;
		private BProject project;

		private Runnable run = new Runnable() {

			@Override
			public void run() {
				List<Run> threads = new ArrayList<Run>();
				try {
					long time = System.currentTimeMillis();
					Application.getInstance().getEditor().getStatusBar().startProgress("設計をExcelへ変換しています...");
					Application.getInstance().getDesignSpective().getTask().getConsole().clear();
					Application.getInstance().getEditor().getStatusBar().setMessag("");
					Application.getInstance().setSpactive(BeeSpective.DETAIL_DESIGN);
					Application.getInstance().getDesignSpective().getTask().setTaskConsoleSelected();
					int count = 0;

					Application.getInstance().getDesignSpective().getTask().getConsole()
							.addText(selected.size() + "Excel変換が開始します…", null);
					P.start(selected.size());
					for (Object object : selected) {
						count++;
						int num = 1;
						String text = threadNum.getText();
						text = text.trim();
						try {
							num = Integer.parseInt(text);
						} catch (Exception e) {

						}

						new Run(threads, (BeeTreeFileNode) object, count, selected.size(), check.isSelected(),
								staticValue.isSelected(), getSelectedFile().getAbsolutePath(), num);

						P.go();
						for (Run run : threads) {
							if (run.getException() != null) {
								throw run.getException();
							}
							if (run.isEnd()) {
								threads.remove(run);
							}
						}
					}
					while (threads.size() != 0) {
						for (Run run : threads) {
							if (run.getException() != null) {
								throw run.getException();
							}
							if (run.isEnd()) {
								threads.remove(run);
							}
						}
						Thread.sleep(1000);
					}
					P.end();
					Application.getInstance().getEditor().getStatusBar().endProgress();
					long spend = System.currentTimeMillis() - time;
					String msg = "変換するには" + spend / 1000 + "秒かかりました";
					Application.getInstance().getEditor().getStatusBar().setMessag(msg);

				} catch (Exception e1) {
					e1.printStackTrace();
					Application.getInstance().getEditor().getStatusBar().setMessag("変換エラーが発生しました。");
					Application.getInstance().getDesignSpective().getTask().getConsole()
							.addText("変換エラー(" + e1.getLocalizedMessage() + ")が発生しました。", project);
				} finally {
					Application.getInstance().getEditor().getStatusBar().endProgress();
					Application.getInstance().getDesignSpective().getTask().getConsole().end();
				}
			}

		};

		public static class Run implements Runnable {
			BeeTreeFileNode node;
			private int index, all;
			private boolean doInvoker, doStatic;
			private String template;
			private Exception e;
			private boolean end = false;
			private List<Run> threads;

			public Run(List<Run> threads, BeeTreeFileNode node, int index, int all, boolean doInvoker, boolean doStatic,
					String template, int runNum) {
				this.node = node;
				this.threads = threads;
				this.index = index;
				this.all = all;
				this.doInvoker = doInvoker;
				this.doStatic = doStatic;
				this.template = template;

				if (runNum == 1) {
					this.run();
					return;
				}

				BeeThread bee = new BeeThread(this);

				List<Run> removes = new ArrayList<Run>();

				for (Run t : threads) {
					if (t.isEnd()) {
						removes.add(t);
					}
				}
				for (Run r : removes) {
					threads.remove(r);
				}

				while (threads.size() >= runNum) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				bee.start();
				threads.add(this);
			}

			@Override
			public void run() {
				try {
					String path = node.getFilePath();
					String step = "(" + index + "/" + all + ")";

					Application.getInstance().getDesignSpective().getTask().getConsole().addText(
							step + "<a href='d://" + path + "'>" + node.toString() + "</a>をExcel変換開始してます…",
							node.getProject());
					if (path.endsWith("Gen.bee")) {
						P.go();
						Application.getInstance().getDesignSpective().getTask().getConsole().addText(
								step + "<a href='d://" + path + "'>" + node.toString() + "</a>が自動生成したためExcelへ変換なし",
								node.getProject());
						this.threads.remove(this);
						end = true;
						return;
					} else if (path.endsWith("CommandForm.bee")) {
						P.go();
						Application.getInstance().getDesignSpective().getTask().getConsole().addText(
								step + "<a href='d://" + path + "'>" + node.toString() + "</a>がDTOのためExcelへ変換なし",
								node.getProject());
						this.threads.remove(this);
						end = true;
						return;
					}

					String exportClass = getProperty(node.getProject(), CodecAction.TYPE_EXCEL_PROVIDER_CLASS);
					String exportClassProject = getProperty(node.getProject(), CodecAction.TYPE_EXCEL_PROVIDER_PROJECT);

					IExcelExport exportProvoider = null;
					Configuration config = Application.getInstance().getConfigSpective().getConfig();
					List<BProject> projects = config.getProjects();
					try {
						if (exportClass != null && exportClassProject != null) {

							for (BProject p : projects) {
								if (p.getName().equals(exportClassProject)) {
									Class<?> cls = CodecUtils.getClassByName(exportClass, p);
									exportProvoider = (IExcelExport) cls.newInstance();
									break;

								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)));
					ObjectInputStream ois = new ObjectInputStream(bis);
					Object obj = ois.readObject();
					ois.close();
					bis.close();

					if (obj instanceof BookModel) {
						BookModel b = (BookModel) obj;
						long t = System.currentTimeMillis();

						if (exportProvoider != null) {
							exportProvoider = new ExcelExport();
						}

						File f = exportProvoider.export(b, node.getProject(), this.template, this.doInvoker,
								this.doStatic);

						if (f == null) {
							Application.getInstance().getDesignSpective().getTask().getConsole().addText(
									step + "<a href='d://" + path + "'>" + node.toString() + "</a>がDTOであるためExcelへ変換なし",
									node.getProject());
						} else {
							Application.getInstance().getDesignSpective().getTask().getConsole()
									.addText(
											step + "<a href='f://" + f.getAbsolutePath() + "'>" + f.getName()
													+ "</a>へ変換しました(" + (System.currentTimeMillis() - t) / 1000 + "秒)",
											node.getProject());
						}

					}
					this.threads.remove(this);
				} catch (Exception e) {
					e.printStackTrace();
					this.e = e;
				}
				P.go();
				end = true;

			}

			public boolean isEnd() {
				return this.end;
			}

			public Exception getException() {
				return e;
			}
		}

		@Override
		protected void onFileSelected(File file) {

			if (file != null) {
				if (this.errorText != null) {
					this.errorText.setText("");
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			project = Application.getInstance().getCurrentProject();
			if (project == null) {
				return;
			}
			BeeTree tree = Application.getInstance().getDesignSpective().getFileExplore();
			List<Object> list = this.getAllNode(tree, project);

			List<BEditor> books = Application.getInstance().getDesignSpective().getWorkspace().getAllOpenningEditors();
			List<Object> defaults = new ArrayList<Object>();
			for (Object obj : list) {
				BeeTreeFileNode node = (BeeTreeFileNode) obj;
				for (BEditor book : books) {
					if (node.getFilePath().contains(book.getName())) {
						defaults.add(obj);
					}
				}
			}

			BeeListDialog dialog = new BeeListDialog(list, defaults);
			dialog.setBeforeSubmit(new BeforeActionLisener() {

				@Override
				public boolean beforeAction() {
					File file = getSelectedFile();
					if (file == null) {
						if (errorText == null) {
							errorText = new JLabel();
							errorText.setForeground(Color.RED);
							conditions.add(errorText, BorderLayout.NORTH);
						}
						errorText.setText("テンプレートを選択してください");
						return false;
					}
					return true;
				}

			});
			dialog.addComponent(this.createComps(dialog, project));
			dialog.showDialog("Excel(" + project.getName() + ")へ変換する設計対象選択");

			selected = dialog.getSelected();
			if (!selected.isEmpty()) {
				BeeThread t = new BeeThread(run);
				Application.getInstance().getDesignSpective().getTask().getConsole().start(t);
				t.start();

			}
		}

	}

	public static class BasicGenerate extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3984404200383005168L;
		private BasicBookModel model;
		private BProject project;
		private BasicBook book;

		public BasicGenerate(BasicBook sheet, BProject project) {
			this.book = sheet;
			this.model = sheet.getALLModel();
			this.project = project;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			doGenerateBasicRun(new Runnable() {

				@Override
				public void run() {
					model.validateLayers(book);
					Application.getInstance().getBasicSpective().getTask().setTaskConsoleSelected();
					ConsoleDisplay c = Application.getInstance().getBasicSpective().getTask().getConsole();
					c.clear();
					c.addText("Anylizing,wait please....", project);
					BasicGen gen = new BasicGen(model, project, c, new ArrayList<BParameter>());

					c.clear();
					gen.saveAll(c);
					c.end();
				}
			});

		}

		public void doGenerateBasicRun(Runnable run) {
			BeeThread t = new BeeThread(run);

			ThreadParameter parameter = new ThreadParameter();
			parameter.setDoLinkedObject(false);

			t.setUserObject(parameter);
			t.setName("BEE_RUN");
			Application.getInstance().getBasicSpective().getTask().getConsole().start(t);
			t.start();
		}

	}

	public static class GenerateTestCase extends BasicAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6391253105634843117L;
		private BModule module;
		private BProject project;

		public GenerateTestCase(BModule module, BProject project) {
			this.module = module;
			this.project = project;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			generate();

		}

		public File generate() {
			long time = System.currentTimeMillis();
			ConsoleDisplay c = Application.getInstance().getDesignSpective().getTask().getConsole();
			generateCase(c, project, module);
			return null;

		}

		public static void generateCase(ConsoleDisplay c, BProject project, BModule module) {
			c.addText("Start to generate...", project);
			BProvider p = new BProvider(null, null);

			c.addText("[4.詳細設計Excel]Excelエクスポートが開始します。 ", project);
			// do export
			File file = p.doDetailExport(project, module, null);// sqlSet not assigened
			if (file != null) {
				c.addText("[4.詳細設計Excel]<a href='f://" + file.getAbsolutePath() + "'>" + file.getName()
						+ "</a>へExcelエクスポートしました。", project);
			} else {
				c.addText("[4.詳細設計Excel]Excelエクスポートできませんでした。", project);
			}
		}

	}

}
