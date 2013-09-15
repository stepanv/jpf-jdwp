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

import static org.junit.Assert.*;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jvm.JVMStackFrame;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;

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

    public void initMap(T frame, I frameId) {
      objectToIdentifierMap.put(frame, frameId);
    }

    public void initMap(I frameId) {
      identifierSet.put(frameId, new WeakReference<I>(frameId));
    }
  }

  public static class SpecificIdentifier extends GenericIdentifier<FrameId, StackFrame> {

    public int objectToIdMapSize() {
      return objectToIdentifierMap.size();
    }

    public int idMapSize() {
      return identifierSet.size();
    }

    public FrameId prepareFrame() {
      StackFrame frame = new JVMStackFrame(new MethodInfo("foo", "I(I)", 0));
      Long l = new Long(1);
      FrameId frameId = new FrameId(l, frame);

      initMap(frameId);
      initMap(frame, frameId);
      return frameId;

    }

    public FrameId get(Object l) {
      Reference<FrameId> ref = identifierSet.get(l);
      return ref.get();
    }

    public void printObjectToIdentifier() {
      System.out.println("Print Object to Identifier");
      for (StackFrame key : objectToIdentifierMap.keySet()) {
        System.out.println("Contains key:" + key + " ... value: " + objectToIdentifierMap.get(key));
      }
      System.out.println("Print Object to Identifier ... done");

      System.gc();
    }

    public void printIdToIdentifier() {
      System.out.println("Print ID to Identifier");
      for (FrameId key : identifierSet.keySet()) {
        System.out.println("Contains key:" + key + " ... value: " + identifierSet.get(key));
      }
      System.out.println("Print ID to Identifier ... done");

      System.gc();

    }
  }

  /**
   * This test proves that an existence of a reference to {@link StackFrame}
   * does prevent the GC from collecting items in both
   * {@link GenericIdentifier#identifierSet} and
   * {@link GenericIdentifier#objectToIdentifierMap} maps.
   */
  @Test
  public void testObjectRemains() {
    SpecificIdentifier frameFoo = new SpecificIdentifier();

    frameFoo.prepareFrame();
    StackFrame frame = null;
    assertNotNull(frameFoo.get(new IdentifierPointer(1L)));
    assertEquals(1, frameFoo.idMapSize());
    assertEquals(1, frameFoo.objectToIdMapSize());

    try {
      frame = frameFoo.get(new IdentifierPointer(1L)).get();
    } catch (InvalidIdentifierException e) {
    }

    System.gc();
    System.gc();

    assertEquals(1, frameFoo.idMapSize());
    assertEquals(1, frameFoo.objectToIdMapSize());
    assertNotNull(frame);
    assertNotNull(frameFoo.get(new IdentifierPointer(1L)));

  }

  /**
   * This test proves the fact that if no reference to {@link FrameId} and to
   * {@link StackFrame} exists both
   * {@link GenericIdentifier#objectToIdentifierMap} and
   * {@link GenericIdentifier#identifierSet} maps will cleanup provided the GC
   * runs.
   */
  @Test(expected = NullPointerException.class)
  public void testObjectDiscarded() {
    SpecificIdentifier frameFoo = new SpecificIdentifier();

    frameFoo.prepareFrame();
    assertEquals(1, frameFoo.idMapSize());
    assertEquals(1, frameFoo.objectToIdMapSize());

    System.gc();
    frameFoo.printIdToIdentifier();
    frameFoo.printObjectToIdentifier();
    System.gc();
    frameFoo.printIdToIdentifier();
    frameFoo.printObjectToIdentifier();

    // This test is really weird
    // If one comments out the printing above the assert below may fail
    // So .. if this fails it's maybe just that the GC doesn't collect
    // everything in the idMap map even if it should be collected
    assertEquals(0, frameFoo.idMapSize());

    assertEquals(0, frameFoo.objectToIdMapSize());
    frameFoo.get(new IdentifierPointer(1L));

  }

  /**
   * This test proves the fact that an existence of a reference to
   * {@link FrameId} doesn't prevent the GC from cleaning up the
   * {@link GenericIdentifier#objectToIdentifierMap} map.
   */
  @Test
  public void testObjectDiscardedFrameIdRemains() {
    SpecificIdentifier frameFoo = new SpecificIdentifier();

    FrameId frameId = frameFoo.prepareFrame();
    assertEquals(1, frameFoo.idMapSize());
    assertEquals(1, frameFoo.objectToIdMapSize());

    System.gc();
    frameFoo.printIdToIdentifier();
    frameFoo.printObjectToIdentifier();
    System.gc();
    frameFoo.printIdToIdentifier();
    frameFoo.printObjectToIdentifier();

    assertEquals(1, frameFoo.idMapSize());
    assertEquals(0, frameFoo.objectToIdMapSize());
    assertNotNull(frameId);
    assertTrue(frameId.isNull());

    FrameId frameId2 = frameFoo.get(new IdentifierPointer(1L));
    assertEquals(frameId2, frameId);

  }

}
