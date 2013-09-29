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
import gov.nasa.jpf.jdwp.exception.id.InvalidFieldIdException;
import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.IntegerFieldInfo;

import java.nio.ByteBuffer;

import org.junit.Test;

public class IdManagerTest extends BasicJdwpVerifier {

  @Test
  public void testObjectRemains() throws Exception {
    
    JdwpIdManager manager = JdwpIdManager.getInstance();
    
    FieldInfo field = new IntegerFieldInfo("foobar", 0);
    
    this.init();
    manager.getFieldId(field).write(dataOutputStream);
    ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
    
    // Run GC two times
    System.gc();
    System.gc();
    
    // verify we can get the field
    FieldId fieldId = manager.readFieldId(bb);
    assertEquals(field, fieldId.get());
  }
  
  @Test(expected=InvalidFieldIdException.class)
  public void testObjectDiscarded() throws Exception {
    
    JdwpIdManager manager = JdwpIdManager.getInstance();
    
    this.init();
    manager.getFieldId(new IntegerFieldInfo("foobar", 0)).write(dataOutputStream);
    ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
    
    // Run GC two times
    System.gc();
    System.gc();
    
    // verify we can get the field
    FieldId fieldId = manager.readFieldId(bb);
    fieldId.get();
  }

}
