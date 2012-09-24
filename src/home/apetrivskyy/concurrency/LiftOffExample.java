package home.apetrivskyy.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class LiftOff implements Runnable {

	protected int countDown = 10;
	private static int taskCount = 0;
	private final int id = taskCount++;

	public LiftOff() {
	};

	public LiftOff(int countDown) {
		this.countDown = countDown;
	}

	public String getStatus() {
		return "#" + id + " (" + (countDown > 0 ? countDown : "LiftOff!")
				+ "), ";
	}

	@Override
	public void run() {
		while (countDown-- > 0) {
			System.out.println(getStatus());
			Thread.yield();
		}
	}
}

class SleepingLiftOff extends LiftOff {
	
	public SleepingLiftOff(int n) {
		super(n);
	}
	
	@Override
	public void run() {
		try {
			while (countDown-- > 0) {
				System.out.println(getStatus());
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class LiftOffExample {
	public static void runInCachedThreadPool() {
		ExecutorService exec = Executors.newCachedThreadPool();

		for (int i = 0; i < 5; i++) {
			exec.execute(new LiftOff(10));
		}

		exec.shutdown();
	}

	public static void runInSingleThread() {
		ExecutorService exec = Executors.newSingleThreadExecutor();

		for (int i = 0; i < 5; i++) {
			exec.execute(new LiftOff(10));
		}

		exec.shutdown();
	}
	
	public static void runSleeping() {
		ExecutorService exec = Executors.newCachedThreadPool();

		for (int i = 0; i < 5; i++) {
			exec.execute(new SleepingLiftOff(10));
		}

		exec.shutdown();
	}

	public static void main(String[] args) {
		System.out.println("Waiting for liftoff");
		
		// LiftOffExample.runInSingleThread();
		LiftOffExample.runInCachedThreadPool();
		// LiftOffExample.runSleeping();
	}
}