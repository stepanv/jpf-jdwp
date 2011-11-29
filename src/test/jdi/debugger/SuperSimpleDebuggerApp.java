package test.jdi.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import test.jdi.impl.Bootstrap;

import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;

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

//		for (ThreadReference threadReference : vm.allThreads()) {
//			if (threadReference.name().contains("main")) {
//				threadReference.suspend();
//				System.out.println(threadReference.name());
//				for (StackFrame frame : threadReference.frames()) {
//					System.out.println(frame.toString());
//				}
//				threadReference.resume();
//			}
//		}

		Field field;
		for (ReferenceType referenceType : vm.allClasses()) {
			String name = referenceType.name();
			if (name.contains("SimpleIntApp")) {
				field = referenceType.fieldByName("number");

				System.out.println("Found value: "
						+ referenceType.getValue(field));
			}
		}

	}
}
