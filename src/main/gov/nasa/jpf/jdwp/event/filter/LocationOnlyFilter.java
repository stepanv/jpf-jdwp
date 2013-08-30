/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.BreakpointEvent;
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
   *          Required location
   */
  public LocationOnlyFilter(Location location) {
    super(Filter.ModKind.LOCATION_ONLY, LocationOnlyFilterable.class);

    this.location = location;
  }

  @Override
  public boolean matches(LocationOnlyFilterable event) {
    return location.equals(event.getLocation());
  }
}
