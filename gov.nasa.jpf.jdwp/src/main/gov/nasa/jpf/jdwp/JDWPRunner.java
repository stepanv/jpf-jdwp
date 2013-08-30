/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entry point for the JDWP backend.<br/>
 * Currently, this is the only way how to initialize JDWP for JPF. (In the
 * future other ways needs to be added. TODO)<br/>
 * 
 * <h2>Thread overview</h2>
 * <ol>
 * <li>
 * <h3>Main thread</h3>
 * <p>
 * The main thread (the one that runs the {@link JDWPRunner#main(String[])}) is
 * the same thread that executes JPF, hence runs the application (SuT - System
 * under the test). (Refer to {@link VirtualMachine#run()}<br/>
 * Anytime the SuT is run, the {@link VirtualMachine#getRunLock()} <i>lock</i>
 * must be hold.<br/>
 * The main thread releases the <tt>runLock</tt> voluntarily so that other
 * threads can run SuT or query JPF (even for read only access it's required to
 * not run in parallel).
 * </p>
 * </li>
 * <li>
 * <h3>JDWP initialization thread</h3>
 * <p>
 * The JDWP initialization thread initializes the JDWP backend and then
 * terminates.
 * </p>
 * </li>
 * <li>
 * <h3>JDWP packet processor thread</h3>
 * <p>
 * The JDWP packet processor thread reads commands from the socket and stores
 * them in a queue which is processed by the <i>JDWP command processor</i>
 * thread.
 * </p>
 * </li>
 * <li>
 * <h3>JDWP command processor thread</h3>
 * <p>
 * The JDWP command processor thread executes commands from the queue that is
 * populated by the <i>JDWP packet processor</i> thread.<br/>
 * When the command is executed the {@link VirtualMachine#getRunLock()} must be
 * acquired. Even though several commands can be executed without this lock, the
 * implementation doesn't make a difference between the commands and all of them
 * are treated the same way. The <tt>runLock</tt> must be hold.
 * </p>
 * <p>
 * If the command processor thread gets stuck the commands are not executed.
 * This needs to be solved. TODO
 * </p>
 * </li>
 * </ol>
 * 
 * @author stepan
 */
public class JDWPRunner {

  public static VirtualMachine vm;

  static final Logger logger = LoggerFactory.getLogger(JDWPRunner.class);

  /**
   * The entry point of JPF JPDA implementation.
   * 
   * @param args
   *          Standard JPF application arguments
   */
  public static void main(String[] args) {
    // this initializes the JPF configuration from default.properties,
    // site.properties
    // configured extensions (jpf.properties), current directory
    // (jpf.properies) and
    // command line args ("+<key>=<value>" options and *.jpf)
    Config conf = JPF.createConfig(args);

    conf.printEntries();

    JPF jpf = new JPF(conf);

    String jdwpProperty = System.getProperty("jdwp");

    if (jdwpProperty != null) {
      logger.debug("Found JDWP property: {}", jdwpProperty);

      vm = new VirtualMachine(jpf);

      Jdwp jdwp = new Jdwp(vm);
      jdwp.configure(jdwpProperty);

      vm.setJdwp(jdwp);

      jpf.addListener(new JDWPListener(jpf, vm));
      jdwp.start();

      while (Jdwp.suspendOnStartup() || !jdwp.isServer()) {
        try {
          jdwp.join();
          break;
        } catch (InterruptedException e) {
        }
      }

      vm.run();
    } else {
      System.err.println("System property 'jdwp' not found. Running JPF without the JDWP agent.");
      jpf.run();
    }

  }

}
