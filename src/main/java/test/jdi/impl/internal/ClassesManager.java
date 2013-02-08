package test.jdi.impl.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;

import gov.nasa.jpf.jvm.ClassInfo;
import test.jdi.impl.ClassTypeImpl;
import test.jdi.impl.ReferenceTypeImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.ClassPrepareEventImpl;
import test.jdi.impl.event.ThreadStartEventImpl;
import test.jdi.impl.request.ClassPrepareRequestImpl;

public class ClassesManager {

	public static final Logger log = org.apache.log4j.Logger
			.getLogger(ClassesManager.class);

	private VirtualMachineImpl virtualMachine;

	public ClassesManager(VirtualMachineImpl virtualMachineImpl) {
		this.virtualMachine = virtualMachineImpl;
	}

	Set<ClassInfo> loadedClasses = new HashSet<ClassInfo>();

	private void generateClassPrepareEvent(ClassPrepareRequestImpl request,
			ClassInfo classInfo) {
		log.debug("Generating class prepare event for :" + request);
		ClassPrepareEvent te = new ClassPrepareEventImpl(virtualMachine,
				virtualMachine.getJvm().getLastThreadInfo(), request,
				ClassTypeImpl.factory(classInfo, virtualMachine));
		virtualMachine.addEvent(te);
	}

	boolean simpleMatch(String text, String pattern) {
		String[] subPatterns = pattern.split("\\*");

		// Iterate over the cards.
		for (String subPattern : subPatterns) {
			int index = text.indexOf(subPattern);

			if (index == -1) {
				return false;
			}

			text = text.substring(index + subPattern.length());
		}

		return true;
	}

	void notifyClassLoadded(ClassInfo classInfo) {
		loadedClasses.add(classInfo);
		
		List<ClassPrepareRequest> classPrepareRequests = virtualMachine
				.getEventRequestManager().classPrepareRequests();
		
		if (classPrepareRequests.size() > 0) {
			synchronized (classPrepareRequests) {
				for (ClassPrepareRequest request : classPrepareRequests) {
					ClassPrepareRequestImpl requestImpl = (ClassPrepareRequestImpl) request;
					for (String classFilter : requestImpl
							.getClassFilterString()) {
						if (simpleMatch(classInfo.getName(), classFilter)) {
							generateClassPrepareEvent(
									(ClassPrepareRequestImpl) request,
									classInfo);
						}
					}
				}
			}

		}

		log.debug("Class loaded: " + classInfo);
	}

	public Set<ClassInfo> getLoadedClasses() {
		return loadedClasses;
	}

}
