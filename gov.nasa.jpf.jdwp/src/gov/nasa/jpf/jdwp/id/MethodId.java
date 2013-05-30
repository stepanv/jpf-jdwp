package gov.nasa.jpf.jdwp.id;

import java.nio.ByteBuffer;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

public class MethodId extends Identifier<MethodInfo> {

	public MethodId(long id, MethodInfo object) {
		super(0, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

}
