package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.DoubleValue;
import gov.nasa.jpf.jdwp.value.IntegerValue;
import gov.nasa.jpf.jdwp.value.LongValue;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.ShortValue;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
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
	
	/**
	 * The reference class for method invocation test.
	 * @author stepan
	 *
	 */
	static class MethodCallReferenceClass {
		
		private Integer field;
		private static Integer staticField;
		
		private Object object;
		private static Object staticObject;
		private static Throwable staticThrowable;
		
		MethodCallReferenceClass(Integer field) {
			this.field = field;
		}
		
		public Integer compute(Integer operand1, Integer operand2) {
			return field + staticField + operand1 / operand2;
		}
		
		public static Integer computeStatic(Integer operand1, Integer operand2) {
			return staticField + operand1 / operand2;
		}
		
		public double computePrimitive(int operandInt, double operandDouble, short operandShort, long operandLong) {
			return staticField + field + operandInt + operandDouble + operandShort + operandLong;
		}
	}
	
	JdwpVerifier verifyStaticMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			Value[] values = new Value[2];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[0]); 
			values[1] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[1]); 

			ClassInfo clazz = ClassLoaderInfo.getSystemResolvedClassInfo("gov.nasa.jpf.jdwp.VirtualMachineHelperTest$MethodCallReferenceClass");

			MethodInfo method = clazz.getMethod("computeStatic(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;", false);
			MethodResult mr = VirtualMachineHelper.invokeMethod(null, method, values, VM.getVM().getCurrentThread(), 0);
			
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			
			// pass the result through a well known field to the SuT so that it can be asserted there
			ObjectId integerId = JdwpObjectManager.getInstance().readObjectId(bb);
			clazz.getModifiableStaticElementInfo().setReferenceField("staticObject", integerId.get().getObjectRef());
			
			// no exception hence NullObject expected
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};

	@Test
	public void simpleStaticCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {

			// prepare and clear before the test
			MethodCallReferenceClass.staticField = 1111;
			MethodCallReferenceClass.staticObject = null;
			
			verifyStaticMethodCall.verify(new Integer(10), new Integer(2));
			
			// the resulting object is in MethodCallReferenceClass.staticObject by a convention
			assertEquals(1116, MethodCallReferenceClass.staticObject);
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
			MethodInfo method = clazz.getMethod("compute(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			
			// pass the result through a well known field to the SuT so that it can be asserted there
			ObjectId integerId = JdwpObjectManager.getInstance().readObjectId(bb);
			classInstance.setReferenceField("object", integerId.get().getObjectRef());
			
			// no exception hence NullObject expected
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};

	@Test
	public void simpleCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {

			// prepare and clear before the test
			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100);
			MethodCallReferenceClass.staticField = 1000;
			methodCallReferenceObject.object = null;
			
			verifyMethodCall.verify(methodCallReferenceObject, new Integer(10), new Integer(2));
			
			// the resulting object is in MethodCallReferenceClass.object by a convention
			assertEquals(1105, methodCallReferenceObject.object);
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
			MethodInfo method = clazz.getMethod("compute(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// pass the result through a well known field to the SuT so that it can be asserted there
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			ObjectId exceptionId = JdwpObjectManager.getInstance().readObjectId(bb);
			classInstance.setReferenceField("object", exceptionId.get().getObjectRef());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void exceptionCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {

			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100);
			MethodCallReferenceClass.staticField = 1000;
			methodCallReferenceObject.object = null;
			
			verifyMethodExceptionCall.verify(methodCallReferenceObject, new Integer(10), new Integer(0));
			
			// the thrown exception is in MethodCallReferenceClass.object by a convention
			assertTrue(methodCallReferenceObject.object instanceof ArithmeticException);
		}
	}
	
	JdwpVerifier verifyConstructorCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			Value[] values = new Value[1];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[0]); 

			ClassInfo clazz = ClassLoaderInfo.getSystemResolvedClassInfo("gov.nasa.jpf.jdwp.VirtualMachineHelperTest$MethodCallReferenceClass");
			MethodInfo method = clazz.getMethod("<init>(Ljava/lang/Integer;)V", false);
			
			MethodResult mr = VirtualMachineHelper.invokeConstructor(method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			
			// pass the result through a well known field to the SuT so that it can be asserted there
			ObjectId constructedObjectId = JdwpObjectManager.getInstance().readObjectId(bb);
			clazz.getModifiableStaticElementInfo().setReferenceField("staticObject", constructedObjectId.get().getObjectRef());
			
			// no exception hence NullObject expected
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void constructorCallTest() throws SecurityException, NoSuchFieldException {

		if (verifyNoPropertyViolation()) {

			// prepare and clear before the test
			MethodCallReferenceClass.staticObject = null;
			
			verifyConstructorCall.verify(new Integer(20));
			
			// the constructed object is in MethodCallReferenceClass.staticObject by a convention
			assertTrue(MethodCallReferenceClass.staticObject instanceof MethodCallReferenceClass);
			assertEquals(20, (int)((MethodCallReferenceClass)MethodCallReferenceClass.staticObject).field);
		}
	}
	
	JdwpVerifier verifyPrimitiveMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			Value[] values = new Value[4];
			
			//public double computePrimitive(int operandInt, double operandDouble, short operandShort, long operandLong) {
			values[0] = new IntegerValue(10);
			values[1] = new DoubleValue(100.1);
			values[2] = new ShortValue((short) 1);
			values[3] = new LongValue(1000000000000L);
			
			DynamicElementInfo classInstance = (DynamicElementInfo) passedObjects[0];
			ClassInfo clazz = classInstance.getClassInfo();
			MethodInfo method = clazz.getMethod("computePrimitive(IDSJ)D", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.DOUBLE.identifier().byteValue(), bb.get());
			// verify the primitive value result right in here since it's not that easy to pass primitives back to SuT
			assertEquals(1.0000001011111E12, bb.getDouble());
			
			// no exception hence NullObject expected
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void primitiveMethodCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {
			// prepare and clear before the test
			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100000);
			MethodCallReferenceClass.staticField = 1000;
			methodCallReferenceObject.object = null;
			
			verifyPrimitiveMethodCall.verify(methodCallReferenceObject);
			
			// all the assertions are done in the verifier
		}
	}
	
	JdwpVerifier verifyNativeMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			DynamicElementInfo classInstance = (DynamicElementInfo) passedObjects[0];
			
			// Prepare arguments
			Value[] values = new Value[0];
		
			ClassInfo clazz = classInstance.getClassInfo();
			MethodInfo method = clazz.getMethod("intern()Ljava/lang/String;", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			assertEquals(Tag.DOUBLE.identifier().byteValue(), bb.get());
			// verify the primitive value result right in here since it's not that easy to pass primitives back to SuT
			assertEquals(1.0000001011111E12, bb.getDouble());
			
			// no exception hence NullObject expected
			assertEquals(Tag.OBJECT.identifier().byteValue(), bb.get());
			assertEquals(0, bb.getLong());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void nativeMethodCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {
			// prepare and clear before the test
			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100000);
			MethodCallReferenceClass.staticField = 1000;
			methodCallReferenceObject.object = null;
			
			String string = "ahoj";
			
			verifyNativeMethodCall.verify(string, "oj");
			
			// all the assertions are done in the verifier
		}
	}
	
	JdwpVerifier verifyNativeStaticMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			Value[] values = new Value[1];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[0]); 
			
			DynamicElementInfo classReturnInstance = (DynamicElementInfo) passedObjects[1];
			ClassInfo returnClazz = classReturnInstance.getClassInfo();
		
			ClassInfo clazz = ClassLoaderInfo.getSystemResolvedClassInfo("java.lang.Class");
			MethodInfo method = clazz.getMethod("getPrimitiveClass(Ljava/lang/String;)Ljava/lang/Class;", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(null, method, values, VM.getVM().getCurrentThread(), 0);
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the buffer contains appropriate values
			 */
			
			// tag byte is checked in the SuT
			bb.get();
			
			// pass the result through a well known field to the SuT so that it can be asserted there
			ObjectId objectId = JdwpObjectManager.getInstance().readObjectId(bb);
			ElementInfo elementInfo = objectId.get();
			returnClazz.getModifiableStaticElementInfo().setReferenceField("staticObject", elementInfo == null ? -1 : elementInfo.getObjectRef());
						
			// tag byte is checked in the SuT
			bb.get();
			objectId = JdwpObjectManager.getInstance().readObjectId(bb);
			elementInfo = objectId.get();
			returnClazz.getModifiableStaticElementInfo().setReferenceField("staticThrowable", elementInfo == null ? -1 : elementInfo.getObjectRef());
			
			// verify there is nothing else left in the buffer
			assertEquals(0, bb.remaining());
		}

	};
	
	@Test
	public void nativeStaticMethodCallTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {
			// prepare and clear before the test
			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass(100000);
			
			MethodCallReferenceClass.staticObject = null;
			MethodCallReferenceClass.staticThrowable = new Exception();
			
			verifyNativeStaticMethodCall.verify("long", methodCallReferenceObject);
			
			assertTrue(MethodCallReferenceClass.staticObject instanceof Class);
			assertEquals("long", ((Class<?>)MethodCallReferenceClass.staticObject).getName());
			assertEquals(null, MethodCallReferenceClass.staticThrowable);
			
			MethodCallReferenceClass.staticObject = new Object();
			MethodCallReferenceClass.staticThrowable = null;
			
			verifyNativeStaticMethodCall.verify("nonexistent", methodCallReferenceObject);
			
			assertEquals(null, MethodCallReferenceClass.staticObject);
			assertTrue(MethodCallReferenceClass.staticThrowable instanceof ClassNotFoundException);
		}
	}

}
