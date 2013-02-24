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
	public static void main(String[] args) {

		// this initializes the JPF configuration from default.properties,
		// site.properties
		// configured extensions (jpf.properties), current directory
		// (jpf.properies) and
		// command line args ("+<key>=<value>" options and *.jpf)
		Config conf = JPF.createConfig(args);

		// ... modify config according to your needs
		// conf.setProperty("my.property", "whatever");

		JPF jpf = new JPF(conf);

		String jdwpProperty = System.getProperty("jdwp");

		if (jdwpProperty != null) {
			Jdwp jdwp = new Jdwp();
			jdwp.configure(jdwpProperty);

			VirtualMachine vm = new VirtualMachine(jpf);
			VMVirtualMachine.vm = vm;

			jpf.getVM().addListener(new JDWPListener(jpf, vm));
			jdwp.start();

			while (Jdwp.suspendOnStartup()) {
				try {
					jdwp.join();
					break;
				} catch (InterruptedException e) {
				}
			}
			jpf.run();

			Jdwp.notify(new VmDeathEvent());

			jdwp.shutdown();
		} else {
			jpf.run();
		}

	}

}
