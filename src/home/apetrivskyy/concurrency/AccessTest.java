package home.apetrivskyy.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class IntGenerator {
	private volatile boolean canceled = false;

	public abstract int next();

	public void cancel() {
		canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}
}

class EvenChecker implements Runnable {

	private IntGenerator generator;
	private final int id;

	public EvenChecker(IntGenerator generator, int id) {
		this.generator = generator;
		this.id = id;
	}

	@Override
	public void run() {
		while (!generator.isCanceled()) {
			int val = generator.next();

			if (val % 2 != 0) {
				System.out.println(val + " not even");
				generator.cancel();
			}

			Thread.yield();
		}
	}

	public static void test(IntGenerator gp, int count) {
		System.out.println("Press Ctrl + C to exit");
		ExecutorService service = Executors.newCachedThreadPool();

		for (int i = 0; i < count; i++) {
			service.execute(new EvenChecker(gp, i));
		}
		service.shutdown();
	}

	public static void test(IntGenerator gp) {
		test(gp, 10);
	}
}

class EvenGenerator extends IntGenerator {

	private int currentValue = 0;
	
	@Override
	public int next() {
		System.out.println(Thread.currentThread() + " trying to get lock");
		
		return nextInternal();
	}
	
	public synchronized int nextInternal() {
		System.out.println(Thread.currentThread() + " has got the lock");
		
		++currentValue;
		Thread.yield();
		++currentValue;

		return currentValue;
	}
}

class MutexEvenGenerator extends IntGenerator {

	private int currentEvenValue = 0;
	private Lock lock = new ReentrantLock();
	
	@Override
	public int next() {
		lock.lock();
		
		try {
			++currentEvenValue;
			Thread.yield();
			++currentEvenValue;
			
			return currentEvenValue;
		} finally {
			lock.unlock();
		}
	}
	
}

public class AccessTest {
	public static void main(String[] args) {
		//EvenChecker.test(new EvenGenerator());
		EvenChecker.test(new MutexEvenGenerator());
	}
}
