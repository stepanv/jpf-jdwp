package test.jdi.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;

public class SimpleDebuggerApp {

	private static final int port = 52313;
	private static final String cmd = "C:\\Programs\\Java\\jdk1.6.0_22\\bin\\javaw.exe"
			+ " -agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:"
			+ port
			+ " -Dfile.encoding=Cp1252 -classpath"
			+ " C:\\Users\\stepan\\Data\\workspaces\\mthesis\\jdi-test\\bin;C:\\Programs\\Java\\jdk1.6.0_22\\lib\\tools.jar"
			+ " test.jdi.debuggee.SimpleIntApp";
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

	private void init() throws IOException {
		vmm = Bootstrap.virtualMachineManager();

		vmm.defaultConnector();

		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process process = pb.start();

		Thread processStdoutGobbler = new Thread(new StreamGobbler(
				process.getInputStream()));
		processStdoutGobbler.start();

		// TODO change null to a SocketConnection
		VirtualMachine vm = vmm.createVirtualMachine(null, process);

		for (ThreadReference threadReference : vm.allThreads()) {
			// threadReference.
		}

	}

}
