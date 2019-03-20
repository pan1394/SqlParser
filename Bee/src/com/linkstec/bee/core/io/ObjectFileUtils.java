package com.linkstec.bee.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectFileUtils {
	public static void writeObject(File f, Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		BufferedOutputStream bof = new BufferedOutputStream(fos, 1024 * 100);
		ObjectOutputStream oos = new ObjectOutputStream(bof);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bof.close();
		fos.close();
		bof = null;
		oos = null;
	}

	public static Object readObject(File f) throws IOException, ClassNotFoundException {
		if (!f.exists()) {
			return null;
		}

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f), 1204 * 8);
		ObjectInputStream ois = new ObjectInputStream(bis);

		Object obj = ois.readObject();
		ois.close();
		bis.close();

		return obj;
	}

	public static Object deepCopy(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(obj);
			oos.flush();

			byte[] buffer = out.toByteArray();

			oos.close();

			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = new ObjectInputStream(in);

			Object o = ois.readObject();
			ois.close();

			return o;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
