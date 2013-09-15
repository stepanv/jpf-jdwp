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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.id.object.ObjectId;

/**
 * Identifier pointer class shades instances of {@link Long} the same way as
 * {@link ObjectId} and it's subtype do so that they all can be interchangeably
 * used in hash maps.
 * 
 * @author stepan
 * 
 */
public class IdentifierPointer {
  private Long id;

  /**
   * Constructor of Identifier pointer.
   * 
   * @param id
   *          An ID to shade.
   */
  public IdentifierPointer(Long id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Identifier<?>) {
      return ((Identifier<?>) obj).equals(id);
    }
    if (obj instanceof IdentifierPointer) {
      return id == ((IdentifierPointer) obj).id;
    }
    return false;
  }

  /**
   * Get the ID this pointer shades.
   * 
   * @return The ID.
   */
  public Long getId() {
    return id;
  }

}