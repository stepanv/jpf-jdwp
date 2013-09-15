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
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Thread group reference command test.
 * 
 * @author stepan
 * 
 */
public class ThreadGroupReferenceCommandTest extends TestJdwp {

  public ThreadGroupReferenceCommandTest() {
  }

  CommandVerifier childrenVerifier = new CommandVerifier(ThreadGroupReferenceCommand.CHILDREN) {

    @Override
    protected void processOutput(ByteBuffer outputBytes) throws InvalidIdentifierException {
      List<String> foundThreads = new LinkedList<>();
      List<String> foundGroups = new LinkedList<>();
      int threads = outputBytes.getInt();

      for (int i = 0; i < threads; ++i) {
        ThreadId thread = contextProvider.getObjectManager().readThreadId(outputBytes);
        String name = thread.getInfoObject().getName();
        foundThreads.add(name);
      }

      int groups = outputBytes.getInt();

      for (int i = 0; i < groups; ++i) {
        ThreadGroupId group = contextProvider.getObjectManager().readThreadGroupId(outputBytes);
        int groupNameRef = group.get().getReferenceField("name");
        ElementInfo groupName = contextProvider.getVM().getHeap().get(groupNameRef);
        foundGroups.add(groupName.asString());
      }

      assertEquals(DESIRED_COUNT_ALIVETHREADS, threads);
      assertEquals(1, groups);

      assertTrue(foundThreads.contains("thread2"));

      assertFalse(foundThreads.contains("threadNotStarted"));
      assertFalse(foundThreads.contains("threadTerminated"));

      assertTrue(foundGroups.contains("theSubGroup"));
      assertFalse(foundGroups.contains("theSubSubGroup"));

      assertFalse(foundThreads.contains("subThread"));
      assertFalse(foundThreads.contains("subSubThread"));

    }

    @Override
    protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      ObjectId threadGroup = loadObjectId(0);
      threadGroup.write(inputDataOutputStream);
    }
  };

  public static final Object LOCK = new Object();
  public static final int DESIRED_COUNT_ALIVETHREADS = 2;
  public static AtomicInteger threadsRunning = new AtomicInteger(0);

  private class StopRunner implements Runnable {
    private boolean increment;

    StopRunner(boolean increment) {
      this.increment = increment;
    }

    @Override
    public void run() {
      if (increment) {
        threadsRunning.incrementAndGet();
      }
      synchronized (LOCK) {
        System.out.println("lock obtained");
      }

      if (increment) {
        threadsRunning.decrementAndGet();
      }
    }

  }

  /**
   * Test the children command.
   */
  @Test
  public void childrenTest() throws IOException, JdwpException, ClassNotFoundException, InterruptedException {
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener", */)) {

      ThreadGroup group = new ThreadGroup("theGroup");

      Thread thread1 = new Thread(group, new StopRunner(true), "thread1");
      Thread thread2 = new Thread(group, new StopRunner(true), "thread2");

      Thread threadTerminated = new Thread(group, new StopRunner(true), "threadTerminated");
      Thread threadNotStarted = new Thread(group, new StopRunner(true), "threadNotStarted");

      ThreadGroup subGroup = new ThreadGroup(group, "theSubGroup");
      Thread subThread = new Thread(subGroup, new StopRunner(false), "subThread");

      ThreadGroup subSubGroup = new ThreadGroup(subGroup, "theSubSubGroup");
      Thread subSubThread = new Thread(subSubGroup, new StopRunner(false), "subSubThread");

      threadTerminated.start();
      threadTerminated.join();

      // get the lock
      synchronized (LOCK) {
        subThread.start();
        subSubThread.start();
        
        thread1.start();
        thread2.start();

        while (threadsRunning.get() < DESIRED_COUNT_ALIVETHREADS) {
          // yield
          Thread.yield();
        }

        childrenVerifier.verify(group);

      }

      System.out.println(threadNotStarted);
      System.out.println(subSubThread);
    }
  }

}
