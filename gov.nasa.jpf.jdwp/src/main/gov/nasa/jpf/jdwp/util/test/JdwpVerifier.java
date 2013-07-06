package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.util.test.TestJPF;

/**
 * This class implements two functionalities.
 * 
 * First of all it runs the JDWP command implemented in a subclass (
 * {@link JdwpVerifier#verifyOutsideOfSuT()}).<br/>
 * Second of all it implements the way how the SuT program can notify by
 * executing the {@link JdwpVerifier#verify()} method the
 * {@link JdwpTestListener} listener to run the JDWP test.
 * 
 * @see JdwpTestListener#methodEntered(gov.nasa.jpf.vm.VM,
 *      gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.MethodInfo)
 * 
 * @author stepan
 * 
 */
public abstract class JdwpVerifier {

	public static final String VERIFY_METHOD_NAME;

	static {
		try {
			// this is how we defend against method "verify" rename
			VERIFY_METHOD_NAME = JdwpVerifier.class.getMethod("verify").getName();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method implements the JDWP verification which is run outside of SuT.
	 * (That means by the main thread of JPF.)
	 * 
	 * @throws Throwable
	 */
	abstract public void verifyOutsideOfSuT() throws Throwable;

	/**
	 * Call this method from SuT to trigger synchronous call of this method
	 * outside of SuT. It sounds tricky but this is how it is.
	 */
	public void verify() {
		if (!TestJPF.isJPFRun()) {
			// Now, we're outside of SuT - executed from the listener

			try {
				verifyOutsideOfSuT();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			// this is just notification in SuT - this is how we get into
			// methodExecuted notification so that we can execute this again
			// outside of SuT
		}
	}

}
