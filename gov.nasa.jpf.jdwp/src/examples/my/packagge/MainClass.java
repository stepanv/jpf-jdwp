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

package my.packagge;

/**
 * <p>
 * This is a simple class that demonstrates standard capabilities of the
 * debugger. <br/>
 * No state traversal is performed since there is just one state.
 * </p>
 * <p>
 * <h2>How to use it</h2>
 * Just put a breakpoint somewhere or stop the execution in any other way and
 * take a look at the program state when it's stopped.
 * </p>
 * 
 * @author stepan
 * 
 */
public class MainClass {

  private static double primitiveStaticDouble;
  private static String referenceStaticString = new String("hello");

  private double instancePrimitiveDouble;
  private String instanceReference;

  MainClass(double doubleInitial, String stringInitial) {
    this.instancePrimitiveDouble = doubleInitial;
    this.instanceReference = stringInitial;
  }

  private double aPrimitiveReturnMethod(double doubleArg, String stringArg) {
    double localDouble = doubleArg * 2 + stringArg.length() + instancePrimitiveDouble;
    System.out.println("local double: " + doubleArg);
    return localDouble;
  }

  private Object aReferenceReturnMethod(double doubleArg, String stringArg) {
    String localStringObject = stringArg + (doubleArg * 4) + instanceReference;
    System.out.println("local string: " + localStringObject);
    return localStringObject;
  }

  public static void main(String[] args) {
    System.out.println("beginning");

    double localDouble = primitiveStaticDouble * 4.2 + Thread.activeCount();
    String localReferenceString = referenceStaticString + " ... some other string: " + Thread.currentThread().getState();

    MainClass mainClass = new MainClass(localDouble, localReferenceString);

    double returnDouble = mainClass.aPrimitiveReturnMethod(primitiveStaticDouble, referenceStaticString);
    synchronized (mainClass) {
      // here we should see we have locked mainClass instance
      Object returnObject = mainClass.aReferenceReturnMethod(primitiveStaticDouble, referenceStaticString);
      System.out.println("Returned string reference: " + returnObject);
    }

    System.out.println("Returned primitive double: " + returnDouble);

    System.out.println(localDouble);

    System.out.println("end");
  }

}
