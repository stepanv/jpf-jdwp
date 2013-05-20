package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public enum ClassLoaderReferenceCommand implements Command, ConvertibleEnum<Byte, ClassLoaderReferenceCommand> {
	VISIBLECLASSES(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			JdwpObjectManager objectManager = contextProvider.getObjectManager();
			ClassLoaderId classLoaderId = objectManager.readClassLoaderId(bytes);
			ClassLoaderInfo classLoaderInfo = classLoaderId.getInfoObject();
			
			Iterator<ClassInfo> loadedClasses  = classLoaderInfo.iterator();
			
			// we need to write the number of classes first
			ByteArrayOutputStream loadedClassesOutputBytes = new ByteArrayOutputStream(0);
			DataOutputStream loadedClassesOS = new DataOutputStream (loadedClassesOutputBytes);
			
			int classes = 0;
			
			for (ClassInfo classInfo = loadedClasses.next(); loadedClasses.hasNext(); ) {
				ReferenceTypeId referenceTypeId = objectManager.getReferenceTypeId(classInfo);
				referenceTypeId.writeTagged(loadedClassesOS);
				++classes; // increase the number of classes
			}
			os.writeInt(classes);
			os.write(loadedClassesOutputBytes.toByteArray());

		}
	};
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