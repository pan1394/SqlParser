/*
 * package com.linkstec.excel;
 * 
 * import java.io.FileOutputStream; import java.io.OutputStreamWriter; import
 * java.util.List;
 * 
 * import org.dom4j.Document; import org.dom4j.DocumentException; import
 * org.dom4j.DocumentHelper; import org.dom4j.Element; import
 * org.dom4j.io.XMLWriter;
 * 
 * import com.linkstec.bee.core.fw.BClass; import
 * com.linkstec.bee.core.fw.logic.BAssignment;
 * 
 * public class exportMybatis {
 * 
 * public exportMybatis(MyBatisObject model) throws DocumentException { //
 * 创建一个xml文档 Document doc = DocumentHelper.createDocument();
 * doc.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN",
 * "http://mybatis.org/dtd/mybatis-3-mapper.dtd"); Element root =
 * doc.addElement("mapper"); root.addAttribute("namespace",
 * model.getNamespace());
 * 
 * BClass cls1 = model.getResultMap(); Element resultMap =
 * root.addElement("resultMap"); resultMap.addAttribute("id",
 * cls1.getLogicName()); resultMap.addAttribute("type", cls1.getPackage() +
 * cls1.getLogicName()); List<BAssignment> vars = cls1.getVariables(); for
 * (BAssignment assin : vars) { Element result = resultMap.addElement("result");
 * result.addAttribute("column", assin.getLeft().getLogicName());
 * result.addAttribute("property", assin.getLeft().getLogicName());
 * result.addAttribute("jdbcType",
 * changeType(assin.getLeft().getBClass().getLogicName())); } Element sql =
 * root.addElement(model.getType()); sql.addAttribute("id", model.getSqlId());
 * sql.addAttribute("parameterType", model.getParameterType().getLogicName());
 * sql.addAttribute("resultMap", cls1.getLogicName());
 * sql.setText(model.getSql()); doc2XmlFile(doc, "d:\\test.xml"); }
 * 
 * public String changeType(String type) { String dbType = "varchar"; if
 * (type.equals("String")) { return "varchar"; } if (type.equals("BigDecimal"))
 * { return "DECIMAL"; } if (type.equals("Integer")) { return "INTEGER"; } if
 * (type.equals("Date")) { return "DATE"; } if (type.equals("Timestamp")) {
 * return "TIMESTAMP"; } return dbType; }
 * 
 * public static boolean doc2XmlFile(Document document, String filename) {
 * boolean flag = true; try { XMLWriter writer = new XMLWriter(new
 * OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
 * writer.write(document); writer.close(); } catch (Exception ex) { flag =
 * false; ex.printStackTrace(); } System.out.println(flag); return flag; } }
 */
