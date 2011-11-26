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

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("initial number value: " + number);

		number = new Random().nextInt();

		String currentTime  = SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG).format(new Date());
		System.out.println(currentTime);
		System.out.println("randomized number value: " + number);

		File logFile = new File("c:\\tmp\\output.txt");

		try {
			Writer writer = new FileWriter(logFile, true);
			writer.append("\n" + currentTime);
			writer.append("\nrandomized number value: " + number);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
