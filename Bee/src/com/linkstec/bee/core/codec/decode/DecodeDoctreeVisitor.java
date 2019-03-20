package com.linkstec.bee.core.codec.decode;

import java.util.Hashtable;
import java.util.List;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.IdentifierTree;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SerialTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.util.DocTreeScanner;

public class DecodeDoctreeVisitor extends DocTreeScanner<Object, Object> {

	@Override
	public Object visitAttribute(AttributeTree tree, Object arg1) {
		log("visitAttribute");
		return null;
	}

	@Override
	public Object scan(DocTree tree, Object parent) {
		log("scan");
		// tree.accept(this, parent);
		return super.scan(tree, parent);
	}

	@Override
	public Object scan(Iterable<? extends DocTree> tree, Object arg1) {
		log("scan");
		return super.scan(tree, arg1);
	}

	@Override
	public Object visitAuthor(AuthorTree tree, Object arg1) {
		log("visitAuthor");
		return null;
	}

	@Override
	public Object visitComment(CommentTree tree, Object arg1) {
		log("visitComment");
		return null;
	}

	@Override
	public Object visitDeprecated(DeprecatedTree tree, Object arg1) {
		log("visitDeprecated");
		return null;
	}

	@Override
	public Object visitDocComment(DocCommentTree tree, Object arg1) {

		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		log("visitDocComment:" + tree.toString());

		List<? extends DocTree> sentences = tree.getFirstSentence();
		String title = "";
		for (DocTree t : sentences) {
			Object obj = t.accept(this, tree);
			if (obj != null) {
				log(obj.toString());
				title = title + obj.toString().trim();
			}
		}
		if (!title.equals("")) {
			hash.put("title", title);
		}

		List<? extends DocTree> body = tree.getBody();

		for (DocTree t : body) {
			Object obj = t.accept(this, hash);
			if (obj != null) {
				log(obj.toString());
			}
		}

		List<? extends DocTree> list = tree.getBlockTags();

		for (DocTree t : list) {
			t.accept(this, hash);
		}

		return hash;
	}

	@Override
	public Object visitDocRoot(DocRootTree tree, Object arg1) {
		log("visitDocRoot" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitEndElement(EndElementTree tree, Object arg1) {
		log("visitEndElement" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitEntity(EntityTree tree, Object arg1) {
		log("visitEntity" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitErroneous(ErroneousTree tree, Object arg1) {
		log("visitErroneous" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierTree tree, Object arg1) {
		log("visitIdentifier" + ":" + tree.toString());
		return tree.getName().toString();
	}

	@Override
	public Object visitInheritDoc(InheritDocTree tree, Object arg1) {
		log("visitInheritDoc" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitLink(LinkTree tree, Object arg1) {
		log("visitLink" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitLiteral(LiteralTree tree, Object arg1) {
		log("visitLiteral" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitOther(DocTree tree, Object arg1) {
		log("visitOther" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitParam(ParamTree tree, Object obj) {
		log("visitParam" + ":" + tree.toString());

		Hashtable<String, Object> h = (Hashtable<String, Object>) obj;
		Object para = h.get("param");
		Hashtable<String, Object> paras = new Hashtable<String, Object>();
		if (para != null) {
			paras = (Hashtable<String, Object>) para;
		} else {
			h.put("param", paras);
		}

		IdentifierTree name = tree.getName();
		Object value = name.accept(this, null);

		List<? extends DocTree> desc = tree.getDescription();
		String values = "";
		for (DocTree d : desc) {
			values = values + d.accept(this, paras);
		}
		paras.put(value.toString(), values);
		return h;
	}

	@Override
	public Object visitReference(ReferenceTree tree, Object arg1) {
		log("visitReference" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitReturn(ReturnTree tree, Object hash) {
		log("visitReturn" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitSee(SeeTree tree, Object arg1) {
		log("visitSee" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitSerial(SerialTree tree, Object arg1) {
		log("visitSerial" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitSerialData(SerialDataTree tree, Object arg1) {
		log("visitSerialData" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitSerialField(SerialFieldTree tree, Object arg1) {
		log("visitSerialField" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitSince(SinceTree tree, Object arg1) {
		log("visitSince" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitStartElement(StartElementTree tree, Object arg1) {
		log("visitStartElement" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitText(TextTree tree, Object arg1) {
		log("visitText" + ":" + tree.toString());

		return tree.getBody();
	}

	@Override
	public Object visitThrows(ThrowsTree tree, Object arg1) {
		log("visitThrows" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitUnknownBlockTag(UnknownBlockTagTree tree, Object hash) {
		log("visitUnknownBlockTag" + ":" + tree.toString());
		Hashtable<String, String> h = (Hashtable<String, String>) hash;
		List<? extends DocTree> contents = tree.getContent();
		String tag = tree.getTagName();
		String c = "";
		for (DocTree t : contents) {
			c = c + ' ' + t.accept(this, null).toString().trim();
		}
		h.put(tag, c);
		return h;
	}

	@Override
	public Object visitUnknownInlineTag(UnknownInlineTagTree tree, Object hash) {
		log("visitUnknownInlineTag" + ":" + tree.toString());
		Hashtable<String, String> h = (Hashtable<String, String>) hash;
		List<? extends DocTree> contents = tree.getContent();
		String tag = tree.getTagName();
		String c = "";
		for (DocTree t : contents) {
			c = c + t.accept(this, null).toString().trim();
		}
		h.put(tag, c);
		return h;
	}

	@Override
	public Object visitValue(ValueTree tree, Object arg1) {
		log("visitValue" + ":" + tree.toString());
		return null;
	}

	@Override
	public Object visitVersion(VersionTree tree, Object arg1) {
		log("visitVersion" + ":" + tree.toString());
		return null;
	}

	private void log(String s) {
		/// System.err.println(s);
	}
}
