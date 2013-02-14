/* Location.java -- class to read/write JDWP locations
   Copyright (C) 2005, 2006, 2007 Free Software Foundation

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
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package gnu.classpath.jdwp.util;

import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.VMMethod;
import gnu.classpath.jdwp.exception.JdwpException;
import gnu.classpath.jdwp.id.ClassReferenceTypeId;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.management.RuntimeErrorException;

/**
 * A class to read/write JDWP locations.
 *
 * @author Aaron Luchko <aluchko@redhat.com>
 */
public class Location
{
  private MethodInfo method;
  private long index;
private Instruction instruction;

  /**
   * Create a location with the given parameters.
   *
   * @param method the method
   * @param index location in the method
   */
  private Location(MethodInfo method, long index, Instruction instruction)
  {
    this.method = method;
    this.index = index;
    
    // just for debug info
    this.instruction = instruction;
  }
  
  public static Location factory(Instruction instruction) {
	  return new Location(instruction.getMethodInfo(), instruction.getInstructionIndex(), instruction);
  }

  /**
   * Read a location from the given bytebuffer, consists of a TAG (byte),
   * followed by a ReferenceTypeId, a MethodId and an index (long).
   *
   * @param bb this holds the location
   * @throws IOException    when an error occurs reading from the buffer
   * @throws JdwpException  for invalid class or method IDs
   */
  public Location(ByteBuffer bb)
    throws IOException, JdwpException
  {
    byte tag = bb.get();
    ClassReferenceTypeId classId =
      (ClassReferenceTypeId) VMIdManager.getDefault().readReferenceTypeId(bb);
    ClassInfo klass = classId.getType();
    
    method = VMMethod.readId(klass, bb);
    index = bb.getLong();
  }

  /**
   * Write the given location to an output stream.
   *
   * @param os stream to write to
   * @throws IOException when an error occurs writing to the stream
   */
  public void write(DataOutputStream os)
    throws IOException
  {
    // check if this is an empty location
    if (method != null)
      {
        VMIdManager idm = VMIdManager.getDefault();
        ClassReferenceTypeId crti =
          (ClassReferenceTypeId)
          idm.getReferenceTypeId(method.getClassInfo());
        
        crti.writeTagged(os);
        os.writeLong(method.getGlobalId());
        os.writeLong(index);
      }
    else
      {
        os.writeByte(1);
        os.writeLong((long) 0);
        os.writeLong((long) 0);
        os.writeLong((long) 0);
      }
  }
  
  /**
   * Sets up an empty location
   *
   * @return new Location (setup as empty)
   */
  public static Location getEmptyLocation()
  {
       return new Location(null, 0, null);
  }

  /**
   * Gets the method of this location
   *
   * @return the method
   */
  public MethodInfo getMethod()
  {
    return method;
  }

  /**
   * Gets the code index of this location
   *
   * @return the code index
   */
  public long getIndex ()
  {
    return index;
  }

  // convenient for debugging
  public String toString ()
  {
    return method.toString () + "." + index + instruction != null ? ", Line: " + instruction.getLineNumber() : "";
  }

  public boolean equals(Object obj)
  {
    if (obj instanceof Location)
      {
        Location l = (Location) obj;
        return (getMethod().equals(l.getMethod())
                && getIndex() == l.getIndex());
      }

    return false;
  }
}
