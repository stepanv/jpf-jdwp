package gov.nasa.jpf.jdwp.proxy;

import gov.nasa.jpf.jvm.ThreadInfo;

public class ThreadProxy extends Thread {
	
	private ThreadInfo threadInfo;

	public ThreadProxy(ThreadInfo threadInfo) {
		this.threadInfo = threadInfo;
	}

}
