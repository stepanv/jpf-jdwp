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

import com.sun.jdi.Location;
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
		args.add("+classpath=+,c:/Users/stepan/Data/workspaces/runtime-EclipseApplication/debug-test/bin;C:/Users/stepan/Data/mff/mthesis/sources/jpf-inspector/build/main;C:/Users/stepan/Data/workspaces/mthesis/jdi-test/target/classes;C:/Users/stepan/Data/workspaces/mthesis/jdi-test-deps;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.apache.log4j_1.2.15.v201012070815.jar;C:/Users/stepan/Data/Devel/eclipse.jdt.debug/org.eclipse.jdt.debug/jdi-bin;C:/Users/stepan/Data/Devel/eclipse.jdt.debug/org.eclipse.jdt.debug/bin;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.resources_3.8.0.v20120522-2034.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.debug.core_3.7.100.v20120521-2012.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.jdt.compiler.apt_1.0.500.v20120522-1651.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.jdt.compiler.tool_1.0.101.v20120522-1651.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.osgi_3.8.0.v20120529-1548.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.runtime.compatibility.registry_3.5.100.v20120521-2346/runtime_registry_compatibility.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.equinox.registry_3.5.200.v20120522-1841.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.equinox.app_1.3.100.v20120522-1841.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/org.eclipse.core.expressions_3.4.400.v20120523-2004.jar;C:/Programs/Eclipse/Eclipse-Juno-x64/plugins/com.ibm.icu_4.4.2.v20110823.jar;C:/Users/stepan/Data/mff/mthesis/sources/jpf-inspector/build/jpf-inspector.jar;C:/Users/stepan/.m2/repository/org/apache/felix/org.osgi.core/1.0.0/org.osgi.core-1.0.0.jar;C:/Users/stepan/.m2/repository/org/antlr/antlr-runtime/3.4/antlr-runtime-3.4.jar;C:/Users/stepan/.m2/repository/org/antlr/stringtemplate/3.2.1/stringtemplate-3.2.1.jar;C:/Users/stepan/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar;" + System.getProperty("java.class.path"));
		//c:/Users/stepan/Data/workspaces/runtime-EclipseApplication/debug-test/bin;
		
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
