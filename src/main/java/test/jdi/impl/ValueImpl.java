package test.jdi.impl;

import com.sun.jdi.Value;

public abstract class ValueImpl extends MirrorImpl implements Value {


	public ValueImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	public static ValueImpl factory(Object object, VirtualMachineImpl vm) {
		
		if (object instanceof Integer) {
			return new IntegerValueImpl(vm, (Integer)object);
		}
		
		return null;
//		String sig = lv.getSignature();
//	      int slotIdx = lv.getSlotIndex();
//	      int v = slots[slotIdx];
//
//	      switch (sig.charAt(0)) {
//	        case 'Z':
//	          return Boolean.valueOf(v != 0);
//	        case 'B':
//	          return new Byte((byte) v);
//	        case 'C':
//	          return new Character((char) v);
//	        case 'S':
//	          return new Short((short) v);
//	        case 'I':
//	          return new Integer((int) v);
//	        case 'J':
//	          return new Long(Types.intsToLong(slots[slotIdx + 1], v)); // Java is big endian, Types expects low,high
//	        case 'F':
//	          return new Float(Float.intBitsToFloat(v));
//	        case 'D':
//	          return new Double(Double.longBitsToDouble(Types.intsToLong(slots[slotIdx + 1], v)));
//	        default:  // reference
//	          if (v >= 0) {
//	            return JVM.getVM().getHeap().get(v);
//	          }
//	      }
		
	}

}
