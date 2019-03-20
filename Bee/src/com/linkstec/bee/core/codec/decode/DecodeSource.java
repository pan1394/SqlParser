package com.linkstec.bee.core.codec.decode;

import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.sun.source.tree.LineMap;
import com.sun.source.util.DocTrees;

public class DecodeSource extends BasicSourceDecoder {

	public DecodeSource(DecodeGen gen, BProject project, LineMap map, DocTrees trees, SourceInfo sourceInfo, BookModel model) {
		super(gen, project, map, trees, sourceInfo, model);
	}

}