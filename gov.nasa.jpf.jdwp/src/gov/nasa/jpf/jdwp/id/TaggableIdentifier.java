package gov.nasa.jpf.jdwp.id;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;

public abstract class TaggableIdentifier<T> extends Identifier<T> {

	public TaggableIdentifier(long id, T object) {
		super(id, object);
	}

	public abstract IdentifiableEnum<Byte> getIdentifier();
	
	public void writeTagged(DataOutputStream os) throws IOException {
		os.write(getIdentifier().identifier());
		write(os);
	}
	
}
