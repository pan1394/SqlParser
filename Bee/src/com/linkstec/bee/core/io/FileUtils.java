package com.linkstec.bee.core.io;

import java.io.File;

public class FileUtils {
	public static void deleteAllFile(File f) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				deleteFile(f);

			}

		}).start();
	}

	public static void deleteFile(File f) {
		if (!f.exists()) {
			return;
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				deleteFile(file);
			}
		}
		f.delete();
	}
}
