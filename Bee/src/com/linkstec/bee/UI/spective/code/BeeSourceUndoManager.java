package com.linkstec.bee.UI.spective.code;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;

import com.linkstec.bee.core.fw.editor.BEditorUndo;

public class BeeSourceUndoManager extends UndoManager implements BEditorUndo {

	private static final long serialVersionUID = -5130444797522545316L;

	public BeeSourceUndoManager() {

	}

	@Override
	public void undoableEditHappened(Object obj) {
		super.undoableEditHappened((UndoableEditEvent) obj);

	}
}
