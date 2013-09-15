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

import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.lang.ref.WeakReference;

/**
 * This class extends the functionality of standard object identifiers. Another
 * object (which is called here the <tt>InfoObject</tt>) is bound to this JDWP
 * identifier.<br/>
 * See any subclass for further examples and explanation of the use of this
 * class.<br/>
 * The need for this class comes from the fact that several objects in the SUT
 * are represented by both the {@link ElementInfo} instance and also
 * SomeNameInfo object (like {@link ThreadInfo}). Therefore it is convenient to
 * keep such information at one place.<br/>
 * 
 * Supports lazy load of the infoObject in case it's null.<br/>
 * It's up to the programmer to decide whether the infoObject can be cached or
 * not. Caching is not advisable for {@link ThreadInfo} instances for example
 * since those do change during the SuT execution.
 * 
 * @author stepan
 * 
 * @param <T>
 *          the <tt>InfoObject</tt> that is bound to this identifier (in an
 *          addition to the {@link ElementInfo} instance)
 */
public abstract class InfoObjectId<T> extends ObjectId {

  /**
   * Constructs Info Object ID instance.
   * 
   * @param tag
   *          The Tag
   * @param id
   *          The identifier
   * @param object
   *          The instance in SuT this Object ID stands for
   * @param infoObject
   *          Info Object or null if lazy load is desired
   */
  public InfoObjectId(Tag tag, long id, ElementInfo object, T infoObject) {
    super(tag, id, object);

    infoObjectReference = new WeakReference<T>(infoObject);
  }

  protected WeakReference<T> infoObjectReference;

  /**
   * Gets the <tt>InfoObject</tt> that is bound to this identifier.<br/>
   * Tries to lazy load the object if it is null.
   * 
   * @return <tt>InfoObject</tt> instance
   * @throws InvalidObjectException
   *           If the <tt>InfoObject</tt> doesn't exist
   */
  public T getInfoObject() throws InvalidObjectException {
    T infoObject = infoObjectReference.get();
    if (infoObject == null) {
      infoObject = resolveInfoObject();
      if (infoObject != null) {
        infoObjectReference = new WeakReference<T>(infoObject);
      } else {
        throw new InvalidObjectException(this);
      }
    }
    return infoObject;
  }

  /**
   * The way how to resolve the info object by the subclass for use in
   * {@link InfoObjectId#getInfoObject()} in case lazy load is performed.
   * 
   * @return Info Object instance
   * @throws InvalidObjectException
   */
  abstract protected T resolveInfoObject() throws InvalidObjectException;

}
