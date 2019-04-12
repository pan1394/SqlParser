package com.link.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Rescure {

	public static void main(String[] args) {
		String path = "D:\\Bee\\application\\beny\\basic\\PRC\\Count表編集.bl";
		File file = new File(path);
		try {
			FileOutputStream os = new FileOutputStream(file, true);
			os.write("\r\n".getBytes());
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
