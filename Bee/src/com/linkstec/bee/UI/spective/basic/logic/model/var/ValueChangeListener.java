package com.linkstec.bee.UI.spective.basic.logic.model.var;

import com.linkstec.bee.core.fw.BValuable;

public interface ValueChangeListener {
	public void changed(String messageID, int index, BValuable value);
}
