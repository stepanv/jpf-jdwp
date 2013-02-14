package gov.nasa.jdi.rmi.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import gov.nasa.jdi.rmi.client.impl.Bootstrap;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.BreakpointRequest;

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
		} catch (NullPointerException e) {
			System.out.println(e.getLocalizedMessage());
			throw e;
		}
	}

	private static class StreamGobbler implements Runnable {

		private BufferedReader br;

		public StreamGobbler(InputStream is) {
			br = new BufferedReader(new InputStreamReader(is));
		}

		@Override
		public void run() {
			String line;
			try {
				while ((line = br.readLine()) != null) {
					System.out.println("Gobblered line: " + line);
				}
			} catch (IOException e) {
				System.err.println("Gobbler ended");
			}

		}

	}

	private void init() throws IOException, IllegalConnectorArgumentsException,
			VMStartException, InterruptedException,
			IncompatibleThreadStateException {
		vmm = Bootstrap.virtualMachineManager();

		LaunchingConnector lc = vmm.defaultConnector();
		Map<String, Argument> args = lc.defaultArguments();
		args.get("main").setValue("test.jdi.debuggee.SimpleIntApp");
		VirtualMachine vm = lc.launch(args);

		outer: while (true) {
			boolean vmStartEventReceived = false;
			for (Event event : vm.eventQueue().remove()) {
				if (event instanceof VMStartEvent) {
					System.out.println("VM started");
					break outer;
				} else {
					System.out.println("received event: " + event);
				}
			}
		}

		
		outer_loop: while (true) {
			for (ReferenceType referenceType : vm.classesByName("test.jdi.debuggee.SimpleIntApp")) {
				vm.suspend();
				System.out.println("found");
				try {
					List<Location> locations = referenceType.locationsOfLine(38);
					Location wantedLocation = locations.get(0);
					BreakpointRequest br = vm.eventRequestManager().createBreakpointRequest(wantedLocation);
					br.enable();
				} catch (AbsentInformationException e) {
					e.printStackTrace();
				}
				
				vm.resume();
				break outer_loop;
			}
			//System.out.println("next iteration");
		}
	}
}
