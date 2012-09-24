package home.apetrivskyy.concurrency;

class Sleeper extends Thread {
	private final int duration;

	public Sleeper(String name, int sleepTime) {
		super(name);
		duration = sleepTime;

		start();
	}

	@Override
	public void run() {
		try {
			sleep(duration);
		} catch (InterruptedException ie) {
			System.out.println(getName() + " was interrupted. "
					+ "isInterrupted(): " + isInterrupted());

			return;
		}
		System.out.println(getName() + " has awakened");
	}
}

class Joiner extends Thread {
	private Sleeper sleeper;

	public Joiner(String name, Sleeper sleeper) {
		super(name);
		this.sleeper = sleeper;

		start();
	}

	@Override
	public void run() {
		try {
			sleeper.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted");
		}
		System.out.println(getName() + " join completed");
	}
}

public class Joining {
	public static void main(String[] args) {
		Sleeper sleepy = new Sleeper("sleepy", 5000);
		Sleeper grumpy = new Sleeper("grumpy", 5000);

		Joiner dopey = new Joiner("dopey", sleepy);
		Joiner doc = new Joiner("doc", grumpy);

		grumpy.interrupt();
	}
}
