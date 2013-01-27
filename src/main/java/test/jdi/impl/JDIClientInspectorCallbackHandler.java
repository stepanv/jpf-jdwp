package test.jdi.impl;

import org.apache.log4j.Logger;

import com.sun.jdi.request.BreakpointRequest;

import gov.nasa.jpf.inspector.client.JPFClientCallbackHandler;
import gov.nasa.jpf.inspector.interfaces.BreakPointStatus;
import gov.nasa.jpf.inspector.interfaces.InspectorCallBacks;
import gov.nasa.jpf.inspector.interfaces.ChoiceGeneratorsInterface.CGTypes;
import gov.nasa.jpf.inspector.interfaces.CommandsInterface.InspectorStates;

public class JDIClientInspectorCallbackHandler implements InspectorCallBacks {
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(JDIClientInspectorCallbackHandler.class);

	private VirtualMachineImpl vmImpl;
	private JPFClientCallbackHandler decoratedCallback;

	public JDIClientInspectorCallbackHandler(
			VirtualMachineImpl virtualMachineImpl) {
		vmImpl = virtualMachineImpl;
		decoratedCallback = new JPFClientCallbackHandler(System.err);
	}

	@Override
	public void notifyStateChange(InspectorStates newState, String details) {
		decoratedCallback.notifyStateChange(newState, details);

	}

	@Override
	public void genericError(String msg) {
		decoratedCallback.genericError(msg);

	}

	@Override
	public void genericInfo(String msg) {
		decoratedCallback.genericInfo(msg);
	}

	@Override
	public void notifyBreakpointHit(BreakPointStatus bp) {
		log.debug("Creating event breakpoint HIT");
		
		vmImpl.eventRequestManager.pairAndAddBreakpointEvent(bp);
		
		log.debug("Creating event breakpoint HIT .. done");
		decoratedCallback.notifyBreakpointHit(bp);

	}

	@Override
	public void notifyChoiceGeneratorNewChoice(CGTypes cgType, String cgName,
			int cgId, String[] choices, int nextChoice, int defaultChoice) {
		// TODO Auto-generated method stub

	}

	@Override
	public void specifyChoiceToUse(int maxChoiceIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUsedChoice(CGTypes cgType, String cgName, int cgId,
			int usedChoiceIndex, String usedChoice) {
		// TODO Auto-generated method stub

	}

}
