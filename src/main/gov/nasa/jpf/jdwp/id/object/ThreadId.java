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

package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.State;

/**
 * This interface represents the corresponding <code>threadID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * It's important to understand that even if a thread is represented by
 * {@link ThreadInfo} in the JPF, the user (the debugger respectively) inspects
 * an instance of {@link Thread} and therefore any thread during the debug
 * operations is considered as an instance of {@link Thread} on the first place.
 * </p>
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a thread.
 * </p>
 * 
 * @author stepan
 * 
 */
public interface ThreadId extends ObjectId {

  /**
   * <p>
   * Constants in {@link ThreadStatus} are derived from JVMTI (Java VM Spec,
   * respectively). Whereas JVMTI introduces more constants only these are
   * interested (or supported) by the JDWP specification.<br/>
   * Moreover, the specification isn't even precise how the JVMTI thread states
   * (JNI thread states) should be mapped to JDWP thread states. Therefore it's
   * even more unclear how to map JPF thread states ( {@link State} to
   * {@link ThreadStatus}.
   * </p>
   * <p>
   * JPF thread states mapping is implemented in accordance to Harmony and
   * OpenJDK implementations.
   * </p>
   * <p>
   * These thread states aren't related to the suspension status set from the
   * debugger. The suspension status is reflected by {@link SuspendStatus}.
   * </p>
   * 
   * @author stepan
   * 
   */
  public static enum ThreadStatus implements ConvertibleEnum<Integer, ThreadStatus> {

    /** Thread is terminated */
    ZOMBIE(0),

    /**
     * Thread is running (doesn't mean it's instructions are executed - the
     * thread can be waiting to be scheduled.)<br/>
     */
    RUNNING(1),

    /** Thread is in {@link Thread#sleep(long)} and other alternate methods. */
    SLEEPING(2),

    /** Thread is blocked waiting for a monitor. */
    MONITOR(3),

    /** Thread is waiting for other reasons ... */
    WAIT(4);

    private int id;

    private ThreadStatus(int id) {
      this.id = id;
    }

    @Override
    public Integer identifier() {
      return id;
    }

    private static ReverseEnumMap<Integer, ThreadStatus> map = new ReverseEnumMap<Integer, ThreadId.ThreadStatus>(ThreadStatus.class);

    @Override
    public ThreadStatus convert(Integer val) throws IllegalArgumentException {
      return map.get(val);
    }

    public static ThreadStatus read(int val) throws JdwpException {
      return ZOMBIE.convert(val);
    }
  }

  public enum SuspendStatus implements IdentifiableEnum<Integer> {
    SUSPEND_STATUS_SUSPENDED(1);

    private int suspendStatusId;

    private SuspendStatus(int suspendStatusId) {
      this.suspendStatusId = suspendStatusId;
    }

    @Override
    public Integer identifier() {
      return suspendStatusId;
    }
  }

  public ThreadInfo getThreadInfo() throws InvalidThreadException;

}
