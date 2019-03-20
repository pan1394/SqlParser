package com.linkstec.bee.core.codec.decode;

import com.sun.source.util.TaskListener;

public interface BeeCompilerTaskListener extends TaskListener {
	public void setSourceNumber(int num);

	public int getSourceNumber();
}
