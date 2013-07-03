package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;

/**
 * Utility class for {@link Value} interface.
 * 
 * @author stepan
 * 
 */
public class ValueUtils {

	/**
	 * Only static methods are exposed.
	 */
	private ValueUtils() {
	}

	/**
	 * Converts the object instance's field to the value.
	 * 
	 * @param instance
	 *            The SuT object instance represented by {@link ElementInfo}
	 *            instance.
	 * @param field
	 *            The field.
	 * @return The {@link Value} instance for the given field of the given
	 *         instance.
	 */
	public static Value fieldToValue(ElementInfo instance, FieldInfo field) {
		Tag tag = Tag.fieldToTag(field);
		return tag.value(instance, field);
	}

	/**
	 * Converts the array at the given position to the value.
	 * 
	 * @param array
	 *            The instance of an array in SuT represented by
	 *            {@link ElementInfo} instance.
	 * @param position
	 *            The index to the array.
	 * @return The {@link Value} instance for the given array's position.
	 */
	public static Value arrayIndexToValue(ElementInfo array, int position) {
		ClassInfo arrayClassInfo = array.getClassInfo().getComponentClassInfo();
		Tag tag = Tag.classInfoToTag(arrayClassInfo);
		return tag.value(array, position);
	}

}
