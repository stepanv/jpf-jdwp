package test.jdi.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import test.jdi.impl.Bootstrap;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMStartEvent;

public class SuperSimpleDebuggerApp {

	private VirtualMachineManager vmm;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SuperSimpleDebuggerApp program = new SuperSimpleDebuggerApp();

		try {
			program.init();
		} catch (IOException e) {
			throw new RuntimeException("Program ended", e);
		} catch (IllegalConnectorArgumentsException e) {
			throw new RuntimeException("Program ended", e);
		} catch (VMStartException e) {
			throw new RuntimeException("Program ended", e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Program ended", e);
		} catch (IncompatibleThreadStateException e) {
			throw new RuntimeException("Program ended", e);
		}
	}

	private void init() throws IOException, IllegalConnectorArgumentsException,
			VMStartException, InterruptedException,
			IncompatibleThreadStateException {
		vmm = Bootstrap.virtualMachineManager();

		LaunchingConnector lc = vmm.defaultConnector();
		Map<String, Argument> args = lc.defaultArguments();
		args.get("main").setValue("oldclassic");
		VirtualMachine vm = lc.launch(args);
		
		EventSet nonprocessedEventSet = vm.eventQueue().remove();

		outer: while (true) {
			Iterator<Event> iterator = nonprocessedEventSet.iterator();
			while (iterator.hasNext()) {
				Event event = iterator.next();
				if (event instanceof VMStartEvent) {
					System.out.println("received VM started");
					iterator.remove();
					break outer;
				} else {
					System.out.println("received event: " + event);
				}
				
			}
			EventSet eventSet = vm.eventQueue().remove();
			for (Event event : eventSet) {
				nonprocessedEventSet.add(event);
			}
			System.out.println("Walked through: " + eventSet);
		}
		
		System.out.println("READY TO DEBUG");

		outer: while (true) {
			Iterator<Event> iterator = nonprocessedEventSet.iterator();
			while (iterator.hasNext()) {
				Event event = iterator.next();
				if (event instanceof BreakpointEvent) {
					System.out.println("Breakpoint reached");
					
					((VirtualMachineImpl)vm).debugTmp();
					vm.resume();
					break outer;
				} else {
					System.out.println("received event: " + event + " .. removing..");
					iterator.remove();
				}
			}
			EventSet eventSet = vm.eventQueue().remove();
			for (Event event : eventSet) {
				nonprocessedEventSet.add(event);
			}
			System.out.println("Walked through: " + eventSet);
		}

	}
}
