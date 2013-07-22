package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Test class for VirtualMachine Helper class.
 * 
 * Currently, basics of method invocations are tested.
 * @author stepan
 *
 */
public class VirtualMachineHelperTest  extends TestJdwp {

	public VirtualMachineHelperTest() {
	}
	
	static class MethodCallReferenceClass {
		
		private Integer field;
		private static Integer staticField;
		
		private Object object;
		
		MethodCallReferenceClass(Integer field) {
			this.field = field;
		}
		
		public int compute(Integer operand1, Integer operand2) {
			return field + staticField + operand1 / operand2;
		}
		
		public static int computeStatic(Integer operand1, Integer operand2) {
			return staticField + operand1 / operand2;
		}
	}
	
	JdwpVerifier verifyMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			DynamicElementInfo classInstance = (DynamicElementInfo) passedObjects[0];
			Value[] values = new Value[2];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[1]); 
			values[1] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[2]); 

			ClassInfo clazz = classInstance.getClassInfo();

			MethodInfo method = clazz.getMethod("compute(Ljava/lang/Integer;Ljava/lang/Integer;)I", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the header contains appropriate values
			 */
			assertEquals(Tag.INT.identifier().byteValue(), bb.get());
			assertEquals(1105, bb.getInt());
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			assertEquals(0, bb.remaining());
			
			reset();
			
			method = clazz.getMethod("computeStatic(Ljava/lang/Integer;Ljava/lang/Integer;)I", false);
			
			mr = VirtualMachineHelper.invokeMethod(null, method, values, VM.getVM().getCurrentThread(), 0);
			
			mr.write(dataOutputStream);
			
			// verify the results
			bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the header contains appropriate values
			 */
			assertEquals(Tag.INT.identifier().byteValue(), bb.get());
			assertEquals(1005, bb.getInt());
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			assertEquals(0, bb.remaining());
		}

	};

	@Test
	public void simpleCallTest() throws SecurityException, NoSuchFieldException {
		if (!isJPFRun()) {
			initialize(verifyMethodCall);
		}

		if (verifyNoPropertyViolation()) {

			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100);
			MethodCallReferenceClass.staticField = 1000;
			verifyMethodCall.verify(methodCallReferenceObject, new Integer(10), new Integer(2));
		}
	}
	
	JdwpVerifier verifyMethodExceptionCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			DynamicElementInfo classInstance = (DynamicElementInfo) passedObjects[0];
			Value[] values = new Value[2];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[1]); 
			values[1] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[2]); 

			ClassInfo clazz = classInstance.getClassInfo();

			MethodInfo method = clazz.getMethod("compute(Ljava/lang/Integer;Ljava/lang/Integer;)I", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the header contains appropriate values
			 */
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			ObjectId exceptionId = JdwpObjectManager.getInstance().readObjectId(bb);
			
			classInstance.setReferenceField("object", exceptionId.get().getObjectRef());
			
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void exceptionCallTest() throws SecurityException, NoSuchFieldException {
		if (!isJPFRun()) {
			initialize(verifyMethodExceptionCall);
		}

		if (verifyNoPropertyViolation()) {

			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100);
			MethodCallReferenceClass.staticField = 1000;
			methodCallReferenceObject.object = null;
			
			verifyMethodExceptionCall.verify(methodCallReferenceObject, new Integer(10), new Integer(0));
			assertTrue(methodCallReferenceObject.object instanceof ArithmeticException);
		}
	}

}
