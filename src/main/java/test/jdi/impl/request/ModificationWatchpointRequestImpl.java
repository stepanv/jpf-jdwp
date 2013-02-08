package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.ModificationWatchpointRequest;

public class ModificationWatchpointRequestImpl extends EventRequestImpl implements
		ModificationWatchpointRequest {

	public ModificationWatchpointRequestImpl(VirtualMachineImpl vm, EventRequestContainer<ModificationWatchpointRequest> modificationWatchpointRequestContainer) {
		super(vm, modificationWatchpointRequestContainer);
	}

	@Override
	public void addClassExclusionFilter(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(ReferenceType refType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(String classPattern) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstanceFilter(ObjectReference instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public Field field() {
		// TODO Auto-generated method stub
		return null;
	}

}
