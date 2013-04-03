package gov.nasa.jpf.jdwp.event.filter;

import java.util.regex.Pattern;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

public class ClassMatchFilter extends Filter<Event> {

	private String classPattern;

	static Pattern ALLOWED_PATT = Pattern.compile("^\\*{0,1}[^*]*\\*{0,1}$");

	private static boolean isValidClassPattern(String classPattern) {
		return startsWithAsteriskOnly(classPattern) || endsWithAsteriskOnly(classPattern) || classPattern.indexOf('*') == -1;
	}

	private static boolean endsWithAsteriskOnly(String classPattern) {
		return classPattern.indexOf('*') == classPattern.length() - 1;
	}

	private static boolean startsWithAsteriskOnly(String classPattern) {
		return classPattern.lastIndexOf('*') == 0;
	}

	public ClassMatchFilter(String classPattern) throws JdwpError {
		super(ModKind.CLASS_MATCH);

		if (!isValidClassPattern(classPattern)) {
			throw new JdwpError(ErrorType.INVALID_STRING); 
			// TODO better constructor
		}

		this.classPattern = classPattern;
	}

	@Override
	protected boolean matchesInternal(Event event) {
		return event.matchesClassPattern(this);
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case THREAD_START:
		case THREAD_END:
			return false;
		default:
			return true;
		}
	}

	public boolean matches(String className) {
		if (classPattern.startsWith("*")) {
			return className.endsWith(classPattern.substring(1));
		} else if (classPattern.endsWith("*")) {
			int end = classPattern.length() - 1;
			return className.startsWith(classPattern.substring(0, end));
		} else {
			return className.equals(classPattern);
		}
	}

}
