package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.vm.FieldInfo;

public interface FieldOnlyFilterable extends Event {

	FieldInfo getFieldInfo();

}
