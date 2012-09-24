package home.apetrivskyy.concurrency;

import java.io.IOException;

class UnresponsiveUI {
	private volatile double d = 1;

	public UnresponsiveUI() throws IOException {
		while (d > 0) {
			d = d + (Math.PI + Math.E) / d;
		}

		System.in.read();
	}
}

class ResponsiveUI extends Thread {
	private static volatile double d = 1;

	public ResponsiveUI() {
		setDaemon(true);
		start();
	}
	
	public double getD() {
		return d;
	}
	
	@Override
	public void run() {
		while (true) {
			d = d + (Math.PI + Math.E) / d;
		}
	}
}

public class UI {
	public static void main(String[] args) throws IOException {
		// new UnresponsiveUI();
		ResponsiveUI ui = new ResponsiveUI();
		
		System.in.read();
		System.out.println(ui.getD());
	}
}
