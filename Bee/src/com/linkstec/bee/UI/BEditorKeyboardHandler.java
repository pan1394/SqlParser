package com.linkstec.bee.UI;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import com.linkstec.bee.core.fw.editor.BEditor;

public class BEditorKeyboardHandler {
	public BEditorKeyboardHandler(BEditorManager manager) {
		JComponent comp = manager.getEditor().getContents();
		// InputMap inputMap =
		// getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		// SwingUtilities.replaceUIInputMap(comp,
		// JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		// InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		// SwingUtilities.replaceUIInputMap(comp, JComponent.WHEN_FOCUSED, inputMap);
		// SwingUtilities.replaceUIActionMap(comp, createActionMap(manager));
		this.getInputMap(JComponent.WHEN_FOCUSED, comp, manager);

	}

	private ActionMap createActionMap(BEditorManager manager) {
		BEditor editor = manager.getEditor();
		ActionMap map = SwingUtilities.getUIActionMap(editor.getContents());
		// map.put("refresh", new BEditorActions.RefreshAction(editor));
		map.put("search", new BEditorActions.SearchAction(editor));
		map.put("undo", new BEditorActions.UndoAction(manager.getUndo()));
		map.put("redo", new BEditorActions.RedoAction(manager.getUndo()));
		map.put("save", new BEditorActions.SaveAction(editor));
		map.put("saveAs", new BEditorActions.SaveAsAction(editor));
		map.put("new", new BEditorActions.NewAction(manager));
		// map.put("open", new EditorActions.OpenAction());

		if (editor instanceof BLayoutable) {
			map.put("refresh", new BEditorActions.LayoutAction((BLayoutable) editor));
		}
		if (editor instanceof BGeneratable) {
			map.put("generate", new BEditorActions.GenerateAction((BGeneratable) editor));
		}
		map.put("delete", new BEditorActions.DeleteSelectAction(editor));
		map.put("search", new BEditorActions.SearchAction(editor));

		map.put("selectAll", new BEditorActions.SelecAllAction(editor));
		map.put("cut", TransferHandler.getCutAction());
		map.put("copy", TransferHandler.getCopyAction());
		map.put("paste", TransferHandler.getPasteAction());
		return map;
	}

	protected InputMap getInputMap(int condition, JComponent comp, BEditorManager manager) {
		BEditor editor = manager.getEditor();
		InputMap inputmap = null;

		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
			inputmap = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
			return inputmap;
		} else if (condition == JComponent.WHEN_FOCUSED) {
			inputmap = comp.getInputMap();
		}

		inputmap.put(KeyStroke.getKeyStroke("F5"), "refresh");
		inputmap.put(KeyStroke.getKeyStroke("control F"), "search");

		inputmap.put(KeyStroke.getKeyStroke("control S"), "save");

		inputmap.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
		inputmap.put(KeyStroke.getKeyStroke("control N"), "new");
		inputmap.put(KeyStroke.getKeyStroke("control O"), "open");
		inputmap.put(KeyStroke.getKeyStroke("control G"), "generate");
		if (!(comp instanceof JTextPane)) {
			inputmap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "delete");
		}
		inputmap.put(KeyStroke.getKeyStroke("control Z"), "undo");
		inputmap.put(KeyStroke.getKeyStroke("control Y"), "redo");
		inputmap.put(KeyStroke.getKeyStroke("DELETE"), "delete");
		inputmap.put(KeyStroke.getKeyStroke("control F"), "search");

		inputmap.put(KeyStroke.getKeyStroke("control A"), "selectAll");
		inputmap.put(KeyStroke.getKeyStroke("control X"), "cut");
		inputmap.put(KeyStroke.getKeyStroke("CUT"), "cut");
		inputmap.put(KeyStroke.getKeyStroke("control C"), "copy");
		inputmap.put(KeyStroke.getKeyStroke("COPY"), "copy");
		inputmap.put(KeyStroke.getKeyStroke("control V"), "paste");
		inputmap.put(KeyStroke.getKeyStroke("PASTE"), "paste");
		comp.setInputMap(condition, inputmap);

		ActionMap map = comp.getActionMap();

		map.put("search", new BEditorActions.SearchAction(editor));
		map.put("undo", new BEditorActions.UndoAction(manager.getUndo()));
		map.put("redo", new BEditorActions.RedoAction(manager.getUndo()));
		map.put("save", new BEditorActions.SaveAction(editor));
		map.put("saveAs", new BEditorActions.SaveAsAction(editor));
		map.put("new", new BEditorActions.NewAction(manager));
		// map.put("open", new EditorActions.OpenAction());

		if (editor instanceof BLayoutable) {
			map.put("refresh", new BEditorActions.LayoutAction((BLayoutable) editor));
		}
		if (editor instanceof BGeneratable) {
			map.put("generate", new BEditorActions.GenerateAction((BGeneratable) editor));
		}
		map.put("delete", new BEditorActions.DeleteSelectAction(editor));
		map.put("search", new BEditorActions.SearchAction(editor));

		map.put("selectAll", new BEditorActions.SelecAllAction(editor));
		map.put("cut", TransferHandler.getCutAction());
		map.put("copy", TransferHandler.getCopyAction());
		map.put("paste", TransferHandler.getPasteAction());

		return inputmap;

	}

}
