package gnu.classpath.jdwp;

import gnu.classpath.jdwp.id.StringInternalId;

public class JdwpStringContainer extends JdwpObjectContainer<String, StringInternalId> {
	public JdwpStringContainer(String string) {
		super(string);
	}

	@Override
	public StringInternalId createId() {
		return new StringInternalId();
	}
}
