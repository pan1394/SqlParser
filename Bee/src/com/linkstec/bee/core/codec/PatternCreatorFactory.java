package com.linkstec.bee.core.codec;

import com.linkstec.bee.core.fw.IPatternCreator;

public class PatternCreatorFactory {
	private static IPatternCreator view = new ViewPatternCreator();
	private static IPatternCreator temp = new TempPatternCreator();

	public static IPatternCreator createView() {
		return view;
	}

	public static IPatternCreator createTempPattern() {
		return temp;
	}

}
