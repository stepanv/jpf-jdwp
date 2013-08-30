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

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * This class implements the corresponding <code>classLoaderID</code> common
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
public class ClassLoaderId extends InfoObjectId<ClassLoaderInfo> {

  /**
   * Constructs the classloader ID.
   * 
   * 
   * @param id
   *          The ID known by {@link ObjectIdManager}
   * @param classLoaderInfo
   *          The {@link ClassLoaderInfo} instance that needs JDWP ID
   *          representation.
   */
  public ClassLoaderId(long id, ClassLoaderInfo classLoaderInfo) {
    this(id, VM.getVM().getHeap().get(classLoaderInfo.getClassLoaderObjectRef()), classLoaderInfo);
  }

  /**
   * The common constructor.
   * 
   * @param id
   *          The ID known by {@link ObjectIdManager}
   * @param elementInfo
   * @param classLoaderInfo
   */
  private ClassLoaderId(long id, ElementInfo elementInfo, ClassLoaderInfo classLoaderInfo) {
    super(Tag.CLASS_LOADER, id, elementInfo, classLoaderInfo);
  }

  /**
   * Constructs the classloader ID.
   * 
   * @param id
   *          The ID known by {@link ObjectIdManager}
   * @param elementInfo
   *          The {@link ElementInfo} instance that needs JDWP ID
   *          representation.
   */
  public ClassLoaderId(long id, ElementInfo elementInfo) {
    this(id, elementInfo, getClassLoaderInfo(elementInfo));
  }

  /**
   * Finds info object instance for the given parameter.
   * 
   * @param elementInfo
   *          The {@link ElementInfo} instance that is supposed to be paired
   *          with the {@link ClassLoaderInfo} instance.
   * @return The {@link ClassLoaderInfo} instance
   */
  private static ClassLoaderInfo getClassLoaderInfo(ElementInfo elementInfo) {
    ThreadInfo currentThread = VM.getVM().getCurrentThread();
    MJIEnv env = currentThread.getMJIEnv();
    // TODO maybe don't use current thread but something better...

    return env.getClassLoaderInfo(elementInfo.getObjectRef());
  }

  @Override
  protected ClassLoaderInfo resolveInfoObject() throws InvalidObject {
    return getClassLoaderInfo(get());
  }
}
