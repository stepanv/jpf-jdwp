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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class adds taggable attribute to the standard identifier.<br/>
 * This class should be inherited by all identifiers that may include a
 * {@link Tag} with the identifier itself in the JDWP communication.
 * 
 * @author stepan
 * 
 * @see Identifier
 * @param <T>
 *          Just to expose the template further to implementors.
 */
public abstract class TaggableIdentifierBase<T> extends IdentifierBase<T> implements TaggableIdentifier<T> {

  /**
   * Constructs the taggable identifier.
   * 
   * @param id
   *          The unique id for the given object.
   * @param object
   *          The object that is represented by this identifier.
   */
  public TaggableIdentifierBase(long id, T object) {
    super(id, object);
  }

  @Override
  final public void writeTagged(DataOutputStream os) throws IOException {
    os.write(getIdentifier().identifier());
    write(os);
  }

}