package test.jdi.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMStartEvent;

public class SimpleDebuggerApp {

	private VirtualMachineManager vmm;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleDebuggerApp program = new SimpleDebuggerApp();

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

		vmm.defaultConnector();

		LaunchingConnector lc = vmm.defaultConnector();
		Map<String, Argument> args = lc.defaultArguments();
		args.get("main").setValue("test.jdi.debuggee.SimpleIntApp");
		args.get("options")
				.setValue(
						"-classpath C:\\Users\\stepan\\Data\\workspaces\\mthesis\\jdi-test\\bin");
		VirtualMachine vm = lc.launch(args);

		while (true) {
			boolean vmStartEventReceived = false;
			for (Event event : vm.eventQueue().remove()) {
				if (event instanceof VMStartEvent) {
					System.out.println("VM started");
					vmStartEventReceived = true;
					break;
				} else {
					System.out.println("received event: " + event);
				}
			}
			if (vmStartEventReceived) {
				break;
			}
		}

		Thread processStdoutGobbler = new Thread(new StreamGobbler(vm.process()
				.getInputStream()));
		processStdoutGobbler.start();
		Thread processErrorGobbler = new Thread(new StreamGobbler(vm.process()
				.getErrorStream()));
		processErrorGobbler.start();

		vm.resume();

		for (ThreadReference threadReference : vm.allThreads()) {
			threadReference.suspend();
			System.out.println(threadReference.name());
			for (StackFrame frame : threadReference.frames()) {
				System.out.println(frame.toString());
			}
			threadReference.resume();
		}

		Field field;
		Value value = null;
		for (ReferenceType referenceType : vm.allClasses()) {
			String name = referenceType.name();
			if (name.contains("SimpleIntApp")) {
				field = referenceType.fieldByName("number");
				
				value = referenceType.getValue(field);
			}
		}

		if (value != null) {
		System.out.println("Found value: " + value.toString());
		} else {
			System.out.println("NOTHING FOUND...");
		}

	}

}
