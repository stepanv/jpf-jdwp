package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;

public interface ConvertibleEnum<T, E extends Enum<E> & ConvertibleEnum<T, E>> extends IdentifiableEnum<T> {
	E convert(T val) throws JdwpError;
}
