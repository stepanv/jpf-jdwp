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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.command.Command;
import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements provides the JDWP test classes with easy to use buffers
 * that are used by the all the {@link Command} the JDWP Commands.
 * 
 * @author stepan
 * 
 */
public class BasicJdwpVerifier {

  final static Logger logger = LoggerFactory.getLogger(BasicJdwpVerifier.class);

  protected ByteArrayOutputStream dataOutputBytes;
  protected DataOutputStream dataOutputStream;
  protected ByteBuffer bytes;

  /**
   * Initialize the buffers.
   */
  protected void init(int bufferSize) {
    dataOutputBytes = new ByteArrayOutputStream(bufferSize);
    dataOutputStream = new DataOutputStream(dataOutputBytes);
    bytes = ByteBuffer.allocate(bufferSize); // This "might" be enough
  }

  /**
   * Initialize the buffers.<br/>
   * The {@link BasicJdwpVerifier#bytes} buffer is limited to 200 by default and
   * may throw {@link IndexOutOfBoundsException}.
   */
  protected void init() {
    init(200);
  }

  /**
   * Finish the buffers.
   */
  protected void clear() {
    try {
      dataOutputBytes.close(); // has no effect
      dataOutputStream.close();
    } catch (IOException e) {
      // we don't care let's try to go further
    }
  }

  /**
   * Reset the buffers so that they are like new ones.
   */
  protected void reset() {
    try {
      dataOutputBytes.close(); // has no effect
      dataOutputStream.close();
    } catch (IOException e) {
      // we don't care let's try to go further
    }

    dataOutputBytes = new ByteArrayOutputStream(0);
    dataOutputStream = new DataOutputStream(dataOutputBytes);
    bytes.clear();
  }

  private CommandContextProvider simpleJPFContextProvider = null;
  protected ByteBuffer outputBytes = null;

  protected void initializeSimpleJPF() {
    String[] args = { "+target=HelloWorld" }; // using HelloWorld from
    // jpf-core src/examples
    Config config = new Config(args);
    JPF jpf = new JPF(config);
    VM vm = jpf.getVM();

    simpleJPFContextProvider = new CommandContextProvider(new VirtualMachine(jpf), JdwpIdManager.getInstance());

    vm.initialize();
    init();

  }

  protected void wrapTheOutput() {
    outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
  }

  protected CommandContextProvider simpleJpfContextProvider() {
    return simpleJPFContextProvider;
  }

  public void test() throws Throwable {
  };

}
