package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ClassLoaderReferenceCommand implements Command, ConvertibleEnum<Byte, ClassLoaderReferenceCommand> {
  VISIBLECLASSES(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      JdwpObjectManager objectManager = contextProvider.getObjectManager();

      // TODO maybe this will throw classcast exception ...
      // has to be tested with system class loader!
      ClassLoaderId classLoaderId = objectManager.readClassLoaderId(bytes);
      ClassLoaderInfo classLoaderInfo = classLoaderId.getInfoObject();

      if (classLoaderInfo == null) {
        // system class loader
        classLoaderInfo = ThreadInfo.getCurrentThread().getSystemClassLoaderInfo();
      }

      logger.debug("Using classloader: {}", classLoaderInfo);

      ;

      // we need to write the number of classes first
      ByteArrayOutputStream loadedClassesOutputBytes = new ByteArrayOutputStream(0);
      DataOutputStream loadedClassesOS = new DataOutputStream(loadedClassesOutputBytes);

      int classes = 0;

      for (Iterator<ClassInfo> loadedClasses = classLoaderInfo.iterator(); loadedClasses.hasNext();) {
        ClassInfo classInfo = loadedClasses.next();
        // get the reference type for the class
        ReferenceTypeId referenceTypeId = objectManager.getReferenceTypeId(classInfo);
        referenceTypeId.writeTagged(loadedClassesOS);

        logger.debug("Class found: {}", referenceTypeId);

        // increase the number of classes
        ++classes;
      }
      logger.trace("About to send classes (number: {}) as bytes: {}", classes, loadedClassesOutputBytes.toByteArray());

      os.writeInt(classes);
      os.write(loadedClassesOutputBytes.toByteArray());

      logger.trace("Command end");

    }
  };

  final static Logger logger = LoggerFactory.getLogger(ClassLoaderReferenceCommand.class);

  private byte commandId;

  private ClassLoaderReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ClassLoaderReferenceCommand> map = new ReverseEnumMap<Byte, ClassLoaderReferenceCommand>(
      ClassLoaderReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ClassLoaderReferenceCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}