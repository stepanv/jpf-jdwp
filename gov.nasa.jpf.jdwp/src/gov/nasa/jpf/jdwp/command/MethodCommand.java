package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.VMVirtualMachine;
import gnu.classpath.jdwp.exception.JdwpException;
import gnu.classpath.jdwp.util.LineTable;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum MethodCommand implements Command, ConvertibleEnum<Byte, MethodCommand> {
	LINETABLE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
			    ClassInfo clazz = refId.get();

			    MethodInfo method;
				try {
					method = VMVirtualMachine.getClassMethod(clazz, bytes.getLong());
				} catch (JdwpException e) {
					// TODO Auto-generated catch block
					throw new JdwpError(ErrorType.INVALID_METHODID); // TODO this is not enough
				}
			   LineTable lt = LineTable.factory(method); // TODO do this in a uniform way (see method bellow)
			   
//			    LineTable lt = method.getLineTable();
			    lt.write(os);

		}
	},
	VARIABLETABLE(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	BYTECODES(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	ISOBSOLETE(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	VARIABLETABLEWITHGENERIC(5) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private MethodCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, MethodCommand> map = new ReverseEnumMap<Byte, MethodCommand>(MethodCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public MethodCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}