package com.linkstec.bee.core.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Hashtable;

public class CacheStream {
	private RandomAccessFile file;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public CacheStream(Object obj) {

		String root = System.getProperty("user.home");
		String name = obj.getClass().getSimpleName();
		String path = root + File.separator + "bee";
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// file = new RandomAccessFile(path + File.separator + name, "rw");

		FileChannel channel = file.getChannel();
		try {

			// output = new ObjectOutputStream(new BufferedOutputStream(new
			// FileOutputStream(file)));
			this.write(obj);
			// input = new ObjectInputStream(new BufferedInputStream(new
			// FileInputStream(file)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(Object obj) {
		try {
			output.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object read() {
		try {
			return input.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closeAll() {
		try {
			this.input.close();
			this.output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Hashtable hash = new Hashtable();
		CacheStream cache = new CacheStream(hash);
		hash.put("aaa", "bbb");
		cache.write(hash);

		Object obj = cache.read();
		System.out.println(obj);
	}
}
