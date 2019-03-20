package com.linkstec.bee.UI.look.scroll;

public interface BeeScrollPaneErrorListener {

	public void error(String name, int line, String message, Object object);

	public void clearError(String name);
}
