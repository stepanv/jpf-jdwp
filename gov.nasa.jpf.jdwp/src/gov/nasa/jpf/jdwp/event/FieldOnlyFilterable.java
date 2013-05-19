package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.FieldId;

public interface FieldOnlyFilterable extends Event {

	FieldId getFieldId();

}
