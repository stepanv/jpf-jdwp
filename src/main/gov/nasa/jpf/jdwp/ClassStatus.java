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

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The status of a class.<br/>
 * Currently not really used in JPF. <br/>
 * See <a href=
 * "http://docs.oracle.com/javase/7/docs/platform/jvmti/jvmti.html#GetClassStatus"
 * >http://docs.oracle.com/javase/7/docs/platform/jvmti/jvmti.html#
 * GetClassStatus</a>
 * 
 * 
 * @author stepan
 * 
 */
public enum ClassStatus {
  VERIFIED(1), PREPARED(2), INITIALIZED(4), ERROR(8);

  private int statusId;

  /**
   * The constructor.
   * 
   * @param statusId
   *          The ID of the status.
   */
  ClassStatus(int statusId) {
    this.statusId = statusId;
  }

  /**
   * Writes the status to the output stream.
   * 
   * @param os
   *          The output stream where to write the status.
   * @throws IOException
   *           If an I/O error occurs.
   */
  public void write(DataOutputStream os) throws IOException {
    os.writeInt(statusId);
  }
}
