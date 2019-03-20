package com.linkstec.bee.UI.spective.code.tree;

import java.awt.datatransfer.DataFlavor;

public class BeeSourceFlavor extends DataFlavor {

	BeeSourceFlavor() {

	}

	@Override
	public Class<?> getRepresentationClass() {

		return String.class;
	}

}
