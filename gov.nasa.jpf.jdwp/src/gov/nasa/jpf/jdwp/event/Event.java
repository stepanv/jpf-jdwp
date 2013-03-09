package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.ThreadId;

public abstract class Event {
	
	private EventKind eventKind;

	public Event(EventKind eventKind) {
		this.eventKind = eventKind;
	}
	
	public static enum EventKind {
		/** Never sent across JDWP */
		VM_DISCONNECTED(100),
		 
		SINGLE_STEP(1),	 
		BREAKPOINT(2),	 
		FRAME_POP(3),	 
		EXCEPTION(4),
		USER_DEFINED(5),	 
		THREAD_START(6),	 
		THREAD_END(7),	 
		CLASS_PREPARE(8),	 
		CLASS_UNLOAD(9),	 
		CLASS_LOAD(10),	 
		FIELD_ACCESS(20),	 
		FIELD_MODIFICATION(21),	 
		EXCEPTION_CATCH(30),	 
		METHOD_ENTRY(40),	 
		METHOD_EXIT(41),	 
		VM_INIT(90),	 
		VM_DEATH(99),
		
		/** JDWP.EventKind.VM_INIT */
		VM_START(VM_INIT),
		/** JDWP.EventKind.THREAD_END */
		THREAD_DEATH(THREAD_END);	
		
		private int value;

		EventKind(int value) {
			this.value = value;
		}
		
		EventKind(EventKind eventKind) {
			this.value = eventKind.value;
		}
	}

	private ThreadId threadId;
	
	public ThreadId getThread() {
		return threadId;
	}

	public EventKind getEventKind() {
		return eventKind;
	}

}
