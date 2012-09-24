package home.apetrivskyy.concurrency;

public class Fibonacci {

	public static class Calculator implements Runnable {

		public final int n;

		public Calculator(int n) {
			this.n = n;
		}

		@Override
		public void run() {
			int lastNumber = 0;
			int currentNumber = 1;

			System.out.println(Thread.currentThread().getId() + ": " + 0
					+ " -> " + lastNumber);
			
			System.out.println(Thread.currentThread().getId() + ": " + 1
					+ " -> " + currentNumber);

			for (int i = 2; i < n + 1; i++) {
				System.out.println(Thread.currentThread().getId() + ": " + i
						+ " -> " + (currentNumber + lastNumber));

				currentNumber += lastNumber;
				lastNumber = currentNumber - lastNumber;
				
				Thread.yield();
			}
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			(new Thread(new Calculator(10))).start();
		}
	}
}