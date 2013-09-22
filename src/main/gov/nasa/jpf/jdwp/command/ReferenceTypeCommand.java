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

import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.AbsentInformationException;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.NotImplementedException;
import gov.nasa.jpf.jdwp.exception.id.InvalidFieldIdException;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jvm.ClassFile;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ReferenceTypeCommand} enum class implements the
 * {@link CommandSet#REFERENCETYPE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ReferenceType"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ReferenceType</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ReferenceTypeCommand implements Command, ConvertibleEnum<Byte, ReferenceTypeCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the JNI signature of a reference type. JNI signature formats are
   * described in the <a
   * href="http://java.sun.com/products/jdk/1.2/docs/guide/jni/index.html">Java
   * Native Inteface Specification</a>.<br/>
   * For primitive classes the returned signature is the signature of the
   * corresponding primitive type; for example, "I" is returned as the signature
   * of the class represented by {@link java.lang.Integer#TYPE}.
   * </p>
   */
  SIGNATURE(1) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeSignature(classInfo, bytes, os, contextProvider, false);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the instance of {@link java.lang.ClassLoader} which loaded a given
   * reference type. If the reference type was loaded by the system class
   * loader, the returned object ID is <code>null</code>.
   * </p>
   */
  CLASSLOADER(2) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      ClassLoaderInfo classLoaderInfo = classInfo.getClassLoaderInfo();

      logger.debug("class info: {} has class loader: {}", classInfo, classLoaderInfo);
      
      if (classLoaderInfo.isSystemClassLoader()) {
        // the null object is for the system classloader
        NullObjectId.instantWrite(os);
      } else {
        ObjectId objectId = contextProvider.getObjectManager().getClassLoaderId(classLoaderInfo);
        objectId.write(os);
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the modifiers (also known as access flags) for a reference type.
   * The returned bit mask contains information on the declaration of the
   * reference type. If the reference type is an array or a primitive class (for
   * example, {@link java.lang.Integer#TYPE}), the value of the returned bit
   * mask is undefined.
   * </p>
   */
  MODIFIERS(3) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      os.writeInt(classInfo.getModifiers());

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns information for each field in a reference type. Inherited fields
   * are not included. The field list will include any synthetic fields created
   * by the compiler. Fields are returned in the order they occur in the class
   * file.
   * </p>
   */
  FIELDS(4) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeFields(classInfo, bytes, os, contextProvider, false);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns information for each method in a reference type. Inherited methods
   * are not included. The list of methods will include constructors (identified
   * with the name <tt>&lt;init&gt;</tt>), the initialization method (identified
   * with the name <tt>&lt;clinit&gt;</tt>) if present, and any synthetic
   * methods created by the compiler. Methods are returned in the order they
   * occur in the class file.
   * </p>
   */
  METHODS(5) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeMethods(classInfo, bytes, os, contextProvider, false);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the value of one or more static fields of the reference type. Each
   * field must be member of the reference type or one of its superclasses,
   * superinterfaces, or implemented interfaces. Access control is not enforced;
   * for example, the values of private fields can be obtained.
   * </p>
   */
  GETVALUES(6) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int fields = bytes.getInt();
      os.writeInt(fields);

      for (int i = 0; i < fields; ++i) {
        FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
        FieldInfo fieldInfo = fieldId.get();

        ClassInfo fieldClassInfo = fieldInfo.getClassInfo();
        if (!classInfo.isInstanceOf(fieldClassInfo)) {
          // this is here just for completeness
          // it's not required since fieldId doesn't need classInfo to resolve
          throw new InvalidFieldIdException(fieldId);
        }

        Fields fieldss = fieldClassInfo.getStaticElementInfo().getFields();
        Object object = fieldInfo.getValueObject(fieldss);
        Value val = Tag.classInfoToTag(fieldInfo.getTypeClassInfo()).value(object);
        val.writeTagged(os);
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the name of source file in which a reference type was declared.
   * </p>
   */
  SOURCEFILE(7) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      String sourceFileName = classInfo.getSourceFileName();
      if (sourceFileName == null) {
        throw new AbsentInformationException(classInfo + " has unknown source.");
      }
      JdwpString.write(SOURCEFILENAME_FIX_PATTERN.matcher(sourceFileName).replaceFirst(""), os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification
   * <h2>
   * Returns the classes and interfaces directly nested within this type.Types
   * further nested within those types are not included.
   * </p>
   */
  NESTEDTYPES(8) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      // we need to write the number of nested classes and interfaces first
      ByteArrayOutputStream nestedClassesOutputBytes = new ByteArrayOutputStream(0);
      DataOutputStream nestedClassesOS = new DataOutputStream(nestedClassesOutputBytes);

      int classes = 0;

      for (ClassInfo directInnerClass : classInfo.getInnerClassInfos()) {
        ReferenceTypeId refType = contextProvider.getObjectManager().getReferenceTypeId(directInnerClass);
        refType.writeTagged(nestedClassesOS);
        classes++;
      }

      os.writeInt(classes);
      os.write(nestedClassesOutputBytes.toByteArray());
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the current status of the reference type. The status indicates the
   * extent to which the reference type has been initialized, as described in
   * the VM specification. If the class is linked the PREPARED and VERIFIED bits
   * in the returned status bits will be set. If the class is initialized the
   * INITIALIZED bit in the returned status bits will be set. If an error
   * occurred during initialization then the ERROR bit in the returned status
   * bits will be set. The returned status bits are undefined for array types
   * and for primitive classes (such as java.lang.Integer.TYPE).
   * </p>
   */
  STATUS(9) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      os.writeInt(ClassStatus.classStatus(classInfo));
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the interfaces declared as implemented by this class. Interfaces
   * indirectly implemented (extended by the implemented interface or
   * implemented by a superclass) are not included.
   * </p>
   */
  INTERFACES(10) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      Set<ClassInfo> interfaces = classInfo.getInterfaceClassInfos();
      os.writeInt(interfaces.size());
      for (Iterator<ClassInfo> i = interfaces.iterator(); i.hasNext();) {
        ClassInfo interfaceClass = i.next();
        ReferenceTypeId intId = contextProvider.getObjectManager().getReferenceTypeId(interfaceClass);
        logger.debug("Found interface: {}", intId);
        intId.write(os);
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the class object corresponding to this type.
   * </p>
   * <p>
   * This command is used when inspecting an array in Eclipse.<br/>
   * It is also used when invoking a method of an object instance.
   * </p>
   * <p>
   * For a reverse operation refer too
   * {@link ClassObjectReferenceCommand#REFLECTEDTYPE}
   * </p>
   */
  CLASSOBJECT(11) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      ClassObjectId clazzObjectId = contextProvider.getObjectManager().getClassObjectId(classInfo);
      clazzObjectId.write(os);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the value of the SourceDebugExtension attribute. <br/>
   * Requires {@link CapabilitiesNew#CAN_GET_SOURCE_DEBUG_EXTENSION} capability.
   * </p>
   * 
   * @since JDWP version 1.4.
   * 
   */
  SOURCEDEBUGEXTENSION(12) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // is ok as far as the associated capability is false.
      throw new NotImplementedException();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the JNI signature of a reference type along with the generic
   * signature if there is one. Generic signatures are described in the
   * signature attribute section in the <a
   * href="http://java.sun.com/docs/books/vmspec">Java Virtual Machine
   * Specification, 3rd Edition</a>.
   * </p>
   * 
   * @since JDWP version 1.5
   */
  SIGNATUREWITHGENERIC(13) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeSignature(classInfo, bytes, os, contextProvider, true);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns information, including the generic signature if any, for each field
   * in a reference type. Inherited fields are not included. The field list will
   * include any synthetic fields created by the compiler. Fields are returned
   * in the order they occur in the class file. Generic signatures are described
   * in the signature attribute section in the <a
   * href="http://java.sun.com/docs/books/vmspec">Java Virtual Machine
   * Specification, 3rd Edition</a>.
   * </p>
   * 
   * @since JDWP version 1.5
   */
  FIELDSWITHGENERIC(14) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeFields(classInfo, bytes, os, contextProvider, true);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns information, including the generic signature if any, for each
   * method in a reference type. Inherited methods are not included. The list of
   * methods will include constructors (identified with the name
   * <tt>&lt;init&gt;</tt>), the initialization method (identified with the name
   * <tt>&lt;clinit&gt;</tt>) if present, and any synthetic methods created by
   * the compiler. Methods are returned in the order they occur in the class
   * file. Generic signatures are described in the signature attribute section
   * in the <a href="http://java.sun.com/docs/books/vmspec">Java Virtual Machine
   * Specification, 3rd Edition</a>.
   * </p>
   * 
   * @since JDWP version 1.5
   */
  METHODSWITHGENERIC(15) {
    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      executeMethods(classInfo, bytes, os, contextProvider, true);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns instances of this reference type. Only instances that are reachable
   * for the purposes of garbage collection are returned. <br/>
   * Requires {@link CapabilitiesNew#CAN_GET_INSTANCE_INFO} capability.
   * </p>
   * 
   * @since JDWP version 1.6.
   */
  INSTANCES(16) {

    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int maxInstances = bytes.getInt();

      if (maxInstances < 0) {
        throw new IllegalArgumentException("Max instances cannot be negative: " + maxInstances);
      }

      List<ObjectId> allInstancesFound = VirtualMachineHelper.getInstances(classInfo, maxInstances, contextProvider);

      os.writeInt(allInstancesFound.size());
      for (ObjectId instanceObjectId : allInstancesFound) {
        instanceObjectId.writeTagged(os);
      }

    }

  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the class file major and minor version numbers, as defined in the
   * class file format of the Java Virtual Machine specification.
   * </p>
   * 
   * @since JDWP version 1.6.
   * 
   * @see ClassFile#parse(gov.nasa.jpf.jvm.ClassFileReader)
   */
  CLASSFILEVERSION(17) {

    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // JPF throws minor and major version of classfile away and so we
      throw new AbsentInformationException("Class file version not available for : " + classInfo);
    }

  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Return the raw bytes of the constant pool in the format of the
   * constant_pool item of the Class File Format in the Java Virtual Machine
   * Specification.
   * </p>
   * Requires {@link CapabilitiesNew#CAN_GET_CONSTANT_POOL} capability.
   * 
   * @since JDWP version 1.6.
   */
  CONSTANTPOOL(18) {

    @Override
    protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // is ok as far as the associated capability is false
      throw new NotImplementedException();

    }

  };

  final static Logger logger = LoggerFactory.getLogger(ReferenceTypeCommand.class);

  private byte commandId;

  private ReferenceTypeCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ReferenceTypeCommand> map = new ReverseEnumMap<Byte, ReferenceTypeCommand>(ReferenceTypeCommand.class);
  /**
   * JPF stores sources with it's path but we need to return only the name
   * itself.
   */
  private static final Pattern SOURCEFILENAME_FIX_PATTERN = Pattern.compile("^.*[/\\\\]");

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ReferenceTypeCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  /**
   * The {@link ReferenceTypeCommand} specific extension of command execution.
   * 
   * @param classInfo
   *          The class all the reference commands are associated with.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  protected abstract void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
    logger.debug("ReferenceType: {}", refId.get());
    execute(refId.get(), bytes, os, contextProvider);
  }

  /**
   * Execute signature like command.<br/>
   * This is an aggregator of similar implementation.
   * 
   * @param classInfo
   *          The class all the reference commands are associated with.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @param withGeneric
   *          Whether to execution should include generics.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  protected void executeSignature(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider,
                                  boolean withGeneric) throws IOException, JdwpException {
    JdwpString.write(classInfo.getSignature(), os);
    if (withGeneric) {
      JdwpString.writeNullAsEmpty(classInfo.getGenericSignature(), os);
    }
  }

  /**
   * Execute fields like command.<br/>
   * This is an aggregator of similar implementation.
   * 
   * @param fields
   *          The fields to write.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @param withGeneric
   *          Whether to execution should include generics.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  private void writeFields(FieldInfo[] fields, DataOutputStream os, CommandContextProvider contextProvider, boolean withGeneric) throws IOException {
    for (int i = 0; i < fields.length; i++) {
      FieldInfo field = fields[i];
      FieldId fieldId = contextProvider.getObjectManager().getFieldId(field);
      fieldId.write(os);
      JdwpString.write(field.getName(), os);
      JdwpString.write(field.getSignature(), os);
      if (withGeneric) {
        JdwpString.writeNullAsEmpty(field.getGenericSignature(), os);
      }
      logger.debug("Field: {}, signature: {}, generic signature: {}, fieldId: {}", field.getName(), field.getSignature(),
                   field.getGenericSignature(), fieldId);
      os.writeInt(field.getModifiers());
    }
  }

  /**
   * Execute fields like command.<br/>
   * This is an aggregator of similar implementation.
   * 
   * @param classInfo
   *          The class all the reference commands are associated with.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @param withGeneric
   *          Whether to execution should include generics.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  protected void executeFields(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider,
                               boolean withGeneric) throws IOException, JdwpException {
    FieldInfo[] instanceFields = classInfo.getDeclaredInstanceFields();
    FieldInfo[] staticFields = classInfo.getDeclaredStaticFields();
    os.writeInt(instanceFields.length + staticFields.length);

    writeFields(instanceFields, os, contextProvider, withGeneric);
    writeFields(staticFields, os, contextProvider, withGeneric);
  }

  /**
   * Execute method like command.<br/>
   * This is an aggregator of similar implementation.
   * 
   * @param classInfo
   *          The class all the reference commands are associated with.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @param withGeneric
   *          Whether to execution should include generics.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  protected void executeMethods(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider,
                                boolean withGeneric) throws IOException, JdwpException {
    MethodInfo[] methods = classInfo.getDeclaredMethodInfos();
    os.writeInt(methods.length);
    for (int i = 0; i < methods.length; i++) {
      MethodInfo method = methods[i];
      os.writeLong(method.getGlobalId());

      JdwpString.write(method.getName(), os);
      JdwpString.write(method.getSignature(), os);

      if (withGeneric) {
        JdwpString.writeNullAsEmpty(method.getGenericSignature(), os);
      }

      os.writeInt(method.getModifiers());

      logger.debug("Method: '{}', signature: {}, generic signature: {} (global id: {})", method.getName(), method.getSignature(),
                   method.getGenericSignature(), method.getGlobalId());
    }
  }

}
