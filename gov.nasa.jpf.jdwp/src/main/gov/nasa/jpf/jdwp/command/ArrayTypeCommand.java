package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.id.type.ArrayTypeReferenceId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayTypeCommand implements Command, ConvertibleEnum<Byte, ArrayTypeCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Creates a new array object of this type with a given length.
   * </p>
   */
  NEWINSTANCE(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      ArrayTypeReferenceId arrayTypeId = contextProvider.getObjectManager().readArrayTypeReferenceId(bytes);
      int length = bytes.getInt();

      ClassInfo componentClassInfo = arrayTypeId.get().getComponentClassInfo();
      Heap heap = contextProvider.getVM().getHeap();

      // TODO do a check for current thread
      ThreadInfo threadInfo = contextProvider.getVM().getCurrentThread();

      // TODO [for PJA] how to get a proper type???
      int typeCode = Types.getTypeCode(componentClassInfo.getSignature());
      // this works for primitive types
      String type = Types.getElementDescriptorOfType(typeCode);
      if (type == null) {
        // this works for references ... it is so weird!
        type = componentClassInfo.getType();
      }

      ElementInfo elementInfoArray = heap.newArray(type, length, threadInfo);
      ArrayId arrayId = contextProvider.getObjectManager().getArrayId(elementInfoArray);
      arrayId.writeTagged(os);

    }
  };
  private byte commandId;

  private ArrayTypeCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ArrayTypeCommand> map = new ReverseEnumMap<Byte, ArrayTypeCommand>(ArrayTypeCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ArrayTypeCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}