package com.link.test;

public class PrimaryClassTest {

	public PrimaryClassTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		int[] ints = new int[1];
		System.out.println(ints.getClass().getName());
		// [I

		long[] longs = new long[1];
		System.out.println(longs.getClass().getName());
		// [J

		short[] shorts = new short[1];
		System.out.println(shorts.getClass().getName());
		// [S

		double[] doubles = new double[1];
		System.out.println(doubles.getClass().getName());
		// [D

		float[] floats = new float[1];
		System.out.println(floats.getClass().getName());
		// [F

		byte[] bytes = new byte[1];
		System.out.println(bytes.getClass().getName());
		// [B

		boolean[] booleans = new boolean[1];
		System.out.println(booleans.getClass().getName());
		// [Z

		char[] chars = new char[1];
		System.out.println(chars.getClass().getName());
		// [C

		try {
			Class cls = Class.forName("[I");
			System.out.println(cls.getName());
			System.out.println(cls.isArray());
			System.out.println(cls.getComponentType().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
