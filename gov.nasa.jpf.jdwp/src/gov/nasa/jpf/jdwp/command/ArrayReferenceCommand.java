package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayReferenceCommand implements Command, ConvertibleEnum<Byte, ArrayReferenceCommand> {
	LENGTH(1) {
		@Override
		public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			os.writeInt(array.arrayLength());
		}
	}, GETVALUES(2) {
		@Override
		public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			   int first = bytes.getInt();
			    int length = bytes.getInt();

			    // We need to write out the byte signifying the type of array first
			    ClassInfo componentClassInfo = array.getClassInfo().getComponentClassInfo();
			    
			    os.writeByte(Tag.classInfoToTag(componentClassInfo).identifier());

			    // write the number of values we send back
			    os.writeInt(length);
			    
			    // Uugh, this is a little ugly but it's the only time we deal with
			    // arrayregions
//			    if (componentClassInfo == byte.class)
//			      os.writeByte(JdwpConstants.Tag.BYTE);
//			    else if (componentClassInfo == char.class)
//			      os.writeByte(JdwpConstants.Tag.CHAR);
//			    else if (componentClassInfo == float.class)
//			      os.writeByte(JdwpConstants.Tag.FLOAT);
//			    else if (componentClassInfo == double.class)
//			      os.writeByte(JdwpConstants.Tag.DOUBLE);
//			    else if (componentClassInfo == int.class)
//			      os.writeByte(JdwpConstants.Tag.BYTE);
//			    else if (componentClassInfo == long.class)
//			      os.writeByte(JdwpConstants.Tag.LONG);
//			    else if (componentClassInfo == short.class)
//			      os.writeByte(JdwpConstants.Tag.SHORT);
//			    else if (componentClassInfo == void.class)
//			      os.writeByte(JdwpConstants.Tag.VOID);
//			    else if (componentClassInfo == boolean.class)
//			      os.writeByte(JdwpConstants.Tag.BOOLEAN);
//			    else if (componentClassInfo.isArray())
//			      os.writeByte(JdwpConstants.Tag.ARRAY);
//			    else if (String.class.isAssignableFrom(componentClassInfo))
//			      os.writeByte(JdwpConstants.Tag.STRING);
//			    else if (Thread.class.isAssignableFrom(componentClassInfo))
//			      os.writeByte(JdwpConstants.Tag.THREAD);
//			    else if (ThreadGroup.class.isAssignableFrom(componentClassInfo))
//			      os.writeByte(JdwpConstants.Tag.THREAD_GROUP);
//			    else if (ClassLoader.class.isAssignableFrom(componentClassInfo))
//			      os.writeByte(JdwpConstants.Tag.CLASS_LOADER);
//			    else if (Class.class.isAssignableFrom(componentClassInfo))
//			      os.writeByte(JdwpConstants.Tag.CLASS_OBJECT);
//			    else
//			      os.writeByte(JdwpConstants.Tag.OBJECT);

			    // Write all the values, primitives should be untagged and Objects must be
			    // tagged
			    
			    for (int i = first; i < first + length; i++)
			      {
			    	Value value = null;
			    	if (!componentClassInfo.isPrimitive()) {
			    		ElementInfo ei = VM.getVM().getHeap().get(array.getReferenceElement(i));
			    		value = JdwpObjectManager.getInstance().getObjectId(ei);
			    		value.writeTagged(os);
			    	} else {
			    		value = Tag.arrayFieldToValue(array.getFields(), i);
			    		value.write(os);
			        }
			      }
			
		}
	}, SETVALUES(3) {
		@Override
		public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {

			
		}
	};

	private byte commandId;

	private ArrayReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ArrayReferenceCommand> map = new ReverseEnumMap<Byte, ArrayReferenceCommand>(ArrayReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ArrayReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	public abstract void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
	
	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		 ArrayId arrayId = contextProvider.getObjectManager().readArrayId(bytes);
		    execute(arrayId.get(), bytes, os, contextProvider);
		
	}
}