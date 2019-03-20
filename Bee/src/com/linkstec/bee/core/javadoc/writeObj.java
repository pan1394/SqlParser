package com.linkstec.bee.core.javadoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class writeObj {

	static File or;
	static File[] files;

	// 用于遍历文件价
	public static void iteratorPath(String dir) throws IOException {
		or = new File(dir);
		files = or.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					Html2Obj(file.getPath());
				} else if (file.isDirectory()) {
					if (!file.getName().equals("class-use"))
						iteratorPath(file.getAbsolutePath());
				}
			}
		}
	}

	private static String target = "D:\\bee\\javadoc_analize\\";

	public static void main(String[] args) throws IOException {
		String directory = "D:\\bee\\javadoc_japanese";
		iteratorPath(directory);
	}

	public static void Html2Obj(String filename) throws IOException {
		File input = new File(filename);
		Document doc = Jsoup.parse(input, "UTF-8", "");
		DocClass docClass = new DocClass();
		Elements name = doc.select("li.blocklist pre span");
		if (name.size() == 0) {
			return;
		}
		docClass.setName(name.get(0).text());
		Elements comment = doc.select("li.blocklist div.block");
		if (comment.size() > 0)
			docClass.setComment(comment.get(0).text());
		Elements detail = doc.select("div.details>ul.blocklist>li.blocklist>ul.blocklist>li.blocklist");
		if (detail.select("a[name=field.detail]").size() > 0) {
			Elements fields = detail.select("a[name=field.detail]").get(0).parent().select("ul.blocklist");
			List<DocField> fieldList = new ArrayList<DocField>();
			for (int i = 0; i < fields.size(); i++) {
				DocField docfield = new DocField();
				docfield.setName(fields.get(i).select("h4").get(0).text());
				if (fields.get(i).select("div.block").size() > 0)
					docfield.setComment(fields.get(i).select("div.block").get(0).text());
				fieldList.add(docfield);
			}
			docClass.setFieldList(fieldList);
		}

		if (detail.select("a[name=constructor.detail]").size() > 0) {
			Elements constructors = detail.select("a[name=constructor.detail]").get(0).parent().select("ul.blocklist");
			List<DocMethod> constructorList = new ArrayList<DocMethod>();
			for (int i = 0; i < constructors.size(); i++) {
				DocMethod constructor = new DocMethod();
				constructor.setName(constructors.get(i).select("h4").get(0).text());
				if (constructors.get(i).select("div.block").size() > 0)
					constructor.setComment(constructors.get(i).select("div.block").get(0).text());
				if (constructors.get(i).select("dt:has(.paramLabel)").size() > 0) {
					Element paramLabel = constructors.get(i).select("dt:has(.paramLabel)").get(0).nextElementSibling();
					constructor.setParameter(new ArrayList<DocType>());
					while (paramLabel != null && paramLabel.is("dd")) {
						DocType param = new DocType();
						param.setName(paramLabel.text().split("-")[0]);
						if (paramLabel.text().split("-").length > 1)
							param.setComment(paramLabel.text().split("-")[1]);
						HashMap<String, String> typeEle = resolutionParam(constructors.get(i));
						String key = paramLabel.text().split("-")[0].trim();
						param.setType(typeEle.get(key));
						constructor.getParameter().add(param);
						paramLabel = paramLabel.nextElementSibling();
					}
				}
				if (constructors.get(i).select("dt:has(.throwsLabel)").size() > 0) {
					Element throwsLabel = constructors.get(i).select("dt:has(.throwsLabel)").get(0)
							.nextElementSibling();
					constructor.setExceptionStr(new ArrayList<DocType>());
					while (throwsLabel != null && throwsLabel.is("dd")) {
						DocType exp = new DocType();
						exp.setName(throwsLabel.text().split("-")[0]);
						if (throwsLabel.text().split("-").length > 1)
							exp.setComment(throwsLabel.text().split("-")[1]);
						if (throwsLabel.select("a").size() > 0) {
							String regex = "[内のクラス]";
							Pattern pat = Pattern.compile(regex);
							Matcher mat = pat.matcher(throwsLabel.select("a").get(0).attr("title"));
							String repickStr = mat.replaceAll("");
							exp.setType(repickStr + "." + exp.getName());
						}
						constructor.getExceptionStr().add(exp);
						throwsLabel = throwsLabel.nextElementSibling();
					}
				}
				constructorList.add(constructor);
			}
			docClass.setConstructorList(constructorList);
		}

		if (detail.select("a[name=method.detail]").size() > 0) {
			Elements methodes = detail.select("a[name=method.detail]").get(0).parent().select("li.blocklist");
			List<DocMethod> methodList = new ArrayList<DocMethod>();
			for (int i = 0; i < methodes.size(); i++) {
				DocMethod methode = new DocMethod();
				methode.setName(methodes.get(i).select("h4").get(0).text());
				if (methodes.get(i).select("div.block").size() > 0)
					methode.setComment(methodes.get(i).select("div.block").get(0).text());
				if (methodes.get(i).select("dt:has(.paramLabel)").size() > 0) {
					Element paramLabel = methodes.get(i).select("dt:has(.paramLabel)").get(0).nextElementSibling();
					methode.setParameter(new ArrayList<DocType>());
					while (paramLabel != null && paramLabel.is("dd")) {
						DocType param = new DocType();
						param.setName(paramLabel.text().split("-")[0]);
						if (paramLabel.text().split("-").length > 1)
							param.setComment(paramLabel.text().split("-")[1]);
						HashMap<String, String> typeEle = resolutionParam(methodes.get(i));
						param.setType(typeEle.get(paramLabel.text().split("-")[0].trim()));
						methode.getParameter().add(param);
						paramLabel = paramLabel.nextElementSibling();
					}
				}
				if (methodes.get(i).select("dt:has(.throwsLabel)").size() > 0) {
					Element throwsLabel = methodes.get(i).select("dt:has(.throwsLabel)").get(0).nextElementSibling();
					methode.setExceptionStr(new ArrayList<DocType>());
					while (throwsLabel != null && throwsLabel.is("dd")) {
						DocType exp = new DocType();
						exp.setName(throwsLabel.text().split("-")[0]);
						if (throwsLabel.text().split("-").length > 1)
							exp.setComment(throwsLabel.text().split("-")[1]);
						if (throwsLabel.select("a").size() > 0) {
							String regex = "[内のクラス]";
							Pattern pat = Pattern.compile(regex);
							Matcher mat = pat.matcher(throwsLabel.select("a").get(0).attr("title"));
							String repickStr = mat.replaceAll("");
							exp.setType(repickStr + "." + exp.getName());
						}
						methode.getExceptionStr().add(exp);
						throwsLabel = throwsLabel.nextElementSibling();
					}
				}
				if (methodes.get(i).select("dt:has(.returnLabel)").size() > 0) {
					Element returnLabel = methodes.get(i).select("dt:has(.returnLabel)").get(0).nextElementSibling();
					DocType returnDoc = new DocType();
					returnDoc.setComment(returnLabel.text());
					String str = methodes.get(i).select("pre").get(0).text();
					String returnName = str.split(" ")[1];
					Elements types = methodes.get(i).select("pre>a");
					for (Element type : types) {
						if (type.text().equals(returnName)) {
							returnDoc.setType(type.attr("title").split(" ")[2] + "." + returnName);
						}
					}
					if (returnDoc.getType() == null) {
						returnDoc.setType(returnName);
					}
					methode.setReturnStr(returnDoc);
				}
				methodList.add(methode);
			}
			docClass.setMethodList(methodList);
		}

		try {
			String fileName = "";
			String filePath = input.getPath().substring(24);
			fileName = filePath.substring(0, filePath.lastIndexOf("."));
			createFile(target + fileName + ".d");
			// 写对象流的对象
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(target + fileName + ".d"));

			oos.writeObject(docClass); // 将Person对象p写入到oos中

			oos.close(); // 关闭文件流
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createFile(String path) throws IOException {
		if (!path.isEmpty()) {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
	}

	public static HashMap<String, String> resolutionParam(Element ele) {
		HashMap<String, String> result = new HashMap<String, String>();
		String text = ele.select("pre").get(0).text();
		String rex = "[()]+";
		String[] str = text.split(rex);
		if (str.length < 2) {
			return new HashMap<String, String>();
		}
		String params = str[1];
		String[] paramList = params.split(",");
		for (String param : paramList) {
			String typeStr = param.split(" ")[0].replaceAll("\n|\t", "").trim();
			Elements types = ele.select("pre>a");
			for (Element type : types) {
				if (type.text().equals(typeStr)) {
					typeStr = type.attr("title").split(" ")[2] + "." + typeStr;
				}
			}
			if (param.split(" ").length > 1) {
				result.put(param.split(" ")[1], typeStr);
			}
		}
		return result;
	}

}
