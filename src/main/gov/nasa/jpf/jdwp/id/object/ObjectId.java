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

import gov.nasa.jpf.jdwp.command.ObjectReferenceCommand;
import gov.nasa.jpf.jdwp.exception.JdwpException.ErrorType;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * This interface represents the corresponding <code>objectID</code> common data
 * type (tagged-objectID respectively) from the JDWP Specification.
 * 
 * <p>
 * {@link ObjectId} class stands for all the elements in the JPF that are
 * accessible from the SUT.<br/>
 * The {@link ElementInfo} generic type of {@link TaggableIdentifier} forces all
 * instances of classes and subclasses of {@link ObjectId} to represent an
 * {@link ElementInfo}.<br/>
 * There are several subclasses of this class that represent only specific
 * objects in SUT (like {@link ThreadId} represents {@link Thread} which is
 * represented by {@link ThreadInfo}) which are required by the JDWP
 * Specification. Nevertheless, those subclasses are sometimes treated by JPDA
 * as {@link ObjectId} instances as well.
 * 
 * <br/>
 * 
 * <h3>ElementInfo hashCode invariant problem</h3>
 * The biggest problem with ElementInfos is that their hashCode() method returns
 * different values throughout the lifetime of the object they represent.<br/>
 * If ElementInfo represents java.lang.Thread then its hashCode changes even
 * when the thread changes its state from STARTED to RUNNING.<br/>
 * Therefore it's not possible to put ElementInfos into hashMaps and it's also
 * tricky to call equals (since equals is congruent).
 * 
 * <br/>
 * 
 * <h3>What kind of information do we need</h3>
 * Every ObjectId stands for one object instance which is identified by its
 * pointer (heap obj ref).<br/>
 * JPF is little bit tricky since it can recreate new ElementInfo instance for a
 * object instance in SuT. And therefore it's irrelevant to keep the ElementInfo
 * instance here (even as a weak reference). We should keep the HEAP index and
 * always return the up-to-date ElementInfo instance.<br/>
 * The only question is what if HEAP index is reused after the GC activity by
 * completely strange new object?<br/>
 * [for PJA] is this possible?
 * 
 * <br/>
 * 
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM. A particular object will be
 * identified by exactly one objectID in JDWP commands and replies throughout
 * its lifetime (or until the objectID is explicitly disposed). An ObjectID is
 * not reused to identify a different object unless it has been explicitly
 * disposed, regardless of whether the referenced object has been garbage
 * collected. An objectID of 0 represents a null object.<br/>
 * 
 * Note that the existence of an object ID does not prevent the garbage
 * collection of the object. Any attempt to access a a garbage collected object
 * with its object ID will result in the {@link ErrorType#INVALID_OBJECT} error
 * code. Garbage collection can be disabled with the
 * {@link ObjectReferenceCommand#DISABLECOLLECTION} command, but it is not
 * usually necessary to do so.
 * </p>
 * 
 * @author stepan
 * 
 */
public interface ObjectId extends TaggableIdentifier<DynamicElementInfo>, Value {

  /**
   * Disable garbage collection of the object instance this identifier
   * represents.
   * 
   * @throws InvalidObjectException
   */
  public void disableCollection() throws InvalidObjectException;

  /**
   * Enable garbage collection of the object instasnce this identifier
   * represents.
   * 
   * @throws InvalidObjectException
   */
  public void enableCollection() throws InvalidObjectException;

  /**
   * Get the modifiable element info.
   * 
   * @return The modifiable instance of the object this identifier stands for.
   * @throws InvalidObjectException
   *           If this identifier is invalid.
   */
  public DynamicElementInfo getModifiable() throws InvalidObjectException;

  /**
   * Get the element info.
   * 
   * @throws InvalidObjectException
   *           If this identifier is invalid.
   */
  @Override
  public DynamicElementInfo get() throws InvalidObjectException;

}
