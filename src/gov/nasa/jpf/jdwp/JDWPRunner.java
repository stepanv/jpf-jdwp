package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gnu.classpath.jdwp.VMVirtualMachine;
import gnu.classpath.jdwp.event.VmDeathEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.util.ArrayList;
import java.util.List;

public class JDWPRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args2) {
		List<String> args = new ArrayList<String>();

		args.add("+target=oldclassic");
		args.add("+classpath=+,C:/Users/stepan/Data/workspaces/runtime-EclipseApplication/debug-test/bin;" + System.getProperty("java.class.path"));
		
	      // this initializes the JPF configuration from default.properties, site.properties
	      // configured extensions (jpf.properties), current directory (jpf.properies) and
	      // command line args ("+<key>=<value>" options and *.jpf)
	      Config conf = JPF.createConfig(args.toArray(new String[0]));

	      // ... modify config according to your needs
	      //conf.setProperty("my.property", "whatever");

	      JPF jpf = new JPF(conf);

	      Jdwp jdwp = new Jdwp();
	      jdwp.configure("transport=dt_socket,server=y,suspend=y,address=8000");
	      
	      
	      VirtualMachine vm = new VirtualMachine(jpf);
	      VMVirtualMachine.vm = vm;
	      
	      jpf.getVM().addListener(new JDWPListener(jpf, vm));
	      jdwp.start();
	      jpf.run();
	      
	      Jdwp.notify(new VmDeathEvent());
		
	}

}
