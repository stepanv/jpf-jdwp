package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum InterfaceTypeCommand implements Command, ConvertibleEnum<Byte, InterfaceTypeCommand> {
  NONE;

  private static ReverseEnumMap<Byte, InterfaceTypeCommand> map = new ReverseEnumMap<Byte, InterfaceTypeCommand>(InterfaceTypeCommand.class);

  @Override
  public Byte identifier() {
    return null;
  }

  @Override
  public InterfaceTypeCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
  }
}