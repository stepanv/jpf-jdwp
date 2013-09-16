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

import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassObjectException;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;

/**
 * The {@link ClassObjectId} implementation.
 * 
 * @see ClassObjectId
 * 
 * @author stepan
 * 
 */
public class ClassObjectIdImpl extends ObjectIdImpl implements ClassObjectId {

  /**
   * Constructs the {@link ClassObjectIdImpl} based on the given parameters.
   * 
   * @param id
   *          The ID.
   * @param classInfo
   *          The corresponding class.
   */
  public ClassObjectIdImpl(long id, ClassInfo classInfo) {
    this(id, classInfo.getClassObject());
  }

  /**
   * Constructs the {@link ClassObjectIdImpl} based on the given parameters.
   * 
   * @param id
   *          The ID.
   * @param elementInfo
   *          The instance.
   */
  public ClassObjectIdImpl(long id, ElementInfo elementInfo) {
    super(Tag.CLASS_OBJECT, id, elementInfo);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.object.InfoObjectId#getInfoObject()
   */
  @Override
  public ClassInfo getClassInfo() throws InvalidClassObjectException {
    try {
      int typeNameRef = get().getReferenceField(JdwpConstants.FIELDNAME_CLASS_NAME);
      ElementInfo typeName = VM.getVM().getHeap().get(typeNameRef);
      String reflectedTypeString = typeName.asString();
      ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo(reflectedTypeString);
      return ci;
    } catch (NullPointerException e) {
      throw new InvalidClassObjectException(this);
    }
  }
}
