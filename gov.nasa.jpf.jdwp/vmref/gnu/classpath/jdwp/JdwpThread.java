package gnu.classpath.jdwp;

public interface JdwpThread {

	boolean isAlive();

	JdwpThread currentThread();

	JdwpThreadGroup getThreadGroup();

	String getName();

	void stop(Throwable throwable);

	void interrupt();

}
