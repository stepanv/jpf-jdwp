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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassObjectReferenceCommand implements Command, ConvertibleEnum<Byte, ClassObjectReferenceCommand> {
  /**
   * Returns the reference type reflected by this class object.
   * <p>
   * For a reverse operation refer too {@link ReferenceTypeCommand#CLASSOBJECT}
   * </p>
   */
  REFLECTEDTYPE(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      ClassObjectId oid = contextProvider.getObjectManager().readClassObjectId(bytes);
      ClassInfo ci = oid.getInfoObject();

      // The difference between a ClassObjectId and a ReferenceTypeId is
      // one is stored as an ObjectId and the other as a ReferenceTypeId.
      ReferenceTypeId refId = contextProvider.getObjectManager().getReferenceTypeId(ci);
      refId.writeTagged(os);

    }
  };
  private byte commandId;

  private ClassObjectReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ClassObjectReferenceCommand> map = new ReverseEnumMap<Byte, ClassObjectReferenceCommand>(
      ClassObjectReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ClassObjectReferenceCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}