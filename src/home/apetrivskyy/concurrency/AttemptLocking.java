package home.apetrivskyy.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AttemptLocking {
	private Lock lock = new ReentrantLock();

	public void untimed() {

		boolean captured = lock.tryLock();
		try {
			System.out.println("tryLock(): " + captured);
		} finally {
			if (captured) {
				lock.unlock();
			}
		}
	}

	public void timed() {
		boolean captured = false;

		try {
			captured = lock.tryLock(2, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}

		try {
			System.out.println("tryLock(2, TimeUnit.SECONDS): " + captured);
		} finally {
			if (captured) {
				lock.unlock();
			}
		}
	}

	static class LockGrabberThread extends Thread {
		boolean acquired = false;
		AttemptLocking attemptLocking = null;

		public LockGrabberThread(AttemptLocking attemptLocking) {
			setDaemon(true);
			
			this.attemptLocking = attemptLocking;
		}

		@Override
		public void run() {
			attemptLocking.lock.lock();
			System.out.println("acquired");
			acquired = true;
		}

		public boolean getLockStatus() {
			return acquired;
		}
	}

	public static void main(String[] args) {
		final AttemptLocking attemptLocking = new AttemptLocking();
		attemptLocking.untimed();
		attemptLocking.timed();

		LockGrabberThread t = new LockGrabberThread(attemptLocking);

		t.start();

//		while (true) {
//			if (t.getLockStatus()) {
//				break;
//			}
//			
//			Thread.yield();
//		}

		attemptLocking.untimed();
		attemptLocking.timed();
	}
}
