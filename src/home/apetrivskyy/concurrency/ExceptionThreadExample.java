package home.apetrivskyy.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class ExceptionThread implements Runnable {

	@Override
	public void run() {
		Thread t = Thread.currentThread();
		System.out.println("run by: " + t);
		System.out.println("eh = " + t.getUncaughtExceptionHandler());

		throw new RuntimeException();
	}
}

class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("caught in: " + t + ". Exception: " + e);
	}

}

class CustomThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		System.out.println(this + " creating new thread");

		Thread t = new Thread(r);

		System.out.println("created " + t);

		t.setUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		System.out.println("eh = " + t.getUncaughtExceptionHandler());

		return t;
	}

}

public class ExceptionThreadExample {

	public static void main(String[] args) {
		ExecutorService service = Executors
				.newCachedThreadPool(new CustomThreadFactory());

		try {
			service.execute(new ExceptionThread());
		} catch (Throwable th) {
			System.out.println("Exception occurred in the thread");
		}
	}
}
