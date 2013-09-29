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

import gov.nasa.jpf.jdwp.exception.id.InvalidMethodIdException;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * This class implements the corresponding <code>methodID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * All the methods in the JPF are promised to have a unique ID which is true for
 * one classloader. Therefore these IDs can be safely used since the IDs are
 * definitively unique for any clazz and all its supertypes.
 * </p>
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a method in some class in the target VM. The methodID
 * must uniquely identify the method within its class/interface or any of its
 * subclasses/subinterfaces/implementors. A methodID is not necessarily unique
 * on its own; it is always paired with a referenceTypeID to uniquely identify
 * one method. The referenceTypeID can identify either the declaring type of the
 * method or a subtype.
 * </p>
 * 
 * @see JdwpIdManager#MethodIdManager
 * @author stepan
 * 
 */
public class MethodId extends IdentifierBase<MethodInfo> {

  /**
   * Method ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier which must be
   *          {@link MethodInfo} GID.
   */
  MethodId(long id) {
    super(id, null);
  }

  /**
   * Method ID constructor.
   * 
   * @param method
   *          The method info to construct the <code>methodID</code> for.
   */
  MethodId(MethodInfo method) {
    super((long) method.getGlobalId(), null);
  }

  @Override
  public MethodInfo get() throws InvalidMethodIdException {
    MethodInfo method = MethodInfo.getMethodInfo(id().intValue());
    if (method == null) {
      nullObjectHandler();
    }
    return method;
  }

  @Override
  public MethodInfo nullObjectHandler() throws InvalidMethodIdException {
    throw new InvalidMethodIdException(this);
  }

}
