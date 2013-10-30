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
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
   * <p>
   * The JDWP load utility class helps to create classes that are invisible for
   * the system classloader hence they can be loaded by a custom classloader.<br/>
   * I would say the behavior of classloaders is not standard in JPF.
   * </p>
   * Reused from {@link gov.nasa.jpf.test.java.net.LoadUtility}.
   * 
   * @see gov.nasa.jpf.test.java.net.LoadUtility
   */
  public static class JdwpLoadUtility {
    public String user_dir = System.getProperty("user.dir");
    public String pkg = "classloader_specific_tests";

    protected String originalPath = user_dir + "/build/tests/" + pkg;
    protected String tempPath = user_dir + "/build/" + pkg;

    protected String jarUrl = "jar:file:" + user_dir + "/build/" + pkg + ".jar!/";
    protected String dirUrl = "file:" + user_dir + "/build";

    /**
     * move the package, to avoid systemClassLoader loading its classes
     */
    public void movePkgOut() {
      if (!TestJPF.isJPFRun()) {
        movePkg(originalPath, tempPath);
      }
    }

    /**
     * move the package back to its original place
     */
    public void movePkgBack() {
      if (!TestJPF.isJPFRun()) {
        movePkg(tempPath, originalPath);
      }
    }

    protected void movePkg(String from, String to) {
      File dstFile = new File(to);
      if (!dstFile.exists()) {
        dstFile = new File(from);
        assertTrue(dstFile.renameTo(new File(to)));
      } else {
        File srcFile = new File(from);
        if (srcFile.exists()) {
          // empty the directory
          for (String name : srcFile.list()) {
            assertTrue((new File(from + "/" + name)).delete());
          }
          // remove the directory
          assertTrue(srcFile.delete());
        }
      }
    }
  }

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

      Set<String> loadedClasses = new HashSet<>();
      for (int i = 0; i < num; i++) {
        outputBytes.get();
        ReferenceTypeId typeId = contextProvider.getObjectManager().readReferenceTypeId(outputBytes);
        ClassInfo clazz = typeId.get();
        loadedClasses.add(clazz.getName());
      }

      assertEquals(4, num);
      assertTrue(loadedClasses.contains("classloader_specific_tests.Class1"));

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
    JdwpLoadUtility util = new JdwpLoadUtility();
    util.movePkgOut();
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

      URL[] urls = { new URL(util.dirUrl) };
      URLClassLoader cl = new URLClassLoader(urls);

      String cname = util.pkg + ".Class1";

      // we need to load completely unrelated class
      Class cls = cl.loadClass(cname);

      assertEquals(cls.getClassLoader(), cl);
      assertFalse(cls.getClassLoader() == ClassLoader.getSystemClassLoader());

      assertEquals(cls.getInterfaces().length, 1);
      for (Class<?> ifc : cls.getInterfaces()) {
        assertEquals(cls.getClassLoader(), ifc.getClassLoader());
      }

      userClassLoaderVerifier.verify(cl);
    }
    util.movePkgBack();

  }

}
