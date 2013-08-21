package gov.nasa.jpf.jdwp.util.test;

import gov.nasa.jpf.jdwp.command.Command;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CommandVerifier extends JdwpVerifier {

	public static class ObjectWrapper<T> {
		public T wrappedObject;
	}

	private Command command;
	protected Object[] passedObjects;
	protected MJIEnv mjiEnv;

	public CommandVerifier(Command command) {
		this.command = command;
	}

	protected ObjectId loadObjectId(int i) {
		return JdwpObjectManager.getInstance().getObjectId((ElementInfo) passedObjects[i]);
	}

	@SuppressWarnings("unchecked")
	protected <T> T loadBoxObject(int i, Class<T> clazz) {
		return (T) ((ElementInfo) passedObjects[i]).asBoxObject();
	}

	protected FieldId toFieldId(ObjectId objectId, int i) {
		String fieldString = passedObjectAs(i, ElementInfo.class).asString();
		FieldInfo fieldInfo = objectId.get().getFieldInfo(fieldString);
		return JdwpObjectManager.getInstance().getFieldId(fieldInfo);
	}

	protected void storeToWrapper(int i, int objectReference) {
		DynamicElementInfo intResultElementInfo = (DynamicElementInfo) passedObjects[i];
		mjiEnv.setReferenceField(intResultElementInfo.getObjectRef(), "wrappedObject", objectReference);
	}

	protected void storeToArray(int i, int arrayIndex, int objectRef) {
		DynamicElementInfo arrayResultElementInfo = (DynamicElementInfo) passedObjects[i];
		mjiEnv.setReferenceArrayElement(arrayResultElementInfo.getObjectRef(), arrayIndex, objectRef);
	}

	@Override
	protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
		this.passedObjects = passedObjects;
		this.mjiEnv = VM.getVM().getCurrentThread().getMJIEnv();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream inputDataOutputStream = new DataOutputStream(baos);
		prepareInput(inputDataOutputStream);
		bytes = ByteBuffer.wrap(baos.toByteArray());

		command.execute(this.bytes, dataOutputStream, contextProvider);

		ByteBuffer outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());
		processOutput(outputBytes);

	}

	abstract protected void processOutput(ByteBuffer outputBytes);

	abstract protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException;

	@SuppressWarnings("unchecked")
	protected <T> T passedObjectAs(int i, Class<T> clazz) {
		return (T) passedObjects[i];
	}

	protected void prepareIdentifier(Identifier<?> identifier) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		identifier.write(dos);

		bytes.put(baos.toByteArray());

		baos.close();
		dos.close();
	}

	protected void prepareUntaggedValue(ObjectId objectId) throws IOException {
		prepareIdentifier(objectId);
	}

}