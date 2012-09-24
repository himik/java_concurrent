package home.apetrivskyy.concurrency;

import java.util.concurrent.locks.Lock;

public class Util {
	public static void printThread() {
		System.out.println("Thread " + Thread.currentThread().getName()
				+ " does something");
	}

	public static String getThreadName() {
		return Thread.currentThread().getName();
	}
	
	public static void acquiredLock(Lock lock) {
		System.out.println(getThreadName() + " has acquired lock " + lock);
	}
	
	public static void releasedLock(Lock lock) {
		System.out.println(getThreadName() + " has released lock " + lock);
	}
	
}
