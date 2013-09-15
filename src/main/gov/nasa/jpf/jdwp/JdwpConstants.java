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

package gov.nasa.jpf.jdwp;

/**
 * The constants used in JDWP for JPF.
 * 
 * @author stepan
 * 
 */
public class JdwpConstants {
  public static final int MINOR = 1;
  public static final int MAJOR = 6;

  /**
   * Field name in {@link ThreadGroup} that references a parent
   * <tt>ThreadGroup</tt>.<br/>
   * This is environment dependent and may cause problems in the future if the
   * field name changes!
   */
  public static final String FIELDNAME_THREADGROUP_PARENT = "parent";

  /**
   * Field name in {@link ThreadGroup} that references the name of the
   * <tt>ThreadGroup</tt>.<br/>
   * This is environment dependent and may cause problems in the future if the
   * field name changes!
   */
  public static final String FIELDNAME_THREADGROUP_NAME = "name";

  /**
   * Field name in {@link ThreadGroup} that references all the threads that
   * belongs to the group. This field is an array of {@link Thread} instances.<br/>
   * This is environment dependent and may cause problems in the future if the
   * field name changes!
   */
  public static final String FIELDNAME_THREADGROUP_THREADS = "threads";

  /**
   * Field name in {@link ThreadGroup} that references all the subgroups that
   * are direct groups of the group. This field is an array of
   * {@link ThreadGroup} instances.<br/>
   * This is environment dependent and may cause problems in the future if the
   * field name changes!
   */
  public static final String FIELDNAME_THREADGROUP_GROUPS = "groups";

  /**
   * Field name in {@link Thread} class that references the name of the
   * <tt>ThreadGroup</tt> the thread belongs to.<br/>
   * This is environment dependent and may cause problems in the future if the
   * field name changes!
   */
  public static final String FIELDNAME_THREAD_GROUP = "group";

}
