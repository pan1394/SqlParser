package com.linkstec.bee.UI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.TransferHandler;

import com.linkstec.bee.UI.look.menu.BeeMemuDisableListener;
import com.linkstec.bee.UI.look.menu.BeeMenu;
import com.linkstec.bee.UI.look.menu.BeeMenuItem;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.detail.BeeDataSheet;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.EditorBook;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.EditorActions;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.CodecAction;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class EditorMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4060203894740766714L;

	public enum AnalyzeType {
		IS_CONNECTED, IS_SIMPLE, IS_CYCLIC_DIRECTED, IS_CYCLIC_UNDIRECTED, COMPLEMENTARY, REGULARITY, COMPONENTS, MAKE_CONNECTED, MAKE_SIMPLE, IS_TREE, ONE_SPANNING_TREE, IS_DIRECTED, GET_CUT_VERTEXES, GET_CUT_EDGES, GET_SOURCES, GET_SINKS, PLANARITY, IS_BICONNECTED, GET_BICONNECTED, SPANNING_TREE, FLOYD_ROY_WARSHALL
	}

	public EditorMenuBar(final BeeEditor editor) {

		this.setOpaque(false);
		this.setPreferredSize(new Dimension(20, BeeUIUtils.getDefaultFontSize() * 2));

		BeeMenu menu = null;

		// Creates the file menu
		menu = (BeeMenu) add(new BeeMenu(mxResources.get("file")));

		BeeMenu submenu = new BeeMenu(mxResources.get("new"));
		submenu.setIcon(BeeConstants.NEW_ICON);
		menu.add(submenu);

		submenu.add(
				BeeActions.bind("新規プロジェクト", new EditorActions.ConfigAction(true), BeeConstants.NEW_PROJECT_ICON, null));
		submenu.addSeparator();

		submenu.add(BeeActions.bind("プロジェクトImport", new EditorActions.ImportProjectAction(),
				BeeConstants.PROJECT_IMPORT, null));
		submenu.addSeparator();

		submenu.add(BeeActions.bind("内部設計", new EditorActions.CodeConfigAction(), BeeConstants.CODE_CONFIG_ICON,
				this.projectActiveListner));

		submenu.add(BeeActions.bind("新規データ", new EditorActions.NewDataSheetAction(), BeeConstants.SHEET_DATA_ICON,
				this.projectActiveListner));
		submenu.add(BeeActions.bind("新規ロジック", new EditorActions.NewGraphSheetAction(), BeeConstants.SHEET_LOGIC_ICON,
				this.projectActiveListner));

		menu.addSeparator();

		menu.add(BeeActions.bind(mxResources.get("save"), new EditorActions.SaveAction(false), BeeConstants.SAVE_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind("名前をつけて保存する", new EditorActions.SaveAction(true), BeeConstants.SAVEAS_ICON,
				this.editorActiveListner));

		menu.addSeparator();

		menu.add(BeeActions.bind("印刷設定", new EditorActions.PageSetupAction(), BeeConstants.PAGESETUP_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind(mxResources.get("print"), new EditorActions.PrintAction(), BeeConstants.PRINT_ICON,
				this.editorActiveListner));

		menu.addSeparator();

		BeeMenuItem item = (BeeMenuItem) menu.add(
				BeeActions.bind(mxResources.get("exit"), new EditorActions.ExitAction(), BeeConstants.EXIT_ICON, null));
		item.setOnMenuBar(false);
		// Creates the edit menu
		menu = (BeeMenu) add(new BeeMenu(mxResources.get("edit")));

		menu.add(BeeActions.bind(mxResources.get("undo"), new EditorActions.HistoryAction(true), BeeConstants.UNDO_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind(mxResources.get("redo"), new EditorActions.HistoryAction(false),
				BeeConstants.REDO_ICON, this.editorActiveListner));

		menu.addSeparator();

		menu.add(BeeActions.bind(mxResources.get("cut"), TransferHandler.getCutAction(), BeeConstants.CUT_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), BeeConstants.COPY_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind(mxResources.get("paste"), TransferHandler.getPasteAction(),
				BeeConstants.MENU_PASTE_ICON, this.editorActiveListner));

		menu.add(BeeActions.bind(mxResources.get("delete"), mxGraphActions.getDeleteAction(), BeeConstants.DELETE_ICON,
				this.editorActiveListner));

		menu.addSeparator();

		menu.add(BeeActions.bind(mxResources.get("selectAll"), mxGraphActions.getSelectAllAction(),
				BeeConstants.SELECTALL_ICON, this.editorActiveListner));
		menu.add(BeeActions.bind("すべて解除", mxGraphActions.getSelectNoneAction(), BeeConstants.SELECTNONE_ICON,
				this.editorActiveListner));

		menu = (BeeMenu) add(new BeeMenu("検索"));
		menu.add(BeeActions.bind("Javaソース検索", new BEditorActions.SearchSourceAction(), BeeConstants.JAVA_SERACH_ICON,
				this.editorActiveListner));
		menu.add(BeeActions.bind("設計検索", new BEditorActions.SearchDesignAction(), BeeConstants.GRAPH_SEARCH_ICON,
				this.editorActiveListner));

		// Creates the project menu
		menu = (BeeMenu) add(new BeeMenu("プロジェクト"));
		menu.add(BeeActions.bind("設定", new EditorActions.ConfigAction(false), BeeConstants.CONFIG_ICON,
				this.projectActiveListner));

		menu = (BeeMenu) add(new BeeMenu("アクション"));
		menu.add(BeeActions.bind("ソース生成", new CodecAction.GenerateSource(), BeeConstants.GENERATE_CODE_ICON,
				this.hasGraphProjectListner));

		menu.add(BeeActions.bind("ソースクリーンアップ", new CodecAction.SourceCleanup(), BeeConstants.CLEANUP_ICON,
				this.hasSourceProjectListner));
		menu.add(BeeActions.bind("設計生成", new CodecAction.GenerateView(), BeeConstants.GENERATE_GRAPH_ICON,
				this.hasSourceProjectListner));
		menu.add(BeeActions.bind("Excelエクスポート", new CodecAction.ExportExcel(), BeeConstants.EXCEL_ICON,
				this.hasGraphProjectListner));
		menu.add(BeeActions.bind("設計検証", new CodecAction.CompileDesign(), BeeConstants.COMPILE_ICON,
				this.sheetActiveListner));
		// menu.add(BeeActions.bind("コメントなしソースエクスポート", new CodecAction.ExportJava(),
		// BeeConstants.EXPORT_ICON, this.sheetActiveListner));

		// Creates the help menu
		menu = (BeeMenu) add(new BeeMenu(mxResources.get("help")));

		Action help = new Action() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				editor.about();
			}

			@Override
			public Object getValue(String key) {

				return null;
			}

			@Override
			public void putValue(String key, Object value) {

			}

			@Override
			public void setEnabled(boolean b) {

			}

			@Override
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub

			}
		};

		menu.add(HelpAction("About Bee", BeeConstants.HELP_ICON, editor));

	}

	public static Action HelpAction(String name, ImageIcon icon, BeeEditor editor) {

		AbstractAction newAction = new AbstractAction(name, icon) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 8196768062499302415L;

			public void actionPerformed(ActionEvent e) {
				editor.about();
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, "help");
		return newAction;
	}

	public static Action EmptyAction(String name, ImageIcon icon) {

		AbstractAction newAction = new AbstractAction(name, icon) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 8196768062499302415L;

			public void actionPerformed(ActionEvent e) {

			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, "None");
		return newAction;
	}

	private BeeMemuDisableListener projectActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			return Application.getInstance().getCurrentProject() != null;
		}

	};
	private BeeMemuDisableListener subSystemActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			BProject project = Application.getInstance().getCurrentProject();
			if (project == null) {
				return false;
			}
			SubSystem sub = Application.getInstance().getBasicSpective().getMenu().getDataResurce(project)
					.getCurrentSub();
			return sub != null;
		}

	};

	public static BeeMemuDisableListener graphActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			BEditor editor = Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
			if (editor instanceof EditorBook) {
				EditorBook book = (EditorBook) editor;
				BEditor b = book.getCurrentEditor();
				if (b instanceof BeeGraphSheet) {
					return true;
				}
			}

			return false;
		}

	};

	public BeeMemuDisableListener dataActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			BEditor editor = Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
			if (editor instanceof EditorBook) {
				EditorBook book = (EditorBook) editor;
				BEditor b = book.getCurrentEditor();
				if (b instanceof BeeDataSheet) {
					return true;
				}
			}

			return false;

		}

	};

	private BeeMemuDisableListener sheetActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			BEditor editor = Application.getInstance().getDesignSpective().getWorkspace().getCurrentEditor();
			if (editor != null) {
				EditorBook book = (EditorBook) editor;
				BEditor b = book.getCurrentEditor();
				if (b != null) {
					return true;
				}
			}

			return false;

		}

	};

	private BeeMemuDisableListener editorActiveListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			BEditor editor = Application.getInstance().getCurrentEditor();
			if (editor != null) {

				return true;

			}

			return false;
		}

	};

	private BeeMemuDisableListener hasGraphProjectListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			return Application.getInstance().getCurrentProject() != null;
		}

	};

	private BeeMemuDisableListener hasSourceProjectListner = new BeeMemuDisableListener() {

		@Override
		public boolean enable(Object object) {
			return Application.getInstance().getCurrentProject() != null;

		}

	};

};