package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.event.VmDeathEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDWPRunner {

	public static VirtualMachine vm;

	static final Logger logger = LoggerFactory.getLogger(JDWPRunner.class);

	/**
	 * The entry point of JPF JPDA implementation.
	 * 
	 * @param args
	 *            Standard JPF application arguments
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
			logger.debug("Found JDWP property: {}", jdwpProperty);

			Jdwp jdwp = new Jdwp();
			jdwp.configure(jdwpProperty);

			vm = new VirtualMachine(jpf);

			jpf.getVM().addListener(new JDWPListener(jpf, vm));
			jdwp.start();

			while (Jdwp.suspendOnStartup() || !jdwp.isServer()) {
				try {
					jdwp.join();
					break;
				} catch (InterruptedException e) {
				}
			}
			jpf.run();

			synchronized (vm) {
				Jdwp.notify(new VmDeathEvent());
			}

			jdwp.shutdown();
		} else {
			System.err.println("System property 'jdwp' not found. Running JPF without the JDWP agent.");
			jpf.run();
		}

	}

}
