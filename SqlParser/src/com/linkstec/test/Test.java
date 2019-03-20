package com.linkstec.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		
		String result = "���ꂎ��wBBXAPZ1081GetTblNameLogicOutDto\r\n" + 
				"\r\n" + 
				"�Ʃ`�֥�����1111";
		String serviceId=null;
		Pattern ptn = Pattern.compile("�Ʃ`�֥�����([A-Z])(\\d{4})");
		
		Matcher m = ptn.matcher(result);
		if (m.find()) {
			String tbl1 = "", tbl2 = "", tblName = "";
			tbl1 = m.group(1);
			tbl2 = m.group(2);
			tblName = m.group(); 
 			String template = "xxx";
			if (template != null) {
				tblName = tbl1 + "_" + template + tbl2;
			}
			result = result.replaceAll("�Ʃ`�֥�����([A-Z])(\\d{4})","�Ʃ`�֥�����" + tblName.toLowerCase());
		}
		System.out.println(result);
	}
}
