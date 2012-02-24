package test.jdi.debuggee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SimpleIntApp {

	public static int number = 0;

	
	private static int generateRandom() {
		return new Random().nextInt();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("initial number value: " + number);

		number = generateRandom();

		String currentTime  = SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG).format(new Date());
		System.out.println(currentTime);
		System.out.println("randomized number value: " + number);

//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}

}
