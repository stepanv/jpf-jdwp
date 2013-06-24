package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.lang.ref.WeakReference;

/**
 * This class extends the functionality of standard object identifiers. Another
 * object (which is called here the <tt>InfoObject</tt>) is bound to this JDWP
 * identifier.<br/>
 * See any subclass for further examples and explanation of the use of this
 * class.<br/>
 * The need for this class comes from the fact that several objects in the SUT
 * are represented by both the {@link ElementInfo} instance and also
 * SomeNameInfo object (like {@link ThreadInfo}). Therefore it is convenient to
 * keep such information at one place.<br/>
 * 
 * Supports lazy load of the infoObject in case it's null.
 * 
 * @author stepan
 * 
 * @param <T>
 *            the <tt>InfoObject</tt> that is bound to this identifier (in an
 *            addition to the {@link ElementInfo} instance)
 */
public abstract class InfoObjectId<T> extends ObjectId {

	/**
	 * Constructs Info Object ID instance.
	 * 
	 * @param tag
	 *            The Tag
	 * @param id
	 *            The identifier
	 * @param object
	 *            The instance in SuT this Object ID stands for
	 * @param infoObject
	 *            Info Object or null if lazy load is desired
	 */
	public InfoObjectId(Tag tag, long id, ElementInfo object, T infoObject) {
		super(tag, id, object);

		infoObjectReference = new WeakReference<T>(infoObject);
	}

	protected WeakReference<T> infoObjectReference;

	/**
	 * Gets the <tt>InfoObject</tt> that is bound to this identifier.<br/>
	 * Tries to lazy load the object if it is null.
	 * 
	 * @return <tt>InfoObject</tt> instance
	 * @throws InvalidObject
	 *             If the <tt>InfoObject</tt> doesn't exist
	 */
	public T getInfoObject() throws InvalidObject {
		T infoObject = infoObjectReference.get();
		if (infoObject == null) {
			infoObject = resolveInfoObject();
			if (infoObject != null) {
				infoObjectReference = new WeakReference<T>(infoObject);
			} else {
				throw new InvalidObject("ObjectId: " + this);
			}
		}
		return infoObject;
	}

	/**
	 * The way how to resolve the info object by the subclass for use in
	 * {@link InfoObjectId#getInfoObject()} in case lazy load is performed.
	 * 
	 * @return Info Object instance
	 * @throws InvalidObject
	 */
	abstract protected T resolveInfoObject() throws InvalidObject;

}
