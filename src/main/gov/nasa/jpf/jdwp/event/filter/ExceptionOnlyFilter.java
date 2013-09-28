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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.ExceptionOnlyFilterable;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.id.object.special.NullReferenceId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;

/**
 * <p>
 * Exception Only filter class that restricts reported exceptions.
 * </p>
 * <p>
 * Can be used with {@link ExceptionOnlyFilterable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported exceptions by their class and whether they are caught or
 * uncaught. This modifier can be used with exception event kinds only.
 * </p>
 * 
 * @see ExceptionEvent
 * @see ExceptionOnlyFilterable
 * 
 * @author stepan
 * 
 */
public class ExceptionOnlyFilter extends Filter<ExceptionOnlyFilterable> {
  ReferenceTypeId exceptionOrNull;
  boolean caught;
  boolean uncaught;

  /**
   * 
   * @param exceptionOrNull
   *          Exception to report. Null (0) (ie. {@link NullReferenceId}
   *          instance) means report exceptions of all types. A non-null type
   *          restricts the reported exception events to exceptions of the given
   *          type or any of its subtypes.
   * @param caught
   *          Report caught exceptions
   * @param uncaught
   *          Report uncaught exceptions. Note that it is not always possible to
   *          determine whether an exception is caught or uncaught at the time
   *          it is thrown. See the exception event catch location under
   *          composite events for more information.
   */
  public ExceptionOnlyFilter(ReferenceTypeId exceptionOrNull, boolean caught, boolean uncaught) {
    super(ModKind.EXCEPTION_ONLY, ExceptionOnlyFilterable.class);
    this.exceptionOrNull = exceptionOrNull;
    this.caught = caught;
    this.uncaught = uncaught;
    
    logger.info("EXCEPTION_ONLY: exception {} ... caught {} ... uncaugh {}", exceptionOrNull, caught, uncaught);
  }
  
  final static Logger logger = LoggerFactory.getLogger(ExceptionOnlyFilter.class);

  @Override
  public boolean matches(ExceptionOnlyFilterable event) {
    return event.visit(this);
  }

  public boolean matches(ExceptionEvent event) {
    logger.info("EXCEPTION_ONLY: FILTER: exception {} ... caught {} ... uncaugh {}", exceptionOrNull, caught, uncaught);
    logger.info("EXCEPTION_ONLY: EVENT: exception {} ... caught {} ... uncaugh {} ... event object: {}", event.getException().getClassInfo(), event.isCaught(), !event.isCaught(), event);

    // we don't want caught exceptions
    if (event.isCaught() && !caught) {
      return false;
    }

    // we don't want uncaught exceptions
    if (!event.isCaught() && !uncaught) {
      return false;
    }

    if (exceptionOrNull == null || exceptionOrNull.isNull()) {
      // return exception of all types as the specs states
      return true;
    }
    
    ElementInfo exception = event.getException();
    if (exception == null) {
    	// this should be property violation kind
    	return true;
    }
    ClassInfo exceptionClassInfo = exception.getClassInfo();

    try {
      // restricts the reported exception events to exceptions of the
      // given type or any of its !!subtypes!!
      return exceptionClassInfo.isInstanceOf(exceptionOrNull.get());
    } catch (InvalidIdentifierException e) {
      // exceptionOrNull stands for not known (GCed) type hence is ineffective
      return false;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    return super.toString() + sb.append(" exception: '").append(exceptionOrNull).append("' ... caught: '").append(caught).append("' ... uncaugh: '").append(uncaught).append("'");
  }
  
  

}
