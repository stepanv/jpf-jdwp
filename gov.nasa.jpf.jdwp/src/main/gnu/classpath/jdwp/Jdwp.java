/* Jdwp.java -- Virtual machine to JDWP back-end programming interface
   Copyright (C) 2005, 2006, 2007 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.classpath.jdwp;

import gnu.classpath.jdwp.event.EventManager;
import gnu.classpath.jdwp.processor.PacketProcessor;
import gnu.classpath.jdwp.transport.ITransport;
import gnu.classpath.jdwp.transport.JdwpConnection;
import gnu.classpath.jdwp.transport.TransportException;
import gnu.classpath.jdwp.transport.TransportFactory;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.JPF.ExitException;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventBase;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.IOException;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

/**
 * Main interface from the virtual machine to the JDWP back-end.
 * 
 * The thread created by this class is only used for initialization. Once it
 * exits, the JDWP backend is fully initialized.
 * 
 * @author Keith Seitz (keiths@redhat.com)
 */
public class Jdwp extends Thread {
	// The single instance of the back-end
	private static Jdwp _instance = null;

	/**
	 * Are we debugging? Only true if debugging *and* initialized.
	 */
	public static boolean isDebugging = false;

	// Packet processor
	private PacketProcessor _packetProcessor;
	private Thread _ppThread;

	// JDWP configuration properties
	private HashMap<String, String> _properties;

	// The suspend property of the configure string
	// (-Xrunjdwp:..suspend=<boolean>)
	private static final String _PROPERTY_SUSPEND = "suspend";
	private static final String _PROPERTY_QUIET = "quiet";
	private static final String _PROPERTY_SERVER = "server";

	// Connection to debugger
	private JdwpConnection _connection;

	// Are we shutting down the current session?
	private boolean _shutdown;

	// A thread group for the JDWP threads
	private ThreadGroup _group;

	// Initialization synchronization
	private Object _initLock = new Object();
	private int _initCount = 0;

	private VirtualMachine vm;

	/**
	 * constructor
	 * 
	 * @param vm
	 */
	public Jdwp(VirtualMachine vm) {
		super("JDWP-initializator");
		_shutdown = false;
		_instance = this;

		this.vm = vm;
	}

	/**
	 * Returns the JDWP back-end, creating an instance of it if one does not
	 * already exist.
	 */
	public static Jdwp getDefault() {
		return _instance;
	}

	/**
	 * Get the thread group used by JDWP threads
	 * 
	 * @return the thread group
	 */
	public ThreadGroup getJdwpThreadGroup() {
		return _group;
	}

	/**
	 * Should the virtual machine suspend on startup?
	 */
	public static boolean suspendOnStartup() {
		Jdwp jdwp = getDefault();
		if (jdwp != null) {
			String suspend = (String) jdwp._properties.get(_PROPERTY_SUSPEND);
			if (suspend != null && suspend.equals("y"))
				return true;
		}

		return false;
	}

	public boolean isServer() {
		Jdwp jdwp = getDefault();
		if (jdwp != null) {
			String suspend = (String) jdwp._properties.get(_PROPERTY_SERVER);
			if (suspend != null && suspend.equals("y"))
				return true;
		}

		return false;
	}

	/**
	 * Configures the back-end
	 * 
	 * @param configArgs
	 *            a string of configury options
	 */
	public void configure(String configArgs) {
		_processConfigury(configArgs);
	}

	// A helper function to initialize the transport layer
	private void _doInitialization() throws TransportException {
		_group = new ThreadGroup("JDWP threads") {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.err.println("Shutting down whole VM: " + e.getMessage());
				System.exit(1);
			}

		};
		// initialize transport
		ITransport transport = TransportFactory.newInstance(_properties);
		_connection = new JdwpConnection(_group, transport);

		// Get quiet {form: "y" or "n"} .. applies only when server=y
		if (_properties.get(_PROPERTY_QUIET) == null || _properties.get(_PROPERTY_QUIET).toLowerCase().equals("y")) {
			if (transport.isServer()) {
				System.out.println("Listening for transport " + transport.getName() + " at address: " + transport.getAddress());
			}
		}
		_connection.initialize();
		_connection.start();

		// Create processor
		_packetProcessor = new PacketProcessor(_connection);
		_ppThread = new Thread(_group, new Runnable() {
			public void run() {
				AccessController.doPrivileged(_packetProcessor);
			}
		}, "packet processor");
		_ppThread.start();
	}

	/**
	 * Shutdown the JDWP back-end
	 * 
	 * NOTE: This does not quite work properly. See notes in run() on this
	 * subject (catch of InterruptedException).
	 */
	public void shutdown() {
		if (!_shutdown) {
			_packetProcessor.shutdown();
			_ppThread.interrupt();
			_connection.shutdown();
			_shutdown = true;
			isDebugging = false;

			vm.exit(1);

			interrupt();
		}
	}

	/**
	 * Notify the debugger of an event. This method should not be called if
	 * debugging is not active (but it would not cause any harm). Places where
	 * event notifications occur should check isDebugging before doing anything.
	 * 
	 * The event is filtered through the event manager before being sent.
	 * 
	 * @param event
	 *            the event to report
	 */
	public static void notify(Event event) {
		Jdwp jdwp = getDefault();
		if (jdwp != null) {
			EventManager em = EventManager.getDefault();
			EventRequest[] requests = em.getEventRequests(event);
			for (int i = 0; i < requests.length; ++i) {
				try {
					sendEvent(requests[i], event);
					jdwp._enforceSuspendPolicy(requests[i].getSuspendPolicy());
				} catch (JPFException jpfe) {
					/*
					 * A JPF generated exception. Let's not pretend nothing
					 * happened
					 */
					throw jpfe;
				} catch (ExitException ee) {
					/* Exit exception should be passed not modified. */
					throw ee;
				} catch (Exception e) {
					/*
					 * Really not much we can do. For now, just print out a
					 * warning to the user.
					 */
					throw new IllegalStateException("The execution happended to end in not predicted state.", e);
				}
			}
		}
	}

	// public static void notify2(EventBase[] events) {
	// Jdwp jdwp = getDefault();
	//
	// if (jdwp != null) {
	// SuspendPolicy suspendPolicy = SuspendPolicy.NONE;
	// EventManager em = EventManager.getDefault();
	// ArrayList allEvents = new ArrayList();
	// ArrayList allRequests = new ArrayList();
	// for (int i = 0; i < events.length; ++i) {
	// EventRequest[] r = em.getEventRequests(events[i]);
	// for (int j = 0; j < r.length; ++j) {
	// /*
	// * This is hacky, but it's not clear whether this can really
	// * happen, and if it does, what should occur.
	// */
	// allEvents.add(events[i]);
	// allRequests.add(r[j]);
	//
	// // Perhaps this is overkill?
	// if (suspendPolicy.compareTo(r[j].getSuspendPolicy()) < 0) {
	// suspendPolicy = r[j].getSuspendPolicy();
	// }
	//
	// }
	// }
	//
	// try {
	// EventBase[] e = new EventBase[allEvents.size()];
	// allEvents.toArray(e);
	// EventRequest[] r = new EventRequest[allRequests.size()];
	// allRequests.toArray(r);
	// sendEvents(r, e, suspendPolicy);
	// jdwp._enforceSuspendPolicy(suspendPolicy);
	// } catch (Exception e) {
	// /*
	// * Really not much we can do. For now, just print out a warning
	// * to the user.
	// */
	// System.out.println("Jdwp.notify: caught exception: " + e);
	// }
	// }
	// }

	/**
	 * Notify the debugger of "co-located" events. This method should not be
	 * called if debugging is not active (but it would not cause any harm).
	 * Places where event notifications occur should check isDebugging before
	 * doing anything.
	 * 
	 * The events are filtered through the event manager before being sent.
	 * 
	 * @param events
	 *            the events to report
	 */
	public static void notify(EventBase[] events) {
		Jdwp jdwp = getDefault();

		if (jdwp != null) {
			SuspendPolicy suspendPolicy = SuspendPolicy.NONE;
			EventManager em = EventManager.getDefault();

			Map<EventRequest<Event>, Event> requestToEventMap = new HashMap<EventRequest<Event>, Event>();
			for (int i = 0; i < events.length; ++i) {
				EventRequest[] r = em.getEventRequests(events[i]);
				for (int j = 0; j < r.length; ++j) {
					/*
					 * This is hacky, but it's not clear whether this can really
					 * happen, and if it does, what should occur.
					 */
					requestToEventMap.put(r[j], events[i]);

					// Perhaps this is overkill?
					if (suspendPolicy.compareTo(r[j].getSuspendPolicy()) < 0) {
						suspendPolicy = r[j].getSuspendPolicy();
					}

				}
			}

			try {
				sendEvents(requestToEventMap, suspendPolicy);
				jdwp._enforceSuspendPolicy(suspendPolicy);
			} catch (Exception e) {
				/*
				 * Really not much we can do. For now, just print out a warning
				 * to the user.
				 */
				System.out.println("Jdwp.notify: caught exception: " + e);
			}
		}
	}

	/**
	 * Sends the event to the debugger.
	 * 
	 * This method bypasses the event manager's filtering.
	 * 
	 * @param request
	 *            the debugger request for the event
	 * @param event
	 *            the event to send
	 * @throws IOException
	 *             if a communications failure occurs
	 */
	public static void sendEvent(EventRequest request, Event event) throws IOException {
		Map<EventRequest<Event>, Event> requestToEventMap = new HashMap<EventRequest<Event>, Event>();
		requestToEventMap.put(request, event);
		sendEvents(requestToEventMap, request.getSuspendPolicy());
	}

	/**
	 * Sends the events to the debugger.
	 * 
	 * This method bypasses the event manager's filtering.
	 * 
	 * @param requests
	 *            list of debugger requests for the events
	 * @param events
	 *            the events to send
	 * @param suspendPolicy
	 *            .identifier() the suspendPolicy enforced by the VM
	 * @throws IOException
	 *             if a communications failure occurs
	 */
	public static void sendEvents(Map<EventRequest<Event>, Event> requestToEventMap, SuspendPolicy suspendPolicy) throws IOException {
		Jdwp jdwp = getDefault();
		if (jdwp != null && isDebugging) {
			synchronized (jdwp._connection) {
				jdwp._connection.sendEvents(requestToEventMap, suspendPolicy);
			}
		}
	}

	// Helper function to enforce suspend policies on event notification
	private void _enforceSuspendPolicy(SuspendPolicy suspendPolicy) {
		switch (suspendPolicy) {
		case NONE:
			// do nothing
			break;

		case EVENT_THREAD:
			ThreadInfo currentThread = VM.getVM().getCurrentThread();
			vm.getExecutionManager().markThreadSuspended(currentThread);
			vm.getExecutionManager().blockVMExecution();
			break;

		case ALL:
			vm.getExecutionManager().markVMSuspended();
			vm.getExecutionManager().blockVMExecution();
			break;
		}
	}

	/**
	 * Allows subcomponents to specify that they are initialized.
	 * 
	 * Subcomponents include JdwpConnection and PacketProcessor.
	 */
	public void subcomponentInitialized() {
		synchronized (_initLock) {
			++_initCount;
			_initLock.notify();
		}
	}

	public void run() {
		try {
			_doInitialization();

			/*
			 * We need a little internal synchronization here, so that when this
			 * thread dies, the back-end will be fully initialized, ready to
			 * start servicing the VM and debugger.
			 */
			synchronized (_initLock) {
				while (_initCount != 2)
					_initLock.wait();
			}
			_initLock = null;
		} catch (Throwable t) {
			System.out.println("Exception in JDWP back-end: " + t);
			System.exit(1);
		}

		/*
		 * Force creation of the EventManager. If the event manager has not been
		 * created when isDebugging is set, it is possible that the VM will call
		 * Jdwp.notify (which uses EventManager) while the EventManager is being
		 * created (or at least this is a problem with gcj/gij).
		 */
		EventManager.getDefault();

		// Now we are finally ready and initialized
		isDebugging = true;
	}

	// A helper function to process the configure string "-Xrunjdwp:..."
	private void _processConfigury(String configString) {
		// Loop through configuration arguments looking for a
		// transport name
		_properties = new HashMap<String, String>();
		String[] options = configString.split(",");
		for (int i = 0; i < options.length; ++i) {
			String[] property = options[i].split("=");
			if (property.length == 2)
				_properties.put(property[0], property[1]);
			// ignore malformed options
		}
	}
}
