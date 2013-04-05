package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.SourceNameMatchFilterable;

/**
 * <p>
 * 
 * <h2>JDWP Specification</h2>
 * Restricts reported class prepare events to those for reference types which
 * have a source name which matches the given restricted regular expression. The
 * source names are determined by the reference type's SourceDebugExtension.
 * This modifier can only be used with class prepare events. <br/>
 * Since JDWP version 1.6. <br/>
 * Requires the canUseSourceNameFilters capability - see CapabilitiesNew.
 * </p>
 * 
 * @author stepan
 * 
 */
public class SourceNameMatchFilter extends Filter<SourceNameMatchFilterable> {
	private String sourceNamePattern;

	/**
	 * Creates Source Name Match filter for the given source name pattern
	 * parameter.
	 * 
	 * @param sourceNamePattern
	 *            Required source name pattern. Matches are limited to exact
	 *            matches of the given pattern and matches of patterns that
	 *            begin or end with '*'; for example, "*.Foo" or "java.*".
	 */
	public SourceNameMatchFilter(String sourceNamePattern) {
		super(ModKind.SOURCE_NAME_MATCH);
		this.sourceNamePattern = sourceNamePattern;
	}

	@Override
	public boolean matches(SourceNameMatchFilterable event) {
		// TODO not implemented yet!
		throw new RuntimeException("Not implemented yet!");
	}

}
