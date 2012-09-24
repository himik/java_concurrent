package home.apetrivskyy.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class SleepBlocked implements Runnable {

	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("InterruptedException");
		}
		System.out.println("Exiting SleepBlocked.run()");
	}
}

class IOBlocked implements Runnable {

	private InputStream inputStream;

	public IOBlocked(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		System.out.println("Waiting for read():");
		try {
			inputStream.read();
		} catch (IOException e) {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("Interrupted from blocked I/O");
			} else {
				throw new RuntimeException(e);
			}
		}

		System.out.println("Exiting IOBlocked.run()");
	}
}

class SynchronizedBlocked implements Runnable {

	public synchronized void f() {
		while (true) { // Never releases the lock
			Thread.yield();
		}
	}

	public SynchronizedBlocked() {
		new Thread() {
			@Override
			public void run() {
				f(); // Lock acquired by this thread
			}
		};
	}

	@Override
	public void run() {
		System.out.println("Trying to call f()");
		f();
		System.out.println("Exiting SynchronizedBlock.run()");
	}
}

public class Interrupting {
	private static ExecutorService exec = Executors.newCachedThreadPool();

	static void test(Runnable r) throws Exception {
		Future<?> f = exec.submit(r);
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("Interrupting " + r.getClass().getSimpleName());
		f.cancel(true); // Interrupts if running
		System.out.println("Interrupt sent to " + r.getClass().getSimpleName());
	}

	public static void main(String[] args) throws Exception {
		test(new SleepBlocked());
		test(new IOBlocked(System.in));
		test(new SynchronizedBlocked());

		TimeUnit.SECONDS.sleep(3);
		System.out.println("Aborting with System.exit(0)");
		System.exit(0); // Since last 2 interrupts failed
	}
}
