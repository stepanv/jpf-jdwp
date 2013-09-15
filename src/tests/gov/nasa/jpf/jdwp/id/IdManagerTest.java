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

package gov.nasa.jpf.jdwp.id;

import static org.junit.Assert.assertEquals;
import gov.nasa.jpf.jdwp.exception.id.InvalidFrameIdException;
import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.jvm.JVMStackFrame;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.nio.ByteBuffer;

import org.junit.Test;

public class IdManagerTest extends BasicJdwpVerifier {

  @Test
  public void testObjectRemains() throws Exception {
    
    JdwpIdManager manager = JdwpIdManager.getInstance();
    
    StackFrame frame = new JVMStackFrame(new MethodInfo("foo", "I(I)", 0));
    
    this.init();
    manager.getFrameId(frame).write(dataOutputStream);
    ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
    
    // Run GC two times
    System.gc();
    System.gc();
    
    // verify we can get the frame
    FrameId frameId = manager.readFrameId(bb);
    assertEquals(frame, frameId.get());
  }
  
  @Test(expected=InvalidFrameIdException.class)
  public void testObjectDiscarded() throws Exception {
    
    JdwpIdManager manager = JdwpIdManager.getInstance();
    
    this.init();
    manager.getFrameId(new JVMStackFrame(new MethodInfo("foo", "I(I)", 0))).write(dataOutputStream);
    ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
    
    // Run GC two times
    System.gc();
    System.gc();
    
    // verify we can get the frame
    FrameId frameId = manager.readFrameId(bb);
    frameId.get();
  }

}
