package test.jdi.impl.internal;

import gov.nasa.jpf.jvm.ClassInfo;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import test.jdi.impl.VirtualMachineImpl;

public class ClassesManager {

	public static final Logger log = org.apache.log4j.Logger
			.getLogger(ClassesManager.class);

	public ClassesManager(VirtualMachineImpl virtualMachineImpl) {
	}

	Set<ClassInfo> loadedClasses = new HashSet<ClassInfo>();

	void notifyClassLoadded(ClassInfo classInfo) {
		loadedClasses.add(classInfo);
		log.debug("Class loaded: " + classInfo);
	}

	public Set<ClassInfo> getLoadedClasses() {
		return loadedClasses;
	}

}
