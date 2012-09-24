package home.apetrivskyy.concurrency;

public class SerialNumberGenerator {
	private static volatile int serialNumber = 0;

	public static /*synchronized*/ int nextSerialNumber() {
		return serialNumber++;
	}

	public static void main(String[] args) {
		System.out.println(SerialNumberGenerator.nextSerialNumber());
	}
}
