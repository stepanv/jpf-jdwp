package gov.nasa.jpf.jdwp.proxy;

import gnu.classpath.jdwp.VMMethod;
import gnu.classpath.jdwp.util.Location;
import gov.nasa.jpf.jvm.MethodInfo;

public class LocationProxy extends Location {

	private MethodInfo methodInfo;

	public LocationProxy(MethodInfo methodInfo, long index) {
		super(null, index);
		this.methodInfo = methodInfo;
	}

}
