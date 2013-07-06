package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.Arrays;

public abstract class TestJdwp extends TestJPF {

	/**
	 * This sucks TODO - use reflection to get the right verifier instance.
	 */
	private static JdwpVerifier verifier;

	public TestJdwp() {
		// TODO Auto-generated constructor stub
	}

	public TestJdwp(String sutClassName) {
		super(sutClassName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * run JPF expecting no SuT property violations or JPF exceptions
	 * 
	 * @param args
	 *            JPF main() arguments
	 */
	@Override
	protected JPF noPropertyViolation(StackTraceElement testMethod, String... args) {
		// add the JDWPTestListener
		String listener = JdwpTestListener.class.getName(); 
		
		// if the +listener was already specified
		for (int i = 0; i < args.length; ++ i) {
			if (args[0].startsWith("+listener=")) {
				args[0] += "," + listener;
				return super.noPropertyViolation(testMethod, args);
			}
		}
		
		// if the +listener is not there
		String[] newargs = Arrays.copyOf(args, args.length + 1);
		newargs[args.length] = "+listener=" + listener;
		return super.noPropertyViolation(testMethod, newargs);
	}

	public static JdwpVerifier currentVerificator() {
		return verifier;
	}

	public void initialize(JdwpVerifier verificator) {
		TestJdwp.verifier = verificator;
	}

}
