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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.util.test.TestJPF;

import java.util.Arrays;

import org.junit.Before;

public abstract class TestJdwp extends TestJPF {

  static TestJdwp verifierTest;

  public TestJdwp() {
  }

  public TestJdwp(String sutClassName) {
    super(sutClassName);
  }

  private String[] modifyArgs(String[] args, String prefix, String value) {
    return modifyArgs(args, prefix, value, value);
  }

  private String[] modifyArgs(String[] args, String prefix, String value, String noMatchValue) {
    for (int i = 0; i < args.length; ++i) {
      if (args[i].startsWith(prefix)) {
        args[i] += "," + value;
        return args;
      }
    }
    // if the prefix is not there
    String[] newargs = Arrays.copyOf(args, args.length + 1);
    newargs[args.length] = prefix + noMatchValue;
    return newargs;
  }

  /**
   * Override arguments to fit jdwp needs.<br/>
   * Classpath and listener are modified.
   * 
   * @param args
   *          Arguments to override
   * @return Overriden arguments.
   */
  private String[] overrideArguments(String[] args) {

    String listener = JdwpTestListener.class.getName();
    String classpath = "lib/slf4j-api-1.7.5.jar,lib/slf4j-simple-1.7.5.jar";

    // add the JDWPTestListener
    args = modifyArgs(args, "+listener=", listener);
    // modify classpath
    args = modifyArgs(args, "+classpath=", classpath, "+," + classpath);

    return args;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.util.test.TestJPF#noPropertyViolation(java.lang.StackTraceElement
   * , java.lang.String[])
   */
  @Override
  protected JPF noPropertyViolation(StackTraceElement testMethod, String... args) {
    return super.noPropertyViolation(testMethod, overrideArguments(args));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.util.test.TestJPF#unhandledException(java.lang.StackTraceElement
   * , java.lang.String, java.lang.String, java.lang.String[])
   */
  @Override
  protected JPF unhandledException(StackTraceElement testMethod, String xClassName, String details, String... args) {
    return super.unhandledException(testMethod, xClassName, details, overrideArguments(args));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.util.test.TestJPF#propertyViolation(java.lang.StackTraceElement
   * , java.lang.Class, java.lang.String[])
   */
  @Override
  protected JPF propertyViolation(StackTraceElement testMethod, Class<? extends Property> propertyCls, String... args) {
    return super.propertyViolation(testMethod, propertyCls, overrideArguments(args));
  }

  /**
   * Sets the test instance so that it can be picked up from the JPF Test
   * Listener in order to execute the verify method.
   */
  @Before
  public void initializeExecutor() {
    TestJdwp.verifierTest = this;
  }

}
