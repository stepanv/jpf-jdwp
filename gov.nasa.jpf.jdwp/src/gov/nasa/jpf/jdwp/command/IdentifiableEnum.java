package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;

public interface IdentifiableEnum<T, E extends Enum<E> & IdentifiableEnum<T, E>> {
	T identifier();

	E convert(T val) throws JdwpError;
}
