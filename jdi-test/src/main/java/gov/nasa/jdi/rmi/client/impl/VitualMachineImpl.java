package gov.nasa.jdi.rmi.client.impl;

import gov.nasa.jdi.rmi.common.EventQueueRemote;
import gov.nasa.jdi.rmi.common.ReferenceTypeRemote;
import gov.nasa.jdi.rmi.common.VirtualMachineRemote;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;

public class VitualMachineImpl implements VirtualMachine {

	VirtualMachineRemote vmRemote;
	public VitualMachineImpl(VirtualMachineRemote vmRemote) {
		this.vmRemote = vmRemote;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReferenceType> classesByName(String paramString) {
		try {
			List<ReferenceType> rtList = new ArrayList<ReferenceType>();
			for (ReferenceTypeRemote rtr : vmRemote.classesByName(paramString)) {
				rtList.add(new ReferenceTypeImpl(rtr));
			}
			return rtList;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		
		
	}

	@Override
	public List<ReferenceType> allClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redefineClasses(Map<? extends ReferenceType, byte[]> paramMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ThreadReference> allThreads() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suspend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		try {
			vmRemote.resume();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ThreadGroupReference> topLevelThreadGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventQueue eventQueue() {
		try {
			return new EventQueueImpl(vmRemote.eventQueue());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public EventRequestManager eventRequestManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanValue mirrorOf(boolean paramBoolean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteValue mirrorOf(byte paramByte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharValue mirrorOf(char paramChar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShortValue mirrorOf(short paramShort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerValue mirrorOf(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongValue mirrorOf(long paramLong) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FloatValue mirrorOf(float paramFloat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleValue mirrorOf(double paramDouble) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringReference mirrorOf(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VoidValue mirrorOfVoid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Process process() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(int paramInt) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canWatchFieldModification() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWatchFieldAccess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetBytecodes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetSyntheticAttribute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetOwnedMonitorInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetCurrentContendedMonitor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMonitorInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUseInstanceFilters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRedefineClasses() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUnrestrictedlyRedefineClasses() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPopFrames() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetSourceDebugExtension() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRequestVMDeathEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMethodReturnValues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetInstanceInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUseSourceNameFilters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canForceEarlyReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRequestMonitorEvents() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetMonitorFrameInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetClassFileVersion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canGetConstantPool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDefaultStratum(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultStratum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] instanceCounts(List<? extends ReferenceType> paramList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDebugTraceMode(int paramInt) {
		// TODO Auto-generated method stub

	}

}
