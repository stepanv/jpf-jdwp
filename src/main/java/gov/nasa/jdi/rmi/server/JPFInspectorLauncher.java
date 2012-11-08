package gov.nasa.jdi.rmi.server;

import java.util.ArrayList;
import java.util.List;

import test.jdi.impl.JDIClientInspectorCallbackHandler;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.inspector.JPFInspectorFacade;
import gov.nasa.jpf.inspector.client.ClientCommandInterface;
import gov.nasa.jpf.inspector.client.JPFClientCallbackHandler;
import gov.nasa.jpf.inspector.client.JPFInspectorClient;
import gov.nasa.jpf.inspector.client.JPFInspectorClientInterface;
import gov.nasa.jpf.inspector.client.parser.CommandParserFactory;
import gov.nasa.jpf.inspector.client.parser.CommandParserInterface;
import gov.nasa.jpf.inspector.interfaces.InspectorCallBacks;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorBackEndInterface;
import gov.nasa.jpf.inspector.interfaces.exceptions.JPFInspectorGenericErrorException;

/**
 * Launches JPF Inspector.
 * 
 * @author stepan
 *
 */
public class JPFInspectorLauncher {
	
	private JPFInspectorBackEndInterface inspector;
	private List<String> args;
	
	
	
	public JPFInspectorLauncher(List<String> args) {
		this.args = args;
	}
	private JPF prepareJPF() {

//		args.add("+target=oldclassic");
//		args.add("+classpath=+," + System.getProperty("java.class.path"));
		
			// this initializes the JPF configuration from default.properties,
			// site.properties
			// configured extensions (jpf.properties), current directory
			// (jpf.properies) and
			// command line args ("+<key>=<value>" options and *.jpf)
			Config conf = JPF
					.createConfig(args.toArray(new String[args.size()]));


			JPF jpf = new JPF(conf);
			return jpf;

	}
	public JPF launch(VirtualMachineImpl virtualMachineImpl) throws InvocationException {
		InspectorCallBacks callBacks = new JDIClientInspectorCallbackHandler(virtualMachineImpl);
		
		inspector = JPFInspectorFacade.getInspectorBackend(callBacks);
		JPF jpf = prepareJPF();
		
		try {
			inspector.bindWithJPF(jpf);
			return jpf;
		} catch (JPFInspectorGenericErrorException e) {
			throw new InvocationException("Cannot connect to JPF", e);
		}
		
	}
	public JPFInspectorBackEndInterface getInspector() {
		return inspector;
	}
	
	private JPFInspectorClient inspectorClient = (JPFInspectorClient) JPFInspectorFacade.getInspectorClient("", System.err);
	
	public void executeCommand (String cmdStr) {
	    CommandParserInterface parser = CommandParserFactory.getClientCommandParser();
	    ClientCommandInterface cmd = inspectorClient.parseCommand(cmdStr, parser);
	    cmd.executeCommands(inspectorClient, inspector, System.err);
	  }

}
