package gov.nasa.jpf.jdwp.util;

import gov.nasa.jpf.jdwp.VirtualMachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Anytime it's required to not run an action in parallel this is a way how
 * to easily ensure nobody else enters the critical section. <br/>
 * This lock is not recursive since and thus any recursion will be
 * considered as a programmer's fault.<br/>
 * 
 * @author stepan
 * 
 */
public class SafeLock {
	
	static final Logger logger = LoggerFactory.getLogger(SafeLock.class);

	private Object lock = new Object();
	private Thread threadOwner = null;

	/**
	 * Obtain the run lock.<br/>
	 * 
	 */
	public void lock() {
		synchronized (lock) {
			while (threadOwner != null) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			threadOwner = Thread.currentThread();
		}
		logger.trace("RUN LOCK obtained");
	}

	/**
	 * Unlocks the lock.<br/>
	 * If unlocking not owned lock, {@link IllegalStateException} is thrown.
	 * 
	 * @see VirtualMachine#lock()
	 */
	public void unlock() {
		synchronized (lock) {
			if (threadOwner != Thread.currentThread()) {
				throw new IllegalStateException("Trying to unlock not owned lock. Last owner: " + threadOwner);
			}
			threadOwner = null;
			lock.notify();
		}
		logger.trace("RUN LOCK released");
	}

}
