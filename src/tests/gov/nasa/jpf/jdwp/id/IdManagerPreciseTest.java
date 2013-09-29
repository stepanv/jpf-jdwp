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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.IntegerFieldInfo;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.junit.Test;

/**
 * With all the tests bellow, it is intended to prove that the both Maps
 * {@link GenericIdentifier#identifierSet} and
 * {@link GenericIdentifier#objectToIdentifierMap} can automatically cleanup if
 * an instance of <tt>T</tt> is added to the
 * {@link GenericIdentifier#objectToIdentifierMap} provided the reference to the
 * <tt>T</tt> is not kept anywhere else than in those maps and that the GC runs.
 * 
 * @author stepan
 * 
 */
public class IdManagerPreciseTest {

  public static class GenericIdentifier<I extends Identifier<T>, T> {

    Map<I, WeakReference<I>> identifierSet = new WeakHashMap<I, WeakReference<I>>();
    Map<T, I> objectToIdentifierMap = new WeakHashMap<T, I>();

    public void initMap(T field, I fieldId) {
      objectToIdentifierMap.put(field, fieldId);
    }

    public void initMap(I fieldId) {
      identifierSet.put(fieldId, new WeakReference<I>(fieldId));
    }
  }

  public static class SpecificIdentifier extends GenericIdentifier<FieldId, FieldInfo> {

    public int objectToIdMapSize() {
      return objectToIdentifierMap.size();
    }

    public int idMapSize() {
      return identifierSet.size();
    }

    public FieldId prepareField() {
      FieldInfo field = new IntegerFieldInfo("foobar", 0);
      Long l = new Long(1);
      FieldId fieldId = new FieldId(l, field);

      initMap(fieldId);
      initMap(field, fieldId);
      return fieldId;

    }

    public FieldId get(Object l) {
      Reference<FieldId> ref = identifierSet.get(l);
      return ref.get();
    }

    public void printObjectToIdentifier() {
      System.out.println("Print Object to Identifier");
      for (FieldInfo key : objectToIdentifierMap.keySet()) {
        System.out.println("Contains key:" + key + " ... value: " + objectToIdentifierMap.get(key));
      }
      System.out.println("Print Object to Identifier ... done");

      System.gc();
    }

    public void printIdToIdentifier() {
      System.out.println("Print ID to Identifier");
      for (FieldId key : identifierSet.keySet()) {
        System.out.println("Contains key:" + key + " ... value: " + identifierSet.get(key));
      }
      System.out.println("Print ID to Identifier ... done");

      System.gc();

    }
  }

  /**
   * This test proves that an existence of a reference to {@link FieldInfo}
   * does prevent the GC from collecting items in both
   * {@link GenericIdentifier#identifierSet} and
   * {@link GenericIdentifier#objectToIdentifierMap} maps.
   */
  @Test
  public void testObjectRemains() {
    SpecificIdentifier fieldFoo = new SpecificIdentifier();

    fieldFoo.prepareField();
    FieldInfo field = null;
    assertNotNull(fieldFoo.get(new IdentifierPointer(1L)));
    assertEquals(1, fieldFoo.idMapSize());
    assertEquals(1, fieldFoo.objectToIdMapSize());

    try {
      field = fieldFoo.get(new IdentifierPointer(1L)).get();
    } catch (InvalidIdentifierException e) {
    }

    System.gc();
    System.gc();

    assertEquals(1, fieldFoo.idMapSize());
    assertEquals(1, fieldFoo.objectToIdMapSize());
    assertNotNull(field);
    assertNotNull(fieldFoo.get(new IdentifierPointer(1L)));

  }

  /**
   * This test proves the fact that if no reference to {@link FieldId} and to
   * {@link FieldInfo} exists both
   * {@link GenericIdentifier#objectToIdentifierMap} and
   * {@link GenericIdentifier#identifierSet} maps will cleanup provided the GC
   * runs.
   */
  @Test(expected = NullPointerException.class)
  public void testObjectDiscarded() {
    SpecificIdentifier fieldFoo = new SpecificIdentifier();

    fieldFoo.prepareField();
    assertEquals(1, fieldFoo.idMapSize());
    assertEquals(1, fieldFoo.objectToIdMapSize());

    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();
    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();

    // This test is really weird
    // If one comments out the printing above the assert below may fail
    // So .. if this fails it's maybe just that the GC doesn't collect
    // everything in the idMap map even if it should be collected
    assertEquals(0, fieldFoo.idMapSize());

    assertEquals(0, fieldFoo.objectToIdMapSize());
    fieldFoo.get(new IdentifierPointer(1L));

  }

  /**
   * This test proves the fact that an existence of a reference to
   * {@link FieldId} doesn't prevent the GC from cleaning up the
   * {@link GenericIdentifier#objectToIdentifierMap} map.
   */
  @Test
  public void testObjectDiscardedFieldIdRemains() {
    SpecificIdentifier fieldFoo = new SpecificIdentifier();

    FieldId fieldId = fieldFoo.prepareField();
    assertEquals(1, fieldFoo.idMapSize());
    assertEquals(1, fieldFoo.objectToIdMapSize());

    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();
    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();
    
    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();
    System.gc();
    fieldFoo.printIdToIdentifier();
    fieldFoo.printObjectToIdentifier();


    assertEquals(1, fieldFoo.idMapSize());
    assertEquals(0, fieldFoo.objectToIdMapSize());
    assertNotNull(fieldId);
    assertTrue(fieldId.isNull());

    FieldId fieldId2 = fieldFoo.get(new IdentifierPointer(1L));
    assertEquals(fieldId2, fieldId);

  }

}
