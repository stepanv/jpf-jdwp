package test.jdi.impl.internal;

import gov.nasa.jpf.jvm.ThreadInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import test.jdi.impl.VirtualMachineImpl;

public class ThreadManager {
	
	private Map<ThreadInfo, AdditionalThreadInfo> threads = new ConcurrentHashMap<ThreadInfo, AdditionalThreadInfo>();
	private VirtualMachineImpl vm;
	
	public ThreadManager(VirtualMachineImpl virtualMachineImpl) {
		this.vm = virtualMachineImpl;
	}

	public void addInfo(ThreadInfo ti, AdditionalThreadInfo ati) {
		threads.put(ti, ati);
	}
	
	public void setIsAtBreakpoint(ThreadInfo ti) {
		AdditionalThreadInfo ati = additionalInfo(ti);
		ati.setAtBreakpoint(true);
	}	
	
	private AdditionalThreadInfo additionalInfo(ThreadInfo ti) {
		synchronized (threads) {
			if (!threads.containsKey(ti)) {
				AdditionalThreadInfo ati = new AdditionalThreadInfo();
				threads.put(ti, ati);
				return ati;
			}
		}
		return threads.get(ti);
	}
	
	public AdditionalThreadInfo getAdditionalInfo(ThreadInfo ti) {
		if (threads.containsKey(ti)) {
			return threads.get(ti);
		}
		threads.put(ti, new AdditionalThreadInfo());
		return threads.get(ti);
	}

	public static class AdditionalThreadInfo {
		private boolean atBreakpoint = false;

		public boolean isAtBreakpoint() {
			return atBreakpoint;
		}

		public void setAtBreakpoint(boolean atBreakpoint) {
			this.atBreakpoint = atBreakpoint;
		}
	}
}
