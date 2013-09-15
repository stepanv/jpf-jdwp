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

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;

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
public interface TaggableIdentifier<T> extends Identifier<T> {


  /**
   * All subtypes must implement this method to provide the {@link Tag} (
   * {@link IdentifiableEnum} respectively) to this abstract class.
   * 
   * @return Something that can be a <bb>byte</bb>.
   */
  public IdentifiableEnum<Byte> getIdentifier();

  /**
   * Write this identifier with preceding tag byte.
   * 
   * @param os
   *          The output stream where to write.
   * @throws IOException
   *           In case of an I/O Error.
   */
  public void writeTagged(DataOutputStream os) throws IOException;

}
