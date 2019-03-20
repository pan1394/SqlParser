package com.linkstec.bee.core.fw.editor;

public interface BEditorUndo {

	public void undo();

	public void redo();

	public void undoableEditHappened(Object obj);

	// public void addListener(String eventName, mxIEventListener listener);

}
