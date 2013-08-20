package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectReferenceCommandTest extends TestJdwp {

	/**
	 * This is the reference class we query in this test set.
	 * 
	 * @author stepan
	 * 
	 */
	public static class ObjectReferenceTestReferenceClass {
		Object instanceObject = new Object();
		String instanceString = "test";
		
		void method() {
			Object[] variableArray = new Object[] {"StringValue3", null, null, new Integer(3), null};
		}
	}
	
	public static abstract class ObjectReferenceVerifier extends JdwpVerifier {

		private ObjectReferenceCommand command;
		protected Object[] passedObjects;
		
		ObjectReferenceVerifier(ObjectReferenceCommand command) {
			this.command = command;
		}
		
		protected ObjectId toObjectId(int i) {
			return JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[i]);
		}
		protected FieldId toFieldId(int i) {
			String fieldString = passedObjectAs(i, ElementInfo.class).asString();
			FieldInfo fieldInfo = ((DynamicElementInfo)passedObjects[0]).getFieldInfo(fieldString);
			return JdwpObjectManager.getInstance().getFieldId(fieldInfo);
		}
		
		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
			this.passedObjects = passedObjects; 
			ObjectId objectId = toObjectId(0);
			
			prepareInput();
			
			command.execute(objectId, this.bytes, dataOutputStream, contextProvider);
			
		}

		abstract protected void prepareInput() throws IOException;
		
		@SuppressWarnings("unchecked")
		protected <T> T passedObjectAs(int i, Class<T> clazz) {
			return (T)passedObjects[i];
		}
		
		protected void prepareIdentifier(Identifier<?> identifier) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			identifier.write(dos);
			
			bytes.put(baos.toByteArray());
			
			baos.close();
			dos.close();
		}
		
		void prepareUntaggedValue(ObjectId objectId) throws IOException {
			prepareIdentifier(objectId);
		}
		
	}
	
	ObjectReferenceVerifier objectReferenceVerifier = new ObjectReferenceVerifier(ObjectReferenceCommand.SETVALUES) {

		@Override
		protected void prepareInput() throws IOException {
			bytes.putInt(1);
			prepareIdentifier(toFieldId(1));
			prepareUntaggedValue(toObjectId(2));
		}

		
		
	};
	
	Logger logger = LoggerFactory.getLogger(ObjectReferenceTestReferenceClass.class);
	
	@Test
	public void setValuesTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {
			// prepare and clear before the test
			ObjectReferenceTestReferenceClass objectRefClass = new ObjectReferenceTestReferenceClass();
			String string = "test";
			
			objectReferenceVerifier.verify(objectRefClass, "instanceObject", string);
			
			assertEquals(string, objectRefClass.instanceObject);
		}
	}

}
