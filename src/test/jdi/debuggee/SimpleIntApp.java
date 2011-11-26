package test.jdi.debuggee;

import java.util.Random;

public class SimpleIntApp {

	public static int number = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("initial number value: " + number);

		number = new Random().nextInt();
		
		System.out.println("randomized number value: " + number);
		
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
