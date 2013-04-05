/* StackFrameCommandSet.java -- class to implement the StackFrame Command Set
   Copyright (C) 2005, 2007 Free Software Foundation

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


package gnu.classpath.jdwp.processor;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMFrame;
import gnu.classpath.jdwp.VMVirtualMachine;
import gnu.classpath.jdwp.exception.JdwpException;
import gnu.classpath.jdwp.exception.JdwpInternalErrorException;
import gnu.classpath.jdwp.exception.NotImplementedException;
import gnu.classpath.jdwp.id.ThreadId;
import gnu.classpath.jdwp.value.ObjectValue;
import gnu.classpath.jdwp.value.Value;
import gnu.classpath.jdwp.value.ValueFactory;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.management.RuntimeErrorException;

/**
 * A class representing the StackFrame Command Set.
 *
 * @author Aaron Luchko <aluchko@redhat.com>
 */
public class StackFrameCommandSet
  extends CommandSet
{
  public boolean runCommand(ByteBuffer bb, DataOutputStream os, byte command)
      throws JdwpException
  {
    boolean keepRunning = true;
    try
      {
        switch (command)
          {
          case JdwpConstants.CommandSet.StackFrame.GET_VALUES:
            executeGetValues(bb, os);
            break;
          case JdwpConstants.CommandSet.StackFrame.SET_VALUES:
            executeSetValues(bb, os);
            break;
          case JdwpConstants.CommandSet.StackFrame.THIS_OBJECT:
            executeThisObject(bb, os);
            break;
          case JdwpConstants.CommandSet.StackFrame.POP_FRAMES:
            executePopFrames(bb, os);
            break;
          default:
            throw new NotImplementedException("Command " + command +
            " not found in Stack Frame Command Set.");
          }
      }
    catch (IOException ex)
      {
        // The DataOutputStream we're using isn't talking to a socket at all
        // So if we throw an IOException we're in serious trouble
        throw new JdwpInternalErrorException(ex);
      }

    return false;
  }

  private void executeGetValues(ByteBuffer bb, DataOutputStream os)
      throws JdwpException, IOException
  {
    ThreadId tId = (ThreadId) idMan.readObjectId(bb);
    ThreadInfo thread = tId.getThread();

    // Although Frames look like other ids they are not. First they are not
    // ObjectIds since they don't exist in the users code. Storing them as an
    // ObjectId would mean they could be garbage collected since no one else
    // has a reference to them. Furthermore they are not ReferenceTypeIds since
    // these are held permanently and we want these to be held only as long as
    // the Thread is suspended.
    long frameID = bb.getLong();
    
    StackFrame frame = VMVirtualMachine.getFrame(thread, frameID);
    
    
    //throw new RuntimeException("not yet");
    int slots = bb.getInt();
    os.writeInt(slots); // Looks pointless but this is the protocol
   
    for (int i = 0; i < slots; i++)
      {
        int slot = bb.getInt();
        byte tag = bb.get();
        
        Object object = null;
        for (LocalVarInfo localVarInfo : frame.getMethodInfo().getLocalVars()) {
        	if (localVarInfo.getSlotIndex() == slot) {
        		object = frame.getLocalValueObject(localVarInfo);
        		break;
        	}
        	
        }
        
        Value val = ValueFactory.createFromObjectTagged(object, tag);
        val.writeTagged(os);
      }
  }

  private void executeSetValues(ByteBuffer bb, DataOutputStream os)
      throws JdwpException, IOException
  {
    ThreadId tId = (ThreadId) idMan.readObjectId(bb);
    ThreadInfo thread = tId.getThread();

    long frameID = bb.getLong();
    StackFrame frame = VMVirtualMachine.getFrame(thread, frameID);
    throw new RuntimeException("not yet");
//    int slots = bb.getInt();
//    for (int i = 0; i < slots; i++)
//      {
//        int slot = bb.getInt();
//        Value value = ValueFactory.createFromTagged(bb);
//        frame.setValue(slot, value);
//      }
  }

  private void executeThisObject(ByteBuffer bb, DataOutputStream os)
      throws JdwpException, IOException
  {
    ThreadId tId = (ThreadId) idMan.readObjectId(bb);
    ThreadInfo thread = tId.getThread();

    long frameID = bb.getLong();
    StackFrame frame = VMVirtualMachine.getFrame(thread, frameID);
    
    ElementInfo thisObject = VMVirtualMachine.vm.getJpf().getVM().getHeap().get(frame.getThis());

    if (thisObject instanceof StaticElementInfo) {
    	throw new IllegalArgumentException("Not sure whether we're allowed to return static elements"); // TODO this is possibly completely wrong
    }
    ObjectValue objVal = new ObjectValue(thisObject); 
    objVal.writeTagged(os);
  }

  private void executePopFrames(ByteBuffer bb, DataOutputStream os)
    throws JdwpException, IOException
  {
    if (!VMVirtualMachine.canPopFrames)
      {
        String msg = "popping frames is unsupported";
        throw new NotImplementedException(msg);
      }

    ThreadId tid = (ThreadId) idMan.readObjectId(bb);
    ThreadInfo thread = tid.getThread();
    long fid = bb.getLong();
    VMVirtualMachine.popFrames(thread, fid);
  }
}
