package com.link.test;

public class ThreadTest {
	public static void main(String[] args) {
		ThreadClass tc = new ThreadClass();
		System.out.println("bb");
		tc = null;
	}

	public static class ThreadClass implements Runnable {
		public ThreadClass() {
			Thread t = new Thread(this);
			t.start();
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("aa");
			}
		}
	}
}
