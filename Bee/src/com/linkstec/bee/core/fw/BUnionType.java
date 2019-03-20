package com.linkstec.bee.core.fw;

import java.util.List;

public interface BUnionType extends BObject {

	public void addType(BVariable var);

	public List<BVariable> getTypes();

}
