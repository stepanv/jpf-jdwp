package test.jdi.impl;

import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.MonitorInfo;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ThreadReferenceImpl implements ThreadReference {

	public static final Logger log = org.apache.log4j.Logger.getLogger(ThreadReferenceImpl.class);
	private VirtualMachineImpl vm;
	private ThreadInfo ti;
	private ThreadGroupReference threadGroupReference;
	private ReferenceTypeImpl referenceType;
	
	public ThreadReferenceImpl(VirtualMachineImpl vm, ThreadInfo ti) {
		this.vm = vm;
		this.ti = ti;
		
		ElementInfo ei = ti.getElementInfo(ti.getThreadObjectRef());
	    this.threadGroupReference = new ThreadGroupReferenceImpl(vm, ei.getReferenceField("group"));
	    this.referenceType = new ReferenceTypeImpl(ti.getClassInfo(), vm);
	}

	@Override
	public void disableCollection() {
		log.debug("method entering");

	}

	@Override
	public void enableCollection() {
		log.debug("method entering");

	}

	@Override
	public int entryCount() throws IncompatibleThreadStateException {
		log.debug("method entering");
		return 0;
	}

	@Override
	public Value getValue(Field sig) {
		log.debug("method entering");
		return null;
	}

	@Override
	public Map<Field, Value> getValues(List<? extends Field> fields) {
		log.debug("method entering");
		return null;
	}

	@Override
	public Value invokeMethod(ThreadReference thread, Method method,
			List<? extends Value> arguments, int options)
			throws InvalidTypeException, ClassNotLoadedException,
			IncompatibleThreadStateException, InvocationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public boolean isCollected() {
		log.debug("method entering");
		return false;
	}

	@Override
	public ThreadReference owningThread()
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		return null;
	}

	@Override
	public ReferenceType referenceType() {
		log.debug("method entering");
		return referenceType;
	}

	@Override
	public List<ObjectReference> referringObjects(long maxReferrers) {
		log.debug("method entering");
		return null;
	}

	@Override
	public void setValue(Field field, Value value) throws InvalidTypeException,
			ClassNotLoadedException {
		log.debug("method entering");

	}

	@Override
	public long uniqueID() {
		return ti.getId();
	}

	@Override
	public List<ThreadReference> waitingThreads()
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		return null;
	}

	@Override
	public Type type() {
		log.debug("method entering");
		return null;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public ObjectReference currentContendedMonitor()
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		return null;
	}

	@Override
	public void forceEarlyReturn(Value value) throws InvalidTypeException,
			ClassNotLoadedException, IncompatibleThreadStateException {
		log.debug("method entering");

	}

	@Override
	public StackFrame frame(int index) throws IncompatibleThreadStateException {
		log.debug("method entering");
		return null;
	}

	@Override
	public int frameCount() throws IncompatibleThreadStateException {
		log.debug("method entering");
		return 0;
	}

	@Override
	public List<StackFrame> frames() throws IncompatibleThreadStateException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<StackFrame> frames(int start, int length)
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		
		List<gov.nasa.jpf.jvm.StackFrame> stack = ti.getStack();
		
		return null;
	}

	@Override
	public void interrupt() {
		log.debug("method entering");

	}

	@Override
	public boolean isAtBreakpoint() {
		log.debug("method entering");
		return vm.getThreadManager().getAdditionalInfo(ti).isAtBreakpoint();
	}

	@Override
	public boolean isSuspended() {
		return vm.getJPFManager().isAllThreadsSuspended();
	}

	@Override
	public String name() {
		return ti.getName();
	}

	@Override
	public List<ObjectReference> ownedMonitors()
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		return new ArrayList<ObjectReference>();
	}

	@Override
	public List<MonitorInfo> ownedMonitorsAndFrames()
			throws IncompatibleThreadStateException {
		log.debug("method entering");
		return new ArrayList<MonitorInfo>(); // TODO implement this
	}

	@Override
	public void popFrames(StackFrame frame)
			throws IncompatibleThreadStateException {
		log.debug("method entering");

	}

	@Override
	public void resume() {
		log.debug("method entering");

	}

	@Override
	public int status() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public void stop(ObjectReference throwable) throws InvalidTypeException {
		log.debug("method entering");

	}

	@Override
	public void suspend() {
		log.debug("method entering");

	}

	@Override
	public int suspendCount() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public ThreadGroupReference threadGroup() {
		return threadGroupReference;
	}

}
