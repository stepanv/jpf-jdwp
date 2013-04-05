package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class ObjectId<T> extends TaggableIdentifier<T> {
	public static enum Tag implements IdentifiableEnum<Byte>{
		ARRAY(91) ,
		OBJECT(76),
		STRING(115),
		THREAD(116),
		THREAD_GROUP(103),
		CLASS_LOADER(108),
		CLASS_OBJECT(99);
		
		private byte tagId;

		Tag(int id) {
			this.tagId = (byte)id;
		}

		@Override
		public Byte identifier() {
			return tagId;
		}
	}
	
	private Tag tag;

	public ObjectId(Tag tag, long id, T object) {
		super(id, object);
		this.tag = tag;
	}

	public static ObjectId factory(long id, Object object) {
		if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().isArray()) {
	        return new ArrayId(id, (ElementInfo) object);
	    } else if (object.getClass().getName().equals("gov.nasa.jpf.vm.ThreadInfo")) { // TODO don't use string comparison - it's slow
	    	return new ThreadId(id, (ThreadInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().isStringClassInfo()) {
	    	return new StringId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.Class")) {
	    	return new ClassObjectId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.ThreadGroup")) {
	    	return new ThreadGroupId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.ClassLoader")) {
	    	return new ClassLoaderId(id, (ElementInfo) object);
	    } else {
	    	return new ObjectId(Tag.OBJECT, id, object);
	    }
		
		//throw new RuntimeException("FACTORY NOT FULLY IMPLEMENTED YET. For object: " + object + " class: " + object.getClass());
	}

	@Override
	public IdentifiableEnum<Byte> getIdentifier() {
		return tag;
	}
}
