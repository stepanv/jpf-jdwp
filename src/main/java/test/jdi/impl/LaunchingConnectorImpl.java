package test.jdi.impl;

import gov.nasa.jdi.rmi.server.InvocationException;
import gov.nasa.jdi.rmi.server.JPFInspectorLauncher;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.inspector.JPFInspectorFacade;
import gov.nasa.jpf.inspector.client.JPFInspectorClientInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorBackEndInterface;
import gov.nasa.jpf.inspector.interfaces.exceptions.JPFInspectorGenericErrorException;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.VMStartException;

//s/ \([a-zA-Z0-9]*\)\(([^)][^)]*) {\)/ \1\2\r\t\t\tlog.debug("Entering method '\1'");/

public class LaunchingConnectorImpl implements LaunchingConnector {
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(LaunchingConnectorImpl.class);

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
		
		
		JPFInspectorLauncher inspectorLauncher = new JPFInspectorLauncher(args);
		

		
		VirtualMachineImpl vm;
		try {
			vm = new VirtualMachineImpl(inspectorLauncher);
		} catch (InvocationException e) {
			throw new VMStartException("Cannot start VM: " + e.getMessage(), null);
		}
		
		vm.start();

		return vm;
	}
	

}
