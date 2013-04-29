package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.util.MethodResult;
import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.UncaughtException;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassTypeCommand implements Command, ConvertibleEnum<Byte, ClassTypeCommand> {
	SUPERCLASS(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		    ClassInfo clazz = refId.get();
		    ClassInfo superClazz = clazz.getSuperClass();

		    if (superClazz == null) {
		        os.writeLong(0L);
		    } else {
		        ReferenceTypeId clazzId = contextProvider.getObjectManager().getReferenceTypeId(superClazz);
		        clazzId.write(os);
		    }

		}
	},
	SETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INVOKEMETHOD(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId refId = JdwpObjectManager.getInstance().readReferenceTypeId(bytes);
		    ClassInfo clazz = refId.get();

		    ThreadId tId = JdwpObjectManager.getInstance().readThreadId(bytes);
		    ThreadInfo thread = tId.get();

		    MethodInfo method = VirtualMachineHelper.getClassMethod(clazz, bytes.getLong());

		    int args = bytes.getInt();
		    Value[] values = new Value[args];

		    for (int i = 0; i < args; i++) {
		    	values[i] = Tag.bytesToValue(bytes);
		    }

		    int invokeOpts = bytes.getInt();
		    
		    
		    Object obj = null;
		    
		    
		    
		 // TODO [for PJA] What is the best way to execute a method
			  // it's typical, we want to execute obj.toString() when generating a popup of a hover info when inspecting an object
			  System.out.println("Executing method: " + method + " of object instance: " + obj);
			  
			  MethodInfo stub = method.createDirectCallStub("[jdwp-method-invocation]" + clazz + "." + method.getName());
			    stub.setFirewall(true); // we don't want to let exceptions pass through this
			    
			    DirectCallStackFrame frame = new DirectCallStackFrame(stub);
			    
			    // push this on a stack
			    if (obj != null) { // when obj == null then method is static (and we don't need to push this on stack)
			    	frame.pushRef(((ElementInfo)obj).getObjectRef());
			    }
			    
			    for (Value value : values) {
			    	System.out.println(value);
			    	
			    	value.push(frame);
			    }
			    
			    MethodResult methodResult = null;
			    try {
			    	thread.executeMethodHidden(frame);
			      //ti.advancePC();

			    } catch (UncaughtException ux) {  // frame's method is firewalled
			      System.out.println("# hidden method execution failed, leaving nativeHiddenRoundtrip: " + ux);
			      thread.clearPendingException();
			      ExceptionInfo exceptionInfo = thread.getPendingException();
			      throw new RuntimeException("exceptions not yet implemented");
//			      methodResult = new MethodResult(null, exceptionInfo);
//			      thread.popFrame(); // this is still the DirectCallStackFrame, and we want to continue execution
//			      return -1;
			    }

			    // get the return value from the (already popped) frame
			    int res = frame.peek();
			    ElementInfo result = VM.getVM().getHeap().get(res); // TODO implicitly assuming returned value is a reference 
			    if (result == null) {
			    	// TODO is probably primitive
			    	throw new RuntimeException("Not implemented");
			    }
			    System.out.println("# exit nativeHiddenRoundtrip: " + result);
			       
			    ObjectId objectId = JdwpObjectManager.getInstance().getObjectId(result);
			    //return new MethodResult(objectId.factory(), null);
		    
			
			    objectId.writeTagged(os);
			    NullObjectId.getInstance().writeTagged(os);
			
			
			
			//MethodResult mr = invokeMethod(bytes);
//		      Throwable exception = mr.getThrownException();
//		      ObjectId eId = idMan.getObjectId(exception);
//		      mr.getReturnedValue().writeTagged(os);
//		      eId.writeTagged(os);

		}
	},
	NEWINSTANCE(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ClassTypeCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassTypeCommand> map = new ReverseEnumMap<Byte, ClassTypeCommand>(ClassTypeCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassTypeCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
	
//	 private MethodResult invokeMethod(ByteBuffer bytes)
//			    throws JdwpError, IOException
//			  {
//			    ReferenceTypeId refId = JdwpObjectManager.getInstance().readReferenceTypeId(bytes);
//			    ClassInfo clazz = refId.get();
//
//			    ThreadId tId = JdwpObjectManager.getInstance().readThreadId(bytes);
//			    ThreadInfo thread = tId.get();
//
//			    MethodInfo method = VirtualMachineHelper.getClassMethod(clazz, bytes.getLong());
//
//			    int args = bytes.getInt();
//			    Value[] values = new Value[args];
//
//			    for (int i = 0; i < args; i++) {
//			    	values[i] = Tag.bytesToValue(bytes);
//			    }
//
//			    int invokeOpts = bytes.getInt();
//			    //throw new RuntimeException("not implemented");
//			    MethodResult mr = VMVirtualMachine.executeMethod(null, thread,
//			                                                     clazz, method,
//			                                                     values, invokeOpts);
//			    return mr;
//			  }
}