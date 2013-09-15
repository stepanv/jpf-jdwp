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

package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ElementInfo;

/**
 * This class implements the corresponding <code>stringID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a string
 * object. Note: this is very different from {@link JdwpString}, which is a
 * {@link Value}.
 * </p>
 * 
 * @author stepan
 * 
 */
public class StringId extends ObjectIdImpl {

  /**
   * Constructs the string ID.
   * 
   * @param id
   *          The ID known by {@link ObjectIdManager}
   * @param object
   *          The {@link ElementInfo} instance that needs JDWP ID
   *          representation.
   */
  public StringId(long id, ElementInfo object) {
    super(Tag.STRING, id, object);
  }

}
