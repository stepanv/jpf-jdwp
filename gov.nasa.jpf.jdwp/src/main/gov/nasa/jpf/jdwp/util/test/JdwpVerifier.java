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

import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements two functionalities.
 * 
 * First of all it runs the JDWP command implemented in a subclass (
 * {@link JdwpVerifier#verifyOutsideOfSuT()}).<br/>
 * Second of all it implements the way how the SuT program can notify by
 * executing the {@link JdwpVerifier#verify()} method the
 * {@link JdwpTestListener} listener to run the JDWP test.
 * 
 * @see JdwpTestListener#methodEntered(gov.nasa.jpf.vm.VM,
 *      gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.MethodInfo)
 * 
 * @author stepan
 * 
 */
public abstract class JdwpVerifier {

  final static Logger logger = LoggerFactory.getLogger(JdwpVerifier.class);

  public static final String VERIFY_METHOD_NAME;

  static {
    try {
      // this is how we defend against method "verify" rename
      logger.trace("Methods found: {}", (Object) JdwpVerifier.class.getMethods());
      VERIFY_METHOD_NAME = JdwpVerifier.class.getDeclaredMethod("verify", Object[].class).getName();
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  protected ByteArrayOutputStream dataOutputBytes;
  protected DataOutputStream dataOutputStream;
  protected CommandContextProvider contextProvider;
  protected ByteBuffer bytes;

  /**
   * This method implements the JDWP verification which is run outside of SuT.
   * (That means by the main thread of JPF.)
   * 
   * @param contextProvider
   * @param dataOutputStream
   * @param dataOutputBytes
   * 
   * @throws Throwable
   */
  abstract protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable;

  /**
   * Call this method from SuT to trigger synchronous call of this method
   * outside of SuT. It sounds tricky but this is how it is.
   */
  public void verify(Object... passedObjects) {
    if (!TestJPF.isJPFRun()) {
      // Now, we're outside of SuT - executed from the listener

      try {
        init();
        verifyOutsideOfSuT(passedObjects);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        clear();
      }
    } else {
      // this is just notification in SuT - this is how we get into
      // methodExecuted notification so that we can execute this again
      // outside of SuT
    }
  }

  protected void init() {
    dataOutputBytes = new ByteArrayOutputStream(0);
    dataOutputStream = new DataOutputStream(dataOutputBytes);
    bytes = ByteBuffer.allocate(200); // This "might" be enough
    contextProvider = new CommandContextProvider(new VirtualMachine(VM.getVM().getJPF()), JdwpObjectManager.getInstance());
  }

  protected void clear() {
    try {
      dataOutputBytes.close(); // has no effect
      dataOutputStream.close();
    } catch (IOException e) {
      // we don't care let's try to go further
    }
  }

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

}
