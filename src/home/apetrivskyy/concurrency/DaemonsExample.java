package home.apetrivskyy.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class DaemonThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);

		return thread;
	}
}

class Daemon implements Runnable {
	@Override
	public void run() {
		try {
			while (true) {
				TimeUnit.SECONDS.sleep(1);
				System.out.println(Thread.currentThread() + " " + this);
			}
		} catch (Throwable th) {
			// ignore
		}
	}
}

public class DaemonsExample {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService service = Executors
				.newCachedThreadPool(new DaemonThreadFactory());

		for (int i = 0; i < 10; i++) {
			service.execute(new Daemon());
		}

		System.out.println("All daemons started");
		TimeUnit.SECONDS.sleep(10);
	}
}
