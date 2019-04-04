package com.linkstec.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SqlParser {

	public static void process(File source) throws IOException {
		if (source == null)
			return;

		List<List<String>> tmp = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"))) {
			String line = null;
			String preline = null;
			List<String> obj = new ArrayList<>();
			tmp.add(obj);
			while ((line = reader.readLine()) != null) {
				obj.add(line);
				if (preline != null && "【ソート条件】".equals(preline.trim())) {
					obj = new ArrayList<>();
					tmp.add(obj);
				}
				preline = line;
			}
		} 
		
		int count = 0;
		List<SqlObject> collection = new ArrayList<>();
		for(List<String> obj : tmp) {
			++count;
			SqlObject convertedObj = convert(obj);
			if(convertedObj.getTable() == null) continue;
			collection.add(convertedObj);
			System.out.println("==================="+count+"=======================");
			System.out.println(convertedObj);
			System.out.println("===================end=====================");

			System.out.println();
		}


		 
	}

	private static SqlObject convert(List<String> obj) {
		return SqlObject.parse(obj);
	}

	public static void main(String... args) throws IOException {
		File file = new File("d:/txt10.txt");
		process(file);
	}
	

}
