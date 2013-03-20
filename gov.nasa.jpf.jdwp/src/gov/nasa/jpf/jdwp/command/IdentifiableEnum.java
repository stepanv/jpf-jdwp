package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;

public interface IdentifiableEnum<T> {
	T identifier();
}
