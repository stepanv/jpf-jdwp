package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum InterfaceTypeCommandSet implements Command, IdentifiableEnum<Byte, InterfaceTypeCommandSet> {
		NONE;
		
		private static ReverseEnumMap<Byte, InterfaceTypeCommandSet> map = new ReverseEnumMap<Byte, InterfaceTypeCommandSet>(InterfaceTypeCommandSet.class);

		@Override
		public Byte identifier() {
			return null;
		}

		@Override
		public InterfaceTypeCommandSet convert(Byte val) throws JdwpError {
			return map.get(val);
		}

		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
		}
	}