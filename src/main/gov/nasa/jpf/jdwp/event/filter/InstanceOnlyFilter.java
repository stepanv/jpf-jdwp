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

import gov.nasa.jpf.jdwp.event.InstanceOnlyFilterable;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;

import java.util.Objects;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those whose active 'this' object is the given
 * object. Match value is the null object for static methods. This modifier can
 * be used with any event kind except class prepare, class unload, thread start,
 * and thread end. Introduced in JDWP version 1.4.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InstanceOnlyFilter extends Filter<InstanceOnlyFilterable> {

  private ObjectId objectId;

  /**
   * Creates Instance Only Filter.
   * 
   * @param objectId
   *          Required objectId (which can reference null {@link MJIEnv#NULL}
   *          for static methods.
   */
  public InstanceOnlyFilter(ObjectId objectId) {
    super(ModKind.INSTANCE_ONLY, InstanceOnlyFilterable.class);
    this.objectId = objectId;
  }

  @Override
  public boolean matches(InstanceOnlyFilterable event) {
    ElementInfo eventInstance = event.instance();
    return Objects.equals(eventInstance, objectId.get());
  }

}
