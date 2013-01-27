package test.jdi.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ThreadGroupReferenceImpl implements ThreadGroupReference {

	public static final Logger log = org.apache.log4j.Logger.getLogger(ThreadReferenceImpl.class);
	
	private VirtualMachineImpl vm;
	private int referenceField;

	public ThreadGroupReferenceImpl(VirtualMachineImpl vm, int referenceField) {
		this.vm = vm;
		this.referenceField = referenceField;
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
		return null;
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
		log.debug("method entering");
		return 0;
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
		log.debug("method entering");
		return vm;
	}

	@Override
	public String name() {
		log.debug("method entering");
		return "name";
	}

	@Override
	public ThreadGroupReference parent() {
		log.debug("method entering");
		return null;
	}

	@Override
	public void resume() {
		log.debug("method entering");

	}

	@Override
	public void suspend() {
		log.debug("method entering");

	}

	@Override
	public List<ThreadGroupReference> threadGroups() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<ThreadReference> threads() {
		log.debug("method entering");
		return null;
	}

}
