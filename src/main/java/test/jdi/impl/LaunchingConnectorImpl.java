package test.jdi.impl;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

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

//s/ /([a-zA-Z0-9]*/)/(([^)][^)]*) {/)/ /1/2/r/t/t/tlog.debug("Entering method '/1'");/

public class LaunchingConnectorImpl implements LaunchingConnector {
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(LaunchingConnectorImpl.class);

	public LaunchingConnectorImpl() {
		arguments.put("main", new ArgumentImpl("main"));
		arguments.put("options", new ArgumentImpl("options"));
		arguments.put("classpath", new ArgumentImpl("classpath"));
		arguments.put("workingdir", new ArgumentImpl("workingdir"));
		
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
		args.add("+classpath=+," + paramMap.get("classpath").value() +";" + System.getProperty("java.class.path"));
		
	      // this initializes the JPF configuration from default.properties, site.properties
	      // configured extensions (jpf.properties), current directory (jpf.properies) and
	      // command line args ("+<key>=<value>" options and *.jpf)
	      Config conf = JPF.createConfig(args.toArray(new String[0]));

	      // ... modify config according to your needs
	      //conf.setProperty("my.property", "whatever");

	      JPF jpf = new JPF(conf);

		VirtualMachineImpl vm;
			vm = new VirtualMachineImpl(jpf);
			
		
		vm.start();

		return vm;
	}
	

}
