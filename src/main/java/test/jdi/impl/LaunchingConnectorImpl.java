package test.jdi.impl;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.VMStartException;

public class LaunchingConnectorImpl implements LaunchingConnector {

	public LaunchingConnectorImpl() {
		arguments.put("main", new ArgumentImpl("main"));
		arguments.put("options", new ArgumentImpl("options"));
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transport transport() {
		// TODO Auto-generated method stub
		return null;
	}

	Map<String, Argument> arguments = new HashMap<String, Connector.Argument>();

	@Override
	public Map<String, Argument> defaultArguments() {
		return arguments;
	}

	@Override
	public VirtualMachine launch(Map<String, ? extends Argument> paramMap)
			throws IOException, IllegalConnectorArgumentsException,
			VMStartException {

		List<String> args = new ArrayList<String>();

		args.add("+target=" + paramMap.get("main").value());
		args.add("+classpath=+," + System.getProperty("java.class.path"));

		JPF jpf = null;

		try {
			// this initializes the JPF configuration from default.properties,
			// site.properties
			// configured extensions (jpf.properties), current directory
			// (jpf.properies) and
			// command line args ("+<key>=<value>" options and *.jpf)
			Config conf = JPF
					.createConfig(args.toArray(new String[args.size()]));

			// ... modify config according to your needs
//			conf.setProperty("my.property", "whatever");

			// ... explicitly create listeners (could be reused over multiple
			// JPF runs)
			// MyListener myListener = ...

			jpf = new JPF(conf);

			// ... set your listeners
		// jpf.addListener(myListener);
			jpf.addListener(new MyListener());

		} catch (JPFConfigException cx) {
			throw new IllegalConnectorArgumentsException(cx.getMessage(), cx
					.getStackTrace().toString());
		} catch (JPFException jx) {
			throw new VMStartException(jx.getMessage(), null);
		}

		
		
		VirtualMachineImpl vm = new VirtualMachineImpl(jpf);

		return vm;
	}
	
	private static class MyListener implements VMListener {

		@Override
		public void executeInstruction(JVM vm) {
//			Instruction nextInstruction = vm.getNextInstruction();
//			int nextLineNumber = nextInstruction.getMethodInfo().getLineNumber(nextInstruction);
//			
//			if (vm.getLastStep()!= null && vm.getNextInstruction() !=  vm.getLastStep().getInstruction()) {
//				System.out.println("not equal");
//			}
//			
//			if (nextInstruction.getFileLocation().contains("SimpleIntApp")) {
//				System.out.print(nextInstruction.getFileLocation() + "," + nextInstruction.getPosition() + " ");
//			}
			
			// TODO Auto-generated method stub
			
		}

		@Override
		public void instructionExecuted(JVM vm) {
			StringBuffer sb = new StringBuffer(100);

		    assert vm != null;
		    Instruction instr = vm.getLastInstruction();
		    if (instr == null) {
		      return;
		    }

		    sb.append("SuT ");
		    if (vm.getCurrentThread() != null) {
		      sb.append(" (Thread=" + vm.getCurrentThread().getId() + ") ");
		    }

		    sb.append("executes the " + instr.getMethodInfo().getSourceFileName() + ":" + instr.getLineNumber() + " - " + instr.toString() + " source: "
		        + instr.getSourceLine());
		    System.out.println(sb);
			
		}

		@Override
		public void threadStarted(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadBlocked(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadWaiting(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadNotified(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadInterrupted(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadTerminated(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void threadScheduled(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void classLoaded(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectCreated(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectReleased(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectLocked(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectUnlocked(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectWait(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectNotify(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void objectNotifyAll(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void gcBegin(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void gcEnd(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exceptionThrown(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exceptionBailout(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exceptionHandled(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void choiceGeneratorRegistered(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void choiceGeneratorSet(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void choiceGeneratorAdvanced(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void choiceGeneratorProcessed(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void methodEntered(JVM vm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void methodExited(JVM vm) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
