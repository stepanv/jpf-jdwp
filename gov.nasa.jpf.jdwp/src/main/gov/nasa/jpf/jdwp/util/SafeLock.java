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
