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
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Thread reference command test.
 * 
 * @author stepan
 * 
 */
public class ThreadReferenceCommandTest extends TestJdwp {

  public ThreadReferenceCommandTest() {
  }

  private static final int MAX_ITERATIONS = 1000;

  CommandVerifier stopVerifier = new CommandVerifier(ThreadReferenceCommand.STOP) {

    @Override
    protected void processOutput(ByteBuffer outputBytes) throws InvalidIdentifierException {

    }

    @Override
    protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      ObjectId thread = loadObjectId(0);
      thread.write(inputDataOutputStream);
      ObjectId exception = loadObjectId(1);
      exception.write(inputDataOutputStream);
    }
  };

  public static final Object LOCK = new Object();
  public static final int DESIRED_COUNT_ALIVETHREADS = 1;
  public static AtomicInteger threadsRunning = new AtomicInteger(0);
  public static boolean exceptionReceived = false;
  public static AtomicBoolean stopped = new AtomicBoolean(false);

  private class StopRunner implements Runnable {

    @Override
    public void run() {
      threadsRunning.incrementAndGet();
      try {
        synchronized (this) {
          while (!stopped.get())
            try {
              wait();
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
        }
      } catch (RuntimeException e) {
        System.out.println("Exception received: " + e);
        exceptionReceived = true;
      } finally {
        threadsRunning.decrementAndGet();
        System.out.println("thread stop");
      }
    }

  }

  /**
   * Test the children command.
   */
  @Test
  public void stopTest() throws IOException, JdwpException, ClassNotFoundException, InterruptedException {
    if (verifyNoPropertyViolation("+search.class=.search.RandomSearch", "+cg.randomize_choices=FIXED_SEED"
    /*
     * , "+listener=.jdwp.JDWPListener"
     */
    )) {

      Thread thread1 = new Thread(new StopRunner(), "thread1");

      // get the lock
      synchronized (LOCK) {
        assertFalse(exceptionReceived);

        thread1.start();

        while (threadsRunning.get() < DESIRED_COUNT_ALIVETHREADS) {
          // yield
          Thread.yield();
        }

        stopVerifier.verify(thread1, new RuntimeException("end test"));
        stopped.set(true);

        int i = 0;
        while (threadsRunning.get() >= DESIRED_COUNT_ALIVETHREADS) {
          // yield
          Thread.yield();
          if (++i > MAX_ITERATIONS) {
            System.err.println("The thread1 didn't exit even after " + MAX_ITERATIONS + " yields.");
            assertTrue("The thread1 didn't exit even after " + MAX_ITERATIONS + " yields.", false);
          }
        }

        // yield for once more so that the thread can really exit
        Thread.yield();

        assertTrue(exceptionReceived);
        thread1.join();
        assertFalse(thread1.isAlive());

      }

    }
  }

  CommandVerifier interruptVerifier = new CommandVerifier(ThreadReferenceCommand.INTERRUPT) {

    @Override
    protected void processOutput(ByteBuffer outputBytes) throws InvalidIdentifierException {

    }

    @Override
    protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      ObjectId thread = loadObjectId(0);
      thread.write(inputDataOutputStream);
    }
  };

  private class InterruptRunner implements Runnable {

    @Override
    public void run() {
      threadsRunning.incrementAndGet();
      try {
        synchronized (this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        System.out.println("Exception received: " + e);
        exceptionReceived = true;
      } finally {
        threadsRunning.decrementAndGet();
      }
    }

  }

  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
  }

  /**
   * Test the children command.
   */
  @Test
  public void interruptTest() throws IOException, JdwpException, ClassNotFoundException, InterruptedException {
    if (verifyNoPropertyViolation("-show", "+search.class=.search.RandomSearch", "+cg.randomize_choices=FIXED_SEED"
    /* "+search.properties=gov.nasa.jpf.vm.NotDeadlockedProperty" */
    /*
     * , "+listener=.jdwp.JDWPListener"
     */
    )) {

      Thread thread1 = new Thread(new InterruptRunner(), "thread1");

      // get the lock
      assertFalse(exceptionReceived);

      thread1.start();

      while (threadsRunning.get() < DESIRED_COUNT_ALIVETHREADS) {
        // yield
        Thread.yield();
      }

      interruptVerifier.verify(thread1);

      int i = 0;
      while (threadsRunning.get() >= DESIRED_COUNT_ALIVETHREADS) {
        // yield
        Thread.yield();

        if (++i > MAX_ITERATIONS) {
          if (++i > MAX_ITERATIONS) {
            System.err.println("The thread1 was not interrupted even after " + MAX_ITERATIONS + " yields.");
            fail("The thread1 was not interrupted even after " + MAX_ITERATIONS + " yields.");
            assertTrue("The thread1 was not interrupted even after " + MAX_ITERATIONS + " yields.", false);
          }
        }
      }

      // yield for once more so that the thread can really exit
      Thread.yield();

      assertTrue(exceptionReceived);
      assertFalse(thread1.isAlive());

    }
  }

}
