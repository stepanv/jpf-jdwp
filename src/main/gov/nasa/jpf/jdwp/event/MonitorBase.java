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

package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The base class for all the monitor based events.<br/>
 * This class aggregates all the common functionality at one place.
 * 
 * @author stepan
 * 
 */
public class MonitorBase extends LocatableEvent {

  private ElementInfo taggedObject;

  /**
   * The general constructor. Hidden from the outside.
   * 
   * @param eventKind
   *          The event kind of the monitor event.
   * @param threadInfo
   *          Thread which entered monitor
   * @param location
   *          location of contended monitor enter
   * @param taggedObject
   *          Monitor object reference
   */
  protected MonitorBase(EventKind eventKind, ThreadInfo threadInfo, ElementInfo taggedObject, Location location) {
    super(eventKind, threadInfo, location);
    this.taggedObject = taggedObject;
  }

  /**
   * Overrides threadable specific write since we have to write tagged Object Id
   * before it's actual location.
   */
  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    ObjectId taggedObjectId = JdwpIdManager.getInstance().getObjectId(taggedObject);
    taggedObjectId.write(os);
    getLocation().write(os);

  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
    // empty .. no need to write an additional info
  }

}
