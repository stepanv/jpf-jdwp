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

package gov.nasa.jpf.jdwp.value;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * This class implements the corresponding JDWP Specification for strings sent
 * across the protocol. <br/>
 * <p>
 * <h2>JDWP Specification</h2>
 * A UTF-8 encoded string, not zero terminated, preceded by a four-byte integer
 * length.
 * </p>
 * 
 * @author stepan
 * 
 */
public class JdwpString {

  /**
   * No instances are allowed.
   */
  private JdwpString() {
  }

  /**
   * Writes data to the stream in a conformity with the specification.
   * 
   * @param data
   *          The string to send.
   * @param os
   *          The stream where to write the string.
   * @throws IOException
   *           If IO error occurs
   */
  public static void write(String data, DataOutputStream os) throws IOException {
    byte[] utfEncoded = data.getBytes("UTF-8");
    os.writeInt(utfEncoded.length);
    os.write(utfEncoded);
  }

  /**
   * Write data to the stream in a conformity with the specification.<br/>
   * If data is null, empty string &lt;""&gt; is sent.
   * 
   * @param data
   *          String or null to send across JDWP
   * @param os
   *          Output Stream
   * @throws IOException
   *           If I/O error occurs
   */
  public static void writeNullAsEmpty(String data, DataOutputStream os) throws IOException {
    write(data == null ? "" : data, os);
  }

  /**
   * Reads the string from in a conformity with the specification.
   * 
   * @param bytes
   *          Byte buffer
   * @return Received string
   */
  public static String read(ByteBuffer bytes) {
    int size = bytes.getInt();
    byte[] string = new byte[size];
    bytes.get(string);
    try {
      return new String(string, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // ALL VMs MUST implements UTF-8 (If compiled with 1.7, there is
      // already constant for this
      throw new RuntimeException("This VM doesn't conform to Java VM Specification!", e);
    }
  }

}
