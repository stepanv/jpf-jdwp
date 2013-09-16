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

import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassLoaderException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.vm.ClassLoaderInfo;

/**
 * This interface represents the corresponding <code>classLoaderID</code> common
 * data type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a class
 * loader object.
 * </p>
 * 
 * @author stepan
 * 
 */
public interface ClassLoaderId extends ObjectId {

  /**
   * Gets the <tt>InfoObject</tt> that is bound to this identifier.<br/>
   * Tries to lazy load the object if it is null.
   * 
   * @return <tt>InfoObject</tt> instance
   * @throws InvalidObjectException
   *           If the <tt>InfoObject</tt> doesn't exist
   */
  public ClassLoaderInfo getClassLoaderInfo() throws InvalidClassLoaderException;

}
