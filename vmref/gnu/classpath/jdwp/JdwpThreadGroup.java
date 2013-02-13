package gnu.classpath.jdwp;

public interface JdwpThreadGroup {

	int activeCount();

	String getName();

	JdwpThreadGroup getParent();

	void enumerate(JdwpThread[] allThreads, boolean b);

	void enumerate(JdwpThreadGroup[] allGroups, boolean b);

	void enumerate(JdwpThread[] allThreads);

}
