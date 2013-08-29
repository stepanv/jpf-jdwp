package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.test.TestInError;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Unit tests of ClassType command JPF JDWP implementation.
 * 
 * @author stepan
 * 
 */
public class ClassTypeCommandTest extends TestJdwp {

  private ByteArrayOutputStream dataOutputBytes;
  private DataOutputStream dataOutputStream;
  private ByteBuffer bytes;

  public ClassTypeCommandTest() {
  }

  /** Reference interface */
  public static interface I1 {
  }

  /** Reference interface */
  public static interface I2 extends I1 {
  }

  /** Reference interface */
  public static interface I3 {
  }

  /** Reference class */
  public static class A implements I3 {
  }

  /** Reference class */
  public static class B extends A implements I2 {
  }

  private void resetBuffers() {
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

  @Test
  public void superclassTest() {

    String[] args = { "+target=HelloWorld" }; // using HelloWorld from
    // jpf-core src/examples
    Config config = new Config(args);
    JPF jpf = new JPF(config);
    VM vm = jpf.getVM();

    dataOutputBytes = new ByteArrayOutputStream(0);
    dataOutputStream = new DataOutputStream(dataOutputBytes);
    bytes = ByteBuffer.allocate(200); // This "might" be enough
    CommandContextProvider contextProvider = new CommandContextProvider(new VirtualMachine(jpf), JdwpObjectManager.getInstance());

    vm.initialize();

    ByteBuffer outputBytes;
    ReferenceTypeId refTypeId;

    try {
      ClassTypeCommand.SUPERCLASS.execute(ClassLoaderInfo.getSystemResolvedClassInfo(B.class.getName()), bytes, dataOutputStream,
                                          contextProvider);

      outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
      refTypeId = contextProvider.getObjectManager().readReferenceTypeId(outputBytes);
      assertEquals(ClassLoaderInfo.getSystemResolvedClassInfo(A.class.getName()), refTypeId.get());

      resetBuffers();

      ClassTypeCommand.SUPERCLASS.execute(ClassLoaderInfo.getSystemResolvedClassInfo(A.class.getName()), bytes, dataOutputStream,
                                          contextProvider);

      outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
      refTypeId = contextProvider.getObjectManager().readReferenceTypeId(outputBytes);
      assertEquals(ClassLoaderInfo.getSystemResolvedClassInfo(Object.class.getName()), refTypeId.get());

      resetBuffers();

      ClassTypeCommand.SUPERCLASS.execute(ClassLoaderInfo.getSystemResolvedClassInfo(Object.class.getName()), bytes, dataOutputStream,
                                          contextProvider);

      outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
      refTypeId = contextProvider.getObjectManager().readReferenceTypeId(outputBytes);
      assertEquals(null, refTypeId);

      resetBuffers();

      ClassTypeCommand.SUPERCLASS.execute(ClassLoaderInfo.getSystemResolvedClassInfo(I2.class.getName()), bytes, dataOutputStream,
                                          contextProvider);

      outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
      refTypeId = contextProvider.getObjectManager().readReferenceTypeId(outputBytes);
      assertEquals(ClassLoaderInfo.getSystemResolvedClassInfo(Object.class.getName()), refTypeId.get());

    } catch (Exception e) {
      throw new TestInError(e);
    }

  }

}
