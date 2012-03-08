package gov.nasa.jdi.rmi.common.impl.local;

import java.io.Serializable;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;

public class EventLocal implements Event, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6485021879809753425L;

	
	
	public EventLocal(Event event) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventRequest request() {
		// TODO Auto-generated method stub
		return null;
	}

}
