package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.BreakpointEvent;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.FieldAccessEvent;
import gov.nasa.jpf.jdwp.event.FieldModificationEvent;
import gov.nasa.jpf.jdwp.event.Locatable;
import gov.nasa.jpf.jdwp.event.LocationOnlyFilterable;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * Location Only filter class restricts events to those that match the given
 * location.<br/>
 * Even though it seems that any {@link Locatable} event might be used (i.e.
 * those that always carry {@link Location} instance, the JDWP Specification
 * clearly states only a subset of {@link Locatable} events might be used. <br/>
 * For further details see JDWP Specification bellow.
 * </p>
 * <p>
 * This filter accepts only and only {@link LocationOnlyFilterable} event
 * instances.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those that occur at the given location. This
 * modifier can be used with breakpoint, field access, field modification, step,
 * and exception event kinds.
 * </p>
 * 
 * @see BreakpointEvent
 * @see FieldAccessEvent
 * @see FieldModificationEvent
 * @see SingleStepEvent
 * @see ExceptionEvent
 * @see LocationOnlyFilterable
 * 
 * @author stepan
 * 
 */
public class LocationOnlyFilter extends Filter<LocationOnlyFilterable> {

	private Location location;

	/**
	 * Creates Location Only Filter.
	 * 
	 * @param location
	 *            Required location
	 */
	public LocationOnlyFilter(Location location) {
		super(Filter.ModKind.LOCATION_ONLY);

		this.location = location;
	}

	@Override
	public boolean matches(LocationOnlyFilterable event) {
		return location.equals(event.getLocation());
	}
}
