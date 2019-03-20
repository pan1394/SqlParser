package com.linkstec.bee.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.linkstec.bee.UI.config.Configuration;

public class BeeLogger extends PrintStream {
	private static BeeLogger instance;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public BeeLogger(OutputStream out) {
		super(out, true);
		System.setOut(this);
		System.setErr(this);
	}

	@Override
	public void print(String s) {
		String a = format.format(Calendar.getInstance().getTime()) + "  " + s;

		super.print(a);
	}

	@Override
	public void println() {
		super.println();
	}

	@Override
	public void println(Object x) {
		String s = x == null ? "" : x.toString();
		String a = format.format(Calendar.getInstance().getTime()) + "  " + s;
		super.println(a);
	}

	static BeeLogger temp;

	public static void initialize() {
		String root = System.getProperty("user.dir");
		try {
			String path = null;// root + File.separator + "bee.log";
			File file = null;// new File(path);
			// if (!file.exists()) {
			// file.createNewFile();
			// } else {
			// if (file.length() > 1000 * 100) {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmm");
			path = root + File.separator + "bee" + f.format(Calendar.getInstance().getTime()) + ".log";
			file = new File(path);
			file.createNewFile();
			// }
			// }
			FileOutputStream out = new FileOutputStream(file, true);

			temp = new BeeLogger(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeTemp() {
		if (temp != null) {
			temp.close();
			System.setOut(System.out);
			System.setErr(System.err);
			temp = null;
		}
	}

	public static void initialize(Configuration config) {
		if (temp != null) {
			temp.close();
			System.setOut(System.out);
			System.setErr(System.err);
			temp = null;
		}
		if (instance == null) {
			String root = config.getWorkSpace();
			try {
				String path = null;// root + File.separator + "bee.log";
				File file = null;// new File(path);
				// if (!file.exists()) {
				// file.createNewFile();
				// } else {
				// if (file.length() > 1000 * 100) {
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmm");
				path = root + File.separator + "bee" + f.format(Calendar.getInstance().getTime()) + ".log";
				file = new File(path);
				file.createNewFile();
				// }
				// }
				FileOutputStream out = new FileOutputStream(file, true);

				instance = new BeeLogger(out);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
