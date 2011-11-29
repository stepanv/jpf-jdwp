package test.jdi.impl;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

		} catch (JPFConfigException cx) {
			throw new IllegalConnectorArgumentsException(cx.getMessage(), cx
					.getStackTrace().toString());
		} catch (JPFException jx) {
			throw new VMStartException(jx.getMessage(), null);
		}

		VirtualMachineImpl vm = new VirtualMachineImpl(jpf);

		return vm;
	}

}
