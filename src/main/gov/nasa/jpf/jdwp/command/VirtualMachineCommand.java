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

package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.VirtualMachine.Capabilities;
import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.JdwpException.ErrorType;
import gov.nasa.jpf.jdwp.exception.NotImplementedException;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VirtualMachineCommand} enum class implements the
 * {@link CommandSet#VIRTUALMACHINE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_VirtualMachine"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_VirtualMachine</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum VirtualMachineCommand implements Command, ConvertibleEnum<Byte, VirtualMachineCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the JDWP version implemented by the target VM. The version string
   * format is implementation dependent.
   * </p>
   */
  VERSION(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      JdwpString.write(contextProvider.getJPF().getReporter().getJPFBanner(), os);
      os.writeInt(JdwpConstants.MINOR);
      os.writeInt(JdwpConstants.MAJOR);
      JdwpString.write(System.getProperty("java.version"), os);
      JdwpString.write(System.getProperty("java.vm.name"), os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns reference types for all the classes loaded by the target VM which
   * match the given signature. Multple reference types will be returned if two
   * or more class loaders have loaded a class of the same name. The search is
   * confined to loaded classes only; no attempt is made to load a class of the
   * given signature.
   * </p>
   */
  CLASSESBYSIGNATURE(2) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      String signature = JdwpString.read(bytes);

      ArrayList<ClassInfo> matchingClasses = new ArrayList<ClassInfo>();

      Collection<ClassInfo> classes = contextProvider.getVirtualMachine().getAllLoadedClasses();

      logger.info("looking for class: " + signature);
      for (ClassInfo classInfo : classes) {
        if (signature.equals(classInfo.getSignature())) {
          matchingClasses.add(classInfo);
        }
      }

      os.writeInt(matchingClasses.size());
      for (ClassInfo classInfo : matchingClasses) {
        logger.debug("Sending classes by signature: " + classInfo + "");
        ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(classInfo);
        id.writeTagged(os);

        // write the VERIFIED status since JPF doesn't implement them
        ClassStatus.VERIFIED.write(os);
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns reference types for all classes currently loaded by the target VM.
   * </p>
   */
  ALLCLASSES(3) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      Collection<ClassInfo> classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
      os.writeInt(classes.size());

      for (ClassInfo clazz : classes) {
        ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
        id.writeTagged(os);
        JdwpString.write(clazz.getSignature(), os);

        os.writeInt(ClassStatus.classStatus(clazz));
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns all threads currently running in the target VM . The returned list
   * contains threads created through java.lang.Thread, all native threads
   * attached to the target VM through JNI, and system threads created by the
   * target VM. Threads that have not yet been started and threads that have
   * completed their execution are not included in the returned list.
   * </p>
   */
  ALLTHREADS(4) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ThreadList threadList = contextProvider.getVM().getThreadList();

      os.writeInt(threadList.getLiveThreadCount());

      for (ThreadInfo thread : threadList) {
        if (thread.isAlive()) {
          contextProvider.getObjectManager().getThreadId(thread).write(os);
        }
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns all thread groups that do not have a parent. This command may be
   * used as the first step in building a tree (or trees) of the existing thread
   * groups.
   * </p>
   */
  TOPLEVELTHREADGROUPS(5) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      List<ElementInfo> topLevelThreadGroups = new ArrayList<ElementInfo>();

      for (ThreadInfo threadInfo : VM.getVM().getLiveThreads()) {
        int group = threadInfo.getThreadObject().getReferenceField(JdwpConstants.FIELDNAME_THREAD_GROUP);
        ElementInfo threadGroupElementInfo = contextProvider.getVM().getHeap().get(group);

        int parentref = threadGroupElementInfo.getReferenceField(JdwpConstants.FIELDNAME_THREADGROUP_PARENT);
        ElementInfo parent = contextProvider.getVM().getHeap().get(parentref);

        if (parent == null) {
          // null parent means it is top level thread group
          topLevelThreadGroups.add(threadGroupElementInfo);
        }
      }

      os.writeInt(topLevelThreadGroups.size());

      for (ElementInfo threadGroupElementInfo : topLevelThreadGroups) {
        ThreadGroupId threadGroupId = contextProvider.getObjectManager().getThreadGroupId(threadGroupElementInfo);
        threadGroupId.write(os);
      }

    }
  },

  /**
   * 
   * <h2>JDWP Specification</h2>
   * <p>
   * Invalidates this virtual machine mirror. The communication channel to the
   * target VM is closed, and the target VM prepares to accept another
   * subsequent connection from this debugger or another debugger, including the
   * following tasks:
   * <ul>
   * <li>
   * All event requests are cancelled.</li>
   * </li>
   * <li>All threads suspended by the thread-level
   * {@link ThreadReferenceCommand#RESUME} command or the VM-level
   * {@link VirtualMachineCommand#RESUME} command are resumed as many times as
   * necessary for them to run.</li>
   * <li>Garbage collection is re-enabled in all cases where it was disabled
   * with {@link ObjectReferenceCommand#DISABLECOLLECTION}</li>
   * </ul>
   * Any current method invocations executing in the target VM are continued
   * after the disconnection. Upon completion of any such method invocation, the
   * invoking thread continues from the location where it was originally
   * stopped.
   * </p>
   * <p>
   * Resources originating in this VirtualMachine (ObjectReferences,
   * ReferenceTypes, etc.) will become invalid.
   * </p>
   */
  DISPOSE(6) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      // All event requests are cancelled.
      for (EventKind eventKind : EventKind.values()) {

        if (eventKind == EventKind.VM_DISCONNECTED) {
          continue;
        }
        Jdwp.getEventRequestManager().clearEventRequests(eventKind);
      }

      // All threads suspended by the thread-level resume command or the
      // VM-level resume command are resumed as many times as necessary
      // for them to run.

      // Garbage collection is re-enabled in all cases where it was
      // ObjectReference
      contextProvider.getVirtualMachine().collectAllDisabledObjects();

      // Now, we need to get prepared for another connection .. this
      // might be a problem with singletons...

      throw new NotImplementedException();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the sizes of variably-sized data types in the target VM.The
   * returned values indicate the number of bytes used by the identifiers in
   * command and reply packets.
   * </p>
   */
  IDSIZES(7) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Suspends the execution of the application running in the target VM. All
   * Java threads currently running will be suspended. <br/>
   * Unlike java.lang.Thread.suspend, suspends of both the virtual machine and
   * individual threads are counted. Before a thread will run again, it must be
   * resumed through the VM-level resume command or the thread-level resume
   * command the same number of times it has been suspended.
   * </p>
   * Note that we don't count thread suspension count as stated above in the
   * specification
   */
  SUSPEND(8) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().getExecutionManager().markVMSuspended();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Resumes execution of the application after the suspend command or an event
   * has stopped it. Suspensions of the Virtual Machine and individual threads
   * are counted. If a particular thread is suspended n times, it must resumed n
   * times before it will continue.
   * </p>
   */
  RESUME(9) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().getExecutionManager().markVMResumed();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Terminates the target VM with the given exit code. On some platforms, the
   * exit code might be truncated, for example, to the low order 8 bits. All ids
   * previously returned from the target VM become invalid. Threads running in
   * the VM are abruptly terminated. A thread death exception is not thrown and
   * finally blocks are not run.
   * </p>
   */
  EXIT(10) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int exitCode = bytes.getInt();

      contextProvider.getVirtualMachine().exit(exitCode);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Creates a new string object in the target VM and returns its id.
   * </p>
   */
  CREATESTRING(11) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // is invoked when inspecting an array field for instance
      String string = JdwpString.read(bytes);

      // JPF requires a thread to create an ElementInfo
      // There is no better thread than the current so let's use it
      ThreadInfo currentThread = contextProvider.getVM().getCurrentThread();
      ElementInfo stringElementInfo = VM.getVM().getHeap().newInternString(string, currentThread);

      contextProvider.getVirtualMachine().lastCreatedString = stringElementInfo.getObjectRef();

      ObjectId stringId = contextProvider.getObjectManager().getObjectId(stringElementInfo);

      // Since this string isn't referenced anywhere we'll disable garbage
      // collection on it so it's still around when the debugger gets back
      // to it.

      stringId.disableCollection();
      logger.debug("Collection for: {} disabled (is this instance: {}).", stringId, stringId.get());

      stringId.write(os);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Retrieve this VM's capabilities. The capabilities are returned as booleans,
   * each indicating the presence or absence of a capability. The commands
   * associated with each capability will return the NOT_IMPLEMENTED error if
   * the cabability is not available.
   * </p>
   */
  CAPABILITIES(12) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      os.writeBoolean(Capabilities.CAN_WATCH_FIELD_MODIFICATION);
      os.writeBoolean(Capabilities.CAN_WATCH_FIELD_ACCESS);
      os.writeBoolean(Capabilities.CAN_GET_BYTECODES);
      os.writeBoolean(Capabilities.CAN_GET_SYNTHETIC_ATTRIBUTE);
      os.writeBoolean(Capabilities.CAN_GET_OWNED_MONITOR_INFO);
      os.writeBoolean(Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR);
      os.writeBoolean(Capabilities.CAN_GET_MONITOR_INFO);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Retrieve the classpath and bootclasspath of the target VM. If the classpath
   * is not defined, returns an empty list. If the bootclasspath is not defined
   * returns an empty list.
   * </p>
   */
  CLASSPATHS(13) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ClassLoaderInfo systemClassLoader = ClassLoaderInfo.getCurrentSystemClassLoader();

      ByteArrayOutputStream classpathOs = new ByteArrayOutputStream(0);
      DataOutputStream classpathDos = new DataOutputStream(classpathOs);

      int classpathElements = 0;
      for (String pathElement : systemClassLoader.getClassPathElements()) {
        JdwpString.writeNullAsEmpty(pathElement, classpathDos);
        classpathElements++;
      }

      JdwpString.writeNullAsEmpty(System.getProperty("user.dir"), os);
      os.writeInt(classpathElements);
      os.write(classpathOs.toByteArray());

      // no boothclasspath
      os.writeInt(0);
    }
  },

  /**
   * <h2>JDWP Specification</h2>
   * <p>
   * Releases a list of object IDs. For each object in the list, the following
   * applies. The count of references held by the back-end (the reference count)
   * will be decremented by refCnt. If thereafter the reference count is less
   * than or equal to zero, the ID is freed. Any back-end resources associated
   * with the freed ID may be freed, and if garbage collection was disabled for
   * the object, it will be re-enabled. The sender of this command promises that
   * no further commands will be sent referencing a freed ID.
   * </p>
   * <p>
   * Use of this command is not required. If it is not sent, resources
   * associated with each ID will be freed by the back-end at some time after
   * the corresponding object is garbage collected. It is most useful to use
   * this command to reduce the load on the back-end if a very large number of
   * objects has been retrieved from the back-end (a large array, for example)
   * but may not be garbage collected any time soon.
   * </p>
   * <p>
   * IDs may be re-used by the back-end after they have been freed with this
   * command.This description assumes reference counting, a back-end may use any
   * implementation which operates equivalently.
   * </p>
   */
  DISPOSEOBJECTS(14) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int requests = bytes.getInt();
      for (int i = 0; i < requests; ++i) {
        ObjectId objectId = contextProvider.getObjectManager().readObjectId(bytes);
        @SuppressWarnings("unused")
        int refCnt = bytes.getInt();

        contextProvider.getVirtualMachine().enableCollection(objectId);
        // we don't care about refCnt because the JDWP back-end doesn't hold the
        // associated object directly and thus the object can be always GCed
      }

    }
  },
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Tells the target VM to stop sending events. Events are not discarded; they
   * are held until a subsequent ReleaseEvents command is sent. This command is
   * useful to control the number of events sent to the debugger VM in
   * situations where very large numbers of events are generated. While events
   * are held by the debugger back-end, application execution may be frozen by
   * the debugger back-end to prevent buffer overflows on the back end.
   * Responses to commands are never held and are not affected by this command.
   * If events are already being held, this command is ignored.
   * </p>
   */
  HOLDEVENTS(15) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().holdEvents();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Tells the target VM to continue sending events. This command is used to
   * restore normal activity after a HoldEvents command. If there is no current
   * HoldEvents command in effect, this command is ignored.
   * </p>
   */
  RELEASEEVENTS(16) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().releaseEvents();

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Retrieve all of this VM's capabilities. The capabilities are returned as
   * booleans, each indicating the presence or absence of a capability. The
   * commands associated with each capability will return the
   * {@link ErrorType#NOT_IMPLEMENTED} (that is {@link NotImplementedException})
   * error if the cabability is not available.
   * </p>
   * 
   * @since JDWP version 1.4.
   */
  CAPABILITIESNEW(17) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      CAPABILITIES.execute(bytes, os, contextProvider);
      os.writeBoolean(CapabilitiesNew.CAN_REDEFINE_CLASSES);
      os.writeBoolean(CapabilitiesNew.CAN_ADD_METHOD);
      os.writeBoolean(CapabilitiesNew.CAN_UNRESTRICTEDLY_REDEFINE_CLASSES);
      os.writeBoolean(CapabilitiesNew.CAN_POP_FRAMES);
      os.writeBoolean(CapabilitiesNew.CAN_USE_INSTANCE_FILTERS);
      os.writeBoolean(CapabilitiesNew.CAN_GET_SOURCE_DEBUG_EXTENSION);
      os.writeBoolean(CapabilitiesNew.CAN_REQUEST_V_M_DEATH_EVENT);
      os.writeBoolean(CapabilitiesNew.CAN_SET_DEFAULT_STRATUM);
      os.writeBoolean(CapabilitiesNew.CAN_GET_INSTANCE_INFO);
      os.writeBoolean(CapabilitiesNew.CAN_REQUEST_MONITOR_EVENTS);
      os.writeBoolean(CapabilitiesNew.CAN_GET_MONITOR_FRAME_INFO);
      os.writeBoolean(CapabilitiesNew.CAN_USE_SOURCE_NAME_FILTERS);
      os.writeBoolean(CapabilitiesNew.CAN_GET_CONSTANT_POOL);
      os.writeBoolean(CapabilitiesNew.CAN_FORCE_EARLY_RETURN);

      for (int i = 22; i <= 32; ++i) {
        os.writeBoolean(false);
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Installs new class definitions. If there are active stack frames in methods
   * of the redefined classes in the target VM then those active frames continue
   * to run the bytecodes of the original method. These methods are considered
   * obsolete - see {@link MethodCommand#ISOBSOLETE} command. The methods in the
   * redefined classes will be used for new invokes in the target VM. The
   * original method ID refers to the redefined method. All breakpoints in the
   * redefined classes are cleared.If resetting of stack frames is desired, the
   * {@link StackFrameCommand#POPFRAMES} command can be used to pop frames with
   * obsolete methods.
   * </p>
   * <p>
   * Requires {@link CapabilitiesNew#CAN_REDEFINE_CLASSES} capability. In
   * addition to the this capability, the target VM must have the
   * {@link CapabilitiesNew#CAN_ADD_METHOD} capability to add methods when
   * redefining classes, or the
   * {@link CapabilitiesNew#CAN_UNRESTRICTEDLY_REDEFINE_CLASSES} to redefine
   * classes in arbitrary ways.
   * </p>
   */
  REDEFINECLASSES(18) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // can throw an exception as far as
      throw new NotImplementedException();

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Set the default stratum. <br/>
   * Requires {@link CapabilitiesNew#CAN_SET_DEFAULT_STRATUM} capability.
   * </p>
   */
  SETDEFAULTSTRATUM(19) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // it's ok as far as the associated capability is false
      throw new NotImplementedException();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns reference types for all classes currently loaded by the target VM.
   * Both the JNI signature and the generic signature are returned for each
   * class. Generic signatures are described in the signature attribute section
   * in the <a href="http://java.sun.com/docs/books/vmspec">Java Virtual Machine
   * Specification, 3rd Edition</a>.
   * </p>
   * 
   * @since JDWP version 1.5.
   * 
   */
  ALLCLASSESWITHGENERIC(20) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      Collection<ClassInfo> classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
      os.writeInt(classes.size());

      Iterator<ClassInfo> iter = classes.iterator();
      while (iter.hasNext()) {
        ClassInfo clazz = iter.next();
        ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
        id.writeTagged(os);
        JdwpString.write(clazz.getSignature(), os);
        JdwpString.writeNullAsEmpty(clazz.getGenericSignature(), os);
        os.writeInt(ClassStatus.classStatus(clazz));
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the number of instances of each reference type in the input list.
   * Only instances that are reachable for the purposes of garbage collection
   * are counted. If a reference type is invalid, eg. it has been unloaded, zero
   * is returned for its instance count. <br/>
   * Requires {@link CapabilitiesNew#CAN_GET_INSTANCE_INFO}.
   * </p>
   * 
   * @since JDWP version 1.6.
   */
  INSTANCECOUNTS(21) {

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.nasa.jpf.jdwp.command.VirtualMachineCommand#execute(java.nio.ByteBuffer
     * , java.io.DataOutputStream,
     * gov.nasa.jpf.jdwp.command.CommandContextProvider)
     */
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int refTypesCount = bytes.getInt();
      List<ReferenceTypeId> referenceTypeIdList = new LinkedList<ReferenceTypeId>();

      if (refTypesCount < 0) {
        throw new IllegalArgumentException("Reference types count is less than zero.");
      }

      // write back the number of returned counts
      os.writeInt(refTypesCount);

      for (int i = 0; i < refTypesCount; ++i) {
        ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
        referenceTypeIdList.add(referenceTypeId);
        os.writeLong(VirtualMachineHelper.getInstancesCount(referenceTypeId.get(), contextProvider));
      }
    }

  }

  ;

  final static Logger logger = LoggerFactory.getLogger(VirtualMachineCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public VirtualMachineCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  private byte commandId;

  private VirtualMachineCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, VirtualMachineCommand> map = new ReverseEnumMap<Byte, VirtualMachineCommand>(
      VirtualMachineCommand.class);

}
