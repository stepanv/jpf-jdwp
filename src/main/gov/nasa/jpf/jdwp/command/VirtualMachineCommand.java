package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.VirtualMachine.Capabilities;
import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

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

public enum VirtualMachineCommand implements Command, ConvertibleEnum<Byte, VirtualMachineCommand> {

  VERSION(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      JdwpString.write(contextProvider.getJPF().getReporter().getJPFBanner(), os);
      os.writeInt(JdwpConstants.MINOR);
      os.writeInt(JdwpConstants.MAJOR);
      JdwpString.write(System.getProperty("java.version"), os);
      JdwpString.write(System.getProperty("java.vm.name"), os);
    }
  },
  CLASSESBYSIGNATURE(2) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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

        // TODO [for PJA] do we have class statuses in JPF?
        ClassStatus.VERIFIED.write(os);
      }

    }
  },
  ALLCLASSES(3) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      Collection classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
      os.writeInt(classes.size());

      Iterator iter = classes.iterator();
      while (iter.hasNext()) {
        ClassInfo clazz = (ClassInfo) iter.next();
        ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
        id.writeTagged(os);
        JdwpString.write(clazz.getSignature(), os);
        // TODO [for PJA] do we have class statuses in JPF?
        ClassStatus.VERIFIED.write(os);
      }
    }
  },
  ALLTHREADS(4) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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
   * Returns all thread groups that do not have a parent. This command may be
   * used as the first step in building a tree (or trees) of the existing thread
   * groups.
   */
  TOPLEVELTHREADGROUPS(5) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      List<ElementInfo> topLevelThreadGroups = new ArrayList<ElementInfo>();

      for (ThreadInfo threadInfo : VM.getVM().getLiveThreads()) {
        int group = threadInfo.getThreadGroupRef();
        ElementInfo threadGroupElementInfo = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(group);

        int parentref = threadGroupElementInfo.getReferenceField("parent");
        ElementInfo parent = contextProvider.getVM().getHeap().get(parentref);

        if (parent == null) {
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
  DISPOSE(6) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {

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
      // TODO do the simulation for all threads

      // Garbage collection is re-enabled in all cases where it was
      // ObjectReference
      contextProvider.getVirtualMachine().collectAllDisabledObjects();

      // TODO now, we need to get prepared for another connection .. this
      // might be a problem with singletons...
    }
  },
  IDSIZES(7) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
      os.writeInt(TaggableIdentifier.SIZE);
    }
  },
  /**
   * Suspends the execution of the application running in the target VM. All
   * Java threads currently running will be suspended. <br/>
   * Unlike java.lang.Thread.suspend, suspends of both the virtual machine and
   * individual threads are counted. Before a thread will run again, it must be
   * resumed through the VM-level resume command or the thread-level resume
   * command the same number of times it has been suspended.
   * 
   * TODO we don't count thread suspension count as stated above in the
   * specification
   */
  SUSPEND(8) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      contextProvider.getVirtualMachine().getExecutionManager().markVMSuspended();

    }
  },
  RESUME(9) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      contextProvider.getVirtualMachine().getExecutionManager().markVMResumed();
    }
  },

  /**
   * Terminates the target VM with the given exit code. On some platforms, the
   * exit code might be truncated, for example, to the low order 8 bits. All ids
   * previously returned from the target VM become invalid. Threads running in
   * the VM are abruptly terminated. A thread death exception is not thrown and
   * finally blocks are not run.
   */
  EXIT(10) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      int exitCode = bytes.getInt();

      contextProvider.getVirtualMachine().exit(exitCode);
    }
  },
  CREATESTRING(11) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      // is invoked when inspecting an array field for instance (TODO
      // rewrite this method)
      String string = JdwpString.read(bytes);

      // TODO [for PJA] which thread we should use?
      ElementInfo stringElementInfo = VM.getVM().getHeap().newInternString(string, VM.getVM().getCurrentThread());

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
  CAPABILITIES(12) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      os.writeBoolean(Capabilities.CAN_WATCH_FIELD_MODIFICATION);
      os.writeBoolean(Capabilities.CAN_WATCH_FIELD_ACCESS);
      os.writeBoolean(Capabilities.CAN_GET_BYTECODES);
      os.writeBoolean(Capabilities.CAN_GET_SYNTHETIC_ATTRIBUTE);
      os.writeBoolean(Capabilities.CAN_GET_OWNED_MONITOR_INFO);
      os.writeBoolean(Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR);
      os.writeBoolean(Capabilities.CAN_GET_MONITOR_INFO);
    }
  },
  CLASSPATHS(13) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  DISPOSEOBJECTS(14) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  HOLDEVENTS(15) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  RELEASEEVENTS(16) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  CAPABILITIESNEW(17) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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
  REDEFINECLASSES(18) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  SETDEFAULTSTRATUM(19) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      String stratumId = JdwpString.read(bytes);
      // TODO Finish this if possible
    }
  },
  ALLCLASSESWITHGENERIC(20) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      Collection<ClassInfo> classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
      os.writeInt(classes.size());

      Iterator<ClassInfo> iter = classes.iterator();
      while (iter.hasNext()) {
        ClassInfo clazz = iter.next();
        ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
        id.writeTagged(os);
        JdwpString.write(clazz.getSignature(), os);
        JdwpString.writeNullAsEmpty(clazz.getGenericSignature(), os);
        // TODO [for PJA] do we have class statuses in JPF?
        ClassStatus.VERIFIED.write(os);
      }

    }
  },

  /**
   * Returns the number of instances of each reference type in the input list.
   * Only instances that are reachable for the purposes of garbage collection
   * are counted. If a reference type is invalid, eg. it has been unloaded, zero
   * is returned for its instance count. <br/>
   * Requires {@link CapabilitiesNew#CAN_GET_INSTANCE_INFO}.
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
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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
  public VirtualMachineCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  private byte commandId;

  private VirtualMachineCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, VirtualMachineCommand> map = new ReverseEnumMap<Byte, VirtualMachineCommand>(
      VirtualMachineCommand.class);

  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;

}
