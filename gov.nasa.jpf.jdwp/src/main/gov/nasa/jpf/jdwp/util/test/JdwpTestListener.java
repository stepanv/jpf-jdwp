package gov.nasa.jpf.jdwp.util.test;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
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
			

			if (JdwpVerifier.VERIFY_METHOD_NAME.equals(enteredMethod.getName()) && enteredMethod.getArgumentTypeNames().length == 1 && "java.lang.Object[]".equals(enteredMethod.getArgumentTypeNames()[0])) {

				List<ElementInfo> passedObjects = new LinkedList<ElementInfo>();
				// number [0] is this which we don't care about
				LocalVarInfo passedObjectsAsArray = enteredMethod.getArgumentLocalVars()[1];
				ElementInfo passedObjectsAsArrayElementInfo = (ElementInfo) currentThread.getTopFrame().getLocalValueObject(passedObjectsAsArray);

				for (int i = 0; i < passedObjectsAsArrayElementInfo.arrayLength(); ++i) {
					int objRef = passedObjectsAsArrayElementInfo.getReferenceElement(i);
					passedObjects.add(vm.getHeap().get(objRef));
				}

				// TODO use reflection to get the right verifier instance (now,
				// only one is supported)
				TestJdwp.currentVerificator().verify(passedObjects.toArray());
			}
		}

	}
}
