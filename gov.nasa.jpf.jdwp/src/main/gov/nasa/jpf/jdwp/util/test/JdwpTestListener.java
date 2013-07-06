package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

public class JdwpTestListener extends ListenerAdapter implements VMListener {

	public JdwpTestListener() {
	}

	@Override
	public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		ClassInfo methodClassInfo = enteredMethod.getClassInfo();

		System.out.println("Method entered: " + methodClassInfo + " ... " + enteredMethod);

		if (methodClassInfo != null && methodClassInfo.getName().equals(JdwpVerifier.class.getName())) {
			// we entered verifier class which is a notification to execute the
			// same method outside of SuT

			if (JdwpVerifier.VERIFY_METHOD_NAME.equals(enteredMethod.getName())) {

				// TODO use reflection to get the right verifier instance (now,
				// only one is supported)
				TestJdwp.currentVerificator().verify();
			}
		}

	}
}
