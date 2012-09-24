package home.apetrivskyy.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicityTest implements Runnable {

	// private int i = 0;
	private AtomicInteger i = new AtomicInteger(0);

	public /*synchronized*/ int getValue() {
		// return i;
		return i.get();
	}

	private /*synchronized*/ void evenIncrement() {
		//i++; i++;
		i.addAndGet(2);
	}

	@Override
	public void run() {
		while (true) {
			evenIncrement();
		}
	}

	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		AtomicityTest at = new AtomicityTest();
		
		exec.execute(at);

		while (true) {
			int val = at.getValue();
			
			if ((val % 2 != 0)) {
				System.out.println(val);
				System.exit(0);
			}
		}
	}
}
