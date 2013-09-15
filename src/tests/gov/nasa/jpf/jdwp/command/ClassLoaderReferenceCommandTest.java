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

import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Class loader reference command test.
 * 
 * @author stepan
 * 
 */
public class ClassLoaderReferenceCommandTest extends TestJdwp {

  public ClassLoaderReferenceCommandTest() {
  }

  BasicJdwpVerifier simpleSystemClassLoaderVerifier = new BasicJdwpVerifier() {

    @Override
    public void test() throws IOException, JdwpException {
      initializeSimpleJPF();

      // the system classloader
      bytes.putLong(0);

      ClassInfo classInfo = simpleJpfContextProvider().getVM().getCurrentThread().getSystemClassLoaderInfo()
          .getResolvedClassInfo(ClassLoaderReferenceCommandTest.class.getName());
      ClassLoaderReferenceCommand.VISIBLECLASSES.execute(bytes, dataOutputStream, simpleJpfContextProvider());

      wrapTheOutput();

      List<ClassInfo> loadedClasses = new LinkedList<>();
      int num = outputBytes.getInt();
      for (int i = 0; i < num; ++i) {
        outputBytes.get();
        ClassInfo loadedClass = simpleJpfContextProvider().getObjectManager().readReferenceTypeId(outputBytes).get();
        loadedClasses.add(loadedClass);
      }

      assertTrue(loadedClasses.contains(classInfo));
    }

  };

  /**
   * A simple test of a system class loader.
   */
  @Test
  public void simpleSystemClassLoaderTest() throws Throwable {
    simpleSystemClassLoaderVerifier.test();
  }

  CommandVerifier userClassLoaderVerifier = new CommandVerifier(ClassLoaderReferenceCommand.VISIBLECLASSES) {

    @Override
    protected void processOutput(ByteBuffer outputBytes) throws InvalidIdentifierException {
      int num = outputBytes.getInt();

      assertEquals(0, num);

    }

    @Override
    protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException, InvalidIdentifierException {
      ObjectId classLoader = loadObjectId(0);
      System.out.println(classLoader.get());
      classLoader.write(inputDataOutputStream);
    }
  };

  /**
   * Test using the custom classloader.<br/>
   * The problem is though, classloaders don't really work in JPF as one would
   * excpect. That is why the class is not registered as a loaded class even if
   * it is loaded!.
   */
  @Test
  public void simpleClassLoaderTest() throws IOException, JdwpException, ClassNotFoundException {
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener", */)) {

      URL url = ClassLoaderReferenceCommand.class.getClassLoader().getResource(UNNAMED_PACKAGE);
      ClassLoader userClassLoader = new URLClassLoader(new URL[] { url }, null);

      Thread.currentThread().setContextClassLoader(userClassLoader);
      // we need to load completely unrelated class
      Class foo = userClassLoader.loadClass(ClassLoaderReferenceCommandTest.class.getName());

      System.out.println("classloader: " + foo.getClassLoader());

      userClassLoaderVerifier.verify(userClassLoader);
    }

  }

}
