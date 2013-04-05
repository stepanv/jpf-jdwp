package gov.nasa.jpf.jdwp.id;

import java.nio.ByteBuffer;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.vm.FieldInfo;

public class FieldId extends Identifier<FieldInfo>{

	public FieldId(FieldInfo object) {
		super(0, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

	public static FieldId factory(ByteBuffer bytes, CommandContextProvider contextProvider) {
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}

}
