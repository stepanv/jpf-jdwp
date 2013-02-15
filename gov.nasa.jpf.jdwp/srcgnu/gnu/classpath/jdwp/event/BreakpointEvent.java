/* BreakpointEvent.java -- An event specifying that the interpreter
   has hit a breakpoint
   Copyright (C) 2006 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package gnu.classpath.jdwp.event;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.id.ThreadId;
import gnu.classpath.jdwp.util.Location;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * "Notification of a breakpoint in the target VM. The breakpoint event is
 * generated before the code at its location is executed."
 *
 * @author Keith Seitz  (keiths@redhat.com)
 */
public class BreakpointEvent
  extends Event
{
  // The thread in which this event occurred
  private ThreadInfo _thread;

  // Location where breakpoint occurred
  private Location _location;

  //object instance
  private Object _instance;

  /**
   * Constructs a new BreakpointEvent
   *
   * @param thread  thread in which event occurred
   * @param loc     location where breakpoint occurred
   * @param instance object instance
   */
  public BreakpointEvent(ThreadInfo thread, Location loc, ClassInfo instance)
  {
    super(JdwpConstants.EventKind.BREAKPOINT);
    _thread = thread;
    _location = loc;
    _instance = instance;
  }

  /**
   * Returns a specific filtering parameter for this event.
   * Valid types are thread and location.
   *
   * @param type  the type of parameter desired
   * @returns the desired parameter or null
   */
  public Object getParameter(int type)
  {
    if (type == EVENT_THREAD)
      return _thread;
    else if (type == EVENT_LOCATION)
      return _location;
    else if (type == EVENT_INSTANCE)
      return _instance;

    return null;
  }

  /**
   * Writes the event to the given stream
   *
   * @param outStream  the output stream to write the event to
   */
  protected void _writeData (DataOutputStream outStream)
    throws IOException
  {
    VMIdManager idm = VMIdManager.getDefault();
    ThreadId tid = (ThreadId) idm.getObjectId(_thread);

    tid.write(outStream);
    _location.write(outStream);
  }
  
  @Override
  public void printDebugInfo() {
	  System.out.println(" >>>>>>>>> Event: " + this + ", Location: " + _location + ", Thread: " + _thread);
  }
}
