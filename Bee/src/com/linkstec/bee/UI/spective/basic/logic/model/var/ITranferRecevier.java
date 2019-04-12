package com.linkstec.bee.UI.spective.basic.logic.model.var;

import com.linkstec.bee.core.fw.BValuable;

public interface ITranferRecevier {

	void setFireEvent(boolean b);

	void setText(String name);

	void setValue(BValuable invoker);

	void localMove(int hashCode);

	void updateUI();

}
