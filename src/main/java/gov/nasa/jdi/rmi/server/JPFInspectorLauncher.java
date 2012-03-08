package gov.nasa.jdi.rmi.server;

import java.util.ArrayList;
import java.util.List;

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
	
	private JPF prepareJPF() {
		List<String> args = new ArrayList<String>();

		args.add("+target=oldclassic");
		args.add("+classpath=+," + System.getProperty("java.class.path"));
		
		JPFInspectorClientInterface inspector = JPFInspectorFacade.getInspectorClient("oldclassic", System.err);

		JPF jpf = null;

			// this initializes the JPF configuration from default.properties,
			// site.properties
			// configured extensions (jpf.properties), current directory
			// (jpf.properies) and
			// command line args ("+<key>=<value>" options and *.jpf)
			Config conf = JPF
					.createConfig(args.toArray(new String[args.size()]));


			jpf = new JPF(conf);
			return jpf;

	}
	public JPF launch() throws InvocationException {
		InspectorCallBacks callBacks = new JPFClientCallbackHandler(System.err);
		
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