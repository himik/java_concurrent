package home.apetrivskyy.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Pair { // Non thread-safe
	private int x, y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Pair() {
		this(0, 0);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void incrementX() {
		x++;
	}

	public void incrementY() {
		y++;
	}

	@Override
	public String toString() {
		return "x: " + x + ", y: " + y;
	}

	public class PairValuesNotEqualException extends RuntimeException {

		private static final long serialVersionUID = -5629441302001322060L;

		public PairValuesNotEqualException() {
			super("Pair values not equal: " + Pair.this);
			System.out.println("Pair values not equal: " + Pair.this);
			System.out.println(Util.getThreadName() + " failed");
			System.exit(0);
		}
	}

	public void checkState() {
		if (x != y) {
			throw new PairValuesNotEqualException();
		}
	}
}

// Protect pair inside thread-safe class
abstract class PairManager {
	AtomicInteger checkCounter = new AtomicInteger(0);
	protected Pair p = new Pair();
	private List<Pair> storage = Collections
			.synchronizedList(new ArrayList<Pair>());

	public synchronized Pair getPair() {
		// Make a copy to keep the original safe
		return new Pair(p.getX(), p.getY());
	}

	// Assume this is a time consuming operation
	protected void store(Pair p) {
		storage.add(p);

		try {
			TimeUnit.MILLISECONDS.sleep(50);
		} catch (InterruptedException ignore) {
			// do nothing
		}
	}

	public abstract void increment();
}

// Synchronize the entire method:
class PairManager1 extends PairManager {

	@Override
	public synchronized void increment() {
		Util.printThread();
		p.incrementX();
		Util.printThread();
		p.incrementY();
		Util.printThread();
		store(getPair());
	}
}

// Use critical section:
class PairManager2 extends PairManager {

	@Override
	public void increment() {
		Pair temp;
		synchronized (this) {
			Util.printThread();
			p.incrementX();
			Util.printThread();
			p.incrementY();
			Util.printThread();
			temp = getPair();
		}
		store(temp);
	}
}

class ExplicitPairManager1 extends PairManager {

	private Lock lock = new ReentrantLock();

	@Override
	public synchronized void increment() {
		lock.lock();
		Util.acquiredLock(lock);
		try {
			Util.printThread();
			p.incrementX();
			Util.printThread();
			p.incrementY();
			Util.printThread();
			store(getPair());
		} finally {
			lock.unlock();
			Util.releasedLock(lock);
		}
	}
}

class ExplicitPairManager2 extends PairManager {
	private Lock lock = new ReentrantLock();

	@Override
	public void increment() {
		Pair temp;
		lock.lock();
		Util.acquiredLock(lock);
		try {
			Util.printThread();
			p.incrementX();
			Util.printThread();
			p.incrementY();
			Util.printThread();
			temp = getPair();
		} finally {
			lock.unlock();
			Util.releasedLock(lock);
		}

		store(temp);
	}
}

class PairManipulator implements Runnable {

	private PairManager pm;

	public PairManipulator(PairManager pm) {
		this.pm = pm;
	}

	@Override
	public void run() {
		while (true) {
			pm.increment();
		}
	}

	@Override
	public String toString() {
		return "Pair: " + pm.getPair() + " checkCounter = "
				+ pm.checkCounter.get();
	}
}

class PairChecker implements Runnable {

	private PairManager pm;

	public PairChecker(PairManager pm) {
		this.pm = pm;
	}

	@Override
	public void run() {
		while (true) {
			// Util.printThread();
			pm.checkCounter.incrementAndGet();
			// Util.printThread();
			pm.getPair().checkState();
		}
	}
}

public class CriticalSection {
	// Test two different approaches
	static void testApproaches(PairManager pman1, PairManager pman2) {
		ExecutorService exec = Executors.newCachedThreadPool();

		PairManipulator pm1 = new PairManipulator(pman1);
		PairManipulator pm2 = new PairManipulator(pman2);

		PairChecker pcheck1 = new PairChecker(pman1);
		PairChecker pcheck2 = new PairChecker(pman2);

		exec.execute(pm1);
		exec.execute(pm2);
		exec.execute(pcheck1);
		exec.execute(pcheck2);

		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Sleep interrupted");
		}

		System.out.println("pm1: " + pm1 + "\npm2: " + pm2);
		System.exit(0);
	}

	public static void main(String[] args) {
		// PairManager pm1 = new PairManager1();
		// PairManager pm2 = new PairManager2();
		PairManager pm1 = new ExplicitPairManager1();
		PairManager pm2 = new ExplicitPairManager2();

		testApproaches(pm1, pm2);
	}
}
