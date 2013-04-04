package gov.nasa.jpf.jdwp.event.filter;


/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those for classes whose name matches the given
 * restricted regular expression. For class prepare events, the prepared class
 * name is matched. For class unload events, the unloaded class name is matched.
 * For other events, the class name of the event's location is matched. This
 * modifier can be used with any event kind except thread start and thread end.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassMatchFilter extends ClassFilter {

	/**
	 * <p>
	 * Creates Class Match Filter for the given restricted regular expression.<br/>
	 * Be aware that we're not talking about standard regular expressions.
	 * </p>
	 * 
	 * @see ClassFilter
	 * @param classPattern
	 *            Required class pattern. Matches are limited to exact matches
	 *            of the given class pattern and matches of patterns that begin
	 *            or end with '*'; for example, "*.Foo" or "java.*".
	 */
	public ClassMatchFilter(String classPattern) {
		super(ModKind.CLASS_MATCH, classPattern);
	}

	@Override
	public boolean accepts(String className) {
		return compare(className);
	}

}
