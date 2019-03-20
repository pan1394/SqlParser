package com.linkstec.bee.UI.spective.code.tree;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorActions;
import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.BeeConfig;
import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.config.BeeProject;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.look.dialog.BeeOptionDialog;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.spective.code.BeeSourceSheet;
import com.linkstec.bee.UI.spective.detail.action.EditorActions;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.SourceCache;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BTreeNode;

public class BeeSourceTree extends BEditorFileExplorer implements TreeSelectionListener, TreeExpansionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458896748412912210L;

	// private BeeSourceTreeNode root;
	private boolean onTachExpand = false;
	private boolean showEmptDir = false;

	private static Clipboard system;

	private static BeeSourceTreeNode handler = new BeeSourceTreeNode(null, null);

	protected void installKeyboardActions() {

		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		this.registerKeyboardAction(new CopyAction(this), "Copy", copy, JComponent.WHEN_FOCUSED);
		this.registerKeyboardAction(new PasteAction(this), "Paste", paste, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();

	}

	public BeeSourceTree(Configuration beeconfig, boolean showEmptDir) {
		super(handler, beeconfig);
		this.showEmptDir = showEmptDir;
		this.installKeyboardActions();
		this.addTreeSelectionListener(this);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openFile(e);
			}

		});
		this.addTreeExpansionListener(this);

	}

	private void openFile(MouseEvent e) {
		TreePath selPath = BeeSourceTree.this.getPathForLocation(e.getX(), e.getY());
		if (e.getSource() == BeeSourceTree.this && e.getClickCount() == 2) {
			if (selPath != null) {
				Object obj = selPath.getLastPathComponent();
				if (obj instanceof BeeSourceTreeNode) {

					BeeSourceTreeNode node = (BeeSourceTreeNode) obj;
					File f = node.getFile();

					if (f != null) {
						if (f.exists() && !f.isDirectory()) {
							if (Application.getInstance().getCurrentProject() != null) {
								Application.getInstance().getJavaSourceSpective().addEditor(f,
										Application.getInstance().getCurrentProject());
							}
						}
					}
				}
			}
		}
	}

	protected void addPopupAction(BeeTreeNode n) {
		if (n == null) {
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this));
			return;
		}
		BeeSourceTreeNode node = (BeeSourceTreeNode) n;
		Object obj = node.getUserObject();
		if (obj != null && obj instanceof BProject) {
			BProject project = (BProject) obj;
			this.addPopupItem("削除", BeeConstants.DELETE_ICON, new DeleteAction(this));
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this));
			this.addPopupItem("プロパティ", BeeConstants.CONFIG_ICON, new EditorActions.ConfigAction(project));
		} else if (node.isSourceRoot()) {
			this.addPopupItem("プロパティ", BeeConstants.PROPERTY_ICON,
					new BEditorActions.FilePropertyAction(node.getFile()));
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this));
		} else if (node.isLibRoot()) {
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this));
		} else if (node.isLib()) {
			this.addPopupItem("削除", BeeConstants.DELETE_ICON, new DeleteAction(this));
			String path = (String) node.getUserObject();
			this.addPopupItem("プロパティ", BeeConstants.PROPERTY_ICON,
					new BEditorActions.FilePropertyAction(new File(path)));
		} else {
			this.addPopupItem("削除", BeeConstants.DELETE_ICON, new DeleteAction(this));
			this.addPopupItem("最新情報に更新", BeeConstants.REFRESH_ICON, new RefreshAction(this));
			this.addPopupItem("プロパティ", BeeConstants.PROPERTY_ICON,
					new BEditorActions.FilePropertyAction(node.getFile()));
		}
	}

	public void initAll() {

		List<BProject> projects = config.getProjects();
		for (BProject p : projects) {
			this.makeProject(p);
		}
		this.expandPath(new TreePath(handler));

	}

	public void updateNode(File file, BProject project) {
		BeeSourceTreeNode node = (BeeSourceTreeNode) this.lookupNode(file, project);
		if (node != null) {
			this.updateNode(node);
		}
	}

	public BTreeNode lookupNode(File file, BProject project) {
		BTreeNode root = (BeeSourceTreeNode) this.getSourceRoot(project);
		return this.lookupNode(file, root);
	}

	private void updateNode(BeeSourceTreeNode node) {
		// lib root
		if (node.isLibRoot()) {
			this.updateLibrary(node);
			return;
		}
		// lib node
		if (node.isLib()) {
			BeeSourceTreeNode lib = this.getLibRoot(node);
			this.updateLibrary(lib);
			return;
		}
		// project root
		if (node.getUserObject() instanceof BProject) {
			BeeSourceTreeNode temp = (BeeSourceTreeNode) node.clone();
			// only when all the directory is under src folder immediately
			this.makeProjecContents(temp);
			this.updateOldNode(temp, node);
			this.expandPath(new TreePath(node));
			return;
		}
		// others=sources
		if (this.getSourceRoot(node) == null) {
			// project
			node = this.getSourceRoot(node.getProject());
		}

		BeeSourceTreeNode temp = (BeeSourceTreeNode) node.clone();
		// only when all the directory is under src folder immediately
		this.makeNodes(temp, temp);
		this.updateOldNode(temp, node);
		this.expandPath(new TreePath(node));
	}

	private void makeProject(BProject project) {

		BeeSourceTreeNode pNod = new BeeSourceTreeNode(null, project);
		pNod.setDisplay(project.getName());
		pNod.setImageIcon(BeeConstants.PROJECT_ICON);
		pNod.setLeaf(false);
		pNod.setUserObject(project);
		pNod.setProject(project);

		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		model.insertNodeInto(pNod, handler, handler.getChildCount());

		this.makeProjecContents(pNod);

	}

	private void makeProjecContents(BeeSourceTreeNode pNod) {
		BProject project = pNod.getProject();
		BeeSourceTreeNode lib = new BeeSourceTreeNode(null, project);
		lib.setImageIcon(BeeConstants.CONFIG_ICON);
		lib.setDisplay("lib");
		lib.setLeaf(false);
		lib.setLib(true);
		lib.setLibRoot(true);
		lib.setProject(project);
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		model.insertNodeInto(lib, pNod, pNod.getChildCount());

		this.makeLibrary(lib);

		BeeSourceTreeNode source = new BeeSourceTreeNode(null, project);
		source.setImageIcon(BeeConstants.FOLDER_ICON);
		source.setDisplay("src");
		source.setSourceRoot(true);
		source.setFile(new File(project.getSourcePath()));

		model.insertNodeInto(source, pNod, pNod.getChildCount());

		this.makeNodes(source, source);
	}

	public void makeLibrary(BeeSourceTreeNode lib) {
		BProject project = lib.getProject();
		lib.removeAllChildren();
		String libs = project.getLibPath();

		if (libs == null) {
			libs = "";
		}
		if (libs.indexOf(File.pathSeparator) >= 0) {
			String[] libss = libs.split(File.pathSeparator);
			for (String libname : libss) {
				if (!libname.equals("")) {
					this.makeLibNode(libname, lib, project);
				}

			}
		} else if (!libs.equals("")) {
			this.makeLibNode(libs, lib, project);
		} else {
			String path = project.getSourcePath() + File.pathSeparator + "lib";
			File file = new File(path);
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (File f : files) {
						this.makeLibNode(f.getAbsolutePath(), lib, project);
					}
				}
			}
		}
	}

	private void makeLibNode(String libname, BeeSourceTreeNode lib, BProject project) {
		BeeSourceTreeNode l = new BeeSourceTreeNode(null, project);
		File f = new File(libname);

		l.setDisplay(f.getName());
		if (f.isDirectory()) {
			l.setImageIcon(BeeConstants.LIB_FOLDER_ICON);
		} else {
			l.setImageIcon(BeeConstants.JAR_ICON);
		}
		l.setLib(true);
		l.setLeaf(true);
		l.setUserObject(libname);
		l.setProject(project);

		if (!f.exists()) {
			l.setError(true);
		}

		lib.add(l);
	}

	public boolean isShowEmptDir() {
		return showEmptDir;
	}

	public void setShowEmptDir(boolean showEmptDir) {
		this.showEmptDir = showEmptDir;
	}

	public boolean isOnTachExpand() {
		return onTachExpand;
	}

	public void setOnTachExpand(boolean onTachExpand) {
		this.onTachExpand = onTachExpand;
	}

	public BeeSourceTreeNode getProjectRoot(BeeSourceTreeNode node) {
		if (node.getUserObject() != null && node.getUserObject() instanceof BProject) {
			return node;
		}
		BeeSourceTreeNode parent = (BeeSourceTreeNode) node.getParent();
		if (parent != null) {
			return getProjectRoot(parent);
		}
		return null;
	}

	public BeeSourceTreeNode getSourceRoot(BeeSourceTreeNode node) {
		if (node.isSourceRoot()) {
			return node;
		}
		BeeSourceTreeNode parent = (BeeSourceTreeNode) node.getParent();
		if (parent != null) {
			return getSourceRoot(parent);
		}
		return null;
	}

	public BeeSourceTreeNode getSourceRoot(BProject project) {
		return this.getSourceRoot(project, handler);
	}

	private BeeSourceTreeNode getSourceRoot(BProject project, BeeSourceTreeNode node) {

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeSourceTreeNode child = (BeeSourceTreeNode) node.getChildAt(i);
			if (child.isSourceRoot() && child.getProject().getName().equals(project.getName())) {
				return child;
			}
			BeeSourceTreeNode next = this.getSourceRoot(project, child);
			if (next != null) {
				return next;
			}
		}
		return null;
	}

	private BeeSourceTreeNode getProjectRoot(BProject project) {

		int count = handler.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeSourceTreeNode child = (BeeSourceTreeNode) handler.getChildAt(i);
			Object obj = child.getUserObject();
			if (obj instanceof BProject) {
				BProject b = (BProject) obj;
				if (b.getName().equals(project.getName())) {
					return child;
				}
			}
		}
		return null;
	}

	public BeeSourceTreeNode getLibRoot(BeeSourceTreeNode node) {
		if (node.isLibRoot()) {
			return node;
		}
		BeeSourceTreeNode parent = (BeeSourceTreeNode) node.getParent();
		if (parent != null) {
			return getLibRoot(parent);
		}
		return null;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

	@Override
	public void importFiles(List<File> files) {
		Point p = this.getMousePosition();
		if (p != null) {
			TreePath path = this.getPathForLocation(p.x, p.y);
			if (path != null) {
				this.setSelectionPath(path);
				PasteAction action = new PasteAction(this);
				action.doExecute(files);
			}
		}
	}

	@Override
	public boolean canImportFiles() {
		return true;
	}

	@Override
	public void importData(String data) {
	}

	@Override
	public boolean canImportData() {
		return false;
	}

	public void doDelete() {
		TreePath[] pathes = this.getSelectionPaths();
		Hashtable<BProject, String> changedPath = new Hashtable<BProject, String>();
		for (TreePath path : pathes) {
			Object obj = path.getLastPathComponent();
			if (obj != null && obj instanceof BeeSourceTreeNode) {
				BeeSourceTreeNode node = (BeeSourceTreeNode) obj;
				if (node.getUserObject() != null && node.getUserObject() instanceof BProject) {
					BProject project = (BProject) this.getProjectRoot(node).getUserObject();
					int r = JOptionPane.showConfirmDialog(Application.FRAME, project.getName() + "を削除します。よろしいですか？",
							"プロジェクト削除", JOptionPane.OK_CANCEL_OPTION);

					if (r == JOptionPane.OK_OPTION) {
						Application.getInstance().deleteProject(project);
					}
					return;
				}

				BProject project = node.getProject();
				if (node.isLibRoot()) {
					return;
				}
				if (node.isSourceRoot()) {
					return;
				}
				if (node.isLib()) {

					String target = (String) node.getUserObject();
					if (changedPath.get(project) == null) {
						changedPath.put(project, project.getLibPath());
					}
					String libs = changedPath.get(project);
					String withSeparator = target + File.pathSeparator;
					int index = libs.indexOf(withSeparator);
					if (index > -1) {
						libs = libs.substring(0, index) + libs.substring(index + withSeparator.length());
					} else {
						index = libs.indexOf(target);
						libs = libs.substring(0, index) + libs.substring(index + target.length());
					}
					File f = new File(target);
					if (f.exists()) {
						f.delete();
					}

					changedPath.put(project, libs);

				} else {
					File file = node.getFile();
					if (file.exists()) {
						this.deleteFile(file);
						Application.getInstance().getJavaSourceSpective().fileDeleted(file, project);
					}
				}

				DefaultTreeModel model = (DefaultTreeModel) this.getModel();
				model.removeNodeFromParent(node);
			}
		}

		Enumeration<BProject> en = changedPath.keys();
		boolean changed = false;
		while (en.hasMoreElements()) {
			BeeProject project = (BeeProject) en.nextElement();
			String libs = changedPath.get(project);
			if (!libs.equals(project.getLibPath())) {
				// here it will fire event to compile sources
				project.setLibPath(libs);
				changed = true;
			}
		}
		if (changed) {
			BeeConfig config = Application.getInstance().getConfigSpective();
			config.save();
		}
	}

	public void deleteNode(File file, BProject project) {
		BeeSourceTreeNode node = this.getSourceRoot(project);
		BeeSourceTreeNode target = this.lookupNodeByFile(node, file);
		if (target != null) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			model.removeNodeFromParent(node);
			if (file.exists()) {
				this.deleteFile(file);
				Application.getInstance().getJavaSourceSpective().fileDeleted(file, project);
			}
		}
	}

	private BeeSourceTreeNode lookupNodeByFile(BeeSourceTreeNode node, File file) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			BeeSourceTreeNode child = (BeeSourceTreeNode) node.getChildAt(i);
			if (child.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
				return child;
			} else {
				BeeSourceTreeNode target = this.lookupNodeByFile(child, file);
				if (target != null) {
					return target;
				}
			}
		}
		return null;
	}

	public void doRefresh() {
		TreePath path = this.getSelectionPath();
		if (path == null) {
			this.updateAllProject();
			return;
		}
		BeeSourceTreeNode node = (BeeSourceTreeNode) path.getLastPathComponent();
		if (node == null) {
			return;
		}

		this.updateNode(node);

	}

	public void updateAllProject() {
		Configuration config = Application.getInstance().getConfigSpective().getConfig();
		List<BProject> projects = config.getProjects();
		List<BeeSourceTreeNode> removes = new ArrayList<BeeSourceTreeNode>();
		List<BeeSourceTreeNode> sames = new ArrayList<BeeSourceTreeNode>();
		List<BProject> adds = new ArrayList<BProject>();
		adds.addAll(projects);

		int count = handler.getChildCount();

		for (int i = 0; i < count; i++) {
			BeeSourceTreeNode node = (BeeSourceTreeNode) handler.getChildAt(i);
			BProject bp = node.getProject();
			boolean found = false;
			for (BProject p : projects) {
				if (bp.getName().equals(p.getName())) {
					sames.add(node);
					adds.remove(p);
					found = true;
				}
			}
			if (!found) {
				removes.add(node);
			}
		}
		for (BeeSourceTreeNode node : removes) {
			DefaultTreeModel model = (DefaultTreeModel) this.getModel();
			model.removeNodeFromParent(node);
		}
		for (BeeSourceTreeNode node : sames) {
			this.updateNode(node);
		}

		for (BProject project : adds) {
			this.makeProject(project);
		}
	}

	public void updateLibrary(BeeSourceTreeNode node) {
		BeeSourceTreeNode temp = (BeeSourceTreeNode) node.clone();
		this.makeLibrary(temp);
		this.updateOldNode(temp, node);
		this.expandPath(new TreePath(node));
	}

	public void makeNodes(BeeSourceTreeNode parent, BeeSourceTreeNode sourceRoot) {
		BProject project = parent.getProject();
		File f = parent.getFile();
		parent.setSubConfiged(true);

		if (f != null) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.isDirectory()) {
							File[] subs = file.listFiles();
							boolean makeNode = false;
							if (subs != null) {
								for (File s : subs) {
									if (!s.isDirectory() && !s.getPath().endsWith(".class")) {
										makeNode = true;
										break;
									}
								}
							}

							BeeSourceTreeNode node = new BeeSourceTreeNode(file, project);

							if (makeNode) {
								node.setLeaf(false);
								DefaultTreeModel model = (DefaultTreeModel) this.getModel();
								model.insertNodeInto(node, sourceRoot, sourceRoot.getChildCount());

							} else {
								if (this.showEmptDir) {

									node.setLeaf(false);
									node.setImageIcon(BeeConstants.EMPTY_DIR_ICON);

									DefaultTreeModel model = (DefaultTreeModel) this.getModel();
									model.insertNodeInto(node, sourceRoot, sourceRoot.getChildCount());
									makeNodes(node, sourceRoot);
								}
							}
							makeNodes(node, sourceRoot);
						} else {
							BeeSourceTreeNode node = new BeeSourceTreeNode(file, project);

							parent.add(node);
							node.setLeaf(true);
							SourceCache.addSource(file, project);
						}
					}
				}
			}
		}
	}

	public static class PasteAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6141355501311785903L;
		private BeeSourceTree tree;

		public PasteAction(BeeSourceTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new BeeThread(new Runnable() {

				@Override
				public void run() {

					execute();
				}
			}).start();
		}

		private void execute() {
			try {
				if (BeeSourceTree.system.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
					@SuppressWarnings({ "unchecked" })
					List<File> files = (List<File>) BeeSourceTree.system.getContents(this)
							.getTransferData(DataFlavor.javaFileListFlavor);

					this.doExecute(files);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		public void doExecute(List<File> files) {
			if (files != null && !files.isEmpty()) {
				TreePath path = tree.getSelectionPath();
				if (path == null) {
					return;
				}
				BeeSourceTreeNode node = (BeeSourceTreeNode) path.getLastPathComponent();

				BeeSourceTreeNode libRoot = null;
				if (node.isLibRoot()) {
					libRoot = node;
				} else if (node.isLib()) {
					libRoot = (BeeSourceTreeNode) node.getParent();
				}
				if (libRoot != null) {
					BeeConfig config = Application.getInstance().getConfigSpective();
					BeeProject project = (BeeProject) libRoot.getProject();

					String libs = project.getLibPath();

					for (File f : files) {
						BeeSourceTreeNode lib = new BeeSourceTreeNode(f, project);
						lib.setDisplay(f.getName());
						boolean isLib = false;

						if (f.isDirectory()) {
							lib.setImageIcon(BeeConstants.FOLDER_ICON);
							isLib = true;
						} else {
							if (f.getName().endsWith("jar") || f.getName().endsWith("zip")) {
								lib.setImageIcon(BeeConstants.JAR_ICON);
								isLib = true;
							}
						}

						String root = project.getRootPath();
						File rootDir = new File(root + File.separator + "lib");
						if (!rootDir.exists()) {
							rootDir.mkdirs();
						}
						File newFile = new File(rootDir.getAbsolutePath() + File.separator + f.getName());
						this.copyGeneralFile(f.getAbsolutePath(), rootDir.getAbsolutePath(), project);

						if (isLib) {
							if (libs == null || libs.trim().equals("")) {
								libs = newFile.getAbsolutePath();
							} else {
								if (libs.indexOf(f.getName()) > -1) {
									// JOptionPane.showMessageDialog(Application.FRAME,
									// f.getAbsolutePath() + "はすでに存在しています。", "インポートエラー", JOptionPane.YES_OPTION);
									continue;
								} else {
									libs = libs + File.pathSeparator + newFile.getAbsolutePath();
								}
							}

						}
					}
					project.setLibPath(libs);
					config.save();
					BeeSourceTreeNode lib = tree.getLibRoot(node);
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					tree.expandPath(new TreePath(model.getPathToRoot(lib)));
					tree.updateNode(tree.getLibRoot(node));

					return;
				}
				File f = node.getFile();
				for (File s : files) {
					if (f.isDirectory()) {
						copyGeneralFile(s.getAbsolutePath(), f.getAbsolutePath(), node.getProject());
					} else {
						File dir = f.getParentFile();
						copyGeneralFile(s.getAbsolutePath(), dir.getAbsolutePath(), node.getProject());
					}
				}

				tree.doRefresh();
				Application.getInstance().getJavaSourceSpective().fileAdded(files, node.getProject());
			}
		}

		public boolean copyGeneralFile(String srcPath, String destDir, BProject project) {
			boolean flag = false;
			File file = new File(srcPath);
			if (!file.exists()) {
				BeeOptionDialog.showDialog("コピー", "コピー元のファイルが存在していません。", BeeOptionDialog.OK_MODE);

				return false;
			}
			if (file.isFile()) {

				flag = copyFile(srcPath, destDir, project);
			} else if (file.isDirectory()) {

				flag = copyDirectory(srcPath, destDir, project);
			}

			return flag;
		}

		private boolean copyDirectory(String srcPath, String destDir, BProject project) {

			boolean flag = false;

			File srcFile = new File(srcPath);
			if (!srcFile.exists()) {
				BeeOptionDialog.showDialog("コピー", "コピー元のファイルが存在していません。", BeeOptionDialog.OK_MODE);

				return false;
			}

			String dirName = getDirName(srcPath);

			String destPath = destDir + File.separator + dirName;

			// if (destPath.equals(srcPath)) {
			// if (!showDialog(dirName + "が存在しています。上書きします。よろしいでしょう？")) {
			// return false;
			// }
			//
			// }
			File destDirFile = new File(destPath);
			if (destDirFile.exists()) {
				// if (!showDialog(destDirFile.getName() + "が存在しています。上書きします。よろしいでしょう？")) {
				// return false;
				// }
			} else {
				destDirFile.mkdirs();
			}

			File[] fileList = srcFile.listFiles();
			if (fileList.length == 0) {
				flag = true;
			} else {
				for (File temp : fileList) {
					if (temp.isFile()) {
						flag = copyFile(temp.getAbsolutePath(), destPath, project);
					} else if (temp.isDirectory()) {
						flag = copyDirectory(temp.getAbsolutePath(), destPath, project);
					}
					if (!flag) {
						break;
					}
				}
			}

			return flag;
		}

		private static String getDirName(String dir) {
			if (dir.endsWith(File.separator)) {
				dir = dir.substring(0, dir.lastIndexOf(File.separator));
			}
			return dir.substring(dir.lastIndexOf(File.separator) + 1);
		}

		private boolean copyFile(String srcPath, String destDir, BProject project) {
			boolean flag = false;

			File srcFile = new File(srcPath);
			if (!srcFile.exists()) {

				BeeOptionDialog.showDialog("コピー", "コピー元のファイルが存在していません。", BeeOptionDialog.OK_MODE);
				return false;
			}

			String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
			String destPath = destDir + fileName;
			if (destPath.equals(srcPath)) {
				BeeOptionDialog.showDialog("コピー", "コピー元とコピー先が同じでコピーを実施できません。", BeeOptionDialog.OK_MODE);

				return false;
			}
			File destFile = new File(destPath);
			if (destFile.exists() && destFile.isFile()) {
				if (!showDialog(destFile.getName() + "が存在しています。上書きします。よろしいでしょう？")) {
					return false;
				}

			}

			File destFileDir = new File(destDir);
			destFileDir.mkdirs();
			try {
				FileInputStream fis = new FileInputStream(srcPath);
				FileOutputStream fos = new FileOutputStream(destFile);
				byte[] buf = new byte[1024];
				int c;
				while ((c = fis.read(buf)) != -1) {
					fos.write(buf, 0, c);
				}
				fis.close();
				fos.close();

				flag = true;
			} catch (IOException e) {
				//
			}

			BeeTabbedPane pane = Application.getInstance().getJavaSourceSpective().getWorkspace();
			int count = pane.getTabCount();
			for (int i = 0; i < count; i++) {

				BeeSourceSheet sheet = (BeeSourceSheet) pane.getComponentAt(i);

				if (sheet.getFile().getAbsolutePath().equals(destFile.getAbsolutePath())) {
					Application.getInstance().getJavaSourceSpective().addEditor(destFile, project);
				}
			}

			return flag;
		}

		private boolean isLater() {
			Thread t = Thread.currentThread();
			if (t instanceof BeeThread) {
				BeeThread b = (BeeThread) t;
				Boolean later = b.getLater();
				if (later == null) {
					return false;
				}
				return later.booleanValue();
			}
			return false;
		}

		private void setLater(boolean later) {
			Thread t = Thread.currentThread();
			if (t instanceof BeeThread) {
				BeeThread b = (BeeThread) t;
				b.setLater(later);
			}
		}

		private boolean showDialog(String message) {
			boolean test = true;
			if (test) {
				return false;
			}
			boolean showDialog = true;
			Boolean later = isLater();
			if (later != null) {
				if (!later.booleanValue()) {
					return false;
				} else {
					showDialog = false;
				}
			}
			if (showDialog) {
				BeeOptionDialog dialog = BeeOptionDialog.showDialog("コピー", message, BeeOptionDialog.YES_NO_ANDLATER);

				if (dialog.isLater()) {
					setLater(dialog.yes());
				}

				if (!dialog.yes()) {
					return false;
				}
			}
			return true;
		}
	}

	public static class CopyAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2958080270410223793L;
		private BeeSourceTree tree;

		public CopyAction(BeeSourceTree tree) {
			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	public static class BeeThread extends Thread {
		public Boolean later = null;

		public BeeThread(Runnable runnable) {
			super(runnable);
		}

		public void setLater(boolean later) {
			this.later = later;
		}

		public Boolean getLater() {
			return this.later;
		}
	}

	@Override
	public void addProject(BProject project) {
		this.updateAllProject();

	}

	@Override
	public void deleteProject(BProject project, boolean deleteFiles) {
		this.updateAllProject();

	}

	@Override
	public void updateProject(BProject project) {
		BeeSourceTreeNode node = this.getProjectRoot(project);
		BeeSourceTreeNode temp = (BeeSourceTreeNode) node.clone();
		// only when all the directory is under src folder immediately
		this.makeProjecContents(temp);
		this.updateOldNode(temp, node);
		this.expandPath(new TreePath(node));

	}

	@Override
	public String getRoot(BProject project) {
		return project.getSourcePath();
	}
}
