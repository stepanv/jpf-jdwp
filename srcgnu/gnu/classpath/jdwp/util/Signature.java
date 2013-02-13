/* Signature.java -- utility class to compute class and method signatures
   Copyright (C) 2005 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package gnu.classpath.jdwp.util;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.MethodInfo;

/**
 * A class to compute class and method signatures.
 *
 * @author Tom Tromey  (tromey@redhat.com)
 * @author Keith Seitz  (keiths@redhat.com)
 */
public class Signature
{
  /**
   * Computes the class signature, i.e., java.lang.String.class
   * returns "Ljava/lang/String;".
   *
   * @param theClass  the class for which to compute the signature
   * @return          the class's type signature
   */
  public static String computeClassSignature (ClassInfo classInfo)
  {
    return classInfo.getSignature();
  }

  /**
   * Computes the field signature which is just the class signature of the
   * field's type, ie a Field of type java.lang.String this will return
   * "Ljava/lang/String;".
   *
   * @param field  the field for which to compute the signature
   * @return       the field's type signature
   */
  public static String computeFieldSignature (FieldInfo field)
  {
    return computeClassSignature (field.getClassInfo());
  }

  /**
   * Computes the method signature, i.e., java.lang.String.split (String, int)
   * returns "(Ljava/lang/String;I)[Ljava/lang/String;"
   *
   * @param method  the method for which to compute the signature
   * @return        the method's type signature
   */
  public static String computeMethodSignature (MethodInfo method)
  {
	  return method.getSignature();
  }

}
