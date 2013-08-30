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

package gov.nasa.jpf.jdwp.util;

import gov.nasa.jpf.jdwp.VirtualMachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Anytime it's required to not run an action in parallel this is a way how to
 * easily ensure nobody else enters the critical section. <br/>
 * This lock is not recursive since and thus any recursion will be considered as
 * a programmer's fault.<br/>
 * 
 * @author stepan
 * 
 */
public class SafeLock {

  public SafeLock(String name) {
    this.name = name;
  }

  private String name;

  static final Logger logger = LoggerFactory.getLogger(SafeLock.class);

  private Thread threadOwner = null;

  /**
   * Obtain the run lock.<br/>
   * 
   */
  public synchronized void lock() {
    while (threadOwner != null) {
      try {
        this.wait();
      } catch (InterruptedException e) {
      }
    }
    threadOwner = Thread.currentThread();
    logger.trace("[{}] RUN LOCK obtained", name);
  }

  /**
   * Unlocks the lock.<br/>
   * If unlocking not owned lock, {@link IllegalStateException} is thrown.
   * 
   * @see VirtualMachine#lockRunLock()
   */
  public synchronized void unlock() {
    if (threadOwner != Thread.currentThread()) {
      throw new IllegalStateException("Trying to unlock not owned lock. Last owner: " + threadOwner);
    }
    logger.trace("[{}] unlocking RUN LOCK", name);
    threadOwner = null;
    this.notify();
  }

  public synchronized void unlockIfOwned() {
    if (threadOwner == Thread.currentThread()) {
      threadOwner = null;
      this.notify();
    }
  }

}
