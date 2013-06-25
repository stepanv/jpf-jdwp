/* EventManager.java -- event management and notification infrastructure
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


package gnu.classpath.jdwp.event;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.InvalidEventType;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages event requests and filters event notifications.
 *
 * The purpose of this class is actually two-fold:
 *
 * 1) Maintain a list of event requests from the debugger
 * 2) Filter event notifications from the VM
 *
 * If an event request arrives from the debugger, the back-end will
 * call {@link #requestEvent}, which will first check for a valid event.
 * If it is valid, <code>EventManager</code> will record the request
 * internally and register the event with the virtual machine, which may
 * choose to handle the request itself (as is likely the case with
 * breakpoints and other execution-related events), or it may decide to
 * allow the <code>EventManager</code> to handle notifications and all
 * filtering (which is convenient for other events such as class (un)loading).
 *
 * @author Keith Seitz  (keiths@redhat.com)
 */
public class EventManager
{
  final static Logger logger = LoggerFactory.getLogger(EventManager.class);
  
  // Single instance
  private static class EventManagerHolder {
	  private static final EventManager _instance = new EventManager();
  }

  // maps event (EVENT_*) to lists of EventRequests
  private Hashtable<EventKind, Hashtable<Integer, EventRequest>> _requests = null;

  /**
   * Returns an instance of the event manager
   *
   * @return the event manager
   */
  public static EventManager getDefault()
  {
    return EventManagerHolder._instance;
  }

  // Private constructs a new <code>EventManager</code>
  private EventManager ()
  {
    _requests = new Hashtable <EventKind, Hashtable<Integer, EventRequest>>();

    // Add lists for all the event types
    // We're using Hashtables since all access methods are synchronized (using this object as a lock)
    _requests.put (EventKind.SINGLE_STEP,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.BREAKPOINT,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.FRAME_POP,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.EXCEPTION,
                   new Hashtable<Integer, EventRequest> ());
    _requests.put (EventKind.USER_DEFINED,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.THREAD_START,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.THREAD_DEATH,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.CLASS_PREPARE,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.CLASS_UNLOAD,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.CLASS_LOAD,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.FIELD_ACCESS,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.FIELD_MODIFICATION,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.METHOD_ENTRY,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.METHOD_EXIT,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.VM_START,
                   new Hashtable <Integer, EventRequest>());
    _requests.put (EventKind.VM_DEATH,
                   new Hashtable <Integer, EventRequest>());
    
    _requests.put (EventKind.VM_INIT,
    		_requests.get(EventKind.VM_START));
    _requests.put (EventKind.THREAD_END,
    		_requests.get(EventKind.THREAD_DEATH));


    // Add auto-generated event notifications
    // only two: VM_INIT, VM_DEATH
    try
      {
        SuspendPolicy sp = (Jdwp.suspendOnStartup()
                   ? SuspendPolicy.ALL : SuspendPolicy.NONE);
        requestEvent (EventRequest.nullRequestIdFactory(EventKind.VM_INIT, sp, null));
        requestEvent (EventRequest.nullRequestIdFactory(EventKind.VM_DEATH,
        		SuspendPolicy.NONE, null));
      }
    catch (JdwpError e)
      {
        // This can't happen
      }
  }

  /**
   * Returns all requests for the given event. This method will only
   * be used if the <code>EventManager</code> is handling event filtering.
   *
   * @param  event  the event
   * @return requests that are interested in this event
   *         or <code>null</code> if none (and event should not be sent)
   * @throws IllegalArgumentException for invalid event kind
   */
  public EventRequest[] getEventRequests(Event event)
  {
    List<EventRequest> interestedEvents = new CopyOnWriteArrayList<EventRequest>();
    Hashtable<Integer, EventRequest> requests = _requests.get(event.getEventKind());
    if (requests == null)
      {
        // Did not get a valid event type
        throw new IllegalArgumentException("invalid event kind: " + event.getEventKind());
      }

    synchronized (requests) {
		
	
    // Loop through the requests. Must look at ALL requests in order
    // to evaluate all filters (think count filter).
    Iterator<EventRequest> rIter = requests.values().iterator();
    while (rIter.hasNext())
      {
        EventRequest request = rIter.next();
        if (request.matches(event))
          interestedEvents.add(request);
      }
    }

    EventRequest[] r = new EventRequest[interestedEvents.size()];
    interestedEvents.toArray(r);
    return r;
    
  }

  /**
   * Requests monitoring of an event.
   *
   * The debugger registers for event notification through
   * an event filter. If no event filter is specified for an event
   * in the VM, it is assumed that the debugger is not interested in
   * receiving notifications of this event.
   *
   * The virtual machine will be notified of the request.
   *
   * @param request  the request to monitor
   * @throws InvalidEventTypeException for invalid event kind
   * @throws JdwpException for other errors involving request
   */
  public void requestEvent (EventRequest request)
    throws JdwpError
  {
    // Add request to request list
    Hashtable<Integer, EventRequest> requests = _requests.get (request.getEventKind ());
    if (requests == null)
      {
        // Did not get a valid event type
        throw new InvalidEventType (request.getEventKind ());
      }

    // Register the event with the VM
//    VMVirtualMachine.vm.registerEventRequest (request);
    requests.put (new Integer (request.getId ()), request);
    logger.info("Registered event request: {}", request);
  }

  /**
   * Deletes the given request from the management table
   *
   * @param  kind  the event kind
   * @param  id    the ID of the request to delete
   * @throws IllegalArgumentException for invalid event kind
   * @throws JdwpException for other errors deleting request
   */
  public void deleteRequest (byte kind, int id)
    throws JdwpError
  {
    Hashtable<Integer, EventRequest> requests =  _requests.get (EventKind.BREAKPOINT.convert(kind));
    if (requests == null)
      {
        // Did not get a valid event type
        throw new IllegalArgumentException ("invalid event kind: " + kind);
      }

		
	
    Integer iid = new Integer (id);
    EventRequest request = requests.get (iid);
    if (request != null)
      {
    	
        //VMVirtualMachine.unregisterEvent (request);
        requests.remove (iid);
        logger.info("Removed event request: {}", request);
      }
  }

  /**
   * Clears all the requests for a given event
   *
   * @param  kind  the event kind
   * @throws IllegalArgumentException for invalid event kind
   * @throws JdwpException for error clearing events
   */
  public void clearRequests (EventKind eventKind)
    throws JdwpError
  {
    Hashtable<Integer, EventRequest> requests = _requests.get (eventKind);

   //VMVirtualMachine.clearEvents (kind);
    
    	requests.clear ();
    
  }

  /**
   * Returns a given event request for an event
   *
   * @param  kind  the kind of event for the request
   * @param  id    the integer request id to return
   * @return  the request for the given event kind with the given id
   *          (or <code>null</code> if not found)
   * @throws IllegalArgumentException for invalid event kind
   */
  public EventRequest getRequest (EventKind eventKind, int id)
  {
    Hashtable<Integer, EventRequest> requests = _requests.get (eventKind);

    	return (EventRequest) requests.get (new Integer (id));
    
  }

  /**
   * Returns all requests of the given event kind
   *
   * @param  kind  the event kind
   * @returns a <code>Collection</code> of all the registered requests
   * @throws IllegalArgumentException for invalid event kind
   */
  public Collection getRequests (EventKind eventKind)
  {
    Hashtable<Integer, EventRequest> requests = _requests.get (eventKind);

    return requests.values ();
  }
}
