package com.linkstec.bee.UI.spective.basic;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.tree.TreePath;

import com.linkstec.bee.UI.BEditorFileExplorer;
import com.linkstec.bee.UI.config.Configuration;
import com.linkstec.bee.UI.spective.detail.tree.BeeTreeFileNode;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.io.ObjectFileUtils;

public class BasicFileExplore extends BEditorFileExplorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3668533842409433149L;

	private MouseAdapter adapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getSource() == BasicFileExplore.this && e.getClickCount() == 2) {

				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if (selPath != null) {
					Object obj = selPath.getLastPathComponent();
					if (obj instanceof BeeTreeFileNode) {
						BeeTreeFileNode node = (BeeTreeFileNode) obj;
						String path = node.getFilePath();
						File file = new File(path);

						new BeeThread(new Runnable() {

							@Override
							public void run() {
								if (Application.INSTANCE_COMPLETE) {
									Application.getInstance().getEditor().getStatusBar()
											.startProgress("openning " + file.getName() + "...");
								}
								openBook(file, node);
								if (Application.INSTANCE_COMPLETE) {
									Application.getInstance().getEditor().getStatusBar().endProgress();
								}
							}

						}).start();
						e.consume();
					}
				}
			}
		}
	};

	private void openBook(File file, BeeTreeFileNode node) {
		try {
			Object o = ObjectFileUtils.readObject(file);
			if (o instanceof BasicBookModel) {
				BasicBookModel model = (BasicBookModel) o;
				BEditor editor = model.getEditor(node.getProject(), file,
						Application.getInstance().getBasicSpective().getWorkspace());
				Application.getInstance().getBasicSpective().getWorkspace().insertTab(editor.getLogicName(),
						editor.getImageIcon(), (Component) editor, editor.getName(), 0);
			}
		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}

	@Override
	protected boolean addable(File file) {
		if (!file.isDirectory()) {
			return file.getName().endsWith("bl");
		}
		return true;
	}

	public BasicFileExplore(BeeTreeFileNode node, Configuration config) {
		super(node, config);
		this.addMouseListener(this.adapter);

	}

	@Override
	public String getRoot(BProject project) {
		return project.getRootPath() + File.separator + "basic";
	}

}
