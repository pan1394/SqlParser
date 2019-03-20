package com.linkstec.bee.core.fw.basic;

import java.util.List;

public interface IJudgeCell extends ILogicCell {

	public List<ILogicCell> getYes();

	public List<ILogicCell> getNo();
}
