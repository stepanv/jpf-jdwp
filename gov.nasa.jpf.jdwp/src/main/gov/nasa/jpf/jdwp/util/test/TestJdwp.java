package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.Arrays;

import org.junit.Before;

public abstract class TestJdwp extends TestJPF {

	/**
	 * This sucks TODO - find a way how to pass the test instance to the
	 * listener without using static fields.
	 */
	static TestJdwp verifierTest;

	public TestJdwp() {
	}

	public TestJdwp(String sutClassName) {
		super(sutClassName);
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
		for (int i = 0; i < args.length; ++i) {
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

	/**
	 * Sets the test instance so that it can be picked up from the JPF Test
	 * Listener in order to execute the verify method.
	 */
	@Before
	public void initializeExecutor() {
		TestJdwp.verifierTest = this;
	}

}
