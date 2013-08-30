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

package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.jdwp.command.Command;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CommandVerifier extends JdwpVerifier {

  public static class ObjectWrapper<T> {
    public T wrappedObject;
  }

  private Command command;
  protected Object[] passedObjects;
  protected MJIEnv mjiEnv;

  public CommandVerifier(Command command) {
    this.command = command;
  }

  protected ObjectId loadObjectId(int i) {
    return JdwpObjectManager.getInstance().getObjectId((ElementInfo) passedObjects[i]);
  }

  @SuppressWarnings("unchecked")
  protected <T> T loadBoxObject(int i, Class<T> clazz) {
    return (T) ((ElementInfo) passedObjects[i]).asBoxObject();
  }

  protected FieldId loadFieldId(ObjectId objectId, int i) {
    String fieldString = passedObjectAs(i, ElementInfo.class).asString();
    FieldInfo fieldInfo = objectId.get().getFieldInfo(fieldString);
    return JdwpObjectManager.getInstance().getFieldId(fieldInfo);
  }

  protected void storeToWrapper(int i, int objectReference) {
    DynamicElementInfo intResultElementInfo = (DynamicElementInfo) passedObjects[i];
    mjiEnv.setReferenceField(intResultElementInfo.getObjectRef(), "wrappedObject", objectReference);
  }

  protected void storeToArray(int i, int arrayIndex, int objectRef) {
    DynamicElementInfo arrayResultElementInfo = (DynamicElementInfo) passedObjects[i];
    mjiEnv.setReferenceArrayElement(arrayResultElementInfo.getObjectRef(), arrayIndex, objectRef);
  }

  @Override
  protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
    this.passedObjects = passedObjects;
    this.mjiEnv = VM.getVM().getCurrentThread().getMJIEnv();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream inputDataOutputStream = new DataOutputStream(baos);
    prepareInput(inputDataOutputStream);
    bytes = ByteBuffer.wrap(baos.toByteArray());

    command.execute(this.bytes, dataOutputStream, contextProvider);

    ByteBuffer outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
    processOutput(outputBytes);

  }

  abstract protected void processOutput(ByteBuffer outputBytes);

  abstract protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException;

  @SuppressWarnings("unchecked")
  protected <T> T passedObjectAs(int i, Class<T> clazz) {
    return (T) passedObjects[i];
  }

  protected void prepareIdentifier(Identifier<?> identifier) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    identifier.write(dos);

    bytes.put(baos.toByteArray());

    baos.close();
    dos.close();
  }

  protected void prepareUntaggedValue(ObjectId objectId) throws IOException {
    prepareIdentifier(objectId);
  }

}