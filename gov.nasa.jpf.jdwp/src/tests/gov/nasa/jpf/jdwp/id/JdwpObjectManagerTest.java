package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.InvalidFieldId;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests JDWP Object Manager for its correctness.
 * 
 * @author stepan
 * 
 */
public class JdwpObjectManagerTest extends TestJdwp {

	public static void main(String[] args) throws SecurityException, NoSuchFieldException {
		runTestsOfThisClass(args);
	}

	private void forceGc() {
		List<WeakReference<Object>> weakRefList = new ArrayList<WeakReference<Object>>();
		weakRefList.add(new WeakReference<Object>(new Object()));
		while (weakRefList.get(0).get() != null) {
			weakRefList.add(new WeakReference<Object>(new Object()));
		}
	}

	FieldId fieldId;
	WeakReference<FieldInfo> weaklyReferencedFieldInfo;

	private static class FieldInfoStub extends FieldInfo {

		protected FieldInfoStub(String name, String signature, int modifiers) {
			super(name, signature, modifiers);
		}

		@Override
		public String valueToString(Fields f) {
			return null;
		}

		@Override
		public void initialize(ElementInfo ei, ThreadInfo ti) {
		}

		@Override
		public Object getValueObject(Fields data) {
			return null;
		}
	}

	private void storeElements() {
		FieldInfo storedFieldInfo = new FieldInfoStub("", "", 0);
		FieldInfo weaklyStoredFieldInfo = new FieldInfoStub("", "", 0);

		fieldId = JdwpObjectManager.getInstance().getFieldId(storedFieldInfo);
		weaklyReferencedFieldInfo = new WeakReference<FieldInfo>(weaklyStoredFieldInfo);
	}

	/**
	 * Simple test whether JDWP Object Identifier Manager let GC to collect the
	 * objects it manages the identifiers for.
	 * 
	 * @throws InvalidIdentifier
	 */
	@Test(expected = InvalidFieldId.class)
	public void simpleNoIdentifierLeakingTest() throws InvalidIdentifier {
		storeElements();

		forceGc();

		assertNull("This might be rather programmer failure", weaklyReferencedFieldInfo.get());
		assertTrue("Memory leak", fieldId.isNull());
		fieldId.get();
	}
	
	private WeakReference<StackFrame> weaklyStoredFrame;
	private FrameId testedFrameId;
	private long testedFrameIdAsLong;
	private long testedFrameIdStoredAsLongOnly;
	private JdwpVerifier storeFrameVerifier = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
			List<StackFrame> frames = VirtualMachineHelper.getFrames(contextProvider.getVM().getCurrentThread(), 0, -1);

			StackFrame nullFrame = frames.get(0);
			StackFrame firstFrame = frames.get(1);
			StackFrame secondFrame = frames.get(2);

			assertEquals("verify", nullFrame.getMethodName());
			assertEquals("methodXyDGe3sSBBWithSpecialId", firstFrame.getMethodName());
			assertEquals("methodXyd34itSjfWithSpecialId", secondFrame.getMethodName());

			testedFrameId = contextProvider.getObjectManager().getFrameId(secondFrame);
			FrameId onlyAsIdStoredFrameId = contextProvider.getObjectManager().getFrameId(firstFrame);
			weaklyStoredFrame = new WeakReference<StackFrame>(nullFrame);
			
			testedFrameId.write(dataOutputStream);
			onlyAsIdStoredFrameId.write(dataOutputStream);
			
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
			testedFrameIdAsLong = bb.getLong();
			testedFrameIdStoredAsLongOnly = bb.getLong();
			
			FrameId frameId;
			bb.rewind();
			frameId = contextProvider.getObjectManager().readFrameId(bb);
			assertTrue(frameId == testedFrameId);
			assertEquals(frameId.get(), testedFrameId.get());
			
			frameId = contextProvider.getObjectManager().readFrameId(bb);
			assertTrue(frameId == onlyAsIdStoredFrameId);
			assertEquals(frameId.get(), onlyAsIdStoredFrameId.get());
			
		}
	};
	
	/**
	 * An assertion helper class for thrown exceptions.<br/>
	 * 
	 * Typical usage:
	 * <pre>
	 * new AssertExceptionThrown() {
	 *     {@literal @}Override
	 *     public void execute() throws Exception {
	 *         /{@literal *} a code that throws an exception {@literal *}/
	 *     }
	 * }.doAssert("Assertion message", InvalidIdentifier.class);
	 * </pre>
	 * 
	 * @author stepan
	 *
	 */
	private abstract class AssertExceptionThrown {
		public abstract void execute() throws Exception;
		
		public void doAssert(String message, Class<? extends Exception> exceptionClass) {
			Exception e = null;
			try {
				execute();
			} catch (Exception ee) {
				e = ee;
			}
			assertTrue(message, exceptionClass.isInstance(e));
		}
	}
	
	private JdwpVerifier frameGcedVerifier = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
			if (weaklyStoredFrame.get() != null) {
				// force GC
				forceGc();
				// GC done
			}

			/*
			 * If objects in this test don't assert to be null it doesn't necessarily mean there is a real problem.
			 * Following aspects need to be considered:
			 * [1] Does your JVM do GC as it is expected in this test?
			 * [2] Is it possible the objects were pinned down by the JVM because of a debugger request
			 *     (Eclipse 4.2 debugger can prevent JDK1.7 from garbage collecting of these objects)
			 * [3] And maybe other aspects 
			 */
			assertTrue("Java VM should have removed this frame if not JPF implementation probably changed.", weaklyStoredFrame.get() == null);
			assertTrue("Memory leak detected", testedFrameId.isNull());

			// also testing the exception itself
			new AssertExceptionThrown() {
				@Override
				public void execute() throws Exception {
					testedFrameId.get();
				}
			}.doAssert("If the exception wasn't thrown a potential memory leak is detected.", InvalidIdentifier.class);
			
			// Now, we also want to test other related objects
			dataOutputStream.writeLong(testedFrameIdAsLong);
			dataOutputStream.writeLong(testedFrameIdStoredAsLongOnly);
			final ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());
			
			FrameId frameId;
			bb.rewind();
			frameId = contextProvider.getObjectManager().readFrameId(bb);
			assertTrue(frameId == testedFrameId);
			
			
			// TODO this should throw an exception otherwise the FrameId is leaking
			new AssertExceptionThrown() {
				@Override
				public void execute() throws Exception {
					contextProvider.getObjectManager().readFrameId(bb);
				}
			}.doAssert("If the exception wasn't thrown a potential memory leak is detected.", InvalidIdentifier.class);
		}
	};

	private class ReferenceClass {

		void methodXyDGe3sSBBWithSpecialId() {
			storeFrameVerifier.verify();
		}

		void methodXyd34itSjfWithSpecialId() {
			methodXyDGe3sSBBWithSpecialId();
		}
	}

	/**
	 * Way more complicated test of JDWP Object manager correctness.<br/>
	 * If this test fails but the simple one
	 * {@link JdwpObjectManagerTest#simpleNoIdentifierLeakingTest()} doesn't
	 * then it likely to be incorrect test rather than an error in JPF of in the
	 * JDWP back-end.
	 */
	@Test
	public void runtimeNoIdentifierLeakingTest() {
		if (verifyNoPropertyViolation()) {
			ReferenceClass referenceClass = new ReferenceClass();
			referenceClass.methodXyd34itSjfWithSpecialId();
			// now we're running further
			frameGcedVerifier.verify();
		}
	}
}
