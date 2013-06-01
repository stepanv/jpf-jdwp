package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.LineTable;
import gov.nasa.jpf.jdwp.util.VariableTable;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum MethodCommand implements Command, ConvertibleEnum<Byte, MethodCommand> {

	/**
	 * <p>
	 * <h2>JDWP Specification</h2>
	 * Returns line number information for the method, if present. The line
	 * table maps source line numbers to the initial code index of the line. The
	 * line table is ordered by code index (from lowest to highest). The line
	 * number information is constant unless a new class definition is installed
	 * using {@link VirtualMachineCommand#REDEFINECLASSES}.
	 * </p>
	 */
	LINETABLE(1) {
		@Override
		public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			// TODO Has to be verified, that JPF has always sorted all
			// instructions according to their index otherwise the specification
			// is violated
			LineTable lineTable = new LineTable(methodInfo);
			lineTable.write(os);
		}
	},

	/**
	 * Returns variable information for the method. The variable table includes
	 * arguments and locals declared within the method. For instance methods,
	 * the "this" reference is included in the table. Also, synthetic variables
	 * may be present.
	 */
	VARIABLETABLE(2) {
		@Override
		public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {

			VariableTable variableTable = new VariableTable(methodInfo);
			variableTable.write(os);

		}
	},
	BYTECODES(3) {
		@Override
		public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	ISOBSOLETE(4) {
		@Override
		public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	VARIABLETABLEWITHGENERIC(5) {
		@Override
		public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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

	public abstract void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
			JdwpError;

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		ClassInfo clazz = refId.get();

		execute(VirtualMachineHelper.getClassMethod(clazz, bytes.getLong()), bytes, os, contextProvider);
	}
}