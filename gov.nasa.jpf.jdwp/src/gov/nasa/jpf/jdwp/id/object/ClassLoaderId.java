package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * This class implements the corresponding classLoaderID common data type from
 * the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a class
 * loader object.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassLoaderId extends InfoObjectId<ClassLoaderInfo> {

	/**
	 * Constructs the classloader ID.
	 * 
	 * 
	 * @param id
	 *            The ID known by {@link ObjectIdManager}
	 * @param classLoaderInfo
	 *            The {@link ClassLoaderInfo} instance that needs JDWP ID
	 *            representation.
	 */
	public ClassLoaderId(long id, ClassLoaderInfo classLoaderInfo) {
		this(id, VM.getVM().getHeap().get(classLoaderInfo.getClassLoaderObjectRef()), classLoaderInfo);
	}

	/**
	 * The common constructor.
	 * 
	 * @param id
	 *            The ID known by {@link ObjectIdManager}
	 * @param elementInfo
	 * @param classLoaderInfo
	 */
	private ClassLoaderId(long id, ElementInfo elementInfo, ClassLoaderInfo classLoaderInfo) {
		super(Tag.CLASS_LOADER, id, elementInfo, classLoaderInfo);
	}

	/**
	 * Constructs the classloader ID.
	 * 
	 * @param id
	 *            The ID known by {@link ObjectIdManager}
	 * @param elementInfo
	 *            The {@link ElementInfo} instance that needs JDWP ID
	 *            representation.
	 */
	public ClassLoaderId(long id, ElementInfo elementInfo) {
		this(id, elementInfo, getClassLoaderInfo(elementInfo));
	}

	/**
	 * Finds info object instance for the given parameter.
	 * 
	 * @param elementInfo
	 *            The {@link ElementInfo} instance that is supposed to be paired
	 *            with the {@link ClassLoaderInfo} instance.
	 * @return The {@link ClassLoaderInfo} instance
	 */
	private static ClassLoaderInfo getClassLoaderInfo(ElementInfo elementInfo) {
		ThreadInfo currentThread = VM.getVM().getCurrentThread();
		MJIEnv env = currentThread.getMJIEnv();
		// TODO maybe don't use current thread but something better...

		return env.getClassLoaderInfo(elementInfo.getObjectRef());
	}

	@Override
	protected ClassLoaderInfo resolveInfoObject() throws InvalidObject {
		return getClassLoaderInfo(get());
	}
}
