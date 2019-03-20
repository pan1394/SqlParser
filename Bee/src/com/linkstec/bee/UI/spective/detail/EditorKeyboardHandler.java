
package com.linkstec.bee.UI.spective.detail;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.linkstec.bee.UI.BEditorActions;
import com.linkstec.bee.UI.spective.detail.action.EditorActions;
import com.linkstec.bee.core.codec.CodecAction;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.util.mxGraphActions;

/**
 * @author Administrator
 * 
 */
public class EditorKeyboardHandler extends mxKeyboardHandler {

	mxGraphComponent graphComponent;

	/**
	 * 
	 * @param graphComponent
	 */
	public EditorKeyboardHandler(mxGraphComponent graphComponent) {
		super(graphComponent);
		this.graphComponent = graphComponent;
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition) {
		InputMap map = super.getInputMap(condition);

		if (condition == JComponent.WHEN_FOCUSED && map != null) {
			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			// map.put(KeyStroke.getKeyStroke("control O"), "open");
			map.put(KeyStroke.getKeyStroke("control G"), "generate");
			map.put(KeyStroke.getKeyStroke("BACK_SPACE"), "delete");
			map.put(KeyStroke.getKeyStroke("control F"), "search");
			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			map.put(KeyStroke.getKeyStroke("control shift V"), "selectVertices");
			map.put(KeyStroke.getKeyStroke("control shift E"), "selectEdges");

			map.put(KeyStroke.getKeyStroke("F5"), "relayout");
		}

		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap() {
		ActionMap map = super.createActionMap();

		map.put("save", new EditorActions.SaveAction(false));
		map.put("saveAs", new EditorActions.SaveAction(true));
		map.put("new", new EditorActions.NewAction());
		// map.put("open", new EditorActions.OpenAction());
		map.put("undo", new EditorActions.HistoryAction(true));
		map.put("redo", new EditorActions.HistoryAction(false));
		map.put("selectVertices", mxGraphActions.getSelectVerticesAction());
		map.put("selectEdges", mxGraphActions.getSelectEdgesAction());
		// map.put("relayout", new EditorActions.LayoutAction());
		map.put("generate", new CodecAction.GenerateSource());
		map.put("delete", mxGraphActions.getDeleteAction());
		map.put("search", new BEditorActions.SearchDesignAction());

		return map;
	}

}
