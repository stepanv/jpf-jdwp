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

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.NotImplementedException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.nio.ByteBuffer;

/**
 * The base class of the filter facility. <br/>
 * Here, in the filter facility, we want to use only and only JDWP internal
 * wrappers so that GC of JPF itself isn't affected. Therefore all Filter
 * instances never keep references to {@link ThreadInfo} or {@link ElementInfo}
 * (or any other JPF specific) instances.
 * <p>
 * <h2>JDWP Specification for modifiers/filters</h2>
 * Constraints used to control the number of generated events. Modifiers specify
 * additional tests that an event must satisfy before it is placed in the event
 * queue. Events are filtered by applying each modifier to an event in the order
 * they are specified in this collection Only events that satisfy all modifiers
 * are reported. A value of 0 means there are no modifiers in the request.
 * </p>
 * <p>
 * Filtering can improve debugger performance dramatically by reducing the
 * amount of event traffic sent from the target VM to the debugger VM.
 * </p>
 * 
 * @author stepan
 * 
 */
public abstract class Filter<T extends Event> {

  public static enum ModKind implements ConvertibleEnum<Byte, ModKind> {

    /**
     * <p>
     * Creates the Count filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link CountFilter} documentation.
     * </p>
     */
    COUNT(1) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        return new CountFilter(bytes.getInt());
      }
    },

    /**
     * <p>
     * Creates the Conditional filter/modifier of a possibly sent event.<br/>
     * </p>
     * Note that this modifier/filter is meant for future use and doesn't have
     * any use even in the JDWP JDK 7.
     */
    CONDITIONAL(2) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        // this is ok as long as this agent is intended to implement JDWP JDK 7
        // or lesser.
        throw new NotImplementedException();
      }
    },

    /**
     * <p>
     * Creates the Thread only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link ThreadOnlyFilter}
     * documentation.
     * </p>
     */
    THREAD_ONLY(3) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
        return new ThreadOnlyFilter(threadId);
      }
    },

    /**
     * <p>
     * Creates the Class only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link ClassOnlyFilter}
     * documentation.
     * </p>
     */
    CLASS_ONLY(4) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
        return new ClassOnlyFilter(referenceTypeId);
      }
    },

    /**
     * <p>
     * Creates the Class match filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link ClassMatchFilter}
     * documentation.
     * </p>
     */
    CLASS_MATCH(5) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        String classPattern = JdwpString.read(bytes);
        return new ClassMatchFilter(classPattern);
      }
    },

    /**
     * <p>
     * Creates the Class exclude filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link ClassExcludeFilter}
     * documentation.
     * </p>
     */
    CLASS_EXCLUDE(6) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        String classPattern = JdwpString.read(bytes);
        return new ClassExcludeFilter(classPattern);
      }
    },

    /**
     * <p>
     * Creates the Location only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link LocationOnlyFilter}
     * documentation.
     * </p>
     */
    LOCATION_ONLY(7) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        Location location = Location.factory(bytes, contextProvider);
        return new LocationOnlyFilter(location);
      }
    },

    /**
     * <p>
     * Creates the Exception only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link ExceptionOnlyFilter}
     * documentation.
     * </p>
     */
    EXCEPTION_ONLY(8) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        ReferenceTypeId exceptionOrNull = contextProvider.getObjectManager().readReferenceTypeId(bytes);
        boolean caught = bytes.get() != 0;
        boolean uncaught = bytes.get() != 0;
        return new ExceptionOnlyFilter(exceptionOrNull, caught, uncaught);
      }
    },

    /**
     * <p>
     * Creates the Field only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link FieldOnlyFilter}
     * documentation.
     * </p>
     */
    FIELD_ONLY(9) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        ReferenceTypeId declaring = contextProvider.getObjectManager().readReferenceTypeId(bytes);
        FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
        return new FieldOnlyFilter(declaring, fieldId);
      }
    },

    /**
     * <p>
     * Creates the Step filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link StepFilter} documentation.
     * </p>
     */
    STEP(10) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        return StepFilter.factory(bytes, contextProvider);
      }
    },

    /**
     * <p>
     * Creates the Instance only filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link InstanceOnlyFilter}
     * documentation.
     * </p>
     */
    INSTANCE_ONLY(11) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        ObjectId objectId = contextProvider.getObjectManager().readObjectId(bytes);
        return new InstanceOnlyFilter(objectId);
      }
    },
    
    /**
     * <p>
     * Creates the Source name match filter/modifier of a possibly sent event.<br/>
     * For further information refer to the {@link InstanceOnlyFilter}
     * documentation.
     * </p>
     */
    SOURCE_NAME_MATCH(12) {
      @Override
      public Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
        String sourceNamePattern = JdwpString.read(bytes);
        return new SourceNameMatchFilter(sourceNamePattern);
      }
    };

    private byte modKindId;

    ModKind(int modKindId) {
      this.modKindId = (byte) modKindId;
    }

    @Override
    public Byte identifier() {
      return modKindId;
    }

    private static ReverseEnumMap<Byte, ModKind> map = new ReverseEnumMap<Byte, Filter.ModKind>(ModKind.class);

    @Override
    public ModKind convert(Byte val) throws IllegalArgumentException {
      return map.get(val);
    }

    public abstract Filter<? extends Event> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException;
  }

  private ModKind modKind;
  private Class<T> genericClazz;

  public Filter(ModKind modKind, Class<T> genericClass) { // TODO remove
    // unused parameter
    this.modKind = modKind;
    this.genericClazz = genericClass;
  }

  public static Filter<? extends Event> factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpException {
    return ModKind.COUNT.convert(bytes.get()).createFilter(bytes, contextProvider);
  }

  /**
   * Whether this filter allows the given event.
   * 
   * @param event
   *          The event to be filtered.
   * @return True of false as a result of filtering.
   * @throws InvalidObjectException
   * @throws InvalidIdentifierException
   */
  public boolean matches(T event) throws InvalidIdentifierException {
    return false;
  }

  public String toString() {
    return "class: " + this.getClass() + "; modKind: " + modKind.toString();
  }

  public Class<T> getGenericClass() {
    return this.genericClazz;
  }

}
