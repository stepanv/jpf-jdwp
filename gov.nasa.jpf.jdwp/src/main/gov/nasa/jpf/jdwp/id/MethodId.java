package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.vm.MethodInfo;

public class MethodId extends Identifier<MethodInfo> {

	public MethodId(long id, MethodInfo object) {
		super(0, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

}
